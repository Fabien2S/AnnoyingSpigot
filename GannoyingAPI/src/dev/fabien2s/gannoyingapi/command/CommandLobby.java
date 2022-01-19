package dev.fabien2s.gannoyingapi.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.command.CommandNode;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import dev.fabien2s.annoyingapi.command.annotation.FunctionInfo;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.lobby.LobbyPlayer;
import dev.fabien2s.annoyingapi.command.annotation.Arg;
import dev.fabien2s.annoyingapi.command.suggestion.SuggestionHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CommandLobby extends CommandNode {

    public CommandLobby() {
        super("lobby");
    }

    @FunctionInfo(permission = "command.lobby.set_role")
    private void set_role(ICommandContext context, @Arg(name = "role", suggestionProvider = RoleSuggestionProvider.class) NamespacedKey roleKey) throws CommandSyntaxException {
        Player player = context.requiresPlayer();

        AnnoyingPlugin plugin = context.getPlugin();
        PlayerList playerList = plugin.getPlayerList();
        GamePlayer currentPlayer = playerList.getPlayer(player);
        if (currentPlayer instanceof LobbyPlayer) {
            ((LobbyPlayer) currentPlayer).setSelectedRole(roleKey);
            context.sendMessage("Set own game role to " + roleKey, true);
        } else
            context.sendError("Unable to set your game role");
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
