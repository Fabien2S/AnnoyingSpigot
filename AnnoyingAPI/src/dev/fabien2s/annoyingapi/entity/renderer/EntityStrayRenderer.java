package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.Stray;

public class EntityStrayRenderer extends EntityLivingRenderer<Stray, EntityStrayRenderer> {

    public EntityStrayRenderer(EntityStrayRenderer parent, Stray entity, IEntityController controller) {
        super(parent, entity, controller);
    }

}
