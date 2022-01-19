package dev.fabien2s.annoyingapi.math.shape;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.util.Vector;

@NoArgsConstructor
@AllArgsConstructor
public class Point2D implements IShape {

    @Getter @Setter private double x;
    @Getter @Setter private double y;

    @Override
    public double distanceSqr(double x, double y) {
        double deltaX = this.x - x;
        double deltaY = this.y - y;
        return deltaX * deltaX + deltaY * deltaY;
    }

    @Override
    public Point2D closestPoint(double x, double y) {
        return new Point2D(this.x, this.y);
    }

    @Override
    public double getCenterX() {
        return x;
    }

    @Override
    public double getCenterY() {
        return y;
    }

    public Vector toVector(float y) {
        return new Vector(x, y, this.y);
    }

    @Override
    public String toString() {
        return "Point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
