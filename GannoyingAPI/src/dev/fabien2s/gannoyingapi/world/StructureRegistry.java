package dev.fabien2s.gannoyingapi.world;

import dev.fabien2s.gannoyingapi.GamePlugin;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.structure.StructureAnchorManager;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class StructureRegistry {

    private final StructureAnchorManager structureManager;
    private final Map<NamespacedKey, Structure> structureMap = new HashMap<>();

    public Structure loadStructure(String name) {
        NamespacedKey namespacedKey = GamePlugin.createKey(name);
        Structure structure = structureMap.get(namespacedKey);
        if (structure != null)
            return structure;

        try {
            structure = structureManager.load(namespacedKey);
            this.structureMap.put(namespacedKey, structure);
            return structure;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the tile " + name, e);
        }
    }

}
