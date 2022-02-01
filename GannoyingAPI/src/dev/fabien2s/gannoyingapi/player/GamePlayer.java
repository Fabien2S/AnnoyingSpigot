package dev.fabien2s.gannoyingapi.player;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import dev.fabien2s.gannoyingapi.world.object.IGameObjectRegistrable;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class GamePlayer extends AnnoyingPlayer {

    protected GamePlayer(AnnoyingPlugin plugin, NamespacedKey roleName, Player spigotPlayer) {
        super(plugin, roleName, spigotPlayer);
    }

    @Override
    public void init() {
        super.init();

        GamePlugin plugin = getPlugin();
        GameWorld gameWorld = plugin.getGameWorld();
        if (gameWorld != null)
            gameWorld.forGameObjects(IGameObjectRegistrable.class, registrable -> registrable.register(this));
    }

    @Override
    public void reset() {
        super.reset();

        GamePlugin plugin = getPlugin();
        GameWorld gameWorld = plugin.getGameWorld();
        if (gameWorld != null)
            gameWorld.forGameObjects(IGameObjectRegistrable.class, registrable -> registrable.unregister(this));
    }

    @Override
    public GamePlugin getPlugin() {
        return (GamePlugin) super.getPlugin();
    }

}
