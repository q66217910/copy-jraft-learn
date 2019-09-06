package com.zd.jraft.node;

import com.zd.jraft.closure.Closure;
import com.zd.jraft.closure.ClosureQueue;
import com.zd.jraft.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.concurrent.locks.StampedLock;

/**
 * 投票箱
 */
public class BallotBox {

    private static final Logger LOG = LoggerFactory.getLogger(BallotBox.class);

    private final StampedLock stampedLock = new StampedLock();

    private ClosureQueue closureQueue;

    private long pendingIndex;

    private final ArrayDeque<Ballot> pendingMetaQueue = new ArrayDeque<>();

    public boolean appendPendingTask(final Configuration conf, final Configuration oldConf, final Closure done) {
        final Ballot bl = new Ballot();
        if (!bl.init(conf, oldConf)) {
            LOG.error("Fail to init ballot.");
            return false;
        }
        final long stamp = this.stampedLock.writeLock();
        try {
            if (this.pendingIndex <= 0) {
                LOG.error("Fail to appendingTask, pendingIndex={}.", this.pendingIndex);
                return false;
            }
            this.pendingMetaQueue.add(bl);
            this.closureQueue.appendPendingClosure(done);
            return true;
        } finally {
            this.stampedLock.unlock(stamp);
        }
    }
}
