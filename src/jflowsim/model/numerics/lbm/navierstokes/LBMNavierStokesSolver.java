package jflowsim.model.numerics.lbm.navierstokes;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LBMSolver;
import jflowsim.model.numerics.lbm.LBMSolverThread;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
import java.util.concurrent.BrokenBarrierException;

import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LBMNavierStokesSolver extends LBMSolver {

    public LBMNavierStokesSolver(UniformGrid grid) {
        super(grid);
    }

    public void run() {

        // init threads
        for (int rank = 0; rank < num_of_threads; rank++) {
            threadList.add(new LBMNavierStokesThread(rank, num_of_threads, this, grid, barrier));
        }

        // start threads and wait until all have finished
        startThreads();
    }

    /* ======================================================================================= */
    class LBMNavierStokesThread extends LBMSolverThread {

        /* ======================================================================================= */
        public LBMNavierStokesThread(int myrank, int num_of_threads, Solver solver, LBMUniformGrid grid, CyclicBarrier barrier) {
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


                    this.collisionFCM(s_nu);
                    //this.collisionMRT(s_nu);
                    //this.collision(s_nu);

                    this.addForcing();

                    this.propagate();

                    //this.applyBCs();
                    if (myrank == 0) {
                        this.applyBCsNEW();
                    }
                    this.periodicBCs();

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
    }//end: solveInrthread
}//end:solver

