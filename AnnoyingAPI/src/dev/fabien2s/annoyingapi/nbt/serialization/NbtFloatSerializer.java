package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtFloat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtFloatSerializer extends NbtSerializer<NbtFloat, Float> {

    @Override
    public NbtFloat deserialize(DataInput input) throws IOException {
        return new NbtFloat(input.readFloat());
    }

    @Override
    public void serialize(Float value, DataOutput output) throws IOException {
        output.writeFloat(value);
    }

}
