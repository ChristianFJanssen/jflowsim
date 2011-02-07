package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Rectangle2D;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.graphics.GraphicRectangle;
import java.awt.Color;

public class Rectangle2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new Rectangle2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicObject graphic = new GraphicRectangle((Rectangle2D) geo, Color.GREEN);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {
        if (geoObj == null) {
            geoObj = new Rectangle2D();
            ((Rectangle2D) geoObj).setPoint1(new Point2D(x, y));
            ((Rectangle2D) geoObj).setPoint2(new Point2D(x, y));
        } else {
            Rectangle2D rect = ((Rectangle2D) geoObj);
            ((Rectangle2D) geoObj).setPoint2(new Point2D(x, y));
            this.objFinished = objFinished;
        }
    }

    public Geometry2DBuilder clone() {
        return new Rectangle2DBuilder();
    }

    public void finishObject() {
    }
}
