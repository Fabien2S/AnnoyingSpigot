package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtInt extends NbtTag<Integer> {

    public NbtInt(int value) {
        super(NbtRegistry.INT_TAG, value);
    }

}
