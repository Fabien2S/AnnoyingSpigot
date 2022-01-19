package dev.fabien2s.annoyingapi.structure;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.nbt.NbtHelper;
import dev.fabien2s.annoyingapi.nbt.NbtIO;
import dev.fabien2s.annoyingapi.nbt.NbtRegistry;
import dev.fabien2s.annoyingapi.nbt.exception.NbtFormatException;
import dev.fabien2s.annoyingapi.nbt.tag.*;
import dev.fabien2s.annoyingapi.util.Minecraft;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StructureManager {

    private static final NamespacedKey STRUCTURE_FOLDER = AnnoyingPlugin.createKey("structures");

    private final JavaPlugin plugin;

    public StructureManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Structure load(DataInput input) throws IOException {
        Logger logger = plugin.getLogger();
        NbtCompound rootCompound = NbtIO.read(input);

        // reading name
        NbtString nameTag = rootCompound.get("Name", NbtString.class);
        String name = nameTag.getValue();

        logger.log(Level.INFO, "Loading structure \"{0}\"", name);

        // reading size
        NbtIntArray sizeTag = rootCompound.get("Size", NbtIntArray.class);
        int[] sizeArray = sizeTag.getValue();
        if (sizeArray.length != 3)
            throw new NbtFormatException("Expected Size to have a size of 3");
        int sizeX = sizeArray[0];
        int sizeY = sizeArray[1];
        int sizeZ = sizeArray[2];

        // creating structure
        Structure structure = new Structure(name, sizeX, sizeY, sizeZ);
        Server server = plugin.getServer();

        // reading palette
        NbtList<NbtString, String> paletteTag = rootCompound.getList("Palette", NbtRegistry.STRING_TAG);
        List<BlockData> blockPalette = structure.getBlockPalette();
        blockPalette.clear();
        for (NbtTag<String> tag : paletteTag) {
            String serializedBlock = tag.getValue();
            BlockData blockData = server.createBlockData(serializedBlock);
            blockPalette.add(blockData);
        }

        // reading blocks
        NbtIntArray blocksTag = rootCompound.get("Blocks", NbtIntArray.class);
        int[] blocks = blocksTag.getValue();
        int[] structureBlocks = structure.getBlocks();
        if (blocks.length != structureBlocks.length)
            throw new NbtFormatException("Blocks length is not valid");
        System.arraycopy(blocks, 0, structureBlocks, 0, blocks.length);

        // reading anchors
        NbtCompound anchorsTag = rootCompound.get("Anchors", NbtCompound.class);
        Map<String, Structure.Anchor> anchorMap = structure.getAnchorMap();
        for (Map.Entry<String, NbtTag<?>> entry : anchorsTag.entrySet()) {
            NbtCompound anchorCompound = (NbtCompound) entry.getValue();
            NbtString anchorTagTag = anchorCompound.get("Tag", NbtString.class);

            NbtList<NbtDouble, Double> positionTag = anchorCompound.getList("Position", NbtDouble.class, 3);
            double[] position = NbtHelper.deserializeDouble(positionTag);

            NbtList<NbtFloat, Float> rotationTag = anchorCompound.getList("Rotation", NbtFloat.class, 2);
            float[] rotation = NbtHelper.deserializeFloat(rotationTag);

            String anchorName = entry.getKey();
            if (anchorMap.containsKey(anchorName))
                throw new NbtFormatException("Duplicate anchor: " + anchorName);

            NbtCompound dataCompound;
            if (anchorCompound.containsKey("Data"))
                dataCompound = anchorCompound.get("Data", NbtCompound.class);
            else
                dataCompound = null;

            anchorMap.put(anchorName, new Structure.Anchor(
                    anchorName,
                    anchorTagTag.getValue(),
                    position[0],
                    position[1],
                    position[2],
                    rotation[0],
                    rotation[1],
                    dataCompound
            ));
        }

        return structure;
    }


    public void save(Structure structure, DataOutput output) throws IOException {
        NbtCompound rootCompound = new NbtCompound();

        String structureName = structure.getName();
        rootCompound.addProperty("Name", structureName);
        rootCompound.addProperty("Size", new int[]{
                structure.getSizeX(),
                structure.getSizeY(),
                structure.getSizeZ()
        });

        NbtList<NbtString, String> palette = new NbtList<>();
        List<BlockData> blockPalette = structure.getBlockPalette();
        for (BlockData blockData : blockPalette) {
            String serializedBlockData = blockData.getAsString();
            NbtString blockDataTag = new NbtString(serializedBlockData);
            palette.add(blockDataTag);
        }
        rootCompound.put("Palette", palette);

        int[] blocks = structure.getBlocks();
        rootCompound.addProperty("Blocks", blocks);

        NbtCompound anchorTag = new NbtCompound();
        Map<String, Structure.Anchor> anchorMap = structure.getAnchorMap();
        for (Map.Entry<String, Structure.Anchor> entry : anchorMap.entrySet()) {
            String anchorName = entry.getKey();
            Structure.Anchor anchor = entry.getValue();

            NbtCompound anchorCompound = new NbtCompound();
            anchorCompound.addProperty("Tag", anchor.getTag());

            NbtList<NbtDouble, Double> positionTag = NbtHelper.serializeDouble(anchor.getX(), anchor.getY(), anchor.getZ());
            anchorCompound.put("Position", positionTag);

            NbtList<NbtFloat, Float> rotationTag = NbtHelper.serializeFloat(anchor.getYaw(), anchor.getPitch());
            anchorCompound.put("Rotation", rotationTag);

            if (anchor.hasData()) {
                NbtCompound dataCompound = anchor.getDataCompound();
                anchorCompound.put("Data", dataCompound);
            }

            anchorTag.put(anchorName, anchorCompound);
        }
        rootCompound.put("Anchors", anchorTag);

        NbtIO.write(output, rootCompound);
    }

    public Structure load(NamespacedKey name) throws IOException {
        NamespacedKey structureLocation = Minecraft.createIdentifier(name.getNamespace(), "structures/" + name.getKey() + ".nbt");
        File structureFile = AnnoyingPlugin.toFileLocation(structureLocation);
        if (!structureFile.exists())
            throw new FileNotFoundException("Structure file not found (" + structureFile + ")");

        try (
                FileInputStream fileInputStream = new FileInputStream(structureFile);
                DataInputStream dataInputStream = new DataInputStream(fileInputStream)
        ) {
            return this.load(dataInputStream);
        }
    }

    public boolean save(Structure structure, NamespacedKey name) throws IOException {
        File structureFile = AnnoyingPlugin.toFileLocation(name);
        if (structureFile.exists() && !structureFile.delete())
            return false;
        if (!structureFile.createNewFile())
            return false;

        try (
                FileOutputStream fileOutputStream = new FileOutputStream(structureFile);
                DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)
        ) {
            this.save(structure, dataOutputStream);
            return true;
        }
    }

    public void spawn(World world, StructureInstance instance) {
        int poxX = instance.getX();
        int posY = instance.getY();
        int posZ = instance.getZ();

        int sizeX = instance.getSizeX();
        int sizeY = instance.getSizeY();
        int sizeZ = instance.getSizeZ();
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    if (instance.hasBlock(x, y, z)) {
                        Block block = world.getBlockAt(poxX + x, posY + y, posZ + z);
                        BlockData blockData = instance.getBlock(x, y, z);
                        block.setBlockData(blockData, false);
                    }
                }
            }
        }
    }

    public Collection<NamespacedKey> getStructures() {
        File structureFolder = AnnoyingPlugin.toFileLocation(STRUCTURE_FOLDER);
        File[] structureSubfolder = structureFolder.listFiles(File::isDirectory);
        if (structureSubfolder == null)
            return Collections.emptyList();

        Set<NamespacedKey> namespacedKeySet = new HashSet<>();
        for (File folder : structureSubfolder) {
            String folderName = folder.getName();
            File[] files = folder.listFiles(File::isFile);
            if (files == null)
                continue;

            for (File file : files) {
                String fileName = file.getName();
                String baseName = FilenameUtils.getBaseName(fileName);
                NamespacedKey identifier = Minecraft.createIdentifier(folderName, baseName);
                namespacedKeySet.add(identifier);
            }
        }
        return namespacedKeySet;
    }

}
