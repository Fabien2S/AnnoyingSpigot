package dev.fabien2s.annoyingapi.math.shape;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Circle2D implements IShape {

    @Getter private double x;
    @Getter private double y;
    @Getter private double radius;

    @Override
    public double distanceSqr(double x, double y) {
        double deltaX = this.x - x;
        double deltaY = this.y - y;
        return ((deltaX * deltaX) + (deltaY * deltaY)) - (radius * radius);
    }

    @Override
    public Point2D closestPoint(double x, double y) {
        double dX = x - this.x;
        double dY = y - this.y;
        double length = Math.sqrt(dX * dX + dY * dY);
        return new Point2D(
                this.x + dX / length * radius,
                this.y + dY / length * radius
        );
    }

    @Override
    public double getCenterX() {
        return x;
    }

    @Override
    public double getCenterY() {
        return y;
    }

    @Override
    public String toString() {
        return "Circle2D{" +
                "x=" + x +
                ", y=" + y +
                ", radius=" + radius +
                '}';
    }

}
