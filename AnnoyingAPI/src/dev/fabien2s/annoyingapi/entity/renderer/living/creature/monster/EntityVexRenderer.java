package dev.fabien2s.annoyingapi.entity.renderer.living.creature.monster;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import org.bukkit.entity.Vex;

public class EntityVexRenderer extends EntityMonsterRenderer<Vex, EntityVexRenderer> {

    public EntityVexRenderer(EntityVexRenderer parent, Vex entity, EntityController controller) {
        super(parent, entity, controller);
    }

}