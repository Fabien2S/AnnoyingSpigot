package dev.fabien2s.annoyingapi.interaction.constraints;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class PredicateConstraint implements  IInteractionConstraint {

    private final Predicate<InteractionManager> predicate;

    @Override
    public boolean canInteract(InteractionManager interactionManager) {
        return predicate.test(interactionManager);
    }

    @Override
    public double computePriority(InteractionManager interactionManager) {
        return 0;
    }

}
