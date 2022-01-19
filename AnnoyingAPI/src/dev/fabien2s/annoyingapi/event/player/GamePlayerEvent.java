package dev.fabien2s.annoyingapi.event.player;

import dev.fabien2s.annoyingapi.player.GamePlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;

@RequiredArgsConstructor
public abstract class GamePlayerEvent extends Event {

    @Getter private final GamePlayer gamePlayer;

}
