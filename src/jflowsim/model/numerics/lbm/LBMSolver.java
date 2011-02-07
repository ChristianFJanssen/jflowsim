package jflowsim.model.numerics.lbm;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;
import java.util.ArrayList;

public abstract class LBMSolver extends Solver {

    protected LBMUniformGrid grid;
    protected ArrayList<LBMSolverThread> threadList = new ArrayList<LBMSolverThread>();

    public LBMSolver(UniformGrid grid) {
        super();
        this.grid = (LBMUniformGrid) grid;
    }

    protected void startThreads() {

        // start SolverThreads
        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).start();
        }

        // wait until all threads are finished
        try {
            for (int i = 0; i < threadList.size(); i++) {
                threadList.get(i).join();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public void interrupt() {

        for (int i = 0; i < threadList.size(); i++) {
            threadList.get(i).interrupt();
        }

        threadList.clear();
        this.thread.interrupt();
    }

    protected void collision(double s_nu) {
        double[] feq = new double[9];
        int nodeIndex = -1;

        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                nodeIndex = (i + j * grid.nx) * 9;

                if (grid.getType(i, j) != GridNodeType.SOLID) {

                    double dens = grid.f[nodeIndex + LbEQ.ZERO]
                            + grid.f[nodeIndex + LbEQ.E]
                            + grid.f[nodeIndex + LbEQ.W]
                            + grid.f[nodeIndex + LbEQ.N]
                            + grid.f[nodeIndex + LbEQ.S]
                            + grid.f[nodeIndex + LbEQ.NE]
                            + grid.f[nodeIndex + LbEQ.SW]
                            + grid.f[nodeIndex + LbEQ.NW]
                            + grid.f[nodeIndex + LbEQ.SE];

                    double vx = (grid.f[nodeIndex + LbEQ.E]
                            - grid.f[nodeIndex + LbEQ.W]
                            + grid.f[nodeIndex + LbEQ.NE]
                            - grid.f[nodeIndex + LbEQ.SW]
                            + grid.f[nodeIndex + LbEQ.SE]
                            - grid.f[nodeIndex + LbEQ.NW]) / dens;


                    double vy = (+grid.f[nodeIndex + LbEQ.N]
                            - grid.f[nodeIndex + LbEQ.S]
                            + grid.f[nodeIndex + LbEQ.NE]
                            + grid.f[nodeIndex + LbEQ.NW]
                            - grid.f[nodeIndex + LbEQ.SE]
                            - grid.f[nodeIndex + LbEQ.SW]) / dens;

                    LbEQ.getBGKEquilibrium(dens, vx, vy, feq);

                    for (int dir = 0; dir < 9; dir++) {
                        grid.ftemp[nodeIndex + dir] = grid.f[nodeIndex + dir] - s_nu * (grid.f[nodeIndex + dir] - feq[dir]);
                    }

                } else {
                    //nodal bounce back
                    for (int dir = 1; dir < 9; dir++) {
                        grid.ftemp[nodeIndex + dir] = grid.f[nodeIndex + LbEQ.invdir[dir]];
                    }

                }
            }
        }
    }

    protected void propagate() {
        int plusx, minusx, plusy, minusy;

        int nodeIndex = -1;

        for (int dir = 0; dir < 9; dir++) {
            plusx = LbEQ.ex[dir];
            plusy = LbEQ.ey[dir];
            minusx = -LbEQ.ex[dir];
            minusy = -LbEQ.ey[dir];
            plusx = (plusx < 0 ? 0 : plusx);
            minusx = (minusx < 0 ? 0 : minusx);
            plusy = (plusy < 0 ? 0 : plusy);
            minusy = (minusy < 0 ? 0 : minusy);

            int offset = (LbEQ.ex[dir] + LbEQ.ey[dir] * grid.nx) * 9;

            for (int i = 0 + minusx; i < grid.nx - plusx; i++) {
                for (int j = 0 + minusy; j < grid.ny - plusy; j++) {

                    nodeIndex = (i + j * grid.nx) * 9 + dir;

                    grid.f[nodeIndex + offset] = grid.ftemp[nodeIndex];
                }
            }
        }
    }

    protected void periodicBCs() {

        //top-bottom
        if (grid.periodicY) {

            int nodeNorth = 0;
            int nodeSouth = 0;

            for (int i = 0; i < grid.nx; i++) {

                nodeSouth = (i + (0) * grid.nx) * 9;
                nodeNorth = (i + (grid.ny - 1) * grid.nx) * 9;

                grid.f[nodeSouth + LbEQ.N] = grid.f[nodeNorth + LbEQ.N];
                grid.f[nodeSouth + LbEQ.NE] = grid.f[nodeNorth + LbEQ.NE];
                grid.f[nodeSouth + LbEQ.NW] = grid.f[nodeNorth + LbEQ.NW];

                grid.f[nodeNorth + LbEQ.S] = grid.f[nodeSouth + LbEQ.S];
                grid.f[nodeNorth + LbEQ.SE] = grid.f[nodeSouth + LbEQ.SE];
                grid.f[nodeNorth + LbEQ.SW] = grid.f[nodeSouth + LbEQ.SW];
            }
        }
        //left-right
        if (grid.periodicX) {

            int nodeEast = 0;
            int nodeWest = 0;

            for (int j = 0; j < grid.ny; j++) {

                nodeWest = (0 + j * grid.nx) * 9;
                nodeEast = (grid.nx - 1 + j * grid.nx) * 9;

                grid.f[nodeWest + LbEQ.E] = grid.f[nodeEast + LbEQ.E];
                grid.f[nodeWest + LbEQ.NE] = grid.f[nodeEast + LbEQ.NE];
                grid.f[nodeWest + LbEQ.SE] = grid.f[nodeEast + LbEQ.SE];

                grid.f[nodeEast + LbEQ.W] = grid.f[nodeWest + LbEQ.W];
                grid.f[nodeEast + LbEQ.NW] = grid.f[nodeWest + LbEQ.NW];
                grid.f[nodeEast + LbEQ.SW] = grid.f[nodeWest + LbEQ.SW];
            }
        }
    }
}
