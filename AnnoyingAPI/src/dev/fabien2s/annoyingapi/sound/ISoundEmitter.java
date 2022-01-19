package dev.fabien2s.annoyingapi.sound;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISoundEmitter {

    default void emitSound(@Nonnull Sound sound, @Nonnull SoundCategory category) {
        this.emitSound(sound, category, 1, 1);
    }

    void emitSound(@Nonnull Sound sound, @Nonnull SoundCategory category, float volume, float pitch);

    default void emitSound(@Nonnull NamespacedKey sound, @Nonnull SoundCategory category) {
        this.emitSound(sound, category, 1, 1);
    }

    void emitSound(@Nonnull NamespacedKey sound, @Nonnull SoundCategory category, float volume, float pitch);

    void stopEmittingSound(Sound sound, @Nullable SoundCategory category);

    void stopEmittingSound(@Nonnull NamespacedKey sound, @Nullable SoundCategory category);

}
