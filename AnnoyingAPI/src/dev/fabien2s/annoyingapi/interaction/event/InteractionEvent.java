package dev.fabien2s.annoyingapi.interaction.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionSamplingMode;

@RequiredArgsConstructor
public class InteractionEvent {

    @Getter private final double time;
    @Getter private final InteractionSamplingMode mode;
    @Getter private final Callback event;

    public interface Callback {
        void call(InteractionManager interactionManager, double time);
    }

}
