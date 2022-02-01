package dev.fabien2s.annoyingapi.adapter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.RootCommandNode;
import dev.fabien2s.annoyingapi.adapter.command.argument.*;
import dev.fabien2s.annoyingapi.adapter.command.argument.entities.EntityArrayConverter;
import dev.fabien2s.annoyingapi.adapter.command.argument.entities.EntityConverter;
import dev.fabien2s.annoyingapi.adapter.command.argument.entities.PlayerArrayConverter;
import dev.fabien2s.annoyingapi.adapter.command.argument.entities.PlayerConverter;
import dev.fabien2s.annoyingapi.command.CommandNode;
import dev.fabien2s.annoyingapi.command.ICommandRegistry;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import dev.fabien2s.annoyingapi.command.reflection.CommandBaker;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class CommandRegistry implements ICommandRegistry<CommandListenerWrapper> {

    private final Set<ArgumentConverterEntry<?>> converterEntries = new HashSet<>();

    private final CommandDispatcher<CommandListenerWrapper> commandDispatcher;
    private final CommandBaker<CommandListenerWrapper> commandBaker;

    public CommandRegistry(Server server) {
        CraftServer craftServer = (CraftServer) server;
        DedicatedServer dedicatedServer = craftServer.getServer();
        net.minecraft.commands.CommandDispatcher commandDispatcher = dedicatedServer.vanillaCommandDispatcher;
        this.commandDispatcher = commandDispatcher.a();
        this.commandBaker = new CommandBaker<>(this, CommandContext::new, this::testPermission);

        this.registerArgumentConverter(Vector.class, new VectorConverter());
        this.registerArgumentConverter(BlockVector.class, new BlockVectorConverter());
        this.registerArgumentConverter(NamespacedKey.class, new NamespacedKeyConverter());
        this.registerArgumentConverter(ItemStack.class, new ItemStackConverter());
        this.registerArgumentConverter(BlockData.class, new BlockDataConverter());
        this.registerArgumentConverter(Entity.class, new EntityConverter());
        this.registerArgumentConverter(Entity[].class, new EntityArrayConverter());
        this.registerArgumentConverter(Player.class, new PlayerConverter());
        this.registerArgumentConverter(Player[].class, new PlayerArrayConverter());
        this.registerArgumentConverter(OfflinePlayer[].class, new OfflinePlayerArrayConverter());
    }

    private boolean testPermission(CommandListenerWrapper commandListenerWrapper, String permission) {
        if (permission.isEmpty())
            return true;

        CommandSender sender = commandListenerWrapper.getBukkitSender();
        return sender.hasPermission(permission);
    }

    @Override
    public void registerCommand(CommandNode command) {
        LiteralArgumentBuilder<CommandListenerWrapper> argumentBuilder = commandBaker.bakeCommand(command);
        this.commandDispatcher.register(argumentBuilder);
    }

    @Override
    public void unregisterCommand(CommandNode command) {
//        RootCommandNode<CommandListenerWrapper> rootCommandNode = commandDispatcher.getRoot();
//        String commandName = command.getName();
//        rootCommandNode.removeCommand(commandName);
    }

    @Override
    public <U> void registerArgumentConverter(Class<U> argClass, ICommandArgumentConverter<CommandListenerWrapper, U, ?> argumentConverter) {
        this.converterEntries.add(new ArgumentConverterEntry<>(argClass, argumentConverter));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> ICommandArgumentConverter<CommandListenerWrapper, U, ?> getArgumentConverter(Class<U> argClass) {
        for (ArgumentConverterEntry<?> converterEntry : converterEntries) {
            if (converterEntry.clazz.isAssignableFrom(argClass))
                return (ICommandArgumentConverter<CommandListenerWrapper, U, ?>) converterEntry.converter;
        }
        return null;
    }

    private record ArgumentConverterEntry<U>(
            Class<U> clazz,
            ICommandArgumentConverter<CommandListenerWrapper, U, ?> converter
    ) {
    }
}
