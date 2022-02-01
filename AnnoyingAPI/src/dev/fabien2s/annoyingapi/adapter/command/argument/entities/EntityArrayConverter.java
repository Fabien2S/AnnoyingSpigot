package dev.fabien2s.annoyingapi.adapter.command.argument.entities;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.bukkit.entity.Entity;

import java.util.List;

public class EntityArrayConverter implements ICommandArgumentConverter<CommandListenerWrapper, Entity[], EntitySelector> {

    private static final Entity[] EMPTY = new Entity[0];

    @Override
    public Entity[] convert(CommandContext<CommandListenerWrapper> context, Class<Entity[]> type, EntitySelector selector) throws CommandSyntaxException {
        CommandListenerWrapper source = context.getSource();
        List<? extends net.minecraft.world.entity.Entity> entities = selector.b(source);
        if (entities.isEmpty())
            return EMPTY;

        Entity[] entityArray = new Entity[entities.size()];
        for (int i = 0; i < entities.size(); i++)
            entityArray[i] = entities.get(i).getBukkitEntity();
        return entityArray;
    }

    @Override
    public ArgumentType<EntitySelector> getArgumentType() {
        return ArgumentEntity.b();
    }

    @Override
    public Class<EntitySelector> getArgumentClass() {
        return EntitySelector.class;
    }

}
