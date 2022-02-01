package dev.fabien2s.annoyingapi.adapter.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.commands.arguments.blocks.ArgumentTileLocation;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public class BlockDataConverter implements ICommandArgumentConverter<CommandListenerWrapper, BlockData, ArgumentTileLocation> {

    @Override
    public BlockData convert(CommandContext<CommandListenerWrapper> context, Class<BlockData> type, ArgumentTileLocation o) throws CommandSyntaxException {
        IBlockData blockData = o.a();
        return CraftBlockData.fromData(blockData);
    }

    @Override
    public ArgumentType<ArgumentTileLocation> getArgumentType() {
        return ArgumentTile.a();
    }

    @Override
    public Class<ArgumentTileLocation> getArgumentClass() {
        return ArgumentTileLocation.class;
    }
}
