package dev.fabien2s.annoyingapi.command.reflection;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.fabien2s.annoyingapi.command.CommandNode;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import dev.fabien2s.annoyingapi.command.ICommandRegistry;
import dev.fabien2s.annoyingapi.command.annotation.Arg;
import dev.fabien2s.annoyingapi.command.annotation.FunctionInfo;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import dev.fabien2s.annoyingapi.command.suggestion.EnumSuggestionProvider;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

@RequiredArgsConstructor
public class CommandBaker<T> {

    private static final Logger LOGGER = LogManager.getLogger(CommandBaker.class);
    private static final CommandWrapper.Argument[] EMPTY_ARGS = new CommandWrapper.Argument[0];

    private final ICommandRegistry<T> commandRegistry;
    private final CommandContextProvider<T> contextProvider;
    private final BiPredicate<T, String> permissionPredicate;

    public LiteralArgumentBuilder<T> bakeCommand(CommandNode commandNode) {
        String commandName = commandNode.getName();
        LiteralArgumentBuilder<T> commandBuilder = LiteralArgumentBuilder.literal(commandName);

        Map<String, LiteralArgumentBuilder<T>> builderMap = new HashMap<>();

        CommandNode[] children = commandNode.getChildren();
        for (CommandNode child : children)
            commandBuilder.then(bakeCommand(child));

        Class<? extends CommandNode> commandNodeClass = commandNode.getClass();
        Method[] methods = commandNodeClass.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(FunctionInfo.class))
                continue;

            FunctionInfo functionInfo = method.getAnnotation(FunctionInfo.class);
            String functionName = functionInfo.name();
            if (functionName.isEmpty())
                functionName = method.getName();

            String permission = functionInfo.permission();

            Parameter[] parameters = method.getParameters();

            int parameterCount = parameters.length;
            boolean hasContext = false;

            if (parameters.length > 0) {
                Parameter firstParameter = parameters[0];
                Class<?> firstParameterType = firstParameter.getType();
                if (firstParameterType.isAssignableFrom(ICommandContext.class)) {
                    hasContext = true;
                    parameterCount -= 1;
                }
            }

