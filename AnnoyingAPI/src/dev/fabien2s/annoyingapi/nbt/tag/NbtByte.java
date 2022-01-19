package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtByte extends NbtTag<Byte> {

    public NbtByte(byte value) {
        super(NbtRegistry.BYTE_TAG, value);
    }

}
