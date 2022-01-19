package dev.fabien2s.annoyingapi.math;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MathHelper {

    public static final double PI = Math.PI;
    public static final double PI_2 = PI * 2;

    public static final double RAD_2_DEG = 180 / PI;
    public static final double DEG_2_RAD = PI / 180;

    public static final double RAD_MAX_ANGLE = PI * 2;
    public static final double RAD_HALF_ANGLE = PI;
    public static final double DEG_MAX_ANGLE = 360;
    public static final double DEG_HALF_ANGLE = 180;

    public static double lerp(double start, double end, double t) {
        return start + ((end - start) * t);
    }

    public static double lerpAngle(double start, double end, double value) {
        var delta = (end - start) % DEG_MAX_ANGLE;
        return start + (2 * delta % DEG_MAX_ANGLE - delta) * clamp01(value);
    }

    public static double normalize(double normalized, double start, double end) {
        return (normalized - start) / (end - start);
    }

    public static float map(float value, float currentStart, float currentEnd, float expectedStart, float expectedEnd) {
        return (value - currentStart) / (currentEnd - currentStart) * (expectedEnd - expectedStart) + expectedStart;
    }

    public static double map(double value, double currentStart, double currentEnd, double expectedStart, double expectedEnd) {
        return (value - currentStart) / (currentEnd - currentStart) * (expectedEnd - expectedStart) + expectedStart;
    }

    public static double snap(double value, double a, double b) {
        return Math.abs(value - a) <= Math.abs(value - b) ? a : b;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static double clamp01(double value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }
}
