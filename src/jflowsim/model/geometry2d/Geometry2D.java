package jflowsim.model.geometry2d;

import jflowsim.model.numerics.UniformGrid;
import java.io.Serializable;
import java.util.Observable;

public abstract class Geometry2D extends Observable implements Serializable{

    public abstract Geometry2D clone();    

    public abstract void moveObject(double x, double y);

    public abstract double getCenterX();

    public abstract double getMinX();

    public abstract double getMaxX();

    public abstract double getCenterY();

    public abstract double getMinY();

    public abstract double getMaxY();

    public  abstract void map2Grid(UniformGrid grid);

    public String getObjectType() {
        return this.getClass().getSimpleName();
    }    

    public abstract boolean isPointInside(double x, double y);

    public abstract boolean isPointOnBoundary(double x, double y, double r);
}
