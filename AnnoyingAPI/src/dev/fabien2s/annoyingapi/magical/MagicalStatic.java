package dev.fabien2s.annoyingapi.magical;

import lombok.Getter;

public class MagicalStatic implements IValueSupplier {

    public static final MagicalStatic ZERO = new MagicalStatic(0);
    public static final MagicalStatic ONE = new MagicalStatic(1);
    public static final MagicalStatic POSITIVE_INFINITY = new MagicalStatic(Double.POSITIVE_INFINITY);

    @Getter private final double value;

    public MagicalStatic(double value) {
        this.value = value;
    }

    @Override
    public double getBaseValue() {
        return value;
    }

}
