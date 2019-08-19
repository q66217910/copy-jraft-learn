package com.zd.jraft.closure;

import com.zd.jraft.node.Status;

/**
 * 关闭时回调
 */
public interface Closure {

    /**
     * 任务结束回调
     *
     * @param status the task status.
     */
    void run(final Status status);
}
