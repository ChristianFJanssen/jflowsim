package jflowsim.model.numerics.lbm.shallowwater;

import java.util.logging.Level;
import java.util.logging.Logger;
import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LBMSolver;
import jflowsim.model.numerics.lbm.LBMSolverThread;
import jflowsim.model.numerics.lbm.LBMUniformGrid;

import java.util.concurrent.CyclicBarrier;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.utilities.GridNodeType;

public class LBMShallowWaterSolver extends LBMSolver {

    public LBMShallowWaterSolver(UniformGrid grid) {
        super(grid);
    }

    public void run() {

        // init threads
        for (int rank = 0; rank < num_of_threads; rank++) {
            threadList.add(new LBMShallowWaterThread(rank, num_of_threads, this, grid, barrier));
        }

        // start threads and wait until all have finished
        startThreads();
    }

    /* ======================================================================================= */
    class LBMShallowWaterThread extends LBMSolverThread {

        /* ======================================================================================= */
        public LBMShallowWaterThread(int myrank, int num_of_threads, Solver solver, LBMUniformGrid grid, CyclicBarrier barrier) {
            super(myrank, num_of_threads, solver, grid, barrier);
        }

        /* ======================================================================================= */
        public void run() {

            try {

                double s_nu = 2. / (6. * grid.nue_lbm + 1.);

                long timer = 0;
                long counter = 0;

                if (myrank == 0) {
                    timer = System.currentTimeMillis();
                }

                while (true) {

                    if (myrank == 0) {
                        grid.timestep++;
                        counter++;
                    }

                    this.collisionShallowWater(s_nu);

                    this.propagate();
                    this.periodicBCs();

                    if (myrank == 0) {
                        this.applyBCsNEW();
                    }

                    barrier.await();

                    if (myrank == 0) {
                        if (grid.timestep % grid.updateInterval == 0) {

                            long timespan = ((System.currentTimeMillis() - timer));

                            if (timespan > 0) {

                                grid.real_time = grid.timestep * grid.nue_lbm / grid.viscosity * Math.pow(grid.getLength() / grid.nx, 2.0);
                                grid.mnups = (grid.nx * grid.ny) * counter / timespan / 1000.0;

                                solver.update();
                            }

                            if ((System.currentTimeMillis() - timer) > 2000) {
                                timer = System.currentTimeMillis();
                                counter = 0;
                            }

                            //grid.adjustMachNumber(0.1);
                        }
                    }


                    // check if thread is interrupted
                    if (isInterrupted()) {
                        return;
                    }
                }

            } catch (Exception ex) {
                System.out.println("OVERALL EXCEPTION " + ex);
                return;
            }
        }

        protected void collisionShallowWater(double s_nu) {
        double[] feq = new double[9];
        int nodeIndex = -1;

        try {

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    nodeIndex = (i + j * grid.nx) * 9;

                    if (grid.getType(i, j) != GridNodeType.SOLID) {

                        double h = grid.f[nodeIndex + LbEQ.ZERO]
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
                                - grid.f[nodeIndex + LbEQ.NW]) / h;


                        double vy = (+grid.f[nodeIndex + LbEQ.N]
                                - grid.f[nodeIndex + LbEQ.S]
                                + grid.f[nodeIndex + LbEQ.NE]
                                + grid.f[nodeIndex + LbEQ.NW]
                                - grid.f[nodeIndex + LbEQ.SE]
                                - grid.f[nodeIndex + LbEQ.SW]) / h;

                        LbEQ.getBGKEquilibriumShallowWater(h, vx, vy, feq, grid.v_scale, grid.gravity);

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

        } catch (Exception ex) {
            System.out.println("Collision " + ex);
            return;

        }
    }
    }//end: solveInrthread
}//end:solver

