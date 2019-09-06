package com.zd.jraft.closure;

public interface TaskClosure extends Closure {

    void onCommitted();
}
