package dev.fabien2s.annoyingapi.entity.renderer.living.creature.animal;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.entity.renderer.living.creature.EntityAgeableRenderer;
import org.bukkit.entity.Cow;

public class EntityCowRenderer extends EntityAgeableRenderer<Cow, EntityCowRenderer> {

    public EntityCowRenderer(EntityCowRenderer parent, Cow entity, EntityController controller) {
        super(parent, entity, controller);
    }

}
