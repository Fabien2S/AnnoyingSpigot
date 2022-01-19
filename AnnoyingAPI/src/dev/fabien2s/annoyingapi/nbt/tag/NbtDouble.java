package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtDouble extends NbtTag<Double> {

    public NbtDouble(double value) {
        super(NbtRegistry.DOUBLE_TAG, value);
    }

}
