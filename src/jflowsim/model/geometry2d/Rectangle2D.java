package jflowsim.model.geometry2d;

import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;

public class Rectangle2D extends Geometry2D {

    private Point2D p1, p2;
    private Point2D selectedPoint;
    private double minX, minY, maxX, maxY;
    private Point2D center;

    public Rectangle2D() {
        this.p1 = new Point2D(0.0, 0.0);
        this.p2 = new Point2D(0.0, 0.0);
        this.center = new Point2D(0.0, 0.0);
        super.setChanged();
        super.notifyObservers();
    }

    public Rectangle2D(Point2D p1, Point2D p2) {
        this.p1 = p1;
        this.p2 = p2;
        this.center = new Point2D(0.0, 0.0);
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        return new Rectangle2D(p1.clone(), p2.clone());
    }

    public Point2D getPoint1() {
        return p1;
    }

    public Point2D getPoint2() {
        return p2;
    }

    public void setPoint1(Point2D p1) {
        this.p1 = p1;
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
        if (x > getMinX() && x < getMinX() && y > getMinY() && y < getMaxY()) {
            return true;
        }
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

        //eckpunkte

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
            System.out.println("punkt1");
            super.setChanged();
            super.notifyObservers();
            return true;
        }
        if ((x >= xminP2 && x <= xmaxP2 && y <= ymaxP2 && y >= yminP2)) {
            selectedPoint = new Point2D(0.0, 0.0);
            selectedPoint = p2;//referenz!!!
            System.out.println("punkt2");
            super.setChanged();
            super.notifyObservers();
            return true;
        }

        //rechteck
        if (x > getMinX() - r && x < getMinX() + r && y > getMinY() && y < getMaxY()) {
            return true;
        } else if (x > getMaxX() - r && x < getMaxX() + r && y > getMinY() && y < getMaxY()) {
            return true;
        } else if (y > getMaxY() - r && y < getMaxY() + r && x > getMinX() && x < getMaxX()) {
            return true;
        } else if (y > getMinY() - r && y < getMinY() + r && x > getMinX() && x < getMaxX()) {
            return true;
        }

        return false;
    }

    protected void calculateValues() {
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

        int x1 = grid.transCoord2XIndex(minX, Rounding.FLOOR);
        int y1 = grid.transCoord2YIndex(minY, Rounding.FLOOR);
        int x2 = grid.transCoord2XIndex(maxX, Rounding.FLOOR);
        int y2 = grid.transCoord2YIndex(maxY, Rounding.FLOOR);

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (grid.getType(x, y) == GridNodeType.FLUID) {
                    grid.setType(x, y, GridNodeType.SOLID);
                }
            }
        }
    }
}
