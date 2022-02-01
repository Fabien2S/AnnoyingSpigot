package dev.fabien2s.annoyingapi.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface ICommandArgumentConverter<TContext, TCraft, TNms> {

    TCraft convert(CommandContext<TContext> context, Class<TCraft> type, TNms o) throws CommandSyntaxException;

    ArgumentType<TNms> getArgumentType();

    Class<TNms> getArgumentClass();

}
