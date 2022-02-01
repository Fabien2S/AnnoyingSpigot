package dev.fabien2s.annoyingapi.adapter.command.argument.entities;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerArrayConverter implements ICommandArgumentConverter<CommandListenerWrapper, Player[], EntitySelector> {

    private static final Player[] EMPTY = new Player[0];

    @Override
    public Player[] convert(CommandContext<CommandListenerWrapper> context, Class<Player[]> type, EntitySelector selector) throws CommandSyntaxException {
        CommandListenerWrapper source = context.getSource();
        List<EntityPlayer> entities = selector.d(source);
        if (entities.isEmpty())
            return EMPTY;

        Player[] entityArray = new Player[entities.size()];
        for (int i = 0; i < entities.size(); i++)
            entityArray[i] = entities.get(i).getBukkitEntity();
        return entityArray;
    }

    @Override
    public ArgumentType<EntitySelector> getArgumentType() {
        return ArgumentEntity.d();
    }

    @Override
    public Class<EntitySelector> getArgumentClass() {
        return EntitySelector.class;
    }

}
