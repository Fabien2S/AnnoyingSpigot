package dev.fabien2s.annoyingapi.math;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomHelper {

    public static int getIndexFromWeights(Random random, double[] weights) {
        double sumOfWeight = 0;
        for (double weight : weights)
            sumOfWeight += weight;

        double rand = (random.nextDouble() * sumOfWeight);
        for (int i = 0; i < weights.length; i++) {
            if (rand < weights[i])
                return i;
            rand -= weights[i];
        }

        return -1;
    }

    public static int getWeighted(Random random, int[] array, double[] weights) {
        Validate.isTrue(array.length == weights.length, "Length doesn't match");

        int index = getIndexFromWeights(random, weights);
        return index != -1 ? array[index] : -1;
    }

    public static <T> @Nullable T getWeighted(Random random, T[] array, double[] weights) {
        Validate.isTrue(array.length == weights.length, "Length doesn't match");

        int index = getIndexFromWeights(random, weights);
        return index != -1 ? array[index] : null;
    }

    public static <T> T getRandom(Random random, T[] array) {
        return array[random.nextInt(array.length)];
    }

    public static <T extends Enum<T>> T getRandom(Random random, Class<T> clazz) {
        T[] enumConstants = clazz.getEnumConstants();
        return enumConstants[random.nextInt(enumConstants.length)];
    }

    public static <T> T getRandom(Random random, List<T> list) {
        int size = list.size();
        if (size == 0)
            throw new IllegalArgumentException("Collection is empty");

        if (size == 1)
            return list.get(0);

        int randomIndex = random.nextInt(size);
        return list.get(randomIndex);
    }

    public static <T> T getRandom(Random random, Collection<T> collection) {
        if (collection instanceof List)
            return getRandom(random, ((List<T>) collection));

        int size = collection.size();
        if (size == 0)
            throw new IllegalArgumentException("Collection is empty");

        int randomIndex = random.nextInt(size);
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < randomIndex; i++)
            iterator.next();
        return iterator.next();
    }

    public static int nextInt(Random random, int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static double nextDouble(Random random, double min, double max) {
        return (random.nextDouble() * (max - min)) + min;
    }

    public static float nextFloat(Random random, float min, float max) {
        return (random.nextFloat() * (max - min)) + min;
    }

    public static Vector randomPointInCircle(Random random, float radius) {
        float r = (float) (radius * Math.sqrt(random.nextFloat()));
        float theta = random.nextFloat() * 2 * (float) Math.PI;
        return new Vector(
                r * Math.cos(theta),
                0,
                r * Math.sin(theta)
        );
    }
}
