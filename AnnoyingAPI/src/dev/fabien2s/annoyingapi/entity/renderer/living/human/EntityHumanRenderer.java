package dev.fabien2s.annoyingapi.entity.renderer.living.human;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.entity.renderer.living.EntityLivingRenderer;
import org.bukkit.entity.HumanEntity;

public class EntityHumanRenderer<T extends HumanEntity, U extends EntityHumanRenderer<T, U>> extends EntityLivingRenderer<T, U> {

    public EntityHumanRenderer(U parent, T entity, EntityController controller) {
        super(parent, entity, controller);
    }

}