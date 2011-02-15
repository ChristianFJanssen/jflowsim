package jflowsim.model.numerics.lbm.shallowwater;

import jflowsim.model.numerics.lbm.*;
import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;

public class LBMEquilibriumBC extends BoundaryCondition {

    LBMUniformGrid myGrid;
    double h,vx, vy;

    public LBMEquilibriumBC(LBMUniformGrid _grid, int _type, double _h, double _vx, double _vy) {
        this.myGrid = _grid;
        this.type = _type;
        this.h = _h;
        this.vx = _vx;
        this.vy = _vy;
    }

    public void apply() {

        int nodeIndex;

        double feq[] = new double[9];

        if (type == EAST) {
            for (int j = 0; j < myGrid.ny; j++) {

                LbEQ.getBGKEquilibriumShallowWater(h, vx, vy, feq, myGrid.dv, myGrid.gravity);

                nodeIndex = ((myGrid.nx - 1) + j * myGrid.nx) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        } else if (type == WEST) {
            for (int j = 0; j < myGrid.ny; j++) {

                LbEQ.getBGKEquilibriumShallowWater(h, vx, vy, feq, myGrid.dv, myGrid.gravity);

                nodeIndex = (0 + j * myGrid.nx) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        } else if (type == NORTH) {
            for (int i = 0; i < myGrid.nx; i++) {

                LbEQ.getBGKEquilibriumShallowWater(h, vx, vy, feq, myGrid.dv, myGrid.gravity);

                nodeIndex = (i + (myGrid.ny - 1) * myGrid.nx) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        } else if (type == SOUTH) {
            for (int i = 0; i < myGrid.nx; i++) {

                LbEQ.getBGKEquilibriumShallowWater(h, vx, vy, feq, myGrid.dv, myGrid.gravity);
                
                nodeIndex = (i) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        }
    }
}