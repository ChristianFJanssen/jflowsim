package jflowsim.model.geometry2d;

import jflowsim.model.geometry2d.utilities.Bresenham;
import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.UniformGrid;

public class Line2D extends Geometry2D {

    private Point2D p1, p2;
    private Point2D center;
    private double minX, minY, maxX, maxY;
    private Point2D selectedPoint;

    public Line2D() {
        this.center = new Point2D(0.0, 0.0);
        this.p1 = new Point2D(0.0, 0.0);
        this.p2 = new Point2D(0.0, 0.0);
        super.setChanged();
        super.notifyObservers();
    }

    public Line2D(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.center = new Point2D(0.0, 0.0);
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        return new Line2D(p1.clone(), p2.clone());
    }

    public Point2D getPoint1() {
        return p1;
    }

    public Point2D getPoint2() {
        return p2;
    }

    public void setPoint1(Point2D p1) {
        this.p1 = p1;
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public void setPoint2(Point2D p2) {
        this.p2 = p2;
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    // testet ob Koordinaten (x,y) innerhalb des Rechtecks liegen
    public boolean isPointInside(double x, double y) {
        return false;
    }

    public void moveObject(double x, double y) {
        if (selectedPoint != null) {
            selectedPoint.setX(x);
            selectedPoint.setY(y);
        } else {
            double Ax = x - getCenterX();
            double Ay = y - getCenterY();

            this.p1.setX(p1.getX() + Ax);
            this.p1.setY(p1.getY() + Ay);

            this.p2.setX(p2.getX() + Ax);
            this.p2.setY(p2.getY() + Ay);
        }
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public double getCenterX() {
        if (selectedPoint == null) {
            return this.center.getX();
        } else {
            return selectedPoint.getX();
        }
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getCenterY() {
        if (selectedPoint == null) {
            return this.center.getY();
        } else {
            return selectedPoint.getY();
        }
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public boolean isPointOnBoundary(double x, double y, double r) {

        selectedPoint = null;

        //Punkte

        double xminP1 = p1.getX() - r;
        double yminP1 = p1.getY() - r;
        double xmaxP1 = p1.getX() + r;
        double ymaxP1 = p1.getY() + r;

        double xminP2 = p2.getX() - r;
        double yminP2 = p2.getY() - r;
        double xmaxP2 = p2.getX() + r;
        double ymaxP2 = p2.getY() + r;

        if ((x >= xminP1 && x <= xmaxP1 && y <= ymaxP1 && y >= yminP1)) {
            selectedPoint = new Point2D(0.0, 0.0);
            selectedPoint = p1;//referenz!!!

            super.setChanged();
            super.notifyObservers();
            return true;
        }
        if ((x >= xminP2 && x <= xmaxP2 && y <= ymaxP2 && y >= yminP2)) {
            selectedPoint = new Point2D(0.0, 0.0);
            selectedPoint = p2;//referenz!!!

            super.setChanged();
            super.notifyObservers();
            return true;
        }

        //linie
        double distance;
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

        return false;
    }

    private void calculateValues() {
        if (p1.getX() < p2.getX()) {
            minX = p1.getX();
            maxX = p2.getX();
        } else {
            minX = p2.getX();
            maxX = p1.getX();
        }

        if (p1.getY() < p2.getY()) {
            minY = p1.getY();
            maxY = p2.getY();
        } else {
            minY = p2.getY();
            maxY = p1.getY();
        }
        center.setY(minY + (maxY - minY) / 2);
        center.setX(minX + (maxX - minX) / 2);
    }

    public void map2Grid(UniformGrid grid) {

        /* Testen ob Objekt vollständig innerhalb des Gitters liegt */
        if(!grid.isPointInside(minX, minY) || !grid.isPointInside(maxX, maxY)){
            return;
        }

        int x1 = grid.transCoord2XIndex(p1.getX(), Rounding.FLOOR);
        int y1 = grid.transCoord2YIndex(p1.getY(), Rounding.FLOOR);
        int x2 = grid.transCoord2XIndex(p2.getX(), Rounding.FLOOR);
        int y2 = grid.transCoord2YIndex(p2.getY(), Rounding.FLOOR);

        Bresenham.start(x1, y1, x2, y2, grid);
    }
}
