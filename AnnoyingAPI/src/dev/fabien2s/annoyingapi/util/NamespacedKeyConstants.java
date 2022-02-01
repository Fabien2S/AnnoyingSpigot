package dev.fabien2s.annoyingapi.util;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import org.bukkit.NamespacedKey;

public interface NamespacedKeyConstants {

    NamespacedKey NAME = AnnoyingPlugin.createKey("Name");
    NamespacedKey TAG = AnnoyingPlugin.createKey("Tag");
    NamespacedKey DATA = AnnoyingPlugin.createKey("Data");

    NamespacedKey X = AnnoyingPlugin.createKey("X");
    NamespacedKey Y = AnnoyingPlugin.createKey("Y");
    NamespacedKey Z = AnnoyingPlugin.createKey("Z");

    NamespacedKey YAW = AnnoyingPlugin.createKey("Yaw");
    NamespacedKey PITCH = AnnoyingPlugin.createKey("Pitch");

}
