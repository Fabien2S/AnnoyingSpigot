package dev.fabien2s.annoyingapi.interaction.module.player;

import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.statemachine.IState;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class PlayerStateUpdateInteractionModule implements IInteractionModule {

    private final Supplier<IState<AnnoyingPlayer>> stateSupplier;

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {
    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
        if (cause.isCancelled())
            return;

        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        IState<AnnoyingPlayer> playerState = stateSupplier.get();
        annoyingPlayer.setState(playerState);
    }

}
