package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtIntSerializer extends NbtSerializer<NbtInt, Integer> {

    @Override
    public NbtInt deserialize(DataInput input) throws IOException {
        return new NbtInt(input.readInt());
    }

    @Override
    public void serialize(Integer value, DataOutput output) throws IOException {
        output.writeInt(value);
    }

}
