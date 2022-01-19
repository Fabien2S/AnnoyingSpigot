package dev.fabien2s.annoyingapi.math.shape;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ComplexShape implements IShape {

    private final IShape[] shapes;

    @Override
    public double distanceSqr(double x, double y) {
        double distance = Double.POSITIVE_INFINITY;
        for (IShape shape : shapes) {
            double d = shape.distanceSqr(x, y);
            if (d < distance)
                distance = d;
        }
        return distance;
    }

    @Override
    public Point2D closestPoint(double x, double y) {
        double distance = Double.POSITIVE_INFINITY;
        Point2D closestPoint = new Point2D();
        for (IShape shape : shapes) {
            Point2D p = shape.closestPoint(x, y);
            double d = p.distanceSqr(x, y);
            if (d < distance) {
                distance = d;
                closestPoint = p;
            }
        }
        return closestPoint;
    }

    @Override
    public double getCenterX() {
        return 0;
    }

    @Override
    public double getCenterY() {
        return 0;
    }

    public static IShape of(IShape... shapes) {
        return new ComplexShape(shapes);
    }

    public static IShape nearest(Collection<IShape> shapes, double x, double y) {
        double distance = Double.POSITIVE_INFINITY;
        IShape closestShape = null;
        for (IShape shape : shapes) {
            double d = shape.distanceSqr(x, y);
            if (d < distance) {
                distance = d;
                closestShape = shape;
            }
        }
        return closestShape;
    }

}
