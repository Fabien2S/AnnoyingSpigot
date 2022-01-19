package dev.fabien2s.annoyingapi.interaction.module;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;

@RequiredArgsConstructor
public class CallbackInteractionModule implements IInteractionModule {

    private final Callback interactionConsumer;
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
        switch (cause) {
            case SELF:
            case SELF_BY_USER:
                if(finished)
                    interactionConsumer.call();
                break;
            case CANCELLED_BY_USER:
                if(userCancelled)
                    interactionConsumer.call();
                break;
            case CANCELLED:
            case CANCELLED_BY_GAME:
            case CANCELLED_BY_INTERACTION:
                if(cancelled)
                    interactionConsumer.call();
                break;
        }
    }

    public interface Callback {
        void call();
    }

}
