package dev.fabien2s.gannoyingapi.world.lod;

import dev.fabien2s.gannoyingapi.world.GameObject;
import lombok.RequiredArgsConstructor;
import dev.fabien2s.annoyingapi.player.GamePlayer;
import dev.fabien2s.annoyingapi.util.ITickable;
import org.bukkit.Location;

import java.util.*;

@RequiredArgsConstructor
public class LodController implements ITickable {

    private final GameObject gameObject;
    private final Map<GamePlayer, Entry> trackedPlayers = new HashMap<>();
    private final SortedSet<Entry> states = new TreeSet<>(Comparator.comparingDouble(o -> o.distance));

    @Override
    public void tick(double deltaTime) {
        Location objectLocation = gameObject.getLocation();

        Set<Map.Entry<GamePlayer, Entry>> entries = trackedPlayers.entrySet();
        for (Map.Entry<GamePlayer, Entry> entry : entries) {
            GamePlayer gamePlayer = entry.getKey();
            double distanceSqr = gamePlayer.distanceSqr(objectLocation);

            Entry computedEntry = computeEntry(distanceSqr);
            if (computedEntry != entry.getValue()) {
                int level = states.headSet(computedEntry).size();
                computedEntry.callback.onChange(gamePlayer, computedEntry.distance, level);
                entry.setValue(computedEntry);
            }
        }
    }

    private Entry computeEntry(double distanceSqr) {
        Entry entry = null;
        double highestLod = 0;

        for (Entry state : states) {
            double dstSqr = state.distance * state.distance;

            if (distanceSqr < dstSqr)
                continue;

            if (dstSqr < highestLod)
                continue;

            highestLod = dstSqr;
            entry = state;
        }

        return entry;
    }

    public void addState(double distance, ILodStateChanged callback) {
        this.states.add(new Entry(distance, callback));
    }

    public void addPlayer(GamePlayer gamePlayer) {
        this.trackedPlayers.put(gamePlayer, null);
    }

    public void removePlayer(GamePlayer gamePlayer) {
        this.trackedPlayers.remove(gamePlayer);
    }

    @RequiredArgsConstructor
    public static class Entry {
        private final double distance;
        private final ILodStateChanged callback;
    }

}
