package dev.fabien2s.gannoyingapi.world.object;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;

public interface IGameObjectRegistrable {

    void register(AnnoyingPlayer annoyingPlayer);

    void unregister(AnnoyingPlayer annoyingPlayer);

}
