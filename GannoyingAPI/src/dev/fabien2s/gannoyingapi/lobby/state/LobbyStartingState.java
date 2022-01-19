package dev.fabien2s.gannoyingapi.lobby.state;

import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.lobby.LobbyPlayer;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.statemachine.IState;
import dev.fabien2s.annoyingapi.util.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.Map;

@RequiredArgsConstructor
public class LobbyStartingState implements IState<GamePlugin>, Listener {

    private static final Logger LOGGER = LogManager.getLogger(LobbyStartingState.class);

    private static final double STATE_DURATION = 2.5;
    private static final PotionEffect POTION_EFFECT = new PotionEffect(
            PotionEffectType.CONFUSION,
            (int) (STATE_DURATION * Minecraft.TICK_PER_SECOND), 0,
            false, false, false
    );

    private final Map<Player, LobbyPlayer> lobbyPlayerMap;

    private GamePlugin plugin;
    private double timeLeft;

    @Override
    public void onStateEnter(GamePlugin plugin, @Nullable IState<GamePlugin> previousState) {
        this.plugin = plugin;
        this.timeLeft = STATE_DURATION;

        this.lobbyPlayerMap.forEach((player, lobbyPlayer) -> {
            IPlayerController controller = lobbyPlayer.getController();
            controller.playSound(player, Sound.BLOCK_PORTAL_TRIGGER, SoundCategory.MASTER);

            player.addPotionEffect(POTION_EFFECT);
        });
    }

    @Override
    public void onStateUpdate(GamePlugin plugin, double deltaTime) {
        this.timeLeft -= deltaTime;
        if (this.timeLeft <= 0)
            plugin.startGame();
    }

    @Override
    public void onStateExit(GamePlugin plugin) {
        this.lobbyPlayerMap.forEach((player, lobbyPlayer) -> player.removePotionEffect(PotionEffectType.CONFUSION));
        this.lobbyPlayerMap.clear();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (lobbyPlayerMap.containsKey(player)) {
            LOGGER.info("Game start aborted ({} left the game)", player.getName());
            this.plugin.stopGame();
        }
    }

}
