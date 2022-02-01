package dev.fabien2s.annoyingapi.adapter.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.NamespacedKey;

public class NamespacedKeyConverter implements ICommandArgumentConverter<CommandListenerWrapper, NamespacedKey, MinecraftKey> {

    @Override
    @SuppressWarnings("deprecation")
    public NamespacedKey convert(CommandContext<CommandListenerWrapper> context, Class<NamespacedKey> type, MinecraftKey o) {
        return new NamespacedKey(o.b(), o.a());
    }

    @Override
    public ArgumentType<MinecraftKey> getArgumentType() {
        return ArgumentMinecraftKeyRegistered.a();
    }

    @Override
    public Class<MinecraftKey> getArgumentClass() {
        return MinecraftKey.class;
    }

}
