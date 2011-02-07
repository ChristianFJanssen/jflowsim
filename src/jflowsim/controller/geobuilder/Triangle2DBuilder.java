package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Line2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Triangle2D;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.graphics.GraphicTriangle;
import java.awt.Color;

public class Triangle2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Line2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicObject graphic = new GraphicTriangle((Triangle2D) geo, Color.ORANGE);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {
        if (geoObj == null) {
            geoObj = new Triangle2D();
            ((Triangle2D) geoObj).setPoint(new Point2D(x, y));
        } else if (objFinished) {
            ((Triangle2D) geoObj).setPoint(new Point2D(x, y));
            if (((Triangle2D) geoObj).p3 != null) {
                this.objFinished = true;
            }
        }
    }

    public Geometry2DBuilder clone() {
        return new Triangle2DBuilder();
    }

    public void finishObject() {
    }
}
