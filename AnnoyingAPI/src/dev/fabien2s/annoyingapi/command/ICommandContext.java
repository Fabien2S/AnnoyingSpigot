package dev.fabien2s.annoyingapi.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ICommandContext {

    void sendSuccess(@Nonnull String message, boolean notifyAdmin);

    void sendFailure(@Nonnull String message);

    void sendSuccess(@Nonnull BaseComponent message, boolean notifyAdmin);

    void sendFailure(@Nonnull BaseComponent message);

    @Nonnull
    String getName();

    @Nonnull
    BaseComponent[] getDisplayName();

    @Nonnull
    Server getServer();

    @Nonnull
    World getWorld();

    default Location getLocation() {
        Vector position = getPosition();
        World world = getWorld();
        float yaw = getYaw();
        float pitch = getPitch();
        return position.toLocation(world, yaw, pitch);
    }

    float getYaw();

    float getPitch();

    @Nonnull
    default Vector getPosition() {
        float yaw = getYaw();
        float pitch = getPitch();
        return VectorHelper.direction(yaw, pitch);
    }

    @Nullable
    Entity getEntity();

    @Nonnull
    Player requiresPlayer() throws CommandSyntaxException;

    @Nonnull
    Entity requiresEntity() throws CommandSyntaxException;

    default AnnoyingPlugin getPlugin() {
        return AnnoyingPlugin.getInstance();
    }

}
