package com.zd.jraft.utils;

public final class CrcUtil {

    private static final ThreadLocal<CRC64> CRC_64_THREAD_LOCAL = ThreadLocal.withInitial(CRC64::new);

    /**
     * Compute CRC64 checksum for byte[].
     *
     * @param array source array
     * @return checksum value
     */
    public static long crc64(final byte[] array) {
        if (array != null) {
            return crc64(array, 0, array.length);
        }

        return 0;
    }

    /**
     * Compute CRC64 checksum for byte[].
     *
     * @param array  source array
     * @param offset starting position in the source array
     * @param length the number of array elements to be computed
     * @return checksum value
     */
    public static long crc64(final byte[] array, final int offset, final int length) {
        final CRC64 crc32 = CRC_64_THREAD_LOCAL.get();
        crc32.update(array, offset, length);
        final long ret = crc32.getValue();
        crc32.reset();
        return ret;
    }
}
