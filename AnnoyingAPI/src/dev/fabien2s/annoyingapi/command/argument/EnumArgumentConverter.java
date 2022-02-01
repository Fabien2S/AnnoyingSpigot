package dev.fabien2s.annoyingapi.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.fabien2s.annoyingapi.adapter.AnnoyingAdapter;

import java.util.Locale;

public class EnumArgumentConverter<T, U extends Enum<U>> implements ICommandArgumentConverter<T, U, String> {

    private static final DynamicCommandExceptionType INVALID_ENUM_EXCEPTION = new DynamicCommandExceptionType(input ->
            AnnoyingAdapter.translate("command.exception", input)
    );

    @Override
    public U convert(CommandContext<T> context, Class<U> type, String input) throws CommandSyntaxException {
        try {
            String enumConstant = input.toUpperCase(Locale.ROOT);
            return Enum.valueOf(type, enumConstant);
        } catch (IllegalArgumentException e) {
            throw INVALID_ENUM_EXCEPTION.create(input);
        }
    }

    @Override
    public ArgumentType<String> getArgumentType() {
        return StringArgumentType.word();
    }

    @Override
    public Class<String> getArgumentClass() {
        return String.class;
    }

}
