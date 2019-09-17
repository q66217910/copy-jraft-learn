package com.zd.jraft.node;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.zd.jraft.closure.Closure;
import com.zd.jraft.closure.LeaderStableClosure;
import com.zd.jraft.entity.*;
import com.zd.jraft.error.RaftError;
import com.zd.jraft.option.RaftOptions;
import com.zd.jraft.rpc.RaftServerService;
import com.zd.jraft.storage.LogManager;
import com.zd.jraft.utils.Requires;
import com.zd.jraft.utils.ThreadHelper;
import com.zd.jraft.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Node实现
 */
public class NodeImpl implements Node, RaftServerService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeImpl.class);

    private LogManager logManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    protected final Lock writeLock = this.readWriteLock.writeLock();
    protected final Lock readLock = this.readWriteLock.readLock();

    private volatile State state;

    private BallotBox ballotBox;

    private long currTerm;

    private RaftOptions raftOptions;

    private ConfigurationEntry conf;

    public static final AtomicInteger GLOBAL_NUM_NODES = new AtomicInteger(0);

    private volatile CountDownLatch shutdownLatch;

    private NodeMetrics metrics;

    private RingBuffer<LogEntryAndClosure> applyQueue;

    /**
     * 最大重试次数
     */
    private static final int MAX_APPLY_RETRY_TIMES = 3;

    @Override
    public void apply(final Task task) {
        if (this.shutdownLatch != null) {
            //锁被占用
            Utils.runClosureInThread(task.getDone(), new Status(RaftError.EN_ODE_SHUTDOWN, "Node is shutting down."));
            throw new IllegalStateException("Node is shutting down");
        }
        Requires.requireNonNull(task, "Null task");

        final LogEntry entry = new LogEntry();
        entry.setData(task.getData());
        int retryTimes = 0;
        try {
            final EventTranslator<LogEntryAndClosure> translator = (event, sequence) -> {
                event.reset();
                event.done = task.getDone();
                event.entry = entry;
                event.expectedTerm = task.getExpectedTerm();
            };
            while (true) {
                if (this.applyQueue.tryPublishEvent(translator)) {
                    break;
                } else {
                    retryTimes++;
                    if (retryTimes > MAX_APPLY_RETRY_TIMES) {
                        Utils.runClosureInThread(task.getDone(),
                                new Status(RaftError.EBUSY, "Node is busy, has too many tasks."));
                        LOG.warn("Node {} applyQueue is overload.", getNodeId());
                        this.metrics.recordTimes("apply-task-overload-times", 1);
                    }
                    ThreadHelper.onSpinWait();
                }
            }
        } catch (final Exception e) {
            Utils.runClosureInThread(task.getDone(), new Status(RaftError.EPERM, "Node is down."));
        }
    }

    private static class LogEntryAndClosure {

        LogEntry entry;
        Closure done;
        long expectedTerm;
        CountDownLatch shutdownLatch;

        public void reset() {
            this.entry = null;
            this.done = null;
            this.expectedTerm = 0;
            this.shutdownLatch = null;
        }

    }

    private static class LogEntryAndClosureFactory implements EventFactory<LogEntryAndClosure> {

        @Override
        public LogEntryAndClosure newInstance() {
            return new LogEntryAndClosure();
        }
    }

    private class LogEntryAndClosureHandler implements EventHandler<LogEntryAndClosure> {

        private final List<LogEntryAndClosure> tasks = new ArrayList<>(NodeImpl.this.raftOptions.getApplyBatch());

        @Override
        public void onEvent(LogEntryAndClosure event, long sequence, boolean endOfBatch) throws Exception {
            //获取到锁
            if (event.shutdownLatch != null) {
                if (!this.tasks.isEmpty()) {
                    //执行任务
                    executeApplyingTasks(tasks);
                }
                final int num = GLOBAL_NUM_NODES.decrementAndGet();
                LOG.info("The number of active nodes decrement to {}.", num);
                event.shutdownLatch.countDown();
                return;
            }
            //没有获取到锁
            this.tasks.add(event);
            if (this.tasks.size() >= NodeImpl.this.raftOptions.getApplyBatch() || endOfBatch) {
                //大于最大任务数，直接运行，并把任务清空
                executeApplyingTasks(tasks);
                this.tasks.clear();
            }
        }
    }

    /**
     * 执行任务
     */
    private void executeApplyingTasks(final List<LogEntryAndClosure> tasks) {
        this.writeLock.lock();
        try {
            final int size = tasks.size();
            if (this.state != State.STATE_LEADER) {
                //不是leader,设置错误，执行回调
                final Status st = new Status();
                if (this.state != State.STATE_TRANSFERRING) {
                    st.setError(RaftError.EPERM, "Is not leader.");
                } else {
                    st.setError(RaftError.EBUSY, "Is transferring leadership.");
                }
                LOG.debug("Node {} can't apply, status={}.", getNodeId(), st);
                for (LogEntryAndClosure task : tasks) {
                    Utils.runClosureInThread(task.done, st);
                }
                return;
            }
            //是leader
            final List<LogEntry> entries = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                final LogEntryAndClosure task = tasks.get(i);
                //选举term届数不对，不进行同步
                if (task.expectedTerm != -1 && task.expectedTerm != this.currTerm) {
                    LOG.debug("Node {} can't apply task whose expectedTerm={} doesn't match currTerm={}.", getNodeId(),
                            task.expectedTerm, this.currTerm);
                    if (task.done != null) {
                        final Status st = new Status(RaftError.EPERM, "expected_term=%d doesn't match current_term=%d",
                                task.expectedTerm, this.currTerm);
                        Utils.runClosureInThread(task.done, st);
                    }
                    continue;
                }
                if (!this.ballotBox.appendPendingTask(this.conf.getConf(),
                        this.conf.isStable() ? null : this.conf.getOldConf(), task.done)) {
                    Utils.runClosureInThread(task.done, new Status(RaftError.EINTERNAL, "Fail to append task."));
                    continue;
                }
                task.entry.getId().setTerm(this.currTerm);
                task.entry.setType(EnumOuter.EntryType.ENTRY_TYPE_DATA);
                entries.add(task.entry);
            }
            //添加条目
            logManager.appendEntries(entries, new LeaderStableClosure(entries));
            //更新配置文件
            this.conf = this.logManager.checkAndSetConfiguration(this.conf);
        } finally {
            this.writeLock.unlock();
        }
    }
}
