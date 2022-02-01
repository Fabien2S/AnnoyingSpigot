package dev.fabien2s.annoyingapi.adapter.npc;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NPCAdapter {

    public static GameProfile createProfile(String name) {
        byte[] nameBytes = (EntityHuman.bF + name).getBytes(StandardCharsets.UTF_8);
        UUID uuid = UUID.nameUUIDFromBytes(nameBytes);
        return new GameProfile(uuid, name);
    }

    public static HumanEntity spawnNPC(@Nonnull Location location, @Nonnull GameProfile gameProfile) {
        CraftWorld craftWorld = (CraftWorld) location.getWorld();
        if (craftWorld == null)
            throw new NullPointerException("world is null");

        WorldServer worldServer = craftWorld.getHandle();
        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        EntityNPC entityNPC = new EntityNPC(worldServer, position, 0, gameProfile);
        worldServer.addFreshEntity(entityNPC, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return entityNPC.getBukkitEntity();
    }


}
