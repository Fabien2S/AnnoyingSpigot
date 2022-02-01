package dev.fabien2s.annoyingapi.adapter.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.commands.arguments.coordinates.IVectorPosition;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.util.Vector;

public class VectorConverter implements ICommandArgumentConverter<CommandListenerWrapper, Vector, IVectorPosition> {

    @Override
    public Vector convert(CommandContext<CommandListenerWrapper> context, Class<Vector> type, IVectorPosition o) {
        Vec3D vec3D = o.a(context.getSource());
        return new Vector(vec3D.b, vec3D.c, vec3D.d);
    }

    @Override
    public ArgumentType<IVectorPosition> getArgumentType() {
        return ArgumentVec3.a();
    }

    @Override
    public Class<IVectorPosition> getArgumentClass() {
        return IVectorPosition.class;
    }

}
