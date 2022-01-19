package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.tag.NbtTag;
import lombok.RequiredArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@RequiredArgsConstructor
public abstract class NbtSerializer<T extends NbtTag<U>, U> {

    public abstract T deserialize(DataInput input) throws IOException;

    public abstract void serialize(U value, DataOutput output) throws IOException;

}
