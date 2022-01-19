package dev.fabien2s.annoyingapi.entity.renderer;

import dev.fabien2s.annoyingapi.entity.EntityAnimation;
import dev.fabien2s.annoyingapi.entity.EntityFlag;
import dev.fabien2s.annoyingapi.entity.EntityPose;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@RequiredArgsConstructor
public abstract class EntityRenderer<T extends Entity, U extends EntityRenderer<T, U>> implements ITickable {

    @Nullable protected final U parent;
    @Nonnull protected final T entity;

    @Getter
    @Nonnull
    protected final IEntityController controller;

    @Nullable private ChatColor color;
    @Nullable private Team.OptionStatus nameTagVisibility;
    @Nullable private Team.OptionStatus collision;

    @Override
    public void tick(double deltaTime) {
        this.controller.tick(deltaTime);
    }

    public void applyTo(U other) {
        other.restoreDefault();

        other.setColor(getColor());
        other.setNameTagVisibility(getNameVisibility());
        other.setCollision(getCollision());

        EntityFlag[] flags = EntityFlag.values();
        for (EntityFlag flag : flags) {
            boolean value = controller.getFlag(flag);
            other.setFlag(flag, value);
        }
    }

    public void forceUpdate(IPlayerController playerController) {
        this.controller.forceUpdate(playerController);
    }

    public <TItem> void mergeMetadata(List<TItem> input, List<TItem> output) {
        if (parent != null)
            this.parent.mergeMetadata(input, output);
        else
            this.controller.applyEntityOverride(input, output);

        this.controller.applyMetadataOverride(input, output);
    }

    public void playAnimation(EntityAnimation animation) {
    }

    public boolean getFlag(EntityFlag flag) {
        return controller.getFlag(flag);
    }

    public void setFlag(EntityFlag flag, boolean value) {
        this.controller.setFlag(flag, value);
    }

    public EntityPose getPose() {
        return controller.getPose();
    }

    public void setPose(@Nonnull EntityPose pose) {
        this.controller.setPose(pose);
    }

    public ChatColor getColor() {
        return color == null && parent != null ? parent.getColor() : color;
    }

    public void setColor(@Nullable ChatColor color) {
        if (this.color != color) {
            this.color = color;
            this.controller.updateTeam(getColor(), getNameVisibility(), getCollision());
        }
    }

    public Team.OptionStatus getNameVisibility() {
        return nameTagVisibility == null && parent != null ? parent.getNameVisibility() : nameTagVisibility;
    }

    public void setNameTagVisibility(@Nullable Team.OptionStatus nameTagVisibility) {
        if (this.nameTagVisibility != nameTagVisibility) {
            this.nameTagVisibility = nameTagVisibility;
            this.controller.updateTeam(getColor(), getNameVisibility(), getCollision());
        }
    }

    public Team.OptionStatus getCollision() {
        return collision == null && parent != null ? parent.getCollision() : collision;
    }

    public void setCollision(@Nullable Team.OptionStatus collision) {
        if (this.collision != collision) {
            this.collision = collision;
            this.controller.updateTeam(getColor(), getNameVisibility(), getCollision());
        }
    }

    public void restoreDefault() {
        this.setColor(null);
        this.setNameTagVisibility(null);
        this.setCollision(null);
    }

    public boolean isValid() {
        return !entity.isDead();
    }

}
