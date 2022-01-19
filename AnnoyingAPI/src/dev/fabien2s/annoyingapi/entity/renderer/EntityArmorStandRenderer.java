package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.ArmorStand;

public class EntityArmorStandRenderer extends EntityLivingRenderer<ArmorStand, EntityArmorStandRenderer> {

    public EntityArmorStandRenderer(EntityArmorStandRenderer parent, ArmorStand entity, IEntityController controller) {
        super(parent, entity, controller);
    }

}
