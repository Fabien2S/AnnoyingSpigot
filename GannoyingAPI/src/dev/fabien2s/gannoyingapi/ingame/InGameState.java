package dev.fabien2s.gannoyingapi.ingame;

import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.statemachine.IState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public abstract class InGameState implements IState<GamePlugin>, Listener {

    protected final GameWorld world;

    @Override
    public final void onStateEnter(GamePlugin plugin, IState<GamePlugin> previousState) {
        this.onStateEnter(plugin, world);
    }

    @Override
    public final void onStateUpdate(GamePlugin plugin, double deltaTime) {
        this.onStateUpdate(plugin, world, deltaTime);
    }

    @Override
    public final void onStateExit(GamePlugin plugin) {
        this.onStateExit(plugin, world);
    }

    protected abstract void onStateEnter(GamePlugin plugin, GameWorld world);

    protected abstract void onStateUpdate(GamePlugin plugin, GameWorld world, double deltaTime);

    protected abstract void onStateExit(GamePlugin plugin, GameWorld world);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GamePlugin plugin = world.getPlugin();
        PlayerList playerList = plugin.getPlayerList();
        playerList.setPlayer(player, SpectatorPlayer::new);
    }

}
