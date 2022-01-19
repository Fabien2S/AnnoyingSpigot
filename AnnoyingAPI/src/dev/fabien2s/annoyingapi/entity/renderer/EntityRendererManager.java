package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.adapter.GameAdapters;
import dev.fabien2s.annoyingapi.adapter.IGameAdapter;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.adapter.entity.IEntityAdapter;
import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
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

    @Getter private final EntityRendererManager parent;
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

    protected EntityRenderer<?, ?> create(Entity entity, EntityRenderer<?, ?> parent, IEntityController controller) {
        switch (entity.getType()) {
            case ARMOR_STAND:
                return new EntityArmorStandRenderer((EntityArmorStandRenderer) parent, (ArmorStand) entity, controller);
            case COW:
                return new EntityCowRenderer((EntityCowRenderer) parent, (Cow) entity, controller);
            case PLAYER:
                return new EntityPlayerRenderer((EntityPlayerRenderer) parent, (Player) entity, controller);
            case STRAY:
                return new EntityStrayRenderer((EntityStrayRenderer) parent, (Stray) entity, controller);
            case VEX:
                return new EntityVexRenderer((EntityVexRenderer) parent, (Vex) entity, controller);
            case MINECART:
                return new EntityMinecartRenderer((EntityMinecartRenderer) parent, (Minecart) entity, controller);
            default:
                return new EntityDefaultRenderer((EntityDefaultRenderer) parent, entity, controller);
        }
    }

    protected EntityRenderer<?, ?> register(EntityRenderer<?, ?> parent, Entity entity, IEntityTracker tracker) {
        LOGGER.info("Creating entity renderer for entity {}", entity);

        IGameAdapter gameAdapter = GameAdapters.INSTANCE;
        IEntityAdapter entityAdapter = gameAdapter.getEntityAdapter();
        IEntityController entityController = entityAdapter.createController(entity, tracker, parent != null ? parent.controller : null);

        EntityRenderer<?, ?> entityRenderer = create(entity, parent, entityController);
        int entityId = entity.getEntityId();
        this.entityRendererMap.put(entityId, entityRenderer);
        return entityRenderer;
    }

    protected void reset(EntityRenderer<?, ?> renderer) {
    }

    public EntityRenderer<?, ?> getRenderer(Entity entity) {
        IEntityTracker entityTracker = trackerBuilder.apply(entity);
        return getRenderer(entity, entityTracker);
    }

    public EntityRenderer<?, ?> getRenderer(Entity entity, IEntityTracker tracker) {
        int entityId = entity.getEntityId();
        EntityRenderer<?, ?> entityRenderer = entityRendererMap.get(entityId);
        if (entityRenderer != null)
            return entityRenderer;

        EntityRenderer<?, ?> parent = this.parent != null ? this.parent.getRenderer(entity, tracker) : null;
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
