package dev.fabien2s.annoyingapi.adapter.packet;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.Nullable;

public interface IPacketHandler<T> {

    @Nullable
    T handle(AnnoyingPlayer player, BukkitScheduler scheduler, T packet) throws IllegalAccessException;

}
