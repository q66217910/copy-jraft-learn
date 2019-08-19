package com.zd.jraft.common;

/**
 * 标记为可复制
 */
public interface Copiable<T> {

    /**
     * 深复制
     */
    T copy();

}
