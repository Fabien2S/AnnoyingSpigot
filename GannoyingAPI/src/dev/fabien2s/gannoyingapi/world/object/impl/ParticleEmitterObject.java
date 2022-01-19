package dev.fabien2s.gannoyingapi.world.object.impl;

import lombok.Getter;
import lombok.Setter;
import dev.fabien2s.annoyingapi.math.VectorHelper;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import dev.fabien2s.gannoyingapi.world.GameObject;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ParticleEmitterObject extends GameObject {

    @Getter @Setter private int spawnCount = 1;
    @Getter @Setter private double spawnInterval = .1;
    @Getter @Setter private Consumer<Vector> spawnOffsetSupplier;

    @Getter @Setter private Particle particleType = Particle.SMOKE_NORMAL;
    @Getter @Setter @Nullable private Object particleData = null;

    @Getter @Setter private Vector particleSize = VectorHelper.zero();
    @Getter @Setter private float particleSpeed = 0;
    @Getter @Setter private boolean particleForced = false;

    private Vector spawnOffset;
    private double particleTime;

    public ParticleEmitterObject(GameWorld gameWorld, Location location) {
        super(gameWorld, "game.particle_emitter", location);
    }

    @Override
    public void init() {
        super.init();

        this.spawnOffset = new Vector();
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        this.particleTime += deltaTime;
        if (this.particleTime >= spawnInterval) {
            this.particleTime = 0;
            this.spawnParticle();
        }
    }

    private void spawnParticle() {
        double spawnX = location.getX();
        double spawnY = location.getY();
        double spawnZ = location.getZ();
        this.spawnOffsetSupplier.accept(spawnOffset);

        double particleSizeX = particleSize.getX();
        double particleSizeY = particleSize.getY();
        double particleSizeZ = particleSize.getZ();

        World spigotWorld = gameWorld.getSpigotWorld();
        spigotWorld.spawnParticle(
                particleType,
                spawnX + spawnOffset.getX(),
                spawnY + spawnOffset.getY(),
                spawnZ + spawnOffset.getZ(),
                spawnCount,
                particleSizeX,
                particleSizeY,
                particleSizeZ,
                particleSpeed,
                particleData,
                particleForced
        );
    }

}
