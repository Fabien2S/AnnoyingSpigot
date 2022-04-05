package dev.fabien2s.annoyingapi.magical;

import lombok.Getter;
import org.bukkit.NamespacedKey;

import java.util.HashMap;

public class MagicalDouble implements IValueSupplier {

    @Getter private double baseValue;

    private final HashMap<NamespacedKey, Modifier> modifiers = new HashMap<>();
    private double cachedValue;

    public MagicalDouble(double baseValue) {
        this.baseValue = baseValue;
        this.invalidate();
    }

    private double recomputeValue() {
        this.cachedValue = baseValue;
        for (Modifier modifier : modifiers.values()) {
            switch (modifier.operation) {
                case ADD -> cachedValue += modifier.value;
                case ADD_SCALAR -> cachedValue += cachedValue * modifier.value;
            }
        }
        return cachedValue;
    }

    private void invalidate() {
        this.cachedValue = Double.NaN;
    }

    public void setBaseValue(double baseValue) {
        this.baseValue = baseValue;
        this.invalidate();
    }

    public void addModifier(NamespacedKey key, Operation operation, java.util.function.DoubleSupplier doubleSupplier) {
        this.addModifier(key, operation, doubleSupplier.getAsDouble());
    }

    public void addModifier(NamespacedKey key, Operation operation, double value) {
        this.modifiers.put(key, new Modifier(operation, value));
        this.invalidate();
    }

    public void removeModifier(NamespacedKey key) {
        this.modifiers.remove(key);
        this.invalidate();
    }

    public void clearModifiers() {
        this.modifiers.clear();
        this.invalidate();
    }

    @Override
    public double getValue() {
        return isInvalid() ? recomputeValue() : cachedValue;
    }

    public boolean isInvalid() {
        return Double.isNaN(cachedValue);
    }

    public enum Operation {
        ADD, ADD_SCALAR
    }

    private record Modifier(Operation operation, double value) {
    }

}
