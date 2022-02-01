package dev.fabien2s.annoyingapi.interaction.constraints.location;

import dev.fabien2s.annoyingapi.interaction.constraints.IInteractionConstraint;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.math.IUnsafeEntityLocation;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class AngleInteractionConstraint implements IInteractionConstraint {

    private final IUnsafeEntityLocation location;
    private final float anchorToPlayerLimit;
    private final float playerToAnchorLimit;

    @Override
    public boolean canInteract(InteractionManager interactionManager) {
        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        Location anchorLocation = location.getUnsafeLocation();
        Location playerLocation = annoyingPlayer.getUnsafeLocation();
        return VectorHelper.inAngle(anchorLocation, playerLocation, anchorToPlayerLimit) && VectorHelper.inAngle(playerLocation, anchorLocation, playerToAnchorLimit);
    }

    @Override
    public double computePriority(InteractionManager interactionManager) {
        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        Location anchorLocation = location.getUnsafeLocation();
        Location playerLocation = annoyingPlayer.getUnsafeLocation();

        double priority = 0;

        float playerToAnchorAngle = computeAngle(playerLocation, anchorLocation);
        if (playerToAnchorAngle > playerToAnchorLimit)
            return Double.NaN;
        priority += (playerToAnchorLimit - playerToAnchorAngle);

        float anchorToPlayerAngle = computeAngle(anchorLocation, playerLocation);
        if (anchorToPlayerAngle > anchorToPlayerLimit)
            return Double.NaN;
        priority += (anchorToPlayerLimit - anchorToPlayerAngle);

        return priority;
    }

    private float computeAngle(Location point, Location other) {
        Vector direction = VectorHelper.direction(point.getYaw());
        Vector pointToOtherDirection = VectorHelper.direction(point.toVector(), other.toVector());
        return direction.angle(pointToOtherDirection);
    }

}
