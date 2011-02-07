package jflowsim.controller.geobuilder;

import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.view.graphics.GraphicObject;
import java.util.ArrayList;
import java.util.TreeMap;

// Singelton Factory
public class Geometry2DFactory {

    private TreeMap<String, Geometry2DBuilder> builderSet = new TreeMap<String, Geometry2DBuilder>();
    private static Geometry2DFactory instance;

    private Geometry2DFactory() {
        builderSet.put("Circle2D", new Circle2DBuilder());
        builderSet.put("Rectangle2D", new Rectangle2DBuilder());
        builderSet.put("Polyline2D", new Polyline2DBuilder());
        builderSet.put("Line2D", new Line2DBuilder());
        builderSet.put("Bezier2D", new Bezier2DBuilder());
        builderSet.put("Spline2D", new Spline2DBuilder());
        builderSet.put("Delaunay2D", new Delaunay2DBuilder());
        builderSet.put("Triangle2D", new Triangle2DBuilder());
        builderSet.put("ConvexHull2D", new ConvexHull2DBuilder());
        builderSet.put("Polygon2D", new Polygon2DBuilder());
    }

    public static Geometry2DFactory getInstance() {
        if (instance == null) {
            instance = new Geometry2DFactory();
        }
        return instance;
    }

    public Geometry2D createGeoObj(String name) {
        return builderSet.get(name).createGeometry2D();
    }

    public GraphicObject createGraphicObj(String name, Geometry2D geo) {
        return builderSet.get(name).createGraphic2D(geo);
    }

    public Geometry2DBuilder getBuilder(String name) {
        return builderSet.get(name).clone();
    }

    public ArrayList<String> getKeySet() {
        return new ArrayList<String>(builderSet.keySet());
    }
}
