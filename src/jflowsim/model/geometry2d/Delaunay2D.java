package jflowsim.model.geometry2d;

import jflowsim.model.numerics.UniformGrid;
import java.util.LinkedList;

public class Delaunay2D extends Geometry2D {

    private LinkedList<Point2D> pointList;
    private LinkedList<Triangle2D> triangleList;
    private Point2D center;
    private Point2D selectedPoint = null;
    private double minX,  minY,  maxX,  maxY;

    public Delaunay2D() {
        this.center = new Point2D(0.0, 0.0);
        this.pointList = new LinkedList<Point2D>();
        this.triangleList = new LinkedList<Triangle2D>();
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        Delaunay2D del = new Delaunay2D();
        del.pointList = pointList;
        return del;
    }

    public void setPoint(Point2D p) {
        pointList.add(p);
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public void removePoint(Point2D p) {
        pointList.remove(p);
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    // testet ob Koordinaten (x,y) innerhalb der Geometrie liegen
    public boolean isPointInside(double x, double y) {
        return false;
    }

    public void moveObject(double x, double y) {
        // Punkt verschieben
        if (selectedPoint != null) {
            this.selectedPoint.setX(x);
            this.selectedPoint.setY(y);
        }//
        // Kanten verschieben
        else {
            double dx = x - getCenterX();
            double dy = y - getCenterY();
            for (Point2D p : pointList) {
                p.setX(p.getX() + dx);
                p.setY(p.getY() + dy);
            }
        }
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    private void calculateValues() {

        minY = minX = Double.MAX_VALUE;
        maxX = maxY = -Double.MAX_VALUE;
        for (Point2D p : pointList) {
            if (p.getX() < minX) {
                minX = p.getX();
            }
            if (p.getY() < minY) {
                minY = p.getY();
            }
            if (p.getX() > maxX) {
                maxX = p.getX();
            }
            if (p.getY() > maxY) {
                maxY = p.getY();
            }
        }
        this.center.setX(0.5 * (maxX - minX));
        this.center.setY(0.5 * (maxY - minY));

        triangulate();
    }


    public double getCenterX() {
        if (selectedPoint != null) {
            return selectedPoint.getX();
        } else {
            return this.center.getX();
        }
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getCenterY() {
        if (selectedPoint != null) {
            return selectedPoint.getY();
        } else {
            return center.getY();
        }
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public boolean isPointOnBoundary(double x, double y, double r) {

        // Prüft ob Koordinate (x,y) auf einem Kontrollpunkt der Bezierkurve liegt
        for (Point2D p : pointList) {
            if ((Math.abs(x - p.getX()) < r) && (Math.abs(y - p.getY()) < r)) {
                this.selectedPoint = p;
                return true;
            }
        }
        this.selectedPoint = null;

        // Prüft ob Koordinate (x,y) auf einer Kante der Triangulierung liegt
        for(Triangle2D tri : triangleList){
            if(tri.isPointOnBoundary(x, y, r)){
                return true;
            }
        }

        return false;
    }

    public LinkedList<Point2D> getPointList() {
        return pointList;
    }

    private double getMaxDimension() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (Point2D p : pointList) {
            if (p.getX() < minX) {
                minX = p.getX();
            }
            if (p.getX() > maxX) {
                maxX = p.getX();
            }
            if (p.getY() < minY) {
                minY = p.getY();
            }
            if (p.getY() > maxY) {
                maxY = p.getY();
            }
        }
        return Math.max(Math.abs(maxX - minX), maxY - minY);
    }

    private Triangle2D findTriangleWithEdge(Point2D p1, Point2D p2) {
        for (Triangle2D triangle : triangleList) {
            if (triangle.p1 == p1 || triangle.p2 == p1 || triangle.p3 == p1) {
                if (triangle.p1 == p2 || triangle.p2 == p2 || triangle.p3 == p2) {
                    return triangle;
                }
            }
        }
        return null;
    }

    private void triangulate() {

        // Abbrechen bei weniger als drei Punkten
        if (pointList.size() < 3) {
            return;
        }

        // lösche alte Triangulierung
        triangleList.clear();

        // Finde maximale Ausdehnung
        double m = getMaxDimension();

        // baue Ausgangsdreieck
        Point2D p1 = new Point2D(0.0, 3.0 * m);
        Point2D p2 = new Point2D(3.0 * m, 0.0);
        Point2D p3 = new Point2D(-3.0 * m, -3.0 * m);
        triangleList.add(new Triangle2D(p1, p2, p3));


        // Füge Punkte der Liste hinzu
        for (Point2D p : pointList) {
            LinkedList<Triangle2D> newTriangles = new LinkedList<Triangle2D>();
            for (int i = 0; i < triangleList.size(); i++) {
                Triangle2D triangle = triangleList.get(i);
                if (triangle.isPointInside(p)) {

                    // altes Dreieck entfernen
                    triangleList.remove(i); i--;

                    // neue Dreiecke bauen
                    Triangle2D t1 = new Triangle2D(triangle.p1, triangle.p2, p);
                    Triangle2D t2 = new Triangle2D(triangle.p2, triangle.p3, p);
                    Triangle2D t3 = new Triangle2D(triangle.p3, triangle.p1, p);

                    // neue Dreiecke hinzufügen
                    if (t1.getArea() > 1.E-8) {
                        newTriangles.add(t1);
                    }
                    if (t2.getArea() > 1.E-8) {
                        newTriangles.add(t2);
                    }
                    if (t3.getArea() > 1.E-8) {
                        newTriangles.add(t3);
                    }

                    // Umkreisbedingung überprüfen - Kante 1
                    Triangle2D tmpT1 = findTriangleWithEdge(triangle.p1, triangle.p2);
                    if (tmpT1 != null && t1.isPointInCircumCircle(tmpT1.getOppositePoint(triangle.p1, triangle.p2))) {
                        t1.flipEdge(tmpT1);
                    }
                    // Umkreisbedingung überprüfen - Kante 2
                    Triangle2D tmpT2 = findTriangleWithEdge(triangle.p2, triangle.p3);
                    if (tmpT2 != null && t2.isPointInCircumCircle(tmpT2.getOppositePoint(triangle.p2, triangle.p3))) {
                        t2.flipEdge(tmpT2);
                    }
                    // Umkreisbedingung überprüfen - Kante 3
                    Triangle2D tmpT3 = findTriangleWithEdge(triangle.p3, triangle.p1);
                    if (tmpT3 != null && t3.isPointInCircumCircle(tmpT3.getOppositePoint(triangle.p3, triangle.p1))) {
                        t3.flipEdge(tmpT3);
                    }
                }
            }
            triangleList.addAll(newTriangles);
        }
	// alle Dreiecke mit Verbindung zum äußeren Dreieck entfernen
        for (int i = 0; i < triangleList.size(); i++) {
            Triangle2D triangle = triangleList.get(i);
            if (triangle.contains(p1) || triangle.contains(p2) || triangle.contains(p3)) {
                triangleList.remove(i); i--;
            }
        }
    }
   
    public LinkedList<Triangle2D> getTriangleList() {
        return triangleList;
    }

    public void map2Grid(UniformGrid grid) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
