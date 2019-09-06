package com.zd.jraft.closure;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

@ThreadSafe
public interface ClosureQueue {

    /**
     * 情况队列中所有回调
     */
    void clear();

    /**
     * 重置第一个
     */
    void resetFirstIndex(final long firstIndex);

    /**
     * 添加一个回调
     */
    void appendPendingClosure(final Closure closure);

    long popClosureUntil(final long endIndex, final List<Closure> closures);

    long popClosureUntil(final long endIndex, final List<Closure> closures, final List<TaskClosure> taskClosures);

}
