package dev.fabien2s.annoyingapi.adapter;

import com.mojang.brigadier.Message;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnnoyingAdapter {

//    public GameAdapter() {
//        try {
//            Field field = FastReflection.getField(Entity.class, AtomicInteger.class, 0);
//            AtomicInteger entityCount = (AtomicInteger) field.get(null);
//            entityCount.incrementAndGet();
//        } catch (IllegalAccessException ignored) {
//        }
//    }

    public static Message translate(String arg, Object... args) {
        return new ChatMessage(arg, args);
    }

    @Nullable
    public static Entity getEntityByID(World world, int id) {
        CraftWorld craftWorld = (CraftWorld) world;
        WorldServer worldServer = craftWorld.getHandle();
        net.minecraft.world.entity.Entity nmsEntity = worldServer.a(id);
        if (nmsEntity == null) return null;
        return nmsEntity.getBukkitEntity();
    }

}
