package com.zd.jraft.closure;

import com.zd.jraft.entity.LogEntry;
import com.zd.jraft.node.Status;

import java.util.List;

public class LeaderStableClosure extends StableClosure{

    public LeaderStableClosure(final List<LogEntry> entries) {
        super(entries);
    }

    @Override
    public void run(Status status) {

    }
}
