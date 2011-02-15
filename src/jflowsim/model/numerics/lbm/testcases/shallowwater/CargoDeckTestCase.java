package jflowsim.model.numerics.lbm.testcases.shallowwater;

import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LBMNoSlipBC;
import jflowsim.model.numerics.lbm.shallowwater.LBMShallowWaterGrid;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class CargoDeckTestCase extends TestCase {

    public UniformGrid getGrid() {

        LBMShallowWaterGrid grid = new LBMShallowWaterGrid(50.0 /* length */, 20.0 /* width */, 0.25 /* dx */);

        grid.testcase = this.getClass().getSimpleName();

        double h0 = 1.5; // m

        grid.setGravity(0.0, 0.0 /* m/s^2 */);
        grid.setViscosity(0.01 /* m^2/s */);
        grid.setTimeStep(0.01 /* s */);
        grid.updateParameters();

        grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.EAST));
        grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.WEST));
        grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.NORTH));
        grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.SOUTH));

        // Initial conditions
        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {
                //grid.init(i, j, h0, 0.0, 0.0);
                grid.init(i, j, h0*(1.+0.2*Math.sin(Math.PI*i/grid.nx)), 0.0, 0.0);
            }
        }

        return grid;

    }
}
