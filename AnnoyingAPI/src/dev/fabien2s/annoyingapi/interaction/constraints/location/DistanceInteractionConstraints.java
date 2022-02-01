package dev.fabien2s.annoyingapi.interaction.constraints.location;

import dev.fabien2s.annoyingapi.interaction.constraints.IInteractionConstraint;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.math.IUnsafeEntityLocation;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;

@RequiredArgsConstructor
public class DistanceInteractionConstraints implements IInteractionConstraint {

    private final IUnsafeEntityLocation location;
    private final double range;

    @Override
    public boolean canInteract(InteractionManager interactionManager) {
        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        return annoyingPlayer.distanceSqr(location) <= range * range;
    }

    @Override
    public double computePriority(InteractionManager interactionManager) {
        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        double distanceSqr = annoyingPlayer.distanceSqr(location);
        if (distanceSqr > range * range)
            return Double.NaN;
        return range - Math.sqrt(distanceSqr);
    }

}
