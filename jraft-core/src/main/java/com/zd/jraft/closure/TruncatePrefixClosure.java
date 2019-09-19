package com.zd.jraft.closure;

import com.zd.jraft.node.Status;

public class TruncatePrefixClosure extends StableClosure {


    long firstIndexKept;

    public TruncatePrefixClosure(final long firstIndexKept) {
        super(null);
        this.firstIndexKept = firstIndexKept;
    }

    @Override
    public void run(Status status) {

    }
}
