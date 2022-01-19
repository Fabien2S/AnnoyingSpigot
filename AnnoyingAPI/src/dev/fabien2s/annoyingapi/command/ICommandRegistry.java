package dev.fabien2s.annoyingapi.command;

import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;

public interface ICommandRegistry<T> {

    void registerCommand(CommandNode command);

    void unregisterCommand(CommandNode command);

    <U> void registerArgumentConverter(Class<U> argClass, ICommandArgumentConverter<T, U, ?> argumentConverter);

    <U> ICommandArgumentConverter<T, U, ?> getArgumentConverter(Class<U> argClass);

}
