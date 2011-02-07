package jflowsim.view.graphics;

import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.geometry2d.Triangle2D;

public class GraphicTriangle extends GraphicObject {

    public GraphicTriangle(Triangle2D triangle, Color color) {
        this.geometry2d = triangle;
        this.color = color;
    }
    
    public void paint(Graphics g, WorldViewTransformator2D trafo) {

        g.setColor(color);
        Point2D p1 = ((Triangle2D) geometry2d).p1;
        Point2D p2 = ((Triangle2D) geometry2d).p2;
        Point2D p3 = ((Triangle2D) geometry2d).p3;

        if (p1 != null && p2 != null && p3 != null) {
            int p1x_view = (int) trafo.transformWorldToViewXCoord(p1.getX(), p1.getY(), true);
            int p1y_view = (int) trafo.transformWorldToViewYCoord(p1.getX(), p1.getY(), true);
            int p2x_view = (int) trafo.transformWorldToViewXCoord(p2.getX(), p2.getY(), true);
            int p2y_view = (int) trafo.transformWorldToViewYCoord(p2.getX(), p2.getY(), true);
            int p3x_view = (int) trafo.transformWorldToViewXCoord(p3.getX(), p3.getY(), true);
            int p3y_view = (int) trafo.transformWorldToViewYCoord(p3.getX(), p3.getY(), true);

            g.drawLine(p1x_view, p1y_view, p2x_view, p2y_view);
            g.drawLine(p2x_view, p2y_view, p3x_view, p3y_view);
            g.drawLine(p3x_view, p3y_view, p1x_view, p1y_view);
        }

        // Kontrollpunkte zeichnen
        int k = 4;
        g.setColor(Color.BLACK);
        // p1
        if (p1 != null) {
            int p1x_view = (int) trafo.transformWorldToViewXCoord(p1.getX(), p1.getY(), true);
            int p1y_view = (int) trafo.transformWorldToViewYCoord(p1.getX(), p1.getY(), true);
            g.drawLine(p1x_view - k, p1y_view - k, p1x_view + k, p1y_view + k);
            g.drawLine(p1x_view + k, p1y_view - k, p1x_view - k, p1y_view + k);
        }
        // p2
        if (p2 != null) {
            int p2x_view = (int) trafo.transformWorldToViewXCoord(p2.getX(), p2.getY(), true);
            int p2y_view = (int) trafo.transformWorldToViewYCoord(p2.getX(), p2.getY(), true);
            g.drawLine(p2x_view - k, p2y_view - k, p2x_view + k, p2y_view + k);
            g.drawLine(p2x_view + k, p2y_view - k, p2x_view - k, p2y_view + k);
        }
        // p3
        if (p3 != null) {
            int p3x_view = (int) trafo.transformWorldToViewXCoord(p3.getX(), p3.getY(), true);
            int p3y_view = (int) trafo.transformWorldToViewYCoord(p3.getX(), p3.getY(), true);
            g.drawLine(p3x_view - k, p3y_view - k, p3x_view + k, p3y_view + k);
            g.drawLine(p3x_view + k, p3y_view - k, p3x_view - k, p3y_view + k);
        }
    }

    public GraphicObject clone() {
        return new GraphicTriangle((Triangle2D) geometry2d.clone(), color);
    }
}
