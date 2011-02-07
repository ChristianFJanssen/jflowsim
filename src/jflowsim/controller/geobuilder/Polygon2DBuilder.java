package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Polygon2D;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.graphics.GraphicPolygon;
import java.awt.Color;

public class Polygon2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Polygon2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicObject graphic = new GraphicPolygon((Polygon2D) geo, Color.white);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {
        if (geoObj == null) {
            geoObj = new Polygon2D();

            ((Polygon2D) geoObj).setPoint(new Point2D(x, y));

        } else {
            if (objFinished) {
                ((Polygon2D) geoObj).setPoint(new Point2D(x, y));
            } else {
                if (((Polygon2D) geoObj).GetPointList().size() > 1) {
                    ((Polygon2D) geoObj).removeLastPoint();
                }
                ((Polygon2D) geoObj).setPoint(new Point2D(x, y));
            }
        }
    }

    public void finishObject() {
        this.objFinished = true;
        ((Polygon2D) geoObj).removeLastPoint();
    }

    public Geometry2DBuilder clone() {
        return new Polygon2DBuilder();
    }
}
