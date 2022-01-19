package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtStringSerializer extends NbtSerializer<NbtString, String> {

    @Override
    public NbtString deserialize(DataInput input) throws IOException {
        return new NbtString(input.readUTF());
    }

    @Override
    public void serialize(String value, DataOutput output) throws IOException {
        output.writeUTF(value);
    }

}
