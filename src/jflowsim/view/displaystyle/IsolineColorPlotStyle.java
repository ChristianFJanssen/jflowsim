package jflowsim.view.displaystyle;

import jflowsim.view.utilities.ColorValue;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Line2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.Scalar;
import jflowsim.view.graphics.GraphicLine;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.ArrayList;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class IsolineColorPlotStyle extends DisplayStyle {

    public String[] getScalars() {
        Field[] fields = Scalar.class.getDeclaredFields();
        String[] str = new String[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            str[i] = fields[i].getName();
        }

        return str;
    }

    public void paint(Graphics g, WorldViewTransformator2D trafo, UniformGrid grid, HeadUpDisplay hud) {

        if (enabled) {

            this.tmp_min = min;
            this.tmp_max = max;

            this.min = Double.MAX_VALUE;
            this.max = -Double.MAX_VALUE;


            if (tmp_min >= Double.MAX_VALUE || tmp_max <= -Double.MAX_VALUE) {
                for (int x = 0; x < grid.nx; x++)
                {
                    for (int y = 0; y < grid.ny; y++) {
                        double scalar = grid.getScalar(x, y, scalar_type);
                        if (scalar < this.tmp_min) {
                            this.tmp_min = scalar;
                        }
                        if (scalar > this.tmp_max) {
                            this.tmp_max = scalar;
                        }
                    }
                }
            }

            double schrittweite = Math.abs((tmp_max - tmp_min) / 20.0);
            if (schrittweite < 1) {
                if (schrittweite < 0.1) {
                    schrittweite = Math.round(schrittweite * 1000) / 1000.0;
                } else {
                    schrittweite = Math.round(schrittweite * 10) / 10.0;
                }
            }
            if (schrittweite <= 0.0) {
                for (int x = 0; x < grid.nx; x++) {
                    for (int y = 0; y < grid.ny; y++) {
                        double scalar = grid.getScalar(x, y, scalar_type);

                        if (scalar < min) {
                            min = scalar;
                        }
                        if (scalar > max) {
                            max = scalar;
                        }
                    }
                }
                return;
            }

            ArrayList<Double> isoV = new ArrayList<Double>();
            for (int i = 0; i < (int) ((tmp_max - tmp_min) / schrittweite); i++) {
                isoV.add(min + (i * schrittweite));
//              minmax[0] könnte man auch runden -> gerundeter Startwert!
            }
            double h0 = tmp_min;
            double deltaH = schrittweite;

//        double delta = grid.delta;

            //Lauf durch diskretes Gitter
            for (int x = 0; x < grid.nx - 1; x++) {
                for (int y = 0; y < grid.ny - 1; y++) {

                    int xtmp = x;
                    int ytmp = y;



                    double ax = grid.transXIndex2Coord(xtmp);
                    double ay = grid.transYIndex2Coord(ytmp);

                    double bx = grid.transXIndex2Coord(xtmp + 1);
                    double by = grid.transYIndex2Coord(ytmp);

                    double cx = grid.transXIndex2Coord(xtmp + 1);
                    double cy = grid.transYIndex2Coord(ytmp + 1);

                    double dx = grid.transXIndex2Coord(xtmp);
                    double dy = grid.transYIndex2Coord(ytmp + 1);


                    double scalar = grid.getScalar(x, y, scalar_type);

                    if (scalar < min) {
                        min = scalar;
                    }
                    if (scalar > max) {
                        max = scalar;
                    }

                    double a = grid.getScalar(xtmp, ytmp, scalar_type);
                    double b = grid.getScalar(xtmp + 1, ytmp, scalar_type);
                    double c = grid.getScalar(xtmp + 1, ytmp + 1, scalar_type);
                    double d = grid.getScalar(xtmp, ytmp + 1, scalar_type);

                    //erstes Dreieck in dieser Zelle ACD
                    double wmin = Math.min(Math.min(a, c), d);
                    double wmax = Math.max(Math.max(a, c), d);

                    //Schritt 1
                    if (wmin != wmax) {
                        int nmin = 0, nmax = 0;
                        //Schritt 2
                        if (wmin > h0) {
                            nmin = (int) ((wmin - h0) / deltaH + 1);
                        }
                        if (wmin <= h0) {
                            nmin = (int) ((-wmin + h0) / deltaH);
                        }
                        if (wmax >= h0) {
                            nmax = (int) Math.ceil((wmax - h0) / deltaH);
                        }
                        if (wmax < h0) {
                            nmax = (int) (Math.ceil((wmax - h0) / deltaH - 1));
                        }
                        //---
                        if (nmin < nmax) {
                            //Schritt 3
                            for (int n = nmin; n < nmax; n++) {
                                //Schritt 4
                                double hn = h0 + n * deltaH;//iso

                                //Schritt 5
                                double tc = (hn - a) / (d - a);
                                double ta = (hn - d) / (c - d);
                                double td = (hn - c) / (a - c);

                                //Schritt 6
                                Point2D p1 = new Point2D(0, 0);
                                Point2D p2 = new Point2D(0, 0);
                                boolean test = false;
                                double c1 = 0, c2 = 0;
                                if (tc > 0 && tc < 1) {
                                    c1 = ax + tc * (dx - ax);
                                    c2 = ay + tc * (dy - ay);
                                    p1.setX(c1);
                                    p1.setY(c2);
                                    test = true;
                                }
                                double a1 = 0, a2 = 0;
                                if (ta > 0 && ta < 1) {
                                    a1 = dx + ta * (cx - dx);
                                    a2 = dy + ta * (cy - dy);
                                    if (test == true) {
                                        p2.setX(a1);
                                        p2.setY(a2);
                                    } else {
                                        p1.setX(a1);
                                        p1.setY(a2);
                                    }
                                }
                                double d1 = 0, d2 = 0;
                                if (td > 0 && td < 1) {
                                    d1 = cx + td * (ax - cx);
                                    d2 = cy + td * (ay - cy);
                                    p2.setX(d1);
                                    p2.setY(d2);
                                }

                                Line2D l = new Line2D(p1, p2);
                                GraphicLine gl = new GraphicLine(l, getLineColor(hn));
                                gl.paint(g, trafo);
                            }
                        }
                    }
                    //zweites Dreieck in dieser Zelle ABC
                    wmin = Math.min(Math.min(a, b), c);
                    wmax = Math.max(Math.max(a, b), c);

                    //Schritt 1
                    if (wmin != wmax) {
                        int nmin = 0, nmax = 0;
                        //Schritt 2
                        if (wmin > h0) {
                            nmin = (int) ((wmin - h0) / deltaH + 1);
                        }
                        if (wmin <= h0) {
                            nmin = (int) ((-wmin + h0) / deltaH);
                        }
                        if (wmax >= h0) {
                            nmax = (int) Math.ceil((wmax - h0) / deltaH);
                        }
                        if (wmax < h0) {
                            nmax = (int) (Math.ceil((wmax - h0) / deltaH - 1));
                        }
                        //---
                        if (nmin < nmax) {
                            //Schritt 3
                            for (int n = nmin; n < nmax; n++) {
                                //Schritt 4
                                double hn = h0 + n * deltaH;//iso

                                //Schritt 5
                                double tc = (hn - a) / (b - a);
                                double ta = (hn - b) / (c - b);
                                double tb = (hn - c) / (a - c);

                                //Schritt 6
                                Point2D p1 = new Point2D(0, 0);
                                Point2D p2 = new Point2D(0, 0);
                                boolean test = false;
                                double c1 = 0, c2 = 0;
                                if (tc > 0 && tc < 1) {
                                    c1 = ax + tc * (bx - ax);
                                    c2 = ay + tc * (by - ay);
                                    p1.setX(c1);
                                    p1.setY(c2);
                                    test = true;
                                }
                                double a1 = 0, a2 = 0;
                                if (ta > 0 && ta < 1) {
                                    a1 = bx + ta * (cx - bx);
                                    a2 = by + ta * (cy - by);
                                    if (test == true) {
                                        p2.setX(a1);
                                        p2.setY(a2);
                                    } else {
                                        p1.setX(a1);
                                        p1.setY(a2);
                                    }
                                }
                                double b1 = 0, b2 = 0;
                                if (tb > 0 && tb < 1) {
                                    b1 = cx + tb * (ax - cx);
                                    b2 = cy + tb * (ay - cy);
                                    p2.setX(b1);
                                    p2.setY(b2);
                                }

                                Line2D l = new Line2D(p1, p2);
                                GraphicLine gl = new GraphicLine(l, getLineColor(hn));
                                gl.paint(g, trafo);
                            }
                        }
                    }
                }
            }
        }
    }

    protected Color getLineColor(double scalar) {
        return ColorValue.getColor4Value(tmp_min, tmp_max, scalar);
    }
}
