package jflowsim.model.geometry2d;

import jflowsim.model.algebra.jama.Matrix;
import jflowsim.model.geometry2d.utilities.Bresenham;
import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.UniformGrid;
import java.util.LinkedList;

public class Spline2D extends Geometry2D {

    private LinkedList<Point2D> pointList;
    private Point2D center;
    private Point2D selectedPoint = null;
    private double minX, minY, maxX, maxY;
    private Matrix x;
    private int numOfPoints = 100;

    public Spline2D() {
        this.center = new Point2D(0.0, 0.0);
        this.pointList = new LinkedList<Point2D>();
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        Spline2D spline = new Spline2D();
        spline.pointList = pointList;
        return spline;
    }

    public Point2D getPointOnSpline(double t) {

        //stelle in bezug zur gesamtlaenge n
        double offset = t * (pointList.size() - 1);

        /* in welchem spline sind wir gerade? */
        int num = (int) (Math.floor(offset));


        //wie ist der versatz auf den aktuellen spline bezogen?
        double t_new = offset - num;

        //abfangen falls er bei num = n+1 landet...
        //allerletzter punkt gehört spline n...
        if (num == pointList.size() - 1) {
            num--;
            t_new = 1.0;
        }

        double px = 0.0;
        double py = 0.0;

        for (int i = 0; i < 4; i++) {
            px += (x.get(num * 8 + 2 * i, 0) * Math.pow(t_new, i));
            py += (x.get(num * 8 + 2 * i + 1, 0) * Math.pow(t_new, i));
        }

        return new Point2D(px, py);
    }

    private void calculateCoefficients() {

        if (pointList.size() > 2) {
            int numOfSplines = pointList.size() - 1;
            int numOfCoeff = numOfSplines * 8;

            Matrix A = new Matrix(numOfCoeff, numOfCoeff);
            Matrix b = new Matrix(numOfCoeff, 1); // rechte Seite

            /* schleife fuer jeden spline */
            for (int i = 0; i < numOfSplines; i++) {
                int offset = 8 * i;

                /* erste gleichung - anfangspunkte */
                A.set(offset, offset, 1.0);
                A.set(offset + 1, offset + 1, 1.0);
                b.set(offset, 0, pointList.get(i).getX());
                b.set(offset + 1, 0, pointList.get(i).getY());

                /* zweite gleichung - endpunkte */
                for (int j = 0; j < 4; j++) {
                    A.set(offset + 2, offset + 2 * j, 1.0);
                    A.set(offset + 3, offset + 1 + 2 * j, 1.0);
                }
                b.set(offset + 2, 0, pointList.get(i + 1).getX());
                b.set(offset + 3, 0, pointList.get(i + 1).getY());

                /* dritte gleichung - stetigkeit 1. abl */
                if (i < numOfSplines - 1) {

                    for (int j = 1; j < 4; j++) {
                        A.set(offset + 4, offset + 2 * j, (double) j);
                        A.set(offset + 5, offset + 1 + 2 * j, (double) j);
                    }

                    b.set(offset + 4, 0, 0.0);
                    b.set(offset + 5, 0, 0.0);

                    A.set(offset + 4, offset + 10, -1.0);
                    A.set(offset + 5, offset + 11, -1.0);
                }

                if (i == numOfSplines - 1) {
                    A.set(offset + 4, 4, 2.0);
                    A.set(offset + 5, 5, 2.0);

                    b.set(offset + 4, 0, 0.0);
                    b.set(offset + 5, 0, 0.0);
                }


                /* vierte gleichung - stetigkeit 2. abl */
                A.set(offset + 6, offset + 4, 1.0);
                A.set(offset + 6, offset + 6, 3.0);
                A.set(offset + 7, offset + 5, 1.0);
                A.set(offset + 7, offset + 7, 3.0);

                b.set(offset + 6, 0, 0.0);
                b.set(offset + 7, 0, 0.0);

                if (i < numOfSplines - 1) {
                    A.set(offset + 6, offset + 12, -1.0);
                    A.set(offset + 7, offset + 13, -1.0);
                }
            }
            x = A.solve(b);
        }
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
        // Spline verschieben
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
        calculateCoefficients();

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

        // Prüft ob Mausklick (x,y) auf einer Kante des Polygonzugs liegt
        double distance;        

        // Kurve zeichnen
        if (pointList.size() >= 2) {
            for (int i = 0; i < numOfPoints - 1; i++) {

                double t1 = (i) / (double) (numOfPoints - 1);
                double t2 = (i + 1.0) / (double) (numOfPoints - 1);

                Point2D p1 = getPointOnSpline(t1);
                Point2D p2 = getPointOnSpline(t2);

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

        if (pointList.size() > 2) {

            for (int i = 0; i < numOfPoints - 1; i++) {

                double t1 = (i) / (double) (numOfPoints - 1);
                double t2 = (i + 1.0) / (double) (numOfPoints - 1);

                Point2D p1 = getPointOnSpline(t1);
                Point2D p2 = getPointOnSpline(t2);

                int x1 = grid.transCoord2XIndex(p1.getX(), Rounding.FLOOR);
                int y1 = grid.transCoord2YIndex(p1.getY(), Rounding.FLOOR);
                int x2 = grid.transCoord2XIndex(p2.getX(), Rounding.FLOOR);
                int y2 = grid.transCoord2YIndex(p2.getY(), Rounding.FLOOR);

                Bresenham.start(x1, y1, x2, y2, grid);
            }
        }
    }
}
