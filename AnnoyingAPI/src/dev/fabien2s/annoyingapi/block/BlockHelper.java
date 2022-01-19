package dev.fabien2s.annoyingapi.block;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.util.Vector;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockHelper {

    public static final BlockFace[] CLOCKWISE_AXIS = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

    private static final BlockFace[] CLOCKWISE_BLOCK_FACES = {
            BlockFace.NORTH,
            BlockFace.NORTH_NORTH_EAST,
            BlockFace.NORTH_EAST,
            BlockFace.EAST_NORTH_EAST,

            BlockFace.EAST,
            BlockFace.EAST_SOUTH_EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_SOUTH_EAST,

            BlockFace.SOUTH,
            BlockFace.SOUTH_SOUTH_WEST,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST_SOUTH_WEST,

            BlockFace.WEST,
            BlockFace.WEST_NORTH_WEST,
            BlockFace.NORTH_WEST,
            BlockFace.NORTH_NORTH_WEST
    };

    public static BlockFace getFace(float yaw) {
        return CLOCKWISE_AXIS[Math.round((yaw + 180) / 90f) % CLOCKWISE_AXIS.length];
    }

    private static int rotationToOffset(StructureRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90:
                return CLOCKWISE_BLOCK_FACES.length / 4;
            case CLOCKWISE_180:
                return CLOCKWISE_BLOCK_FACES.length / 2;
            case COUNTERCLOCKWISE_90:
                return -(CLOCKWISE_BLOCK_FACES.length / 4);
            default:
                return 0;
        }
    }

    public static BlockFace rotate(BlockFace blockFace, StructureRotation rotation) {
        if (blockFace == BlockFace.SELF || blockFace == BlockFace.UP || blockFace == BlockFace.DOWN)
            return blockFace;

        int blockFaceCount = CLOCKWISE_BLOCK_FACES.length;
        for (int i = 0; i < blockFaceCount; i++) {
            if (CLOCKWISE_BLOCK_FACES[i] != blockFace)
                continue;

            int indexOffset = rotationToOffset(rotation);
            int index = (i + indexOffset) % blockFaceCount;
            return CLOCKWISE_BLOCK_FACES[index < 0 ? blockFaceCount + index : index];
        }

        return blockFace;
    }

    public static BlockFace rotate(Set<BlockFace> allowedBlockFaces, BlockFace blockFace, StructureRotation rotation) {
        BlockFace rotatedBlockFace = rotate(blockFace, rotation);
        return allowedBlockFaces.contains(rotatedBlockFace) ? rotatedBlockFace : blockFace;
    }

    public static BlockData rotate(BlockData blockData, StructureRotation rotation) {
        if (rotation == StructureRotation.NONE)
            return blockData;

        if (blockData instanceof Rotatable) {
            Rotatable rotatable = (Rotatable) blockData;
            BlockFace blockFace = rotatable.getRotation();
            BlockFace rotatedBlockFace = BlockHelper.rotate(blockFace, rotation);
            rotatable.setRotation(rotatedBlockFace);
        }

        if (blockData instanceof Directional) {
            Directional directional = (Directional) blockData;
            Set<BlockFace> allowedFaces = directional.getFaces();
            BlockFace blockFace = directional.getFacing();
            BlockFace rotatedBlockFace = BlockHelper.rotate(allowedFaces, blockFace, rotation);
            directional.setFacing(rotatedBlockFace);
        }

        if (blockData instanceof MultipleFacing) {
            MultipleFacing multipleFacing = (MultipleFacing) blockData;

            Set<BlockFace> activeFaces = multipleFacing.getFaces();

            for (BlockFace blockFace : activeFaces)
                multipleFacing.setFace(blockFace, false);

            Set<BlockFace> allowedFaces = multipleFacing.getAllowedFaces();
            for (BlockFace blockFace : activeFaces) {
                BlockFace rotatedBlockFace = BlockHelper.rotate(allowedFaces, blockFace, rotation);
                multipleFacing.setFace(rotatedBlockFace, true);
            }
        }

        if (blockData instanceof Orientable) {
            Orientable orientable = (Orientable) blockData;
            Axis axis = orientable.getAxis();
            if (axis != Axis.Y) {
                switch (rotation) {
                    case CLOCKWISE_90:
                    case COUNTERCLOCKWISE_90:
                        orientable.setAxis(axis == Axis.X ? Axis.Z : Axis.X);
                }
            }
        }

        return blockData;
    }

    public static void spawnParticle(BlockData blockData, Location location) {
        World world = location.getWorld();
        if (world == null)
            return;

        world.spawnParticle(Particle.BLOCK_CRACK, location, 8, .1, .1, .1, blockData);
    }

    public static void playHitEffect(Block block, Vector position) {
        World world = block.getWorld();
        Location location = position.toLocation(world);
        BlockData blockData = block.getBlockData();

        BlockHelper.spawnParticle(blockData, location);

        if (blockData instanceof Lightable) {
            ((Lightable) blockData).setLit(false);
            block.setBlockData(blockData, false);
        }
    }

}
