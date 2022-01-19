package dev.fabien2s.annoyingapi.player;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.adapter.GameAdapters;
import dev.fabien2s.annoyingapi.adapter.IGameAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class PlayerList implements ITickable, Listener, Iterable<GamePlayer> {

    private static final Logger LOGGER = LogManager.getLogger(PlayerList.class);

    private final AnnoyingPlugin plugin;
    private final Map<Player, GamePlayer> players = new HashMap<>();

    @Override
    public void tick(double deltaTime) {
        for (GamePlayer player : players.values())
            player.tick(deltaTime);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        this.resetPlayer(player);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        forPlayer(player, gamePlayer -> gamePlayer.setIdleTime(0));
    }

    public GamePlayer setPlayer(Player player, IGamePlayerProvider<AnnoyingPlugin> playerProvider) {
        IGameAdapter gameAdapter = GameAdapters.INSTANCE;
        GamePlayer gamePlayer = playerProvider.provide(plugin, player, gameAdapter::createController);
        this.setPlayer(player, gamePlayer);
        return gamePlayer;
    }

    private void setPlayer(Player player, GamePlayer gamePlayer) {
        resetPlayer(player);

        LOGGER.info("Game player updated {}", gamePlayer);
        this.players.put(player, gamePlayer);
        gamePlayer.init();
    }

    public void resetPlayer(Player player) {
        GamePlayer gamePlayer = players.remove(player);
        if (gamePlayer == null)
            return;

        LOGGER.info("Resetting player {}", gamePlayer);
        gamePlayer.reset();
    }

    public void resetAll() {
        LOGGER.info("Resetting {} player(s)", players.size());

        this.players.forEach((player, gamePlayer) -> gamePlayer.reset());
        this.players.clear();
    }

    public int count(Class<?> clazz) {
        int count = 0;
        Collection<GamePlayer> gamePlayers = players.values();
        for (GamePlayer gamePlayer : gamePlayers) {
            if (clazz.isInstance(gamePlayer))
                count++;
        }
        return count;
    }

    public <T> void forPlayers(Class<T> clazz, Consumer<T> action) {
        Collection<GamePlayer> gamePlayers = players.values();
        for (GamePlayer gamePlayer : gamePlayers) {
            if (!clazz.isInstance(gamePlayer))
                continue;

            T tPlayer = clazz.cast(gamePlayer);
            action.accept(tPlayer);
        }
    }

    public void forPlayer(Player player, Consumer<GamePlayer> action) {
        GamePlayer gamePlayer = players.get(player);
        if (gamePlayer == null) {
            LOGGER.debug("Skipping action for {} (No game player found)", player);
            return;
        }

        action.accept(gamePlayer);
    }

    public <T> void forPlayer(Player player, Class<T> clazz, Consumer<T> action) {
        GamePlayer gamePlayer = players.get(player);
        if (gamePlayer == null) {
            LOGGER.debug("Skipping action for {} (No game player found)", player);
            return;
        }

        if (!clazz.isInstance(gamePlayer)) {
            LOGGER.debug("Skipping action for {} (No game player of type {} found)", player, clazz);
            return;
        }

        T tPlayer = clazz.cast(gamePlayer);
        action.accept(tPlayer);
    }

    @Nullable
    public GamePlayer getPlayer(Player player) {
        return players.get(player);
    }

    public GamePlayer requirePlayer(Player player) {
        GamePlayer gamePlayer = players.get(player);
        if (gamePlayer == null)
            throw new IllegalStateException("No GamePlayer are associated with " + player);
        return gamePlayer;
    }

    @Nullable
    public <T> T getPlayer(Player player, Class<T> clazz) {
        GamePlayer gamePlayer = players.get(player);
        if (gamePlayer == null) {
            LOGGER.debug("Skipping action for {} (No game player found)", player);
            return null;
        }

        if (!clazz.isInstance(gamePlayer)) {
            LOGGER.debug("Skipping action for {} (No game player of type {} found)", player, clazz);
            return null;
        }

        return clazz.cast(gamePlayer);
    }

    public <T> T requirePlayer(Player player, Class<T> clazz) {
        T gamePlayer = getPlayer(player, clazz);
        if (gamePlayer == null)
            throw new IllegalStateException("No GamePlayer of type " + clazz + " are associated with " + player);
        return gamePlayer;
    }

    public <T> T sortPlayer(Class<T> clazz, Comparator<T> comparator) {
        T current = null;
        for (GamePlayer gamePlayer : players.values()) {
            if (clazz.isInstance(gamePlayer)) {
                T player = clazz.cast(gamePlayer);
                if (current == null || comparator.compare(player, current) > 0)
                    current = player;
            }
        }
        return current;
    }

    public <T> Collection<T> getPlayers(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (GamePlayer gamePlayer : players.values()) {
            if (clazz.isInstance(gamePlayer))
                list.add(clazz.cast(gamePlayer));
        }
        return list;
    }

    public <T> Collection<T> getPlayers(Class<T> clazz, Predicate<T> playerPredicate) {
        List<T> list = new ArrayList<>();
        for (GamePlayer gamePlayer : players.values()) {

            if (!clazz.isInstance(gamePlayer))
                continue;

            T t = clazz.cast(gamePlayer);
            if (!playerPredicate.test(t))
                continue;

            list.add(t);
        }
        return list;
    }

    @Nonnull
    @Override
    public Iterator<GamePlayer> iterator() {
        Collection<GamePlayer> gamePlayers = players.values();
        return gamePlayers.iterator();
    }

}