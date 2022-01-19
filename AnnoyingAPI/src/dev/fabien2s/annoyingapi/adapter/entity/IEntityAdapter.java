package dev.fabien2s.annoyingapi.adapter.entity;

import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public interface IEntityAdapter {

    IEntityTracker createTracker(Entity entity);

    IEntityController createController(Entity entity, IEntityTracker tracker, IEntityController parent);

    @Nullable
    Entity getEntity(World world, int id);

    void setLeashHolder(Entity entity, @Nullable Entity leashHolder);

    boolean isInWater(Entity entity, boolean includeBubbleColumn);

}
