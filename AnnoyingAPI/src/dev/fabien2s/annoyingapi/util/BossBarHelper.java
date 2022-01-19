package dev.fabien2s.annoyingapi.util;

import dev.fabien2s.annoyingapi.magical.IValueSupplier;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BossBarHelper {

    public static void setColor(BossBar bar, IValueSupplier duration) {
        setColor(bar, duration, BarColor.WHITE);
    }

    public static void setColor(BossBar bar, IValueSupplier duration, BarColor color) {
        double value = duration.getValue();
        double baseValue = duration.getBaseValue();
        if (value < baseValue)
            bar.setColor(BarColor.YELLOW);
        else if (value > baseValue)
            bar.setColor(BarColor.RED);
        else
            bar.setColor(color);
    }

    public static KeyedBossBar create(NamespacedKey key, GamePlayer gamePlayer) {
        Server server = Bukkit.getServer();
        KeyedBossBar keyedBossBar = server.createBossBar(key, key.toString(), BarColor.WHITE, BarStyle.SOLID);

        Player spigotPlayer = gamePlayer.getSpigotPlayer();
        keyedBossBar.addPlayer(spigotPlayer);

        return keyedBossBar;
    }

    public static void remove(KeyedBossBar bar) {
        bar.removeAll();

        Server server = Bukkit.getServer();
        NamespacedKey namespacedKey = bar.getKey();
        server.removeBossBar(namespacedKey);
    }

}
