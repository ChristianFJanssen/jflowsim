package jflowsim.model.geometry2d;

import java.io.Serializable;

public class Point2D implements Comparable<Point2D>,Serializable {

    // Koordinaten
    private double x;
    private double y;
    private boolean permanent;

    // Konstruktor
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(double x, double y, boolean permanent) {
        this.x = x;
        this.y = y;
        this.permanent = permanent;
    }

    public Point2D(Point2D p){
        this.x=p.getX();
        this.y=p.getY();
    }

    public double getDistance(Point2D p) {
        double dm_x = x - p.x;
        double dm_y = y - p.y;
        return Math.sqrt(dm_x * dm_x + dm_y * dm_y);

    }

    public boolean equal(Point2D p) {
        return this.x == p.x && this.y == p.y;
    }

    public Point2D clone() {
        return new Point2D(x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String toString() {
        return ("x: " + x + " y: " + y);
    }

    public boolean isPermanent() {
        return permanent;
    }

    public boolean compareCoords(Point2D p) {
        if ((getX() == p.getX()) && (getY() == p.getY())) {
            return true;
        } else {
            return false;
        }
    }

    public int compareTo(Point2D p) {
        /*compare -> Punktklasse
        collections.sort(arraylist);
        implements Comparable<>co

        this.x < p.x -> return -1

        this.scanner = new Scanner(new InputStreamReader(new FileInputStream(file)));
         *
         * scanner.hasNext();
         * String str=scanner.next();
         * value=Double.parseDouble(str);
         */
        if (this.x < p.x) {
            return -1;
        } else if (this.x > p.x) {
            return 1;
        } else {
            return 0;
        }

    }

    public double getAngle(Point2D p2) {
        if (compareCoords(p2) == false) {
            double dx = (p2.getX() - getX());
            double dy = (p2.getY() - getY());

            double a1 = Math.atan(dx / dy);
            if (((dx >= 0) && (dy < 0)) || ((dx < 0) && (dy < 0))) {
                a1 = a1 + Math.PI;
            } else if ((dx < 0) && (dy >= 0)) {
                a1 = a1 + 2 * Math.PI;
            }

            return a1;
        } else {
            return 0;
        }

    }
}
