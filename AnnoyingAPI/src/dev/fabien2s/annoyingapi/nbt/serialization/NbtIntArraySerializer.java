package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtIntArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtIntArraySerializer extends NbtSerializer<NbtIntArray, int[]> {

    @Override
    public NbtIntArray deserialize(DataInput input) throws IOException {
        int length = input.readInt();
        int[] value = new int[length];
        for (int i = 0; i < length; i++)
            value[i] = input.readInt();
        return new NbtIntArray(value);
    }

    @Override
    public void serialize(int[] value, DataOutput output) throws IOException {
        output.writeInt(value.length);
        for (int i : value)
            output.writeInt(i);
    }

}
