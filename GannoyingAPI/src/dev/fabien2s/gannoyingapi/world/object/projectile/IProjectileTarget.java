package dev.fabien2s.gannoyingapi.world.object.projectile;

import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public interface IProjectileTarget {

    boolean isDamageable();

    @Nullable
    Entity getEntity();

}
