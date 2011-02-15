package jflowsim.model.geometry2d;

import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class ConvexHull2D extends Geometry2D {

    private Point2D selectedPoint;
    private LinkedList<Point2D> pointList;
    private Scanner scanner;
    private Polyline2D polyTemp = new Polyline2D();
    private Polyline2D convexHull = new Polyline2D();
    private double minX, minY, maxX, maxY;
    private Point2D center;

    public ConvexHull2D() {
        this.center = new Point2D(0.0, 0.0);
        pointList = new LinkedList<Point2D>();
        super.setChanged();
        super.notifyObservers();
    }

    public ConvexHull2D(Point2D selectedPoint, LinkedList<Point2D> pointList) {
        this.selectedPoint = selectedPoint;
        this.pointList = pointList;
        super.setChanged();
        super.notifyObservers();
    }

    public void setPoint(Point2D p) throws FileNotFoundException {
        pointList.add(p);
        polyTemp.getPointList().clear();									//PointList in Polygon �bertragen
        for (int i = 0; i < pointList.size(); i++) {
            polyTemp.getPointList().add(pointList.get(i));//referenz
        }
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public void removePoint(Point2D p) {
        polyTemp.removePoint(p);
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public void readFile(String file) throws FileNotFoundException {
        //einlesen
        scanner = new Scanner(new InputStreamReader(new FileInputStream(file)));
        double x, y;
        boolean exists = false;
        int existing = 0;
        while (scanner.hasNextLine()) //Schleife �ber alle Zeilen des Eingabefiles
        {
            exists = false;
            scanner.next();										//
            x = Double.parseDouble(scanner.next());	//Aufrufen des n�chsten Zeichenblocks
            y = Double.parseDouble(scanner.next());	//
            for (Point2D p : pointList) //�berpr�fung, ob Punkt bereits in Liste enthalten
            {
                if (p.compareCoords(new Point2D(x, y))) {
                    exists = true;
                    existing++;
                }
            }

            if (!exists) //Hinzuf�gen des Punktes
            {
                pointList.add(new Point2D(x, y));
            }
            scanner.nextLine();
        }
//        System.out.println(existing + " Punkte mehrfach in der Liste vorhanden. " + pointList.size() + " Punkte aufgenommen.");
        polyTemp.getPointList().clear();									//PointList in Polygon �bertragen
        for (Point2D p1 : pointList) {
            polyTemp.setPoint(p1);
        }
        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    public Polyline2D computeCH(Polyline2D pL) {
        Polyline2D pLleft = new Polyline2D();
        Polyline2D pLright = new Polyline2D();

        for (int i = 0; i < (int) (0.5 * pL.getPointList().size()); i++) {														//Liste aufteilen in linke und rechte H�lfte
            pLleft.setPoint(pL.getPointList().get(i));
        }
        for (int i = (int) (0.5 * pL.getPointList().size()); i < pL.getPointList().size(); i++) {
            pLright.setPoint(pL.getPointList().get(i));
        }

        if (pLleft.getPointList().size() > 3) //rekursiver Aufruf, falls Listen >3
        {
            pLleft = computeCH(pLleft);
        }
        if (pLright.getPointList().size() > 3) {
            pLright = computeCH(pLright);
        }

        return merge(pLleft, pLright);
    }

    public Polyline2D merge(Polyline2D pLleft, Polyline2D pLright) {
        getStartTri(pLleft);									//sortieren der Punkte im Uhrzeigersinn f�r 3 Punkte
        getStartTri(pLright);


        int leftupindex = pLleft.getIndexMaxX();				//Index des rechten Punktes des linken Polygons
        int leftdownindex = pLleft.getIndexMaxX();
        int rightupindex = pLright.getIndexMinX();				//Index des linken Punktes des rechten Polygons
        int rightdownindex = pLright.getIndexMinX();

        Point2D pleftup = new Point2D(pLleft.getPointList().get(leftupindex));	//rechter Punkt des linken Polygons
        Point2D pleftdown = new Point2D(pLleft.getPointList().get(leftdownindex));
        Point2D prightup = new Point2D(pLright.getPointList().get(rightupindex));	//linker Punkt des rechten Polygons
        Point2D prightdown = new Point2D(pLright.getPointList().get(rightdownindex));
        boolean leftup = true;
        boolean leftdown = true;
        boolean rightup = true;
        boolean rightdown = true;

        Polyline2D pLmerge = new Polyline2D();

        while ((leftup == true) || (leftdown == true) || (rightup == true) || (rightdown == true)) {														//Schleife solange durchlaufen, bis sich kein Punkt
            leftup = false;										//mehr verschiebt
            leftdown = false;
            rightup = false;
            rightdown = false;

            for (int i = 0; i < pLleft.getPointList().size(); i++) //�berpr�fung, ob Punkt aus linkem Polygon oberhalb
            {													//der Verbindung liegt
                if (getPointOverLine(pleftup, prightup, pLleft.getPointList().get(i)) == 1) {
                    pleftup = pLleft.getPointList().get(i);
                    leftup = true;
                    leftupindex = i;
                }
                if (getPointOverLine(pleftdown, prightdown, pLleft.getPointList().get(i)) == -1) {
                    pleftdown = pLleft.getPointList().get(i);
                    leftdown = true;
                    leftdownindex = i;
                }
            }

            for (int i = 0; i < pLright.getPointList().size(); i++) //�berpr�fung, ob Punkt aus rechtem Polygon oberhalb
            {													//der Verbindung liegt
                if (getPointOverLine(pleftup, prightup, pLright.getPointList().get(i)) == 1) {
                    prightup = pLright.getPointList().get(i);
                    rightup = true;
                    rightupindex = i;
                }
                if (getPointOverLine(pleftdown, prightdown, pLright.getPointList().get(i)) == -1) {
                    prightdown = pLright.getPointList().get(i);
                    rightdown = true;
                    rightdownindex = i;
                }
            }
        }

        if (leftupindex >= leftdownindex) //Einf�gen des linken Polygons
        {
            for (int i = leftdownindex; i <= leftupindex; i++) {
                pLmerge.setPoint(pLleft.getPointList().get(i));
            }
        } else if (leftupindex < leftdownindex) {
            for (int i = leftdownindex; i < pLleft.getPointList().size(); i++) {
                pLmerge.setPoint(pLleft.getPointList().get(i));
            }
            for (int i = 0; i <= leftupindex; i++) {
                pLmerge.setPoint(pLleft.getPointList().get(i));
            }
        }


        if (rightdownindex >= rightupindex) //Einf�gen des rechten Polygons
        {
            for (int i = rightupindex; i <= rightdownindex; i++) {
                pLmerge.setPoint(pLright.getPointList().get(i));
            }
        } else if (rightdownindex < rightupindex) {
            for (int i = rightupindex; i < pLright.getPointList().size(); i++) {
                pLmerge.setPoint(pLright.getPointList().get(i));
            }
            for (int i = 0; i <= rightdownindex; i++) {
                pLmerge.setPoint(pLright.getPointList().get(i));
            }
        }

        pLmerge.getPointList().add(pLmerge.getPointList().getFirst());		//Schlie�en des Polygons
        return pLmerge;

    }

    public int getPointOverLine(Point2D p1, Point2D p2, Point2D p3) {
        double a1 = p1.getAngle(p2);							//Winkel zwischen Nullrichtung und p1p2
        double a2 = p1.getAngle(p3);							//Winkel zwischen Nullrichtung und p1p3

        double diff = a1 - a2;
        if (diff <= 0) //Einstellen des Winkels 0 < 2*PI
        {
            diff += 2 * Math.PI;
        }

        if ((p1.compareCoords(p2)) || (p1.compareCoords(p3)) || (p2.compareCoords(p3))) {														//keine Aussage bei gleichen Punkten
            return 0;
        }

        if (((diff) >= 0) && ((diff) <= Math.PI)) {
            return 1;
        } else if (((diff) >= Math.PI) && ((diff) <= 2 * Math.PI)) {
            return -1;
        } else {
            return 0;
        }
    }

    public void getStartTri(Polyline2D pL) {
        if (pL.getPointList().size() == 3) {
            if ((pL.getPointList().get(0).getAngle(pL.getPointList().get(1))) > (pL.getPointList().get(0).getAngle(pL.getPointList().get(2)))) {
                pL.setPoint(pL.getPointList().get(1));
                pL.getPointList().remove(1);
            }
        }
    }

    public LinkedList<Point2D> getPointList() {
        return pointList;
    }

    public Geometry2D clone() {
        return new ConvexHull2D(selectedPoint.clone(), (LinkedList<Point2D>) pointList.clone());
    }

    public void moveObject(double x, double y) {
        if (selectedPoint != null) {
            this.selectedPoint.setX(x);
            this.selectedPoint.setY(y);
        } else {
            double dx = x - this.getCenterX();
            double dy = y - this.getCenterY();
            for (int i = 0; i < polyTemp.getPointList().size(); i++) {
                Point2D p1 = polyTemp.getPointList().get(i);
                p1.setX(p1.getX() + dx);
                p1.setY(p1.getY() + dy);
            }
        }

        calculateValues();
        super.setChanged();
        super.notifyObservers();
    }

    private void calculateValues() {//setzt neuen center und minX, minY, maxX, maxY
        //center
        minY = minX = Double.MAX_VALUE;
        maxX = maxY = -Double.MAX_VALUE;
        for (Point2D p : polyTemp.getPointList()) {
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

        //convexe Hülle
        Collections.sort(polyTemp.getPointList());	//Punkte des Polygons sortieren
        if (pointList.size() > 1) {                      //ConvexHull berechnen
            convexHull = computeCH(polyTemp);
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
            return this.center.getY();
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

        boolean boundary = false;
        for (int i = 0; i < polyTemp.getPointList().size(); i++) {
            if ((Math.abs(polyTemp.getPointList().get(i).getX() - x) < r) && (Math.abs(polyTemp.getPointList().get(i).getY() - y) < r)) {
                selectedPoint = polyTemp.getPointList().get(i);
                return true;
            }
        }

        if (selectedPoint == null) {

            Point2D p1;
            Point2D p2;
            for (int i = 0; i < convexHull.getPointList().size() - 1; i++) {
                p1 = convexHull.getPointList().get(i);
                p2 = convexHull.getPointList().get(i + 1);
                double a = Math.sqrt((p2.getX() - x) * (p2.getX() - x) + (p2.getY() - y) * (p2.getY() - y));
                double b = Math.sqrt((p1.getX() - x) * (p1.getX() - x) + (p1.getY() - y) * (p1.getY() - y));
                double c = Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));
                double dist2 = b * Math.sin(Math.acos((b * b + c * c - a * a) / (2 * b * c)));
                if ((Math.abs(dist2) <= r) && ((x >= this.getMinX(p1, p2) - r) && (x <= this.getMaxX(p1, p2) + r)) && ((y >= this.getMinY(p1, p2) - r) && (y <= this.getMaxY(p1, p2) + r))) {
                    boundary = true;
                }

            }
        }
        return boundary;
    }

    public double getMaxX(Point2D p1, Point2D p2) {
        return maxX;
    }

    public double getMinX(Point2D p1, Point2D p2) {
        return minX;
    }

    public double getMaxY(Point2D p1, Point2D p2) {
        return maxY;
    }

    public double getMinY(Point2D p1, Point2D p2) {
        return minY;
    }

    public void refresh() {
        super.setChanged();
        super.notifyObservers();
    }

    public Polyline2D getMerge() {
        return convexHull;
    }

    public Polyline2D getPoly() {
        return polyTemp;
    }

    public boolean isPointInside(double x, double y) {

        int pointsOfIntersection = 0;

        int i, ii;
        for (ii = 0, i = convexHull.getPointList().size() - 1; ii < convexHull.getPointList().size(); i = ii++) {

            double xi = convexHull.getPointList().get(i).getX() - x;
            double xii = convexHull.getPointList().get(ii).getX() - x;
            double yi = convexHull.getPointList().get(i).getY() - y;
            double yii = convexHull.getPointList().get(ii).getY() - y;



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
