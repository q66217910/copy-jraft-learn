package com.zd.jraft.entity;

import com.zd.jraft.conf.Configuration;

import java.util.HashSet;
import java.util.Set;

public class ConfigurationEntry {

    private LogId id = new LogId(0, 0);

    private Configuration conf = new Configuration();

    private Configuration oldConf = new Configuration();

    public LogId getId() {
        return id;
    }

    public void setId(LogId id) {
        this.id = id;
    }

    public Configuration getConf() {
        return conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getOldConf() {
        return oldConf;
    }

    public void setOldConf(Configuration oldConf) {
        this.oldConf = oldConf;
    }

    public ConfigurationEntry() {
        super();
    }

    public ConfigurationEntry(LogId id, Configuration conf, Configuration oldConf) {
        super();
        this.id = id;
        this.conf = conf;
        this.oldConf = oldConf;
    }

    public boolean isStable() {
        return this.oldConf.isEmpty();
    }

    public boolean isEmpty() {
        return this.conf.isEmpty();
    }

    public Set<PeerId> listPeers() {
        final Set<PeerId> ret = new HashSet<>(this.conf.listPeers());
        ret.addAll(this.oldConf.listPeers());
        return ret;
    }

    public boolean contains(PeerId peer) {
        return this.conf.contains(peer) || this.oldConf.contains(peer);
    }

    @Override
    public String toString() {
        return "ConfigurationEntry [id=" + this.id + ", conf=" + this.conf + ", oldConf=" + this.oldConf + "]";
    }
}
