package com.zd.jraft.node;

import com.zd.jraft.closure.Closure;
import com.zd.jraft.closure.ReadIndexClosure;
import com.zd.jraft.common.Describer;
import com.zd.jraft.conf.Configuration;
import com.zd.jraft.entity.PeerId;
import com.zd.jraft.entity.Task;
import com.zd.jraft.entity.UserLog;
import com.zd.jraft.machine.Iterator;
import com.zd.jraft.machine.StateMachine;
import com.zd.jraft.option.NodeOptions;
import com.zd.jraft.option.RaftOptions;

import java.util.List;

/**
 * raft replica node
 */
public interface Node extends Lifecycle<NodeOptions>, Describer {

    /**
     * @return 获取leader的地址
     */
    PeerId getLeaderId();

    /**
     * @return 获取当前节点
     */
    NodeId getNodeId();


    /**
     * @return 获取节点性能指标
     */
    NodeMetrics getNodeMetrics();

    /**
     * @return 获取当前groupId
     */
    String getGroupId();

    /**
     * 获取节点配置
     */
    NodeOptions getOptions();

    /**
     * 获取raft配置
     */
    RaftOptions getRaftOptions();

    /**
     * 当前节点是leader返回 true
     */
    boolean isLeader();

    /**
     * 关闭时当前节点
     *
     * @param done 关闭时回调
     */
    void shutdown(final Closure done);

    /**
     * 阻塞线程，直至节点成功停止
     *
     * @throws InterruptedException 当前线程在等待时被中断
     */
    void join() throws InterruptedException;

    /**
     * 线程安全，无需等待
     * 执行任务，到状态机
     * |task.data|: 处于性能考虑，会把内容清空。
     * |task.done|: 提交成功后，所有权交给#{@link StateMachine#onApply(Iterator)}.
     *
     * @param task 要执行的任务
     */
    void apply(final Task task);

    /**
     * @param requestContext 请求的内容
     * @param done           回调
     */
    void readIndex(final byte[] requestContext, final ReadIndexClosure done);

    /**
     * @return 列出当前group所有的PeerId，只有leader有返回
     */
    List<PeerId> listPeers();

    /**
     * @return 列出当前group所有活跃的PeerId，只有leader有返回
     */
    List<PeerId> listAlivePeers();


    /**
     * 添加一个新的 peer 到 group
     *
     * @param peer peer
     * @param done 回调
     */
    void addPeer(final PeerId peer, final Closure done);

    /**
     * 移除 peer 出 group
     *
     * @param peer peer
     * @param done 回调
     */
    void removePeer(final PeerId peer, final Closure done);

    /**
     * 修改peer配置
     *
     * @param newPeers 新的配置
     * @param done     回调
     */
    void changePeers(final Configuration newPeers, final Closure done);

    /**
     * 重置配置
     *
     * @param newPeers 新的配置
     */
    Status resetPeers(final Configuration newPeers);

    /**
     * 启动快照
     *
     * @param done 回调
     */
    void snapshot(final Closure done);

    /**
     * 重置每个节点选举时间
     *
     * @param electionTimeoutMs 选举时间
     */
    void resetElectionTimeoutMs(final int electionTimeoutMs);


    /**
     * 转移leader到新的peer
     *
     * @param peer 新leader
     * @return 操作状态
     */
    Status transferLeadershipTo(final PeerId peer);

    /**
     * 读取index第一个提交的log
     *
     * @param index log index
     * @return log
     */
    UserLog readCommittedUserLog(final long index);
}
