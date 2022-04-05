package dev.fabien2s.annoyingapi.entity.renderer.living.creature;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.entity.renderer.living.EntityLivingRenderer;
import org.bukkit.entity.Ageable;

public class EntityAgeableRenderer<T extends Ageable, U extends EntityLivingRenderer<T, U>> extends EntityLivingRenderer<T, U> {

    public EntityAgeableRenderer(U parent, T entity, EntityController controller) {
        super(parent, entity, controller);
    }

}
