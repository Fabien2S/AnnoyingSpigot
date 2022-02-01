package dev.fabien2s.annoyingapi.adapter.packet.in;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.adapter.packet.IPacketHandler;
import dev.fabien2s.annoyingapi.event.player.PlayerJumpAnnoyingEvent;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public class PacketPlayerPositionHandler implements IPacketHandler<PacketPlayInFlying> {

    public static final PacketPlayerPositionHandler INSTANCE = new PacketPlayerPositionHandler();

    @org.jetbrains.annotations.Nullable
    @Override
    public PacketPlayInFlying handle(AnnoyingPlayer player, BukkitScheduler scheduler, PacketPlayInFlying packet) {
        Location unsafeLocation = player.getUnsafeLocation();
        Player spigotPlayer = player.getSpigotPlayer();
        double previousY = unsafeLocation.getY();
        double newY = packet.b(previousY);

        if (spigotPlayer.isOnGround() && !packet.b() && previousY < newY) {
            AnnoyingPlugin plugin = player.getPlugin();
            scheduler.runTask(plugin, () -> {
                Server server = plugin.getServer();
                PluginManager pluginManager = server.getPluginManager();
                PlayerJumpAnnoyingEvent event = new PlayerJumpAnnoyingEvent(player);
                pluginManager.callEvent(event);
            });
        }

        return packet;
    }
}
