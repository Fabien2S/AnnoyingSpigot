package dev.fabien2s.annoyingapi.interaction.module;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionTrigger;

@RequiredArgsConstructor
public class RegisterInteractionModule implements IInteractionModule {

    private final InteractionTrigger trigger;
    private final Interaction interaction;

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
        interactionManager.register(trigger, this.interaction);
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {
    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
        interactionManager.unregister(trigger, this.interaction);
    }

}
