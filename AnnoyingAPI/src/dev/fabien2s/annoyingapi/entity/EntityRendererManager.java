package dev.fabien2s.annoyingapi.entity;

import dev.fabien2s.annoyingapi.entity.renderer.*;
import dev.fabien2s.annoyingapi.entity.renderer.living.EntityArmorStandRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.living.creature.monster.EntityStrayRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.living.creature.monster.EntityVexRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.living.human.EntityPlayerRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.living.creature.animal.EntityCowRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.vehicle.EntityMinecartRenderer;
import dev.fabien2s.annoyingapi.util.ITickable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EntityRendererManager implements ITickable {

    private static final Logger LOGGER = LogManager.getLogger(EntityRendererManager.class);

    @Nullable private final EntityRendererManager parent;

    @Nonnull private final Scoreboard scoreboard;

    private final Map<Integer, EntityRenderer<?, ?>> rendererMap;

    public EntityRendererManager(@Nonnull Scoreboard scoreboard) {
        this.parent = null;
        this.scoreboard = scoreboard;
        this.rendererMap = new HashMap<>();
    }

    public EntityRendererManager(EntityRendererManager parent) {
        this.parent = parent;
        this.scoreboard = parent.scoreboard;
        this.rendererMap = new HashMap<>();
    }

    @Override
    public void tick(double deltaTime) {
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity, U extends EntityRenderer<T, U>> EntityRenderer<T, U> create(T entity) {
        LOGGER.info("Creating entity renderer for entity {}", entity);

        EntityRenderer<T, U> parent = this.parent != null ? this.parent.getRenderer(entity) : null;

        EntityType entityType = entity.getType();
        EntityRenderer<T, U> entityRenderer = (EntityRenderer<T, U>) switch (entityType) {
            case ARMOR_STAND -> new EntityArmorStandRenderer((EntityArmorStandRenderer) parent, (ArmorStand) entity, controller);
            case COW -> new EntityCowRenderer((EntityCowRenderer) parent, (Cow) entity, controller);
            case PLAYER -> new EntityPlayerRenderer((EntityPlayerRenderer) parent, (Player) entity, controller);
            case STRAY -> new EntityStrayRenderer((EntityStrayRenderer) parent, (Stray) entity, controller);
            case VEX -> new EntityVexRenderer((EntityVexRenderer) parent, (Vex) entity, controller);
            case MINECART -> new EntityMinecartRenderer((EntityMinecartRenderer) parent, (Minecart) entity, controller);
            default -> new EntityDefaultRenderer((EntityDefaultRenderer) parent, entity, controller);
        };

        int entityId = entity.getEntityId();
        this.rendererMap.put(entityId, entityRenderer);
        return entityRenderer;
    }

    public <T extends Entity, U extends EntityRenderer<T, U>> EntityRenderer<T, U> getRenderer(T entity) {
        int entityId = entity.getEntityId();

        @SuppressWarnings("unchecked")
        EntityRenderer<T, U> entityRenderer = (EntityRenderer<T, U>) rendererMap.get(entityId);
        if (entityRenderer != null)
            return entityRenderer;

        return create(entity);
    }

}
