package dev.fabien2s.annoyingapi.entity.tracker;

import org.bukkit.entity.Player;

public interface IEntityTracker {

    boolean isTracked(Player player);

    void sendPacket(Object packet, boolean includeSelf);

    default void broadcastPacket(Object packet, boolean includeSelf) {
        this.sendPacket(packet, includeSelf);
    }

}
