package com.zd.jraft.machine;

import com.zd.jraft.error.RaftException;

public interface FSMCaller {

    boolean onError(final RaftException error);

}
