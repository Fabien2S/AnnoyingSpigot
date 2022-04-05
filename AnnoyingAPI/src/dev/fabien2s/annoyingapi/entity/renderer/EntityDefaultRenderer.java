package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import org.bukkit.entity.Entity;

public class EntityDefaultRenderer extends EntityRenderer<Entity, EntityDefaultRenderer> {

    public EntityDefaultRenderer(EntityDefaultRenderer parent, Entity entity, EntityController controller) {
        super(parent, entity, controller);
    }

}
