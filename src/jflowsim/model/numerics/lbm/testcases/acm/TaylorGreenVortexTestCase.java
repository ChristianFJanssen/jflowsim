package jflowsim.model.numerics.lbm.testcases.acm;

import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.acm.AcmGrid;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class TaylorGreenVortexTestCase extends TestCase {

    public UniformGrid getGrid() {

        AcmGrid grid = new AcmGrid(6.28 /* length */, 6.28 /* width */, 65, 65);

        grid.testcase = this.getClass().getSimpleName();

        grid.dx = grid.getLength() / (grid.nx - 1);

        grid.nue = 0.001;

        grid.checkerboard = 0.0;

        grid.dt = 0.005;

        grid.k = grid.dx * grid.dx * 2;

        System.out.println("Grid: nx:" + grid.nx + " ny:" + grid.ny + " - " + grid.nx * grid.ny);
        System.out.println("Delta: " + grid.dx + "    k: " + grid.k);

        for (int x = 0; x < grid.nx; x++) {
            grid.get(x, 0).initBoundary();
            grid.get(x, grid.ny - 1).initBoundary();
        }
        for (int y = 0; y < grid.ny; y++) {
            grid.get(0, y).initBoundary();
//            get(1, y).initBoundary();
//            get(2, y).initBoundary();
            grid.get(grid.nx - 1, y).initBoundary();
        }

        grid.dv = 1.0;

        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                double amp = 1;

                double u = amp * Math.sin(i * grid.dx) * Math.cos(j * grid.dx);
                double v = -amp * Math.cos(i * grid.dx) * Math.sin(j * grid.dx);

                grid.get(i, j).setP(0.0);
                grid.get(i, j).setU(u);
                grid.get(i, j).setV(v);

            }
        }

        return grid;
    }
}
