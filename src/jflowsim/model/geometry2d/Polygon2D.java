package jflowsim.model.geometry2d;

import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;
import java.util.LinkedList;

public class Polygon2D extends Geometry2D {

    private LinkedList<Point2D> pointList;
    private Point2D selectedPoint;
    double minX, minY, maxX, maxY;
    private Point2D center;

    public Polygon2D() {
        this.center = new Point2D(0.0, 0.0);
        pointList = new LinkedList<Point2D>();
        super.setChanged();
        super.notifyObservers();
    }

    public Polygon2D(LinkedList<Point2D> pointList) {
        this.pointList = pointList;
        super.setChanged();
        super.notifyObservers();
    }

    public Geometry2D clone() {
        return new Polygon2D((LinkedList<Point2D>) pointList.clone());
    }

    public void moveObject(double x, double y) {

        //verschiebung von einem punkt
        if (selectedPoint != null) {
            selectedPoint.setX(x);
            selectedPoint.setY(y);

        } else {
            //normale verschiebung
            double dx = x - getCenterX();
            double dy = y - getCenterY();

            for (int i = 0; i < pointList.size(); i++) {
                pointList.get(i).setX(pointList.get(i).getX() + dx);
                pointList.get(i).setY(pointList.get(i).getY() + dy);
            }
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

    public boolean isPointInside(double x, double y) {

        int pointsOfIntersection = 0;

        int i, ii;
        for (ii = 0, i = pointList.size() - 1; ii < pointList.size(); i = ii++) {

            double xi = pointList.get(i).getX() - x;
            double xii = pointList.get(ii).getX() - x;
            double yi = pointList.get(i).getY() - y;
            double yii = pointList.get(ii).getY() - y;



            if (yi * yii <= 0) {

                double r = xi + ((yi * (xii - xi)) / (yi - yii));

                if (r >= 0) { // #4
                    if (yi * yii < 0) {
                        pointsOfIntersection++;
                    } else {
                        if (yi < yii) {// #1,3
                            if (yi == 0) {
                                pointsOfIntersection++;
                            }
                        } else {//#2
                            if (yii == 0 && yi != 0) {//#3
                                pointsOfIntersection++;
                            }
                        }
                    }
                }
            }
        }


        if (pointsOfIntersection % 2.0 == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isPointOnBoundary(double x, double y, double r) {
        selectedPoint = null;

        //kontrollpunkt
        for (int i = 0; i < pointList.size(); i++) {

            double xminP = pointList.get(i).getX() - r;
            double yminP = pointList.get(i).getY() - r;
            double xmaxP = pointList.get(i).getX() + r;
            double ymaxP = pointList.get(i).getY() + r;

            if ((x >= xminP && x <= xmaxP && y <= ymaxP && y >= yminP)) {
                selectedPoint = new Point2D(0.0, 0.0);
                selectedPoint = pointList.get(i);//referenz!!!

                super.setChanged();
                super.notifyObservers();
                return true;
            }
        }

        //boundary
        double distance;
        int i, j;
        for (i = 0, j = this.pointList.size() - 1; i < this.pointList.size(); j = i++) {
            double xa = this.pointList.get(i).getX();
            double ya = this.pointList.get(i).getY();
            double xb = this.pointList.get(j).getX();
            double yb = this.pointList.get(j).getY();

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
        return false;
    }

    public void setPoint(Point2D p) {//mmuss auch für die verschiebung eines kontrollpunktes sein!?

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
        calculateValues();
        pointList.removeLast();
        super.setChanged();
        super.notifyObservers();
    }

    public LinkedList<Point2D> GetPointList() {
        return this.pointList;
    }

    protected void calculateValues() {
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
    }

    public void map2Grid(UniformGrid grid) {

        if (pointList.size() > 2) {

            /* Testen ob Objekt vollständig innerhalb des Gitters liegt */
            if (!grid.isPointInside(minX, minY) || !grid.isPointInside(maxX, maxY)) {
                return;
            }

            int x1 = grid.transCoord2XIndex(minX, Rounding.FLOOR);
            int y1 = grid.transCoord2YIndex(minY, Rounding.FLOOR);
            int x2 = grid.transCoord2XIndex(maxX, Rounding.FLOOR);
            int y2 = grid.transCoord2YIndex(maxY, Rounding.FLOOR);

            for (int x = x1; x < x2; x++) {
                for (int y = y1; y < y2; y++) {

                    double xx1 = grid.transXIndex2Coord(x);
                    double yy1 = grid.transYIndex2Coord(y);
                    if (isPointInside(xx1, yy1)) {
                        if (grid.getType(x, y) == GridNodeType.FLUID) {
                            grid.setType(x, y, GridNodeType.SOLID);
                        }
                    }
                }
            }
        }
    }
}
