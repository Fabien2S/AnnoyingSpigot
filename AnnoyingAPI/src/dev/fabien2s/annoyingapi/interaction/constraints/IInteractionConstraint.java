package dev.fabien2s.annoyingapi.interaction.constraints;

import dev.fabien2s.annoyingapi.interaction.InteractionManager;

public interface IInteractionConstraint {

    boolean canInteract(InteractionManager interactionManager);

    double computePriority(InteractionManager interactionManager);
    
}
