package jflowsim.model.numerics.lbm.temperature;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LBMSolver;
import jflowsim.model.numerics.lbm.LBMSolverThread;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.utilities.GridNodeType;
import java.util.concurrent.CyclicBarrier;

public class LBMTemperatureSolver extends LBMSolver {

    public LBMTemperatureSolver(UniformGrid grid) {
        super(grid);
    }

    public void run() {

        // init threads
        for (int rank = 0; rank < num_of_threads; rank++) {
            threadList.add(new LBMTemperatureThread(rank, num_of_threads, this, grid, barrier));
        }

        // start threads and wait until all have finished
        startThreads();
    }

    /* ======================================================================================= */
    class LBMTemperatureThread extends LBMSolverThread {

        /* ======================================================================================= */
        public LBMTemperatureThread(int myrank, int num_of_threads, Solver solver, LBMUniformGrid grid, CyclicBarrier barrier) {
            super(myrank, num_of_threads, solver, grid, barrier);
        }

        /* ======================================================================================= */
        public void run() {

            try {
                LBMTemperatureGrid myGrid = (LBMTemperatureGrid) grid;

                double s_nu = 2. / (6. * myGrid.nue_lbm + 1.);
                double omegaT = 2. / (6. * myGrid.k + 1.);

                long counter = 0;

                long timer = System.currentTimeMillis();

                while (true) {

                    if (myrank == 0) {
                        grid.timestep++;
                        counter++;
                    }

                    collision(s_nu);
                    collisionTemperature(omegaT);

                    addForcingTemperature(myGrid.bouyancyX, myGrid.bouyancyY);

                    propagate();
                    propagateTemperature();

                    periodicBCs();
                    periodicBCsTemperature();

                    applyBCs();
                    applyBCsTemperature();

                    barrier.await();

                    if (myrank == 0) {
                        if (grid.timestep % grid.updateInterval == 0) {
                            grid.real_time = grid.timestep * grid.nue_lbm / grid.viscosity * Math.pow(grid.getLength() / grid.nx, 2.0);
                            grid.mnups = (grid.nx * grid.ny) * counter / ((System.currentTimeMillis() - timer)) / 1000;

                            solver.update();

                            if ((System.currentTimeMillis() - timer) > 2000) {
                                timer = System.currentTimeMillis();
                                counter = 0;
                            }
                        }
                    }

                    // check if thread is interrupted
                    if (Thread.interrupted()) {
                        break;
                    }
                }
            } catch (Exception ex) {
                return;
            }
        }

        protected void applyBCs() {

            LBMTemperatureGrid myGrid = (LBMTemperatureGrid) grid;

            int nodeIndex = 0;

            //top-Boundary Bounce Back
            for (int dir = 1; dir < 9; dir++) {
                if (LbEQ.ey[dir] == -1) {
                    for (int i = startX; i < endX; i++) {

                        nodeIndex = (i + (grid.ny - 1) * grid.nx) * 9;

                        myGrid.f[nodeIndex + dir] = myGrid.ftemp[nodeIndex + LbEQ.invdir[dir]];
                    }
                }
            }//
            //bottom boundary Bounce Back
            for (int dir = 1; dir < 9; dir++) {
                if (LbEQ.ey[dir] == 1) {
                    for (int i = startX; i < endX; i++) {

                        myGrid.f[i * 9 + dir] = myGrid.ftemp[i * 9 + LbEQ.invdir[dir]];
                    }
                }
            }
        }

        private void periodicBCsTemperature() {

            LBMTemperatureGrid myGrid = (LBMTemperatureGrid) grid;

            //top-bottom
            if (grid.periodicY) {

                int nodeNorth = 0;
                int nodeSouth = 0;

                for (int i = startX; i < endX; i++) {

                    nodeSouth = (i + (0) * grid.nx) * 5;
                    nodeNorth = (i + (grid.ny - 1) * grid.nx) * 5;

                    myGrid.g[nodeSouth + 3] = myGrid.g[nodeNorth + 3];
                    myGrid.g[nodeNorth + 4] = myGrid.g[nodeSouth + 4];
                }
            }
            //left-right
            if (grid.periodicX) {

                int nodeEast = 0;
                int nodeWest = 0;

                for (int j = 0; j < grid.ny; j++) {

                    nodeWest = (0 + j * grid.nx) * 5;
                    nodeEast = (grid.nx - 1 + j * grid.nx) * 5;

                    myGrid.g[nodeWest + 1] = myGrid.g[nodeEast + 1];
                    myGrid.g[nodeEast + 2] = myGrid.g[nodeWest + 2];
                }
            }
        }

        private void applyBCsTemperature() {

            LBMTemperatureGrid myGrid = (LBMTemperatureGrid) grid;

            int nodeindex;

            //top-boundary temperature value
            for (int i = startX; i < endX; i++) {

                nodeindex = (i + (grid.ny - 1) * grid.nx) * 5;

                myGrid.g[nodeindex + LbEQ.S] = myGrid.Tcold - myGrid.g[nodeindex + 0] - myGrid.g[nodeindex + 1] - myGrid.g[nodeindex + 2] - myGrid.g[nodeindex + 3];
            }

            for (int i = 0; i < grid.nx; i++) {

                nodeindex = (i + 0 * grid.nx) * 5;

                myGrid.g[nodeindex + LbEQ.N] = myGrid.Thot - myGrid.g[nodeindex + 0] - myGrid.g[nodeindex + 1] - myGrid.g[nodeindex + 2] - myGrid.g[nodeindex + 4];
            }
        }

