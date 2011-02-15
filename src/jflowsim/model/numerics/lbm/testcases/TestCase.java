package jflowsim.model.numerics.lbm.testcases;

import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;

public abstract class TestCase {

    public abstract UniformGrid getGrid();

    protected void initCircle(UniformGrid grid, double centerX, double centerY, double radius) {
        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {
                
                double x = i*grid.dx;
                double y = j*grid.dx;

                double dx = x-centerX;
                double dy = y-centerY;

                double dist = Math.sqrt(dx*dx+dy*dy);

                if( dist < radius )
                {
                    grid.setType(i,j,GridNodeType.BOUNDARY);
                }
            }
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
