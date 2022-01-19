package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtByteArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtByteArraySerializer extends NbtSerializer<NbtByteArray, byte[]> {

    @Override
    public NbtByteArray deserialize(DataInput input) throws IOException {
        int length = input.readInt();
        byte[] value = new byte[length];
        for (int i = 0; i < length; i++)
            value[i] = input.readByte();
        return new NbtByteArray(value);
    }

    @Override
    public void serialize(byte[] value, DataOutput output) throws IOException {
        output.writeInt(value.length);
        for (byte b : value)
            output.writeByte(b);
    }

}
