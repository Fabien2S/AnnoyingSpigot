package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.LivingEntity;

public class EntityAgeableRenderer<T extends LivingEntity, U extends EntityLivingRenderer<T, U>> extends EntityLivingRenderer<T, U> {

    public EntityAgeableRenderer(U parent, T entity, IEntityController controller) {
        super(parent, entity, controller);
    }

}
