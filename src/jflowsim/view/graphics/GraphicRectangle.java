package jflowsim.view.graphics;

import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Rectangle2D;


public class GraphicRectangle extends GraphicObject{

    public GraphicRectangle(Rectangle2D rect, Color color){
        this.geometry2d = rect;
        this.color = color;
    }
    
    public void paint(Graphics g, WorldViewTransformator2D trafo) {

        Rectangle2D rect = (Rectangle2D)geometry2d;

        double xView1 = trafo.transformWorldToViewXCoord(rect.getPoint1().getX(), rect.getPoint1().getY(), true);
        double yView1 = trafo.transformWorldToViewYCoord(rect.getPoint1().getX(), rect.getPoint1().getY(), true);
        double xView2 = trafo.transformWorldToViewXCoord(rect.getPoint2().getX(), rect.getPoint2().getY(), true);
        double yView2 = trafo.transformWorldToViewYCoord(rect.getPoint2().getX(), rect.getPoint2().getY(), true);

        g.setColor(color);
        g.drawRect((int) Math.min(xView1, xView2), (int) Math.min(yView1, yView2), (int) Math.abs(xView2-xView1), (int) Math.abs(yView2-yView1));
    }

    public GraphicObject clone() {
        return new GraphicRectangle((Rectangle2D)geometry2d.clone(), color);
    }
}
