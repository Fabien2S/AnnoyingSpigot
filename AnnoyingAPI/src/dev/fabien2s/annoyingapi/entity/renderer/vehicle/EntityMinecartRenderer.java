package dev.fabien2s.annoyingapi.entity.renderer.vehicle;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import org.bukkit.entity.Minecart;

public class EntityMinecartRenderer extends EntityRenderer<Minecart, EntityMinecartRenderer> {

    public EntityMinecartRenderer(EntityMinecartRenderer parent, Minecart entity, EntityController controller) {
        super(parent, entity, controller);
    }

}
