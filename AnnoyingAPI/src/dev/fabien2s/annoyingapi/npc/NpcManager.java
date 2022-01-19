package dev.fabien2s.annoyingapi.npc;

import com.mojang.authlib.GameProfile;
import dev.fabien2s.annoyingapi.AnnoyingPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.adapter.GameAdapters;
import dev.fabien2s.annoyingapi.adapter.IGameAdapter;
import dev.fabien2s.annoyingapi.util.ITickable;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class NpcManager implements ITickable {

    @Getter private final AnnoyingPlugin plugin;

    private final Set<Npc> npcSet = new HashSet<>();

    @Override
    public void tick(double deltaTime) {
        this.npcSet.removeIf(Npc::isRemoved);
        for (Npc npc : npcSet)
            npc.tick(deltaTime);
    }

    public Npc spawnNpc(@Nonnull Location location, @Nonnull GameProfile gameProfile) {
        IGameAdapter gameAdapter = GameAdapters.INSTANCE;
        Npc npc = gameAdapter.createNPC(this, location, gameProfile);
        this.npcSet.add(npc);
        return npc;
    }

    public GameProfile createProfile(String name) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        UUID uuid = UUID.nameUUIDFromBytes(nameBytes);
        return new GameProfile(uuid, name);
    }

}
