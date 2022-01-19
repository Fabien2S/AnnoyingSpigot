package dev.fabien2s.gannoyingapi.world.object;

import dev.fabien2s.annoyingapi.player.GamePlayer;

public interface IGameObjectRegistrable {

    void register(GamePlayer gamePlayer);

    void unregister(GamePlayer gamePlayer);

}
