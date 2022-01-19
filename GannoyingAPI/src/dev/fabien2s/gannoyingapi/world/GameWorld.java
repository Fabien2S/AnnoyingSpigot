package dev.fabien2s.gannoyingapi.world;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import dev.fabien2s.gannoyingapi.GamePlugin;
import dev.fabien2s.gannoyingapi.world.object.IGameObjectBuilder;
import dev.fabien2s.gannoyingapi.world.object.impl.SoundEmitterObject;
import lombok.Getter;
import dev.fabien2s.annoyingapi.adapter.player.IPlayerController;
import dev.fabien2s.annoyingapi.math.RandomHelper;
import dev.fabien2s.annoyingapi.nbt.tag.NbtCompound;
import dev.fabien2s.annoyingapi.player.PlayerList;
import dev.fabien2s.annoyingapi.sound.ISoundListener;
import dev.fabien2s.annoyingapi.structure.Structure;
import dev.fabien2s.annoyingapi.structure.StructureInstance;
import dev.fabien2s.annoyingapi.util.ITickable;
import dev.fabien2s.annoyingapi.util.Minecraft;
import dev.fabien2s.gannoyingapi.player.ActiveGamePlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.*;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class GameWorld implements ITickable, ISoundListener {

    private static final Logger LOGGER = LogManager.getLogger(GameWorld.class);

    @Getter private final GamePlugin plugin;
    @Getter private final Random random;

    @Getter private final World spigotWorld;

    private final Set<GameObject> gameObjects = new HashSet<>();
    private final Queue<GameObject> gameObjectQueue = new ArrayDeque<>();

    private final Set<StructureInstance> structures = new HashSet<>();
    private final Multimap<Long, StructureInstance> chunkStructureMap = HashMultimap.create();

    private final ListMultimap<String, Location> anchorMap = ArrayListMultimap.create();

    private boolean delayGameObjectSpawn;

    public GameWorld(GamePlugin plugin, IWorldBuilder builder) {
        this.plugin = plugin;
        this.random = new Random();

        WorldCreator worldCreator = WorldCreator.name("world_game");
        this.spigotWorld = builder.build(this, worldCreator);
        this.anchorMap.forEach((s, location) -> location.setWorld(spigotWorld));
    }

    public void reset() {
        // reset game objects
        for (GameObject gameObject : gameObjects)
            gameObject.reset();
        this.gameObjects.removeIf(this::checkForRemoval);

        // reset structures
        this.structures.clear();
        this.chunkStructureMap.clear();

        // reset anchors
        this.anchorMap.clear();
    }

    @Override
    public void tick(double deltaTime) {
        this.gameObjects.removeIf(this::checkForRemoval);

        this.delayGameObjectSpawn = true;
        for (GameObject gameObject : gameObjects)
            gameObject.tick(deltaTime);
        this.delayGameObjectSpawn = false;

        this.gameObjects.addAll(gameObjectQueue);
        this.gameObjectQueue.clear();
    }

    private boolean checkForRemoval(GameObject gameObject) {
        if (gameObject.isRemoved()) {
            PlayerList playerList = plugin.getPlayerList();
            playerList.forPlayers(ActiveGamePlayer.class, gameObject::unregister);

            gameObject.reset();

            LOGGER.info("{} removed", gameObject);
            return true;
        }
        return false;
    }

    Collection<StructureInstance> pollStructures(int chunkX, int chunkZ) {
        long index = (((long) chunkX) << 32) | (chunkZ & 0xffffffffL);
        return chunkStructureMap.removeAll(index);
    }

    public <T extends GameObject> T addGameObject(IGameObjectBuilder<T> objectBuilder, Location location) {
        return addGameObject(objectBuilder, location, null);
    }

    public <T extends GameObject> T addGameObject(IGameObjectBuilder<T> objectBuilder, Location location, @Nullable Consumer<T> consumer) {
        T gameObject = objectBuilder.build(this, location);
        LOGGER.info("{} spawned at {}", gameObject, location);
        if (consumer != null)
            consumer.accept(gameObject);

        gameObject.init();

        if (delayGameObjectSpawn)
            this.gameObjectQueue.add(gameObject);
        else
            this.gameObjects.add(gameObject);

        PlayerList playerList = plugin.getPlayerList();
        playerList.forPlayers(ActiveGamePlayer.class, gameObject::register);

        return gameObject;
    }

    public <T> void forGameObjects(Class<T> tClass, Consumer<T> callback) {
        for (GameObject gameObject : gameObjects) {
            if (tClass.isInstance(gameObject)) {
                T tObject = tClass.cast(gameObject);
                callback.accept(tObject);
            }
        }
    }

    public <T> Collection<T> getGameObjects(Class<T> tClass) {
        Set<T> objects = new HashSet<>();
        for (GameObject gameObject : gameObjects) {
            if (tClass.isInstance(gameObject)) {
                T tObject = tClass.cast(gameObject);
                objects.add(tObject);
            }
        }
        return objects;
    }

    public void addAnchor(String tag, Location location) {
        this.anchorMap.put(tag, location);
    }

    public void addAnchors(String tag, Iterable<Location> locations) {
        this.anchorMap.putAll(tag, locations);
    }

    public Location getAnchor(String tag) {
        List<Location> locations = anchorMap.get(tag);
        return RandomHelper.getRandom(this.random, locations);
    }

    public boolean removeAnchor(String tag, Location location) {
        return anchorMap.remove(tag, location);
    }

    public List<Location> getAnchors(String tag) {
        return anchorMap.get(tag);
    }

    public Collection<Location> removeAnchors(String tag) {
        return anchorMap.removeAll(tag);
    }

    public <T extends GameObject> Set<T> spawnObjects(String name, String tag, int objectCount, IGameObjectBuilder<T> objectBuilder) {
        List<Location> spawnLocations = anchorMap.get(tag);
        int spawnCount = spawnLocations.size();
        if (spawnCount < objectCount) {
            LOGGER.error("Not enough spawn point for object {} (missing {} spawn(s))", name, objectCount - spawnCount);
            objectCount = spawnCount;
        }

        Set<T> spawnedObjectSet = new HashSet<>();
        for (int spawnedObjects = 0; spawnedObjects < objectCount; spawnedObjects++) {
            Location objectLocation = null;

            if (spawnedObjectSet.isEmpty()) {
                objectLocation = RandomHelper.getRandom(this.random, spawnLocations);
                objectLocation.setWorld(spigotWorld);
            } else {
                double furtherDistanceSqr = 0;
                for (Location location : spawnLocations) {
                    location.setWorld(spigotWorld);

                    double minimumDistanceSqr = Double.MAX_VALUE;
                    for (T otherObject : spawnedObjectSet) {
                        double otherDistanceSqr = otherObject.distanceSqr(location);
                        if (otherDistanceSqr < minimumDistanceSqr)
                            minimumDistanceSqr = otherDistanceSqr;
                    }

                    if (minimumDistanceSqr > furtherDistanceSqr) {
                        objectLocation = location;
                        furtherDistanceSqr = minimumDistanceSqr;
                    }
                }
            }

            if (objectLocation == null)
                throw new IllegalStateException("No spawn point found for " + name);

            T gameObject = this.addGameObject(objectBuilder, objectLocation);
            spawnedObjectSet.add(gameObject);
            spawnLocations.remove(objectLocation);
        }

        return spawnedObjectSet;
    }

    public boolean hasStructure(int chunkX, int chunkZ) {
        int chunkMinX = chunkX * Minecraft.CHUNK_SIZE;
        int chunkMinZ = chunkZ * Minecraft.CHUNK_SIZE;
        int chunkMaxX = chunkMinX + Minecraft.CHUNK_SIZE;
        int chunkMaxZ = chunkMinZ + Minecraft.CHUNK_SIZE;

        for (StructureInstance instance : structures) {
            Structure structure = instance.getStructure();
            int maxX = instance.getX() + structure.getSizeX();
            int maxZ = instance.getZ() + structure.getSizeZ();

            if (chunkMinX < maxX && chunkMaxX > instance.getX() && chunkMinZ < maxZ && chunkMaxZ > instance.getZ())
                return true;
        }

        return false;
    }

    public void addStructure(StructureInstance structureInstance) {
        int structureX = structureInstance.getX();
        int structureZ = structureInstance.getZ();

        LOGGER.info("Added structure {} at {}, {}, {}", structureInstance, structureX, structureInstance.getY(), structureZ);
        this.structures.add(structureInstance);

        int structChunkX = (int) Math.floor(structureX / (double) Minecraft.CHUNK_SIZE);
        int structChunkZ = (int) Math.floor(structureZ / (double) Minecraft.CHUNK_SIZE);
        int structChunkSizeX = structureInstance.getChunkSizeX();
        int structChunkSizeZ = structureInstance.getChunkSizeZ();
        for (int x = 0; x < structChunkSizeX; x++) {
            for (int z = 0; z < structChunkSizeZ; z++) {
                this.chunkStructureMap.put(
                        (((long) structChunkX + x) << 32) | ((structChunkZ + z) & 0xffffffffL),
                        structureInstance
                );
            }
        }

        Collection<StructureInstance.AnchorInstance> anchorInstances = structureInstance.getAnchorInstances(null);
        for (StructureInstance.AnchorInstance anchorInstance : anchorInstances) {
            String tag = anchorInstance.getTag();
            Location location = anchorInstance.getLocation();
            this.addAnchor(tag, location);
        }

        Collection<StructureInstance.AnchorInstance> soundEmitters = structureInstance.getAnchorInstances("sound_emitter");
        for (StructureInstance.AnchorInstance anchorInstance : soundEmitters) {
            Location location = anchorInstance.getLocation();
            this.addGameObject(SoundEmitterObject::new, location, soundEmitterObject -> {
                NbtCompound dataCompound = anchorInstance.getDataCompound();
                soundEmitterObject.deserialize(dataCompound);
            });
        }
    }

    @Override
    public void playSound(Entity entity, Sound sound, SoundCategory category, float volume, float pitch) {
        PlayerList playerList = plugin.getPlayerList();
        playerList.forEach(gamePlayer -> {
            IPlayerController controller = gamePlayer.getController();
            controller.playSound(entity, sound, category, volume, pitch);
        });
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        this.spigotWorld.playSound(location, sound, category, volume, pitch);
    }

    @Override
    public void playSound(Location location, NamespacedKey sound, SoundCategory category, float volume, float pitch) {
        this.spigotWorld.playSound(location, sound.toString(), category, volume, pitch);
    }

    @Override
    public void playSound2D(Sound sound, SoundCategory category, float volume, float pitch) {
        PlayerList playerList = plugin.getPlayerList();
        playerList.forEach(gamePlayer -> {
            IPlayerController controller = gamePlayer.getController();
            controller.playSound2D(sound, category, volume, pitch);
        });
    }

    @Override
    public void playSound2D(NamespacedKey sound, SoundCategory category, float volume, float pitch) {
        PlayerList playerList = plugin.getPlayerList();
        playerList.forEach(gamePlayer -> {
            IPlayerController controller = gamePlayer.getController();
            controller.playSound2D(sound, category, volume, pitch);
        });
    }

}
