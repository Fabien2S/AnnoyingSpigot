package dev.fabien2s.gannoyingapi.lobby.state;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.event.player.PlayerReadyAnnoyingEvent;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.player.IPlayerProvider;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.statemachine.IState;
import dev.fabien2s.annoyingapi.util.BossBarHelper;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.ingame.SpectatorPlayer;
import dev.fabien2s.gannoyingapi.lobby.LobbyPlayer;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import lombok.RequiredArgsConstructor;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

@RequiredArgsConstructor
public class LobbyInitializeGameState implements IState<GamePlugin>, Listener {

    private static final Logger LOGGER = LogManager.getLogger(LobbyInitializeGameState.class);

    private static final NamespacedKey KEY = GamePlugin.createKey("lobby_init_game");
    private static final double START_DELAY = 2;

    private static final PotionEffect POTION_EFFECT = new PotionEffect(
            PotionEffectType.BLINDNESS,
            Integer.MAX_VALUE, 0,
            false, false, false
    );

    private final GameWorld gameWorld;

    private KeyedBossBar bossBar;
    private Collection<LobbyPlayer> pendingPlayers;

    private double startTime;

    @Override
    public void onStateEnter(GamePlugin plugin, @Nullable IState<GamePlugin> previousState) {

        Server server = plugin.getServer();
        this.bossBar = server.createBossBar(KEY, "Preparing world...", BarColor.YELLOW, BarStyle.SOLID);

        this.pendingPlayers = new HashSet<>();
        this.startTime = Double.NaN;

        World spigotWorld = gameWorld.getSpigotWorld();
        Location spawnLocation = spigotWorld.getSpawnLocation();

        PlayerList playerList = plugin.getPlayerList();
        playerList.forPlayers(LobbyPlayer.class, lobbyPlayer -> {
            this.pendingPlayers.add(lobbyPlayer);

            Player spigotPlayer = lobbyPlayer.getSpigotPlayer();
            spigotPlayer.addPotionEffect(POTION_EFFECT);
            spigotPlayer.teleport(spawnLocation);
            this.bossBar.addPlayer(spigotPlayer);
        });

        playerList.forPlayers(SpectatorPlayer.class, spectatorPlayer -> {
            Player spigotPlayer = spectatorPlayer.getSpigotPlayer();
            spigotPlayer.teleport(spawnLocation);
            this.bossBar.addPlayer(spigotPlayer);
        });
    }

    @Override
    public void onStateUpdate(GamePlugin plugin, double deltaTime) {
        if (Double.isNaN(startTime))
            return;

        this.startTime -= deltaTime;
        if (this.startTime <= 0) {
            this.startTime = Double.NaN;
            plugin.enableGame();
        }
    }

    @Override
    public void onStateExit(GamePlugin plugin) {

        BossBarHelper.remove(bossBar);

        PlayerList playerList = plugin.getPlayerList();
        Collection<LobbyPlayer> players = playerList.getPlayers(LobbyPlayer.class);
        for (LobbyPlayer player : players) {

            Player spigotPlayer = player.getSpigotPlayer();
            spigotPlayer.removePotionEffect(PotionEffectType.BLINDNESS);

            NamespacedKey selectedRole = player.getSelectedRole();
            if (selectedRole == null)
                continue;

            IPlayerProvider<AnnoyingPlugin> playerProvider = plugin.getPlayerProvider(selectedRole);
            if (playerProvider == null)
                continue;

            playerList.setPlayer(spigotPlayer, playerProvider);
        }

    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        World spigotWorld = gameWorld.getSpigotWorld();
        if (spigotWorld.equals(world))
            event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerTeleportConfirm(PlayerReadyAnnoyingEvent event) {
        AnnoyingPlayer annoyingPlayer = event.getAnnoyingPlayer();
        Player spigotPlayer = annoyingPlayer.getSpigotPlayer();
        World playerWorld = spigotPlayer.getWorld();

        World spigotWorld = gameWorld.getSpigotWorld();
        if (spigotWorld.equals(playerWorld) && annoyingPlayer instanceof LobbyPlayer)
            this.validatePlayer((LobbyPlayer) annoyingPlayer);
    }

    private void validatePlayer(LobbyPlayer gamePlayer) {
        if (!this.pendingPlayers.remove(gamePlayer))
            return;

        LOGGER.debug("{} is ready to spawn", gamePlayer);
        if (pendingPlayers.isEmpty())
            this.startTime = START_DELAY;
    }

}
