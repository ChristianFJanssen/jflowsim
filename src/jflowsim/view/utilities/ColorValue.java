package jflowsim.view.utilities;

import jflowsim.model.numerics.utilities.GridNodeType;
import java.awt.Color;

public class ColorValue {

    public static Color getColor4Value(double min, double max, double value) {

        if(value<min) value = min;
        if(value>max) value = max;

        double scale_val = (value - min) / (max - min);
        int r = 0;
        int g = 0;
        int b = 0;

        if (scale_val <= 0.25) {
            g = (int) (255. * (scale_val) / 0.25);
            b = 255;
            r = 0;
        } else if (scale_val <= 0.5) {
            b = 255 - (int) (255. * (scale_val - 0.25) / 0.25);
            g = 255;
            r = 0;
        } else if (scale_val <= 0.75) {
            r = (int) (255. * (scale_val - 0.5) / 0.25);
            g = 255;
            b = 0;
        } else if (scale_val <= 1.0) {
            g = 255 - (int) (255. * (scale_val - 0.75) / 0.25);
            r = 255;
            b = 0;
        } else {
            r = 0;
            g = 0;
            b = 0;
        }

        r = r < 0 ? 0 : r;
        g = g < 0 ? 0 : g;
        b = b < 0 ? 0 : b;

        r = r > 255 ? 255 : r;
        g = g > 255 ? 255 : g;
        b = b > 255 ? 255 : b;

        return new Color(r, g, b);
    }//

    public static Color getColor4Value(int grid_type) {
        if (grid_type == GridNodeType.BOUNDARY) {
            return Color.RED;
        } else if (grid_type == GridNodeType.SOLID) {
            return Color.PINK;
        } else if (grid_type == GridNodeType.INTERFACE) {
            return Color.ORANGE;
        } else if (grid_type == GridNodeType.GAS) {
            return Color.LIGHT_GRAY;
        } else if (grid_type == GridNodeType.SOLIDN) {
            return Color.GREEN;
        } else if (grid_type == GridNodeType.SOLIDS) {
            return Color.GREEN;
        } else if (grid_type == GridNodeType.SOLIDE) {
            return Color.GREEN;
        } else if (grid_type == GridNodeType.SOLIDW) {
            return Color.GREEN;
        } else if (grid_type == GridNodeType.FLUID) {
            return Color.BLUE;
        } else if (grid_type == GridNodeType.RUNUP) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
}
