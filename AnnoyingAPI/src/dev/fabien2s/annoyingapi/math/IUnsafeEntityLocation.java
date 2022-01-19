package dev.fabien2s.annoyingapi.math;

import org.bukkit.Location;

public interface IUnsafeEntityLocation {

    default double distanceSqr(IUnsafeEntityLocation unsafeEntityLocation) {
        Location unsafeLocation = getUnsafeLocation();
        Location otherUnsafeLocation = unsafeEntityLocation.getUnsafeLocation();
        return unsafeLocation.distanceSquared(otherUnsafeLocation);
    }

    default double distanceSqr(Location location) {
        Location unsafeLocation = getUnsafeLocation();
        return unsafeLocation.distanceSquared(location);
    }

    /**
     * Return the location of the game player, <b>without cloning it first</b>.
     *
     * @return An unsafe location that should never be modified
     */
    Location getUnsafeLocation();

    /**
     * Return the location of the game player's eyes, <b>without cloning it first</b>.
     *
     * @return An unsafe location that should never be modified
     */
    Location getUnsafeEyeLocation();

}
