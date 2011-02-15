package jflowsim.model.geometry2d.utilities;

import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;

public class Bresenham {
    /*--------------------------------------------------------------
     * Bresenham-Algorithmus: Linien auf Rastergeräten zeichnen
     * http://de.wikipedia.org/wiki/Bresenham-Algorithmus
     *
     * Eingabeparameter:
     *    int xstart, ystart        = Koordinaten des Startpunkts
     *    int xend, yend            = Koordinaten des Endpunkts
     *
     * Ausgabe:
     *    void SetPixel(int x, int y) setze ein Pixel in der Grafik
     *         (wird in dieser oder aehnlicher Form vorausgesetzt)
     *---------------------------------------------------------------
     */

    public static void start(int xstart, int ystart, int xend, int yend, UniformGrid grid) {

        int pdx, pdy, ddx, ddy, es, el, err;

        /* Entfernung in beiden Dimensionen berechnen */
        int dx = xend - xstart;
        int dy = yend - ystart;

        /* Vorzeichen des Inkrements bestimmen */
        int incx = (int) Math.signum((double) dx);
        int incy = (int) Math.signum((double) dy);
        if (dx < 0) {
            dx = -dx;
        }
        if (dy < 0) {
            dy = -dy;
        }

        /* feststellen, welche Entfernung größer ist */
        if (dx > dy) {
            /* x ist schnelle Richtung */
            pdx = incx;
            pdy = 0;    /* pd. ist Parallelschritt */
            ddx = incx;
            ddy = incy; /* dd. ist Diagonalschritt */
            es = dy;
            el = dx;   /* Fehlerschritte schnell, langsam */
        } else {
            /* y ist schnelle Richtung */
            pdx = 0;
            pdy = incy; /* pd. ist Parallelschritt */
            ddx = incx;
            ddy = incy; /* dd. ist Diagonalschritt */
            es = dx;
            el = dy;   /* Fehlerschritte schnell, langsam */
        }

        /* Initialisierungen vor Schleifenbeginn */
        int x = xstart;
        int y = ystart;
        err = el / 2;
        //SetPixel(x,y);
        if (grid.getType(x, y) == GridNodeType.FLUID) {
            grid.setType(x, y, GridNodeType.SOLID);
        }

        /* Pixel berechnen */
        for (int t = 0; t < el; ++t) /* t zaehlt die Pixel, el ist auch Anzahl */ {
            /* Aktualisierung Fehlerterm */
            err -= es;
            if (err < 0) {
                /* Fehlerterm wieder positiv (>=0) machen */
                err += el;
                /* Schritt in langsame Richtung, Diagonalschritt */
                x += ddx;
                y += ddy;
            } else {
                /* Schritt in schnelle Richtung, Parallelschritt */
                x += pdx;
                y += pdy;
            }
            if (grid.getType(x, y) == GridNodeType.FLUID) {
                grid.setType(x, y, GridNodeType.SOLID);
            }
        }
    }
}
