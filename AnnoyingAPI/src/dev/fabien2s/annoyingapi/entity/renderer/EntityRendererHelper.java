package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityRendererHelper {

    public static final String TEAM_PREFIX = "scent";

    @Nullable
    public static EntityRenderer<?, ?> getRenderer(AnnoyingPlayer annoyingPlayer, int id) {
        EntityRendererManager rendererManager = annoyingPlayer.getEntityRendererManager();
        do {
            EntityRenderer<?, ?> entityRenderer = rendererManager.getRenderer(id);
            if (entityRenderer != null) {
                return entityRenderer;
            }
        } while ((rendererManager = rendererManager.getParent()) != null);

        return null;
    }

    public static String getTeamName(EntityRenderer<?, ?> entityRenderer) {
        return getTeamName(entityRenderer.entity);
    }

    public static String getTeamName(Entity entity) {
        int entityId = entity.getEntityId();
        return TEAM_PREFIX + entityId;
    }

}
