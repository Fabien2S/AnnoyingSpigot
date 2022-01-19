package dev.fabien2s.gannoyingapi.lobby.state;

import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.ingame.SpectatorPlayer;
import dev.fabien2s.gannoyingapi.lobby.LobbyPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.statemachine.IState;
import dev.fabien2s.annoyingapi.util.BossBarHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class LobbyWaitingRoomState implements IState<GamePlugin>, Listener {

    private static final Logger LOGGER = LogManager.getLogger(LobbyWaitingRoomState.class);

    private static final NamespacedKey KEY = GamePlugin.createKey("lobby_waiting");

    @Getter private final World world;
    @Getter private final int minimumPlayer;
    @Getter private final int maximumPlayer;

    private final Map<Player, LobbyPlayer> lobbyPlayerMap = new HashMap<>();

    private GamePlugin plugin;
    private KeyedBossBar bossBar;

    private int connectedPlayers;

    @Override
    public void onStateEnter(GamePlugin plugin, IState<GamePlugin> previousState) {
        this.plugin = plugin;

        Server server = plugin.getServer();
        this.bossBar = server.createBossBar(KEY, "Waiting for players", BarColor.YELLOW, BarStyle.SOLID);

        this.connectedPlayers = 0;

        Collection<? extends Player> onlinePlayers = server.getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers)
            this.initPlayer(onlinePlayer);
        this.onPlayerCountUpdated();
    }

    @Override
    public void onStateUpdate(GamePlugin plugin, double deltaTime) {
    }

    @Override
    public void onStateExit(GamePlugin plugin) {
        BossBarHelper.remove(bossBar);
    }

    private void initPlayer(Player player) {
        PlayerList playerList = plugin.getPlayerList();
        if (connectedPlayers >= maximumPlayer) {
            LOGGER.info("Player {} joined but the maximum number of players ({}) has been reached ({})", player.getName(), maximumPlayer, connectedPlayers);

            playerList.setPlayer(player, SpectatorPlayer::new);

        } else {
            this.connectedPlayers++;

            LOGGER.info("Player {} joined ({}/{})", player.getName(), connectedPlayers, maximumPlayer);

            GamePlayer lobbyPlayer = playerList.setPlayer(player, LobbyPlayer::new);
            this.lobbyPlayerMap.put(player, (LobbyPlayer) lobbyPlayer);
        }

        Location spawnLocation = world.getSpawnLocation();
        player.teleport(spawnLocation);

        this.bossBar.addPlayer(player);
    }

    private void onPlayerCountUpdated() {
        if (connectedPlayers == maximumPlayer)
            this.startGame();
        else
            this.bossBar.setProgress((double) connectedPlayers / maximumPlayer);
    }

    private void startGame() {
        LobbyStartingState lobbyStartingState = new LobbyStartingState(lobbyPlayerMap);
        this.plugin.setState(lobbyStartingState);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.initPlayer(player);
        this.onPlayerCountUpdated();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        this.lobbyPlayerMap.remove(player);

        this.connectedPlayers--;
        this.onPlayerCountUpdated();
    }

}
