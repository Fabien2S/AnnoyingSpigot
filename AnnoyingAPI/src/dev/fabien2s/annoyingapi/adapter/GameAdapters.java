package dev.fabien2s.annoyingapi.adapter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameAdapters {

    private static final Logger LOGGER = LogManager.getLogger(GameAdapters.class);

    private static final int PREFIX_LENGTH = "org.bukkit.craftbukkit.".length();

    public static final String VERSION;
    public static final IGameAdapter INSTANCE;

    static {
        Server server = Bukkit.getServer();
        Class<? extends Server> serverClass = server.getClass();
        String packageName = serverClass.getPackageName();
        VERSION = packageName.substring(PREFIX_LENGTH);
        INSTANCE = createAdapter();

        LOGGER.info("Using game adapter {} ({})", INSTANCE, VERSION);
    }

    private static IGameAdapter createAdapter() {
        try {
            Class<?> adapterClass = Class.forName("dev.fabien2s.annoyingapi." + GameAdapters.VERSION + ".GameAdapter");
            Constructor<?> declaredConstructor = adapterClass.getDeclaredConstructor();
            Object gameAdapter = declaredConstructor.newInstance();
            return (IGameAdapter) gameAdapter;
        } catch (ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("No game adapter found for version " + VERSION);
        }
    }

}
