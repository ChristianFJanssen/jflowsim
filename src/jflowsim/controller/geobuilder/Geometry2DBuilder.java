package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.view.graphics.GraphicObject;

public abstract class Geometry2DBuilder {

    protected Geometry2D geoObj = null;
    
    protected boolean objFinished = false;

    public abstract Geometry2D createGeometry2D();

    public abstract GraphicObject createGraphic2D(Geometry2D geo);

    public abstract void setPoint(double x, double y, boolean objFinished);

    public abstract void finishObject();

    public abstract Geometry2DBuilder clone();

    public boolean isObjFinished() {
        return objFinished;
    }

    public Geometry2D getGeoObj() {
        return geoObj;
    }
}
