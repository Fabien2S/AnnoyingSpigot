package dev.fabien2s.annoyingapi.math.shape;

import dev.fabien2s.annoyingapi.math.MathHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
public class Box2D implements IShape {

    @Getter private double minX;
    @Getter private double minY;
    @Getter private double maxX;
    @Getter private double maxY;

    @Override
    public double distanceSqr(double x, double y) {
        double deltaX = Math.max(Math.max(minX - x, x - maxX), 0);
        double deltaZ = Math.max(Math.max(minY - y, y - maxY), 0);
        return deltaX * deltaX + deltaZ * deltaZ;
    }

    @Override
    public Point2D closestPoint(double x, double y) {
        return new Point2D(
                MathHelper.clamp(x, minX, maxX),
                MathHelper.clamp(y, minY, maxY)
        );
    }

    @NotNull
    public Box2D resize(double x1, double y1, double x2, double y2) {
        NumberConversions.checkFinite(x1, "x1 not finite");
        NumberConversions.checkFinite(y1, "y1 not finite");
        NumberConversions.checkFinite(x2, "x2 not finite");
        NumberConversions.checkFinite(y2, "y2 not finite");
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        return this;
    }

    public double getWidthX() {
        return this.maxX - this.minX;
    }

    public double getHeight() {
        return this.maxY - this.minY;
    }

    public double getVolume() {
        return this.getHeight() * this.getWidthX();
    }

    public double getCenterX() {
        return this.minX + this.getWidthX() * 0.5D;
    }

    public double getCenterY() {
        return this.minY + this.getHeight() * 0.5D;
    }

    @Override
    public String toString() {
        return "Box2D{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                '}';
    }

}
