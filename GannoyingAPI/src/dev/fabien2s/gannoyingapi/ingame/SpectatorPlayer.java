package dev.fabien2s.gannoyingapi.ingame;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.gannoyingapi.GamePlugin;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class SpectatorPlayer extends AnnoyingPlayer {

    public static final NamespacedKey ROLE_NAME = GamePlugin.createKey("spectator_player");

    private GameMode originalGameMode;

    public SpectatorPlayer(AnnoyingPlugin plugin, Player spigotPlayer) {
        super(plugin, ROLE_NAME, spigotPlayer);
    }

    @Override
    public void init() {
        super.init();

        this.originalGameMode = spigotPlayer.getGameMode();
        this.spigotPlayer.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void reset() {
        super.reset();

        this.spigotPlayer.setGameMode(originalGameMode);
    }

    @Override
    public GamePlugin getPlugin() {
        return (GamePlugin) super.getPlugin();
    }

}
