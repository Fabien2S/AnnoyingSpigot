package dev.fabien2s.annoyingapi.adapter.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.phys.Vec2F;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class CommandContext implements ICommandContext {

    private final CommandListenerWrapper listener;

    @Override
    public void sendMessage(String message, boolean notifyAdmin) {
        this.listener.a(new ChatComponentText(message), notifyAdmin);
    }

    @Override
    public void sendError(String message) {
        this.listener.a(new ChatComponentText(message));
    }

    @Override
    public void sendMessage(BaseComponent message, boolean notifyAdmin) {
        String json = ComponentSerializer.toString(message);
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a(json);
        this.listener.a(chatComponent, notifyAdmin);
    }

    @Override
    public void sendError(BaseComponent message) {
        String json = ComponentSerializer.toString(message);
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a(json);
        this.listener.a(chatComponent);
    }

    @Override
    public @NotNull Player requiresPlayer() throws CommandSyntaxException {
        EntityPlayer entityPlayer = listener.h();
        return entityPlayer.getBukkitEntity();
    }

    @Override
    public @NotNull Entity requiresEntity() throws CommandSyntaxException {
        net.minecraft.world.entity.Entity entity = listener.g();
        return entity.getBukkitEntity();
    }

    @Override
    public String getName() {
        return listener.c();
    }

    @Override
    public Server getServer() {
        MinecraftServer minecraftServer = listener.j();
        return minecraftServer.server;
    }

    @Override
    public World getWorld() {
        WorldServer world = listener.e();
        return world.getWorld();
    }

    @Override
    public Vector getPosition() {
        Vec3D position = listener.d();
        return new Vector(position.b, position.c, position.d);
    }

    @Override
    public float getYaw() {
        Vec2F rotation = listener.i();
        return rotation.j;
    }

    @Override
    public float getPitch() {
        Vec2F rotation = listener.i();
        return rotation.i;
    }

    @Override
    public Vector getDirection() {
        Vec2F rotation = listener.i();
        return VectorHelper.direction(rotation.j, rotation.i);
    }

}
