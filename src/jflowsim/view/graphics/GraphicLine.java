package jflowsim.view.graphics;

import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Line2D;


public class GraphicLine extends GraphicObject{

    public GraphicLine(Line2D line, Color color){
        this.geometry2d = line;
        this.color = color;
    }
    
    public void paint(Graphics g, WorldViewTransformator2D trafo) {

        Line2D line = (Line2D)geometry2d;

        double p1x = line.getPoint1().getX();
        double p1y = line.getPoint1().getY();
        double p2x = line.getPoint2().getX();
        double p2y = line.getPoint2().getY();


        // view 
        double xView1 = trafo.transformWorldToViewXCoord(p1x, p1y, true);
        double yView1 = trafo.transformWorldToViewYCoord(p1x, p1y, true);

        double xView2 = trafo.transformWorldToViewXCoord(p2x, p2y, true);
        double yView2 = trafo.transformWorldToViewYCoord(p2x, p2y, true);

        g.setColor(color);       
        g.drawLine((int)xView1, (int)yView1, (int)(xView2), (int)(yView2) );

    }

    public GraphicObject clone() {
        return new GraphicLine((Line2D)geometry2d.clone(), color);
    }
}
