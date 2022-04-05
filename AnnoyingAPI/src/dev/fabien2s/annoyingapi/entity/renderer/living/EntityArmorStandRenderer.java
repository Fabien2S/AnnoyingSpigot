package dev.fabien2s.annoyingapi.entity.renderer.living;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import org.bukkit.entity.ArmorStand;

public class EntityArmorStandRenderer extends EntityLivingRenderer<ArmorStand, EntityArmorStandRenderer> {

    public EntityArmorStandRenderer(EntityArmorStandRenderer parent, ArmorStand entity, EntityController controller) {
        super(parent, entity, controller);
    }

}
