package dev.fabien2s.gannoyingapi.world;

import org.bukkit.World;
import org.bukkit.WorldCreator;

public interface IWorldBuilder {

    World build(GameWorld gameWorld, WorldCreator creator);

}
