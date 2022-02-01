package dev.fabien2s.annoyingapi.interaction;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.Keybinds;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public enum InteractionTrigger implements Predicate<AnnoyingPlayer> {

    ATTACK(Keybinds.ATTACK),
    USE(Keybinds.USE, HumanEntity::isHandRaised),
    DROP(Keybinds.DROP),
    SWAP_ITEM("key.swapOffhand"),
    JUMP(Keybinds.JUMP),
    SNEAK(Keybinds.SNEAK, Player::isSneaking),
    SPRINT(Keybinds.SPRINT, Player::isSprinting);

    @Getter private final String key;
    @Getter private final BaseComponent component;

    private final Predicate<AnnoyingPlayer> interactionPredicate;

    InteractionTrigger(String key) {
        this(key, null);
    }

    InteractionTrigger(String key, Predicate<Player> interactionPredicate) {
        this.key = key;

        if (interactionPredicate != null)
            this.interactionPredicate = convert(interactionPredicate);
        else
            this.interactionPredicate = this::hasFire;

        this.component = new KeybindComponent(key);
        this.component.setColor(ChatColor.GRAY);
    }

    private Predicate<AnnoyingPlayer> convert(Predicate<Player> playerPredicate) {
        return gamePlayer -> {
            Player spigotPlayer = gamePlayer.getSpigotPlayer();
            return playerPredicate.test(spigotPlayer);
        };
    }

    private boolean hasFire(AnnoyingPlayer player) {
        InteractionManager interactionManager = player.getInteractionManager();
        return interactionManager.hasDispatched(this);
    }

    public boolean test(AnnoyingPlayer annoyingPlayer) {
        return interactionPredicate.test(annoyingPlayer);
    }

}
