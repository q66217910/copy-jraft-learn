package com.zd.jraft.utils;

import com.zd.jraft.closure.Closure;
import com.zd.jraft.node.Status;
import io.netty.util.internal.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    /**
     * ANY IP address 0.0.0.0
     */
    public static final String IP_ANY = "0.0.0.0";

    private static final int CPUS = SystemPropertyUtil.getInt("jraft.available_processors", Runtime.getRuntime().availableProcessors());

    private static ThreadPoolExecutor CLOSURE_EXECUTOR;

    /**
     * 获取cpu核心数
     */
    public static int cpus() {
        return CPUS;
    }

    /**
     * 结束时运行
     */
    public static Future<?> runClosureInThread(final Closure done, final Status status) {
        if (done == null) {
            return null;
        }
        return runInThread(() -> {
            try {
                done.run(status);
            } catch (final Throwable t) {
                LOG.error("Fail to run done closure", t);
            }
        });
    }

    public static Future<?> runInThread(final Runnable runnable) {
        return CLOSURE_EXECUTOR.submit(runnable);
    }
}
