package dev.fabien2s.annoyingapi.adapter.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.coordinates.IVectorPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.util.BlockVector;

public class BlockVectorConverter implements ICommandArgumentConverter<CommandListenerWrapper, BlockVector, IVectorPosition> {

    @Override
    public BlockVector convert(CommandContext<CommandListenerWrapper> context, Class<BlockVector> type, IVectorPosition o) {
        Vec3D vec3D = o.a(context.getSource());
        return new BlockVector(vec3D.b, vec3D.c, vec3D.d);
    }

    @Override
    public ArgumentType<IVectorPosition> getArgumentType() {
        return ArgumentPosition.a();
    }

    @Override
    public Class<IVectorPosition> getArgumentClass() {
        return IVectorPosition.class;
    }

}
