package dev.fabien2s.annoyingapi.player;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.RequiredArgsConstructor;
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
public class PlayerList implements ITickable, Listener, Iterable<AnnoyingPlayer> {

    private static final Logger LOGGER = LogManager.getLogger(PlayerList.class);

    private final AnnoyingPlugin plugin;
    private final Map<Player, AnnoyingPlayer> players = new HashMap<>();

    @Override
    public void tick(double deltaTime) {
        for (AnnoyingPlayer player : players.values())
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
        forPlayer(player, p -> p.setIdleTime(0));
    }

    public AnnoyingPlayer setPlayer(Player player, IPlayerProvider<AnnoyingPlugin> playerProvider) {
        AnnoyingPlayer annoyingPlayer = playerProvider.provide(plugin, player);
        this.setPlayer(player, annoyingPlayer);
        return annoyingPlayer;
    }

    private void setPlayer(Player player, AnnoyingPlayer annoyingPlayer) {
        resetPlayer(player);

        LOGGER.info("Game player updated {}", annoyingPlayer);
        this.players.put(player, annoyingPlayer);
        annoyingPlayer.init();
    }

    public void resetPlayer(Player player) {
        AnnoyingPlayer annoyingPlayer = players.remove(player);
        if (annoyingPlayer == null)
            return;

        LOGGER.info("Resetting player {}", annoyingPlayer);
        annoyingPlayer.reset();
    }

    public void resetAll() {
        LOGGER.info("Resetting {} player(s)", players.size());

        this.players.forEach((player, gamePlayer) -> gamePlayer.reset());
        this.players.clear();
    }

    public int count() {
        return players.size();
    }

    public int count(Class<?> clazz) {
        int count = 0;
        Collection<AnnoyingPlayer> annoyingPlayers = players.values();
        for (AnnoyingPlayer annoyingPlayer : annoyingPlayers) {
            if (clazz.isInstance(annoyingPlayer))
                count++;
        }
        return count;
    }

    public <T> void forPlayers(Class<T> clazz, Consumer<T> action) {
        Collection<AnnoyingPlayer> annoyingPlayers = players.values();
        for (AnnoyingPlayer annoyingPlayer : annoyingPlayers) {
            if (!clazz.isInstance(annoyingPlayer))
                continue;

            T tPlayer = clazz.cast(annoyingPlayer);
            action.accept(tPlayer);
        }
    }

    public void forPlayer(Player player, Consumer<AnnoyingPlayer> action) {
        AnnoyingPlayer annoyingPlayer = players.get(player);
        if (annoyingPlayer == null) {
            LOGGER.debug("Skipping action for {} (No game player found)", player);
            return;
        }

        action.accept(annoyingPlayer);
    }

    public <T> void forPlayer(Player player, Class<T> clazz, Consumer<T> action) {
        AnnoyingPlayer annoyingPlayer = players.get(player);
        if (annoyingPlayer == null) {
            LOGGER.debug("Skipping action for {} (No game player found)", player);
            return;
        }

        if (!clazz.isInstance(annoyingPlayer)) {
            LOGGER.debug("Skipping action for {} (No game player of type {} found)", player, clazz);
            return;
        }

        T tPlayer = clazz.cast(annoyingPlayer);
        action.accept(tPlayer);
    }

    @Nullable
    public AnnoyingPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public AnnoyingPlayer requirePlayer(Player player) {
        AnnoyingPlayer annoyingPlayer = players.get(player);
        if (annoyingPlayer == null)
            throw new IllegalStateException("No GamePlayer are associated with " + player);
        return annoyingPlayer;
    }

    @Nullable
    public <T> T getPlayer(Player player, Class<T> clazz) {
        AnnoyingPlayer annoyingPlayer = players.get(player);
        if (annoyingPlayer == null) {
            LOGGER.debug("Skipping action for {} (No game player found)", player);
            return null;
        }

        if (!clazz.isInstance(annoyingPlayer)) {
            LOGGER.debug("Skipping action for {} (No game player of type {} found)", player, clazz);
            return null;
        }

        return clazz.cast(annoyingPlayer);
    }

    public <T> T requirePlayer(Player player, Class<T> clazz) {
        T gamePlayer = getPlayer(player, clazz);
        if (gamePlayer == null)
            throw new IllegalStateException("No GamePlayer of type " + clazz + " are associated with " + player);
        return gamePlayer;
    }

    public <T> T sortPlayer(Class<T> clazz, Comparator<T> comparator) {
        T current = null;
        for (AnnoyingPlayer annoyingPlayer : players.values()) {
            if (clazz.isInstance(annoyingPlayer)) {
                T player = clazz.cast(annoyingPlayer);
                if (current == null || comparator.compare(player, current) > 0)
                    current = player;
            }
        }
        return current;
    }

    public <T> Collection<T> getPlayers(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (AnnoyingPlayer annoyingPlayer : players.values()) {
            if (clazz.isInstance(annoyingPlayer))
                list.add(clazz.cast(annoyingPlayer));
        }
        return list;
    }

    public <T> Collection<T> getPlayers(Class<T> clazz, Predicate<T> playerPredicate) {
        List<T> list = new ArrayList<>();
        for (AnnoyingPlayer annoyingPlayer : players.values()) {

            if (!clazz.isInstance(annoyingPlayer))
                continue;

            T t = clazz.cast(annoyingPlayer);
            if (!playerPredicate.test(t))
                continue;

            list.add(t);
        }
        return list;
    }

    @Nonnull
    @Override
    public Iterator<AnnoyingPlayer> iterator() {
        Collection<AnnoyingPlayer> annoyingPlayers = players.values();
        return annoyingPlayers.iterator();
    }

}