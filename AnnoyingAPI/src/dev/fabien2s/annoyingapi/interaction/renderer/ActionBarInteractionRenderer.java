package dev.fabien2s.annoyingapi.interaction.renderer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.interaction.Interaction;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.interaction.InteractionTrigger;
import dev.fabien2s.annoyingapi.player.GamePlayer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ActionBarInteractionRenderer extends InteractionRenderer {

    private static final BaseComponent[] EMPTY = new BaseComponent[0];
    private static final double RENDER_NOW = 2;

    private static final TextComponent INTERACTION_SEPARATOR = new TextComponent("    ");
    private static final TextComponent TRIGGER_PREFIX = new TextComponent("[");
    private static final TextComponent TRIGGER_SUFFIX = new TextComponent("] ");

    static {
        TRIGGER_PREFIX.setColor(ChatColor.DARK_GRAY);
        TRIGGER_SUFFIX.setColor(ChatColor.DARK_GRAY);
    }

    private final BaseComponent[][] cachedComponents = new BaseComponent[InteractionTrigger.values().length][2];

    private BaseComponent[] renderedComponents = new BaseComponent[0];
    private double renderTime = RENDER_NOW;

    public ActionBarInteractionRenderer(InteractionManager interactionManager) {
        super(interactionManager);
    }

    @Override
    public void tick(double deltaTime) {
        this.renderTime += deltaTime;
        if (renderTime < RENDER_NOW)
            return;

        GamePlayer gamePlayer = interactionManager.getGamePlayer();
        IPlayerController playerController = gamePlayer.getController();
        playerController.sendActionBar(renderedComponents);
        this.renderTime = 0;
    }

    @Override
    public void render(Map<InteractionTrigger, Interaction> interactionMap, int updateMask, int renderMask) {
        int interactionCount = Integer.bitCount(renderMask);
        int componentCount = interactionCount * 4 + (interactionCount - 1); // 4 text per interaction + n-1 separator
        this.renderedComponents = new BaseComponent[componentCount];
        this.renderTime = RENDER_NOW;

        int renderIndex = 0;

        Set<Map.Entry<InteractionTrigger, Interaction>> entries = interactionMap.entrySet();
        Iterator<Map.Entry<InteractionTrigger, Interaction>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<InteractionTrigger, Interaction> entry = iterator.next();

            InteractionTrigger trigger = entry.getKey();
            int index = trigger.ordinal();
            int flag = 1 << index;

            if ((renderMask & flag) != flag)
                continue;

            if ((updateMask & flag) == flag) {
                BaseComponent triggerComponent = trigger.getComponent();

                Interaction interaction = entry.getValue();
                String name = interaction.getName();
                BaseComponent interactionComponent = new TextComponent(name);

                cachedComponents[index] = new BaseComponent[]{
                        triggerComponent,
                        interactionComponent
                };
            }

            this.renderedComponents[renderIndex++] = TRIGGER_PREFIX;
            this.renderedComponents[renderIndex++] = cachedComponents[index][0];
            this.renderedComponents[renderIndex++] = TRIGGER_SUFFIX;
            this.renderedComponents[renderIndex++] = cachedComponents[index][1];

            // the next interaction may no be rendered
            if (iterator.hasNext() && renderIndex < this.renderedComponents.length)
                this.renderedComponents[renderIndex++] = INTERACTION_SEPARATOR;
        }
    }

    @Override
    public void clear() {
        this.renderedComponents = EMPTY;
        this.renderTime = Double.NaN;

        GamePlayer gamePlayer = interactionManager.getGamePlayer();
        IPlayerController playerController = gamePlayer.getController();
        playerController.sendActionBar(EMPTY);
    }

}
