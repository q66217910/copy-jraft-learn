package com.zd.jraft.entity;

public enum State {

    /**
     * LEADER
     */
    STATE_LEADER,
    /**
     * Leader转移中
     */
    STATE_TRANSFERRING;

}
