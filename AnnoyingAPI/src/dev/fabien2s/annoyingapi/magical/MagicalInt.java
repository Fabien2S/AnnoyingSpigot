package dev.fabien2s.annoyingapi.magical;

import java.util.function.IntSupplier;

public class MagicalInt extends MagicalDouble implements IntSupplier {

    public MagicalInt(double baseValue) {
        super(baseValue);
    }

    @Override
    public double getValue() {
        return Math.floor(super.getValue());
    }

    @Override
    public int getAsInt() {
        return (int) getValue();
    }

}
