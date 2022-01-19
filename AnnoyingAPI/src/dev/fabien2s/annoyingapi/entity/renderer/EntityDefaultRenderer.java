package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.Entity;

public class EntityDefaultRenderer extends EntityRenderer<Entity, EntityDefaultRenderer> {

    public EntityDefaultRenderer(EntityDefaultRenderer parent, Entity entity, IEntityController controller) {
        super(parent, entity, controller);
    }

}
