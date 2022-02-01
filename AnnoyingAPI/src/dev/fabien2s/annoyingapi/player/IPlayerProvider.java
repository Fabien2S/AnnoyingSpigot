package dev.fabien2s.annoyingapi.player;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import org.bukkit.entity.Player;

public interface IPlayerProvider<T extends AnnoyingPlugin> {

    AnnoyingPlayer provide(T plugin, Player player);

}
