package dev.fabien2s.gannoyingapi.world.lod;

import dev.fabien2s.annoyingapi.player.GamePlayer;

public interface ILodStateChanged
{
    void onChange(GamePlayer gamePlayer, double distance, int level);
}
