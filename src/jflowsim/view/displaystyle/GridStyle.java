package jflowsim.view.displaystyle;

import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.Scalar;
import jflowsim.view.utilities.ColorValue;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class GridStyle extends DisplayStyle {

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

            double offset = 1;
            double scale = 0.4;

            for (int x = 0; x < grid.nx; x += offset) {
                for (int y = 0; y < grid.ny; y += offset) {

                    Color c = Color.BLACK;

                    if (scalar_type == Scalar.GRID_TYPE) {
                        c = ColorValue.getColor4Value(grid.getType(x, y));
                    } else {
                        double scalar = grid.getScalar(x, y, scalar_type);


                        if (grid.getType(x, y) == GridNodeType.SOLID) {
                            c = Color.BLUE;
                        } else {
                                                    if (scalar < min) {
                            min = scalar;
                        }
                        if (scalar > max) {
                            max = scalar;
                        }
                            c = ColorValue.getColor4Value(tmp_min, tmp_max, scalar);
                        }
                    }

                    g.setColor(c);

                    if (grid.getType(x, y) != GridNodeType.GAS) {
                        g.fillRect((int) Math.floor(x1_view + x * dx + scale * dx * 0.5), (int) Math.floor(y1_view - y * dy + scale * dy * 0.5), (int) Math.ceil(scale * dx), (int) Math.ceil(scale * dy));
                    }
                }
            }
        }
    }
}
