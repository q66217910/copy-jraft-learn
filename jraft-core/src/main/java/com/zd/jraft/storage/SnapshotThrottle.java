package com.zd.jraft.storage;

/**
 * 磁盘读写快照
 */
public interface SnapshotThrottle {


    /**
     * 获取可用的吞吐量
     *
     * @param bytes expect size
     * @return 吞吐量
     */
    long throttledByThroughput(final long bytes);

}
