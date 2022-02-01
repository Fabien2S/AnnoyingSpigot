package dev.fabien2s.annoyingapi.structure;

import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record StructureAnchor(
        @NotNull String name,
        @Nullable String tag,
        @NotNull Location location,
        @Nullable PersistentDataContainer dataContainer
) {

    public boolean hasData() {
        return dataContainer != null;
    }

}
