package com.zd.jraft.entity;

public class UnfoundPeerId {

    private PeerId peerId;
    private boolean found;
    private int index;

    public UnfoundPeerId(PeerId peerId, int index, boolean found) {
        super();
        this.peerId = peerId;
        this.index = index;
        this.found = found;
    }
}
