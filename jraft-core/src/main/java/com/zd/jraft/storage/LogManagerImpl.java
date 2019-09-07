package com.zd.jraft.storage;

import com.zd.jraft.closure.StableClosure;
import com.zd.jraft.conf.Configuration;
import com.zd.jraft.conf.ConfigurationManager;
import com.zd.jraft.entity.ConfigurationEntry;
import com.zd.jraft.entity.EnumOuter;
import com.zd.jraft.entity.LogEntry;
import com.zd.jraft.error.RaftError;
import com.zd.jraft.node.Status;
import com.zd.jraft.option.RaftOptions;
import com.zd.jraft.utils.Requires;
import com.zd.jraft.utils.Utils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LogManagerImpl implements LogManager {

    private volatile boolean hasError;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = this.lock.writeLock();
    private final Lock readLock = this.lock.readLock();

    private ConfigurationManager configManager;

    private RaftOptions raftOptions;

    @Override
    public void appendEntries(List<LogEntry> entries, StableClosure done) {
        Requires.requireNonNull(done, "done");
        //是否发生错误
        if (this.hasError) {
            entries.clear();
            Utils.runClosureInThread(done, new Status(RaftError.EIO, "Corrupted LogStorage"));
            return;
        }
        boolean doUnlock = true;
        this.writeLock.lock();
        try {
            //log条目不为空，并检查处理冲突
            if (!entries.isEmpty() && !checkAndResolveConflict(entries, done)) {
                entries.clear();
                Utils.runClosureInThread(done, new Status(RaftError.EINTERNAL, "Fail to checkAndResolveConflict."));
                return;
            }
            for (LogEntry entry : entries) {
                //判断是否配置启动日志校验
                if (this.raftOptions.isEnableLogEntryChecksum()) {
                    entry.setChecksum(entry.getChecksum());
                }
                if (entry.getType() == EnumOuter.EntryType.ENTRY_TYPE_CONFIGURATION) {
                    //配置类条目
                    Configuration oldConf = null;
                    if (entry.getOldPeers() != null) {
                        oldConf = new Configuration(entry.getOldPeers());
                    } else {
                        oldConf = new Configuration();
                    }
                    final ConfigurationEntry conf = new ConfigurationEntry(entry.getId(), new Configuration(entry.getPeers()), oldConf);
                    this.configManager.add(conf);
                }
            }
        } finally {
            if (doUnlock) {
                this.writeLock.unlock();
            }
        }
    }

    private boolean checkAndResolveConflict(List<LogEntry> entries, StableClosure done) {

    }
}
