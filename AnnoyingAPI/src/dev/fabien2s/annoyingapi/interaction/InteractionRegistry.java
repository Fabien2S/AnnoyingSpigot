package dev.fabien2s.annoyingapi.interaction;

import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.HashSet;

@RequiredArgsConstructor
public class InteractionRegistry {

    private final InteractionManager gamePlayer;
    private final HashSet<Interaction> interactions = new HashSet<>();

    @Nullable
    public Interaction getInteraction(@Nullable Interaction currentInteraction) {
        double highestPriority = -Double.MAX_VALUE;
        Interaction targetedInteraction = null;

        for (Interaction interaction : interactions) {
            if(currentInteraction != null && !currentInteraction.canBeOverriddenBy(interaction))
                continue;

            if (!interaction.isLocked() && interaction.canInteract(gamePlayer)) {
                double interactionPriority = interaction.computePriority(gamePlayer);
                if (interactionPriority > highestPriority) {
                    highestPriority = interactionPriority;
                    targetedInteraction = interaction;
                }
            }
        }

        return targetedInteraction;
    }

    public boolean register(Interaction interaction) {
        return this.interactions.add(interaction);
    }

    public boolean unregister(Interaction interaction) {
        return this.interactions.remove(interaction);
    }

}
