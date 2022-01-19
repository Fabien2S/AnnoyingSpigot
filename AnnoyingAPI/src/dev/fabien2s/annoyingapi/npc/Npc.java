package dev.fabien2s.annoyingapi.npc;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import lombok.Getter;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.entity.renderer.EntityPlayerRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.sound.ISoundEmitter;
import dev.fabien2s.annoyingapi.util.ITickable;
import dev.fabien2s.annoyingapi.math.IUnsafeEntityLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Npc implements ITickable, IUnsafeEntityLocation, ISoundEmitter, IEntityTracker {

    private static final int BLOCK_LIMIT = 8;
    private static final int BLOCK_LIMIT_SQR = BLOCK_LIMIT * BLOCK_LIMIT;
    private static final int DELTA_SCALE = 4096;

    private final AnnoyingPlugin plugin;
    private final Player player;
    private final INpcTracker tracker;
    @Getter private final EntityPlayerRenderer renderer;

    private final Set<GamePlayer> trackedPlayers = new HashSet<>();

    @Getter private boolean removed;

    private Location location;
    private Location lastLocation;

    private boolean moved;

    public Npc(NpcManager npcManager, Player player, Location location, Function<Npc, INpcTracker> trackerFunction) {
        this.player = player;
        this.location = location;

        this.tracker = trackerFunction.apply(this);

        this.plugin = npcManager.getPlugin();
        EntityRendererManager entityRendererManager = plugin.getEntityRendererManager();
        this.renderer = (EntityPlayerRenderer) entityRendererManager.getRenderer(player, this);

        this.lastLocation = location;
    }

    @Override
    public void tick(double deltaTime) {

        if (moved) {
            this.moved = false;

            double deltaLength = location.distanceSquared(lastLocation);
            if (deltaLength > BLOCK_LIMIT_SQR)
                this.tracker.teleport(location);
            else {
                short deltaX = (short) ((location.getX() - lastLocation.getX()) * DELTA_SCALE);
                short deltaY = (short) ((location.getY() - lastLocation.getY()) * DELTA_SCALE);
                short deltaZ = (short) ((location.getZ() - lastLocation.getZ()) * DELTA_SCALE);
                this.tracker.move(location, deltaX, deltaY, deltaZ, location.getYaw(), location.getPitch(), true, true);
            }
        }

    }

    @Override
    public Location getUnsafeLocation() {
        return location;
    }

    @Override
    public Location getUnsafeEyeLocation() {
        return location;
    }

    public void spawn(GamePlayer gamePlayer) {
        if (trackedPlayers.add(gamePlayer)) {
            IPlayerController controller = gamePlayer.getController();
            this.tracker.spawn(controller);

            float yaw = location.getYaw();
            this.tracker.setHeadRotation(yaw);
            this.tracker.teleport(location);
        }
    }

    public void remove(GamePlayer gamePlayer) {
        if (trackedPlayers.remove(gamePlayer)) {
            IPlayerController controller = gamePlayer.getController();
            this.tracker.remove(controller);
        }
    }

    public void remove() {
        this.player.remove();
        for (GamePlayer gamePlayer : trackedPlayers) {
            IPlayerController controller = gamePlayer.getController();
            this.tracker.remove(controller);
        }
        this.trackedPlayers.clear();
        this.removed = true;
    }

    @Override
    public void emitSound(@NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        for (GamePlayer gamePlayer : trackedPlayers) {
            IPlayerController controller = gamePlayer.getController();
            controller.playSound(player, sound, category, volume, pitch);
        }
    }

    @Override
    public void emitSound(@NotNull @Nonnull NamespacedKey sound, @Nonnull SoundCategory category, float volume, float pitch) {
        World world = player.getWorld();
        world.playSound(location, sound.toString(), category, volume, pitch);
    }

    @Override
    public void stopEmittingSound(Sound sound, SoundCategory category) {
        for (GamePlayer gamePlayer : trackedPlayers) {
            Player spigotPlayer = gamePlayer.getSpigotPlayer();
            spigotPlayer.stopSound(sound, category);
        }
    }

    @Override
    public void stopEmittingSound(@Nonnull NamespacedKey sound, @Nullable SoundCategory category) {
        String mcSound = sound.toString();
        for (GamePlayer gamePlayer : trackedPlayers) {
            Player spigotPlayer = gamePlayer.getSpigotPlayer();
            spigotPlayer.stopSound(mcSound, category);
        }
    }

    @Override
    public boolean isTracked(Player player) {
        for (GamePlayer trackedPlayer : trackedPlayers) {
            Player spigotPlayer = trackedPlayer.getSpigotPlayer();
            if (spigotPlayer.equals(player))
                return true;
        }
        return false;
    }

    @Override
    public void sendPacket(Object packet, boolean includeSelf) {
        for (GamePlayer gamePlayer : trackedPlayers) {
            IPlayerController controller = gamePlayer.getController();
            controller.sendPacket(packet, includeSelf);
        }
    }

    @Override
    public void broadcastPacket(Object packet, boolean includeSelf) {
        PlayerList playerList = plugin.getPlayerList();
        playerList.forEach(gamePlayer -> {
            IPlayerController playerController = gamePlayer.getController();
            playerController.sendPacket(packet, includeSelf);
        });
    }

    public EntityPlayerRenderer getPlayerRenderer(GamePlayer gamePlayer) {
        EntityRendererManager entityRendererManager = gamePlayer.getEntityRendererManager();
        return (EntityPlayerRenderer) entityRendererManager.getRenderer(player);
    }

    public void setHeadRotation(float headRotation) {
        this.tracker.setHeadRotation(headRotation);
    }

    public void spectate(GamePlayer gamePlayer) {
        if (trackedPlayers.contains(gamePlayer)) {
            IPlayerController controller = gamePlayer.getController();
            controller.spectate(player);
        }
    }

    public Location getLocation() {
        return location.clone();
    }

    public void setLocation(Location location) {
        this.lastLocation = this.location;
        this.location = location.clone();
        this.moved = true;
    }

    public void move(int x, double y, int z) {
        this.lastLocation = this.location.clone();
        this.location.setX(x);
        this.location.setY(y);
        this.location.setZ(z);
        this.moved = true;
    }

    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public int hashCode() {
        return player.getEntityId();
    }

}
