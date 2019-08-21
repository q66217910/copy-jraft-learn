package com.zd.jraft.entity;

import com.zd.jraft.closure.Closure;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * jraft 消息结构
 * <p>
 * data：
 * done：
 * expectedTerm
 */
public class Task implements Serializable {

    private ByteBuffer data;

    private Closure done;

    private long expectedTerm = -1;

    public Task() {
        super();
    }

    public Task(ByteBuffer data, Closure done) {
        super();
        this.data = data;
        this.done = done;
    }

    public Task(ByteBuffer data, Closure done, long expectedTerm) {
        super();
        this.data = data;
        this.done = done;
        this.expectedTerm = expectedTerm;
    }
    public ByteBuffer getData() {
        return this.data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public Closure getDone() {
        return this.done;
    }

    public void setDone(Closure done) {
        this.done = done;
    }

    public long getExpectedTerm() {
        return this.expectedTerm;
    }

    public void setExpectedTerm(long expectedTerm) {
        this.expectedTerm = expectedTerm;
    }

}
