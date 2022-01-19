package dev.fabien2s.annoyingapi.nbt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dev.fabien2s.annoyingapi.nbt.exception.NbtFormatException;
import dev.fabien2s.annoyingapi.nbt.tag.NbtCompound;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NbtIO {

    public static NbtCompound read(DataInput input) throws IOException {
        byte tagId = input.readByte();
        if (tagId != NbtRegistry.COMPOUND_TAG)
            throw new NbtFormatException("TAG_Compound expected");

        short length = input.readShort();
        if (length != 0)
            throw new NbtFormatException("Unexpected root compound name length!");

        return (NbtCompound) NbtSerialization.deserialize(tagId, input);
    }

    public static void write(DataOutput output, NbtCompound nbtCompound) throws IOException {
        output.writeByte(NbtRegistry.COMPOUND_TAG);
        output.writeShort(0);
        NbtSerialization.serialize(nbtCompound, output);
    }
}