        private void addForcingTemperature(double bouyancyX, double bouyancyY) {

            LBMTemperatureGrid myGrid = (LBMTemperatureGrid) grid;

            int nodeindex;

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    nodeindex = (i + j * grid.nx) * 9;

                    double T = myGrid.getTemperature(i, j);

                    for (int dir = 0; dir < 9; dir++) {
                        myGrid.ftemp[nodeindex + dir] += 3. * LbEQ.w[dir] * (T - myGrid.T0) * (LbEQ.ex[dir] * bouyancyX + LbEQ.ey[dir] * bouyancyY) / (myGrid.Thot - myGrid.Tcold);
                    }
                }
            }
        }

        protected void collisionTemperature(double omegaT) {
            double[] geq = new double[5];
            int nodeIndexF = -1;
            int nodeIndexG = -1;

            LBMTemperatureGrid myGrid = (LBMTemperatureGrid) grid;

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    nodeIndexF = (i + j * grid.nx) * 9;
                    nodeIndexG = (i + j * grid.nx) * 5;

                    if (grid.getType(i, j) != GridNodeType.SOLID) {

                        double T = myGrid.g[nodeIndexG + LbEQ.ZERO]
                                + myGrid.g[nodeIndexG + LbEQ.E]
                                + myGrid.g[nodeIndexG + LbEQ.W]
                                + myGrid.g[nodeIndexG + LbEQ.N]
                                + myGrid.g[nodeIndexG + LbEQ.S];

                        double dens = grid.f[nodeIndexF + LbEQ.ZERO]
                                + grid.f[nodeIndexF + LbEQ.E]
                                + grid.f[nodeIndexF + LbEQ.W]
                                + grid.f[nodeIndexF + LbEQ.N]
                                + grid.f[nodeIndexF + LbEQ.S]
                                + grid.f[nodeIndexF + LbEQ.NE]
                                + grid.f[nodeIndexF + LbEQ.SW]
                                + grid.f[nodeIndexF + LbEQ.NW]
                                + grid.f[nodeIndexF + LbEQ.SE];

                        double vx = (grid.f[nodeIndexF + LbEQ.E]
                                - grid.f[nodeIndexF + LbEQ.W]
                                + grid.f[nodeIndexF + LbEQ.NE]
                                - grid.f[nodeIndexF + LbEQ.SW]
                                + grid.f[nodeIndexF + LbEQ.SE]
                                - grid.f[nodeIndexF + LbEQ.NW]) / dens;


                        double vy = (+grid.f[nodeIndexF + LbEQ.N]
                                - grid.f[nodeIndexF + LbEQ.S]
                                + grid.f[nodeIndexF + LbEQ.NE]
                                + grid.f[nodeIndexF + LbEQ.NW]
                                - grid.f[nodeIndexF + LbEQ.SE]
                                - grid.f[nodeIndexF + LbEQ.SW]) / dens;

                        LbEQ.getBGKEquilibriumTemperature(T, vx, vy, geq);

                        for (int dir = 0; dir < 5; dir++) {
                            myGrid.gtemp[nodeIndexG + dir] = myGrid.g[nodeIndexG + dir] - omegaT * (myGrid.g[nodeIndexG + dir] - geq[dir]);
                        }

                    } else {
                        //nodal bounce back
                        for (int dir = 1; dir < 5; dir++) {
                            myGrid.gtemp[nodeIndexG + dir] = myGrid.g[nodeIndexG + LbEQ.invdir[dir]];
                        }

                    }
                }
            }
        }

        private void propagateTemperature() {

            LBMTemperatureGrid myGrid = (LBMTemperatureGrid) grid;

            int nodeIndex;

            int plusx, minusx, plusy, minusy;
            for (int dir = 0; dir < 5; dir++) {
                plusx = LbEQ.ex[dir];
                plusy = LbEQ.ey[dir];
                minusx = -LbEQ.ex[dir];
                minusy = -LbEQ.ey[dir];
                plusx = (plusx < 0 ? 0 : plusx);
                minusx = (minusx < 0 ? 0 : minusx);
                plusy = (plusy < 0 ? 0 : plusy);
                minusy = (minusy < 0 ? 0 : minusy);

                int offset = (LbEQ.ex[dir] + LbEQ.ey[dir] * grid.nx) * 5;


                if (myrank > 0) {
                    minusx = 0;
                }
                if (myrank < num_of_threads - 1) {
                    plusx = 0;
                }


                for (int i = startX + minusx; i < endX - plusx; i++) {
                    for (int j = 0 + minusy; j < grid.ny - plusy; j++) {

                        nodeIndex = (i + j * grid.nx) * 5 + dir;

                        myGrid.g[nodeIndex + offset] = myGrid.gtemp[nodeIndex];
                    }
                }
            }
        }
    }//end: solverthread
}
