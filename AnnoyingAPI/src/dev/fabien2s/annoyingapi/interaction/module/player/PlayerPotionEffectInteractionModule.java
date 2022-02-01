package dev.fabien2s.annoyingapi.interaction.module.player;

import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.magical.IValueSupplier;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.util.Minecraft;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public class PlayerPotionEffectInteractionModule implements IInteractionModule {

    private final PotionEffectType effectType;
    private final int amplifier;
    private final boolean ambient;
    private final boolean particles;

    private boolean applied;

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
        IValueSupplier duration = interaction.getDuration();
        double durationValue = duration.getValue();
        PotionEffect effect = new PotionEffect(
                effectType,
                (int) Math.ceil(durationValue * Minecraft.TICK_PER_SECOND),
                amplifier, ambient, particles
        );

        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        Player spigotPlayer = annoyingPlayer.getSpigotPlayer();
        this.applied = spigotPlayer.addPotionEffect(effect);
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {
    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
        if (applied) {
            AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
            Player spigotPlayer = annoyingPlayer.getSpigotPlayer();
            spigotPlayer.removePotionEffect(effectType);
        }
    }

}
