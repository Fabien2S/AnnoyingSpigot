package dev.fabien2s.annoyingapi.interaction.renderer;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionTrigger;
import dev.fabien2s.annoyingapi.util.ITickable;

import java.util.Map;

@RequiredArgsConstructor
public abstract class InteractionRenderer implements ITickable {

    protected final InteractionManager interactionManager;

    public abstract void render(Map<InteractionTrigger, Interaction> interactionMap, int updateMask, int renderMask);
    public abstract void clear();

}
