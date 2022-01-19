package dev.fabien2s.annoyingapi.listener;

import dev.fabien2s.annoyingapi.event.player.world.entity.GamePlayerEntitySpawnEvent;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class PlayerRendererListener implements Listener {

    @EventHandler
    private void onEntitySpawned(GamePlayerEntitySpawnEvent event) {
        Entity entity = event.getEntity();
        int entityId = entity.getEntityId();

        GamePlayer gamePlayer = event.getGamePlayer();
        IPlayerController playerController = gamePlayer.getController();

        EntityRendererManager rendererManager = gamePlayer.getEntityRendererManager();
        do {
            EntityRenderer<?, ?> entityRenderer = rendererManager.getRenderer(entityId);
            if (entityRenderer != null)
                entityRenderer.forceUpdate(playerController);
        } while ((rendererManager = rendererManager.getParent()) != null);
    }

}
