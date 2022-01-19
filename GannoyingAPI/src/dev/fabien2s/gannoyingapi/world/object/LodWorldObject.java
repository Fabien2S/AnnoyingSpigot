package dev.fabien2s.gannoyingapi.world.object;

import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import dev.fabien2s.gannoyingapi.world.lod.LodController;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class LodWorldObject extends WorldObject implements IGameObjectRegistrable {

    private final LodController lodController = new LodController(this);

    public LodWorldObject(GameWorld world, String name, Location location) {
        super(world, name, location);
    }

    public LodWorldObject(GameWorld world, String name, Location location, Vector renderOffset) {
        super(world, name, location, renderOffset);
    }

    @Override
    public void init() {
        super.init();
        this.registerStates(lodController);
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);
        this.lodController.tick(deltaTime);
    }

    @Override
    public void register(GamePlayer gamePlayer) {
        super.register(gamePlayer);
        this.lodController.addPlayer(gamePlayer);
    }

    @Override
    public void unregister(GamePlayer gamePlayer) {
        super.unregister(gamePlayer);
        this.lodController.removePlayer(gamePlayer);
    }

    protected abstract void registerStates(LodController controller);

}
