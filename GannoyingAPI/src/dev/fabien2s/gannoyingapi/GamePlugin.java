package dev.fabien2s.gannoyingapi;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.player.IPlayerProvider;
import dev.fabien2s.annoyingapi.statemachine.IState;
import dev.fabien2s.annoyingapi.statemachine.IStateMachine;
import dev.fabien2s.gannoyingapi.command.CommandGame;
import dev.fabien2s.gannoyingapi.command.CommandLobby;
import dev.fabien2s.gannoyingapi.ingame.InGameState;
import dev.fabien2s.gannoyingapi.ingame.SpectatorPlayer;
import dev.fabien2s.gannoyingapi.lobby.LobbyPlayer;
import dev.fabien2s.gannoyingapi.lobby.state.LobbyInitializeGameState;
import dev.fabien2s.gannoyingapi.lobby.state.LobbyWaitingRoomState;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class GamePlugin extends AnnoyingPlugin implements IStateMachine<GamePlugin> {

    private static final Logger LOGGER = LogManager.getLogger(GamePlugin.class);

    private final GameSettings settings = new GameSettings();
    private final Map<NamespacedKey, IPlayerProvider<?>> playerProviderMap = new HashMap<>();

    private State state;
    private IState<GamePlugin> gameState;

    private World lobbyWorld;

    @Getter
    @Nullable
    private GameWorld gameWorld;

    @Override
    public final void onEnable() {
        super.onEnable();

        Server server = getServer();
        List<World> worlds = server.getWorlds();
        this.lobbyWorld = worlds.get(0);

        this.commandManager.registerCommand(CommandLobby::new);
        this.commandManager.registerCommand(CommandGame::new);

        this.addPlayerProvider(LobbyPlayer.ROLE_NAME, LobbyPlayer::new);
        this.addPlayerProvider(SpectatorPlayer.ROLE_NAME, SpectatorPlayer::new);

        this.onServerInit(settings);
        this.prepareGame();
    }

    @Override
    public final void onDisable() {
        this.stopGame();
        this.deleteGameWorld();
        this.onServerReset();

        if (gameState != null)
            this.gameState.onStateExit(this);

        super.onDisable();
    }

    @Override
    public void tick(double deltaTime) {
        if (gameWorld != null)
            this.gameWorld.tick(deltaTime);
        if (gameState != null)
            this.gameState.onStateUpdate(this, deltaTime);
    }

    @Override
    public void setState(IState<GamePlugin> state) {
        LOGGER.info("{}: {} -> {}", this, this.gameState, state);

        IState<GamePlugin> previousState = this.gameState;
        if (previousState != null) {
            if (previousState instanceof Listener)
                HandlerList.unregisterAll((Listener) previousState);
            previousState.onStateExit(this);
        }

        this.gameState = state;
        if (state instanceof Listener) {
            Server server = getServer();
            PluginManager pluginManager = server.getPluginManager();
            pluginManager.registerEvents((Listener) state, this);
        }
        state.onStateEnter(this, previousState);
    }

    protected abstract void onServerInit(GameSettings settings);

    protected abstract void onServerReset();

    protected abstract World createGameWorld(GameWorld gameWorld, WorldCreator worldCreator);

    protected abstract InGameState createGameState(GameWorld gameWorld);

    protected abstract void onGameStarted(GameWorld gameWorld);

    protected abstract void onGameStopped(GameWorld gameWorld);

    private void deleteGameWorld() {
        if (gameWorld == null)
            return;

        this.gameWorld.reset();

        World spigotWorld = gameWorld.getSpigotWorld();

        List<Player> players = spigotWorld.getPlayers();
        Location spawnLocation = lobbyWorld.getSpawnLocation();
        for (Player player : players)
            player.teleport(spawnLocation);

        Server server = getServer();
        if (!server.unloadWorld(spigotWorld, false))
            throw new IllegalStateException("Unable to unload the game world");

        this.gameWorld = null;
    }

    @SuppressWarnings("unchecked")
    public <T extends AnnoyingPlugin> IPlayerProvider<T> getPlayerProvider(NamespacedKey role) {
        return (IPlayerProvider<T>) playerProviderMap.get(role);
    }

    public Set<NamespacedKey> getPlayerRoles() {
        return playerProviderMap.keySet();
    }

    public void addPlayerProvider(NamespacedKey key, IPlayerProvider<?> playerProvider) {
        this.playerProviderMap.put(key, playerProvider);
    }

    public void prepareGame() {
        this.state = State.WAITING_ROOM;
        this.setState(new LobbyWaitingRoomState(
                lobbyWorld,
                settings.getMinimumPlayers(),
                settings.getMaximumPlayers()
        ));
        this.deleteGameWorld();
    }

    public boolean startGame() {
        if (state != State.WAITING_ROOM)
            return false;

        this.state = State.PREPARE_TO_START;
        this.gameWorld = new GameWorld(this, this::createGameWorld);
        this.setState(new LobbyInitializeGameState(gameWorld));
        return true;
    }

    public void enableGame() {
        Validate.isTrue(state == State.PREPARE_TO_START, "Invalid game state");

        this.state = State.STARTING_GAME;
        InGameState gameState = createGameState(gameWorld);
        this.setState(gameState);
        this.state = State.IN_GAME;
        this.onGameStarted(gameWorld);
    }

    public boolean stopGame() {
        if (state != State.IN_GAME)
            return false;

        this.state = State.STOPPING_GAME;
        this.onGameStopped(gameWorld);

        if (isEnabled())
            this.prepareGame();
        else
            this.deleteGameWorld();

        return true;
    }

    enum State {
        WAITING_ROOM,
        PREPARE_TO_START,
        STARTING_GAME,
        IN_GAME,
        STOPPING_GAME
    }

}
