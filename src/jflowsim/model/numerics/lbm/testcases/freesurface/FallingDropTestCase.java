package jflowsim.model.numerics.lbm.testcases.freesurface;

import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.freesurface.LBMFreeSurfaceGrid;
import jflowsim.model.numerics.lbm.testcases.TestCase;
import jflowsim.model.numerics.utilities.GridNodeType;

public class FallingDropTestCase extends TestCase {

    public UniformGrid getGrid() {

        LBMFreeSurfaceGrid grid = new LBMFreeSurfaceGrid(0.5 /* length */, 0.5 /* width */, 0.005 /* dx */);

        grid.testcase = this.getClass().getSimpleName();

        grid.setLBParameters(0.05 /* nue_lbm */, 0.0 /* forcingX */, -0.0002 /* forcingY */);

        for (int i = 0; i < grid.nx * grid.ny; i++) {
            grid.type[i] = GridNodeType.GAS;
        }

        int dropCenterX = (int) (0.50 * grid.nx);
        int dropCenterY = (int) (0.50 * grid.ny);
        int dropRadius = (int) (0.25 * grid.nx);

        for (int x = 0; x < grid.nx; x++) {
            for (int y = 0; y < grid.ny; y++) {

                double distanceSquared = (dropCenterX - x) * (dropCenterX - x) + (dropCenterY - y) * (dropCenterY - y);

                if (distanceSquared < dropRadius * dropRadius) {
                    grid.setType(x, y, GridNodeType.FLUID);
                    grid.setFill(x, y, 1.0);
                }
            }
        }

        for (int x = 0; x < grid.nx; x++) {
            for (int y = 0; y < grid.ny; y++) {

                if (grid.getType(x, y) == GridNodeType.FLUID) {

                    Boolean changeToInterface = false;

                    for (int dir = 0; dir < 9; dir++) {
                        if (grid.getType(x + LbEQ.ex[dir], y + LbEQ.ey[dir]) == GridNodeType.GAS) {
                            changeToInterface = true;
                        }
                    }

                    if (changeToInterface) {
                        grid.setType(x, y, GridNodeType.INTERFACE);
                        grid.setFill(x, y, 0.5);
                    }
                }
            }
        }



        // Boundary conditions
        for (int x = 0; x < grid.nx; x++) {
            grid.type[x] = GridNodeType.BOUNDARY;
            grid.type[x + (grid.ny - 1) * grid.nx] = GridNodeType.BOUNDARY;
        }
        for (int y = 0; y < grid.ny; y++) {

            grid.type[0 + y * grid.nx] = GridNodeType.BOUNDARY;
            grid.type[grid.nx - 1 + y * grid.nx] = GridNodeType.BOUNDARY;
        }

        grid.dv = grid.viscosity / grid.nue_lbm * grid.nx / grid.getLength();


        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                double[] feq = new double[9];

                LbEQ.getBGKEquilibrium(1.0, 0., 0., feq);

                for (int dir = 0; dir < 9; dir++) {
                    grid.f[(i + j * grid.nx) * 9 + dir] = feq[dir];
                    grid.ftemp[(i + j * grid.nx) * 9 + dir] = feq[dir];
                }

            }
        }


        return grid;
    }
}
