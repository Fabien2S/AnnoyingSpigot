package dev.fabien2s.gannoyingapi.world;

import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.object.IGameObjectRegistrable;
import lombok.Getter;
import dev.fabien2s.annoyingapi.math.IUnsafeEntityLocation;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.sound.ISoundEmitter;
import dev.fabien2s.annoyingapi.statemachine.IState;
import dev.fabien2s.annoyingapi.statemachine.IStateMachine;
import dev.fabien2s.annoyingapi.util.ITickable;
import dev.fabien2s.gannoyingapi.player.ActiveGamePlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class GameObject implements ITickable, IStateMachine<GameObject>, IGameObjectRegistrable, IUnsafeEntityLocation, ISoundEmitter {

    @Getter protected final GameWorld gameWorld;
    @Getter protected final String name;

    protected final Location location;

    @Getter private boolean removed;

    private IState<GameObject> state;

    protected GameObject(GameWorld gameWorld, String name, Location location) {
        this.gameWorld = gameWorld;
        this.name = name;
        this.location = location.clone();

        World world = gameWorld.getSpigotWorld();
        this.location.setWorld(world);
    }

    /**
     * NEVER CALL THIS FUNCTION
     */
    protected void init() {
    }

    /**
     * NEVER CALL THIS FUNCTION
     */
    protected void reset() {
        if (this.state != null)
            this.state.onStateExit(this);
    }

    @Override
    public void tick(double deltaTime) {
        if (state != null)
            this.state.onStateUpdate(this, deltaTime);
    }

    public final void remove() {
        this.removed = true;
    }

    public Location getLocation() {
        return location.clone();
    }

    @Override
    public void setState(@NotNull IState<GameObject> state) {
        IState<GameObject> previousState = this.state;
        if (previousState != null) {
            previousState.onStateExit(this);
            if (previousState instanceof IGameObjectRegistrable) {
                GamePlugin plugin = gameWorld.getPlugin();
                PlayerList playerList = plugin.getPlayerList();
                IGameObjectRegistrable registrable = (IGameObjectRegistrable) previousState;
                playerList.forPlayers(ActiveGamePlayer.class, registrable::unregister);
            }
        }

        this.state = state;
        this.state.onStateEnter(this, previousState);
        if (state instanceof IGameObjectRegistrable) {
            GamePlugin plugin = gameWorld.getPlugin();
            PlayerList playerList = plugin.getPlayerList();
            IGameObjectRegistrable registrable = (IGameObjectRegistrable) state;
            playerList.forPlayers(ActiveGamePlayer.class, registrable::register);
        }
    }

    @Override
    public void register(GamePlayer gamePlayer) {
        if (state instanceof IGameObjectRegistrable)
            ((IGameObjectRegistrable) state).register(gamePlayer);
    }

    @Override
    public void unregister(GamePlayer gamePlayer) {
        if (state instanceof IGameObjectRegistrable)
            ((IGameObjectRegistrable) state).unregister(gamePlayer);
    }

    public boolean is(Class<?> stateClass) {
        return stateClass.isInstance(state);
    }

    @Override
    public void emitSound(@NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        this.gameWorld.playSound(location, sound, category, volume, pitch);
    }

    @Override
    public void emitSound(@NotNull NamespacedKey sound, @NotNull SoundCategory category, float volume, float pitch) {
        this.gameWorld.playSound(location, sound, category, volume, pitch);
    }

    @Override
    public void stopEmittingSound(Sound sound, SoundCategory category) {
        World spigotWorld = gameWorld.getSpigotWorld();
        List<Player> players = spigotWorld.getPlayers();
        for (Player player : players)
            player.stopSound(sound, category);
    }

    @Override
    public void stopEmittingSound(@Nonnull NamespacedKey sound, @Nullable SoundCategory category) {
        World spigotWorld = gameWorld.getSpigotWorld();
        List<Player> players = spigotWorld.getPlayers();
        for (Player player : players)
            player.stopSound(sound.toString(), category);
    }

    @Override
    public Location getUnsafeLocation() {
        return location;
    }

    @Override
    public Location getUnsafeEyeLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "GameObject[" + name + ']';
    }

}
