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

    public boolean hasChecksum() {
        return this.hasChecksum;
    }

    public boolean isCorrupted() {
        return this.hasChecksum && this.checksum != checksum();
    }

    public long getChecksum() {
        return this.checksum;
    }

    public EnumOuter.EntryType getType() {
        return this.type;
    }

    public void setType(final EnumOuter.EntryType type) {
        this.type = type;
    }

    public LogId getId() {
        return this.id;
    }

    public void setId(final LogId id) {
        this.id = id;
    }

    public List<PeerId> getPeers() {
        return this.peers;
    }

    public void setPeers(final List<PeerId> peers) {
        this.peers = peers;
    }

    public List<PeerId> getOldPeers() {
        return this.oldPeers;
    }

    public void setOldPeers(final List<PeerId> oldPeers) {
        this.oldPeers = oldPeers;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public void setData(final ByteBuffer data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LogEntry [type=" + this.type + ", id=" + this.id + ", peers=" + this.peers + ", oldPeers="
                + this.oldPeers + ", data=" + (this.data != null ? this.data.remaining() : 0) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.data == null ? 0 : this.data.hashCode());
        result = prime * result + (this.id == null ? 0 : this.id.hashCode());
        result = prime * result + (this.oldPeers == null ? 0 : this.oldPeers.hashCode());
        result = prime * result + (this.peers == null ? 0 : this.peers.hashCode());
        result = prime * result + (this.type == null ? 0 : this.type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LogEntry other = (LogEntry) obj;
        if (this.data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!this.data.equals(other.data)) {
            return false;
        }
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        if (this.oldPeers == null) {
            if (other.oldPeers != null) {
                return false;
            }
        } else if (!this.oldPeers.equals(other.oldPeers)) {
            return false;
        }
        if (this.peers == null) {
            if (other.peers != null) {
                return false;
            }
        } else if (!this.peers.equals(other.peers)) {
            return false;
        }
        return this.type == other.type;
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
