package com.zd.jraft.node;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.zd.jraft.closure.Closure;
import com.zd.jraft.entity.LogEntry;
import com.zd.jraft.entity.PeerId;
import com.zd.jraft.entity.Task;
import com.zd.jraft.error.RaftError;
import com.zd.jraft.rpc.RaftServerService;
import com.zd.jraft.utils.Requires;
import com.zd.jraft.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Node实现
 */
public class NodeImpl implements Node, RaftServerService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeImpl.class);

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
}
