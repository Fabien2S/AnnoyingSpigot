package dev.fabien2s.gannoyingapi.world.lod;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;

public interface ILodStateChanged
{
    void onChange(AnnoyingPlayer annoyingPlayer, double distance, int level);
}
