package dev.fabien2s.annoyingapi.adapter.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.phys.Vec2F;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommandContext implements ICommandContext {

    private final CommandListenerWrapper listener;

    public CommandContext(CommandListenerWrapper listener) {
        this.listener = listener;
    }

    @Override
    public void sendSuccess(@Nonnull String message, boolean notifyAdmin) {
        ChatComponentText textComponent = new ChatComponentText(message);
        this.listener.a(textComponent, notifyAdmin);
    }

    @Override
    public void sendSuccess(@Nonnull BaseComponent message, boolean notifyAdmin) {
        String json = ComponentSerializer.toString(message);
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a(json);
        this.listener.a(chatComponent, notifyAdmin);
    }

    @Override
    public void sendFailure(@Nonnull String message) {
        ChatComponentText textComponent = new ChatComponentText(message);
        this.listener.a(textComponent);
    }

    @Override
    public void sendFailure(@Nonnull BaseComponent message) {
        String json = ComponentSerializer.toString(message);
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a(json);
        this.listener.a(chatComponent);
    }

    @Override
    public @Nonnull String getName() {
        return listener.c();
    }

    @Override
    public @Nonnull BaseComponent[] getDisplayName() {
        IChatBaseComponent component = listener.b();
        String json = IChatBaseComponent.ChatSerializer.a(component);
        return ComponentSerializer.parse(json);
    }

    @Override
    public @Nonnull Server getServer() {
        MinecraftServer minecraftServer = listener.j();
        return minecraftServer.server;
    }

    @Override
    public @Nonnull World getWorld() {
        WorldServer world = listener.e();
        return world.getWorld();
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
    public @Nonnull Player requiresPlayer() throws CommandSyntaxException {
        EntityPlayer entityPlayer = listener.h();
        return entityPlayer.getBukkitEntity();
    }

    @Override
    public @Nonnull Entity requiresEntity() throws CommandSyntaxException {
        net.minecraft.world.entity.Entity entity = listener.g();
        return entity.getBukkitEntity();
    }

    @Override
    public @Nullable Entity getEntity() {
        net.minecraft.world.entity.Entity entity = listener.f();
        return entity != null ? entity.getBukkitEntity() : null;
    }
}
