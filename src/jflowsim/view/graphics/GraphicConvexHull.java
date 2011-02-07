package jflowsim.view.graphics;

import jflowsim.controller.geobuilder.Geometry2DFactory;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.ConvexHull2D;
import java.awt.Color;
import java.awt.Graphics;

public class GraphicConvexHull extends GraphicObject {

    public GraphicConvexHull(ConvexHull2D convexHull2D, Color color) {
        this.geometry2d = convexHull2D;
        this.color = color;
    }

    public void paint(Graphics g, WorldViewTransformator2D trafo) {
        g.setColor(color);
        ConvexHull2D convexHull = (ConvexHull2D) geometry2d;


        GraphicObject graphic = Geometry2DFactory.getInstance().createGraphicObj("Polyline2D", convexHull.getMerge());
        graphic.paint(g, trafo);

        g.setColor(Color.BLACK);
        for (Point2D p : convexHull.getPoly().getPointList()) {
            double x = trafo.transformWorldToViewXCoord(p.getX(), p.getY(), true);
            double y = trafo.transformWorldToViewYCoord(p.getX(), p.getY(), true);
            g.drawLine((int) (x), (int) (y - 2), (int) (x), (int) (y + 2));
            g.drawLine((int) (x - 2), (int) (y), (int) (x + 2), (int) (y));
        }
    }

    public GraphicObject clone() {
        return new GraphicConvexHull((ConvexHull2D) geometry2d.clone(), color);
    }
}
