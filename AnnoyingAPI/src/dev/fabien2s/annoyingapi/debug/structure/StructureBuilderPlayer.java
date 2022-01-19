package dev.fabien2s.annoyingapi.debug.structure;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.debug.Debug;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.player.IPlayerControllerProvider;
import dev.fabien2s.annoyingapi.structure.Structure;
import dev.fabien2s.annoyingapi.structure.StructureManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;

public class StructureBuilderPlayer extends GamePlayer {

    private static final double PARTICLE_DELAY = .75;
    private static final DecimalFormat POSITION_FORMAT = new DecimalFormat("#.##");

    private Structure structure;
    private BlockVector structurePosition;
    private BoundingBox structureBox;

    private double particleTime;

    protected StructureBuilderPlayer(AnnoyingPlugin plugin, Player spigotPlayer, IPlayerControllerProvider controllerProvider) {
        super(plugin, new NamespacedKey(plugin, "structure_builder"), spigotPlayer, controllerProvider);
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        this.particleTime += deltaTime;
        if (this.particleTime >= PARTICLE_DELAY) {
            this.particleTime = 0;

            Debug.drawBoundingBox(spigotPlayer, structureBox);

            BoundingBox playerBoundingBox = spigotPlayer.getBoundingBox();

            @Nullable
            Structure.Anchor overlappingAnchor = null;

            Collection<Structure.Anchor> structureAnchors = structure.getAnchors();
            for (Structure.Anchor structureAnchor : structureAnchors) {
                Location anchorLocation = structureAnchor.toLocation().add(structurePosition);
                BoundingBox anchorBoundingBox = BoundingBox.of(anchorLocation, .5, .5, .5);
                Debug.drawBoundingBox(spigotPlayer, anchorBoundingBox);

                if (playerBoundingBox.overlaps(anchorBoundingBox))
                    overlappingAnchor = structureAnchor;

                float yaw = structureAnchor.getYaw();
                float pitch = structureAnchor.getPitch();
                Vector direction = VectorHelper.direction(yaw, pitch);
                Debug.drawRay(spigotPlayer, anchorLocation, direction, 1);
            }

            String structureName = structure.getName();
            Location playerLocation = spigotPlayer.getLocation();
            double relX = playerLocation.getX() - structurePosition.getX();
            double relY = playerLocation.getY() - structurePosition.getY();
            double relZ = playerLocation.getZ() - structurePosition.getZ();

            ComponentBuilder builder = new ComponentBuilder(structureName)
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
                String anchorName = overlappingAnchor.getName();
                String anchorTag = overlappingAnchor.getTag();
                builder.append(" / ")
                        .color(ChatColor.GRAY)
                        .append(anchorName)
                        .color(ChatColor.YELLOW)
                        .append("#")
                        .append(anchorTag);
            }

            this.controller.sendActionBar(builder
                    .create()
            );
        }

    }

    private void synchronizeStructure() {
        World world = spigotPlayer.getWorld();

        int posX = structurePosition.getBlockX();
        int posY = structurePosition.getBlockY();
        int posZ = structurePosition.getBlockZ();

        int sizeX = structure.getSizeX();
        int sizeY = structure.getSizeY();
        int sizeZ = structure.getSizeZ();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    Block block = world.getBlockAt(posX + x, posY + y, posZ + z);
                    BlockData blockData = block.getBlockData();
                    this.structure.setBlock(x, y, z, blockData);
                }
            }
        }
    }

    public void editStructure(Structure structure, BlockVector position) {
        this.structure = structure;
        this.structurePosition = position;
        this.structureBox = BoundingBox.of(
                position,
                position.clone().add(new Vector(
                        structure.getSizeX(),
                        structure.getSizeY(),
                        structure.getSizeZ()
                ))
        );
    }

    public void editStructure(String name, BlockVector position, BlockVector size) {
        try {
            StructureManager structureManager = plugin.getStructureManager();
            NamespacedKey namespacedKey = new NamespacedKey(plugin, name);
            this.structure = structureManager.load(namespacedKey);
        } catch (IOException e) {
            this.structure = new Structure(name, size.getBlockX(), size.getBlockY(), size.getBlockZ());
        }

        this.structurePosition = position;
        this.structureBox = BoundingBox.of(
                position,
                position.clone().add(size)
        );
    }

    public boolean saveStructure() {
        this.synchronizeStructure();

        try {
            String structureName = structure.getName();
            NamespacedKey namespacedKey = new NamespacedKey(plugin, structureName);

            StructureManager structureManager = plugin.getStructureManager();
            return structureManager.save(structure, namespacedKey);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addAnchor(String name, String tag, Location location) {
        Location anchorLocation = location.clone().subtract(structurePosition);
        return structure.addAnchor(name, tag, anchorLocation);
    }

}
