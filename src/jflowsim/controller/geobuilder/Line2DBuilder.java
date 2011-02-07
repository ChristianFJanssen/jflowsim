package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Line2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.view.graphics.GraphicLine;
import jflowsim.view.graphics.GraphicObject;
import java.awt.Color;

public class Line2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Line2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicObject graphic = new GraphicLine((Line2D) geo, Color.cyan);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {
        if (geoObj == null) {
            geoObj = new Line2D();
            ((Line2D) geoObj).setPoint1(new Point2D(x, y));
            ((Line2D) geoObj).setPoint2(new Point2D(x, y));
        } else {
            ((Line2D) geoObj).setPoint2(new Point2D(x, y));
            this.objFinished = objFinished;
        }
    }

    public Geometry2DBuilder clone() {
        return new Line2DBuilder();
    }

    public void finishObject() {
    }
}
