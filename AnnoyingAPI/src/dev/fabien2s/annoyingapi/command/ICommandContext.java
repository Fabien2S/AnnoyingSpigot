package dev.fabien2s.annoyingapi.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface ICommandContext {

    void sendMessage(String message, boolean notifyAdmin);

    void sendError(String message);

    void sendMessage(BaseComponent message, boolean notifyAdmin);

    void sendError(BaseComponent message);

    Player requiresPlayer() throws CommandSyntaxException;

    Entity requiresEntity() throws CommandSyntaxException;

    String getName();

    Server getServer();

    World getWorld();

    Vector getPosition();

    float getYaw();

    float getPitch();

    Vector getDirection();

    default void sendMessage(String message) {
        this.sendMessage(message, false);
    }

    default AnnoyingPlugin getPlugin() {
        return AnnoyingPlugin.getInstance();
    }

    default Location getLocation() {
        Vector position = getPosition();
        World world = getWorld();
        float yaw = getYaw();
        float pitch = getPitch();
        return position.toLocation(world, yaw, pitch);
    }

}
