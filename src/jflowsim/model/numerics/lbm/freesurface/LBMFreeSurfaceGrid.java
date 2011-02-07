package jflowsim.model.numerics.lbm.freesurface;

import jflowsim.model.ModelManager;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
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
