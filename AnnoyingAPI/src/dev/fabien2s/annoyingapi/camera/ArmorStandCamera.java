package dev.fabien2s.annoyingapi.camera;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArmorStandCamera extends PlayerCamera {

    private ArmorStand camera;

    public ArmorStandCamera(AnnoyingPlayer player) {
        super(player);
    }

    @Override
    public void init(Location location) {
        super.init(location);

        Player spigotPlayer = player.getSpigotPlayer();
        World world = spigotPlayer.getWorld();
        this.camera = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        this.camera.setGravity(false);
        this.camera.setVisible(false);
        this.camera.setBasePlate(false);
        this.camera.setInvulnerable(true);

        spigotPlayer.setGameMode(GameMode.SPECTATOR);
        spigotPlayer.setSpectatorTarget(camera);
    }

    @Override
    public void reset(Location location) {
        super.reset(location);

        this.camera.remove();
    }

    @Override
    public boolean isSpectating() {
        return camera.equals(player
                .getSpigotPlayer()
                .getSpectatorTarget()
        );
    }

    @Override
    public void emitSound(@NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
    }

    @Override
    public void emitSound(@NotNull @Nonnull NamespacedKey sound, @Nonnull SoundCategory category, float volume, float pitch) {
    }

    @Override
    public void stopEmittingSound(Sound sound, SoundCategory category) {
    }

    @Override
    public void stopEmittingSound(@Nonnull NamespacedKey sound, @Nullable SoundCategory category) {
    }

    @Override
    public World getWorld() {
        return camera.getWorld();
    }

    @Override
    public Location getLocation() {
        return camera.getLocation();
    }

    @Override
    public void setLocation(Location location) {
        this.camera.teleport(location);
    }

}
