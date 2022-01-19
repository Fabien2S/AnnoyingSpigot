package dev.fabien2s.gannoyingapi.world.object;

import lombok.Getter;
import dev.fabien2s.annoyingapi.entity.renderer.EntityArmorStandRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.GameObject;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public abstract class WorldObject extends GameObject {

    @Getter protected ArmorStand armorStand;
    @Getter protected EntityArmorStandRenderer renderer;

    protected final Vector renderOffset;

    public WorldObject(GameWorld world, String name, Location location) {
        this(world, name, location, VectorHelper.zero());
    }

    protected WorldObject(GameWorld world, String name, Location location, Vector renderOffset) {
        super(world, name, location);
        this.renderOffset = renderOffset.clone();
    }

    @Override
    public void init() {
        super.init();

        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("world is null");

        this.armorStand = createArmorStand(
                location.clone()
                        .add(renderOffset)
        );

        GamePlugin plugin = gameWorld.getPlugin();
        EntityRendererManager entityRendererManager = plugin.getEntityRendererManager();
        this.renderer = (EntityArmorStandRenderer) entityRendererManager.getRenderer(armorStand);
    }

    @Override
    public void reset() {
        super.reset();
        this.armorStand.remove();
        this.armorStand = null;
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);
        this.armorStand.teleport(location.clone().add(renderOffset));
    }

    protected ArmorStand createArmorStand(Location location) {
        World spigotWorld = gameWorld.getSpigotWorld();
        ArmorStand armorStand = (ArmorStand) spigotWorld.spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setInvulnerable(true);
        return armorStand;
    }

}
