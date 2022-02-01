package dev.fabien2s.annoyingapi.math;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
public class WrappedLocation implements IUnsafeEntityLocation {

    private final Location location;

    @Override
    public Location getUnsafeLocation() {
        return location;
    }

    @Override
    public Location getUnsafeEyeLocation() {
        return location;
    }

}
