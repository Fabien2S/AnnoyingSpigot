package dev.fabien2s.annoyingapi.entity;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public enum EntityFlag {

    ON_FIRE(Entity.class),
    SNEAKING(Entity.class),
    SPRINTING(Entity.class),
    SWIMMING(Entity.class),
    INVISIBLE(Entity.class),
    GLOWING(Entity.class),
    GLIDING(Entity.class),
    BABY(Ageable.class);

    private final Class<?> aClass;

    EntityFlag(Class<?> aClass) {
        this.aClass = aClass;
    }

    public boolean isApplicable(Entity entity) {
        return aClass.isInstance(entity);
    }


}
