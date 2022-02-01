package dev.fabien2s.annoyingapi.adapter.command.argument.entities;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.bukkit.entity.Entity;

public class EntityConverter implements ICommandArgumentConverter<CommandListenerWrapper, Entity, EntitySelector> {

    @Override
    public Entity convert(CommandContext<CommandListenerWrapper> context, Class<Entity> type, EntitySelector selector) throws CommandSyntaxException {
        CommandListenerWrapper source = context.getSource();
        return selector.a(source).getBukkitEntity();
    }

    @Override
    public ArgumentType<EntitySelector> getArgumentType() {
        return ArgumentEntity.a();
    }

    @Override
    public Class<EntitySelector> getArgumentClass() {
        return EntitySelector.class;
    }

}
