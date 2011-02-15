package jflowsim.model.numerics.lbm.testcases.acm;

import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.acm.AcmGrid;
import jflowsim.model.numerics.lbm.testcases.TestCase;
import jflowsim.model.numerics.utilities.GridNodeType;

public class SonjasTestCase extends TestCase {

    public UniformGrid getGrid() {

        AcmGrid grid = new AcmGrid(6.28 /* length */, 6.28 /* width */, 65, 65);

        grid.testcase = this.getClass().getSimpleName();

        grid.nue = 0.1;

        grid.checkerboard = 0.01;

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

        double vory = 0.5 * grid.nx + 0.5 * grid.dx;
        double vorx = 0.5 * grid.ny + 0.5 * grid.dx;
        double gammawirb = 0.1; //intensität wirbel

        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                double distToCenterSq = ((i - vorx) * (i - vorx) + (j - vory) * (j - vory));

                double u = 0.5 - gammawirb * (j - vory) / distToCenterSq;
                double v = 0.0 + gammawirb * (i - vorx) / distToCenterSq;

                grid.get(i, j).setP(0.0);
                grid.get(i, j).setU(u);
                grid.get(i, j).setV(v);

                if (i == 1 || (i == grid.nx - 2)) {
                    grid.get(i, j).setType(GridNodeType.SOLID);
                }
//if (i==0 || (i==grid.nx-1)) grid.get(i, j).setType(grid.get(i, j).SOLID);
                if ((i < 16) && (i > 9) && (j > 9) && (j < 16)) {
                    grid.get(i, j).setType(GridNodeType.SOLID);
                }
                if ((j == 15) && (i > 9) && (i < 16)) {
                    grid.get(i, j).setType(GridNodeType.SOLIDN);
                }
                if ((j == 10) && (i > 9) && (i < 16)) {
                    grid.get(i, j).setType(GridNodeType.SOLIDS);
                }
                if ((i == 10) && (j > 9) && (j < 16)) {
                    grid.get(i, j).setType(GridNodeType.SOLIDW);
                }
                if ((i == 15) && (j > 9) && (j < 16)) {
                    grid.get(i, j).setType(GridNodeType.SOLIDE);
                }
            }
        }

        return grid;
    }
}
