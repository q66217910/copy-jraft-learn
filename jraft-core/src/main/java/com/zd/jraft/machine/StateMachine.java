package com.zd.jraft.machine;

import java.nio.ByteBuffer;

public interface StateMachine extends java.util.Iterator<ByteBuffer>{

    void onApply(final Iterator iter);
}
