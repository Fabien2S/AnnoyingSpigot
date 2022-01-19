package dev.fabien2s.annoyingapi.interaction.constraints.location;

import dev.fabien2s.annoyingapi.interaction.constraints.IInteractionConstraint;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.math.IUnsafeEntityLocation;
import dev.fabien2s.annoyingapi.player.GamePlayer;

@RequiredArgsConstructor
public class DistanceInteractionConstraints implements IInteractionConstraint {

    private final IUnsafeEntityLocation location;
    private final double range;

    @Override
    public boolean canInteract(InteractionManager interactionManager) {
        GamePlayer gamePlayer = interactionManager.getGamePlayer();
        return gamePlayer.distanceSqr(location) <= range * range;
    }

    @Override
    public double computePriority(InteractionManager interactionManager) {
        GamePlayer gamePlayer = interactionManager.getGamePlayer();
        double distanceSqr = gamePlayer.distanceSqr(location);
        if (distanceSqr > range * range)
            return Double.NaN;
        return range - Math.sqrt(distanceSqr);
    }

}
