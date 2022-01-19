package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtLongArray extends NbtTag<long[]> {

    public NbtLongArray(long[] value) {
        super(NbtRegistry.LONG_ARRAY_TAG, value);
    }

}
