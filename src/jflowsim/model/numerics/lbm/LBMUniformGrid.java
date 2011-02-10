package jflowsim.model.numerics.lbm;

import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;

public abstract class LBMUniformGrid extends UniformGrid {

    public double nue_lbm, forcingX1, forcingX2;
    public double f[];
    public double ftemp[];
    public double v_in_lbm;

    public LBMUniformGrid(double _length, double _width, double _dx) {
        super(_length, _width, _dx);
    }

    public LBMUniformGrid(double _length, double _width, int _nx, int _ny) {
        super(_length, _width, _nx, _ny);
    }

    public void setLBParameters(double _nu, double _forcingX1, double _forcingX2) {
        this.nue_lbm = _nu;
        this.forcingX1 = _forcingX1;
        this.forcingX2 = _forcingX2;
    }

    public void updateLBParameters() {
        //calculate the lattice vlaues for forcing and viscosity
        this.nue_lbm = this.viscosity * this.dt / this.dx / this.dx;
        this.forcingX1 = this.gravityX * this.dt * this.dt / this.dx;
        this.forcingX2 = this.gravityY * this.dt * this.dt / this.dx;

        this.v_scale = this.dx / this.dt;
    }

//    public void adjustMachNumber(double _targetValue) {
//
//        double min = Double.MAX_VALUE;
//        double max = -Double.MAX_VALUE;
//
//        for (int i = 0; i < this.nx; i++) {
//            for (int j = 0; j < this.ny; j++) {
//
//                if (this.getType(i, j) == GridNodeType.FLUID) {
//                    double vx = this.getVeloX(i, j);
//                    double vy = this.getVeloY(i, j);
//
//                    double v = Math.sqrt(vx * vx + vy * vy);
//
//                    if (v < min) {
//                        min = v;
//                    }
//                    if (v > max) {
//                        max = v;
//                    }
//                }
//
//            }
//        }
//
//        double ratio = max / _targetValue;
//
//        if (ratio > 1.) {
//            this.dt /= ratio;
//            this.v_in_lbm /= ratio;
//
//            this.updateLBParameters();
//        }
//
//    }
    public double getDensity(int x, int y) {

        int nodeIndex = (y * this.nx + x) * 9;

        return this.f[nodeIndex + LbEQ.ZERO]
                + this.f[nodeIndex + LbEQ.E]
                + this.f[nodeIndex + LbEQ.W]
                + this.f[nodeIndex + LbEQ.N]
                + this.f[nodeIndex + LbEQ.S]
                + this.f[nodeIndex + LbEQ.NE]
                + this.f[nodeIndex + LbEQ.SW]
                + this.f[nodeIndex + LbEQ.NW]
                + this.f[nodeIndex + LbEQ.SE];
    }

    public double getDensityTemp(int x, int y) {

        int nodeIndex = (y * this.nx + x) * 9;

        return this.ftemp[nodeIndex + LbEQ.ZERO]
                + this.ftemp[nodeIndex + LbEQ.E]
                + this.ftemp[nodeIndex + LbEQ.W]
                + this.ftemp[nodeIndex + LbEQ.N]
                + this.ftemp[nodeIndex + LbEQ.S]
                + this.ftemp[nodeIndex + LbEQ.NE]
                + this.ftemp[nodeIndex + LbEQ.SW]
                + this.ftemp[nodeIndex + LbEQ.NW]
                + this.ftemp[nodeIndex + LbEQ.SE];
    }

    public double getVeloX(int x, int y) {

        int nodeIndex = (y * this.nx + x) * 9;

        return (this.f[nodeIndex + LbEQ.E]
                - this.f[nodeIndex + LbEQ.W]
                + this.f[nodeIndex + LbEQ.NE]
                - this.f[nodeIndex + LbEQ.SW]
                + this.f[nodeIndex + LbEQ.SE]
                - this.f[nodeIndex + LbEQ.NW]) / getDensity(x, y);
    }

    public double getVeloY(int x, int y) {

        int nodeIndex = (y * this.nx + x) * 9;

        return (+this.f[nodeIndex + LbEQ.N]
                - this.f[nodeIndex + LbEQ.S]
                + this.f[nodeIndex + LbEQ.NE]
                + this.f[nodeIndex + LbEQ.NW]
                - this.f[nodeIndex + LbEQ.SE]
                - this.f[nodeIndex + LbEQ.SW]) / getDensity(x, y);
    }
}
