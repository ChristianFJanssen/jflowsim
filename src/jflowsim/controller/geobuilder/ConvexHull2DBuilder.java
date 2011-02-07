package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.ConvexHull2D;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.graphics.GraphicConvexHull;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConvexHull2DBuilder extends Geometry2DBuilder {

    public Geometry2D createGeometry2D() {
        return new ConvexHull2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicObject graphic = new GraphicConvexHull((ConvexHull2D) geo, Color.RED);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean permanent) {
        if (geoObj == null) {
            geoObj = new ConvexHull2D();
            try {
                ((ConvexHull2D) geoObj).setPoint(new Point2D(x, y, true));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConvexHull2DBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (permanent) {
            try {
                ((ConvexHull2D) geoObj).setPoint(new Point2D(x, y, true));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConvexHull2DBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void finishObject() {
        objFinished = true;
        ((ConvexHull2D) geoObj).getPointList().removeLast();
        ((ConvexHull2D) geoObj).refresh();
    }

    public Geometry2DBuilder clone() {
        return new ConvexHull2DBuilder();
    }
}
