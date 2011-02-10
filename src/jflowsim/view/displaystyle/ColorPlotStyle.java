package jflowsim.view.displaystyle;

import jflowsim.view.utilities.ColorValue;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.Scalar;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class ColorPlotStyle extends DisplayStyle {

    private DecimalFormat df = new DecimalFormat("0.000");

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

            hud.drawText("Min = " + df.format(min));
            hud.drawText("Max = " + df.format(max));

            this.tmp_min = min;
            this.tmp_max = max;

            this.min = Double.MAX_VALUE;
            this.max = -Double.MAX_VALUE;

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
                    g.setColor(c);
                    g.fillRect((int) Math.floor(x1_view + x * dx), (int) Math.floor(y1_view - y * dy), (int) Math.ceil(dx), (int) Math.ceil(dy));
                }
            }
        }
    }
}
