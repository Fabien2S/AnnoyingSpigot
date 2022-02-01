package dev.fabien2s.annoyingapi.adapter.entity;

import com.mojang.datafixers.util.Pair;
import dev.fabien2s.annoyingapi.adapter.player.PlayerController;
import dev.fabien2s.annoyingapi.entity.EntityFlag;
import dev.fabien2s.annoyingapi.entity.EntityPose;
import dev.fabien2s.annoyingapi.entity.controller.IEntityController;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererHelper;
import dev.fabien2s.annoyingapi.entity.tracker.IEntityTracker;
import dev.fabien2s.annoyingapi.util.BitSet;
import dev.fabien2s.annoyingapi.util.HandType;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_18_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityController implements IEntityController {

    public static final int UPDATE_TEAM = 2;

    private static final Scoreboard SCOREBOARD = new Scoreboard();

    private static final DataWatcherObject<Byte> ENTITY_FLAG_OBJECT = new DataWatcherObject<>(0, DataWatcherRegistry.a);
    private static final DataWatcherObject<net.minecraft.world.entity.EntityPose> ENTITY_POSE_OBJECT = new DataWatcherObject<>(6, DataWatcherRegistry.s);
    private static final DataWatcherObject<Byte> LIVING_HAND_STATE_OBJECT = new DataWatcherObject<>(7, DataWatcherRegistry.a);
    private static final DataWatcherObject<Byte> PLAYER_SKIN_OBJECT = new DataWatcherObject<>(16, DataWatcherRegistry.a);
    private static final DataWatcherObject<Boolean> AGEABLE_BABY_OBJECT = new DataWatcherObject<>(15, DataWatcherRegistry.i);

    private final CraftEntity entity;
    private final IEntityTracker tracker;
    private final EntityController parent;

    private final ScoreboardTeam team;
    private final DataWatcherWrapper dataWatcher;

    private boolean teamUpdated;

    public EntityController(CraftEntity entity, IEntityTracker tracker, EntityController parent) {
        this.entity = entity;
        this.tracker = tracker;
        this.parent = parent;

        String teamName = EntityRendererHelper.getTeamName(entity);
        this.team = new ScoreboardTeam(SCOREBOARD, teamName);

        this.dataWatcher = new DataWatcherWrapper();
        this.dataWatcher.set(ENTITY_FLAG_OBJECT, (byte) 0);
        this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.a);

        if (entity instanceof LivingEntity)
            this.dataWatcher.set(LIVING_HAND_STATE_OBJECT, (byte) 0);
        if (entity instanceof Player)
            this.dataWatcher.set(PLAYER_SKIN_OBJECT, (byte) 0);
        if (entity instanceof Ageable)
            this.dataWatcher.set(AGEABLE_BABY_OBJECT, false);

        this.updateTeam(null, null, null);
    }

    @Override
    public void tick(double deltaTime) {
        if (teamUpdated) {
            this.teamUpdated = false;
            Object teamPacket = createTeamPacket();
            this.tracker.broadcastPacket(teamPacket, true);
        }

        if (dataWatcher.isDirty()) {
            int entityId = entity.getEntityId();
            this.tracker.sendPacket(
                    new PacketPlayOutEntityMetadata(entityId, dataWatcher, false),
                    false
            );
        }
    }

    private boolean getFlag(DataWatcherObject<Byte> object, int index) {
        byte flag = dataWatcher.get(object);
        return BitSet.has(flag, 1 << index);
    }

    private void setFlag(DataWatcherObject<Byte> object, int index, boolean value) {
        byte flag = dataWatcher.get(object);
        if (value)
            flag |= (1 << index);
        else
            flag &= ~(1 << index);
        this.dataWatcher.set(object, flag);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void applyEntityOverride(List<T> input, List<T> output) {
        Entity handle = entity.getHandle();
        DataWatcher handleDataWatcher = handle.ai();

        for (int i = 0; i < input.size(); i++) {
            T inputValue = input.get(i);
            DataWatcher.Item<?> inputItem = (DataWatcher.Item<?>) inputValue;
            DataWatcherObject<?> watcherObject = inputItem.a();
            Object entityValue = handleDataWatcher.a(watcherObject);
            DataWatcher.Item<?> convertedItem = convertMetadata(inputItem, entityValue, false);
            output.add(i, (T) convertedItem);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void applyMetadataOverride(List<T> input, List<T> output) {
        for (int i = 0; i < input.size(); i++) {
            T inputValue = input.get(i);

            T outputValue = output.get(i);
            if (outputValue == null) {
                output.add(i, inputValue);
                continue;
            }

            DataWatcher.Item<?> inputItem = (DataWatcher.Item<?>) inputValue;
            DataWatcher.Item<?> outputItem = (DataWatcher.Item<?>) outputValue;
            Object outputItemValue = outputItem.b();
            DataWatcher.Item<?> convertedMetadata = convertMetadata(inputItem, outputItemValue, true);
            output.set(i, (T) convertedMetadata);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T convertMetadata(T current, Object overrideValue, boolean preferOverride) {
        DataWatcher.Item<Object> currentItem = (DataWatcher.Item<Object>) current;
        DataWatcherObject<Object> watcherObject = currentItem.a();

        // apply merging
        if (ENTITY_FLAG_OBJECT.equals(watcherObject))
            return (T) new DataWatcher.Item<>(ENTITY_FLAG_OBJECT, (byte) ((byte) currentItem.b() | (byte) overrideValue));

        return preferOverride ? (T) new DataWatcher.Item<>(watcherObject, overrideValue) : current;
    }

    @Override
    public Object createTeamPacket() {
        return new PacketPlayOutScoreboardTeam(
                team, EntityController.UPDATE_TEAM
        );
    }

    @Override
    public void forceUpdate(PlayerController controller) {
        int entityId = entity.getEntityId();
        controller.sendPacket(new PacketPlayOutEntityMetadata(entityId, dataWatcher, true), true);
    }

    @Override
    public void updateTeam(ChatColor color, Team.OptionStatus nameTagVisibility, Team.OptionStatus collision) {

        EnumChatFormat chatFormat = EnumChatFormat.p;
        ScoreboardTeamBase.EnumNameTagVisibility visibility = ScoreboardTeamBase.EnumNameTagVisibility.a;
        ScoreboardTeamBase.EnumTeamPush collisionRule = ScoreboardTeamBase.EnumTeamPush.a;

        if (parent != null) {
            chatFormat = parent.team.n();
            visibility = parent.team.j();
            collisionRule = parent.team.l();
        }

        if (color != null)
            chatFormat = CraftChatMessage.getColor(color);

        if (nameTagVisibility != null) {
            switch (nameTagVisibility) {
                case ALWAYS:
                    visibility = ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;
                    break;
                case NEVER:
                    visibility = ScoreboardTeamBase.EnumNameTagVisibility.NEVER;
                    break;
                case FOR_OTHER_TEAMS:
                    visibility = ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS;
                    break;
                case FOR_OWN_TEAM:
                    visibility = ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM;
                    break;
            }
        }

        if (collision != null) {
            switch (collision) {
                case ALWAYS:
                    collisionRule = ScoreboardTeamBase.EnumTeamPush.ALWAYS;
                    break;
                case NEVER:
                    collisionRule = ScoreboardTeamBase.EnumTeamPush.NEVER;
                    break;
                case FOR_OTHER_TEAMS:
                    collisionRule = ScoreboardTeamBase.EnumTeamPush.PUSH_OTHER_TEAMS;
                    break;
                case FOR_OWN_TEAM:
                    collisionRule = ScoreboardTeamBase.EnumTeamPush.PUSH_OWN_TEAM;
                    break;
            }
        }

        this.team.setColor(chatFormat);
        this.team.setNameTagVisibility(visibility);
        this.team.setCollisionRule(collisionRule);
        this.teamUpdated = true;
    }

    @Override
    public boolean getFlag(EntityFlag flag) {
        if (!flag.isApplicable(entity))
            return false;

        return switch (flag) {
            case ON_FIRE -> getFlag(ENTITY_FLAG_OBJECT, 0);
            case SNEAKING -> getFlag(ENTITY_FLAG_OBJECT, 1);
            case SPRINTING -> getFlag(ENTITY_FLAG_OBJECT, 3);
            case SWIMMING -> getFlag(ENTITY_FLAG_OBJECT, 4);
            case INVISIBLE -> getFlag(ENTITY_FLAG_OBJECT, 5);
            case GLOWING -> getFlag(ENTITY_FLAG_OBJECT, 6);
            case GLIDING -> getFlag(ENTITY_FLAG_OBJECT, 7);
            case BABY -> dataWatcher.get(AGEABLE_BABY_OBJECT);
        };
    }

    @Override
    public void setFlag(EntityFlag flag, boolean value) {
        if (!flag.isApplicable(entity))
            return;

        switch (flag) {
            case ON_FIRE -> this.setFlag(ENTITY_FLAG_OBJECT, 0, value);
            case SNEAKING -> this.setFlag(ENTITY_FLAG_OBJECT, 1, value);
            case SPRINTING -> this.setFlag(ENTITY_FLAG_OBJECT, 3, value);
            case SWIMMING -> this.setFlag(ENTITY_FLAG_OBJECT, 4, value);
            case INVISIBLE -> this.setFlag(ENTITY_FLAG_OBJECT, 5, value);
            case GLOWING -> this.setFlag(ENTITY_FLAG_OBJECT, 6, value);
            case GLIDING -> this.setFlag(ENTITY_FLAG_OBJECT, 7, value);
            case BABY -> this.dataWatcher.set(AGEABLE_BABY_OBJECT, value);
        }
    }

    @Override
    public EntityPose getPose() {
        net.minecraft.world.entity.EntityPose pose = dataWatcher.get(ENTITY_POSE_OBJECT);
        return switch (pose) {
            case a -> EntityPose.STANDING;
            case b -> EntityPose.FALL_FLYING;
            case c -> EntityPose.SLEEPING;
            case d -> EntityPose.SWIMMING;
            case e -> EntityPose.SPIN_ATTACK;
            case f -> EntityPose.SNEAKING;
            case g -> EntityPose.LONG_JUMPING;
            case h -> EntityPose.DYING;
        };
    }

    @Override
    public void setPose(EntityPose pose) {
        switch (pose) {
            case STANDING -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.a);
            case FALL_FLYING -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.b);
            case SLEEPING -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.c);
            case SWIMMING -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.d);
            case SPIN_ATTACK -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.e);
            case SNEAKING -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.f);
            case LONG_JUMPING -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.g);
            case DYING -> this.dataWatcher.set(ENTITY_POSE_OBJECT, net.minecraft.world.entity.EntityPose.h);
        }
    }

    @Nullable
    @Override
    public HandType getActiveHand() {
        if (getFlag(LIVING_HAND_STATE_OBJECT, 0)) {
            if (getFlag(LIVING_HAND_STATE_OBJECT, 1))
                return HandType.OFF_HAND;
            return HandType.MAIN_HAND;
        }
        return null;
    }

    @Override
    public void setActiveHand(@Nullable HandType activeHand) {
        if (activeHand == null)
            setFlag(LIVING_HAND_STATE_OBJECT, 0, false);
        else {
            setFlag(LIVING_HAND_STATE_OBJECT, 0, true);
            setFlag(LIVING_HAND_STATE_OBJECT, 1, activeHand == HandType.OFF_HAND);
        }
    }

    @Override
    public byte getSkinPartMask() {
        return dataWatcher.get(PLAYER_SKIN_OBJECT);
    }

    @Override
    public void setSkinPartMask(byte skinMask) {
        this.dataWatcher.set(PLAYER_SKIN_OBJECT, skinMask);
    }

    @Override
    public void sendEquipment(@Nullable IEntityTracker tracker, Map<EquipmentSlot, ItemStack> equipmentMap) {
        if (!(entity instanceof CraftLivingEntity))
            return;

        int equipmentCount = equipmentMap.size();
        if (equipmentCount == 0)
            return;

        ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> pairs = new ArrayList<>(equipmentCount);
        for (Map.Entry<EquipmentSlot, ItemStack> entry : equipmentMap.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            EnumItemSlot itemSlot = CraftEquipmentSlot.getNMS(slot);
            ItemStack itemStack = entry.getValue();
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            pairs.add(new Pair<>(itemSlot, nmsItemStack));
        }

        int entityId = entity.getEntityId();
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityId, pairs);
        if (tracker != null)
            tracker.sendPacket(packet, false);
        else
            this.tracker.sendPacket(packet, false);
    }
}
