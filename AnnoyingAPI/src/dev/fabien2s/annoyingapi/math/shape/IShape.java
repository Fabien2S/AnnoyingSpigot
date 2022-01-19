package dev.fabien2s.annoyingapi.math.shape;

public interface IShape {

    default double distanceSqr(IShape other) {
        double x = getCenterX();
        double y = getCenterY();
        Point2D point = other.closestPoint(x, y);
        return distanceSqr(point.getX(), point.getY());
    }

    double distanceSqr(double x, double y);

    default Point2D closestPoint(IShape other) {
        double x = getCenterX();
        double y = getCenterY();
        Point2D point = other.closestPoint(x, y);
        return closestPoint(point.getX(), point.getY());
    }

    Point2D closestPoint(double x, double y);

    double getCenterX();

    double getCenterY();

}
