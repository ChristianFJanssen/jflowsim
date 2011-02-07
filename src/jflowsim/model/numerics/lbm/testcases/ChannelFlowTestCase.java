package jflowsim.model.numerics.lbm.testcases;

import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;

public class ChannelFlowTestCase extends TestCase {

    public UniformGrid getGrid() {

        LBMNavierStokesGrid grid = new LBMNavierStokesGrid(0.5 /* length */, 0.2 /* width */, 0.002 /* dx */);

        grid.testcase = this.getClass().getSimpleName();

        grid.setParameters(0.0002 /* nue_lbm */, 0.0 /* forcingX */, 0.0 /* forcingY */);

        grid.setVxIn(1.0);



        for (int i = 0; i < grid.nx * grid.ny; i++) {
            grid.type[i] = GridNodeType.FLUID;
        }

        // Boundary conditions
        for (int x = 0; x < grid.nx; x++) {
            grid.type[x] = GridNodeType.BOUNDARY;
            grid.type[x + (grid.ny - 1) * grid.nx] = GridNodeType.BOUNDARY;
        }
        for (int y = 0; y < grid.ny; y++) {

            grid.type[0 + y * grid.nx] = GridNodeType.BOUNDARY;
            grid.type[1 + y * grid.nx] = GridNodeType.BOUNDARY;
            grid.type[2 + y * grid.nx] = GridNodeType.BOUNDARY;
            grid.type[grid.nx - 1 + y * grid.nx] = GridNodeType.BOUNDARY;
        }



        // Initial conditions

        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                //LbEQ.getBGKEquilibrium(1.0, 4. * grid.v_in_lbm * (0.25 - Math.pow((0.0 + grid.ny) * 0.5 / (grid.ny - 0.0) - (double) (j) / (grid.ny - 0.0), 2.)), 0., grid.getF(i, j));
                //LbEQ.getBGKEquilibrium(1.0, 4. * grid.v_in_lbm * (0.25 - Math.pow((0.0 + grid.ny) * 0.5 / (grid.ny - 0.0) - (double) (j) / (grid.ny - 0.0), 2.)), 0., grid.getFtemp(i, j));

                double[] feq = new double[9];

                LbEQ.getBGKEquilibrium(1.0, 4. * grid.v_in_lbm * (0.25 - Math.pow((0.0 + grid.ny) * 0.5 / (grid.ny - 0.0) - (double) (j) / (grid.ny - 0.0), 2.)), 0., feq);

                for (int dir = 0; dir < 9; dir++) {
                    grid.f[(i + j * grid.nx) * 9 + dir] = feq[dir];
                    grid.ftemp[(i + j * grid.nx) * 9 + dir] = feq[dir];
                }

            }
        }

        return grid;

    }
}
