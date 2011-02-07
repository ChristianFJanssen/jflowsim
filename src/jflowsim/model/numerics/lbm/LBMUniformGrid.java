package jflowsim.model.numerics.lbm;

import jflowsim.model.numerics.UniformGrid;

public abstract class LBMUniformGrid extends UniformGrid {

    public LBMUniformGrid(double _length, double _width, double _dx) {
        super(_length,_width,_dx);
    }

    public LBMUniformGrid(double _length, double _width, int _nx, int _ny) {
        super(_length,_width,_nx,_ny);
    }

    public void setParameters(double _nu, double _forcingX1, double _forcingX2 ){
        this.nue_lbm = _nu;
        this.forcingX1 = _forcingX1;
        this.forcingX2 = _forcingX2;
    }
    
    public double nue_lbm, forcingX1, forcingX2;
    public double f[];
    public double ftemp[];

    public double bcVeloX[];
    public double bcVeloY[];
    public double bcPress[];

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

    public double calcDensity(double[] f) {
        return f[LbEQ.ZERO] + f[LbEQ.E] + f[LbEQ.W] + f[LbEQ.N] + f[LbEQ.S] + f[LbEQ.NE] + f[LbEQ.SW] + f[LbEQ.NW] + f[LbEQ.SE];
    }

    public double calcVeloX(double[] f) {
        return (f[LbEQ.E] - f[LbEQ.W] + f[LbEQ.NE] - f[LbEQ.SW] + f[LbEQ.SE] - f[LbEQ.NW]) / calcDensity(f);
    }

    public double calcVeloY(double[] f) {
        return (f[LbEQ.N] - f[LbEQ.S] + f[LbEQ.NE] + f[LbEQ.NW] - f[LbEQ.SE] - f[LbEQ.SW]) / calcDensity(f);
    }
}
