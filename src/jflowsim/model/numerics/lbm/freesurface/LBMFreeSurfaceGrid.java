package jflowsim.model.numerics.lbm.freesurface;

import jflowsim.model.ModelManager;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.utilities.Scalar;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class LBMFreeSurfaceGrid extends LBMUniformGrid {

    double[] fill;
    double[] tempfill;

    public LBMFreeSurfaceGrid(double _length, double _width, double _dx) {
        super(_length, _width, _dx);
    }

    public LBMFreeSurfaceGrid(double _length, double _width, int _nx, int _ny) {
        super(_length, _width, _nx, _ny);
    }

    protected void allocateMemory() {
        f = new double[nx * ny * 9];
        ftemp = new double[nx * ny * 9];
        type = new int[nx * ny];

        fill = new double[nx * ny];
        tempfill = new double[nx * ny];

        for (int i = 0; i < nx * ny; i++) {
            f[i] = 0.0;
            ftemp[i] = 0.0;
            fill[i] = 0.0;
            tempfill[i] = 0.0;
            type[i] = GridNodeType.GAS;
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
        double fillNew[] = new double[nx * ny];
        double tempfillNew[] = new double[nx * ny];


        System.out.println("LBMFreeSurfaceGrid::allocateMemory() nx:" + nx + " ny:" + ny + " - " + nx * ny);

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
                fillNew[nodeIndexNew / 9] = fill[nodeIndexOld / 9];
                tempfillNew[nodeIndexNew / 9] = tempfill[nodeIndexOld / 9];
            }
        }

        f = fNew;
        ftemp = ftempNew;
        type = typeNew;
        fill = fillNew;
        tempfill = tempfillNew;
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
            return 0.0;
        } else {
            System.out.println("unknown scalar value " + type);
            System.exit(-1);
            return -1;
        }
    }

    public void updateHeadUpDisplay(HeadUpDisplay hud) {
        hud.drawText("LBM viscosity: " + this.nue_lbm);
        hud.drawText("LBM forcing: " + this.forcingX1 + "," + this.forcingX2);
    }

    public void map2Grid(ModelManager modMan) {

        for (int x = 0; x < nx; x++) {
            for (int y = 0; y < ny; y++) {

                if (getType(x, y) == GridNodeType.SOLID) {
                    setType(x, y, GridNodeType.FLUID);
                }
            }
        }
        // map Geometry to Grid
        for (Geometry2D geo : modMan.getGeometryList()) {
            geo.map2Grid(this);
        }
    }

    public double getFill(int x, int y) {
        return this.fill[y * this.nx + x];
    }

    public void setFill(int x, int y, double _fill) {
        this.fill[y * this.nx + x] = _fill;
    }

    public double getTempFill(int x, int y) {
        return this.tempfill[y * this.nx + x];
    }

    public void setTempFill(int x, int y, double _fill) {
        this.tempfill[y * this.nx + x] = _fill;
    }
}
