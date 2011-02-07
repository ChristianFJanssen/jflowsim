package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Circle2D;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.view.graphics.GraphicCircle;
import jflowsim.view.graphics.GraphicObject;
import java.awt.Color;


public class Circle2DBuilder extends Geometry2DBuilder{

    public Geometry2D createGeometry2D() {
        return new Circle2D();
    }

    public GraphicObject createGraphic2D(Geometry2D geo) {
        GraphicObject graphic = new GraphicCircle((Circle2D)geo, Color.RED);
        geo.addObserver(graphic);
        return graphic;
    }

    public void setPoint(double x, double y, boolean objFinished) {
        if(geoObj==null){
            geoObj = new Circle2D();
            geoObj.moveObject(x, y);
        }else{
            Circle2D circle = ((Circle2D)geoObj);
            double Ax = Math.abs(circle.getCenterX()-x);
            double Ay = Math.abs(circle.getCenterY()-y);
            double radius = Math.sqrt(Ax*Ax + Ay*Ay);

            circle.setRadius(radius);
            this.objFinished = objFinished;
        }
    }

    public Geometry2DBuilder clone() {
        return new Circle2DBuilder();
    }

    public void finishObject() {
    }

}
