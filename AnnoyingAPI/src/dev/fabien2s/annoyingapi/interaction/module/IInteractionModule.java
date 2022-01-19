package dev.fabien2s.annoyingapi.interaction.module;

import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;

public interface IInteractionModule {

    default void onInteractionInit(Interaction interaction) {}

    void onInteractionEnter(Interaction interaction, InteractionManager interactionManager);

    void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime);

    void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause);

}
