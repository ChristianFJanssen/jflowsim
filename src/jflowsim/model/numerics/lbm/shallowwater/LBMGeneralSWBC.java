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

                double ux = myGrid.getVeloX(myGrid.nx - 2, j);
                double e = myGrid.v_scale;
                ux = ux * myGrid.v_scale;
                double fluxX;

                if (isHeightBC) {
                    fluxX = (this.bcValue * ux);
                } else {
                    fluxX = this.bcValue;
                }

                nodeIndex = ((myGrid.nx - 1) + j * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.W] = myGrid.f[nodeIndex + LbEQ.E] - 2.0f * fluxX / 3.0f / e;
                myGrid.f[nodeIndex + LbEQ.NW] = myGrid.f[nodeIndex + LbEQ.SE] - fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.S] - myGrid.f[nodeIndex + LbEQ.N]);
                myGrid.f[nodeIndex + LbEQ.SW] = myGrid.f[nodeIndex + LbEQ.NE] - fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.N] - myGrid.f[nodeIndex + LbEQ.S]);
            }
        } else if (type == WEST) {
            for (int j = 0; j < myGrid.ny; j++) {

                double ux = myGrid.getVeloX(1, j);
                double e = myGrid.v_scale;
                ux = ux * myGrid.v_scale;
                double fluxX;

                if (isHeightBC) {
                    fluxX = (this.bcValue * ux);
                } else {
                    fluxX = this.bcValue;
                }

                nodeIndex = (0 + j * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.E] = myGrid.f[nodeIndex + LbEQ.W] + 2.0f * fluxX / 3.0f / e;
                myGrid.f[nodeIndex + LbEQ.NE] = myGrid.f[nodeIndex + LbEQ.SW] + fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.S] - myGrid.f[nodeIndex + LbEQ.N]);
                myGrid.f[nodeIndex + LbEQ.SE] = myGrid.f[nodeIndex + LbEQ.NW] + fluxX / 6.0f / e + 0.5f * (myGrid.f[nodeIndex + LbEQ.N] - myGrid.f[nodeIndex + LbEQ.S]);
            }
        }
    }
}
