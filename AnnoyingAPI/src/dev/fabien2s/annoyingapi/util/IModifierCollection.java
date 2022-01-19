package dev.fabien2s.annoyingapi.util;

import dev.fabien2s.annoyingapi.magical.IValueSupplier;
import dev.fabien2s.annoyingapi.magical.MagicalDouble;
import org.bukkit.NamespacedKey;

import java.util.function.Consumer;

public interface IModifierCollection {

    MagicalDouble getModifier(NamespacedKey key);

    MagicalDouble removeModifier(NamespacedKey key);

    void setModifier(NamespacedKey key, MagicalDouble modifier);

    default IValueSupplier getModifier(NamespacedKey key, IValueSupplier defaultValue) {
        MagicalDouble modifier = getModifier(key);
        return modifier == null ? defaultValue : modifier;
    }

    default void forModifier(NamespacedKey key, Consumer<MagicalDouble> callable) {
        MagicalDouble modifier = getModifier(key);
        if (modifier != null)
            callable.accept(modifier);
    }

}
