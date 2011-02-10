package jflowsim.model.numerics.lbm.testcases;

import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;

public abstract class TestCase {

    public abstract UniformGrid getGrid();

    public void initFluid(UniformGrid grid) {
        for (int y = 0; y < grid.ny; y++) {
            grid.type[grid.nx - 1 + y * grid.nx] = GridNodeType.FLUID;
        }
    }

    public void initWallEast(UniformGrid grid) {
        for (int y = 0; y < grid.ny; y++) {
            grid.type[grid.nx - 1 + y * grid.nx] = GridNodeType.BOUNDARY;
        }
    }

    public void initWallWest(UniformGrid grid) {
        for (int y = 0; y < grid.ny; y++) {
            grid.type[0 + y * grid.nx] = GridNodeType.BOUNDARY;
        }
    }

    public void initWallNorth(UniformGrid grid) {
        for (int x = 0; x < grid.nx; x++) {
            grid.type[x + (grid.ny - 1) * grid.nx] = GridNodeType.BOUNDARY;
        }
    }

    public void initWallSouth(UniformGrid grid) {
        for (int x = 0; x < grid.nx; x++) {
            grid.type[x] = GridNodeType.BOUNDARY;
        }
    }
}
