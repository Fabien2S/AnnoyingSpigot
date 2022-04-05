package dev.fabien2s.annoyingapi.adapter.packet.out;

import com.mojang.datafixers.util.Pair;
import dev.fabien2s.annoyingapi.adapter.packet.IPacketHandler;
import dev.fabien2s.annoyingapi.entity.renderer.living.EntityLivingRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererHelper;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.reflection.FastReflection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.craftbukkit.v1_18_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketEntityEquipmentHandler implements IPacketHandler<PacketPlayOutEntityEquipment> {

    public static final PacketEntityEquipmentHandler INSTANCE = new PacketEntityEquipmentHandler();

    private static final Field ENT_ID_FIELD = FastReflection.getField(PacketPlayOutEntityEquipment.class, int.class, 0);
    private static final Field EQUIPMENTS_FIELD = FastReflection.getField(PacketPlayOutEntityEquipment.class, List.class, 0);

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public PacketPlayOutEntityEquipment handle(AnnoyingPlayer player, BukkitScheduler scheduler, PacketPlayOutEntityEquipment packet) throws IllegalAccessException {
        int entId = ENT_ID_FIELD.getInt(packet);

        // we skip when no renderer is found
        EntityLivingRenderer<?, ?> entityRenderer = (EntityLivingRenderer<?, ?>) EntityRendererHelper.getRenderer(player, entId);
        if (entityRenderer == null)
            return packet;

        List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = (List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>) EQUIPMENTS_FIELD.get(packet);
        for (int i = 0; i < list.size(); i++) {
            Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair = list.get(i);
            EnumItemSlot slot = pair.getFirst();
            EquipmentSlot equipmentSlot = CraftEquipmentSlot.getSlot(slot);
            ItemStack equipment = entityRenderer.getEquipment(equipmentSlot);
            if (equipment == null)
                continue;

            net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(equipment);
            list.set(i, new Pair<>(slot, nmsCopy));
        }

        return packet;
    }
}
