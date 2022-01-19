package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtIntArray extends NbtTag<int[]> {

    public NbtIntArray(int[] value) {
        super(NbtRegistry.INT_ARRAY_TAG, value);
    }

}
