package jflowsim.view.graphics;

import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Polygon2D;
import java.awt.Color;
import java.awt.Graphics;

public class GraphicPolygon extends GraphicObject {

    public GraphicPolygon(Polygon2D polygon, Color color) {
        this.geometry2d = polygon;
        this.color = color;
    }

    public void paint(Graphics g, WorldViewTransformator2D trafo) {
        Polygon2D polygon = (Polygon2D) this.geometry2d;
        int Intervall = 10;
        double I = 0;
        int Kreuzbreite = 8;


        g.setColor(color);

        int a;
        int z;
        for (a = 0, z = polygon.GetPointList().size() - 1; a < polygon.GetPointList().size();
                z = a++) {

            int neuxa = (int) trafo.transformWorldToViewXCoord(polygon.GetPointList().get(a).getX(), polygon.GetPointList().get(a).getY(), true);
            int neuya = (int) trafo.transformWorldToViewYCoord(polygon.GetPointList().get(a).getX(), polygon.GetPointList().get(a).getY(), true);

            int neuxz = (int) trafo.transformWorldToViewXCoord(polygon.GetPointList().get(z).getX(), polygon.GetPointList().get(z).getY(), true);
            int neuyz = (int) trafo.transformWorldToViewYCoord(polygon.GetPointList().get(z).getX(), polygon.GetPointList().get(z).getY(), true);

            g.drawLine(neuxa, neuya, neuxz, neuyz);
        }
    }

    public GraphicObject clone() {
        return new GraphicPolygon((Polygon2D) geometry2d.clone(), color);
    }
}
