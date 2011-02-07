package jflowsim.model.numerics.lbm.temperature;

import jflowsim.model.ModelManager;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.utilities.Scalar;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class LBMTemperatureGrid extends LBMUniformGrid {

    public double k, Thot, Tcold, T0, bouyancyX, bouyancyY;
    public double[] g;
    public double[] gtemp;

    public LBMTemperatureGrid(double _length, double _width, double _dx) {
        super(_length, _width, _dx);
    }

    public LBMTemperatureGrid(double _length, double _width, int _nx, int _ny) {
        super(_length, _width, _nx, _ny);
    }

    protected void allocateMemory() {
        f = new double[nx * ny * 9];
        ftemp = new double[nx * ny * 9];
        g = new double[nx * ny * 5];
        gtemp = new double[nx * ny * 5];
        type = new int[nx * ny];

        System.out.println("LBMNodefreeGrid::allocateMemoery() nx:" + nx + " ny:" + ny + " - " + nx * ny);
    }

    public double getScalar(int x, int y, int type) {

        if (type == Scalar.V_X) {
            return getVeloX(x, y);
        } else if (type == Scalar.V_Y) {
            return getVeloY(x, y);
        } else if (type == Scalar.V) {
            return Math.sqrt(Math.pow(getVeloX(x, y), 2.0) + Math.pow(getVeloY(x, y), 2.0)) / getDensity(x, y);
        } else if (type == Scalar.RHO) {
            return getDensity(x, y);
        } else if (type == Scalar.GRID_TYPE) {
            return this.getType(x, y);
        } else if (type == Scalar.T) {
            return this.getTemperature(x,y);
        } else {
            System.out.println("unknown scalar value " + type);
            System.exit(-1);
            return -1;
        }
    }

    public void updateHeadUpDisplay(HeadUpDisplay hud) {
        hud.drawText("Thermal diffusivity: " + this.k);
        hud.drawText("T_hot: " + this.Thot);
        hud.drawText("T_cold: " + this.Tcold);
        hud.drawText("T_0: " + this.T0);

        hud.drawText("Bouyancy");
        hud.drawText("X: " + this.bouyancyX);
        hud.drawText("Y: " + this.bouyancyY);

    }

    public double getTemperature(int x, int y){

        int nodeIndex = (y * this.nx + x) * 5;

        return this.g[nodeIndex + LbEQ.ZERO]
                + this.g[nodeIndex + LbEQ.E]
                + this.g[nodeIndex + LbEQ.W]
                + this.g[nodeIndex + LbEQ.N]
                + this.g[nodeIndex + LbEQ.S];
    }


    public void map2Grid(ModelManager modMan) {
    }
}
