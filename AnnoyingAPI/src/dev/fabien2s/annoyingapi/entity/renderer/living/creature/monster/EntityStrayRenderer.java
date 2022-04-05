package dev.fabien2s.annoyingapi.entity.renderer.living.creature.monster;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import org.bukkit.entity.Stray;

public class EntityStrayRenderer extends EntityMonsterRenderer<Stray, EntityStrayRenderer> {

    public EntityStrayRenderer(EntityStrayRenderer parent, Stray entity, EntityController controller) {
        super(parent, entity, controller);
    }

}
