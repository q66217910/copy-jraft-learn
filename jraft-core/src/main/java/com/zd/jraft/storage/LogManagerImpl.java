package com.zd.jraft.storage;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.zd.jraft.closure.StableClosure;
import com.zd.jraft.closure.TruncatePrefixClosure;
import com.zd.jraft.conf.Configuration;
import com.zd.jraft.conf.ConfigurationManager;
import com.zd.jraft.entity.ConfigurationEntry;
import com.zd.jraft.entity.EnumOuter;
import com.zd.jraft.entity.LogEntry;
import com.zd.jraft.entity.LogId;
import com.zd.jraft.error.RaftError;
import com.zd.jraft.error.RaftException;
import com.zd.jraft.machine.FSMCaller;
import com.zd.jraft.node.Status;
import com.zd.jraft.option.RaftOptions;
import com.zd.jraft.utils.ArrayDeque;
import com.zd.jraft.utils.Requires;
import com.zd.jraft.utils.ThreadHelper;
import com.zd.jraft.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogManagerImpl implements LogManager {

    private static final Logger LOG = LoggerFactory.getLogger(LogManagerImpl.class);

    private volatile boolean hasError;

    private static final int APPEND_LOG_RETRY_TIMES = 50; //添加日志重试次数
    private volatile long lastLogIndex;

    private FSMCaller fsmCaller;


    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = this.lock.writeLock();
    private final Lock readLock = this.lock.readLock();

    private volatile boolean stopped;

    private final Map<Long, WaitMeta> waitMap = new HashMap<>();

    private ConfigurationManager configManager;
    private ArrayDeque<LogEntry> logsInMemory = new ArrayDeque<>();
    private RingBuffer<StableClosureEvent> diskQueue;
    private volatile CountDownLatch shutDownLatch;


    private LogId diskId = new LogId(0, 0);
    private LogId appliedId = new LogId(0, 0);

    private final CopyOnWriteArrayList<LastLogIndexListener> lastLogIndexListeners = new CopyOnWriteArrayList<>();

    private RaftOptions raftOptions;

    private enum EventType {
        OTHER,
        RESET,
        TRUNCATE_PREFIX,
        TRUNCATE_SUFFIX,
        SHUTDOWN,
        LAST_LOG_ID
    }

    @Override
    public void appendEntries(List<LogEntry> entries, StableClosure done) {
        Requires.requireNonNull(done, "done");
        //是否发生错误
        if (this.hasError) {
            entries.clear();
            Utils.runClosureInThread(done, new Status(RaftError.EIO, "Corrupted LogStorage"));
            return;
        }
        boolean doUnlock = true;
        this.writeLock.lock();
        try {
            //log条目不为空，并检查处理冲突
            if (!entries.isEmpty() && !checkAndResolveConflict(entries, done)) {
                entries.clear();
                Utils.runClosureInThread(done, new Status(RaftError.EINTERNAL, "Fail to checkAndResolveConflict."));
                return;
            }
            for (LogEntry entry : entries) {
                //判断是否配置启动日志校验
                if (this.raftOptions.isEnableLogEntryChecksum()) {
                    entry.setChecksum(entry.getChecksum());
                }
                if (entry.getType() == EnumOuter.EntryType.ENTRY_TYPE_CONFIGURATION) {
                    //配置类条目
                    Configuration oldConf = null;
                    if (entry.getOldPeers() != null) {
                        oldConf = new Configuration(entry.getOldPeers());
                    } else {
                        oldConf = new Configuration();
                    }
                    final ConfigurationEntry conf = new ConfigurationEntry(entry.getId(), new Configuration(entry.getPeers()), oldConf);
                    this.configManager.add(conf);
                }
            }
            if (!entries.isEmpty()) {
                done.setFirstLogIndex(entries.stream().findFirst().map(LogEntry::getId).map(LogId::getIndex).get());
                logsInMemory.addAll(entries);
            }
            done.setEntries(entries);

            int retryTimes = 0;
            final EventTranslator<StableClosureEvent> translator = ((event, sequence) -> {
                event.reset();
                event.type = EventType.OTHER;
                event.done = done;
            });
            while (true) {
                if (tryOfferEvent(done, translator)) {
                    break;
                } else {
                    retryTimes++;
                    if (retryTimes > APPEND_LOG_RETRY_TIMES) {
                        reportError(RaftError.EBUSY.getNumber(), "LogManager is busy, disk queue overload.");
                        return;
                    }
                    ThreadHelper.onSpinWait();
                }
            }
            doUnlock = false;
            if (!wakeupAllWaiter(this.writeLock)) {
                notifyLastLogIndexListeners();
            }
        } finally {
            if (doUnlock) {
                this.writeLock.unlock();
            }
        }
    }

    private void notifyLastLogIndexListeners() {
        lastLogIndexListeners.stream()
                .filter(Objects::nonNull)
                .forEach(listener -> {
                    try {
                        listener.onLastLogIndexChanged(this.lastLogIndex);
                    } catch (Exception e) {
                        LOG.error("Fail to notify LastLogIndexListener, listener={}, index={}", listener, this.lastLogIndex);
                    }
                });
    }

    /**
     * 唤醒等待线程
     */
    private boolean wakeupAllWaiter(final Lock lock) {
        if (this.waitMap.isEmpty()) {
            lock.unlock();
            return false;
        }
        final List<WaitMeta> wms = new ArrayList<>(this.waitMap.values());
        final int errCode = this.stopped ? RaftError.ESTOP.getNumber() : RaftError.SUCCESS.getNumber();
        this.waitMap.clear();
        lock.unlock();

        wms.forEach(waitMeta -> {
            waitMeta.errorCode = errCode;
            Utils.runInThread(() -> runOnNewLog(waitMeta));
        });

        return true;
    }

    void runOnNewLog(final WaitMeta wm) {
        wm.onNewLog.onNewLog(wm.arg, wm.errorCode);
    }

    private void reportError(final int code, final String fmt, final Object... args) {
        this.hasError = true;
        final RaftException error = new RaftException(EnumOuter.ErrorType.ERROR_TYPE_LOG);
        error.setStatus(new Status(code, fmt, args));
        fsmCaller.onError(error);
    }

    private boolean tryOfferEvent(StableClosure done, EventTranslator<StableClosureEvent> translator) {
        if (this.stopped) {
            Utils.runClosureInThread(done, new Status(RaftError.ESTOP, "Log manager is stopped."));
            return true;
        }
        return this.diskQueue.tryPublishEvent(translator);
    }

    private boolean checkAndResolveConflict(List<LogEntry> entries, StableClosure done) {

    }


    private static class StableClosureEvent {

        StableClosure done;
        EventType type;

        void reset() {
            this.done = null;
            this.type = null;
        }
    }

    private static class StableClosureEventFactory implements EventFactory<StableClosureEvent> {

        @Override
        public StableClosureEvent newInstance() {
            return new StableClosureEvent();
        }
    }

    private class StableClosureEventHandler implements EventHandler<StableClosureEvent> {

        LogId lastId = LogManagerImpl.this.diskId;

        List<StableClosure> storage = new ArrayList<>(256);

        AppendBatcher ab = new AppendBatcher(this.storage, 256, new ArrayList<>(),
                LogManagerImpl.this.diskId);

        @Override
        public void onEvent(StableClosureEvent event, long sequence, boolean endOfBatch) throws Exception {

            if (event.type == EventType.SHUTDOWN) {
                this.lastId = this.ab.flush();
                setDiskId(this.lastId);
                LogManagerImpl.this.shutDownLatch.countDown();
                return;
            }

            final StableClosure done = event.done;
            if (done.getEntries() != null && !done.getEntries().isEmpty()) {
                this.ab.append(done);
            } else {
                this.lastId = this.ab.flush();
                boolean ret = true;
                switch (event.type) {
                    case LAST_LOG_ID:
                        ((LastLogIdClosure) done).setLastLogId(this.lastId.copy());
                        break;
                    case TRUNCATE_PREFIX:
                        long startMs = Utils.monotonicMs();
                        try {
                            final TruncatePrefixClosure tpc = (TruncatePrefixClosure) done;
                        }
                }
            }
        }
    }

    /**
     * 设置当前最新Log
     */
    private void setDiskId(final LogId id) {
        if (id == null) {
            return;
        }
        LogId clearId;
        this.writeLock.lock();
        try {
            //对比选举届数
            if (id.compareTo(this.diskId) < 0) {
                return;
            }
            this.diskId = id;
            clearId = this.diskId.compareTo(this.appliedId) <= 0 ? this.diskId : this.appliedId;
        } finally {
            this.writeLock.unlock();
        }
        //清理内存中的log，删除当前选举届以前的数据
        clearMemoryLogs(clearId);
    }

    /**
     * 清理内存中的log，删除当前选举届以前的数据
     */
    private void clearMemoryLogs(final LogId id) {
        this.writeLock.lock();
        try {
            int index = 0;
            for (final int size = this.logsInMemory.size(); index < size; index++) {
                final LogEntry entry = this.logsInMemory.get(index);
                if (entry.getId().compareTo(id) > 0) {
                    break;
                }
            }
            if (index > 0) {
                this.logsInMemory.removeRange(0, index);
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    private LogId appendToStorage(final List<LogEntry> toAppend) {

    }

    private class AppendBatcher {
        List<StableClosure> storage;
        int cap;
        int size;
        int bufferSize;
        List<LogEntry> toAppend;
        LogId lastId;

        public AppendBatcher(final List<StableClosure> storage, final int cap, final List<LogEntry> toAppend,
                             final LogId lastId) {
            super();
            this.storage = storage;
            this.cap = cap;
            this.toAppend = toAppend;
            this.lastId = lastId;
        }

        LogId flush() {
            if (this.size > 0) {
                this.lastId = appendToStorage(this.toAppend);
                for (int i = 0; i < this.size; i++) {
                    this.storage.get(i).getEntries().clear();
                    if (LogManagerImpl.this.hasError) {
                        this.storage.get(i).run(new Status(RaftError.EIO, "Corrupted LogStorage"));
                    } else {
                        this.storage.get(i).run(Status.OK());
                    }
                }
                this.toAppend.clear();
                this.storage.clear();
            }
            this.size = 0;
            this.bufferSize = 0;
            return this.lastId;
        }

        void append(final StableClosure done) {
            if (this.size == this.cap || this.bufferSize >= LogManagerImpl.this.raftOptions.getMaxAppendBufferSize()) {
                flush();
            }
            this.storage.add(done);
            this.size++;
            this.toAppend.addAll(done.getEntries());
            for (final LogEntry entry : done.getEntries()) {
                this.bufferSize += entry.getData() != null ? entry.getData().remaining() : 0;
            }
        }
    }

    private static class WaitMeta {
        /**
         * callback when new log come in
         */
        NewLogCallback onNewLog;
        /**
         * callback error code
         */
        int errorCode;
        /**
         * the waiter pass-in argument
         */
        Object arg;

        public WaitMeta(final NewLogCallback onNewLog, final Object arg, final int errorCode) {
            super();
            this.onNewLog = onNewLog;
            this.arg = arg;
            this.errorCode = errorCode;
        }

    }

    private static class LastLogIdClosure extends StableClosure {

        public LastLogIdClosure() {
            super(null);
        }

        private LogId lastLogId;

        void setLastLogId(final LogId logId) {
            Requires.requireTrue(logId.getIndex() == 0 || logId.getTerm() != 0);
            this.lastLogId = logId;
        }

        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void run(final Status status) {
            this.latch.countDown();
        }

        void await() throws InterruptedException {
            this.latch.await();
        }

    }
}
