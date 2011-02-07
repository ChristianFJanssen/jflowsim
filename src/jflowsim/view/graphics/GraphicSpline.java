package jflowsim.view.graphics;

import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Spline2D;
import java.util.LinkedList;

public class GraphicSpline extends GraphicObject {

    public GraphicSpline(Spline2D spline, Color color) {
        this.geometry2d = spline;
        this.color = color;
    }

    public void paint(Graphics g, WorldViewTransformator2D trafo) {

        Spline2D spline = (Spline2D) geometry2d;
        LinkedList<Point2D> pointList = spline.getPointList();

        int numOfPoints = 100;

        // Spline zeichnen
        if (pointList.size() > 2) {
            for (int i = 0; i < numOfPoints - 1; i++) {
                g.setColor(color);

                double t1 = (i) / (double) (numOfPoints - 1);
                double t2 = (i + 1.0) / (double) (numOfPoints - 1);

                Point2D p1 = spline.getPointOnSpline(t1);
                Point2D p2 = spline.getPointOnSpline(t2);

                int p1x_view = (int) trafo.transformWorldToViewXCoord(p1.getX(), p1.getY(), true);
                int p1y_view = (int) trafo.transformWorldToViewYCoord(p1.getX(), p1.getY(), true);
                int p2x_view = (int) trafo.transformWorldToViewXCoord(p2.getX(), p2.getY(), true);
                int p2y_view = (int) trafo.transformWorldToViewYCoord(p2.getX(), p2.getY(), true);

                // draw line
                g.drawLine(p1x_view, p1y_view, p2x_view, p2y_view);
            }
        }


        // Kontrollpunkte zeichnen
        for (int i = 0; i < pointList.size(); i++) {
            Point2D p1 = pointList.get(i);

            int p1x_view = (int) trafo.transformWorldToViewXCoord(p1.getX(), p1.getY(), true);
            int p1y_view = (int) trafo.transformWorldToViewYCoord(p1.getX(), p1.getY(), true);

            int k = 4;
            g.setColor(Color.BLACK);
            g.drawLine(p1x_view - k, p1y_view - k, p1x_view + k, p1y_view + k);
            g.drawLine(p1x_view + k, p1y_view - k, p1x_view - k, p1y_view + k);
        }
    }

    public GraphicObject clone() {
        return new GraphicSpline((Spline2D) this.geometry2d.clone(), color);
    }
}
