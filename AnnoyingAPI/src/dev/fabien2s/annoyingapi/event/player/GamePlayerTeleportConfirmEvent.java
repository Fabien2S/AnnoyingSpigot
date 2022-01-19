package dev.fabien2s.annoyingapi.event.player;

import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class GamePlayerTeleportConfirmEvent extends GamePlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public GamePlayerTeleportConfirmEvent(GamePlayer gamePlayer) {
        super(gamePlayer);
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
