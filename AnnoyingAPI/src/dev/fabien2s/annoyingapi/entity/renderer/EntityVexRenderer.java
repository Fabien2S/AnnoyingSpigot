package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.Vex;

public class EntityVexRenderer extends EntityLivingRenderer<Vex, EntityVexRenderer> {

    public EntityVexRenderer(EntityVexRenderer parent, Vex entity, IEntityController controller) {
        super(parent, entity, controller);
    }

}