package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtLong;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtLongSerializer extends NbtSerializer<NbtLong, Long> {

    @Override
    public NbtLong deserialize(DataInput input) throws IOException {
        return new NbtLong(input.readLong());
    }

    @Override
    public void serialize(Long value, DataOutput output) throws IOException {
        output.writeLong(value);
    }

}
