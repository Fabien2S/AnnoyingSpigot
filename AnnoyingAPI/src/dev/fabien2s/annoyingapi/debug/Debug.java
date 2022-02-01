package dev.fabien2s.annoyingapi.debug;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class Debug {

    private static final double DENSITY = 0.5;

    private static final Particle PARTICLE = Particle.REDSTONE;
    private static final Particle.DustOptions DEFAULT_OPTIONS = new Particle.DustOptions(
            Color.WHITE,
            1
    );

    private static final String CHANNEL_ADD_MARKER = "minecraft:debug/game_test_add_marker";
    private static final String CHANNEL_CLEAR_MARKERS = "minecraft:debug/game_test_clear";

    private Debug() {
    }

    public static void drawRay(Player player, Location location, Vector direction, double length) {
        Debug.drawRay(player::spawnParticle, location, direction, length);
    }

    public static void drawRay(World world, Location location, Vector direction, double length) {
        Debug.drawRay(world::spawnParticle, location, direction, length);
    }

    public static void drawRay(Drawable drawable, Location location, Vector direction, double length) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        drawable.draw(Particle.END_ROD, x, y, z, 1, 0, 0, 0, 0, null);

        Vector delta = direction.clone().multiply(DENSITY);
        for (double i = 0; i <= length; i += DENSITY) {
            x += delta.getX();
            y += delta.getY();
            z += delta.getZ();

            drawable.draw(PARTICLE, x, y, z, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
        }

        double offset = length % DENSITY;
        drawable.draw(Particle.END_ROD, x + offset, y + offset, z + offset, 1, 0, 0, 0, 0, null);
    }

    public static void drawBoundingBox(Player player, BoundingBox box) {
        Debug.drawBoundingBox(player::spawnParticle, box);
    }

    public static void drawBoundingBox(Player player, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Debug.drawBoundingBox(player::spawnParticle, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static void drawBoundingBox(World world, BoundingBox box) {
        Debug.drawBoundingBox(world::spawnParticle, box);
    }

    public static void drawBoundingBox(World player, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Debug.drawBoundingBox(player::spawnParticle, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static void drawBoundingBox(Drawable drawable, BoundingBox box) {
        drawBoundingBox(
                drawable,
                box.getMinX(), box.getMinY(), box.getMinZ(),
                box.getMaxX(), box.getMaxY(), box.getMaxZ()
        );
    }

    public static void drawBoundingBox(Drawable drawable, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        for (double x = minX; x < maxX; x += DENSITY) {
            drawable.draw(PARTICLE, x, minY, minZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, x, minY, maxZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, x, maxY, minZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, x, maxY, maxZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
        }

        for (double y = minY; y < maxY; y += DENSITY) {
            drawable.draw(PARTICLE, minX, y, minZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, minX, y, maxZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, maxX, y, minZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, maxX, y, maxZ, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
        }

        for (double z = minZ; z < maxZ; z += DENSITY) {
            drawable.draw(PARTICLE, minX, minY, z, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, minX, maxY, z, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, maxX, minY, z, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
            drawable.draw(PARTICLE, maxX, maxY, z, 1, 0, 0, 0, 0, DEFAULT_OPTIONS);
        }
    }

//    public static void drawMarker(PluginMessageRecipient recipient) {
//        AnnoyingPlugin instance = AnnoyingPlugin.getInstance();
//        recipient.sendPluginMessage(instance, CHANNEL_ADD_MARKER, );
//    }
//
//    public static void clearMarkers(PluginMessageRecipient recipient) {
//        AnnoyingPlugin instance = AnnoyingPlugin.getInstance();
//        recipient.sendPluginMessage(instance, CHANNEL_ADD_MARKER, new byte[0]);
//    }

    private interface Drawable {
        <T> void draw(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data);
    }
}
