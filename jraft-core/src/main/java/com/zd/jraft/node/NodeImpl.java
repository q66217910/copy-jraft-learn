package com.zd.jraft.node;

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

    @Override
    public void apply(final Task task) {
        if (this.shutdownLatch != null) {
            //锁被占用
            Utils.runClosureInThread(task.getDone(), new Status(RaftError.EN_ODE_SHUTDOWN, "Node is shutting down."));
            throw new IllegalStateException("Node is shutting down");
        }
        Requires.requireNonNull(task, "Null task");

        final LogEntry entry = new LogEntry();
    }
}