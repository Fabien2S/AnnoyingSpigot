package dev.fabien2s.annoyingapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScoreboardHelper {

    public static String getNameForTeamEntry(Entity entity) {
        EntityType type = entity.getType();
        if (type == EntityType.PLAYER)
            return entity.getName();
        else {
            UUID uniqueId = entity.getUniqueId();
            return uniqueId.toString();
        }
    }

}
