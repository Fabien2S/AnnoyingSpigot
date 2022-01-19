package dev.fabien2s.gannoyingapi.world.object.projectile;

import lombok.Getter;
import lombok.Setter;
import dev.fabien2s.annoyingapi.magical.IValueSupplier;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.util.ITickable;
import dev.fabien2s.annoyingapi.util.RayTraceHelper;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import dev.fabien2s.gannoyingapi.world.GameObject;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public abstract class Projectile<T extends GamePlayer & IProjectileSource<U>, U extends IProjectileTarget> extends GameObject implements ITickable {

    private static final Vector GRAVITY = new Vector(0, -10, 0);

    @Getter @Setter
    @Nullable private T shooter;
    @Getter @Setter private boolean ignoreShooterCollision = true;
    @Getter @Setter private double lifeTime = Double.NaN;

    @Setter private Collection<U> targets = Collections.emptySet();
    @Setter private Vector gravity = GRAVITY;

    protected Vector velocity = VectorHelper.zero();
    protected Vector direction = VectorHelper.down();
    protected double speed = 0;

    @Getter @Nullable private U hitTarget;
    @Getter @Nullable private Block hitBlock;

    public Projectile(GameWorld world, String name, Location location) {
        super(world, name, location);
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        this.lifeTime -= deltaTime;
        if (this.lifeTime <= 0 || hasColliding(deltaTime)) {
            this.remove();
            return;
        }

        this.velocity.add(gravity.clone()
                .multiply(deltaTime)
        );
        this.direction = velocity.clone().normalize();

        this.location.add(
                velocity.getX() * deltaTime,
                velocity.getY() * deltaTime,
                velocity.getZ() * deltaTime
        );
        this.location.setDirection(direction);

        double y = location.getY();
        if (y < 0 || 256 <= y) {
            this.remove();
            return;
        }

        this.update(deltaTime, location.clone(), direction);
    }

    protected abstract void update(double deltaTime, Location location, Vector direction);

    protected abstract boolean onCollide(Vector position, U target, Entity entity);

    protected abstract boolean onCollide(Vector position, Block block, Material material, BlockFace face);

    private boolean hasColliding(double deltaTime) {
        RayTraceResult result = RayTraceHelper.rayTrace(
                location,
                direction,
                speed * deltaTime,
                FluidCollisionMode.NEVER,
                true,
                getSize(),
                this::canHitBlock,
                this::canHitEntity
        );

        if (result == null)
            return false;

        Vector hitPosition = result.getHitPosition();
        this.location.setX(hitPosition.getX());
        this.location.setY(hitPosition.getY());
        this.location.setZ(hitPosition.getZ());

        Block hitBlock = result.getHitBlock();
        if (hitBlock != null) {
            Material material = hitBlock.getType();
            if (material == Material.BARRIER)
                return false;
            this.hitBlock = hitBlock;
            BlockFace blockFace = result.getHitBlockFace();
            return this.onCollide(hitPosition, hitBlock, material, blockFace);
        } else
            this.hitBlock = null;

        Entity hitEntity = result.getHitEntity();
        if (hitEntity != null) {
            for (U target : targets) {
                Entity targetEntity = target.getEntity();
                if (hitEntity.equals(targetEntity)) {
                    this.hitTarget = target;
                    return this.onCollide(hitPosition, this.hitTarget, hitEntity);
                }
            }

            return false;
        } else
            this.hitTarget = null;

        return false;
    }

    private boolean canHitBlock(Block block) {
        Material blockType = block.getType();
        return blockType != Material.BARRIER && blockType != Material.BROWN_STAINED_GLASS;
    }

    protected boolean canHitEntity(Entity entity) {
        GamePlayer shooter = getShooter();
        if (ignoreShooterCollision && shooter != null && entity instanceof Player) {
            Player shooterPlayer = shooter.getSpigotPlayer();
            if (shooterPlayer.equals(entity))
                return false;
        }

        for (U target : targets) {
            Entity targetEntity = target.getEntity();
            if (targetEntity != null && targetEntity.equals(entity))
                return true;
        }

        return false;
    }

    public void setShooterAndTargets(@Nonnull T shooter) {
        this.setShooter(shooter);
        this.targets = shooter.getTargets();
    }

    public void setVelocity(GamePlayer gamePlayer, IValueSupplier speed) {
        this.setVelocity(gamePlayer, speed.getValue());
    }

    public void setVelocity(GamePlayer gamePlayer, double speed) {
        Location unsafeEyeLocation = gamePlayer.getUnsafeEyeLocation();
        this.direction = unsafeEyeLocation.getDirection();
        this.velocity = direction.clone().multiply(speed);
        this.speed = speed;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity.clone();
        this.direction = velocity.clone().normalize();
        this.speed = velocity.length();
    }

    public double getSize() {
        return .2;
    }

}
