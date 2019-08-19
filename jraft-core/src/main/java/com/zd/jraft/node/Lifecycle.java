package com.zd.jraft.node;

/**
 * 生命周期
 */
public interface Lifecycle<T> {

    /**
     * 初始化服务
     *
     * @return true
     */
    boolean init(final T opts);

    /**
     * 释放资源
     */
    void shutdown();
}
