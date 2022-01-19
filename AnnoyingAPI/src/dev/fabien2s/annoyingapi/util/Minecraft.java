package dev.fabien2s.annoyingapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Minecraft {

    public static final int TICK_PER_SECOND = 20;

    public static final int CHUNK_SIZE = 16;

    public static NamespacedKey parseIdentifier(String identifier) {
        return NamespacedKey.fromString(identifier);
    }

    public static NamespacedKey createIdentifier(String namespace, String key) {
        return new NamespacedKey(namespace, key);
    }
}
