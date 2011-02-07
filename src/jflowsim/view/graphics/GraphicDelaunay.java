package jflowsim.view.graphics;

import jflowsim.controller.geobuilder.Geometry2DFactory;
import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Delaunay2D;
import jflowsim.model.geometry2d.Triangle2D;
import java.util.LinkedList;

public class GraphicDelaunay extends GraphicObject {

    public GraphicDelaunay(Delaunay2D delaunay, Color color) {
        this.geometry2d = delaunay;
        this.color = color;
    }

    public void paint(Graphics g, WorldViewTransformator2D trafo) {
  
        LinkedList<Triangle2D> triangleList = ((Delaunay2D) geometry2d).getTriangleList();

        for(Triangle2D triangle : triangleList){
            GraphicTriangle graphic = (GraphicTriangle)Geometry2DFactory.getInstance().createGraphicObj("Triangle2D", triangle);
            graphic.paint(g, trafo);
        }
    }

    public GraphicObject clone() {
        return new GraphicDelaunay((Delaunay2D) this.geometry2d.clone(), color);
    }
}
