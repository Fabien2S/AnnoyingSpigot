package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtShort extends NbtTag<Short> {

    public NbtShort(short value) {
        super(NbtRegistry.SHORT_TAG, value);
    }

}
