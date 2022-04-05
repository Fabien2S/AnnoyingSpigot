package dev.fabien2s.annoyingapi.adapter.packet.out;

import dev.fabien2s.annoyingapi.adapter.packet.IPacketHandler;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererHelper;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.reflection.FastReflection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketEntityMetadataHandler implements IPacketHandler<PacketPlayOutEntityMetadata> {

    public static final PacketEntityMetadataHandler INSTANCE = new PacketEntityMetadataHandler();

    private static final Field ENT_ID_FIELD = FastReflection.getField(PacketPlayOutEntityMetadata.class, int.class, 0);
    private static final Field LIST_FIELD = FastReflection.getField(PacketPlayOutEntityMetadata.class, List.class, 0);

    @Nullable
    @Override
    public PacketPlayOutEntityMetadata handle(AnnoyingPlayer player, BukkitScheduler scheduler, PacketPlayOutEntityMetadata packet) throws IllegalAccessException {
        int entId = ENT_ID_FIELD.getInt(packet);

        // we skip ourself
        Player spigotPlayer = player.getSpigotPlayer();
        int playerId = spigotPlayer.getEntityId();
        if (entId == playerId)
            return packet;

        // we skip when no renderer is found
        EntityRenderer<?, ?> entityRenderer = EntityRendererHelper.getRenderer(player, entId);
        if (entityRenderer == null)
            return packet;

        @SuppressWarnings("unchecked")
        List<DataWatcher.Item<?>> itemList = (List<DataWatcher.Item<?>>) LIST_FIELD.get(packet);
        List<DataWatcher.Item<?>> outputList = new ArrayList<>(itemList.size());

        entityRenderer.mergeMetadata(itemList, outputList);

        DataWatcherWrapper dataWatcherWrapper = new DataWatcherWrapper(outputList);
        return new PacketPlayOutEntityMetadata(entId, dataWatcherWrapper, false);
    }
}
