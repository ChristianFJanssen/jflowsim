package jflowsim.view.graphics;

import java.awt.Color;
import java.awt.Graphics;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.view.displaystyle.DisplayStyleManager;
import jflowsim.view.headupdisplay.HeadUpDisplay;
import java.text.DecimalFormat;

public class GraphicGrid extends GraphicObject {

    private DisplayStyleManager displayStyleManager;
    private HeadUpDisplay hud;
    private UniformGrid grid;
    private DecimalFormat df = new DecimalFormat("0.0000");
    private DecimalFormat df2 = new DecimalFormat("0.0");
    private DecimalFormat dfExpo = new DecimalFormat("0.00E0");

    public GraphicGrid(UniformGrid grid, DisplayStyleManager displayStyleManager) {
        this.grid = grid;
        this.displayStyleManager = displayStyleManager;
        this.hud = new HeadUpDisplay();
    }

    // draw grid
    public void paint(Graphics g, WorldViewTransformator2D trafo) {

        
        double xView1 = trafo.transformWorldToViewXCoord(grid.getMinX(), grid.getMinY(), true);
        double yView1 = trafo.transformWorldToViewYCoord(grid.getMinX(), grid.getMinY(), true);
        double xView2 = trafo.transformWorldToViewXCoord(grid.getMaxX(), grid.getMaxY(), true);
        double yView2 = trafo.transformWorldToViewYCoord(grid.getMaxX(), grid.getMaxY(), true);

        g.setColor(Color.BLACK);
        g.drawRect((int) Math.min(xView1, xView2), (int) Math.min(yView1, yView2), (int) Math.abs(xView2 - xView1), (int) Math.abs(yView2 - yView1));

        hud.flushDisplay();
        hud.setGraphicDevice(g);

        displayStyleManager.paint(g, trafo, grid, hud);

        g.setColor(Color.BLACK);


        // ----------------------------------------------------------------------------- //
        // Head-up-Display
        // ----------------------------------------------------------------------------- //
        hud.drawText(grid.getClass().getSimpleName());
        hud.drawText(grid.testcase);
        hud.drawText("length: " + grid.getLength() + " [m]  width:" + grid.getWidth() + " [m]");
        hud.drawText("dof: " + grid.nx + "x" + grid.ny + " | " + grid.nx * grid.ny + " | delta:" + grid.dx);
        hud.drawText("real time: " + df.format(grid.real_time) + " [s]");
        hud.drawText("MNUPS: " + df2.format(grid.mnups));
        hud.drawText("Viscosity: " + dfExpo.format(grid.viscosity) + " [m^2/s]");
        hud.drawText("Gravity: " + grid.gravityX + " " + grid.gravityY + " [m/s^2]");
        hud.drawText("Time step: " + grid.dt + " [s]");

        grid.updateHeadUpDisplay(hud);
    }

    public boolean isPointInside(double viewX, double viewY, WorldViewTransformator2D trafo) {
        return false;
    }

    public boolean isPointOnBoundary(double viewX, double viewY, double fangradius_view, WorldViewTransformator2D trafo) {
        return false;
    }

    public GraphicObject clone() {
        System.out.println(" not implemented yet ... ");
        return null;
    }
}
