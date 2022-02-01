package dev.fabien2s.annoyingapi.event.player;

import dev.fabien2s.annoyingapi.event.AnnoyingEvent;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PlayerAnnoyingEvent extends AnnoyingEvent {

    @Getter private final AnnoyingPlayer annoyingPlayer;

}
