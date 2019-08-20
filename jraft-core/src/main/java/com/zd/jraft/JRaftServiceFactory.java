package com.zd.jraft;

import com.zd.jraft.option.NodeOptions;
import com.zd.jraft.option.RaftOptions;
import com.zd.jraft.storage.LogStorage;

/**
 * raft 服务的抽象工厂
 */
public interface JRaftServiceFactory {


    /**
     * 创建一个日志存储
     *
     * @param uri {@link NodeOptions#}
     * @param raftOptions
     * @return
     */
    LogStorage createLogStorage(final String uri, final RaftOptions raftOptions);

}
