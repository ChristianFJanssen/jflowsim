package jflowsim.view.displaystyle;

import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.numerics.utilities.Scalar;
import java.lang.reflect.Field;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.view.headupdisplay.HeadUpDisplay;
import jflowsim.view.utilities.ColorValue;

public class IntersectionStyle extends DisplayStyle {

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

            double x1_view = trafo.transformWorldToViewXCoord(grid.getMinX(), grid.getMinY(), false);
            double y1_view = trafo.transformWorldToViewYCoord(grid.getMinX(), grid.getMinY(), false);
            double x2_view = trafo.transformWorldToViewXCoord(grid.getMaxX(), grid.getMaxY(), false);
            double y2_view = trafo.transformWorldToViewYCoord(grid.getMaxX(), grid.getMaxY(), false);

            double dx = (x2_view - x1_view) / grid.nx;
            double dy = (y1_view - y2_view) / grid.ny;

            y1_view -= dx;

            for (int x = 0; x < grid.nx; x++) {
                for (int y = 0; y < grid.ny; y++) {

                    Color c;

                    double scalar = grid.getScalar(x, y, scalar_type);



                    if (grid.getType(x, y) <= GridNodeType.SOLID) {
                        c = Color.BLACK;
                    } else {
                                            if (scalar < min) {
                        min = scalar;
                    }
                    if (scalar > max) {
                        max = scalar;
                    }
                        c = ColorValue.getColor4Value(tmp_min, tmp_max, scalar);
                    }
                    g.setColor(c);
                    g.fillRect((int) Math.floor(x1_view + x * dx), (int) Math.floor(y1_view - y * dy), (int) Math.ceil(dx), (int) Math.ceil(dy));
                }
            }

            double offset = 0.6 * grid.ny * dx; // 60% offset nach unten unters grid
            double height = 0.5 * grid.ny * dx; // height of the line plot


            double xprev = x1_view;
            double yprev = y1_view + offset;

            // position of the line probe
            int y = grid.ny / 2;

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect((int) Math.floor(x1_view)-10, (int) Math.floor(y1_view - y * dy),
                    (int) Math.ceil(grid.nx * dx)+20, (int) Math.floor(1*dy));

            double range = tmp_max - 0.0;

            //System.out.println("Min,max,range: " + tmp_min + " " + tmp_max + " " + range);



            for (int x = 0; x < grid.nx; x++) {

                Color c;

                double scalar = grid.getScalar(x, y, scalar_type);

                if (grid.getType(x, y) == GridNodeType.BOUNDARY || grid.getType(x, y) == GridNodeType.SOLID) {
                    c = Color.blue;
                    scalar = 0.0;
                } else {
                    c = ColorValue.getColor4Value(tmp_min, tmp_max, scalar);
                }


                scalar = scalar * height / range;

                g.setColor(c);
                g.fillRect(
                        (int) Math.floor(x1_view + x * dx),
                        (int) Math.floor(y1_view + offset),
                        (int) Math.ceil(dx),
                        (int) Math.ceil(-scalar));

                g.setColor(Color.BLACK);

                g.drawLine((int) xprev, (int) yprev, (int) Math.floor(x1_view + (x + 0.5) * dx), (int) Math.floor(y1_view + offset - scalar));

                xprev = (int) Math.floor(x1_view + (x + 0.5) * dx);
                yprev = (int) Math.floor(y1_view + offset - scalar);
            }
        }
    }
}
