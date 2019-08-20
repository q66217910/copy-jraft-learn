package com.zd.jraft.entity;

import java.nio.ByteBuffer;

public class UserLog {

    /**
     * Log index
     */
    private long index;

    /**
     * LOG data
     */
    private ByteBuffer data;

    public UserLog(long index, ByteBuffer data) {
        super();
        this.index = index;
        this.data = data;
    }

    public long getIndex() {
        return this.index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    public void reset() {
        this.data.clear();
        this.index = 0;
    }

    @Override
    public String toString() {
        return "UserLog{" +
                "index=" + index +
                ", data=" + data +
                '}';
    }
}
