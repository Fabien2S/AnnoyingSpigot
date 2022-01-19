package dev.fabien2s.annoyingapi.event.player.world.entity;

import lombok.Getter;
import dev.fabien2s.annoyingapi.event.player.GamePlayerEvent;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class GamePlayerEntityDestroyEvent extends GamePlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    @Nonnull
    private final Entity entity;

    public GamePlayerEntityDestroyEvent(GamePlayer gamePlayer, @NotNull Entity entity) {
        super(gamePlayer);
        this.entity = entity;
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
