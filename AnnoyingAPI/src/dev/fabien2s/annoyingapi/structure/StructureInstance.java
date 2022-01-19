package dev.fabien2s.annoyingapi.structure;

import dev.fabien2s.annoyingapi.block.BlockHelper;
import dev.fabien2s.annoyingapi.nbt.tag.NbtCompound;
import dev.fabien2s.annoyingapi.util.Minecraft;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.structure.StructureRotation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@ToString
@RequiredArgsConstructor
public class StructureInstance {

    @Getter
    private final int x, y, z;
    @Getter
    private final StructureRotation rotation;
    @Getter
    private final Structure structure;

    private int transformX(int x, int z) {
        switch (rotation) {
            case CLOCKWISE_90:
                return z;
            case CLOCKWISE_180:
                int sizeX = structure.getSizeX();
                return -x + sizeX - 1;
            case COUNTERCLOCKWISE_90:
                int sizeZ = structure.getSizeZ();
                return -z + sizeZ - 1;
            default:
                return x;
        }
    }

    private int transformZ(int x, int z) {
        switch (rotation) {
            case CLOCKWISE_90:
                int sizeX = structure.getSizeX();
                return -x + sizeX - 1;
            case CLOCKWISE_180:
                int sizeZ = structure.getSizeZ();
                return -z + sizeZ - 1;
            case COUNTERCLOCKWISE_90:
                return x;
            default:
                return z;
        }
    }

    private int transformValue(int a, int b) {
        switch (rotation) {
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                return b;
            default:
                return a;
        }
    }

    private Location applyTransform(Structure.Anchor anchor) {
        Location location = anchor.toLocation();

        int sizeX = structure.getSizeX();
        int sizeZ = structure.getSizeZ();

        double halfSizeX = sizeX / 2d;
        double halfSizeZ = sizeZ / 2d;

        double x = location.getX() - halfSizeX;
        double z = location.getZ() - halfSizeZ;
        float yaw = location.getYaw();
        switch (rotation) {
            case CLOCKWISE_90:
                location.setX(-z + halfSizeX);
                location.setZ(x + halfSizeZ);
                yaw += 90;
                break;
            case CLOCKWISE_180:
                location.setX(-x + halfSizeX);
                location.setZ(-z + halfSizeZ);
                yaw += 180;
                break;
            case COUNTERCLOCKWISE_90:
                location.setX(z + halfSizeX);
                location.setZ(-x + halfSizeZ);
                yaw -= 90;
                break;
        }
        location.setYaw(Location.normalizeYaw(yaw));

        return location.add(this.x, this.y, this.z);
    }

    public boolean hasBlock(int x, int y, int z) {
        int transformedX = transformX(x, z);
        int transformedZ = transformZ(x, z);
        return structure.hasBlock(transformedX, y, transformedZ);
    }

    public BlockData getBlock(int x, int y, int z) {
        int transformedX = transformX(x, z);
        int transformedZ = transformZ(x, z);
        BlockData blockData = structure.getBlock(transformedX, y, transformedZ);
        return BlockHelper.rotate(blockData, rotation);
    }

    public Location getAnchor(String name) {
        Structure.Anchor anchor = structure.getAnchor(name);
        if (anchor == null)
            throw new NullPointerException("No anchor with name \"" + name + "\" found");
        return applyTransform(anchor);
    }

    public Collection<AnchorInstance> getAnchorInstances(@Nullable String tag) {
        Collection<AnchorInstance> taggedAnchor = new HashSet<>();

        Map<String, Structure.Anchor> anchorMap = structure.getAnchorMap();
        Collection<Structure.Anchor> anchors = anchorMap.values();
        for (Structure.Anchor anchor : anchors) {
            String anchorTag = anchor.getTag();
            if (tag != null && !anchorTag.equals(tag))
                continue;

            Location anchorLocation = applyTransform(anchor);
            taggedAnchor.add(new AnchorInstance(
                    anchorTag,
                    anchorLocation,
                    anchor.getDataCompound()
            ));
        }

        return taggedAnchor;
    }

    public Collection<Location> getAnchorTransforms(String tag) {
        Collection<Location> taggedAnchor = new HashSet<>();

        Map<String, Structure.Anchor> anchorMap = structure.getAnchorMap();
        Collection<Structure.Anchor> anchors = anchorMap.values();
        for (Structure.Anchor anchor : anchors) {
            String anchorTag = anchor.getTag();
            if (!anchorTag.equals(tag))
                continue;

            Location anchorLocation = applyTransform(anchor);
            taggedAnchor.add(anchorLocation);
        }

        return taggedAnchor;
    }

    public int getSizeX() {
        return transformValue(structure.getSizeX(), structure.getSizeZ());
    }

    public int getSizeY() {
        return structure.getSizeY();
    }

    public int getSizeZ() {
        return transformValue(structure.getSizeZ(), structure.getSizeX());
    }

    public int getChunkSizeX() {
        int chunkOffsetX = Math.abs(x) % Minecraft.CHUNK_SIZE;
        int sizeX = getSizeX();
        return (int) Math.ceil((chunkOffsetX + sizeX) / (double) Minecraft.CHUNK_SIZE);
    }

    public int getChunkSizeZ() {
        int chunkOffsetZ = Math.abs(z) % Minecraft.CHUNK_SIZE;
        int sizeZ = getSizeZ();
        return (int) Math.ceil((chunkOffsetZ + sizeZ) / (double) Minecraft.CHUNK_SIZE);
    }

    @RequiredArgsConstructor
    public static class AnchorInstance {
        @Getter
        private final String tag;
        @Getter
        private final Location location;
        @Getter
        private final NbtCompound dataCompound;
    }

}
