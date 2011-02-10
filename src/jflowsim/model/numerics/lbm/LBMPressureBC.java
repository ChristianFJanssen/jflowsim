package jflowsim.model.numerics.lbm;

import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;

public class LBMPressureBC extends BoundaryCondition {

    LBMNavierStokesGrid myGrid;
    double press;

    public LBMPressureBC(LBMNavierStokesGrid _grid, int _type, double _press) {
        this.myGrid = _grid;
        this.type = _type;
        this.press = _press;
    }

    public void apply() {

        int nodeIndex;
        double vx, vy;

        double feq[] = new double[9];

        if (type == EAST) {
            for (int j = 0; j < myGrid.ny; j++) {

                vx = myGrid.getVeloX(myGrid.nx - 1, j);
                vy = myGrid.getVeloY(myGrid.nx - 1, j);

                LbEQ.getBGKEquilibrium(press, vx, vy, feq);

                nodeIndex = ((myGrid.nx - 1) + j * myGrid.nx) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        } else if (type == WEST) {
            for (int j = 0; j < myGrid.ny; j++) {

                vx = myGrid.getVeloX(0, j);
                vy = myGrid.getVeloY(0, j);

                LbEQ.getBGKEquilibrium(press, vx, vy, feq);

                nodeIndex = (0 + j * myGrid.nx) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        } else if (type == NORTH) {
            for (int i = 0; i < myGrid.nx; i++) {

                vx = myGrid.getVeloX(i, myGrid.ny - 1);
                vy = myGrid.getVeloY(i, myGrid.ny - 1);

                LbEQ.getBGKEquilibrium(press, vx, vy, feq);

                nodeIndex = (i + (myGrid.ny - 1) * myGrid.nx) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        } else if (type == SOUTH) {
            for (int i = 0; i < myGrid.nx; i++) {

                vx = myGrid.getVeloX(i, 0);
                vy = myGrid.getVeloY(i, 0);

                LbEQ.getBGKEquilibrium(press, vx, vy, feq);

                nodeIndex = (i) * 9;

                for (int dir = 0; dir < 9; dir++) {
                    myGrid.f[nodeIndex + dir] = feq[dir];
                }
            }
        }
    }
}
