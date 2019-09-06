package com.zd.jraft.conf;

import com.zd.jraft.common.Copiable;
import com.zd.jraft.entity.PeerId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration implements Iterable<PeerId>, Copiable<Configuration> {

    private List<PeerId> peers = new ArrayList<>();

    public Configuration() {
        super();
    }

    public Configuration(final Iterable<PeerId> conf) {
        for (final PeerId peer : conf) {
            this.peers.add(peer.copy());
        }
    }

    @Override
    public Configuration copy() {
        return null;
    }

    @Override
    public Iterator<PeerId> iterator() {
        return this.peers.iterator();
    }

    public void setPeers(List<PeerId> peers) {
        this.peers = peers.stream().map(PeerId::copy).collect(Collectors.toList());
    }

    public void reset() {
        this.peers.clear();
    }

    public boolean isEmpty() {
        return this.peers.isEmpty();
    }

    public int size() {
        return this.peers.size();
    }

    public void appendPeers(final Collection<PeerId> set) {
        this.peers.addAll(set);
    }

    public List<PeerId> listPeers() {
        return new ArrayList<>(this.peers);
    }

    public boolean contains(final PeerId peer) {
        return this.peers.contains(peer);
    }
}
