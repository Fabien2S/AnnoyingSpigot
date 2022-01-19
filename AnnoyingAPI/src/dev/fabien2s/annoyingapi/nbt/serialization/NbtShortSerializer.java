package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtShort;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtShortSerializer extends NbtSerializer<NbtShort, Short> {

    @Override
    public NbtShort deserialize(DataInput input) throws IOException {
        return new NbtShort(input.readShort());
    }

    @Override
    public void serialize(Short value, DataOutput output) throws IOException {
        output.writeShort(value);
    }

}
