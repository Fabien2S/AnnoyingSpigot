package dev.fabien2s.annoyingapi.adapter.command.argument;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentProfile;
import net.minecraft.server.MinecraftServer;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;

import java.util.Collection;

public class OfflinePlayerArrayConverter implements ICommandArgumentConverter<CommandListenerWrapper, OfflinePlayer[], ArgumentProfile.a> {

    private static final OfflinePlayer[] EMPTY = new OfflinePlayer[0];

    @Override
    public OfflinePlayer[] convert(CommandContext<CommandListenerWrapper> context, Class<OfflinePlayer[]> type, ArgumentProfile.a o) throws CommandSyntaxException {
        CommandListenerWrapper source = context.getSource();

        Collection<GameProfile> profiles = o.getNames(source);
        if (profiles.isEmpty())
            return EMPTY;

        MinecraftServer minecraftServer = source.j();
        CraftServer bukkitServer = minecraftServer.server;

        OfflinePlayer[] players = new OfflinePlayer[profiles.size()];

        int i = 0;
        for (GameProfile profile : profiles)
            players[i++] = bukkitServer.getOfflinePlayer(profile);

        return players;
    }

    @Override
    public ArgumentType<ArgumentProfile.a> getArgumentType() {
        return ArgumentProfile.a();
    }

    @Override
    public Class<ArgumentProfile.a> getArgumentClass() {
        return ArgumentProfile.a.class;
    }
}
