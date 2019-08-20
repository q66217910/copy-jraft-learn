package com.zd.jraft.option;

import com.zd.jraft.JRaftServiceFactory;
import com.zd.jraft.JRaftServiceLoader;
import com.zd.jraft.conf.Configuration;
import com.zd.jraft.machine.StateMachine;
import com.zd.jraft.storage.SnapshotThrottle;
import com.zd.jraft.utils.Utils;

/**
 * 节点条目
 */
public class NodeOptions {

    public static final JRaftServiceFactory defaultServiceFactory = JRaftServiceLoader.load(JRaftServiceFactory.class) //
            .first();

    /**
     * 多久没收到消息会成为候选人（ms）
     */
    private int electionTimeoutMs = 1000;

    /**
     * leader时间与选举时间的比利
     */
    private int leaderLeaseTimeRatio = 90;

    /**
     * 多久保存一次快照（s）
     */
    private int snapshotIntervalSecs = 3600;

    /**
     * 追赶合并index数
     */
    private int catchupMargin = 1000;


    private Configuration initialConf = new Configuration();

    /**
     * 状态机
     */
    private StateMachine fsm;

    /**
     * LogStorage   ${type}://${parameters}
     */
    private String logUri;

    /**
     * RaftMetaStorage
     */
    private String raftMetaUri;

    /**
     * SnapshotStorage
     */
    private String snapshotUri;

    /**
     * 启动用，在复制快照之前把重复的文件过滤
     */
    private boolean filterBeforeCopyRemote = false;

    /**
     *
     */
    private boolean disableCli = false;

    /**
     * 定时线程池 cpu*3 的线程数，最大20
     */
    private int timerPoolSize = Math.min(Utils.cpus() * 3, 20);

    /**
     * CLI service request RPC executor pool size
     */
    private int cliRpcThreadPoolSize = Utils.cpus();

    /**
     * RAFT request RPC executor pool size
     */
    private int raftRpcThreadPoolSize = Utils.cpus() * 6;

    /**
     * 是否启用性能监控
     */
    private boolean enableMetrics = false;

    private SnapshotThrottle snapshotThrottle;

    private JRaftServiceFactory serviceFactory = defaultServiceFactory;

    private RaftOptions raftOptions = new RaftOptions();
}
