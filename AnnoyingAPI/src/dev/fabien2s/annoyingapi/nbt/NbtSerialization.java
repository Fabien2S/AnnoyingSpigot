package dev.fabien2s.annoyingapi.nbt;

import dev.fabien2s.annoyingapi.nbt.serialization.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dev.fabien2s.annoyingapi.nbt.exception.NbtFormatException;
import dev.fabien2s.annoyingapi.nbt.tag.NbtTag;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NbtSerialization {

    private static final NbtSerializer<?, ?>[] SERIALIZERS = {
            new NbtDummySerializer(),
            new NbtByteSerializer(),
            new NbtShortSerializer(),
            new NbtIntSerializer(),
            new NbtLongSerializer(),
            new NbtFloatSerializer(),
            new NbtDoubleSerializer(),
            new NbtByteArraySerializer(),
            new NbtStringSerializer(),
            new NbtListSerializer<>(),
            new NbtCompoundSerializer(),
            new NbtIntArraySerializer(),
            new NbtLongArraySerializer()
    };

    private static NbtSerializer<?, ?> getSerializer(byte id) {
        if (0 <= id && id < SERIALIZERS.length)
            return SERIALIZERS[id];
        throw new NbtFormatException("Unknown id " + id);
    }

    public static NbtTag<?> deserialize(byte id, DataInput input) throws IOException {
        NbtSerializer<?, ?> serializer = getSerializer(id);
        return serializer.deserialize(input);
    }

    @SuppressWarnings("unchecked")
    public static <T extends NbtTag<U>, U> void serialize(T tag, DataOutput output) throws IOException {
        NbtSerializer<T, U> serializer = (NbtSerializer<T, U>) getSerializer(tag.getId());
        serializer.serialize(tag.getValue(), output);
    }

}
