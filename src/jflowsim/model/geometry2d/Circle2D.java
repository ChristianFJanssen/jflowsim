package jflowsim.model.geometry2d;

import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;

public class Circle2D extends Geometry2D {

    private double radius;
    private Point2D center;

    public Circle2D() {
        this.center = new Point2D(0.0, 0.0);
        this.radius = 0.0;
        super.setChanged();
        super.notifyObservers();
    }

    public Circle2D(Point2D center, double radius) {
        this.center = center;
        this.radius = radius;
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        return new Circle2D(center.clone(), radius);
    }

    public void moveObject(double x, double y) {
        this.center.setX(x);
        this.center.setY(y);
        super.setChanged();
        super.notifyObservers();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        super.setChanged();
        super.notifyObservers();
    }

    // testet ob Koordinaten (x,y) innerhalb des Kreis liegen 
    public boolean isPointInside(double x, double y) {

        double dx = center.getX() - x;
        double dy = center.getY() - y;

        if (Math.sqrt(dx * dx + dy * dy) <= radius) {
            return true;
        } else {
            return false;
        }
    }

    public double getCenterX() {
        return this.center.getX();
    }

    public double getMinX() {
        return center.getX() - radius;
    }

    public double getMaxX() {
        return center.getX() + radius;
    }

    public double getCenterY() {
        return center.getY();
    }

    public double getMinY() {
        return center.getY() - radius;
    }

    public double getMaxY() {
        return center.getY() + radius;
    }

    public boolean isPointOnBoundary(double x, double y, double r) {

        double min_distance = this.radius - r;
        double max_distance = this.radius + r;
        double distance = Math.sqrt((center.getX() - x) * (center.getX() - x) + (center.getY() - y) * (center.getY() - y));
        return (distance >= min_distance && distance <= max_distance);
    }

    public void map2Grid(UniformGrid grid) {

        int x1 = grid.transCoord2XIndex(getMinX(),Rounding.FLOOR);
        int y1 = grid.transCoord2YIndex(getMinY(),Rounding.FLOOR);
        int x2 = grid.transCoord2XIndex(getMaxX(),Rounding.FLOOR);
        int y2 = grid.transCoord2YIndex(getMaxY(),Rounding.FLOOR);

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {

                if(isPointInside(grid.transXIndex2Coord(x), grid.transYIndex2Coord(y))){
                    if(grid.getType(x,y) == GridNodeType.FLUID)
                        grid.setType(x,y,GridNodeType.SOLID);
                }
            }
        }
    }
}
