package dev.fabien2s.annoyingapi.magical;

import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashMap;

@RequiredArgsConstructor
public class MagicalCollection<T> {

    @Nonnull private final T defaultValue;
    @Nonnull private final Comparator<T> comparator;
    private final HashMap<NamespacedKey, T> modifiers = new HashMap<>();

    private T cachedValue;

    private void invalidate() {
        this.cachedValue = null;
    }

    private T recomputeValue() {
        this.cachedValue = defaultValue;
        for (T value : modifiers.values()) {
            if (comparator.compare(value, cachedValue) > 0)
                this.cachedValue = value;
        }
        return cachedValue;
    }

    public void addModifier(NamespacedKey key, T value) {
        this.modifiers.put(key, value);
        this.invalidate();
    }

    public void removeModifier(NamespacedKey key) {
        this.modifiers.remove(key);
        this.invalidate();
    }

    public void removeModifiers() {
        this.modifiers.clear();
        this.invalidate();
    }

    public boolean isInvalid() {
        return cachedValue == null;
    }

    public T getValue() {
        return isInvalid() ? recomputeValue() : cachedValue;
    }

}
