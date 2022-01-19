package dev.fabien2s.annoyingapi.structure;

import dev.fabien2s.annoyingapi.nbt.tag.NbtCompound;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nullable;
import java.util.*;

public class Structure {

    private static final int DEFAULT_PALETTE_INDEX = 0;
    private static final BlockData DEFAULT_BLOCK_DATA = Material.AIR.createBlockData();

    @Getter
    private final String name;
    @Getter
    private final int sizeX;
    @Getter
    private final int sizeY;
    @Getter
    private final int sizeZ;

    @Getter(AccessLevel.PACKAGE)
    private final List<BlockData> blockPalette = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Anchor> anchorMap = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    private final int[] blocks;

    private transient int voidIndex = -1;

    public Structure(String name, int sizeX, int sizeY, int sizeZ) {
        Validate.inclusiveBetween(1, 255, sizeX);
        Validate.inclusiveBetween(1, 255, sizeY);
        Validate.inclusiveBetween(1, 255, sizeZ);

        this.name = name;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.blocks = new int[sizeX * sizeY * sizeZ];

        this.storeBlockData(DEFAULT_BLOCK_DATA);
    }

    private int index(int x, int y, int z) {
        if (x < 0 || x >= sizeX)
            throw new IllegalArgumentException("X (" + x + ") coordinate out of range [0, " + sizeX + "] for structure " + name);
        if (y < 0 || y >= sizeY)
            throw new IllegalArgumentException("Y (" + y + ") coordinate out of range [0, " + sizeY + "] for structure " + name);
        if (z < 0 || z >= sizeZ)
            throw new IllegalArgumentException("Z (" + z + ") coordinate out of range [0, " + sizeZ + "] for structure " + name);

        return (z * sizeX * sizeY) + (y * sizeX) + x;
    }

    private int storeBlockData(@Nullable BlockData blockData) {
        if (blockData == null)
            return storeBlockData(DEFAULT_BLOCK_DATA);

        int paletteIndex = blockPalette.indexOf(blockData);
        if (paletteIndex != -1)
            return paletteIndex;

        paletteIndex = blockPalette.size();
        this.blockPalette.add(blockData);
        return paletteIndex;
    }

    private void recomputePalette() {
        BlockData[] tmp = new BlockData[this.blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            int paletteIndex = blocks[i];
            tmp[i] = blockPalette.get(paletteIndex);
        }

        this.voidIndex = -1;
        this.blockPalette.clear();
        this.storeBlockData(DEFAULT_BLOCK_DATA);

        for (int i = 0; i < tmp.length; i++) {
            BlockData blockData = tmp[i];
            this.blocks[i] = storeBlockData(blockData);
        }
    }

    public boolean hasBlock(int x, int y, int z) {
        int index = index(x, y, z);
        int paletteIndex = blocks[index];
        if (this.voidIndex == -1) {
            BlockData blockData = blockPalette.get(paletteIndex);
            Material material = blockData.getMaterial();
            if (material == Material.STRUCTURE_VOID) {
                this.voidIndex = paletteIndex;
                return false;
            }
            return true;
        } else
            return paletteIndex != voidIndex;
    }

    public BlockData getBlock(int x, int y, int z) {
        int index = index(x, y, z);
        int paletteIndex = blocks[index];
        BlockData blockData = blockPalette.get(paletteIndex);
        return blockData != null ? blockData.clone() : Material.AIR.createBlockData();
    }

    public void setBlock(int x, int y, int z, @Nullable BlockData blockData) {
        int index = index(x, y, z);

        int oldPaletteIndex = this.blocks[index];
        this.blocks[index] = storeBlockData(blockData);
        if (oldPaletteIndex != DEFAULT_PALETTE_INDEX)
            this.recomputePalette();
    }

    public Collection<Anchor> getAnchors() {
        return anchorMap.values();
    }

    public Anchor getAnchor(String name) {
        return anchorMap.get(name);
    }

    public boolean addAnchor(String name, String tag, Location location) {
        return this.addAnchor(name, tag, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), null);
    }

    public boolean addAnchor(String name, String tag, double x, double y, double z, float yaw, float pitch, @Nullable NbtCompound dataCompound) {
        Validate.notNull(name);
        if (anchorMap.containsKey(name))
            return false;

        Validate.notNull(tag);
        Validate.inclusiveBetween(0, sizeX, x);
        Validate.inclusiveBetween(0, sizeY, y);
        Validate.inclusiveBetween(0, sizeZ, z);

        this.anchorMap.put(name, new Anchor(
                name,
                tag,
                x, y, z,
                Location.normalizeYaw(yaw), Location.normalizePitch(pitch),
                dataCompound
        ));
        return true;
    }

    public Anchor removeAnchor(String name) {
        return anchorMap.remove(name);
    }

    @Override
    public String toString() {
        return "Structure[" + name + ']';
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public static class Anchor {

        @Getter
        private final String name;
        @Getter
        private final String tag;
        @Getter
        private final double x, y, z;
        @Getter
        private final float yaw, pitch;

        @Getter
        @Nullable
        private final NbtCompound dataCompound;

        public boolean hasData() {
            return dataCompound != null;
        }

        public Location toLocation() {
            return new Location(null, x, y, z, yaw, pitch);
        }

    }

}
