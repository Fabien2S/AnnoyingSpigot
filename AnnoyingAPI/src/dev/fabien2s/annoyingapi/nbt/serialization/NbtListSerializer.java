package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;
import dev.fabien2s.annoyingapi.nbt.NbtSerialization;
import dev.fabien2s.annoyingapi.nbt.exception.NbtFormatException;
import dev.fabien2s.annoyingapi.nbt.tag.NbtList;
import dev.fabien2s.annoyingapi.nbt.tag.NbtTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class NbtListSerializer<T extends NbtTag<U>, U> extends NbtSerializer<NbtList<T, U>, List<T>> {

    @Override
    public NbtList<T, U> deserialize(DataInput input) throws IOException {
        byte tagId = input.readByte();
        int length = input.readInt();

        if (tagId == NbtRegistry.END_TAG && length != 0)
            throw new NbtFormatException("Invalid list type (Tag_END)");

        NbtList<T, U> nbtList = new NbtList<>(length);
        for (int i = 0; i < length; i++) {
            @SuppressWarnings("unchecked")
            T tag = (T) NbtSerialization.deserialize(tagId, input);
            nbtList.add(tag);
        }

        return nbtList;
    }

    @Override
    public void serialize(List<T> value, DataOutput output) throws IOException {
        if (value.isEmpty()) {
            output.writeByte(NbtRegistry.END_TAG);
            output.writeInt(0);
            return;
        }

        T firstTag = value.get(0);

        byte listTypeId = firstTag.getId();
        output.writeByte(listTypeId);

        output.writeInt(value.size());

        for (T tag : value) {
            byte tagId = tag.getId();
            if (tagId != listTypeId)
                throw new IOException("Tag mismatch (" + firstTag + " != " + tag + ')');

            NbtSerialization.serialize(tag, output);
        }
    }

}
