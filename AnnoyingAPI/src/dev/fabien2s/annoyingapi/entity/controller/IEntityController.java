package dev.fabien2s.annoyingapi.entity.controller;

import dev.fabien2s.annoyingapi.adapter.player.PlayerController;
import dev.fabien2s.annoyingapi.entity.EntityFlag;
import dev.fabien2s.annoyingapi.entity.EntityPose;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.util.HandType;
import dev.fabien2s.annoyingapi.util.ITickable;
import org.bukkit.ChatColor;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public interface IEntityController extends ITickable {

    <T> void applyEntityOverride(List<T> input, List<T> output);

    <T> void applyMetadataOverride(List<T> input, List<T> output);

    <T> T convertMetadata(T current, Object overrideValue, boolean shouldOverride);

    Object createTeamPacket();

    void forceUpdate(PlayerController controller);

    void updateTeam(@Nullable ChatColor color, @Nullable Team.OptionStatus nameTagVisibility, @Nullable Team.OptionStatus collision);

    boolean getFlag(EntityFlag flag);

    void setFlag(EntityFlag flag, boolean value);

    EntityPose getPose();

    void setPose(EntityPose pose);

    @Nullable
    HandType getActiveHand();

    void setActiveHand(@Nullable HandType activeHand);

    byte getSkinPartMask();

    void setSkinPartMask(byte skinMask);

    void sendEquipment(@Nullable IEntityTracker tracker, Map<EquipmentSlot, ItemStack> equipmentMap);
}
