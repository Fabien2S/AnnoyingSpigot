package dev.fabien2s.gannoyingapi.world.object.seeker;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
public class ReachAndDieTarget implements SeekerObject.ITarget {

    private final Location location;

    @Override
    public void onTargetReached(SeekerObject seekerObject) {
        seekerObject.remove();
    }

    @Override
    public Location getUnsafeLocation() {
        return location;
    }

    @Override
    public Location getUnsafeEyeLocation() {
        return location;
    }

}
