package jflowsim.model.numerics.lbm;

import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;

public class LBMMovingWallBC extends BoundaryCondition {

    LBMUniformGrid myGrid;
    double vx, vy;

    public LBMMovingWallBC(LBMUniformGrid _grid, int _type, double _vx, double _vy) {
        this.myGrid = _grid;
        this.type = _type;
        this.vx = _vx;
        this.vy = _vy;
    }

    public void apply() {

        int nodeIndex;

                double vScale = myGrid.dv;

        if (type == EAST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = ((myGrid.nx - 1) + j * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.W] = myGrid.ftemp[nodeIndex + LbEQ.E] - 2. / 3. * vx/vScale;
                myGrid.f[nodeIndex + LbEQ.NW] = myGrid.ftemp[nodeIndex + LbEQ.SE] - 1. / 6. * (vx - vy)/vScale;
                myGrid.f[nodeIndex + LbEQ.SW] = myGrid.ftemp[nodeIndex + LbEQ.NE] - 1. / 6. * (vx + vy)/vScale;
            }
        } else if (type == WEST) {
            for (int j = 0; j < myGrid.ny; j++) {

                nodeIndex = (0 + j * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.E] = myGrid.ftemp[nodeIndex + LbEQ.W] + 2. / 3. * vx/vScale;
                myGrid.f[nodeIndex + LbEQ.NE] = myGrid.ftemp[nodeIndex + LbEQ.SW] + 1. / 6. * (vx + vy)/vScale;
                myGrid.f[nodeIndex + LbEQ.SE] = myGrid.ftemp[nodeIndex + LbEQ.NW] + 1. / 6. * (vx - vy)/vScale;
            }
        } else if (type == NORTH) {

            for (int i = 0; i < myGrid.nx; i++) {

                nodeIndex = (i + (myGrid.ny - 1) * myGrid.nx) * 9;

                myGrid.f[nodeIndex + LbEQ.S] = myGrid.ftemp[nodeIndex + LbEQ.N] - 2. / 3. * vy/vScale;
                myGrid.f[nodeIndex + LbEQ.SE] = myGrid.ftemp[nodeIndex + LbEQ.NW] + 1. / 6. * (vx - vy)/vScale;
                myGrid.f[nodeIndex + LbEQ.SW] = myGrid.ftemp[nodeIndex + LbEQ.NE] - 1. / 6. * (vx + vy)/vScale;
            }
        } else if (type == SOUTH) {
            for (int i = 0; i < myGrid.nx; i++) {

                nodeIndex = (i) * 9;

                myGrid.f[nodeIndex + LbEQ.N] = myGrid.ftemp[nodeIndex + LbEQ.S] + 2. / 3. * vy/vScale;
                myGrid.f[nodeIndex + LbEQ.NE] = myGrid.ftemp[nodeIndex + LbEQ.SW] + 1. / 6. * (vx + vy)/vScale;
                myGrid.f[nodeIndex + LbEQ.NW] = myGrid.ftemp[nodeIndex + LbEQ.SE] - 1. / 6. * (vx - vy)/vScale;
            }
        }
    }
}
