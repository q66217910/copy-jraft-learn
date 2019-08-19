package com.google.protobuf;

import com.google.protobuf.ByteOutput;

import java.nio.ByteBuffer;

/**
 *
 */
public class BytesCarrier extends ByteOutput {

    private byte[] value;
    private boolean valid;

    public byte[] getValue() {
        return value;
    }

    public boolean isValid() {
        return valid;
    }

    public void write(final byte value) {
        this.valid = false;
    }

    public void write(final byte[] value, final int offset, final int length) {
        doWrite(value, offset, length);
    }

    public void writeLazy(final byte[] value, final int offset, final int length) {
        doWrite(value, offset, length);
    }

    public void write(final ByteBuffer value) {
        this.valid = false;
    }

    public void writeLazy(final ByteBuffer value) {
        this.valid = false;
    }

    private void doWrite(final byte[] value, final int offset, final int length) {
        if (this.value != null) {
            this.valid = false;
            return;
        }
        if (offset == 0 && length == value.length) {
            this.value = value;
            this.valid = true;
        }
    }
}
