package dev.fabien2s.annoyingapi.interaction.module.input;

import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionTrigger;

@RequiredArgsConstructor
public class InputInterruptInteractionModule implements IInteractionModule {

    private final InteractionTrigger trigger;
    private final InteractionInterruptCause cause;

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {
        if (interactionManager.hasDispatched(trigger))
            interactionManager.stopInteract(cause);
    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
    }
}
