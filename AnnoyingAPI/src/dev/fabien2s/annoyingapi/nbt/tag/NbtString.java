package dev.fabien2s.annoyingapi.nbt.tag;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;

public class NbtString extends NbtTag<String> {

    public NbtString(String value) {
        super(NbtRegistry.STRING_TAG, value);
    }

}