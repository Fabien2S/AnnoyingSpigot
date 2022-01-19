package dev.fabien2s.annoyingapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BitSet {

    public static boolean has(int value, int mask) {
        return (value & mask) == mask;
    }

    public static int set(int value, int mask, boolean set) {
        if (set)
            return set(value, mask);
        else
            return clear(value, mask);
    }

    public static int set(int value, int mask) {
        return value | mask;
    }

    public static int clear(int value, int mask) {
        return value & ~mask;
    }

}
