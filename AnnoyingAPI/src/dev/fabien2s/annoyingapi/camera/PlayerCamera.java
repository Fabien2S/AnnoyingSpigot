package dev.fabien2s.annoyingapi.camera;

import dev.fabien2s.annoyingapi.sound.ISoundEmitter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public abstract class PlayerCamera implements ISoundEmitter {

    protected final GamePlayer player;

    private boolean initialized;
    @Setter protected GameMode gameMode;

    public void init() {
        Player spigotPlayer = player.getSpigotPlayer();
        Location playerLocation = spigotPlayer.getLocation();
        this.init(playerLocation);
    }

    public void init(Location location) {
        if (initialized)
            throw new IllegalStateException("Camera already initialized");

        this.initialized = true;

        Player spigotPlayer = player.getSpigotPlayer();
        this.gameMode = spigotPlayer.getGameMode();
    }

    public void reset() {
        Player spigotPlayer = player.getSpigotPlayer();
        Location playerLocation = spigotPlayer.getLocation();
        this.reset(playerLocation);
    }

    public void reset(Location location) {
        if (!initialized)
            throw new IllegalStateException("Camera not initialized");

        this.initialized = false;
        if (isSpectating()) {
            Player spigotPlayer = player.getSpigotPlayer();
            spigotPlayer.setGameMode(gameMode);
            spigotPlayer.teleport(location);
        }
    }

    public abstract boolean isSpectating();

    public abstract World getWorld();

    public abstract Location getLocation();

    public abstract void setLocation(Location location);

}
