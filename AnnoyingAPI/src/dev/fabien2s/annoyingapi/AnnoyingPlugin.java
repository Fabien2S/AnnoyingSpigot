package dev.fabien2s.annoyingapi;

import dev.fabien2s.annoyingapi.command.CommandManager;
import dev.fabien2s.annoyingapi.debug.structure.CommandStructure;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import dev.fabien2s.annoyingapi.entity.renderer.ServerEntityRendererManager;
import dev.fabien2s.annoyingapi.gui.GuiManager;
import dev.fabien2s.annoyingapi.listener.PlayerInteractionListener;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.util.ITickable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.ScoreboardManager;

import javax.annotation.Nullable;
import java.io.File;

public abstract class AnnoyingPlugin extends JavaPlugin implements Runnable, ITickable {

    @Getter
    @Nullable
    private static AnnoyingPlugin instance;

    @Getter
    protected final PlayerList playerList = new PlayerList(this);

    @Getter
    protected EntityRendererManager entityRendererManager;
    @Getter
    protected CommandManager commandManager;
    @Getter
    protected GuiManager guiManager;

    @Getter
    @Setter
    private float timeScale = 1f;

    private BukkitTask task;

    @Override
    public void onEnable() {
        if (instance != null)
            throw new IllegalStateException("Another game plugin is already enabled on this server");

        instance = this;

        super.onEnable();

        File dataFolder = getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs())
            throw new RuntimeException("Unable to create data directory for " + this);

        this.timeScale = 1;

        Server server = getServer();

        ScoreboardManager scoreboardManager = server.getScoreboardManager();
        if (scoreboardManager == null)
            throw new IllegalStateException("ScoreboardManager is null");
        this.entityRendererManager = new ServerEntityRendererManager(scoreboardManager);

        this.commandManager = new CommandManager(this);
        this.guiManager = new GuiManager(this);

        this.commandManager.registerCommand(CommandStructure::new);

        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(playerList, this);
        pluginManager.registerEvents(guiManager, this);
        pluginManager.registerEvents(new PlayerInteractionListener(playerList), this);

        BukkitScheduler scheduler = server.getScheduler();
        this.task = scheduler.runTaskTimer(this, this, 0, 0);
    }

    @Override
    public void onDisable() {
        instance = null;

        super.onDisable();

        this.task.cancel();

        HandlerList.unregisterAll(this);

        this.playerList.resetAll();
        this.entityRendererManager.removeAll();
        this.commandManager.unregisterAll();

        this.entityRendererManager = null;
        this.commandManager = null;
        this.guiManager = null;
    }

    @Override
    public final void run() {
        double deltaTime = 0.05;
        double scaledDeltaTime = deltaTime * timeScale;

        this.tick(scaledDeltaTime);
        this.playerList.tick(scaledDeltaTime);

        this.entityRendererManager.tick(deltaTime);
    }

    public static NamespacedKey createKey(String key) {
        Validate.notNull(instance, "No game plugin are enabled");
        return new NamespacedKey(instance, key);
    }

}
