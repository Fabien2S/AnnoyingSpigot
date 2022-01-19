package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtByteArray extends NbtTag<byte[]> {

    public NbtByteArray(byte[] value) {
        super(NbtRegistry.BYTE_ARRAY_TAG, value);
    }
}
