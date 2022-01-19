package dev.fabien2s.annoyingapi.nbt.tag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class NbtTag<T> {

    @Getter private final byte id;
    @Getter final T value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
