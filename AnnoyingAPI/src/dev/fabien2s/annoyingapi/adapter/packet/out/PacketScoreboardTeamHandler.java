package dev.fabien2s.annoyingapi.adapter.packet.out;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.adapter.packet.IPacketHandler;
import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererHelper;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.reflection.FastReflection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketScoreboardTeamHandler implements IPacketHandler<PacketPlayOutScoreboardTeam> {

    public static final PacketScoreboardTeamHandler INSTANCE = new PacketScoreboardTeamHandler();

    private static final Field TEAM_ACTION_FIELD = FastReflection.getField(PacketPlayOutScoreboardTeam.class, int.class, 0);
    private static final Field TEAM_NAME_FIELD = FastReflection.getField(PacketPlayOutScoreboardTeam.class, String.class, 0);

    @Nullable
    @Override
    public PacketPlayOutScoreboardTeam handle(AnnoyingPlayer player, BukkitScheduler scheduler, PacketPlayOutScoreboardTeam packet) throws IllegalAccessException {

        // We are only interested in team update packet
        int teamAction = TEAM_ACTION_FIELD.getInt(packet);
        if (teamAction != EntityController.UPDATE_TEAM)
            return packet;

        // We check whether the team is an annoying team
        String teamName = (String) TEAM_NAME_FIELD.get(packet);
        if (!teamName.startsWith(EntityRendererHelper.TEAM_PREFIX))
            return packet;

        int startIndex = EntityRendererHelper.TEAM_PREFIX.length();
        int endIndex = teamName.length();
        int entityId = Integer.parseInt(teamName, startIndex, endIndex, 10);

        EntityRenderer<?, ?> entityRenderer = EntityRendererHelper.getRenderer(player, entityId);
        if (entityRenderer == null)
            return packet;

        IEntityController controller = entityRenderer.getController();
        return (PacketPlayOutScoreboardTeam) controller.createTeamPacket();
    }

}
