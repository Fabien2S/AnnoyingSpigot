package dev.fabien2s.annoyingapi.adapter.player.connection;

import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.util.ITickable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class PlayerConnection extends ChannelDuplexHandler implements Executor, ITickable {

    private static final Logger LOGGER = LogManager.getLogger(PlayerConnection.class);

    public static final String NAME = "packet_interceptor";

    private final GamePlayer gamePlayer;
    private final ChannelPipeline channelPipeline;
    private final Map<Class<?>, IPacketHandler<?>> inboundHandlerMap = new HashMap<>();
    private final Map<Class<?>, IPacketHandler<?>> outboundHandlerMap = new HashMap<>();

    private final Queue<Runnable> pendingTasks = new ArrayDeque<>();

    @Override
    public void tick(double deltaTime) {
        Runnable task;
        while ((task = pendingTasks.poll()) != null)
            task.run();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Class<?> msgClass = msg.getClass();

        IPacketHandler<Object> packetHandler = (IPacketHandler<Object>) inboundHandlerMap.get(msgClass);
        if (packetHandler != null) {
            Object packet = packetHandler.handle(gamePlayer, this, msg);
            if (packet == null)
                LOGGER.info("Packet handler \"{}\" has interrupted inbound packet", packetHandler);
            else
                super.channelRead(ctx, packet);
        } else
            super.channelRead(ctx, msg);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Class<?> msgClass = msg.getClass();
        IPacketHandler<Object> packetHandler = (IPacketHandler<Object>) outboundHandlerMap.get(msgClass);

        if (packetHandler != null) {
            Object packet = packetHandler.handle(gamePlayer, this, msg);
            if (packet == null)
                LOGGER.info("Packet handler \"{}\" has interrupted outbound packet", packetHandler);
            else
                super.write(ctx, packet, promise);
        } else
            super.write(ctx, msg, promise);
    }

    @Override
    public void execute(@NotNull Runnable command) {
        this.pendingTasks.add(command);
    }

    public void register() {
        this.unregister();
        this.channelPipeline.addBefore("packet_handler", PlayerConnection.NAME, this);
    }

    public void unregister() {
        ChannelHandler channelHandler = channelPipeline.get(PlayerConnection.NAME);
        if (channelHandler != null)
            this.channelPipeline.remove(PlayerConnection.NAME);
    }

    public <T> void registerInboundHandler(Class<? extends T> aClass, IPacketHandler<T> packetHandler) {
        this.inboundHandlerMap.put(aClass, packetHandler);
    }

    public <T> void registerOutboundHandler(Class<? extends T> aClass, IPacketHandler<T> packetHandler) {
        this.outboundHandlerMap.put(aClass, packetHandler);
    }

}
