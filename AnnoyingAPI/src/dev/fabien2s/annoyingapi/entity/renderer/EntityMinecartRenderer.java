package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.entity.Minecart;

public class EntityMinecartRenderer extends EntityRenderer<Minecart, EntityMinecartRenderer> {

    public EntityMinecartRenderer(EntityMinecartRenderer parent, Minecart entity, IEntityController controller) {
        super(parent, entity, controller);
    }

}
