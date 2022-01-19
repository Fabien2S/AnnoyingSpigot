package dev.fabien2s.annoyingapi.interaction;

import dev.fabien2s.annoyingapi.interaction.constraints.IInteractionConstraint;
import dev.fabien2s.annoyingapi.interaction.event.InteractionEvent;
import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import dev.fabien2s.annoyingapi.magical.IValueSupplier;
import dev.fabien2s.annoyingapi.magical.MagicalStatic;
import dev.fabien2s.annoyingapi.math.MathHelper;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Interaction {

    @Getter @NotNull private final String name;
    @NotNull private final Set<IInteractionModule> moduleSet;
    @NotNull private final Set<IInteractionConstraint> constraintSet;
    @NotNull private final Set<InteractionEvent> eventSet;
    @NotNull private final Set<String> overrideInteractionSet;

    @Getter private InteractionManager interactionManager;
    @Getter private InteractionTrigger trigger;

    @Getter private IValueSupplier duration = MagicalStatic.ONE;
    @Getter private IValueSupplier actionSpeed = MagicalStatic.ONE;

    private float time;
    private float charge;

    public Interaction(
            @NotNull String name, @NotNull Set<IInteractionModule> moduleSet,
            @NotNull Set<IInteractionConstraint> constraintSet,
            @NotNull Set<InteractionEvent> eventSet, @NotNull Set<String> overrideInteractionSet
    ) {
        this.name = name;
        this.moduleSet = moduleSet;
        this.constraintSet = constraintSet;
        this.eventSet = eventSet;
        this.overrideInteractionSet = overrideInteractionSet;

        for (IInteractionModule module : moduleSet)
            module.onInteractionInit(this);
    }

    private void ensureActive() {
        Validate.notNull(interactionManager, "Interactions is not active");
    }

    final void handleEnter(InteractionManager interactionManager, InteractionTrigger trigger) {
        this.interactionManager = interactionManager;
        this.trigger = trigger;

        for (IInteractionModule module : moduleSet)
            module.onInteractionEnter(this, this.interactionManager);
    }

    final void handleUpdate(double deltaTime) {
        this.ensureActive();

        float lastTime = this.time;
        float lastCharge = this.charge;

        time += deltaTime;
        charge += deltaTime * actionSpeed.getValue();

        this.processEvents(lastTime, lastCharge);

        if (charge < 0 || charge >= duration.getValue())
            this.interactionManager.stopInteract(InteractionInterruptCause.SELF);
        else {
            for (IInteractionModule module : moduleSet) {
                module.onInteractionUpdate(this, interactionManager, deltaTime);
                if (interactionManager == null) // in case where a module called InteractionManager#stopInteract
                    break;
            }
        }
    }

    private void processEvents(float lastTime, float lastCharge) {
        for (InteractionEvent event : eventSet) {
            InteractionSamplingMode mode = event.getMode();

            double eventTime = event.getTime();
            double currentTime = sampleTime(mode);
            double previousTime = sampleTime(lastTime, lastCharge, mode);

            if (currentTime >= eventTime && (previousTime == 0 || previousTime < eventTime)) {
                InteractionEvent.Callback callback = event.getEvent();
                callback.call(interactionManager, currentTime);
            }
        }
    }

    final void handleExit(InteractionInterruptCause interruptCause) {
        this.ensureActive();

        for (IInteractionModule module : moduleSet)
            module.onInteractionExit(this, interactionManager, interruptCause);

        this.resetInteraction();
    }

    private void resetInteraction() {
        this.time = 0;
        this.charge = 0;
        this.actionSpeed = MagicalStatic.ONE;
        this.interactionManager = null;
        this.trigger = null;
    }

    private double sampleTime(float time, float charge, InteractionSamplingMode samplingMode) {
        switch (samplingMode) {
            case NORMALIZED:
                return MathHelper.clamp01(charge / duration.getValue());
            case CHARGE:
                return charge;
            case TIME:
            default:
                return time;
        }
    }

    public boolean isLocked() {
        return interactionManager != null;
    }

    public boolean isDown() {
        return trigger == null || trigger.test(interactionManager.getGamePlayer());
    }

    public boolean isRendered() {
        return true;
    }

    public boolean canInteract(InteractionManager interactionManager) {
        for (IInteractionConstraint constraint : constraintSet) {
            if (constraint.canInteract(interactionManager))
                continue;
            return false;
        }
        return true;
    }

    public double sampleTime(InteractionSamplingMode samplingMode) {
        return sampleTime(this.time, this.charge, samplingMode);
    }

    public Interaction setDuration(IValueSupplier duration) {
        this.duration = duration;
        return this;
    }

    public Interaction setActionSpeed(IValueSupplier actionSpeed) {
        this.actionSpeed = actionSpeed;
        return this;
    }

    public void addEvent(double time, InteractionSamplingMode mode, InteractionEvent.Callback event) {
        this.eventSet.add(new InteractionEvent(time, mode, event));
    }

    public double computePriority(InteractionManager interactionManager) {
        double priority = 0;
        for (IInteractionConstraint constraint : constraintSet)
            priority += constraint.computePriority(interactionManager);
        return priority;
    }

    public boolean canBeOverriddenBy(Interaction interaction) {
        return overrideInteractionSet.contains(interaction.name);
    }

    @Override
    public String toString() {
        return name;
    }

}
