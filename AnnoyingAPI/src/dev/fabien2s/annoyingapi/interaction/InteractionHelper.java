package dev.fabien2s.annoyingapi.interaction;

import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InteractionHelper {

    public static void register(PlayerList playerList, Class<? extends AnnoyingPlayer> playerClass, InteractionTrigger trigger, Interaction interaction) {
        playerList.forPlayers(playerClass, player -> {
            InteractionManager interactionManager = player.getInteractionManager();
            interactionManager.register(trigger, interaction);
        });
    }

    public static void unregister(PlayerList playerList, Class<? extends AnnoyingPlayer> playerClass, InteractionTrigger trigger, Interaction interaction) {
        playerList.forPlayers(playerClass, player -> {
            InteractionManager interactionManager = player.getInteractionManager();
            interactionManager.unregister(trigger, interaction);
        });
    }

}
