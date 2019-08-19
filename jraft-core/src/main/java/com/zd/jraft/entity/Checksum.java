package com.zd.jraft.entity;

/**
 * 校验
 */
public interface Checksum {

    /**
     * 计算值进行校验
     */
    long checksum();


    /**
     * Returns the checksum value of two long values.
     *
     * @param v1 first long value
     * @param v2 second long value
     * @return checksum value
     */
    default long checksum(final long v1, final long v2) {
        return v1 ^ v2;
    }

}
