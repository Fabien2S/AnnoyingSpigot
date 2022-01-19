package dev.fabien2s.annoyingapi.interaction.module;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionTrigger;

@RequiredArgsConstructor
public class ChainInteractionModule implements IInteractionModule {

    private final Interaction interaction;
    private final boolean finished;
    private final boolean userCancelled;
    private final boolean cancelled;

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {

    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {

    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
        InteractionTrigger trigger = interaction.getTrigger();
        switch (cause) {
            case SELF:
            case SELF_BY_USER:
                if(finished)
                    interactionManager.interact(this.interaction, trigger);
                break;
            case CANCELLED_BY_USER:
                if(userCancelled)
                    interactionManager.interact(this.interaction, trigger);
                break;
            case CANCELLED:
            case CANCELLED_BY_GAME:
            case CANCELLED_BY_INTERACTION:
                if(cancelled)
                    interactionManager.interact(this.interaction, trigger);
                break;
        }
    }

}
