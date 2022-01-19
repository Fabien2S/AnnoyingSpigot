package dev.fabien2s.annoyingapi.event.player.world;

import lombok.Getter;
import dev.fabien2s.annoyingapi.event.player.GamePlayerEvent;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class GamePlayerLoadChunkEvent extends GamePlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter private final int x;
    @Getter private final int z;

    public GamePlayerLoadChunkEvent(GamePlayer gamePlayer, int x, int z) {
        super(gamePlayer);

        this.x = x;
        this.z = z;
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
