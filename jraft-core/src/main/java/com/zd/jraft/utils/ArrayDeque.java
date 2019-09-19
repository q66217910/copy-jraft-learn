package com.zd.jraft.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayDeque<E> extends ArrayList<E> {


    /**
     * @return 获取第一个元素
     */
    public static <E> E peekFirst(List<E> list) {
        return list.get(0);
    }

    /**
     * @return 获取并移除第一个元素
     */
    public static <E> E pollFirst(List<E> list) {
        return list.remove(0);
    }

    /**
     * @return 获取最后一个元素
     */
    public static <E> E peekLast(List<E> list) {
        return list.get(list.size() - 1);
    }

    /**
     * @return 移除并获取最后一个元素
     */
    public static <E> E pollLast(List<E> list) {
        return list.remove(list.size() - 1);
    }

    /**
     * @return 获取第一个元素
     */
    public E peekFirst() {
        return peekFirst(this);
    }

    /**
     * @return 获取最后一个元素
     */
    public E peekLast() {
        return peekLast(this);
    }

    /**
     * @return 获取并移除第一个元素
     */
    public E pollFirst() {
        return pollFirst(this);
    }

    /**
     * @return 移除并获取最后一个元素
     */
    public E pollLast() {
        return pollLast(this);
    }

    /**
     * 范围内移除
     */
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }
}
