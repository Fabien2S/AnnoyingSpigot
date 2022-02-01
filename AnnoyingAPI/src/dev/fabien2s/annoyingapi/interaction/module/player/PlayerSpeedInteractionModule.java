package dev.fabien2s.annoyingapi.interaction.module.player;

import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.magical.MagicalDouble;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import org.bukkit.NamespacedKey;

import java.util.function.DoubleSupplier;

@RequiredArgsConstructor
public class PlayerSpeedInteractionModule implements IInteractionModule {

    private final MagicalDouble.Operation operation;
    private final DoubleSupplier valueProvider;

    private NamespacedKey id;

    public PlayerSpeedInteractionModule(MagicalDouble.Operation operation, double value) {
        this.operation = operation;
        this.valueProvider = () -> value;
    }

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
        String name = interaction.getName();
        this.id = AnnoyingPlugin.createKey(name + ".walkSpeed");

        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        MagicalDouble walkSpeed = annoyingPlayer.getWalkSpeed();
        walkSpeed.addModifier(id, operation, valueProvider);
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {
    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        MagicalDouble walkSpeed = annoyingPlayer.getWalkSpeed();
        walkSpeed.removeModifier(id);

        this.id = null;
    }

}
