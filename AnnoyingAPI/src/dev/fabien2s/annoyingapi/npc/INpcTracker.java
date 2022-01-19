package dev.fabien2s.annoyingapi.npc;

import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import org.bukkit.Location;

public interface INpcTracker {

    void spawn(IPlayerController controller);

    void remove(IPlayerController controller);

    void setHeadRotation(float headRotation);

    void teleport(Location location);

    void move(Location location, short x, short y, short z, float yaw, float pitch, boolean updatePosition, boolean updateRotation);
}
