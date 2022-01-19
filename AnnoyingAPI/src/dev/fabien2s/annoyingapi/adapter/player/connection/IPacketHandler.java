package dev.fabien2s.annoyingapi.adapter.player.connection;

import dev.fabien2s.annoyingapi.player.GamePlayer;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public interface IPacketHandler<T> {

    @Nullable
    T handle(GamePlayer gamePlayer, Executor executor, T packet) throws IllegalAccessException;

}
