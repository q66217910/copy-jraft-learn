package com.zd.jraft.option;

import com.zd.jraft.common.Copiable;

public class RaftOptions implements Copiable<RaftOptions> {

    /**
     * rpc块大小的最大值
     */
    private int maxByteCountPerRpc = 128 * 1024;

    /**
     * 文件服务检查
     */
    private boolean fileCheckHole = false;

    /**
     * AppendEntriesRequest 中最大条目数
     */
    private int maxEntriesSize = 1024;

    /**
     * AppendEntriesRequest 中最大字节数
     */
    private int maxBodySize = 512 * 1024;

    /**
     * 缓冲区大小限制，如果缓存冲达到限制则刷新到LogStorage
     */
    private int maxAppendBufferSize = 256 * 1024;

    /**
     * 最大延迟选举时间
     */
    private int maxElectionDelayMs = 1000;

    /**
     * 心跳超时因子
     */
    private int electionHeartbeatFactor = 10;

    /**
     * 批量处理最大应用数
     */
    private int applyBatch = 32;

    /**
     * 需要时调用fsync
     */
    private boolean sync = true;

    /**
     * 同步日志，快照，raft
     */
    private boolean syncMeta = false;

    /**
     * 是否启用replicator pipeline
     */
    private boolean replicatorPipeline = true;

    /**
     * 最大pipeline 请求响应
     */
    private int maxReplicatorInflightMsgs = 256;

    /**
     * 内部缓冲区大小
     */
    private int disruptorBufferSize = 16384;

    /**
     * 事件发布disruptor 超时时间
     */
    private int disruptorPublishEventWaitTimeoutSecs = 10;

    /**
     * 是否检查日志条目
     */
    private boolean enableLogEntryChecksum = false;

    private ReadOnlyOption readOnlyOptions = ReadOnlyOption.ReadOnlySafe;

    public int getMaxByteCountPerRpc() {
        return maxByteCountPerRpc;
    }

    public void setMaxByteCountPerRpc(int maxByteCountPerRpc) {
        this.maxByteCountPerRpc = maxByteCountPerRpc;
    }

    public boolean isFileCheckHole() {
        return fileCheckHole;
    }

    public void setFileCheckHole(boolean fileCheckHole) {
        this.fileCheckHole = fileCheckHole;
    }

    public int getMaxEntriesSize() {
        return maxEntriesSize;
    }

    public void setMaxEntriesSize(int maxEntriesSize) {
        this.maxEntriesSize = maxEntriesSize;
    }

    public int getMaxBodySize() {
        return maxBodySize;
    }

    public void setMaxBodySize(int maxBodySize) {
        this.maxBodySize = maxBodySize;
    }

    public int getMaxAppendBufferSize() {
        return maxAppendBufferSize;
    }

    public void setMaxAppendBufferSize(int maxAppendBufferSize) {
        this.maxAppendBufferSize = maxAppendBufferSize;
    }

    public int getMaxElectionDelayMs() {
        return maxElectionDelayMs;
    }

    public void setMaxElectionDelayMs(int maxElectionDelayMs) {
        this.maxElectionDelayMs = maxElectionDelayMs;
    }

    public int getElectionHeartbeatFactor() {
        return electionHeartbeatFactor;
    }

    public void setElectionHeartbeatFactor(int electionHeartbeatFactor) {
        this.electionHeartbeatFactor = electionHeartbeatFactor;
    }

    public int getApplyBatch() {
        return applyBatch;
    }

    public void setApplyBatch(int applyBatch) {
        this.applyBatch = applyBatch;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isSyncMeta() {
        return syncMeta;
    }

    public void setSyncMeta(boolean syncMeta) {
        this.syncMeta = syncMeta;
    }

    public boolean isReplicatorPipeline() {
        return replicatorPipeline;
    }

    public void setReplicatorPipeline(boolean replicatorPipeline) {
        this.replicatorPipeline = replicatorPipeline;
    }

    public int getMaxReplicatorInflightMsgs() {
        return maxReplicatorInflightMsgs;
    }

    public void setMaxReplicatorInflightMsgs(int maxReplicatorInflightMsgs) {
        this.maxReplicatorInflightMsgs = maxReplicatorInflightMsgs;
    }

    public int getDisruptorBufferSize() {
        return disruptorBufferSize;
    }

    public void setDisruptorBufferSize(int disruptorBufferSize) {
        this.disruptorBufferSize = disruptorBufferSize;
    }

    public int getDisruptorPublishEventWaitTimeoutSecs() {
        return disruptorPublishEventWaitTimeoutSecs;
    }

    public void setDisruptorPublishEventWaitTimeoutSecs(int disruptorPublishEventWaitTimeoutSecs) {
        this.disruptorPublishEventWaitTimeoutSecs = disruptorPublishEventWaitTimeoutSecs;
    }

    public boolean isEnableLogEntryChecksum() {
        return enableLogEntryChecksum;
    }

    public void setEnableLogEntryChecksum(boolean enableLogEntryChecksum) {
        this.enableLogEntryChecksum = enableLogEntryChecksum;
    }

    public ReadOnlyOption getReadOnlyOptions() {
        return readOnlyOptions;
    }

    public void setReadOnlyOptions(ReadOnlyOption readOnlyOptions) {
        this.readOnlyOptions = readOnlyOptions;
    }

    @Override
    public RaftOptions copy() {
        final RaftOptions raftOptions = new RaftOptions();
        raftOptions.setMaxByteCountPerRpc(this.maxByteCountPerRpc);
        raftOptions.setFileCheckHole(this.fileCheckHole);
        raftOptions.setMaxEntriesSize(this.maxEntriesSize);
        raftOptions.setMaxBodySize(this.maxBodySize);
        raftOptions.setMaxAppendBufferSize(this.maxAppendBufferSize);
        raftOptions.setMaxElectionDelayMs(this.maxElectionDelayMs);
        raftOptions.setElectionHeartbeatFactor(this.electionHeartbeatFactor);
        raftOptions.setApplyBatch(this.applyBatch);
        raftOptions.setSync(this.sync);
        raftOptions.setSyncMeta(this.syncMeta);
        raftOptions.setReplicatorPipeline(this.replicatorPipeline);
        raftOptions.setMaxReplicatorInflightMsgs(this.maxReplicatorInflightMsgs);
        raftOptions.setDisruptorBufferSize(this.disruptorBufferSize);
        raftOptions.setReadOnlyOptions(this.readOnlyOptions);
        return raftOptions;
    }

    @Override
    public String toString() {
        return "RaftOptions{" + "maxByteCountPerRpc=" + this.maxByteCountPerRpc + ", fileCheckHole="
                + this.fileCheckHole + ", maxEntriesSize=" + this.maxEntriesSize + ", maxBodySize=" + this.maxBodySize
                + ", maxAppendBufferSize=" + this.maxAppendBufferSize + ", maxElectionDelayMs="
                + this.maxElectionDelayMs + ", electionHeartbeatFactor=" + this.electionHeartbeatFactor
                + ", applyBatch=" + this.applyBatch + ", sync=" + this.sync + ", syncMeta=" + this.syncMeta
                + ", replicatorPipeline=" + this.replicatorPipeline + ", maxReplicatorInflightMsgs="
                + this.maxReplicatorInflightMsgs + ", disruptorBufferSize=" + this.disruptorBufferSize
                + ", readOnlyOptions=" + this.readOnlyOptions + '}';
    }
}
