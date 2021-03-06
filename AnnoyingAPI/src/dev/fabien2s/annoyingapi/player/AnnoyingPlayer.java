package dev.fabien2s.annoyingapi.player;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.adapter.player.PlayerController;
import dev.fabien2s.annoyingapi.entity.renderer.living.human.EntityPlayerRenderer;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import dev.fabien2s.annoyingapi.interaction.InteractionInterruptCause;
import dev.fabien2s.annoyingapi.interaction.InteractionManager;
import dev.fabien2s.annoyingapi.magical.MagicalDouble;
import dev.fabien2s.annoyingapi.math.IUnsafeEntityLocation;
import dev.fabien2s.annoyingapi.sound.ISoundEmitter;
import dev.fabien2s.annoyingapi.statemachine.IState;
import dev.fabien2s.annoyingapi.statemachine.IStateMachine;
import dev.fabien2s.annoyingapi.util.IModifierCollection;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AnnoyingPlayer implements ITickable, IStateMachine<AnnoyingPlayer>, IModifierCollection, IUnsafeEntityLocation, ISoundEmitter {

    private static final Logger LOGGER = LogManager.getLogger(AnnoyingPlayer.class);

    public static final double DEFAULT_WALK_SPEED = .2;
    protected static final int DEFAULT_FOOD_LEVEL = 20;
    protected static final int DEFAULT_MAX_HEALTH = 20;

    public static final double GRAVITY_ACCELERATION = -0.08;

    @Getter protected final AnnoyingPlugin plugin;
    @Getter protected final NamespacedKey roleName;
    @Getter protected final Player spigotPlayer;
    @Getter protected final PlayerController controller;

    @Getter protected final EntityRendererManager entityRendererManager;
    @Getter protected final EntityPlayerRenderer playerRenderer;

    @Getter protected final InteractionManager interactionManager;

    @Getter protected final MagicalDouble walkSpeed = new MagicalDouble(DEFAULT_WALK_SPEED);

    private final Map<NamespacedKey, MagicalDouble> modifierMap = new HashMap<>();

    @Nonnull private Location playerLocation;
    @Nonnull private Location headLocation;
    @Nullable protected IState<AnnoyingPlayer> playerState;

    @Getter private double gravity;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private double idleTime;

    protected AnnoyingPlayer(AnnoyingPlugin plugin, NamespacedKey roleName, Player spigotPlayer) {
        this.plugin = plugin;
        this.roleName = roleName;
        this.spigotPlayer = spigotPlayer;
        this.controller = new PlayerController(this);

        EntityRendererManager entityRendererManager = plugin.getEntityRendererManager();
        this.entityRendererManager = new EntityRendererManager(entityRendererManager, e -> controller);
        this.playerRenderer = (EntityPlayerRenderer) entityRendererManager.getRenderer(spigotPlayer);

        this.interactionManager = new InteractionManager(this);

        this.playerLocation = spigotPlayer.getLocation();
        this.headLocation = spigotPlayer.getEyeLocation();
    }

    public void init() {
        this.controller.init();
    }

    public void reset() {
        this.setState(null);

        this.interactionManager.stopInteract(InteractionInterruptCause.CANCELLED);

        this.walkSpeed.clearModifiers();
        this.walkSpeed.setBaseValue(DEFAULT_WALK_SPEED);
        this.spigotPlayer.setWalkSpeed((float) DEFAULT_WALK_SPEED);
        this.spigotPlayer.setFoodLevel(DEFAULT_FOOD_LEVEL);

        AttributeInstance maxHealthAttribute = spigotPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute != null) maxHealthAttribute.setBaseValue(DEFAULT_MAX_HEALTH);

        this.controller.reset();
        this.playerRenderer.restoreDefault();
        this.entityRendererManager.removeAll();
    }

    @Override
    public void tick(double deltaTime) {
        this.interactionManager.tick(deltaTime);
        this.entityRendererManager.tick(deltaTime);

        this.playerLocation = spigotPlayer.getLocation(playerLocation);
        this.headLocation = spigotPlayer.getLocation(headLocation);
        double eyeHeight = spigotPlayer.getEyeHeight();
        this.headLocation.add(0, eyeHeight, 0);

        this.idleTime += deltaTime;

        // fake gravity on server side
        if (spigotPlayer.isOnGround() || spigotPlayer.isFlying())
            this.gravity = 0;
        else
            this.gravity += GRAVITY_ACCELERATION;

        if (playerState != null)
            this.playerState.onStateUpdate(this, deltaTime);

        if (walkSpeed.isInvalid()) {
            double walkSpeedValue = walkSpeed.getValue();
            this.spigotPlayer.setWalkSpeed((float) walkSpeedValue);
        }
    }

    @Override
    public void emitSound(@NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        World world = spigotPlayer.getWorld();
        world.playSound(spigotPlayer, sound, category, volume, pitch);
    }

    @Override
    public void emitSound(@NotNull @Nonnull NamespacedKey sound, @Nonnull SoundCategory category, float volume, float pitch) {
        World world = spigotPlayer.getWorld();
        world.playSound(playerLocation, sound.toString(), category, volume, pitch);
    }

    @Override
    public void stopEmittingSound(Sound sound, SoundCategory category) {
        PlayerList playerList = plugin.getPlayerList();
        playerList.forEach(p -> {
            Player spigotPlayer = p.getSpigotPlayer();
            if (spigotPlayer.canSee(this.spigotPlayer))
                spigotPlayer.stopSound(sound, category);
        });
    }

    @Override
    public void stopEmittingSound(@Nonnull NamespacedKey sound, @Nullable SoundCategory category) {
        String mcSound = sound.toString();
        PlayerList playerList = plugin.getPlayerList();
        playerList.forEach(p -> {
            Player spigotPlayer = p.getSpigotPlayer();
            if (spigotPlayer.canSee(this.spigotPlayer))
                spigotPlayer.stopSound(mcSound, category);
        });
    }

    @Override
    public void setState(@Nullable IState<AnnoyingPlayer> playerState) {
        LOGGER.info("{}: {} -> {}", this, this.playerState, playerState);

        IState<AnnoyingPlayer> previousState = this.playerState;
        if (previousState != null)
            previousState.onStateExit(this);
        this.playerState = playerState;
        if (playerState != null)
            playerState.onStateEnter(this, previousState);
    }

    @Override
    public MagicalDouble getModifier(NamespacedKey key) {
        return modifierMap.get(key);
    }

    @Override
    public MagicalDouble removeModifier(NamespacedKey key) {
        return modifierMap.remove(key);
    }

    @Override
    public void setModifier(NamespacedKey key, MagicalDouble modifier) {
        this.modifierMap.put(key, modifier);
    }

    public void setVelocityWithGravity(Vector velocity) {
        velocity.setY(this.gravity);
        this.spigotPlayer.setVelocity(velocity);
    }

    public boolean is(Class<?> stateClass) {
        return stateClass.isInstance(playerState);
    }

    public <T> boolean test(Class<T> tClass, Predicate<T> predicate) {
        if (tClass.isInstance(this)) {
            T t = tClass.cast(this);
            return predicate.test(t);
        }

        return false;
    }

    public EntityPlayerRenderer getSelfRenderer() {
        return (EntityPlayerRenderer) entityRendererManager.getRenderer(spigotPlayer);
    }

    public Location getUnsafeLocation() {
        return playerLocation;
    }

    @Override
    public Location getUnsafeEyeLocation() {
        return headLocation;
    }

    public Location getLocation() {
        return playerLocation.clone();
    }

    public BoundingBox getBoundingBox() {
        return spigotPlayer.getBoundingBox();
    }

    @Override
    public int hashCode() {
        return spigotPlayer.hashCode();
    }

    @Override
    public String toString() {
        String playerName = spigotPlayer.getName();
        return playerName + '[' + roleName + ']';
    }
}