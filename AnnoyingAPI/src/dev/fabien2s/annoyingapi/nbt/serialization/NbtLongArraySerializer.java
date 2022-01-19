package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtLongArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtLongArraySerializer extends NbtSerializer<NbtLongArray, long[]> {

    @Override
    public NbtLongArray deserialize(DataInput input) throws IOException {
        int length = input.readInt();
        long[] value = new long[length];
        for (int i = 0; i < length; i++)
            value[i] = input.readLong();
        return new NbtLongArray(value);
    }

    @Override
    public void serialize(long[] value, DataOutput output) throws IOException {
        output.writeInt(value.length);
        for (long l : value)
            output.writeLong(l);
    }

}
