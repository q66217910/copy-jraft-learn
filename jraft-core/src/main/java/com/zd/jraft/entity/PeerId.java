package com.zd.jraft.entity;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.zd.jraft.common.Copiable;
import com.zd.jraft.utils.AsciiStringUtil;
import com.zd.jraft.utils.CrcUtil;
import com.zd.jraft.utils.Utils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

public class PeerId implements Copiable<PeerId>, Serializable, Checksum {

    private static final Logger LOG = LoggerFactory.getLogger(PeerId.class);

    private long checksum;

    /**
     * 地址：ip地址+端口号
     */
    private Endpoint endpoint = new Endpoint(Utils.IP_ANY, 0);

    /**
     * 地址相同时的索引
     */
    private int idx;

    /**
     * toString 的缓存
     */
    private String str;

    @Override
    public long checksum() {
        if (this.checksum == 0) {
            this.checksum = CrcUtil.crc64(AsciiStringUtil.unsafeEncode(toString()));
        }
        return this.checksum;
    }

    public PeerId() {
        super();
    }

    public PeerId(final String ip, final int port) {
        this(ip, port, 0);
    }

    public PeerId(final String ip, final int port, final int idx) {
        super();
        this.endpoint = new Endpoint(ip, port);
        this.idx = idx;
    }

    public Endpoint getEndpoint() {
        return this.endpoint;
    }

    public String getIp() {
        return this.endpoint.getIp();
    }

    public int getPort() {
        return this.endpoint.getPort();
    }

    public int getIdx() {
        return this.idx;
    }

    public PeerId(final Endpoint endpoint, final int idx) {
        super();
        this.endpoint = endpoint;
        this.idx = idx;
    }


    /**
     * Create an empty peer.
     *
     * @return empty peer
     */
    public static PeerId emptyPeer() {
        return new PeerId();
    }

    @Override
    public PeerId copy() {
        return new PeerId(this.endpoint, this.idx);
    }

    /**
     * 当0.0.0.0：0.0时返回true
     */
    public boolean isEmpty() {
        return getIp().equals(Utils.IP_ANY) && getPort() == 0 && this.idx == 0;
    }

    public boolean parse(final String s) {
        final String[] tmps = StringUtils.split(s, ':');
        if (tmps.length != 3 && tmps.length != 2) {
            return false;
        }
        try {
            final int port = Integer.parseInt(tmps[1]);
            this.endpoint = new Endpoint(tmps[0], port);
            if (tmps.length == 3) {
                this.idx = Integer.parseInt(tmps[2]);
            } else {
                this.idx = 0;
            }
            this.str = null;
            return true;
        } catch (final Exception e) {
            LOG.error("Parse peer from string failed: {}", s, e);
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.endpoint == null ? 0 : this.endpoint.hashCode());
        result = prime * result + this.idx;
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
        final PeerId other = (PeerId) obj;
        if (this.endpoint == null) {
            if (other.endpoint != null) {
                return false;
            }
        } else if (!this.endpoint.equals(other.endpoint)) {
            return false;
        }
        return this.idx == other.idx;
    }
}
