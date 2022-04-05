package dev.fabien2s.annoyingapi.math;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VectorHelper {

    public static Vector zero() {
        return new Vector(0, 0, 0);
    }

    public static Vector one() {
        return new Vector(1, 1, 1);
    }

    public static Vector up() {
        return new Vector(0, 1, 0);
    }

    public static Vector down() {
        return new Vector(0, -1, 0);
    }

    public static Vector direction(Location viewPosition, Location target) {
        return target.toVector().subtract(viewPosition.toVector()).normalize();
    }

    public static Vector direction(Vector viewPosition, Vector target) {
        return target.clone().subtract(viewPosition).normalize();
    }

    public static Vector direction(Vector vector, double yaw) {
        double yawRad = yaw * MathHelper.DEG_2_RAD;
        vector.setX(-Math.sin(yawRad));
        vector.setY(0);
        vector.setZ(Math.cos(yawRad));
        return vector;
    }

    public static Vector direction(double yaw) {
        double yawRad = yaw * MathHelper.DEG_2_RAD;
        return new Vector(
                -Math.sin(yawRad),
                0,
                Math.cos(yawRad)
        );
    }

    @NotNull
    public static Vector direction(double yaw, double pitch) {
        double yawRad = yaw * MathHelper.DEG_2_RAD;
        double yawPitch = pitch * MathHelper.DEG_2_RAD;

        double xz = Math.cos(yawPitch);

        return new Vector(
                -xz * Math.sin(yawRad),
                -Math.sin(yawPitch),
                xz * Math.cos(yawRad)
        );
    }

    public static double yaw(Vector direction) {
        double x = direction.getX();
        double z = direction.getZ();
        double theta = Math.atan2(-x, z);
        return ((theta + MathHelper.PI_2) % MathHelper.PI_2) * MathHelper.RAD_2_DEG;
    }

    public static double pitch(Vector direction) {
        double x = direction.getX();
        double z = direction.getZ();
        if (x == 0.0D && z == 0.0D)
            return direction.getY() > 0 ? -90 : 90;

        double xz = Math.sqrt(x * x + z * z);
        return Math.atan(-direction.getY() / xz) * MathHelper.RAD_2_DEG;
    }

    public static void lookAt(Location location, Vector target) {
        double deltaX = target.getX() - location.getX();
        double deltaY = target.getY() - location.getY();
        double deltaZ = target.getZ() - location.getZ();

        double lengthXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double lengthY = Math.sqrt(lengthXZ * lengthXZ + deltaY * deltaY);
        double newYaw = Math.acos(deltaX / lengthXZ) * 180 / Math.PI;
        double newPitch = Math.acos(deltaY / lengthY) * 180 / Math.PI - 90;
        if (deltaZ < 0.0)
            newYaw += Math.abs(180 - newYaw) * 2;

        location.setYaw((float) (newYaw - 90f));
        location.setPitch((float) newPitch);
    }

    public static boolean inRange(Location viewPosition, Location target, double range) {
        return viewPosition.distanceSquared(target) <= range * range;
    }

    public static boolean inAngle(Location viewPosition, Vector target, double radAngle) {
        Vector direction = direction(viewPosition.getYaw());
        Vector pointToOtherDirection = direction(viewPosition.toVector(), target);
        return direction.angle(pointToOtherDirection) <= radAngle;
    }

    public static boolean inAngle(Location viewPosition, Location target, double radAngle) {
        Vector direction = direction(viewPosition.getYaw());
        Vector pointToOtherDirection = direction(viewPosition.toVector(), target.toVector());
        return direction.angle(pointToOtherDirection) <= radAngle;
    }

    public static Vector rotate(Vector vector, StructureRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90:
                return new Vector(vector.getZ(), vector.getY(), -vector.getX());
            case CLOCKWISE_180:
                return new Vector(-vector.getX(), vector.getY(), -vector.getZ());
            case COUNTERCLOCKWISE_90:
                return new Vector(-vector.getZ(), vector.getY(), vector.getX());
            default:
                return vector.clone();
        }
    }

    public static void lerp(Location location, Location start, Location end, double value) {
        double x = MathHelper.lerp(start.getX(), end.getX(), value);
        double y = MathHelper.lerp(start.getY(), end.getY(), value);
        double z = MathHelper.lerp(start.getZ(), end.getZ(), value);
        double yaw = MathHelper.lerpAngle(start.getYaw(), end.getYaw(), value);
        double pitch = MathHelper.lerpAngle(start.getPitch(), end.getPitch(), value);

        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw((float) yaw);
        location.setPitch((float) pitch);
    }

    public static void copy(Location from, Location to) {
        if (from == null || to == null)
            return;

        to.setWorld(from.getWorld());
        to.setX(from.getX());
        to.setY(from.getY());
        to.setZ(from.getZ());
        to.setYaw(from.getYaw());
        to.setPitch(from.getPitch());
    }

    public static void copy(Location from, Vector to) {
        if (from == null || to == null)
            return;

        to.setX(from.getX());
        to.setY(from.getY());
        to.setZ(from.getZ());
    }

}
