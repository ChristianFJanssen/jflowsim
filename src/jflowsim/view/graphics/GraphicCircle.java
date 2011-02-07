package jflowsim.view.graphics;

import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.geometry2d.Circle2D;
import jflowsim.model.algebra.WorldViewTransformator2D;
import java.util.Observable;


public class GraphicCircle extends GraphicObject{

    public GraphicCircle(Circle2D circle, Color color){
        this.geometry2d = circle;
        this.color = color;
    }
    // zeichnet Kreis
    public void paint(Graphics g, WorldViewTransformator2D trafo) {

        Circle2D circle = (Circle2D)geometry2d;

        double x_world = circle.getCenterX();
        double y_world = circle.getCenterY();
        double radius = circle.getRadius();

        // view radius
        double min_x = trafo.transformWorldToViewXCoord(x_world-radius, y_world-radius, true);
        double max_x = trafo.transformWorldToViewXCoord(x_world+radius, y_world+radius, true);
        double view_radius = 0.5*(max_x - min_x);
        
        double view_cx = trafo.transformWorldToViewXCoord(x_world, y_world, true);
        double view_cy = trafo.transformWorldToViewYCoord(x_world, y_world, true);
        
        g.setColor(color);
        g.drawOval( (int)(view_cx-view_radius), (int)(view_cy-view_radius), (int)(2*view_radius), (int)(2*view_radius));
    }

    public GraphicObject clone() {
        return new GraphicCircle((Circle2D)geometry2d.clone(), color);
    }
}
