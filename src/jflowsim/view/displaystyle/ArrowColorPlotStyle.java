package jflowsim.view.displaystyle;

import jflowsim.view.utilities.ColorValue;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.Scalar;
import java.awt.Color;
import java.awt.Graphics;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class ArrowColorPlotStyle extends DisplayStyle {

    private double sliderscale = 5;

    public String[] getScalars() {
        return new String[0];
    }

    public void paint(Graphics g, WorldViewTransformator2D trafo, UniformGrid grid, HeadUpDisplay hud) {

        if (enabled) {

            this.tmp_min = min;
            this.tmp_max = max;

            this.min = Double.MAX_VALUE;
            this.max = -Double.MAX_VALUE;

            int offset = 5;

            if (tmp_min >= Double.MAX_VALUE || tmp_max <= -Double.MAX_VALUE) {
                for (int x = 0; x < grid.nx; x += offset)
                {
                    for (int y = 0; y < grid.ny; y += offset) {
                        double v = grid.getScalar(x, y, Scalar.V);
                        if (v < this.tmp_min) {
                            this.tmp_min = v;
                        }
                        if (v > this.tmp_max) {
                            this.tmp_max = v;
                        }
                    }
                }
            }
          

            for (int x = 0; x < grid.nx; x += offset)
            {
                for (int y = 0; y < grid.ny; y += offset) {
                    double cx = grid.transXIndex2Coord(x);
                    double cy = grid.transYIndex2Coord(y);

                    double view_cx = trafo.transformWorldToViewXCoord(cx, cy, false);
                    double view_cy = trafo.transformWorldToViewYCoord(cx, cy, false);

                    //if (grid.getType(x, y) <= GridNodeType.SOLID) {

                        double v = grid.getScalar(x, y, Scalar.V);
                        if (v < min) {
                            min = v;
                        }
                        if (v > max) {
                            max = v;
                        }

                        double scale = sliderscale * 2 / (tmp_max - tmp_min);
                        Color c = getLineColor(v);
                        g.setColor(c);

                        double vx = grid.getScalar(x, y, Scalar.V_X);
                        double vy = grid.getScalar(x, y, Scalar.V_Y);

                        int x1 = (int) Math.round((view_cx + scale * vx));
                        int y1 = (int) Math.round((view_cy - scale * vy));
                        int x2 = (int) Math.round((view_cx - scale * vx));
                        int y2 = (int) Math.round((view_cy + scale * vy));

                        int dy1 = (int) Math.round(0.8 * scale * v * Math.sin(.3 + getAngle(new Point2D(x2, y2), new Point2D(x1, y1))));
                        int dx1 = (int) Math.round(0.8 * scale * v * Math.cos(.3 + getAngle(new Point2D(x2, y2), new Point2D(x1, y1))));
                        int dy2 = (int) Math.round(0.8 * scale * v * Math.sin(-.3 + getAngle(new Point2D(x2, y2), new Point2D(x1, y1))));
                        int dx2 = (int) Math.round(0.8 * scale * v * Math.cos(-.3 + getAngle(new Point2D(x2, y2), new Point2D(x1, y1))));


                        g.drawLine(x1, y1, x2, y2);
                        g.drawLine(x1, y1, x1 - dx1, y1 - dy1);
                        g.drawLine(x1, y1, x1 - dx2, y1 - dy2);
                    }
                //}
            }
        }
    }

    protected Color getLineColor(double scalar) {
        return ColorValue.getColor4Value(tmp_min, tmp_max, scalar);
    }

    private double getAngle(Point2D p1, Point2D p2) {

        /*	---> x
         *  |	P1	.	.	.
         *  |		.		a1
         * 	v			.
         *  				P2
         *  y
         *
         *
         */

        if (comparePointCoords(p1, p2) == false) {
            double dx = (p2.getX() - p1.getX());
            double dy = (p2.getY() - p1.getY());

            double a1 = Math.atan(dy / dx);
            if ((dx < 0)) {
                a1 = a1 + Math.PI;
            } else if ((dx >= 0) && (dy < 0)) {
                a1 = a1 + 2 * Math.PI;
            }

            return a1;
        } else {
            return 0;
        }

    }

    private boolean comparePointCoords(Point2D p1, Point2D p2) {
        if ((p1.getX() == p2.getX()) && (p1.getY() == p2.getY())) {
            return true;
        } else {
            return false;
        }
    }
}
