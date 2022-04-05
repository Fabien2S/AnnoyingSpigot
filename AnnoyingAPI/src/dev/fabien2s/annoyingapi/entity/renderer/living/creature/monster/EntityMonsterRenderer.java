package dev.fabien2s.annoyingapi.entity.renderer.living.creature.monster;

import dev.fabien2s.annoyingapi.adapter.entity.EntityController;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRenderer;
import org.bukkit.entity.Monster;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EntityMonsterRenderer<T extends Monster, U extends EntityMonsterRenderer<T, U>> extends EntityRenderer<T, U> {

    public EntityMonsterRenderer(@Nullable U parent, @NotNull T entity, @NotNull EntityController controller) {
        super(parent, entity, controller);
    }

}
