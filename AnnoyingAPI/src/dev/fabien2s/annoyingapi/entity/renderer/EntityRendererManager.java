package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class EntityRendererManager implements ITickable {

    private static final Logger LOGGER = LogManager.getLogger(EntityRendererManager.class);

    @Getter
    private final EntityRendererManager parent;
    private final Function<Entity, IEntityTracker> trackerBuilder;
    private final Map<Integer, EntityRenderer<?, ?>> entityRendererMap = new HashMap<>();

    @Override
    public void tick(double deltaTime) {
        Collection<EntityRenderer<?, ?>> entityRenderers = entityRendererMap.values();
        Iterator<EntityRenderer<?, ?>> entityRendererIterator = entityRenderers.iterator();
        while (entityRendererIterator.hasNext()) {
            EntityRenderer<?, ?> entityRenderer = entityRendererIterator.next();
            if (entityRenderer.isValid())
                entityRenderer.tick(deltaTime);
            else {
                LOGGER.info("Removing entity renderer of entity {}", entityRenderer.entity);
                this.reset(entityRenderer);
                entityRendererIterator.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends Entity, U extends EntityRenderer<T, U>> EntityRenderer<T, U> create(T entity, EntityRenderer<T, U> parent, IEntityController controller) {
        return (EntityRenderer<T, U>) switch (entity.getType()) {
            case ARMOR_STAND -> new EntityArmorStandRenderer((EntityArmorStandRenderer) parent, (ArmorStand) entity, controller);
            case COW -> new EntityCowRenderer((EntityCowRenderer) parent, (Cow) entity, controller);
            case PLAYER -> new EntityPlayerRenderer((EntityPlayerRenderer) parent, (Player) entity, controller);
            case STRAY -> new EntityStrayRenderer((EntityStrayRenderer) parent, (Stray) entity, controller);
            case VEX -> new EntityVexRenderer((EntityVexRenderer) parent, (Vex) entity, controller);
            case MINECART -> new EntityMinecartRenderer((EntityMinecartRenderer) parent, (Minecart) entity, controller);
            default -> new EntityDefaultRenderer((EntityDefaultRenderer) parent, entity, controller);
        };
    }

    protected <T extends Entity, U extends EntityRenderer<T, U>> EntityRenderer<T, U> register(EntityRenderer<T, U> parent, T entity, IEntityTracker tracker) {
        LOGGER.info("Creating entity renderer for entity {}", entity);

        IEntityController entityController = new EntityController(entity, tracker, parent != null ? parent.controller : null);

        EntityRenderer<T, U> entityRenderer = create(entity, parent, entityController);
        int entityId = entity.getEntityId();
        this.entityRendererMap.put(entityId, entityRenderer);
        return entityRenderer;
    }

    protected void reset(EntityRenderer<?, ?> renderer) {
    }

    public <T extends Entity, U extends EntityRenderer<T, U>> EntityRenderer<T, U> getRenderer(T entity) {
        IEntityTracker entityTracker = trackerBuilder.apply(entity);
        return getRenderer(entity, entityTracker);
    }

    public <T extends Entity, U extends EntityRenderer<T, U>> EntityRenderer<T, U> getRenderer(T entity, IEntityTracker tracker) {
        int entityId = entity.getEntityId();

        @SuppressWarnings("unchecked")
        EntityRenderer<T, U> entityRenderer = (EntityRenderer<T, U>) entityRendererMap.get(entityId);
        if (entityRenderer != null)
            return entityRenderer;

        EntityRenderer<T, U> parent = this.parent != null ? this.parent.getRenderer(entity, tracker) : null;
        return register(parent, entity, tracker);
    }

    public EntityRenderer<?, ?> getRenderer(int entityId) {
        return entityRendererMap.get(entityId);
    }

    public void removeAll() {
        LOGGER.info("Removing {} entity renderer(s)", entityRendererMap.size());
        this.entityRendererMap.forEach((entID, renderer) -> this.reset(renderer));
        this.entityRendererMap.clear();
    }

    public boolean isRoot() {
        return parent == null;
    }

}
