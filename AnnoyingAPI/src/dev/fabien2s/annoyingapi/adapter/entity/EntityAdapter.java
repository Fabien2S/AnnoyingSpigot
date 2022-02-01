package dev.fabien2s.annoyingapi.adapter.entity;

import net.minecraft.server.level.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public class EntityAdapter {

    @Nullable
    public static Entity getById(World world, int id) {
        CraftWorld craftWorld = (CraftWorld) world;
        WorldServer worldServer = craftWorld.getHandle();
        net.minecraft.world.entity.Entity nmsEntity = worldServer.a(id);
        if (nmsEntity == null) return null;
        return nmsEntity.getBukkitEntity();
    }

//    @Override
//    public void setLeashHolder(Entity entity, @Nullable Entity leashHolder) {
//        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
//        WorldServer nmsWorld = (WorldServer) nmsEntity.t;
//        ChunkProviderServer chunkProvider = nmsWorld.k();
//        chunkProvider.broadcast(nmsEntity, new PacketPlayOutAttachEntity(
//                nmsEntity,
//                leashHolder != null ? ((CraftEntity) leashHolder).getHandle() : null
//        ));
//    }

}
