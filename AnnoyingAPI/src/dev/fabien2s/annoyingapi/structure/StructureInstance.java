package dev.fabien2s.annoyingapi.structure;

import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.Objects;

public record StructureInstance(
        Structure structure,
        int x, int y, int z,
        StructureRotation rotation,
        Mirror mirror,
        StructureAnchor[] anchors
) {

    private void transform(Vector vector) {
        BlockVector structureSize = structure.getSize();
        double halfSizeX = structureSize.getBlockX() / 2d;
        double halfSizeZ = structureSize.getBlockZ() / 2d;

        switch (rotation) {
            case NONE -> {
            }
            case CLOCKWISE_90 -> {
            }
            case CLOCKWISE_180 -> {
            }
            case COUNTERCLOCKWISE_90 -> {
            }
        }
    }

    public void place(Location location, boolean includeEntities) {
        this.structure.place(location, includeEntities, rotation, Mirror.NONE, 1, 0, );
    }


    private int transformX(int x, int z) {
        BlockVector size = structure.getSize();
        return switch (rotation) {
            case CLOCKWISE_90 -> z;
            case CLOCKWISE_180 -> -x + size.getBlockX() - 1;
            case COUNTERCLOCKWISE_90 -> -z + size.getBlockZ() - 1;
            default -> x;
        };
    }

    private int transformZ(int x, int z) {
        BlockVector size = structure.getSize();
        return switch (rotation) {
            case CLOCKWISE_90 -> -x + size.getBlockX() - 1;
            case CLOCKWISE_180 -> -z + size.getBlockZ() - 1;
            case COUNTERCLOCKWISE_90 -> x;
            default -> z;
        };
    }

    private int transformValue(int a, int b) {
        return switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> b;
            default -> a;
        };
    }

    private Location applyTransform(StructureAnchor anchor) {
        Location location = anchor.location();

        BlockVector size = structure.getSize();
        double halfSizeX = size.getBlockX() / 2d;
        double halfSizeZ = size.getBlockZ() / 2d;

        double x = location.getX() - halfSizeX;
        double z = location.getZ() - halfSizeZ;
        float yaw = location.getYaw();
        switch (rotation) {
            case CLOCKWISE_90 -> {
                location.setX(-z + halfSizeX);
                location.setZ(x + halfSizeZ);
                yaw += 90;
            }
            case CLOCKWISE_180 -> {
                location.setX(-x + halfSizeX);
                location.setZ(-z + halfSizeZ);
                yaw += 180;
            }
            case COUNTERCLOCKWISE_90 -> {
                location.setX(z + halfSizeX);
                location.setZ(-x + halfSizeZ);
                yaw -= 90;
            }
        }
        location.setYaw(Location.normalizeYaw(yaw));

        return location.add(position);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StructureInstance) obj;
        return Objects.equals(this.structure, that.structure) &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.rotation, that.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(structure, position, rotation);
    }

    @Override
    public String toString() {
        return "StructureTransform[" +
                "structure=" + structure + ", " +
                "position=" + position + ", " +
                "rotation=" + rotation + ']';
    }

}
