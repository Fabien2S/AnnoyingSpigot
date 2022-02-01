package dev.fabien2s.annoyingapi.camera;

import com.mojang.authlib.GameProfile;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.adapter.npc.NPCAdapter;
import dev.fabien2s.annoyingapi.entity.renderer.EntityPlayerRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.skin.ISkinHolder;
import dev.fabien2s.annoyingapi.skin.Skin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NpcCamera extends PlayerCamera {

    @Getter protected HumanEntity npc;

    @Getter @Setter private String name;

    public NpcCamera(AnnoyingPlayer player) {
        super(player);
    }

    @Override
    public void init(Location location) {
        super.init(location);

        String npcName = name == null ? "anim_" + hashCode() : name;
        GameProfile gameProfile = NPCAdapter.createProfile(npcName);

        if (player instanceof ISkinHolder) {
            ISkinHolder skinHolder = (ISkinHolder) this.player;
            Skin skin = skinHolder.getSkin();
            if (skin != null)
                skin.apply(gameProfile);
        }

        Player spigotPlayer = player.getSpigotPlayer();
        spigotPlayer.setGameMode(GameMode.SPECTATOR);
        spigotPlayer.teleport(location);
        spigotPlayer.setVelocity(VectorHelper.zero());

        this.npc = NPCAdapter.spawnNPC(location, gameProfile);

        EntityPlayerRenderer playerRenderer = player.getPlayerRenderer();
        AnnoyingPlugin plugin = player.getPlugin();
        EntityRendererManager rendererManager = plugin.getEntityRendererManager();
        EntityPlayerRenderer npcRenderer = rendererManager.getRenderer(npc);

        playerRenderer.applyTo(npcRenderer);

        this.npc.spectate(player);
    }

    @Override
    public void reset(Location location) {
        super.reset(location);

        IPlayerController controller = player.getController();
        controller.spectate(null);

        this.npc.remove();
    }

    @Override
    public boolean isSpectating() {
        return true;
    }

    @Override
    public void emitSound(@NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        this.npc.emitSound(sound, category, volume, pitch);
    }

    @Override
    public void emitSound(@NotNull @Nonnull NamespacedKey sound, @Nonnull SoundCategory category, float volume, float pitch) {
        this.npc.emitSound(sound, category, volume, pitch);
    }

    @Override
    public void stopEmittingSound(Sound sound, SoundCategory category) {
        this.npc.stopEmittingSound(sound, category);
    }

    @Override
    public void stopEmittingSound(@Nonnull NamespacedKey sound, @Nullable SoundCategory category) {
        this.npc.stopEmittingSound(sound, category);
    }

    @Override
    public World getWorld() {
        return npc.getWorld();
    }

    @Override
    public Location getLocation() {
        return npc.getLocation();
    }

    @Override
    public void setLocation(Location location) {
        this.npc.setLocation(location);

        float yaw = location.getYaw();
        this.npc.setHeadRotation(yaw);
    }

    public EntityPlayerRenderer getRenderer() {
        return npc.getRenderer();
    }

    public void applyGravity() {
        double gravity = player.getGravity();
        this.npc.move(0, gravity, 0);
    }

    public void forceRender() {
        AnnoyingPlugin plugin = player.getPlugin();
        PlayerList playerList = plugin.getPlayerList();
        playerList.forEach(gamePlayer -> {
            this.npc.spawn(gamePlayer);
        });
    }
}
