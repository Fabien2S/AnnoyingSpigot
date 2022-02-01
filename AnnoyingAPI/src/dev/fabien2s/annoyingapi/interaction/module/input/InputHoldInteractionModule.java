package dev.fabien2s.annoyingapi.interaction.module.input;

import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionTrigger;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;

public class InputHoldInteractionModule implements IInteractionModule {

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {
        InteractionTrigger trigger = interaction.getTrigger();
        if (trigger == null)
            return;

        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        if (trigger.test(annoyingPlayer))
            return;

        interactionManager.stopInteract(InteractionInterruptCause.CANCELLED_BY_USER);
    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
    }

}
