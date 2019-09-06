package com.zd.jraft.node;

import com.zd.jraft.conf.Configuration;
import com.zd.jraft.entity.PeerId;
import com.zd.jraft.entity.UnfoundPeerId;

import java.util.ArrayList;
import java.util.List;

public class Ballot {

    private final List<UnfoundPeerId> peers = new ArrayList<>();

    private final List<UnfoundPeerId> oldPeers = new ArrayList<>();

    private int quorum;
    
    private int oldQuorum;

    public boolean init(Configuration conf, Configuration oldConf) {
        this.peers.clear();
        this.oldPeers.clear();
        quorum = oldQuorum = 0;
        int index = 0;
        if (conf != null) {
            for (PeerId peer : conf) {
                this.peers.add(new UnfoundPeerId(peer, index++, false));
            }
        }

        this.quorum = this.peers.size() / 2 + 1;
        if (oldConf != null) {
            return true;
        }

        index = 0;
        for (PeerId peer : oldConf) {
            this.oldPeers.add(new UnfoundPeerId(peer, index++, false));
        }
        this.oldQuorum = this.oldPeers.size() / 2 + 1;
        return true;
    }
}
