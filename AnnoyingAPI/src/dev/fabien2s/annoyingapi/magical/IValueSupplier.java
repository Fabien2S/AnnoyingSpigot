package dev.fabien2s.annoyingapi.magical;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public interface IValueSupplier extends DoubleSupplier, IntSupplier {

    double getValue();

    double getBaseValue();

    @Override
    default double getAsDouble() {
        return getValue();
    }

    @Override
    default int getAsInt() {
        return (int) Math.floor(getValue());
    }

}
