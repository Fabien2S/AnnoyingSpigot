package dev.fabien2s.gannoyingapi.world.object;

import com.google.common.base.Preconditions;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.GameObject;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import lombok.Getter;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public abstract class EntityObject<TEntity extends Entity, TRenderer extends EntityRenderer<TEntity, TRenderer>> extends GameObject {

    @Getter private final Class<TEntity> entityClass;

    @Getter protected TEntity entity;
    @Getter protected TRenderer renderer;

    protected EntityObject(GameWorld world, String name, Location location, Class<TEntity> entityClass) {
        super(world, name, location);
        this.entityClass = entityClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void init() {
        super.init();

        Preconditions.checkArgument(location.isWorldLoaded(), "World is not loaded");

        World spigotWorld = gameWorld.getSpigotWorld();
        this.entity = spigotWorld.spawn(location, entityClass, this::onEntitySpawned);

        GamePlugin plugin = gameWorld.getPlugin();
        EntityRendererManager entityRendererManager = plugin.getEntityRendererManager();
        this.renderer = (TRenderer) entityRendererManager.getRenderer(entity);
    }

    @Override
    protected void reset() {
        super.reset();

        if (entity != null) {
            this.entity.remove();
            this.entity = null;
        }
    }

    protected abstract void onEntitySpawned(TEntity entity);

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        this.entity.getLocation(this.location);
        if (!entity.isValid())
            this.remove();
    }
}
