package jflowsim.model.geometry2d.utilities;


import jflowsim.model.geometry2d.Point2D;


public class BoundingBox2D {
    private Point2D min, max;


    public BoundingBox2D(Point2D min, Point2D max){
        this.min = min;
        this.max = max;
    }

    public Point2D getMin() {
        return min;
    }

    public Point2D getMax() {
        return max;
    }
}
