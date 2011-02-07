package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Spline2D;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.graphics.GraphicSpline;
import java.awt.Color;

public class Spline2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Spline2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicSpline graphic = new GraphicSpline((Spline2D) geo, Color.GREEN);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {

        if (geoObj == null) {
            geoObj = new Spline2D();
            ((Spline2D) geoObj).setPoint(new Point2D(x, y));
        } else if (objFinished) {
            ((Spline2D) geoObj).setPoint(new Point2D(x, y));
        } else {
            if (((Spline2D) geoObj).getPointList().size() > 1) {
                ((Spline2D) geoObj).getPointList().removeLast();
            }
            ((Spline2D) geoObj).setPoint(new Point2D(x, y));
        }
    }

    public Geometry2DBuilder clone() {
        return new Spline2DBuilder();
    }

    public void finishObject() {
        this.objFinished = true;
        ((Spline2D) geoObj).removeLastPoint();
    }
}
