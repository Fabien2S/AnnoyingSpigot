package dev.fabien2s.annoyingapi.player;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface IGamePlayerProvider<T extends AnnoyingPlugin> {

    GamePlayer provide(T plugin, Player player, IPlayerControllerProvider controllerProvider);

}
