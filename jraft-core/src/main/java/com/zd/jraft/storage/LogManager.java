package com.zd.jraft.storage;

import com.zd.jraft.closure.StableClosure;
import com.zd.jraft.entity.ConfigurationEntry;
import com.zd.jraft.entity.LogEntry;
import com.zd.jraft.node.Lifecycle;

import java.util.List;

public interface LogManager extends Lifecycle<LogManager>, Describer {

    /**
     * 添加日志条目，不提交
     */
    void appendEntries(final List<LogEntry> entries, StableClosure done);

    /**
     * @return 检查当前是否是最新配置
     */
    ConfigurationEntry checkAndSetConfiguration(final ConfigurationEntry current);

}
