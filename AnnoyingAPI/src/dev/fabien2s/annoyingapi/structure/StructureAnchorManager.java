package dev.fabien2s.annoyingapi.structure;

import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import dev.fabien2s.annoyingapi.block.BlockHelper;
import dev.fabien2s.annoyingapi.util.NamespacedKeyConstants;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.structure.Structure;
import org.bukkit.util.BlockVector;

public class StructureAnchorManager {

    private static final NamespacedKey ANCHORS_KEY = AnnoyingPlugin.createKey("anchors");
    private static final StructureAnchor[] ANCHORS_EMPTY = new StructureAnchor[0];

    public static StructureInstance loadInstance(Structure structure, BlockVector position, StructureRotation rotation) {

        StructureAnchor[] anchors = loadAnchors(structure);

        // TODO transform anchors

        return new StructureInstance(structure, position, rotation, anchors);
    }

    public static StructureAnchor[] loadAnchors(org.bukkit.structure.Structure structure) {
        PersistentDataContainer dataContainer = structure.getPersistentDataContainer();

        PersistentDataContainer[] anchorContainers = dataContainer.get(ANCHORS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY);
        if (anchorContainers == null || anchorContainers.length == 0) {
            return ANCHORS_EMPTY;
        }

        StructureAnchor[] anchors = new StructureAnchor[anchorContainers.length];
        for (int i = 0; i < anchorContainers.length; i++) {
            PersistentDataContainer anchorContainer = anchorContainers[i];

            String name = anchorContainer.get(NamespacedKeyConstants.NAME, PersistentDataType.STRING);
            if (name == null) name = String.valueOf(i);

            String tag = anchorContainer.get(NamespacedKeyConstants.TAG, PersistentDataType.STRING);

            double x = anchorContainer.getOrDefault(NamespacedKeyConstants.X, PersistentDataType.DOUBLE, 0d);
            double y = anchorContainer.getOrDefault(NamespacedKeyConstants.Y, PersistentDataType.DOUBLE, 0d);
            double z = anchorContainer.getOrDefault(NamespacedKeyConstants.Z, PersistentDataType.DOUBLE, 0d);

            float yaw = anchorContainer.getOrDefault(NamespacedKeyConstants.YAW, PersistentDataType.FLOAT, 0f);
            float pitch = anchorContainer.getOrDefault(NamespacedKeyConstants.PITCH, PersistentDataType.FLOAT, 0f);

            PersistentDataContainer anchorDataContainer = anchorContainer.get(NamespacedKeyConstants.DATA, PersistentDataType.TAG_CONTAINER);

            Location location = new Location(null, x, y, z, yaw, pitch);
            anchors[i] = new StructureAnchor(name, tag, location, anchorDataContainer);
        }

        return anchors;
    }

    public static void storeAnchors(Structure structure, StructureAnchor[] anchors) {
        PersistentDataContainer dataContainer = structure.getPersistentDataContainer();
        PersistentDataAdapterContext context = dataContainer.getAdapterContext();

        PersistentDataContainer[] anchorContainers = new PersistentDataContainer[anchors.length];
        for (int i = 0; i < anchors.length; i++) {
            StructureAnchor anchor = anchors[i];
            PersistentDataContainer anchorContainer = context.newPersistentDataContainer();

            anchorContainer.set(NamespacedKeyConstants.NAME, PersistentDataType.STRING, anchor.name());
            if (anchor.tag() != null)
                anchorContainer.set(NamespacedKeyConstants.TAG, PersistentDataType.STRING, anchor.tag());

            Location location = anchor.location();
            anchorContainer.set(NamespacedKeyConstants.X, PersistentDataType.DOUBLE, location.getX());
            anchorContainer.set(NamespacedKeyConstants.Y, PersistentDataType.DOUBLE, location.getY());
            anchorContainer.set(NamespacedKeyConstants.Z, PersistentDataType.DOUBLE, location.getZ());

            anchorContainer.set(NamespacedKeyConstants.YAW, PersistentDataType.FLOAT, location.getYaw());
            anchorContainer.set(NamespacedKeyConstants.PITCH, PersistentDataType.FLOAT, location.getPitch());

            if (anchor.dataContainer() != null) {
                anchorContainer.set(NamespacedKeyConstants.DATA, PersistentDataType.TAG_CONTAINER, anchor.dataContainer());
            }

            anchorContainers[i] = anchorContainer;
        }

        dataContainer.set(ANCHORS_KEY, PersistentDataType.TAG_CONTAINER_ARRAY, anchorContainers);
    }

    /**
     * Transforms structure anchors into world coordinates
     *
     * @param anchors The anchors to transform
     * @param x The structure X coordinate
     * @param y The structure Y coordinate
     * @param z The structure Z coordinate
     * @param size The structure size
     * @param rotation The structure rotation
     */
    public static void transformAnchors(StructureAnchor[] anchors, int x, int y, int z, BlockVector size, StructureRotation rotation) {
        for (StructureAnchor anchor : anchors) {
            Location location = anchor.location();

            float yaw = location.getYaw();

            double halfSizeX = size.getBlockX() / 2d;
            double halfSizeZ = size.getBlockZ() / 2d;

            switch (rotation) {
                case NONE -> location.add(x, y, z);
                case CLOCKWISE_90 -> {

                    location.setYaw(yaw + 90);

                }
                case CLOCKWISE_180 -> {
                }
                case COUNTERCLOCKWISE_90 -> {
                }
            }
        }
    }

}
