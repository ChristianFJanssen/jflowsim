package jflowsim.model.numerics.lbm.shallowwater;

import jflowsim.model.numerics.lbm.*;
import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;

public class LBMGeneralSWBC extends BoundaryCondition {

    LBMShallowWaterGrid myGrid;
    double bcValue;
    Boolean isHeightBC;

    public LBMGeneralSWBC(LBMShallowWaterGrid _grid, int _type, double _bcValue, Boolean _isHeightBC) {
        this.myGrid = _grid;
        this.type = _type;
        this.bcValue = _bcValue;
        this.isHeightBC = _isHeightBC;
    }

    public void apply() {

        int nodeIndex;

        if (type == EAST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = ((myGrid.nx - 1) + j * myGrid.nx) * 9;

                double h = myGrid.ftemp[nodeIndex + LbEQ.ZERO]
                        + myGrid.ftemp[nodeIndex + LbEQ.E]
                        + myGrid.ftemp[nodeIndex + LbEQ.W]
                        + myGrid.ftemp[nodeIndex + LbEQ.N]
                        + myGrid.ftemp[nodeIndex + LbEQ.S]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        + myGrid.ftemp[nodeIndex + LbEQ.SW]
                        + myGrid.ftemp[nodeIndex + LbEQ.NW]
                        + myGrid.ftemp[nodeIndex + LbEQ.SE];

                double ux = (myGrid.ftemp[nodeIndex + LbEQ.E]
                        - myGrid.ftemp[nodeIndex + LbEQ.W]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        - myGrid.ftemp[nodeIndex + LbEQ.SW]
                        + myGrid.ftemp[nodeIndex + LbEQ.SE]
                        - myGrid.ftemp[nodeIndex + LbEQ.NW]) / h;

                double e = myGrid.v_scale;
                ux = ux * myGrid.v_scale;
                double fluxX;

                if (isHeightBC) {
                    fluxX = (this.bcValue * ux);
                } else {
                    fluxX = this.bcValue;
                }



                myGrid.f[nodeIndex + LbEQ.W] = myGrid.f[nodeIndex + LbEQ.E] - 2.0f * fluxX / 3.0f / e;
                myGrid.f[nodeIndex + LbEQ.NW] = myGrid.f[nodeIndex + LbEQ.SE] - fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.S] - myGrid.f[nodeIndex + LbEQ.N]);
                myGrid.f[nodeIndex + LbEQ.SW] = myGrid.f[nodeIndex + LbEQ.NE] - fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.N] - myGrid.f[nodeIndex + LbEQ.S]);
            }
        } else if (type == WEST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = (0 + j * myGrid.nx) * 9;

                double h = myGrid.ftemp[nodeIndex + LbEQ.ZERO]
                        + myGrid.ftemp[nodeIndex + LbEQ.E]
                        + myGrid.ftemp[nodeIndex + LbEQ.W]
                        + myGrid.ftemp[nodeIndex + LbEQ.N]
                        + myGrid.ftemp[nodeIndex + LbEQ.S]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        + myGrid.ftemp[nodeIndex + LbEQ.SW]
                        + myGrid.ftemp[nodeIndex + LbEQ.NW]
                        + myGrid.ftemp[nodeIndex + LbEQ.SE];

                double ux = (myGrid.ftemp[nodeIndex + LbEQ.E]
                        - myGrid.ftemp[nodeIndex + LbEQ.W]
                        + myGrid.ftemp[nodeIndex + LbEQ.NE]
                        - myGrid.ftemp[nodeIndex + LbEQ.SW]
                        + myGrid.ftemp[nodeIndex + LbEQ.SE]
                        - myGrid.ftemp[nodeIndex + LbEQ.NW]) / h;

                double e = myGrid.v_scale;
                ux = ux * myGrid.v_scale;
                double fluxX;

                if (isHeightBC) {
                    fluxX = (this.bcValue * ux);
                } else {
                    fluxX = this.bcValue;
                }



                myGrid.f[nodeIndex + LbEQ.E] = myGrid.f[nodeIndex + LbEQ.W] + 2.0f * fluxX / 3.0f / e;
                myGrid.f[nodeIndex + LbEQ.NE] = myGrid.f[nodeIndex + LbEQ.SW] + fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.S] - myGrid.f[nodeIndex + LbEQ.N]);
                myGrid.f[nodeIndex + LbEQ.SE] = myGrid.f[nodeIndex + LbEQ.NW] + fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.N] - myGrid.f[nodeIndex + LbEQ.S]);
            }
        }
    }
}
