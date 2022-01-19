package dev.fabien2s.gannoyingapi.world.object.projectile;

import java.util.Collection;

public interface IProjectileSource<T extends IProjectileTarget> {

    Collection<T> getTargets();

}
