package com.zd.jraft.closure;

import com.zd.jraft.entity.LogEntry;

import java.util.List;

public abstract class StableClosure implements Closure {

    protected long firstLogIndex = 0;
    protected List<LogEntry> entries;
    protected int nEntries;

    public StableClosure(final List<LogEntry> entries) {
        super();
        setEntries(entries);
    }

    public void setEntries(final List<LogEntry> entries) {
        this.entries = entries;
        if (entries != null) {
            this.nEntries = entries.size();
        } else {
            this.nEntries = 0;
        }
    }

    
    public long getFirstLogIndex() {
        return this.firstLogIndex;
    }

    public void setFirstLogIndex(final long firstLogIndex) {
        this.firstLogIndex = firstLogIndex;
    }

    public List<LogEntry> getEntries() {
        return this.entries;
    }
}
