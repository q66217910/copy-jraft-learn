package com.zd.jraft.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public final class ThreadPoolUtil {

    /**
     * 拒绝策略
     */
    private static final RejectedExecutionHandler defaultHandler = new ThreadPoolExecutor.AbortPolicy();

    public static class PoolBuilder {

        private String poolName;
        private Boolean enableMetric;
        private Integer coreThreads;
        private Integer maximumThreads;
        private Long keepAliveSeconds;
        private BlockingQueue<Runnable> workQueue;
        private ThreadFactory threadFactory;
        private RejectedExecutionHandler handler = ThreadPoolUtil.defaultHandler;

        public PoolBuilder poolName(final String poolName) {
            this.poolName = poolName;
            return this;
        }

    }

}
