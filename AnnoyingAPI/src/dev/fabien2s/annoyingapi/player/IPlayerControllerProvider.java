package dev.fabien2s.annoyingapi.player;

import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;

public interface IPlayerControllerProvider {

    IPlayerController provide(GamePlayer gamePlayer);

}
