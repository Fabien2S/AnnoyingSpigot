package dev.fabien2s.gannoyingapi.lobby;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.IPlayerControllerProvider;
import dev.fabien2s.gannoyingapi.GamePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.Set;

public class LobbyPlayer extends GamePlayer {

    public static final NamespacedKey ROLE_NAME = GamePlugin.createKey("lobby_player");

    @Getter
    @Setter
    private NamespacedKey selectedRole;

    public LobbyPlayer(AnnoyingPlugin plugin, Player spigotPlayer, IPlayerControllerProvider controllerProvider) {
        super(plugin, ROLE_NAME, spigotPlayer, controllerProvider);
    }

    @Override
    public void init() {
        super.init();

        Set<NamespacedKey> playerRoles = getPlugin().getPlayerRoles();
        for (NamespacedKey playerRole : playerRoles) {
            this.selectedRole = playerRole;
            break;
        }
    }

    @Override
    public GamePlugin getPlugin() {
        return (GamePlugin) super.getPlugin();
    }

}
