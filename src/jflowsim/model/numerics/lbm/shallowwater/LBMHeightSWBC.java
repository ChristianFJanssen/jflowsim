package jflowsim.model.numerics.lbm.shallowwater;

import jflowsim.model.numerics.lbm.*;
import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;

public class LBMHeightSWBC extends BoundaryCondition {

    LBMShallowWaterGrid myGrid;
    double bcHeight;

    public LBMHeightSWBC(LBMShallowWaterGrid _grid, int _type, double _bcHeight) {
        this.myGrid = _grid;
        this.type = _type;
        this.bcHeight = _bcHeight;
    }

    public void apply() {

        int nodeIndex;

        double feq[] = new double[9];

        if (type == EAST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = ((myGrid.nx - 1) + j * myGrid.nx) * 9;

                double h = bcHeight;

                double vx = (myGrid.ftemp[nodeIndex + LbEQ.E]
                        - myGrid.ftemp[nodeIndex + LbEQ.W]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        - myGrid.ftemp[nodeIndex + LbEQ.SW]
                        + myGrid.ftemp[nodeIndex + LbEQ.SE]
                        - myGrid.ftemp[nodeIndex + LbEQ.NW]) / h;


                double vy = (+myGrid.ftemp[nodeIndex + LbEQ.N]
                        - myGrid.ftemp[nodeIndex + LbEQ.S]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        + myGrid.ftemp[nodeIndex + LbEQ.NW]
                        - myGrid.ftemp[nodeIndex + LbEQ.SE]
                        - myGrid.ftemp[nodeIndex + LbEQ.SW]) / h;

                vy = 0.0;

                LbEQ.getBGKEquilibriumShallowWater(h, vx, vy, feq, myGrid.v_scale, myGrid.gravity);

                myGrid.f[nodeIndex + LbEQ.W] = -myGrid.ftemp[nodeIndex + LbEQ.E] + feq[LbEQ.E] + feq[LbEQ.W];
                myGrid.f[nodeIndex + LbEQ.NW] = -myGrid.ftemp[nodeIndex + LbEQ.SE] + feq[LbEQ.SE] + feq[LbEQ.NW];
                myGrid.f[nodeIndex + LbEQ.SW] = -myGrid.ftemp[nodeIndex + LbEQ.NE] + feq[LbEQ.NE] + feq[LbEQ.SW];
            }
        } else if (type == WEST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = (0 + j * myGrid.nx) * 9;

                double h = bcHeight;

                double vx = (myGrid.ftemp[nodeIndex + LbEQ.E]
                        - myGrid.ftemp[nodeIndex + LbEQ.W]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        - myGrid.ftemp[nodeIndex + LbEQ.SW]
                        + myGrid.ftemp[nodeIndex + LbEQ.SE]
                        - myGrid.ftemp[nodeIndex + LbEQ.NW]) / h;


                double vy = (+myGrid.ftemp[nodeIndex + LbEQ.N]
                        - myGrid.ftemp[nodeIndex + LbEQ.S]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        + myGrid.ftemp[nodeIndex + LbEQ.NW]
                        - myGrid.ftemp[nodeIndex + LbEQ.SE]
                        - myGrid.ftemp[nodeIndex + LbEQ.SW]) / h;

                vy = 0.0;

                LbEQ.getBGKEquilibriumShallowWater(h, vx, vy, feq, myGrid.v_scale, myGrid.gravity);

                myGrid.f[nodeIndex + LbEQ.E] = -myGrid.ftemp[nodeIndex + LbEQ.W] + feq[LbEQ.W] + feq[LbEQ.E];
                myGrid.f[nodeIndex + LbEQ.NE] = -myGrid.ftemp[nodeIndex + LbEQ.SW] + feq[LbEQ.SW] + feq[LbEQ.NE];
                myGrid.f[nodeIndex + LbEQ.SE] = -myGrid.ftemp[nodeIndex + LbEQ.NW] + feq[LbEQ.NW] + feq[LbEQ.SW];
            }
        }
    }
}
