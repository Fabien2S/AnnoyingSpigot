package dev.fabien2s.annoyingapi.nbt.serialization;

import dev.fabien2s.annoyingapi.nbt.NbtRegistry;
import dev.fabien2s.annoyingapi.nbt.NbtSerialization;
import dev.fabien2s.annoyingapi.nbt.exception.NbtFormatException;
import dev.fabien2s.annoyingapi.nbt.tag.NbtCompound;
import dev.fabien2s.annoyingapi.nbt.tag.NbtTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class NbtCompoundSerializer extends NbtSerializer<NbtCompound, Map<String, NbtTag<?>>> {

    @Override
    public NbtCompound deserialize(DataInput input) throws IOException {
        NbtCompound nbtCompound = new NbtCompound();

        byte tagId;
        while (((tagId = input.readByte()) != NbtRegistry.END_TAG)) {
            String tagName = input.readUTF();

            NbtTag<?> tag = NbtSerialization.deserialize(tagId, input);
            NbtTag<?> oldTag = nbtCompound.put(tagName, tag);
            if (oldTag != null)
                throw new NbtFormatException("Duplicate entry in TAG_Compound: " + tagName);
        }

        return nbtCompound;
    }

    @Override
    public void serialize(Map<String, NbtTag<?>> value, DataOutput output) throws IOException {
        Set<Map.Entry<String, NbtTag<?>>> entries = value.entrySet();
        for (Map.Entry<String, NbtTag<?>> entry : entries) {

            NbtTag<?> tag = entry.getValue();

            byte tagId = tag.getId();
            String name = entry.getKey();
            output.writeByte(tagId);
            output.writeUTF(name);

            NbtSerialization.serialize(tag, output);
        }

        output.writeByte(NbtRegistry.END_TAG);
    }

}
