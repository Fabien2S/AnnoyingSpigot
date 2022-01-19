package dev.fabien2s.annoyingapi;

import lombok.Getter;
import lombok.Setter;
import dev.fabien2s.annoyingapi.command.CommandManager;
import dev.fabien2s.annoyingapi.debug.structure.CommandStructure;
import dev.fabien2s.annoyingapi.entity.renderer.EntityRendererManager;
import dev.fabien2s.annoyingapi.entity.renderer.ServerEntityRendererManager;
import dev.fabien2s.annoyingapi.gui.GuiManager;
import dev.fabien2s.annoyingapi.listener.PlayerInteractionListener;
import dev.fabien2s.annoyingapi.listener.PlayerRendererListener;
import dev.fabien2s.annoyingapi.npc.NpcManager;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.structure.StructureManager;
import dev.fabien2s.annoyingapi.util.ITickable;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;

public abstract class AnnoyingPlugin extends JavaPlugin implements Runnable, ITickable {

    @Getter private static AnnoyingPlugin instance;

    @Getter protected final PlayerList playerList = new PlayerList(this);

    @Getter protected EntityRendererManager entityRendererManager;
    @Getter protected CommandManager commandManager;
    @Getter protected StructureManager structureManager;
    @Getter protected GuiManager guiManager;
    @Getter protected NpcManager npcManager;

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
        Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
        this.entityRendererManager = new ServerEntityRendererManager(mainScoreboard);

        this.commandManager = new CommandManager(this);
        this.structureManager = new StructureManager(this);
        this.guiManager = new GuiManager(this);
        this.npcManager = new NpcManager(this);

        this.commandManager.registerCommand(CommandStructure::new);

        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(playerList, this);
        pluginManager.registerEvents(guiManager, this);
        pluginManager.registerEvents(new PlayerInteractionListener(playerList), this);
        pluginManager.registerEvents(new PlayerRendererListener(), this);

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
        this.structureManager = null;
        this.guiManager = null;
        this.npcManager = null;
    }

    @Override
    public final void run() {
        double deltaTime = 0.05;
        double scaledDeltaTime = deltaTime * timeScale;

        this.tick(scaledDeltaTime);
        this.playerList.tick(scaledDeltaTime);

        this.entityRendererManager.tick(deltaTime);
        this.npcManager.tick(deltaTime);
    }

    public static NamespacedKey createKey(String key) {
        Validate.notNull(instance, "No game plugin are enabled");
        return new NamespacedKey(instance, key);
    }

    public static File toFileLocation(NamespacedKey path) {
        Validate.notNull(instance, "No game plugin are enabled");
        File dataFolder = instance.getDataFolder();
        String namespace = path.getNamespace();
        String key = path.getKey();
        File file = new File(dataFolder, namespace + '/' + key);

        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs())
            throw new RuntimeException("Unable to create parent directory for " + path);

        return file;
    }

}