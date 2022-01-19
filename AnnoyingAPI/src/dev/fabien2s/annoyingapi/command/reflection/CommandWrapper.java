package dev.fabien2s.annoyingapi.command.reflection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.CommandNode;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandWrapper<T> implements Command<T> {

    private static final Logger LOGGER = LogManager.getLogger(CommandWrapper.class);

    private final CommandNode command;
    private final Method method;
    private final boolean hasContextArg;
    private final Argument[] arguments;
    private final CommandBaker.CommandContextProvider<T> contextProvider;

    public CommandWrapper(CommandNode command, Method method, boolean hasContextArg, Argument[] arguments, CommandBaker.CommandContextProvider<T> contextProvider) {
        this.command = command;
        this.method = method;
        this.hasContextArg = hasContextArg;
        this.arguments = arguments;
        this.contextProvider = contextProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int run(CommandContext<T> commandContext) {

        T commandSource = commandContext.getSource();
        ICommandContext context = contextProvider.get(commandSource);

        try {
            int argumentCount = arguments.length;
            int offset;
            Object[] args;

            if (hasContextArg) {
                args = new Object[argumentCount + 1];
                args[0] = context;
                offset = 1;
            } else {
                args = new Object[argumentCount];
                offset = 0;
            }

            // get the arguments
            for (int i = 0; i < argumentCount; i++) {
                Argument argument = arguments[i];

                Object contextArgument = commandContext.getArgument(
                        argument.name,
                        argument.nmsType
                );

                ICommandArgumentConverter<T, Object, Object> converter = (ICommandArgumentConverter<T, Object, Object>) argument.converter;
                if (converter != null)
                    args[i + offset] = converter.convert(commandContext, (Class<Object>) argument.type, contextArgument);
                else
                    args[i + offset] = contextArgument;
            }

            // invoke the method
            this.method.invoke(command, args);
            return com.mojang.brigadier.Command.SINGLE_SUCCESS;

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            LOGGER.error("Unable to invoke this command", cause);
            throw new RuntimeException(cause);
        } catch (IllegalAccessException e) {
            Throwable cause = e.getCause();
            LOGGER.error("Unable to execute this command", cause);
            throw new RuntimeException(cause);
        } catch (Exception e) {
            LOGGER.error("Unable to execute this command", e);
            throw new RuntimeException(e);
        }
    }

    @RequiredArgsConstructor
    public static class Argument {
        @Getter private final String name;
        @Getter private final Class<?> type;
        @Getter private final Class<?> nmsType;
        @Getter private final ICommandArgumentConverter<?, ?, ?> converter;
    }

}
