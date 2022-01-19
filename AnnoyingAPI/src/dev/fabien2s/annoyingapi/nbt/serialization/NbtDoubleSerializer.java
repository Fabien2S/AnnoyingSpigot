package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtDouble;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtDoubleSerializer extends NbtSerializer<NbtDouble, Double> {

    @Override
    public NbtDouble deserialize(DataInput input) throws IOException {
        return new NbtDouble(input.readDouble());
    }

    @Override
    public void serialize(Double value, DataOutput output) throws IOException {
        output.writeDouble(value);
    }

}
