package dev.fabien2s.annoyingapi.command.suggestion;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuggestionHelper {

    public static CompletableFuture<Suggestions> suggestIdentifier(Iterable<NamespacedKey> entries, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();
        return suggestIdentifier(entries, remaining, key -> key, builder);
    }

    public static <T> CompletableFuture<Suggestions> suggestIdentifier(Iterable<T> entries, String remaining, Function<T, NamespacedKey> identifierSupplier, SuggestionsBuilder builder) {
        boolean hasSemicolon = remaining.indexOf(':') > -1;

        for (T entry : entries) {
            NamespacedKey entryIdentifier = identifierSupplier.apply(entry);
            String identifierNamespace = entryIdentifier.getNamespace();
            String identifierKey = entryIdentifier.getKey();

            String identifierString = entryIdentifier.toString();
            if (hasSemicolon) {
                if (identifierString.startsWith(remaining))
                    builder.suggest(identifierString);
            } else if (identifierNamespace.startsWith(remaining) || identifierNamespace.equals("minecraft") && identifierKey.startsWith(remaining))
                builder.suggest(identifierString);
        }

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> suggest(Iterable<String> entries, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();
        for (String entry : entries) {
            if (entry.startsWith(remaining))
                builder.suggest(entry);
        }
        return builder.buildFuture();
    }

}
