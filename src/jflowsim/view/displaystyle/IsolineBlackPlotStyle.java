package jflowsim.view.displaystyle;

import java.awt.Color;

public class IsolineBlackPlotStyle extends IsolineColorPlotStyle {

    protected Color getLineColor(double scalar) {
        return Color.BLACK;
    }
}
