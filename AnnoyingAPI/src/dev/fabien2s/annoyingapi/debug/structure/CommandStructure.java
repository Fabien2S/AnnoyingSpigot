package dev.fabien2s.annoyingapi.debug.structure;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.command.CommandNode;
import dev.fabien2s.annoyingapi.command.ICommandContext;
import dev.fabien2s.annoyingapi.command.suggestion.SuggestionHelper;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.structure.Structure;
import dev.fabien2s.annoyingapi.structure.StructureManager;
import dev.fabien2s.annoyingapi.command.annotation.Arg;
import dev.fabien2s.annoyingapi.command.annotation.FunctionInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CommandStructure extends CommandNode {

    public CommandStructure() {
        super("structure");
    }

    @Nullable
    private StructureBuilderPlayer getPlayer(ICommandContext context) throws CommandSyntaxException {
        AnnoyingPlugin plugin = context.getPlugin();
        Player player = context.requiresPlayer();
        PlayerList playerList = plugin.getPlayerList();
        StructureBuilderPlayer builderPlayer = playerList.getPlayer(player, StructureBuilderPlayer.class);
        if (builderPlayer == null)
            context.sendError("You are editing no structure");
        return builderPlayer;
    }

    @FunctionInfo
    private void create(ICommandContext context, @Arg(name = "name") String name, @Arg(name = "size") BlockVector size) throws CommandSyntaxException {
        Player player = context.requiresPlayer();
        Vector position = context.getPosition();
        BlockVector blockPosition = new BlockVector(
                position.getBlockX(),
                position.getBlockY(),
                position.getBlockZ()
        );

        AnnoyingPlugin plugin = context.getPlugin();
        PlayerList playerList = plugin.getPlayerList();
        StructureBuilderPlayer builderPlayer = (StructureBuilderPlayer) playerList.setPlayer(player, StructureBuilderPlayer::new);
        builderPlayer.editStructure(name, blockPosition, size);
        context.sendMessage("Structure \"" + name + "\" created", true);
    }

    @FunctionInfo
    private void edit(ICommandContext context, @Arg(name = "name", suggestionProvider = StructureSuggestionProvider.class) NamespacedKey name) throws CommandSyntaxException {
        Player player = context.requiresPlayer();

        AnnoyingPlugin plugin = context.getPlugin();
        StructureManager structureManager = plugin.getStructureManager();
        try {
            Structure structure = structureManager.load(name);

            PlayerList playerList = plugin.getPlayerList();
            StructureBuilderPlayer builderPlayer = (StructureBuilderPlayer) playerList.setPlayer(player, StructureBuilderPlayer::new);

            Vector position = context.getPosition();
            BlockVector blockPosition = new BlockVector(
                    position.getBlockX(),
                    position.getBlockY(),
                    position.getBlockZ()
            );
            builderPlayer.editStructure(structure, blockPosition);
            context.sendMessage("Editing structure \"" + name + "\"", true);

        } catch (IOException e) {
            context.sendError("Structure \"" + name + " \" not found");
        }
    }

    @FunctionInfo
    private void anchor(ICommandContext context, @Arg(name = "name") String name, @Arg(name = "tag") String tag) throws CommandSyntaxException {
        StructureBuilderPlayer builderPlayer = getPlayer(context);
        if (builderPlayer == null)
            return;

        Location location = context.getLocation();
        if (builderPlayer.addAnchor(name, tag, location))
            context.sendMessage("Anchor \"" + name + "\" successfully added");
        else
            context.sendError("An anchor named \"" + name + "\" already exists");
    }

    @FunctionInfo
    private void save(ICommandContext context) throws CommandSyntaxException {
        StructureBuilderPlayer builderPlayer = getPlayer(context);
        if (builderPlayer == null)
            return;

        if (builderPlayer.saveStructure())
            context.sendMessage("Structure saved");
        else
            context.sendError("Unable to save the structure");
    }

    @FunctionInfo
    private void load(ICommandContext context, @Arg(name = "name") NamespacedKey name) {
        Location location = context.getLocation();

        try {
            AnnoyingPlugin plugin = context.getPlugin();
            StructureManager structureManager = plugin.getStructureManager();
            Structure structure = structureManager.load(name);

            Block origin = location.getBlock();
            for (int x = 0; x < structure.getSizeX(); x++) {
                for (int y = 0; y < structure.getSizeY(); y++) {
                    for (int z = 0; z < structure.getSizeZ(); z++) {
                        Block block = origin.getRelative(x, y, z);
                        BlockData blockData = structure.getBlock(x, y, z);
                        Material material = blockData.getMaterial();
                        if (material != Material.STRUCTURE_VOID)
                            block.setBlockData(blockData, false);
                    }
                }
            }

            context.sendMessage("Structure \"" + name + "\" successfully loaded", true);

        } catch (IOException e) {
            context.sendError("Missing structure file");
        }

    }

    public static class StructureSuggestionProvider<T> implements SuggestionProvider<T> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<T> commandContext, SuggestionsBuilder suggestionsBuilder) {
            AnnoyingPlugin plugin = AnnoyingPlugin.getPlugin(AnnoyingPlugin.class);
            StructureManager structureManager = plugin.getStructureManager();
            Collection<NamespacedKey> structures = structureManager.getStructures();
            return SuggestionHelper.suggestIdentifier(structures, suggestionsBuilder);
        }
    }

}
