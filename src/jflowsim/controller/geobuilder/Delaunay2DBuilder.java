package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Delaunay2D;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Spline2D;
import jflowsim.view.graphics.GraphicDelaunay;
import jflowsim.view.graphics.GraphicObject;
import java.awt.Color;

public class Delaunay2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Spline2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicDelaunay graphic = new GraphicDelaunay((Delaunay2D) geo, Color.GREEN);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {

        if (geoObj == null) {
            geoObj = new Delaunay2D();
            ((Delaunay2D) geoObj).setPoint(new Point2D(x, y));
        } else if (objFinished) {
            ((Delaunay2D) geoObj).setPoint(new Point2D(x, y));
        }
    }

    public Geometry2DBuilder clone() {
        return new Delaunay2DBuilder();
    }

    public void finishObject() {
        this.objFinished = true;
    }
}
