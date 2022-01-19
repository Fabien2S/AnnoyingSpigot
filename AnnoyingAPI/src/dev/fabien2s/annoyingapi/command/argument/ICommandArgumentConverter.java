package dev.fabien2s.annoyingapi.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface ICommandArgumentConverter<T, U, V> {

    U convert(CommandContext<T> context, Class<U> type, V o) throws CommandSyntaxException;

    ArgumentType<V> getArgumentType();

    Class<V> getArgumentClass();

}
