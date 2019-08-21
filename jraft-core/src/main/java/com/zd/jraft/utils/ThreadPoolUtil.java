package com.zd.jraft.utils;

import com.zd.jraft.pool.LogThreadPoolExecutor;
import com.zd.jraft.pool.MetricThreadPoolExecutor;

import java.util.concurrent.*;

public final class ThreadPoolUtil {

    public static ThreadPoolExecutor newThreadPool(final String poolName, final boolean enableMetric,
                                                   final int coreThreads, final int maximumThreads,
                                                   final long keepAliveSeconds,
                                                   final BlockingQueue<Runnable> workQueue,
                                                   final ThreadFactory threadFactory,
                                                   final RejectedExecutionHandler handler) {
        final TimeUnit unit = TimeUnit.SECONDS;
        if (enableMetric) {
            return new MetricThreadPoolExecutor(coreThreads, maximumThreads, keepAliveSeconds, unit, workQueue,
                    threadFactory, handler, poolName);
        } else {
            return new LogThreadPoolExecutor(coreThreads, maximumThreads, keepAliveSeconds, unit, workQueue,
                    threadFactory, handler, poolName);
        }
    }

    /**
     * 拒绝策略
     */
    private static final RejectedExecutionHandler defaultHandler = new ThreadPoolExecutor.AbortPolicy();

    public static PoolBuilder newBuilder() {
        return new PoolBuilder();
    }

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

        public PoolBuilder enableMetric(final Boolean enableMetric) {
            this.enableMetric = enableMetric;
            return this;
        }

        public PoolBuilder coreThreads(final Integer coreThreads) {
            this.coreThreads = coreThreads;
            return this;
        }

        public PoolBuilder maximumThreads(final Integer maximumThreads) {
            this.maximumThreads = maximumThreads;
            return this;
        }

        public PoolBuilder keepAliveSeconds(final Long keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        public PoolBuilder workQueue(final BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public PoolBuilder threadFactory(final ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public PoolBuilder rejectedHandler(final RejectedExecutionHandler handler) {
            this.handler = handler;
            return this;
        }

        public ThreadPoolExecutor build() {
            Requires.requireNonNull(this.poolName, "poolName");
            Requires.requireNonNull(this.enableMetric, "enableMetric");
            Requires.requireNonNull(this.coreThreads, "coreThreads");
            Requires.requireNonNull(this.maximumThreads, "maximumThreads");
            Requires.requireNonNull(this.keepAliveSeconds, "keepAliveSeconds");
            Requires.requireNonNull(this.workQueue, "workQueue");
            Requires.requireNonNull(this.threadFactory, "threadFactory");
            Requires.requireNonNull(this.handler, "handler");

            return ThreadPoolUtil.newThreadPool(this.poolName, this.enableMetric, this.coreThreads,
                    this.maximumThreads, this.keepAliveSeconds, this.workQueue, this.threadFactory, this.handler);
        }

    }

}
