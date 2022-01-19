package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtByte;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtByteSerializer extends NbtSerializer<NbtByte, Byte> {

    @Override
    public NbtByte deserialize(DataInput input) throws IOException {
        return new NbtByte(input.readByte());
    }

    @Override
    public void serialize(Byte value, DataOutput output) throws IOException {
        output.writeByte(value);
    }

}
