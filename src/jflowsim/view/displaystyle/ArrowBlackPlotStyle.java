package jflowsim.view.displaystyle;

import java.awt.Color;

public class ArrowBlackPlotStyle extends ArrowColorPlotStyle {
    
    protected Color getLineColor(double scalar) {
        return Color.BLACK;
    }
}
