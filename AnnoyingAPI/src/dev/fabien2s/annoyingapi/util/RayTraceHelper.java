package dev.fabien2s.annoyingapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RayTraceHelper {

    public static final Predicate<Block> NO_BARRIER = block -> {
        Material blockType = block.getType();
        return blockType != Material.BARRIER;
    };

    @Nullable
    public static RayTraceResult rayTrace(Location originLocation, Vector direction, double length, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, Predicate<Block> blockPredicate, Predicate<Entity> entityPredicate) {
        World world = originLocation.getWorld();
        if (world == null)
            throw new IllegalArgumentException("World is null");

        RayTraceResult blockResult = rayTraceBlocks(originLocation, direction, length, fluidCollisionMode, ignorePassableBlocks, blockPredicate);

        Vector originVector = null;
        double blockHitDistance = length;

        if (blockResult != null) {
            originVector = originLocation.toVector();
            Vector hitPosition = blockResult.getHitPosition();
            blockHitDistance = originVector.distance(hitPosition);
        }

        RayTraceResult entityResult = world.rayTraceEntities(originLocation, direction, blockHitDistance, raySize, entityPredicate);
        if (blockResult == null)
            return entityResult;
        else if (entityResult == null)
            return blockResult;
        else {
            double entityHitDistanceSquared = originVector.distanceSquared(entityResult.getHitPosition());
            return entityHitDistanceSquared < blockHitDistance * blockHitDistance ? entityResult : blockResult;
        }
    }

    @Nullable
    public static RayTraceResult rayTraceBlocks(Location location, Vector direction, double length, FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, Predicate<Block> blockPredicate) {
        World world = location.getWorld();
        if (world == null)
            throw new IllegalArgumentException("World is null");

        RayTraceResult result = world.rayTraceBlocks(location, direction, length, fluidCollisionMode, ignorePassableBlocks);
        if (result == null)
            return null;

        Block hitBlock = result.getHitBlock();
        if (hitBlock == null)
            return null;

        if (blockPredicate.test(hitBlock))
            return result;

        Vector hitPosition = result.getHitPosition().add(direction.clone().multiply(.1));
        Location hitLocation = hitPosition.toLocation(world);
        double distanceLeft = length - location.distance(hitLocation);
        if (distanceLeft <= 0)
            return null;

        return rayTraceBlocks(hitLocation, direction, distanceLeft, fluidCollisionMode, ignorePassableBlocks, blockPredicate);
    }

}
