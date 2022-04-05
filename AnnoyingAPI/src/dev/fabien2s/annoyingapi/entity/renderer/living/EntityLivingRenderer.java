package dev.fabien2s.annoyingapi.entity.renderer.living;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.adapter.player.PlayerController;
import dev.fabien2s.annoyingapi.entity.EntityAnimation;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import dev.fabien2s.annoyingapi.util.HandType;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

public abstract class EntityLivingRenderer<T extends LivingEntity, U extends EntityLivingRenderer<T, U>> extends EntityRenderer<T, U> {

    private final EnumMap<EquipmentSlot, ItemStack> equipmentMap = new EnumMap<>(EquipmentSlot.class);
    private final HashSet<EquipmentSlot> updatedEquipmentSet = new HashSet<>();

    public EntityLivingRenderer(U parent, T entity, EntityController controller) {
        super(parent, entity, controller);
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        if (updatedEquipmentSet.size() > 0) {
            Map<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
            for (EquipmentSlot slot : updatedEquipmentSet)
                map.put(slot, getEquipment(slot));
            this.controller.sendEquipment(null, map);
            this.updatedEquipmentSet.clear();
        }
    }

    @Override
    public void applyTo(U other) {
        super.applyTo(other);

        HandType activeHand = getActiveHand();
        other.setActiveHand(activeHand);

        other.resetEquipments();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack equipment = getEquipment(slot);
            other.setEquipment(slot, equipment);
        }
    }

    @Override
    public void forceUpdate(PlayerController playerController) {
        super.forceUpdate(playerController);
        this.controller.sendEquipment(playerController, equipmentMap);
    }

    @Override
    public void playAnimation(EntityAnimation animation) {
        switch (animation) {
            case SWING_MAIN_HAND -> this.entity.swingMainHand();
            case SWING_OFF_HAND -> this.entity.swingOffHand();
            case HURT -> this.entity.playEffect(EntityEffect.HURT);
            default -> super.playAnimation(animation);
        }
    }

    @Override
    public void restoreDefault() {
        super.restoreDefault();
        this.resetEquipments();
    }

    @Nullable
    public HandType getActiveHand() {
        return controller.getActiveHand();
    }

    public void setActiveHand(@Nullable HandType activeHand) {
        this.controller.setActiveHand(activeHand);
    }

    public Map<EquipmentSlot, ItemStack> getEquipmentMap() {
        return equipmentMap;
    }

    public ItemStack getEquipment(EquipmentSlot slot) {
        ItemStack itemStack = equipmentMap.get(slot);
        if (itemStack != null)
            return itemStack;

        if (parent != null)
            return parent.getEquipment(slot);
        else {
            EntityEquipment entityEquipment = entity.getEquipment();
            if (entityEquipment == null)
                return null;

            switch (slot) {
                case HAND:
                    return entityEquipment.getItemInMainHand();
                case OFF_HAND:
                    return entityEquipment.getItemInOffHand();
                case FEET:
                    return entityEquipment.getBoots();
                case LEGS:
                    return entityEquipment.getLeggings();
                case CHEST:
                    return entityEquipment.getChestplate();
                case HEAD:
                    return entityEquipment.getHelmet();
                default:
                    return null;
            }
        }
    }

    public void setEquipment(EquipmentSlot equipmentSlot, @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            this.resetEquipment(equipmentSlot);
            return;
        }

        ItemStack oldItemStack = equipmentMap.put(equipmentSlot, itemStack);
        if (oldItemStack != itemStack)
            this.updatedEquipmentSet.add(equipmentSlot);
    }

    public void resetEquipment(EquipmentSlot equipmentSlot) {
        ItemStack itemStack = equipmentMap.remove(equipmentSlot);
        if (itemStack != null)
            this.updatedEquipmentSet.add(equipmentSlot);
    }

    public void resetEquipments() {
        this.equipmentMap.forEach((s, i) -> this.updatedEquipmentSet.add(s));
        this.equipmentMap.clear();
    }

}
