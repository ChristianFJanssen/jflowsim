package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Polyline2D;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.graphics.GraphicPolyline;
import java.awt.Color;

public class Polyline2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Polyline2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicPolyline graphic = new GraphicPolyline((Polyline2D) geo, Color.YELLOW);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {

        if (geoObj == null) {
            geoObj = new Polyline2D();
            ((Polyline2D) geoObj).setPoint(new Point2D(x, y));
        }

        if (objFinished) {
            ((Polyline2D) geoObj).setPoint(new Point2D(x, y));
        }else{
            ((Polyline2D) geoObj).getPointList().removeLast();
            Point2D newP = new Point2D(x, y);
            ((Polyline2D) geoObj).setPoint(newP);            
        }
    }

    public Geometry2DBuilder clone() {
        return new Polyline2DBuilder();
    }

    public void finishObject() {        
        this.objFinished = true;
        ((Polyline2D) geoObj).removeLastPoint();
    }
}
