package dev.fabien2s.annoyingapi.adapter.entity;

import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkProviderServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Set;

@RequiredArgsConstructor
public class EntityTracker implements IEntityTracker {

    private final Entity tracked;

    @Nullable
    private PlayerChunkMap.EntityTracker getEntityTracker() {
        int entityId = tracked.ae();

        WorldServer world = (WorldServer) tracked.t;
        ChunkProviderServer chunkProviderServer = world.k();
        PlayerChunkMap playerChunkMap = chunkProviderServer.a;
        return playerChunkMap.I.get(entityId);
    }

    @Override
    public boolean isTracked(Player player) {
        PlayerChunkMap.EntityTracker entityTracker = getEntityTracker();
        if (entityTracker == null)
            return false;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        return entityTracker.f.contains(entityPlayer.b);
    }

    @Override
    public void sendPacket(Object packet, boolean includeSelf) {

        if (includeSelf && tracked instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) this.tracked;
            entityPlayer.b.a((Packet<?>) packet);
        }

        PlayerChunkMap.EntityTracker entityTracker = getEntityTracker();
        if (entityTracker == null)
            return;

        Set<ServerPlayerConnection> trackedPlayers = entityTracker.f;
        for (ServerPlayerConnection entityPlayer : trackedPlayers)
            entityPlayer.a((Packet<?>) packet);
    }

    @Override
    public void broadcastPacket(Object packet, boolean includeSelf) {
        MinecraftServer server = tracked.cB();
        if (server == null)
            return;

        PlayerList playerList = server.ac();
        for (EntityPlayer player : playerList.j) {
            if (!includeSelf && player.equals(tracked))
                continue;
            player.b.a((Packet<?>) packet);
        }
    }

}
