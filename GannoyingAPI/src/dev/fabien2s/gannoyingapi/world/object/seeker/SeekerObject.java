package dev.fabien2s.gannoyingapi.world.object.seeker;

import lombok.Getter;
import lombok.Setter;
import dev.fabien2s.annoyingapi.math.IUnsafeEntityLocation;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import dev.fabien2s.gannoyingapi.world.GameObject;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class SeekerObject extends GameObject {

    @Getter @Setter
    @Nullable private ITarget target;

    @Getter @Setter private double speed;

    protected SeekerObject(GameWorld gameWorld, String name, Location location) {
        super(gameWorld, name, location);
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        if (target == null)
            return;

        Location targetLocation = target.getUnsafeEyeLocation();
        double previousDistance = targetLocation.distanceSquared(location);

        Vector seekerToTargetDirection = VectorHelper.direction(location, targetLocation);
        this.location.setDirection(seekerToTargetDirection);

        Vector velocity = seekerToTargetDirection.multiply(speed * deltaTime);
        this.location.add(velocity);

        double currentDistance = targetLocation.distanceSquared(location);
        if (currentDistance >= previousDistance) {
            this.target.onTargetReached(this);
            this.target = null;
        }
    }

    public interface ITarget extends IUnsafeEntityLocation {
        void onTargetReached(SeekerObject seekerObject);
    }

}
