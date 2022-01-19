package dev.fabien2s.gannoyingapi.world.object.impl;

import lombok.Getter;
import lombok.Setter;
import dev.fabien2s.annoyingapi.math.RandomHelper;
import dev.fabien2s.annoyingapi.nbt.NbtHelper;
import dev.fabien2s.annoyingapi.nbt.tag.NbtCompound;
import dev.fabien2s.annoyingapi.nbt.tag.NbtFloat;
import dev.fabien2s.annoyingapi.nbt.tag.NbtList;
import dev.fabien2s.annoyingapi.nbt.tag.NbtString;
import dev.fabien2s.annoyingapi.util.Minecraft;
import dev.fabien2s.gannoyingapi.world.GameWorld;
import dev.fabien2s.gannoyingapi.world.GameObject;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;

import java.util.Random;

public class SoundEmitterObject extends GameObject {

    private final Random random;

    @Getter @Setter private NamespacedKey soundEffect;
    @Getter @Setter private SoundCategory soundCategory;
    @Getter @Setter private double delayMin;
    @Getter @Setter private double delayMax;
    @Getter @Setter private float pitchMin;
    @Getter @Setter private float pitchMax;
    @Getter @Setter private float volumeMin;
    @Getter @Setter private float volumeMax;

    private double soundTime;

    public SoundEmitterObject(GameWorld gameWorld, Location location) {
        super(gameWorld, "game.sound_emitter", location);

        this.random = new Random();
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);

        this.soundTime -= deltaTime;
        if (this.soundTime <= 0) {
            this.soundTime = RandomHelper.nextDouble(random, delayMin, delayMax);
            this.playSound();
        }
    }

    public void deserialize(NbtCompound dataCompound) {
        NbtString soundEffectTag = dataCompound.get("SoundEffect", NbtString.class);
        this.soundEffect = Minecraft.parseIdentifier(soundEffectTag.getValue());

        NbtString soundCategoryTag = dataCompound.get("SoundCategory", NbtString.class);
        this.soundCategory = SoundCategory.valueOf(soundCategoryTag.getValue());

        if (dataCompound.containsKey("Delay")) {
            NbtList<NbtFloat, Float> delayTag = dataCompound.getList("Delay", NbtFloat.class, 2);
            float[] delay = NbtHelper.deserializeFloat(delayTag);
            this.delayMin = delay[0];
            this.delayMax = delay[1];
        }

        if (dataCompound.containsKey("Volume")) {
            NbtList<NbtFloat, Float> volumeTag = dataCompound.getList("Volume", NbtFloat.class, 2);
            float[] volume = NbtHelper.deserializeFloat(volumeTag);
            this.volumeMin = volume[0];
            this.volumeMax = volume[1];
        }

        if (dataCompound.containsKey("Pitch")) {
            NbtList<NbtFloat, Float> pitchTag = dataCompound.getList("Pitch", NbtFloat.class, 2);
            float[] pitch = NbtHelper.deserializeFloat(pitchTag);
            this.pitchMin = pitch[0];
            this.pitchMax = pitch[1];
        }
    }

    private void playSound() {
        this.gameWorld.playSound(
                location,
                soundEffect,
                soundCategory,
                RandomHelper.nextFloat(random, volumeMin, volumeMax),
                RandomHelper.nextFloat(random, pitchMin, pitchMax)
        );
    }

}
