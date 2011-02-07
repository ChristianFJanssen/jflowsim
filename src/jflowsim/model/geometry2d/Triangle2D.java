package jflowsim.model.geometry2d;

import jflowsim.model.geometry2d.utilities.Bresenham;
import jflowsim.model.geometry2d.utilities.FloodFill;
import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.UniformGrid;

public class Triangle2D extends Geometry2D {

    public Point2D p1, p2, p3;
    private Point2D center;
    private Point2D selectedPoint = null;
    private double minX, minY, maxX, maxY;

    public Triangle2D() {
        this.center = new Point2D(0.0, 0.0);
        super.setChanged();
        super.notifyObservers();
    }

    public Triangle2D(Point2D p1, Point2D p2, Point2D p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.center = new Point2D(0.0, 0.0);
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        return new Triangle2D(p1, p2, p3);
    }

    public void setPoint(Point2D p) {
        if (p1 == null) {
            this.p1 = p;
        } else if (p2 == null) {
            this.p2 = p;
        } else {
            this.p3 = p;
        }

        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public double getArea() {

        double a = p1.getDistance(p2);
        double b = p2.getDistance(p3);
        double c = p3.getDistance(p1);
        double s = (a + b + c) * 0.5;
        // Heronsche Flächenformel:
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
    // testet ob Koordinaten (x,y) innerhalb des Kreis liegen
    public boolean isPointInside(Point2D p) {
        return isPointInside(p.getX(), p.getY());
    }

    public boolean isPointInside(double x, double y) {
        if (isPointOnEdge(x, y, p1, p2, 0.0)) {
            return true;
        }
        if (isPointOnEdge(x, y, p2, p3, 0.0)) {
            return true;
        }
        if (isPointOnEdge(x, y, p3, p1, 0.0)) {
            return true;
        }

        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();

        double denominator = (y2 * x1 - y2 * x3 + y1 * x3 - x1 * y3 - x2 * y1 + x2 * y3);

        if (Math.abs(denominator) < 1.0E-8) {
            System.out.println("Die Flaeche vom Dreieck ist null !");
        }
        denominator = 1. / denominator;

        double zae_a = (-y2 * x3 + x2 * y3 - y3 * x + y * x3 + y2 * x - x2 * y);
        double a = zae_a * denominator;
        if (a > 0.0 && a < 1.0) {
            double zae_b = -(y1 * x - y1 * x3 - y3 * x + x1 * y3 + y * x3 - x1 * y);
            double b = zae_b * denominator;
            if (b > 0.0 && b < 1.0) {
                double zae_c = (y2 * x1 + y1 * x - x1 * y - y2 * x - x2 * y1 + x2 * y);
                double c = zae_c * denominator;
                if (c > 0.0 && c < 1.0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void moveObject(double x, double y) {
        // Punkt verschieben
        if (selectedPoint != null) {
            this.selectedPoint.setX(x);
            this.selectedPoint.setY(y);
        } // Dreieck verschieben
        else {
            double dx = x - center.getX();
            double dy = y - center.getY();

            p1.setX(p1.getX() + dx);
            p1.setY(p1.getY() + dy);

            p2.setX(p2.getX() + dx);
            p2.setY(p2.getY() + dy);

            p3.setX(p3.getX() + dx);
            p3.setY(p3.getY() + dy);
        }
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    private void calculateValues() {
        if (p1 != null && p2 != null && p3 != null) {
            minY = minX = Double.MAX_VALUE;
            maxX = maxY = -Double.MAX_VALUE;

            // p1
            if (p1.getX() < minX) {
                minX = p1.getX();
            }
            if (p1.getY() < minY) {
                minY = p1.getY();
            }
            if (p1.getX() > maxX) {
                maxX = p1.getX();
            }
            if (p1.getY() > maxY) {
                maxY = p1.getY();
            }
            // p2
            if (p2.getX() < minX) {
                minX = p2.getX();
            }
            if (p2.getY() < minY) {
                minY = p2.getY();
            }
            if (p2.getX() > maxX) {
                maxX = p2.getX();
            }
            if (p2.getY() > maxY) {
                maxY = p2.getY();
            }
            // p3
            if (p3.getX() < minX) {
                minX = p3.getX();
            }
            if (p3.getY() < minY) {
                minY = p3.getY();
            }
            if (p3.getX() > maxX) {
                maxX = p3.getX();
            }
            if (p3.getY() > maxY) {
                maxY = p3.getY();
            }

            this.center.setX(minX + 0.5 * (maxX - minX));
            this.center.setY(minY + 0.5 * (maxY - minY));
        }
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

    boolean contains(Point2D p) {
        return (p1 == p || p2 == p || p3 == p);
    }

    public boolean isPointOnBoundary(double x, double y, double r) {

        // Prüft ob Mausklick (x,y) auf einem Eckpunkt des Dreiecks liegt
        if ((Math.abs(x - p1.getX()) < r) && (Math.abs(y - p1.getY()) < r)) {
            this.selectedPoint = p1;
            return true;
        }
        if ((Math.abs(x - p2.getX()) < r) && (Math.abs(y - p2.getY()) < r)) {
            this.selectedPoint = p2;
            return true;
        }
        if ((Math.abs(x - p3.getX()) < r) && (Math.abs(y - p3.getY()) < r)) {
            this.selectedPoint = p3;
            return true;
        }

        this.selectedPoint = null;

        return isPointOnEdge(x, y, p1, p2, r) || isPointOnEdge(x, y, p2, p3, r) || isPointOnEdge(x, y, p3, p1, r);
    }

    public boolean isPointOnEdge(double x, double y, Point2D pp1, Point2D pp2, double r) {
        // Prüft ob Koordinate (x,y) auf einer Kante des Polygonzugs liegt
        double distance;

        double xa = pp1.getX();
        double ya = pp1.getY();
        double xb = pp2.getX();
        double yb = pp2.getY();
        double t = 2.0 * x * xb - 2.0 * x * xa + 2.0 * y * yb - 2.0 * y * ya + ya * ya + xa * xa - yb * yb - xb * xb;
        t /= yb * yb - 2.0 * yb * ya + ya * ya + xb * xb - 2.0 * xb * xa + xa * xa;

        if (Math.abs(t) <= 1.) {
            double xd = xa * (0.5 - 0.5 * t) + xb * (0.5 + 0.5 * t);
            double yd = ya * (0.5 - 0.5 * t) + yb * (0.5 + 0.5 * t);

            distance = Math.sqrt((xd - x) * (xd - x) + (yd - y) * (yd - y));
        } else // D liegt außerhalb Strecke AB
        {
            double tmpDistance1 = Math.sqrt((xa - x) * (xa - x) + (ya - y) * (ya - y));
            double tmpDistance2 = Math.sqrt((xb - x) * (xb - x) + (yb - y) * (yb - y));

            distance = Math.min(tmpDistance1, tmpDistance2);
        }
        if (distance <= r) {
            return true;
        } else {
            return false;
        }
    }

    boolean isPointInCircumCircle(Point2D point) {

        double a1 = p1.getX();
        double a2 = p1.getY();

        double b1 = p2.getX();
        double b2 = p2.getY();

        double c1 = p3.getX();
        double c2 = p3.getY();

        double a1_sq = a1 * a1;
        double a2_sq = a2 * a2;
        double b1_sq = b1 * b1;
        double b2_sq = b2 * b2;
        double c1_sq = c1 * c1;
        double c2_sq = c2 * c2;

        double delta = 4.0 * (a1 - b1) * (b2 - c2) - 4.0 * (b1 - c1) * (a2 - b2);

        if (Math.abs(delta) < 1.E-8) {
            return false;
        }
        double delta1 = 2.0 * (a1_sq + a2_sq - b1_sq - b2_sq) * (b2 - c2) - 2.0 * (b1_sq + b2_sq - c1_sq - c2_sq) * (a2 - b2);
        double delta2 = 2.0 * (b1_sq + b2_sq - c1_sq - c2_sq) * (a1 - b1) - 2.0 * (a1_sq + a2_sq - b1_sq - b2_sq) * (b1 - c1);

        double x1 = delta1 / delta;
        double x2 = delta2 / delta;

        double r = Math.sqrt((a1 - x1) * (a1 - x1) + (a2 - x2) * (a2 - x2));

        double u1 = point.getX();
        double u2 = point.getY();

        double ru = Math.sqrt((x1 - u1) * (x1 - u1) + (x2 - u2) * (x2 - u2));

        if (ru < r) {
            return true;
        }

        return false;
    }
    
    public void map2Grid(UniformGrid grid) {
        if(p1==null || p2==null || p3==null){
            return;
        }
        
        /* Testen ob Objekt vollständig innerhalb des Gitters liegt */
        if (!grid.isPointInside(minX, minY)) {
            return;
        }
        if (!grid.isPointInside(maxX, maxY)) {
            return;
        }

        int x1 = grid.transCoord2XIndex(p1.getX(), Rounding.FLOOR);
        int y1 = grid.transCoord2YIndex(p1.getY(), Rounding.FLOOR);
        int x2 = grid.transCoord2XIndex(p2.getX(), Rounding.FLOOR);
        int y2 = grid.transCoord2YIndex(p2.getY(), Rounding.FLOOR);
        int x3 = grid.transCoord2XIndex(p3.getX(), Rounding.FLOOR);
        int y3 = grid.transCoord2YIndex(p3.getY(), Rounding.FLOOR);

        Bresenham.start(x1, y1, x2, y2, grid);
        Bresenham.start(x2, y2, x3, y3, grid);
        Bresenham.start(x3, y3, x1, y1, grid);

        int x = grid.transCoord2XIndex(center.getX(), Rounding.FLOOR);
        int y = grid.transCoord2YIndex(center.getY(), Rounding.FLOOR);

        FloodFill.fill(x,y, grid);
    }

    // -------------------------------------------------------------------- //
    // Lokale Klasse Edge
    // -------------------------------------------------------------------- //
    class Edge {

        Point2D p1, p2;

        Edge(Point2D p1, Point2D p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    /**************************************
    --------          ---------
    |     /|          |\      |
    | t1 / |          | \  t2 |
    |   /  |    ->    |  \    |
    |  / t2|          |   \   |
    | /    |          | t1 \  |
    --------          ---------
     **************************************/
    void flipEdge(Triangle2D t) {

        Edge e = getSharedEdge(t);
        Point2D p41 = getOppositePoint(e.p1, e.p2);
        Point2D p42 = t.getOppositePoint(e.p1, e.p2);

        this.p1 = p41;
        this.p2 = e.p1;
        this.p3 = p42;

        t.p1 = p41;
        t.p2 = e.p2;
        t.p3 = p42;
    }

    Edge getSharedEdge(Triangle2D t) {

        int tester = 0;
        if (this.p1.equal(t.p1) || this.p1.equal(t.p2) || this.p1.equal(t.p3)) {
            tester += 1;
        }
        if (this.p2.equal(t.p1) || this.p2.equal(t.p2) || this.p2.equal(t.p3)) {
            tester += 3;
        }
        if (this.p3.equal(t.p1) || this.p3.equal(t.p2) || this.p3.equal(t.p3)) {
            tester += 5;
        }


        if (tester == 4) {
            return new Edge(p1, p2);
        }
        if (tester == 6) {
            return new Edge(p1, p3);
        }
        if (tester == 8) {
            return new Edge(p2, p3);
        }
        System.out.println("! ERROR cannot find shared edge !");
        return null;
    }

    Point2D getOppositePoint(Point2D pp1, Point2D pp2) {
        int tester = 0;
        if (this.p1.equal(pp1) || this.p1.equal(pp2)) {
            tester += 1;
        }
        if (this.p2.equal(pp1) || this.p2.equal(pp2)) {
            tester += 3;
        }
        if (this.p3.equal(pp1) || this.p3.equal(pp2)) {
            tester += 5;
        }

        if (tester == 4) {
            return p3;
        }
        if (tester == 6) {
            return p2;
        }
        if (tester == 8) {
            return p1;
        }

        System.out.println("! ERROR cannot find opposite point !");
        return null;
    }



}
