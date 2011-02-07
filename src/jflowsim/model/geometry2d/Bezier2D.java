package jflowsim.model.geometry2d;

import jflowsim.model.geometry2d.utilities.Bresenham;
import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.UniformGrid;
import java.math.BigInteger;
import java.util.LinkedList;

public class Bezier2D extends Geometry2D {

    private LinkedList<Point2D> pointList;
    private Point2D center;
    private Point2D selectedPoint = null;
    private double minX, minY, maxX, maxY;
    private int numOfPoints = 100;

    public Bezier2D() {
        this.center = new Point2D(0.0, 0.0);
        this.pointList = new LinkedList<Point2D>();
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        Bezier2D bezier = new Bezier2D();
        bezier.pointList = pointList;
        return bezier;
    }

    public Point2D getPointOnCurve(double t) {

        double px = 0.0;
        double py = 0.0;
        int n = pointList.size();
        for (int i = 0; i < n; i++) {

            double c = binomialCoefficient(n - 1, i) * Math.pow(t, i) * Math.pow(1.0 - t, n - 1 - i);

            px += c * pointList.get(i).getX();
            py += c * pointList.get(i).getY();
        }

        return new Point2D(px, py);
    }

    private BigInteger factorial(BigInteger n) {
        BigInteger z = BigInteger.ONE;

        // solange n > 1
        while (n.compareTo(BigInteger.ZERO) > 0) {
            z = z.multiply(n); // z *= n
            n = n.subtract(BigInteger.valueOf(1)); // n--
        }

        return z;
    }

    private long factorial(int n) {
        return n == 0 ? 1 : n * factorial(n - 1);
    }

    //binomial coefficient (n over k)
    private double binomialCoefficient(int n, int k) {
        if (n >= k && n >= 0.0) {
            return factorial(n) / (factorial(k) * factorial(n - k));
        }
        System.out.println("ERROR: binomialCoefficient : n<k || n<0.0");
        return -1;
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

    public void removeLastPoint() {
        pointList.removeLast();
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    // testet ob Koordinaten (x,y) innerhalb der Bezierkurve liegt
    public boolean isPointInside(double x, double y) {
        return false;
    }

    public void moveObject(double x, double y) {
        // Punkt verschieben
        if (selectedPoint != null) {
            this.selectedPoint.setX(x);
            this.selectedPoint.setY(y);
        } // Polyline verschieben
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
        this.center.setX(minX + 0.5 * (maxX - minX));
        this.center.setY(minY + 0.5 * (maxY - minY));
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

        // Prüft ob Mausklick (x,y) auf einem Kontrollpunkt der Bezierkurve liegt
        for (Point2D p : pointList) {
            if ((Math.abs(x - p.getX()) < r) && (Math.abs(y - p.getY()) < r)) {
                this.selectedPoint = p;
                return true;
            }
        }
        this.selectedPoint = null;

        // Prüft ob Mausklick (x,y) auf einer Kante der B ezierkurve liegt
        double distance;
        

        // Kurve zeichnen
        if (pointList.size() >= 2) {
            for (int i = 0; i < numOfPoints - 1; i++) {

                double t1 = (i) / (double) (numOfPoints - 1);
                double t2 = (i + 1.0) / (double) (numOfPoints - 1);

                Point2D p1 = getPointOnCurve(t1);
                Point2D p2 = getPointOnCurve(t2);

                double xa = p1.getX();
                double ya = p1.getY();
                double xb = p2.getX();
                double yb = p2.getY();
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
                }
            }
        }

        return false;
    }

    public LinkedList<Point2D> getPointList() {
        return pointList;
    }
    
    public void map2Grid(UniformGrid grid) {
        /* Testen ob Objekt vollständig innerhalb des Gitters liegt */
        if(!grid.isPointInside(minX, minY) || !grid.isPointInside(maxX, maxY)){
            return;
        }

        if (pointList.size() > 1) {

            for (int i = 0; i < numOfPoints - 1; i++) {

                double t1 = (i) / (double) (numOfPoints - 1);
                double t2 = (i + 1.0) / (double) (numOfPoints - 1);

                Point2D p1 = getPointOnCurve(t1);
                Point2D p2 = getPointOnCurve(t2);

                int x1 = grid.transCoord2XIndex(p1.getX(), Rounding.FLOOR);
                int y1 = grid.transCoord2YIndex(p1.getY(), Rounding.FLOOR);
                int x2 = grid.transCoord2XIndex(p2.getX(), Rounding.FLOOR);
                int y2 = grid.transCoord2YIndex(p2.getY(), Rounding.FLOOR);

                Bresenham.start(x1, y1, x2, y2, grid);
            }
        }
    }
}
