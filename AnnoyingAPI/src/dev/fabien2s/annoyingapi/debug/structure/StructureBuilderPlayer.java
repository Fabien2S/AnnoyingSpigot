package dev.fabien2s.annoyingapi.debug.structure;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.debug.Debug;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.annoyingapi.player.AnnoyingPlayer;
import dev.fabien2s.annoyingapi.structure.StructureAnchor;
import dev.fabien2s.annoyingapi.structure.StructureAnchorManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.DecimalFormat;

public class StructureBuilderPlayer extends AnnoyingPlayer {

    private static final double PARTICLE_DELAY = .75;
    private static final DecimalFormat POSITION_FORMAT = new DecimalFormat("#.##");

    private Structure structure;
    private StructureAnchor[] anchors;
    private NamespacedKey structureName;
    private Location structurePosition;

    private double particleTime;

    protected StructureBuilderPlayer(AnnoyingPlugin plugin, Player spigotPlayer) {
        super(plugin, new NamespacedKey(plugin, "structure_builder"), spigotPlayer);
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        this.particleTime += deltaTime;
        if (this.particleTime >= PARTICLE_DELAY) {
            this.particleTime = 0;

            BlockVector structureSize = structure.getSize();
            Debug.drawBoundingBox(
                    spigotPlayer,
                    structurePosition.getBlockX(),
                    structurePosition.getBlockY(),
                    structurePosition.getBlockZ(),
                    structureSize.getBlockX(),
                    structureSize.getBlockY(),
                    structureSize.getBlockZ()
            );

            BoundingBox playerBoundingBox = spigotPlayer.getBoundingBox();

            @Nullable
            StructureAnchor overlappingAnchor = null;

            for (StructureAnchor structureAnchor : anchors) {
                Location location = structureAnchor.location();
                BoundingBox anchorBoundingBox = BoundingBox.of(location, .5, .5, .5);
                Debug.drawBoundingBox(spigotPlayer, anchorBoundingBox);

                if (playerBoundingBox.overlaps(anchorBoundingBox))
                    overlappingAnchor = structureAnchor;

                float yaw = location.getYaw();
                float pitch = location.getPitch();
                Vector direction = VectorHelper.direction(yaw, pitch);
                Debug.drawRay(spigotPlayer, location, direction, 1);
            }

            Location playerLocation = spigotPlayer.getLocation();
            double relX = playerLocation.getX() - structurePosition.getX();
            double relY = playerLocation.getY() - structurePosition.getY();
            double relZ = playerLocation.getZ() - structurePosition.getZ();

            ComponentBuilder builder = new ComponentBuilder(structureName.toString())
                    .color(ChatColor.BLUE)
                    .append(" - ")
                    .color(ChatColor.GRAY)
                    .append(POSITION_FORMAT.format(relX))
                    .color(ChatColor.GREEN)
                    .append(" / ")
                    .color(ChatColor.GRAY)
                    .append(POSITION_FORMAT.format(relY))
                    .color(ChatColor.GREEN)
                    .append(" / ")
                    .color(ChatColor.GRAY)
                    .append(POSITION_FORMAT.format(relZ))
                    .color(ChatColor.GREEN);

            if (overlappingAnchor != null) {
                String anchorName = overlappingAnchor.name();
                String anchorTag = overlappingAnchor.tag();
                builder.append(" / ")
                        .color(ChatColor.GRAY)
                        .append(anchorName)
                        .color(ChatColor.YELLOW)
                        .append("#")
                        .append(anchorTag);
            }

            this.spigotPlayer.sendTitle();
            this.controller.sendActionBar(builder
                    .create()
            );
        }

    }

    private void synchronizeStructure() {
        BlockVector structureSize = structure.getSize();
        this.structure.fill(structurePosition, structureSize, true);
    }

    public void editStructure(NamespacedKey structureName, Location location) {
        Server server = plugin.getServer();
        StructureManager structureManager = server.getStructureManager();
        Structure structure = structureManager.loadStructure(structureName, false);
        editStructure(structureName, structure, location);
    }

    public void editStructure(NamespacedKey structureName, @Nullable Structure structure, Location location) {
        this.structureName = structureName;

        if (structure == null) {
            Server server = plugin.getServer();
            StructureManager structureManager = server.getStructureManager();
            this.structure = structureManager.createStructure();
            this.anchors = new StructureAnchor[0];
        } else {
            this.structure = structure;
            this.anchors = StructureAnchorManager.loadAnchors(structure);
        }
        this.structurePosition = location;
    }

    public boolean saveStructure() {
        this.synchronizeStructure();

        try {
            Server server = plugin.getServer();
            StructureManager structureManager = server.getStructureManager();
            structureManager.saveStructure(structureName, structure);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean addAnchor(String name, String tag, Location location) {
        Location anchorLocation = location.clone().subtract(structurePosition);
        return structure.addAnchor(name, tag, anchorLocation);
    }

}
