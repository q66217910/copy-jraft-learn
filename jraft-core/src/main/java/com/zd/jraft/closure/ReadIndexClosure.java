package com.zd.jraft.closure;

import com.zd.jraft.node.Node;
import com.zd.jraft.node.Status;

/**
 * 读取关闭操作
 */
public abstract class ReadIndexClosure implements Closure {

    public static final long INVALID_LOG_INDEX = -1;

    private long index = INVALID_LOG_INDEX;

    private byte[] requestContext;

    public long getIndex() {
        return index;
    }

    public byte[] getRequestContext() {
        return requestContext;
    }

    /**
     * Set callback result, called by jraft.
     *
     * @param index  the committed index.
     * @param reqCtx the request context passed by {@link Node#readIndex(byte[], ReadIndexClosure)}.
     */
    public void setResult(final long index, final byte[] reqCtx) {
        this.index = index;
        this.requestContext = reqCtx;
    }

    @Override
    public void run(Status status) {
        run(status, this.index, this.requestContext);
    }

    /**
     * 当读取结束时回调被执行
     *
     * @param status         读取的数据的状态
     * @param index          启动时的索引
     * @param requestContext 读取的内容
     */
    protected abstract void run(Status status, long index, byte[] requestContext);
}
