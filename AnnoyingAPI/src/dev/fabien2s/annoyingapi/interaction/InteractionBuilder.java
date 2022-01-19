package dev.fabien2s.annoyingapi.interaction;

import dev.fabien2s.annoyingapi.interaction.constraints.IInteractionConstraint;
import dev.fabien2s.annoyingapi.interaction.event.InteractionEvent;
import dev.fabien2s.annoyingapi.interaction.module.IInteractionModule;
import dev.fabien2s.annoyingapi.magical.IValueSupplier;
import dev.fabien2s.annoyingapi.magical.MagicalStatic;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;

@RequiredArgsConstructor
public class InteractionBuilder {

    private final String name;
    private final HashSet<IInteractionModule> moduleSet = new HashSet<>();
    private final HashSet<IInteractionConstraint> constraintSet = new HashSet<>();
    private final HashSet<InteractionEvent> eventSet = new HashSet<>();
    private final HashSet<String> overrideInteractionSet = new HashSet<>();

    private IValueSupplier duration = MagicalStatic.POSITIVE_INFINITY;
    private IValueSupplier actionSpeed = MagicalStatic.ONE;

    public InteractionBuilder withDuration(double duration) {
        this.duration = new MagicalStatic(duration);
        return this;
    }

    public InteractionBuilder withDuration(IValueSupplier duration) {
        this.duration = duration;
        return this;
    }

    public InteractionBuilder withActionSpeed(IValueSupplier actionSpeed) {
        this.actionSpeed = actionSpeed;
        return this;
    }

    public InteractionBuilder addModule(IInteractionModule module) {
        this.moduleSet.add(module);
        return this;
    }

    public InteractionBuilder addConstraint(IInteractionConstraint constraint) {
        this.constraintSet.add(constraint);
        return this;
    }

    public InteractionBuilder addOverride(Interaction interaction) {
        this.overrideInteractionSet.add(interaction.getName());
        return this;
    }

    public InteractionBuilder addOverride(String interaction) {
        this.overrideInteractionSet.add(interaction);
        return this;
    }

    public InteractionBuilder addEvent(double time, InteractionSamplingMode mode, InteractionEvent.Callback event) {
        this.eventSet.add(new InteractionEvent(time, mode, event));
        return this;
    }

    public Interaction build() {
        return new Interaction(name, moduleSet, constraintSet, eventSet, overrideInteractionSet)
                .setDuration(duration)
                .setActionSpeed(actionSpeed);
    }

}
