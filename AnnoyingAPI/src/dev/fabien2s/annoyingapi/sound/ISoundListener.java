package dev.fabien2s.annoyingapi.sound;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;

public interface ISoundListener {

    default void playSound(Entity entity, Sound sound, SoundCategory category) {
        this.playSound(entity, sound, category, 1, 1);
    }

    void playSound(Entity entity, Sound sound, SoundCategory category, float volume, float pitch);

    default void playSound(Location location, Sound sound, SoundCategory category) {
        this.playSound(location, sound, category, 1, 1);
    }

    void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch);

    default void playSound(Location location, NamespacedKey sound, SoundCategory category) {
        this.playSound(location, sound, category, 1, 1);
    }

    void playSound(Location location, NamespacedKey sound, SoundCategory category, float volume, float pitch);

    default void playSound2D(Sound sound, SoundCategory category) {
        this.playSound2D(sound, category, 1, 1);
    }

    void playSound2D(Sound sound, SoundCategory category, float volume, float pitch);

    default void playSound2D(NamespacedKey sound, SoundCategory category) {
        this.playSound2D(sound, category, 1, 1);
    }

    void playSound2D(NamespacedKey sound, SoundCategory category, float volume, float pitch);

}
