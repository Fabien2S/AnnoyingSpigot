package dev.fabien2s.annoyingapi.nbt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import dev.fabien2s.annoyingapi.nbt.tag.NbtDouble;
import dev.fabien2s.annoyingapi.nbt.tag.NbtFloat;
import dev.fabien2s.annoyingapi.nbt.tag.NbtList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NbtHelper {

    public static double[] deserializeDouble(NbtList<NbtDouble, Double> list) {
        double[] doubles = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            doubles[i] = list
                    .get(i)
                    .getValue();
        }
        return doubles;
    }

    public static NbtList<NbtDouble, Double> serializeDouble(double... doubles) {
        NbtList<NbtDouble, Double> nbtList = new NbtList<>();
        for (double d : doubles)
            nbtList.add(new NbtDouble(d));
        return nbtList;
    }

    public static float[] deserializeFloat(NbtList<NbtFloat, Float> list) {
        float[] floats = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            floats[i] = list
                    .get(i)
                    .getValue();
        }
        return floats;
    }

    public static NbtList<NbtFloat, Float> serializeFloat(float... floats) {
        NbtList<NbtFloat, Float> nbtList = new NbtList<>();
        for (float f : floats)
            nbtList.add(new NbtFloat(f));
        return nbtList;
    }

}
