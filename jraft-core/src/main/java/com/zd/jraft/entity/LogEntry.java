package com.zd.jraft.entity;

import com.zd.jraft.utils.CrcUtil;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 副本日志条目
 */
public class LogEntry implements Checksum {

    private EnumOuter.EntryType type;

    private LogId id = new LogId(0, 0);

    private List<PeerId> peers;

    private List<PeerId> oldPeers;

    private ByteBuffer data;

    private long checksum;

    private boolean hasChecksum;

    public LogEntry() {
        super();
    }

    public LogEntry(final EnumOuter.EntryType type) {
        super();
        this.type = type;
    }

    @Override
    public long checksum() {
        long c = this.checksum(this.type.getNumber(), this.id.checksum());
        if (this.peers != null && !this.peers.isEmpty()) {
            for (PeerId peer : this.peers) {
                c = this.checksum(c, peer.checksum());
            }
        }
        if (this.oldPeers != null && !this.oldPeers.isEmpty()) {
            for (PeerId peer : this.oldPeers) {
                c = this.checksum(c, peer.checksum());
            }
        }
        if (this.data != null && this.data.hasRemaining()) {
            byte[] bs = new byte[this.data.remaining()];
            this.data.mark();
            this.data.get(bs);
            this.data.reset();
            c = this.checksum(c, CrcUtil.crc64(bs));
        }
        return c;
    }
}
