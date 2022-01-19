package dev.fabien2s.annoyingapi.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class EnumSuggestionProvider<T> implements SuggestionProvider<T> {

    private final List<String> enumValues;

    public EnumSuggestionProvider(Class<?> enumClass) {
        if (!enumClass.isEnum())
            throw new IllegalArgumentException(enumClass + " is not an enum");

        Enum<?>[] enumConstants = (Enum<?>[]) enumClass.getEnumConstants();
        this.enumValues = new ArrayList<>(enumConstants.length);
        for (Enum<?> enumConstant : enumConstants) {
            String enumName = enumConstant
                    .name()
                    .toLowerCase(Locale.ROOT);
            this.enumValues.add(enumName);
        }
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<T> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SuggestionHelper.suggest(enumValues, suggestionsBuilder);
    }

}
