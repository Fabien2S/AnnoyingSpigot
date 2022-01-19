package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.Cow;

public class EntityCowRenderer extends EntityAgeableRenderer<Cow, EntityCowRenderer> {

    public EntityCowRenderer(EntityCowRenderer parent, Cow entity, IEntityController controller) {
        super(parent, entity, controller);
    }

}
