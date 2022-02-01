package dev.fabien2s.annoyingapi.adapter.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

public class EntityNPC extends EntityHuman {

    public EntityNPC(World world, BlockPosition position, float angle, GameProfile profile) {
        super(world, position, angle, profile);
    }

    @Override
    public boolean B_() {
        return false; // isSpectator
    }

    @Override
    public boolean f() {
        return false; // isCreative
    }


    @Override
    public Packet<?> S() {
        return new PacketPlayOutNamedEntitySpawn(this);
    }
}
