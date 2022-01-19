package dev.fabien2s.annoyingapi.listener;

import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.debug.structure.StructureBuilderPlayer;
import dev.fabien2s.annoyingapi.event.player.GamePlayerJumpEvent;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionTrigger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerInteractionListener implements Listener {

    private final PlayerList playerList;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking())
            return;

        Player player = event.getPlayer();
        this.playerList.forPlayer(player, gamePlayer -> {
            InteractionManager interactionManager = gamePlayer.getInteractionManager();
            interactionManager.dispatch(InteractionTrigger.SNEAK);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerSprint(PlayerToggleSprintEvent event) {
        if (!event.isSprinting())
            return;

        Player player = event.getPlayer();
        this.playerList.forPlayer(player, gamePlayer -> {
            InteractionManager interactionManager = gamePlayer.getInteractionManager();
            interactionManager.dispatch(InteractionTrigger.SPRINT);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        this.playerList.forPlayer(player, gamePlayer -> {
            InteractionManager interactionManager = gamePlayer.getInteractionManager();
            interactionManager.dispatch(InteractionTrigger.USE);
        });

        this.playerList.forPlayer(player, StructureBuilderPlayer.class, builderPlayer -> {
            Entity entity = event.getRightClicked();
            EntityType type = entity.getType();
            if (type != EntityType.ARMOR_STAND)
                return;

            PlayerInventory inventory = player.getInventory();
            ItemStack itemStack = inventory.getItem(event.getHand());
            Material material = itemStack.getType();
            if (material != Material.NAME_TAG)
                return;

            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null)
                return;

            UUID entityUUID = entity.getUniqueId();
            String anchorName = entityUUID.toString();
            String anchorTag = itemMeta.getDisplayName();
            Location anchorLocation = entity.getLocation();
            builderPlayer.addAnchor(anchorName, anchorTag, anchorLocation);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        switch (action) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                this.playerList.forPlayer(player, gamePlayer -> {
                    InteractionManager interactionManager = gamePlayer.getInteractionManager();
                    interactionManager.dispatch(InteractionTrigger.ATTACK);
                });
                break;
            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                this.playerList.forPlayer(player, gamePlayer -> {
                    InteractionManager interactionManager = gamePlayer.getInteractionManager();
                    interactionManager.dispatch(InteractionTrigger.USE);
                });
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerAnimation(PlayerAnimationEvent event) {
        PlayerAnimationType animationType = event.getAnimationType();
        if (animationType == PlayerAnimationType.ARM_SWING) {
            Player player = event.getPlayer();
            this.playerList.forPlayer(player, gamePlayer -> {
                InteractionManager interactionManager = gamePlayer.getInteractionManager();
                interactionManager.dispatch(InteractionTrigger.ATTACK);
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        this.playerList.forPlayer(player, gamePlayer -> {
            InteractionManager interactionManager = gamePlayer.getInteractionManager();
            interactionManager.dispatch(InteractionTrigger.SWAP_ITEM);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        this.playerList.forPlayer(player, gamePlayer -> {
            InteractionManager interactionManager = gamePlayer.getInteractionManager();
            interactionManager.dispatch(InteractionTrigger.DROP);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJump(GamePlayerJumpEvent event) {
        GamePlayer gamePlayer = event.getGamePlayer();
        InteractionManager interactionManager = gamePlayer.getInteractionManager();
        interactionManager.dispatch(InteractionTrigger.JUMP);
    }


}
