package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Bezier2D;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.view.graphics.GraphicBezier;
import jflowsim.view.graphics.GraphicObject;
import java.awt.Color;

public class Bezier2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Bezier2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicBezier graphic = new GraphicBezier((Bezier2D) geo, Color.PINK);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {

        if (geoObj == null) {
            geoObj = new Bezier2D();
            ((Bezier2D) geoObj).setPoint(new Point2D(x, y));
        } else if (objFinished) {
            ((Bezier2D) geoObj).setPoint(new Point2D(x, y));
        } else {
            if (((Bezier2D) geoObj).getPointList().size() > 1) {
                ((Bezier2D) geoObj).getPointList().removeLast();
            }
            ((Bezier2D) geoObj).setPoint(new Point2D(x, y));
        }
    }

    public Geometry2DBuilder clone() {
        return new Bezier2DBuilder();
    }

    public void finishObject() {
        this.objFinished = true;
        ((Bezier2D) geoObj).removeLastPoint();
    }
}
