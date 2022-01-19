package dev.fabien2s.annoyingapi.interaction.module.player;

import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

@RequiredArgsConstructor
public class PlayerSoundEffectInteractionModule implements IInteractionModule {

    private final Sound sound;
    private final SoundCategory category;
    private final boolean stopOnExit;

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
        GamePlayer gamePlayer = interactionManager.getGamePlayer();
        gamePlayer.emitSound(sound, category);
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {

    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
        if (stopOnExit) {
            GamePlayer gamePlayer = interactionManager.getGamePlayer();
            gamePlayer.stopEmittingSound(sound, category);
        }
    }

}
