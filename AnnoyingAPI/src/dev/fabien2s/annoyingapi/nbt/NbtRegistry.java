package dev.fabien2s.annoyingapi.nbt;

import dev.fabien2s.annoyingapi.nbt.tag.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NbtRegistry {

    public static final byte END_TAG = 0;
    public static final byte BYTE_TAG = 1;
    public static final byte SHORT_TAG = 2;
    public static final byte INT_TAG = 3;
    public static final byte LONG_TAG = 4;
    public static final byte FLOAT_TAG = 5;
    public static final byte DOUBLE_TAG = 6;
    public static final byte BYTE_ARRAY_TAG = 7;
    public static final byte STRING_TAG = 8;
    public static final byte LIST_TAG = 9;
    public static final byte COMPOUND_TAG = 10;
    public static final byte INT_ARRAY_TAG = 11;
    public static final byte LONG_ARRAY_TAG = 12;

    private static final Class<?>[] CLASSES = {
            Void.class,
            NbtByte.class,
            NbtShort.class,
            NbtInt.class,
            NbtLong.class,
            NbtFloat.class,
            NbtDouble.class,
            NbtByteArray.class,
            NbtString.class,
            NbtList.class,
            NbtCompound.class,
            NbtIntArray.class,
            NbtLongArray.class
    };

    public static byte getID(Class<?> aClass) {
        for (byte i = 0; i < CLASSES.length; i++) {
            if (CLASSES[i].isAssignableFrom(aClass))
                return i;
        }
        return -1;
    }


}
