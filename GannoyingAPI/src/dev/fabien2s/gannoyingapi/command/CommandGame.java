package dev.fabien2s.gannoyingapi.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.player.IPlayerProvider;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.annoyingapi.command.CommandNode;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import dev.fabien2s.annoyingapi.command.annotation.Arg;
import dev.fabien2s.annoyingapi.command.annotation.FunctionInfo;
import dev.fabien2s.annoyingapi.command.suggestion.SuggestionHelper;
import dev.fabien2s.annoyingapi.player.PlayerList;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CommandGame extends CommandNode {

    public CommandGame() {
        super("game");
    }

    @FunctionInfo(permission = "command.game.start")
    private void start(ICommandContext context) {
        GamePlugin plugin = (GamePlugin) context.getPlugin();

        context.sendSuccess("Starting game (it may take a while)", true);
        if (plugin.startGame())
            context.sendSuccess("Game successfully started", true);
        else
            context.sendFailure("Game is already started");
    }

    @FunctionInfo(permission = "command.game.stop")
    private void stop(ICommandContext context) {
        GamePlugin plugin = (GamePlugin) context.getPlugin();
        if (plugin.stopGame())
            context.sendSuccess("Game successfully stopped", true);
        else
            context.sendFailure("Game is not started");
    }

    @FunctionInfo(permission = "command.game.set_role")
    private void set_role(ICommandContext context, @Arg(name = "role", suggestionProvider = RoleSuggestionProvider.class) NamespacedKey roleKey) throws CommandSyntaxException {
        Player player = context.requiresPlayer();

        GamePlugin plugin = (GamePlugin) context.getPlugin();

        IPlayerProvider<AnnoyingPlugin> playerProvider = plugin.getPlayerProvider(roleKey);
        if (playerProvider == null) {
            context.sendFailure("Unknown role " + roleKey);
            return;
        }

        PlayerList playerList = plugin.getPlayerList();
        playerList.setPlayer(player, playerProvider);

        context.sendSuccess("Set own role to " + roleKey, true);
    }

    @FunctionInfo(permission = "command.game.reset_role")
    private void reset_role(ICommandContext context) throws CommandSyntaxException {
        Player player = context.requiresPlayer();

        GamePlugin plugin = (GamePlugin) context.getPlugin();
        PlayerList playerList = plugin.getPlayerList();
        playerList.resetPlayer(player);

        context.sendSuccess("Successfully reset own role", true);
    }

    public static class RoleSuggestionProvider<T> implements SuggestionProvider<T> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<T> commandContext, SuggestionsBuilder suggestionsBuilder) {
            GamePlugin plugin = GamePlugin.getPlugin(GamePlugin.class);
            Set<NamespacedKey> playerRoles = plugin.getPlayerRoles();
            return SuggestionHelper.suggestIdentifier(playerRoles, suggestionsBuilder);
        }
    }

}
