package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtLong extends NbtTag<Long> {

    public NbtLong(long value) {
        super(NbtRegistry.LONG_TAG, value);
    }

}