            LiteralArgumentBuilder<T> functionBuilder = builderMap.computeIfAbsent(functionName, LiteralArgumentBuilder::literal);
            if (parameterCount != 0) {

                int offset = hasContext ? 1 : 0;
                BakedParameter[] bakedParameters = new BakedParameter[parameterCount];
                for (int i = 0; i < parameterCount; i++) {
                    Parameter parameter = parameters[i + offset];
                    Arg annotation = parameter.getAnnotation(Arg.class);
                    if (annotation == null)
                        throw new IllegalArgumentException("Missing Arg annotation");
                    bakedParameters[i] = new BakedParameter(
                            parameter.getType(),
                            annotation
                    );
                }

                bakeComplexFunction(functionBuilder, permission, commandNode, method, bakedParameters, hasContext);
            } else
                bakeSimpleFunction(functionBuilder, permission, commandNode, method, hasContext);
        }

        for (LiteralArgumentBuilder<T> argumentBuilder : builderMap.values())
            commandBuilder.then(argumentBuilder);

        return commandBuilder;
    }

    private void bakeSimpleFunction(LiteralArgumentBuilder<T> functionBuilder, String permission, CommandNode commandNode, Method method, boolean hasContext) {
        method.setAccessible(true);
        CommandWrapper<T> commandWrapper = new CommandWrapper<>(commandNode, method, hasContext, EMPTY_ARGS, contextProvider);
        functionBuilder
                .requires(arg -> permissionPredicate.test(arg, permission))
                .executes(commandWrapper);
    }

    private void bakeComplexFunction(LiteralArgumentBuilder<T> functionBuilder, String permission, CommandNode commandNode, Method method, BakedParameter[] parameters, boolean hasContext) {
        CommandWrapper.Argument[] arguments = new CommandWrapper.Argument[parameters.length];

        ArgumentBuilder<T, ?> childBuilder = null;
        for (int i = parameters.length - 1; i >= 0; i--) {

            BakedParameter parameter = parameters[i];

            Class<?> nmsType;
            ArgumentType<?> argumentType;

            ICommandArgumentConverter<T, ?, ?> argumentConverter = commandRegistry.getArgumentConverter(parameter.clazz);

            if (argumentConverter != null) {
                nmsType = argumentConverter.getArgumentClass();
                argumentType = argumentConverter.getArgumentType();
            } else {
                nmsType = parameter.clazz;
                argumentType = bakePrimitiveArgument(parameter.arg, nmsType);
            }

            if (argumentType == null)
                throw new IllegalArgumentException("Unable to bake parameter of type " + nmsType);

            String argName = parameter.arg.name();
            arguments[i] = new CommandWrapper.Argument(
                    argName, parameter.clazz, nmsType, argumentConverter
            );

            RequiredArgumentBuilder<T, ?> parameterBuilder = RequiredArgumentBuilder.argument(
                    argName,
                    argumentType
            );

            Class<?> suggestionProviderClass = parameter.arg.suggestionProvider();
            if (SuggestionProvider.class.isAssignableFrom(suggestionProviderClass)) {
                try {
                    Constructor<?> constructor = suggestionProviderClass.getConstructor();
                    @SuppressWarnings("unchecked")
                    SuggestionProvider<T> suggestionProvider = (SuggestionProvider<T>) constructor.newInstance();
                    parameterBuilder.suggests(suggestionProvider);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    LOGGER.error("Unable to create the suggestion provider for node: " + commandNode, e);
                }
            } else if (parameter.clazz.isEnum())
                parameterBuilder.suggests(new EnumSuggestionProvider<>(parameter.clazz));

            if (childBuilder != null)
                parameterBuilder.then(childBuilder);
            else {
                CommandWrapper<T> commandWrapper = new CommandWrapper<>(commandNode, method, hasContext, arguments, contextProvider);
                parameterBuilder.requires(arg -> permissionPredicate.test(arg, permission)).executes(commandWrapper);
            }

            childBuilder = parameterBuilder;
        }

        method.setAccessible(true);
        if (childBuilder != null)
            functionBuilder.then(childBuilder);
    }

    private ArgumentType<?> bakePrimitiveArgument(Arg arg, Class<?> parameterType) {

        if (parameterType.isAssignableFrom(String.class)) {
            switch (arg.type()) {
                case QUOTABLE_PHRASE:
                    return StringArgumentType.string();
                case GREEDY_PHRASE:
                    return StringArgumentType.greedyString();
                case SINGLE_WORD:
                default:
                    return StringArgumentType.word();
            }
        } else if (parameterType.isAssignableFrom(boolean.class))
            return BoolArgumentType.bool();

        double min = arg.min();
        double max = arg.max();
        if (parameterType.isAssignableFrom(int.class)) {
            return IntegerArgumentType.integer(
                    Double.isNaN(min) ? Integer.MIN_VALUE : (int) min,
                    Double.isNaN(max) ? Integer.MAX_VALUE : (int) max
            );
        } else if (parameterType.isAssignableFrom(long.class)) {
            return LongArgumentType.longArg(
                    Double.isNaN(min) ? Long.MIN_VALUE : (long) min,
                    Double.isNaN(max) ? Long.MAX_VALUE : (long) max
            );
        } else if (parameterType.isAssignableFrom(float.class)) {
            return FloatArgumentType.floatArg(
                    Double.isNaN(min) ? -Float.MAX_VALUE : (float) min,
                    Double.isNaN(max) ? Float.MAX_VALUE : (float) max
            );
        } else if (parameterType.isAssignableFrom(double.class)) {
            return DoubleArgumentType.doubleArg(
                    Double.isNaN(min) ? -Double.MAX_VALUE : min,
                    Double.isNaN(max) ? Double.MAX_VALUE : max
            );
        }

        return null;
    }

    public interface CommandContextProvider<T> {
        ICommandContext get(T t);
    }

    @RequiredArgsConstructor
    private static class BakedParameter {
        private final Class<?> clazz;
        private final Arg arg;
    }

}
