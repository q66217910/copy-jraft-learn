package com.zd.jraft.storage;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.zd.jraft.closure.StableClosure;
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
import com.zd.jraft.utils.Requires;
import com.zd.jraft.utils.Utils;

import javax.lang.model.type.ErrorType;
import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogManagerImpl implements LogManager {

    private volatile boolean hasError;

    private static final int APPEND_LOG_RETRY_TIMES = 50; //添加日志重试次数

    private FSMCaller fsmCaller;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = this.lock.writeLock();
    private final Lock readLock = this.lock.readLock();

    private ConfigurationManager configManager;
    private ArrayDeque<LogEntry> logsInMemory = new ArrayDeque<>();

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
                    ThreadHelper
                }
            }
        } finally {
            if (doUnlock) {
                this.writeLock.unlock();
            }
        }
    }

    private void reportError(final int code, final String fmt, final Object... args) {
        this.hasError = true;
        final RaftException error = new RaftException(EnumOuter.ErrorType.ERROR_TYPE_LOG);
        error.setStatus(new Status(code, fmt, args));
        fsmCaller.onError(error);
    }

    private boolean tryOfferEvent(StableClosure done, EventTranslator<StableClosureEvent> translator) {


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
}
