package dev.fabien2s.annoyingapi.entity.renderer.living.creature;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import org.bukkit.entity.Creature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EntityCreatureRenderer<T extends Creature, U extends EntityCreatureRenderer<T, U>> extends EntityRenderer<T, U> {

    public EntityCreatureRenderer(@Nullable U parent, @NotNull T entity, @NotNull EntityController controller) {
        super(parent, entity, controller);
    }

}
