package dev.fabien2s.annoyingapi.adapter.player;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.adapter.packet.IPacketHandler;
import dev.fabien2s.annoyingapi.adapter.packet.in.PacketPlayerPositionHandler;
import dev.fabien2s.annoyingapi.adapter.packet.in.PacketTeleportConfirmHandler;
import dev.fabien2s.annoyingapi.adapter.packet.out.PacketEntityEquipmentHandler;
import dev.fabien2s.annoyingapi.adapter.packet.out.PacketEntityMetadataHandler;
import dev.fabien2s.annoyingapi.adapter.packet.out.PacketScoreboardTeamHandler;
import dev.fabien2s.annoyingapi.entity.EntityReference;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import io.netty.channel.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

public class PlayerController extends ChannelDuplexHandler implements IEntityTracker {

    public static final String NAME = "annoying_packet_interceptor";

    private static final Logger LOGGER = LogManager.getLogger(PlayerController.class);

    private final AnnoyingPlayer player;
    private final BukkitScheduler scheduler;

    private final Map<Class<?>, IPacketHandler<?>> inboundHandlerMap = new HashMap<>();
    private final Map<Class<?>, IPacketHandler<?>> outboundHandlerMap = new HashMap<>();

    public PlayerController(AnnoyingPlayer player) {
        this.player = player;

        AnnoyingPlugin plugin = player.getPlugin();
        Server server = plugin.getServer();
        this.scheduler = server.getScheduler();
    }

    public void init() {
        CraftPlayer craftPlayer = (CraftPlayer) player.getSpigotPlayer();
        EntityPlayer nmsPlayer = craftPlayer.getHandle();
        ChannelPipeline pipeline = nmsPlayer.b.a.k.pipeline();
        pipeline.addBefore("packet_handler", NAME, this);

        this.registerOutboundHandler(PacketPlayOutEntityMetadata.class, PacketEntityMetadataHandler.INSTANCE);
        this.registerOutboundHandler(PacketPlayOutEntityEquipment.class, PacketEntityEquipmentHandler.INSTANCE);

        this.registerOutboundHandler(PacketPlayOutScoreboardTeam.class, PacketScoreboardTeamHandler.INSTANCE);

//        this.registerInboundHandler(PacketPlayInItemName.class, PacketRenameItem.INSTANCE);
        this.registerInboundHandler(PacketPlayInFlying.class, PacketPlayerPositionHandler.INSTANCE);
        this.registerInboundHandler(PacketPlayInFlying.PacketPlayInLook.class, PacketPlayerPositionHandler.INSTANCE);
        this.registerInboundHandler(PacketPlayInFlying.PacketPlayInPosition.class, PacketPlayerPositionHandler.INSTANCE);
        this.registerInboundHandler(PacketPlayInFlying.PacketPlayInPositionLook.class, PacketPlayerPositionHandler.INSTANCE);
        this.registerInboundHandler(PacketPlayInTeleportAccept.class, PacketTeleportConfirmHandler.INSTANCE);
    }

    public void reset() {
        CraftPlayer craftPlayer = (CraftPlayer) player.getSpigotPlayer();
        EntityPlayer nmsPlayer = craftPlayer.getHandle();
        ChannelPipeline pipeline = nmsPlayer.b.a.k.pipeline();
        pipeline.remove(NAME);

        this.inboundHandlerMap.clear();
        this.outboundHandlerMap.clear();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Class<?> msgClass = msg.getClass();

        @SuppressWarnings("unchecked")
        IPacketHandler<Object> packetHandler = (IPacketHandler<Object>) inboundHandlerMap.get(msgClass);
        if (packetHandler != null) {
            Object packet = packetHandler.handle(player, scheduler, msg);
            if (packet == null)
                LOGGER.info("Packet handler \"{}\" has interrupted inbound packet", packetHandler);
            else
                super.channelRead(ctx, packet);
        } else
            super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Class<?> msgClass = msg.getClass();

        @SuppressWarnings("unchecked")
        IPacketHandler<Object> packetHandler = (IPacketHandler<Object>) outboundHandlerMap.get(msgClass);

        if (packetHandler != null) {
            Object packet = packetHandler.handle(player, scheduler, msg);
            if (packet == null)
                LOGGER.info("Packet handler \"{}\" has interrupted outbound packet", packetHandler);
            else
                super.write(ctx, packet, promise);
        } else
            super.write(ctx, msg, promise);
    }

    @Override
    public boolean isTracked(Player player) {
        return player == this.player.getSpigotPlayer();
    }

    @Override
    public void sendPacket(Object packet, boolean includeSelf) {
        CraftPlayer player = (CraftPlayer) this.player.getSpigotPlayer();
        EntityPlayer entityPlayer = player.getHandle();
        entityPlayer.b.a((Packet<?>) packet);
    }

    public <T> void registerInboundHandler(Class<? extends T> aClass, IPacketHandler<T> packetHandler) {
        this.inboundHandlerMap.put(aClass, packetHandler);
    }

    public <T> void registerOutboundHandler(Class<? extends T> aClass, IPacketHandler<T> packetHandler) {
        this.outboundHandlerMap.put(aClass, packetHandler);
    }

    public void lookAt(Entity entity, EntityReference playerReference, EntityReference targetReference) {
        CraftEntity craftEntity = (CraftEntity) entity;
        net.minecraft.world.entity.Entity handle = craftEntity.getHandle();
        PacketPlayOutLookAt packet = new PacketPlayOutLookAt(
                playerReference == EntityReference.EYES ? ArgumentAnchor.Anchor.b : ArgumentAnchor.Anchor.a,
                handle,
                targetReference == EntityReference.EYES ? ArgumentAnchor.Anchor.b : ArgumentAnchor.Anchor.a
        );
        this.sendPacket(packet, true);
    }

    public void sendActionBar(BaseComponent[] components) {
        // TODO actionbar
    }
}
