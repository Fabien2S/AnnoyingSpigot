package dev.fabien2s.annoyingapi.util;

import lombok.Getter;

public enum SkinPart {

    CAPE,
    JACKET,
    LEFT_SLEEVE,
    RIGHT_SLEEVE,
    LEFT_PANTS_LEG,
    RIGHT_PANTS_LEG,
    HAT;

    @Getter private final byte mask;

    SkinPart() {
        this.mask = (byte) (1 << ordinal());
    }

}
