package dev.fabien2s.annoyingapi.interaction.module;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionSamplingMode;
import dev.fabien2s.annoyingapi.magical.IValueSupplier;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.util.BossBarHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.KeyedBossBar;

public class BossBarInteractionModule implements IInteractionModule {

    private KeyedBossBar bossBar;

    @Override
    public void onInteractionEnter(Interaction interaction, InteractionManager interactionManager) {
        String name = interaction.getName();
        NamespacedKey key = AnnoyingPlugin.createKey(name);
        AnnoyingPlayer annoyingPlayer = interactionManager.getAnnoyingPlayer();
        this.bossBar = BossBarHelper.create(key, annoyingPlayer);
    }

    @Override
    public void onInteractionUpdate(Interaction interaction, InteractionManager interactionManager, double deltaTime) {
        double normalizedTime = interaction.sampleTime(InteractionSamplingMode.NORMALIZED);
        this.bossBar.setProgress(normalizedTime);

        IValueSupplier actionSpeed = interaction.getActionSpeed();
        double actionSpeedValue = actionSpeed.getValue();
        if (actionSpeedValue < 1)
            this.bossBar.setColor(BarColor.RED);
        else if (actionSpeedValue > 1)
            this.bossBar.setColor(BarColor.YELLOW);
        else
            this.bossBar.setColor(BarColor.WHITE);
    }

    @Override
    public void onInteractionExit(Interaction interaction, InteractionManager interactionManager, InteractionInterruptCause cause) {
        BossBarHelper.remove(bossBar);
        this.bossBar = null;
    }

}
