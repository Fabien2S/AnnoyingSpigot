package dev.fabien2s.gannoyingapi.world.object;

import dev.fabien2s.gannoyingapi.world.GameObject;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import org.bukkit.Location;

public interface IGameObjectBuilder<T extends GameObject> {

    T build(GameWorld world, Location location);

}
