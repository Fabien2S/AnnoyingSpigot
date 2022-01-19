package dev.fabien2s.gannoyingapi.world;

import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.structure.Structure;
import dev.fabien2s.annoyingapi.structure.StructureInstance;
import dev.fabien2s.annoyingapi.util.Minecraft;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collection;

@RequiredArgsConstructor
public class GameChunkGenerator extends ChunkGenerator {

    private final GameWorld gameWorld;

    protected void generateStructures(int chunkX, int chunkZ, ChunkData chunkData) {
        int worldX = chunkX * Minecraft.CHUNK_SIZE;
        int worldZ = chunkZ * Minecraft.CHUNK_SIZE;

        int maxHeight = chunkData.getMaxHeight();

        Collection<StructureInstance> structureInstances = gameWorld.pollStructures(chunkX, chunkZ);
        for (StructureInstance instance : structureInstances) {

            // structure info
            int structurePositionX = instance.getX();
            int structurePositionY = instance.getY();
            int structurePositionZ = instance.getZ();

            Structure structure = instance.getStructure();
            int structureSizeX = structure.getSizeX();
            int structureSizeY = structure.getSizeY();
            int structureSizeZ = structure.getSizeZ();

            // generation info
            int structureOffsetX = worldX - structurePositionX;
            int structureOffsetZ = worldZ - structurePositionZ;

            int structureChunkPositionX = Math.max(structurePositionX - worldX, 0);
            int structureChunkPositionZ = Math.max(structurePositionZ - worldZ, 0);

            // should be 8
            int structureChunkEndPositionX = Math.min(structureSizeX - structureOffsetX, Minecraft.CHUNK_SIZE);
            // should be 48
            int structureChunkEndPositionY = Math.min(structurePositionY + structureSizeY, maxHeight - structurePositionY);
            // should be 16 (max out at 16, chunk limit)
            int structureChunkEndPositionZ = Math.min(structureSizeZ - structureOffsetZ, Minecraft.CHUNK_SIZE);

            for (int x = structureChunkPositionX; x < structureChunkEndPositionX; x++) {
                for (int y = structurePositionY; y < structureChunkEndPositionY; y++) {
                    for (int z = structureChunkPositionZ; z < structureChunkEndPositionZ; z++) {
                        int structX = x + structureOffsetX;
                        int structY = y - structurePositionY;
                        int structZ = z + structureOffsetZ;
                        if (instance.hasBlock(structX, structY, structZ)) {
                            BlockData blockData = instance.getBlock(structX, structY, structZ);
                            chunkData.setBlock(x, y, z, blockData);
                        }
                    }
                }
            }

        }
    }

}
