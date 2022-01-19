package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtFloat extends NbtTag<Float> {

    public NbtFloat(float value) {
        super(NbtRegistry.FLOAT_TAG, value);
    }

}
