package dev.fabien2s.annoyingapi.event.player.inventory;

import lombok.Getter;
import dev.fabien2s.annoyingapi.event.player.GamePlayerEvent;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class GamePlayerRenameItemEvent extends GamePlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter private final String input;

    public GamePlayerRenameItemEvent(GamePlayer gamePlayer, String input) {
        super(gamePlayer);
        this.input = input;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
