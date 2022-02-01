package dev.fabien2s.annoyingapi.adapter.packet.in;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.adapter.packet.IPacketHandler;
import dev.fabien2s.annoyingapi.event.player.PlayerReadyAnnoyingEvent;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.protocol.game.PacketPlayInTeleportAccept;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketTeleportConfirmHandler implements IPacketHandler<PacketPlayInTeleportAccept> {

    public static final PacketTeleportConfirmHandler INSTANCE = new PacketTeleportConfirmHandler();

    @Nullable
    @Override
    public PacketPlayInTeleportAccept handle(AnnoyingPlayer player, BukkitScheduler scheduler, PacketPlayInTeleportAccept packet) {
        AnnoyingPlugin plugin = player.getPlugin();
        scheduler.runTask(plugin, () -> {
            Server server = plugin.getServer();
            PluginManager pluginManager = server.getPluginManager();
            pluginManager.callEvent(new PlayerReadyAnnoyingEvent(player));
        });
        return packet;
    }
}
