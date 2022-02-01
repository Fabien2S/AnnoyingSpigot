package dev.fabien2s.annoyingapi.interaction.constraints;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class PlayerPredicateConstraint<T> implements IInteractionConstraint {

    private final Class<T> tClass;
    private final Predicate<T> predicate;

    @Override
    public boolean canInteract(InteractionManager interactionManager) {
        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        return annoyingPlayer.test(tClass, predicate);
    }

    @Override
    public double computePriority(InteractionManager interactionManager) {
        return 0;
    }

}
