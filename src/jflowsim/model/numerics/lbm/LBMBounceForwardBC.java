package jflowsim.model.numerics.lbm;

import jflowsim.model.numerics.BoundaryCondition;

public class LBMBounceForwardBC extends BoundaryCondition {

    LBMUniformGrid myGrid;

    public LBMBounceForwardBC(LBMUniformGrid _grid, int _type) {
        this.myGrid = _grid;
        this.type = _type;
    }

    public void apply() {

        int nodeIndex;

        if (type == EAST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = ((myGrid.nx - 1) + j * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.W] = myGrid.ftemp[nodeIndex + LbEQ.E];
                myGrid.f[nodeIndex + LbEQ.NW] = myGrid.ftemp[nodeIndex + LbEQ.NE];
                myGrid.f[nodeIndex + LbEQ.SW] = myGrid.ftemp[nodeIndex + LbEQ.SE];
            }
        } else if (type == WEST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = (0 + j * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.E] = myGrid.ftemp[nodeIndex + LbEQ.W];
                myGrid.f[nodeIndex + LbEQ.NE] = myGrid.ftemp[nodeIndex + LbEQ.NW];
                myGrid.f[nodeIndex + LbEQ.SE] = myGrid.ftemp[nodeIndex + LbEQ.SW];
            }
        } else if (type == NORTH) {

            for (int i = 0; i < myGrid.nx; i++) {

                nodeIndex = (i + (myGrid.ny - 1) * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.S] = myGrid.ftemp[nodeIndex + LbEQ.N];
                myGrid.f[nodeIndex + LbEQ.SE] = myGrid.ftemp[nodeIndex + LbEQ.NE];
                myGrid.f[nodeIndex + LbEQ.SW] = myGrid.ftemp[nodeIndex + LbEQ.NW];
            }
        } else if (type == SOUTH) {
            for (int i = 0; i < myGrid.nx; i++) {

                nodeIndex = (i) * 9;

                myGrid.f[nodeIndex + LbEQ.N] = myGrid.ftemp[nodeIndex + LbEQ.S];
                myGrid.f[nodeIndex + LbEQ.NE] = myGrid.ftemp[nodeIndex + LbEQ.SE];
                myGrid.f[nodeIndex + LbEQ.NW] = myGrid.ftemp[nodeIndex + LbEQ.SW];
            }
        }
    }
}
