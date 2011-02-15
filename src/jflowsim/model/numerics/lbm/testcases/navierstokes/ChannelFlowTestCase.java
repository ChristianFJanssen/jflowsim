package jflowsim.model.numerics.lbm.testcases.navierstokes;

import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LBMNoSlipBC;
import jflowsim.model.numerics.lbm.LBMPressureBC;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;
import jflowsim.model.numerics.lbm.LBMVelocityBC;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class ChannelFlowTestCase extends TestCase {

    public UniformGrid getGrid() {

        LBMNavierStokesGrid grid = new LBMNavierStokesGrid(0.5 /* length */, 0.2 /* width */, 0.002 /* dx */);

        grid.testcase = this.getClass().getSimpleName();

        grid.setGravity(0.0, 0.0 /* m/s^2 */);
        grid.setViscosity(0.000001 /* m^2/s */);
        grid.setTimeStep(0.0001 /* s */);

        grid.updateLBParameters();

        this.initFluid(grid);
       
        grid.addBC(new LBMPressureBC(grid, BoundaryCondition.EAST, 1.0));
        grid.addBC(new LBMVelocityBC(grid, BoundaryCondition.WEST, 0.05, 0.0));
        grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.NORTH));
        grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.SOUTH));


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
