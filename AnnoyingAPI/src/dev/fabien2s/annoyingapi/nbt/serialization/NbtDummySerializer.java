package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtTag;

import java.io.DataInput;
import java.io.DataOutput;

public class NbtDummySerializer extends NbtSerializer<NbtTag<Object>, Object> {

    @Override
    public NbtTag<Object> deserialize(DataInput input) {
        return null;
    }

    @Override
    public void serialize(Object value, DataOutput output) {
    }

}
