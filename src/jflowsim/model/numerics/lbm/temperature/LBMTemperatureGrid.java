package jflowsim.model.numerics.lbm.temperature;

import java.text.DecimalFormat;
import jflowsim.model.ModelManager;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.utilities.GridNodeType;
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

        for (int i = 0; i < nx * ny; i++) {
            f[i] = 0.0;
            ftemp[i] = 0.0;
            g[i] = 0.0;
            gtemp[i] = 0.0;
            type[i] = GridNodeType.FLUID;
        }

        System.out.println("LBMNodefreeGrid::allocateMemoery() nx:" + nx + " ny:" + ny + " - " + nx * ny);
    }

    public void refineGrid(double scaleFactor) {

        System.out.println("Refinement factor: " + scaleFactor);

        int nxOld = this.nx;
        int nyOld = this.ny;

        // update information on the domain size
        this.nx = (int) ((this.nx - 1) * scaleFactor) + 1;
        this.ny = (int) ((this.ny - 1) * scaleFactor) + 1;
        this.dx = this.getLength() / (this.nx - 1);
        this.updateParameters();

        // allocate memory for distribution functions and geo matrix
        double fNew[] = new double[nx * ny * 9];
        double ftempNew[] = new double[nx * ny * 9];
        int typeNew[] = new int[nx * ny];
        double gNew[] = new double[nx * ny * 5];
        double gtempNew[] = new double[nx * ny * 5];


        System.out.println("LBMTemperatureGrid::allocateMemory() nx:" + nx + " ny:" + ny + " - " + nx * ny);

        // constant interpolation of the PDFs and the geo matrix
        for (int x = 0; x < this.nx; x++) {
            for (int y = 0; y < this.ny; y++) {
                // index of source node in old data array
                int xOld = (int) Math.floor(x / scaleFactor);
                int yOld = (int) Math.floor(y / scaleFactor);

                int nodeIndexOld = (yOld * nxOld + xOld) * 9;
                int nodeIndexNew = (y * this.nx + x) * 9;

                for (int dir = 0; dir <= LbEQ.ENDDIR; dir++) {
                    fNew[nodeIndexNew + dir] = f[nodeIndexOld + dir];
                    ftempNew[nodeIndexNew + dir] = ftemp[nodeIndexOld + dir];
                }
                typeNew[nodeIndexNew / 9] = type[nodeIndexOld / 9];

                nodeIndexOld = (yOld * nxOld + xOld) * 5;
                nodeIndexNew = (y * this.nx + x) * 5;

                for (int dir = 0; dir <= LbEQ.S; dir++) {
                    gNew[nodeIndexNew + dir] = g[nodeIndexOld + dir];
                    gtempNew[nodeIndexNew + dir] = gtemp[nodeIndexOld + dir];
                }
            }
        }

        f = fNew;
        ftemp = ftempNew;
        type = typeNew;
        g = gNew;
        gtemp = gtempNew;
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
            return this.getTemperature(x, y);
        } else {
            System.out.println("unknown scalar value " + type);
            System.exit(-1);
            return -1;
        }
    }

    public void updateHeadUpDisplay(HeadUpDisplay hud) {

        DecimalFormat df = new DecimalFormat("0.00E0");

        hud.drawText("LBM viscosity: " + df.format(this.nue_lbm));
        hud.drawText("LBM forcing: " + df.format(this.forcingX1) + " , " + df.format(this.forcingX2));
        hud.drawText("v_in_lbm: " + v_in_lbm);


        hud.drawText("Thermal diffusivity: " + df.format(this.k));
        hud.drawText("T_hot: " + this.Thot);
        hud.drawText("T_cold: " + this.Tcold);
        hud.drawText("T_0: " + this.T0);

        hud.drawText("Bouyancy: " + df.format(this.bouyancyX) + " , " + df.format(this.bouyancyY));
    }

    public double getTemperature(int x, int y) {

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
