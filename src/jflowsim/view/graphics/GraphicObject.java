package jflowsim.view.graphics;

import java.awt.Graphics;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.algebra.WorldViewTransformator2D;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

public abstract class GraphicObject extends Observable implements Observer {

    protected Geometry2D geometry2d;
    protected Color color;

    public Geometry2D getGeometry2D() {
        return geometry2d;
    }

    public abstract void paint(Graphics g, WorldViewTransformator2D trafo);

    public abstract GraphicObject clone();

    // checks if coordinate (x,y) is inside the geometry
    public boolean isPointInside(double viewX, double viewY, WorldViewTransformator2D trafo) {
        double worldX, worldY;

        worldX = trafo.transformViewToWorldXCoord(viewX, viewY, false);
        worldY = trafo.transformViewToWorldYCoord(viewX, viewY, false);
        return this.geometry2d.isPointInside(worldX, worldY);
    }

    // checks if coordinate (x,y) is on the geometry boundary
    public boolean isPointOnBoundary(double viewX, double viewY, double fangradius_view, WorldViewTransformator2D trafo) {
        double fangradius = trafo.scaleViewToWorldLength(fangradius_view, false);

        double worldX = trafo.transformViewToWorldXCoord(viewX, viewY, false);
        double worldY = trafo.transformViewToWorldYCoord(viewX, viewY, false);

        return this.geometry2d.isPointOnBoundary(worldX, worldY, fangradius);
    }

    public void update(Observable o, Object arg) {
    }
}
