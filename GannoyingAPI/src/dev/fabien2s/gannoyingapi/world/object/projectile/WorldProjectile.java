package dev.fabien2s.gannoyingapi.world.object.projectile;

import lombok.Getter;
import dev.fabien2s.annoyingapi.entity.renderer.EntityArmorStandRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public abstract class WorldProjectile<T extends AnnoyingPlayer & IProjectileSource<U>, U extends IProjectileTarget> extends Projectile<T, U> {

    @Getter protected ArmorStand armorStand;
    @Getter protected EntityArmorStandRenderer renderer;

    protected final Vector renderOffset;

    public WorldProjectile(GameWorld world, String name, Location location) {
        this(world, name, location, VectorHelper.zero());
    }

    protected WorldProjectile(GameWorld world, String name, Location location, Vector renderOffset) {
        super(world, name, location);
        this.renderOffset = renderOffset.clone();
    }

    @Override
    public void init() {
        super.init();

        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("world is null");

        this.armorStand = (ArmorStand) world.spawnEntity(location.clone().add(renderOffset), EntityType.ARMOR_STAND);
        this.armorStand.setMarker(true);
        this.armorStand.setGravity(false);
        this.armorStand.setVisible(false);
        this.armorStand.setBasePlate(false);
        this.armorStand.setInvulnerable(true);

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

}
