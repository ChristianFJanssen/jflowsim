package jflowsim.model.numerics.acm;

import jflowsim.model.ModelManager;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.utilities.Scalar;

public class AcmGrid extends UniformGrid {

    public double nue, k, dt, checkerboard;
    AcmGridNode[] field;

    public AcmGrid(double _length, double _width, double _dx) {
        super(_length, _width, _dx);
    }

    public AcmGrid(double _length, double _width, int _nx, int _ny) {
        super(_length, _width, _nx, _ny);
    }

    protected void allocateMemory() {

        field = new AcmGridNode[nx * ny];

        for (int i = 0; i < nx * ny; i++) {
            field[i] = new AcmGridNode();

            field[i].setType(GridNodeType.FLUID);
        }
        System.out.println("AcmGrid::allocateMemoery() nx:" + nx + " ny:" + ny + " - " + nx * ny);
    }

    public void updateParameters() {
    }

    public void refineGrid(double scaleFactor) {
    }

    public double getScalar(int x, int y, int type) {

        if (type == Scalar.V_X) {
            return get(x, y).getVeloX();
        } else if (type == Scalar.V_Y) {
            return get(x, y).getVeloY();
        } else if (type == Scalar.V) {
            return Math.sqrt(Math.pow(get(x, y).getVeloX(), 2.0) + Math.pow(get(x, y).getVeloY(), 2.0));
        } else if (type == Scalar.RHO) {
            return get(x, y).getP(0);
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

    public void map2Grid(ModelManager modMan) {
    }

    public int getType(int x, int y) {
        return this.get(x, y).getType();
    }

    public void setType(int x, int y, int type) {

        this.get(x, y).setType(type);
    }

    public double getDxU(int i, int j, int id) {
        return (this.get(i + 1, j).getU(id) - this.get(i - 1, j).getU(id)) / (2 * this.dx);
    }

    public double getDyU(int i, int j, int id) {
        return (this.get(i, j + 1).getU(id) - this.get(i, j - 1).getU(id)) / (2 * this.dx);
    }

    public double getDxS(int i, int j, int id) {
        return (this.get(i + 1, j).getTurbulentViscosity(id) - this.get(i - 1, j).getTurbulentViscosity(id)) / (2 * this.dx);
    }

    public double getDyS(int i, int j, int id) {
        return (this.get(i, j + 1).getTurbulentViscosity(id) - this.get(i, j - 1).getTurbulentViscosity(id)) / (2 * this.dx);
    }

    public double getDxV(int i, int j, int id) {
        return (this.get(i + 1, j).getV(id) - this.get(i - 1, j).getV(id)) / (2 * this.dx);
    }

    public double getDyV(int i, int j, int id) {
        return (this.get(i, j + 1).getV(id) - this.get(i, j - 1).getV(id)) / (2 * this.dx);
    }

    public double getDxxP(int i, int j, int id) {
        return (this.get(i + 1, j).getP(id) - 2 * this.get(i, j).getP(id) + this.get(i - 1, j).getP(id)) / (this.dx * this.dx);
    }

    public double getDyyP(int i, int j, int id) {
        return (this.get(i, j + 1).getP(id) - 2 * this.get(i, j).getP(id) + this.get(i, j - 1).getP(id)) / (this.dx * this.dx);
    }

    public double getDtP(int i, int j, int id) {
        double DxU = (this.get(i + 1, j).getU(id) - this.get(i - 1, j).getU(id)) / (2 * this.dx);
        double DyV = (this.get(i, j + 1).getV(id) - this.get(i, j - 1).getV(id)) / (2 * this.dx);

        return -1 / k * (DxU + DyV) + this.get(i, j).getMedicine() * this.checkerboard;
    }

    public double getDtU(int i, int j, int id) {
        double DxU = (this.get(i + 1, j).getU(id) - this.get(i - 1, j).getU(id)) / (2 * this.dx);
        double DyU = (this.get(i, j + 1).getU(id) - this.get(i, j - 1).getU(id)) / (2 * this.dx);

        double DxP = (this.get(i + 1, j).getP(id) - this.get(i - 1, j).getP(id)) / (2 * this.dx);

        double DxxU = (this.get(i + 1, j).getU(id) - 2 * this.get(i, j).getU(id) + this.get(i - 1, j).getU(id)) / (this.dx * this.dx);
        double DyyU = (this.get(i, j + 1).getU(id) - 2 * this.get(i, j).getU(id) + this.get(i, j - 1).getU(id)) / (this.dx * this.dx);

        double U = this.get(i, j).getU(id);
        double V = this.get(i, j).getV(id);
        double turbviscterm = DxU * this.getDxS(i, j, id) + DyU * this.getDyS(i, j, id);
        return -U * DxU - V * DyU - DxP + (nue + this.get(i, j).getTurbulentViscosity(id)) * (DxxU + DyyU) + turbviscterm; //um's richtig zu machen müsste man noch abl von turbvisc berücksichtigen, aber erstmal nicht ausserdem jenseits von O(eps^2)
    }

    public double getDtV(int i, int j, int id) {
        double DxV = (this.get(i + 1, j).getV(id) - this.get(i - 1, j).getV(id)) / (2 * this.dx);
        double DyV = (this.get(i, j + 1).getV(id) - this.get(i, j - 1).getV(id)) / (2 * this.dx);

        double DyP = (this.get(i, j + 1).getP(id) - this.get(i, j - 1).getP(id)) / (2 * this.dx);

        double DxxV = (this.get(i + 1, j).getV(id) - 2 * this.get(i, j).getV(id) + this.get(i - 1, j).getV(id)) / (this.dx * this.dx);
        double DyyV = (this.get(i, j + 1).getV(id) - 2 * this.get(i, j).getV(id) + this.get(i, j - 1).getV(id)) / (this.dx * this.dx);

        double U = this.get(i, j).getU(id);
        double V = this.get(i, j).getV(id);
        double turbviscterm = DxV * this.getDxS(i, j, id) + DyV * this.getDyS(i, j, id);

        return -U * DxV - V * DyV - DyP + (nue + this.get(i, j).getTurbulentViscosity(id)) * (DxxV + DyyV) + turbviscterm;
    }

    public AcmGridNode get(int x, int y) {

        // Periodicity
        if (x < 0) {
            x = this.nx - 2;
        }
        if (x > this.nx - 1) {
            x = 1;
        }
        if (y < 0) {
            y = this.ny - 2;
        }
        if (y > this.ny - 1) {
            y = 1;
        }

        return (AcmGridNode) field[this.nx * y + x];
    }

    public void setNode(int x, int y, AcmGridNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
