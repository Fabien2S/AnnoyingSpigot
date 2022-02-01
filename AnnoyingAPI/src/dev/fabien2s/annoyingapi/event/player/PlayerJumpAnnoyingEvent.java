package dev.fabien2s.annoyingapi.event.player;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerJumpAnnoyingEvent extends PlayerAnnoyingEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerJumpAnnoyingEvent(AnnoyingPlayer annoyingPlayer) {
        super(annoyingPlayer);
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
