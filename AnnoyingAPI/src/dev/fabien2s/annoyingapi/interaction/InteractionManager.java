package dev.fabien2s.annoyingapi.interaction;

import dev.fabien2s.annoyingapi.interaction.renderer.InteractionRenderer;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;

public class InteractionManager implements ITickable {

    private static final Logger LOGGER = LogManager.getLogger(InteractionManager.class);
    private static final InteractionTrigger[] TRIGGERS = InteractionTrigger.values();

    @Getter private final GamePlayer gamePlayer;

    private final EnumMap<InteractionTrigger, InteractionRegistry> registryMap = new EnumMap<>(InteractionTrigger.class);
    private final EnumMap<InteractionTrigger, Interaction> interactionMap = new EnumMap<>(InteractionTrigger.class);
    private final HashSet<InteractionTrigger> triggeredActions = new HashSet<>();

    @Setter @Nullable private InteractionRenderer renderer;
    @Getter @Nullable private Interaction currentInteraction;

    private int lastRenderMask = Integer.MAX_VALUE;

    public InteractionManager(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void tick(double deltaTime) {
        this.updateAvailableInteractions();

        if (renderer != null)
            this.renderer.tick(deltaTime);
        if (currentInteraction != null)
            this.currentInteraction.handleUpdate(deltaTime);

        this.triggeredActions.clear();
    }

    private void updateAvailableInteractions() {
        int updateMask = 0;
        int renderMask = 0;

        for (InteractionTrigger trigger : TRIGGERS) {
            InteractionRegistry registry = registryMap.get(trigger);
            if (registry == null)
                continue;

            int index = trigger.ordinal();
            int flag = 1 << index;

            Interaction interaction = registry.getInteraction(currentInteraction);
            if (interaction != null) {
                if (interaction.isRendered())
                    renderMask |= flag;

                Interaction previousInteraction = interactionMap.get(trigger);
                if (interaction != previousInteraction) {
                    updateMask |= flag;
                    this.interactionMap.put(trigger, interaction);
                }
            } else if (interactionMap.containsKey(trigger)) {
                updateMask |= flag;
                this.interactionMap.remove(trigger);
            }
        }

        if (currentInteraction != null)
            renderMask = 0;

        if (this.renderer != null && (updateMask != 0 || lastRenderMask != renderMask)) {
            this.lastRenderMask = renderMask;
            if (renderMask == 0)
                this.renderer.clear();
            else
                this.renderer.render(interactionMap, updateMask, renderMask);
        }
    }

    private InteractionRegistry getRegister(InteractionTrigger trigger) {
        return registryMap.computeIfAbsent(trigger, t -> new InteractionRegistry(this));
    }

    public void register(InteractionTrigger trigger, Interaction interaction) {
        InteractionRegistry registry = getRegister(trigger);
        if (registry.register(interaction))
            LOGGER.info("{}: Interaction {} (trigger: {}) registered", gamePlayer, interaction, trigger);
        else
            LOGGER.warn("{}: Unable to register the interaction {} (trigger {})", gamePlayer, interaction, trigger);
    }

    public void unregister(InteractionTrigger trigger, Interaction interaction) {
        InteractionRegistry registry = getRegister(trigger);
        if (registry.unregister(interaction))
            LOGGER.info("{}: Interaction {} (trigger: {}) unregistered", gamePlayer, interaction, trigger);
        else
            LOGGER.warn("{}: Unable to unregister the interaction {} (trigger {})", gamePlayer, interaction, trigger);
    }

    public boolean hasDispatched(InteractionTrigger trigger) {
        return triggeredActions.contains(trigger);
    }

    public void dispatch(InteractionTrigger trigger) {
        if (shouldCancelDispatch(triggeredActions, trigger) || !triggeredActions.add(trigger))
            return;

        LOGGER.info("{}: {} dispatched", gamePlayer, trigger);

        InteractionRegistry registry = registryMap.get(trigger);
        if (registry == null)
            return;

        Interaction interaction = registry.getInteraction(currentInteraction);
        if (interaction == null)
            return;

        this.interact(interaction, trigger);
    }

    public boolean interact(Interaction interaction, InteractionTrigger trigger) {
        if (interaction.isLocked() || !interaction.canInteract(this)) {
            LOGGER.warn("{}: Unable to interact with {}", gamePlayer, interaction);
            return false;
        }

        if (currentInteraction != null)
            this.stopInteract(InteractionInterruptCause.CANCELLED_BY_INTERACTION);

        LOGGER.info("{}: Interaction {} started", gamePlayer, interaction);
        this.currentInteraction = interaction;
        this.currentInteraction.handleEnter(this, trigger);
        return true;
    }

    public void stopInteract(InteractionInterruptCause interruptCause) {
        if (currentInteraction == null)
            return;

        LOGGER.info("{}: Interaction {} ended (cause: {})", gamePlayer, currentInteraction, interruptCause);

        // handle cases where Interaction#onInteractionExit call InteractionManager#interact
        Interaction tmp = this.currentInteraction;
        this.currentInteraction = null;
        tmp.handleExit(interruptCause);
    }

    // this a fix for minecraft being weird
    private static boolean shouldCancelDispatch(Collection<InteractionTrigger> triggers, InteractionTrigger trigger) {
        switch (trigger) {
            case ATTACK:
                if (triggers.contains(InteractionTrigger.USE))
                    return true;
                if (triggers.contains(InteractionTrigger.DROP))
                    return true;
            case USE:
            case DROP:
                if (triggers.contains(InteractionTrigger.ATTACK))
                    return true;
            default:
                return false;
        }
    }

}
