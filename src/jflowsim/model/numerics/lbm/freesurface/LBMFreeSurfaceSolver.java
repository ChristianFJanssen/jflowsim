package jflowsim.model.numerics.lbm.freesurface;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LBMSolver;
import jflowsim.model.numerics.lbm.LBMSolverThread;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.utilities.GridNodeType;
import java.util.concurrent.CyclicBarrier;

public class LBMFreeSurfaceSolver extends LBMSolver {

    public LBMFreeSurfaceSolver(UniformGrid grid) {
        super(grid);
    }

    public void run() {
        // init threads
        for (int rank = 0; rank < num_of_threads; rank++) {
            threadList.add(new LBMFreeSurfaceThread(rank, num_of_threads, this, grid, barrier));
        }

        startThreads();
    }

    class LBMFreeSurfaceThread extends LBMSolverThread {

        public LBMFreeSurfaceThread(int myrank, int num_of_threads, Solver solver, LBMUniformGrid grid, CyclicBarrier barrier) {
            super(myrank, num_of_threads, solver, grid, barrier);
        }

        public void run() {

            try {
                double s_nu = 2. / (6. * grid.nue_lbm + 1.);

                startX = myrank * grid.nx / num_of_threads;
                endX = (myrank + 1) * grid.nx / num_of_threads;

                long timer = System.currentTimeMillis();
                long counter = 0;

                while (true) {
                    if (myrank == 0) {
                        grid.timestep++;
                        counter++;
                    }

                    collision(s_nu);

                    addForcing();

                    propagate();

                    applyBCs();
                    applyBCsFreeSurface();

                    barrier.await();

                    //ftemp hat postcoll-werte
                    //f     hat werte nach der propagation
                    this.calcNewFill();
                    barrier.await();
                    this.setNewNodeStates();
                    barrier.await();
                    this.condenseLostInterfaceCells();
                    barrier.await();
                    this.swapFill();
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

        private void calcNewFill() {

            LBMFreeSurfaceGrid myGrid = (LBMFreeSurfaceGrid) grid;

            int nodeIndex;

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    nodeIndex = (i + j * grid.nx) * 9;

                    if (grid.getType(i, j) == GridNodeType.INTERFACE) {
                        double dm = 0.0;
                        double oldFill = myGrid.getFill(i, j);

                        for (int dir = 1; dir < 9; dir++) {

                            int neighborState = myGrid.getType(i + LbEQ.ex[dir], j + LbEQ.ey[dir]);
                            double neighborFill = myGrid.getFill(i + LbEQ.ex[dir], j + LbEQ.ey[dir]);

                            double faceFill = 0.0;

                            if (neighborState == GridNodeType.FLUID) {
                                faceFill = 1.0;
                            } else if (neighborState == GridNodeType.INTERFACE) {
                                faceFill = 0.5 * (neighborFill + oldFill);
                            }

                            // ftemp enthaelt werte aus der collision, vor dem streaming
                            // f     enthaelt die werte nach dem streaming
                            dm += faceFill * (myGrid.f[nodeIndex + LbEQ.invdir[dir]] - myGrid.ftemp[nodeIndex + dir]);
                        }

                        double newFill = (oldFill * myGrid.getDensityTemp(i, j) + dm) / myGrid.getDensity(i, j);

                        myGrid.setTempFill(i, j, newFill);
                    }
                }
            }
        }

        private void setNewNodeStates() {

            LBMFreeSurfaceGrid myGrid = (LBMFreeSurfaceGrid) grid;

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    // check if the interface nodes change their state
                    if (myGrid.getType(i, j) == GridNodeType.INTERFACE) {

                        // is the interface cell filled up?
                        if (myGrid.getTempFill(i, j) > 1.0) {
                            //achtung, neuer fluidknoten
                            myGrid.setType(i, j, GridNodeType.FLUID);
                            myGrid.setTempFill(i, j, 1.0);

                            //assure closed interface layer
                            for (int dir = 1; dir < 9; dir++) {
                                int ni = i + LbEQ.ex[dir];
                                int nj = j + LbEQ.ey[dir];

                                if (myGrid.getType(ni, nj) == GridNodeType.GAS) {
                                    myGrid.setType(ni, nj, GridNodeType.INTERFACE);
                                    myGrid.setTempFill(ni, nj, 0.01);

                                    double[] feq = new double[9];

                                    LbEQ.getBGKEquilibrium(1.0, 0.0, 0.0, feq);

                                    for (int dir2 = 0; dir2 < 9; dir2++) {
                                        grid.f[(ni + nj * grid.nx) * 9 + dir2] = feq[dir2];
                                        grid.ftemp[(ni + nj * grid.nx) * 9 + dir2] = feq[dir2];
                                    }

                                }
                            }
                        } // is the interface cell emptied?
                        else if (myGrid.getTempFill(i, j) < 0.0) {
                            //achtung neuer gasknoten
                            myGrid.setType(i, j, GridNodeType.GAS);
                            myGrid.setTempFill(i, j, 0.0);

                            //assure closed interface layer
                            for (int dir = 1; dir < 9; dir++) {
                                int ni = i + LbEQ.ex[dir];
                                int nj = j + LbEQ.ey[dir];
                                if (myGrid.getType(ni, nj) == GridNodeType.FLUID) {
                                    myGrid.setType(ni, nj, GridNodeType.INTERFACE);
                                    myGrid.setTempFill(ni, nj, 0.99);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void condenseLostInterfaceCells() {

            LBMFreeSurfaceGrid myGrid = (LBMFreeSurfaceGrid) grid;

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    if (myGrid.getType(i, j) == GridNodeType.INTERFACE) {
                        int interfaceCounter = 0;
                        int fluidCounter = 0;
                        int gasCounter = 0;

                        //assure closed interface layer
                        for (int dir = 1; dir < 9; dir++) {

                            int ni = i + LbEQ.ex[dir];
                            int nj = j + LbEQ.ey[dir];

                            if (myGrid.getType(ni, nj) == GridNodeType.FLUID) {
                                fluidCounter++;
                            }
                            if (myGrid.getType(ni, nj) == GridNodeType.GAS) {
                                gasCounter++;
                            }
                            if (myGrid.getType(ni, nj) == GridNodeType.INTERFACE) {
                                interfaceCounter++;
                            }
                        }

                        // First case: no gas neighbors, entrapped interface cell
                        if (gasCounter == 0) {
                            myGrid.setType(i, j, GridNodeType.FLUID);
                        }

                        // Second case: no fluid neighbors, entrapped interface cell
                        if (fluidCounter == 0) {
                            myGrid.setType(i, j, GridNodeType.GAS);
                        }
                    }
                }
            }
        }

        private void swapFill() {

            LBMFreeSurfaceGrid myGrid = (LBMFreeSurfaceGrid) grid;

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    double fill = myGrid.getFill(i, j);
                    double tempfill = myGrid.getTempFill(i, j);

                    myGrid.setFill(i, j, tempfill);
                    myGrid.setTempFill(i, j, fill);
                }
            }
        }

        private void applyBCsFreeSurface() {

            LBMFreeSurfaceGrid myGrid = (LBMFreeSurfaceGrid) grid;
            int nodeindex;

            double feq[] = new double[9];

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < grid.ny; j++) {

                    // free surface boundary condition on all interface nodes
                    if (myGrid.getType(i, j) == GridNodeType.INTERFACE) {

                        nodeindex = (i + j * grid.nx) * 9;

                        double vx = myGrid.getVeloX(i, j);
                        double vy = myGrid.getVeloY(i, j);

                        //loop over neighbor nodes: if GAS, do the BC
                        for (int dir = 1; dir < 9; dir++) {
                            if (myGrid.getType(i - LbEQ.ex[dir], j - LbEQ.ey[dir]) == GridNodeType.GAS) {
                                LbEQ.getBGKEquilibrium(1.0, vx, vy, feq);
                                myGrid.f[nodeindex + dir] = -myGrid.ftemp[nodeindex + LbEQ.invdir[dir]] + feq[LbEQ.invdir[dir]] + feq[dir];
                            }
                        }
                    }

                }

            }
        }

        protected void applyBCs() {

            LBMFreeSurfaceGrid myGrid = (LBMFreeSurfaceGrid) grid;

            double feq[] = new double[9];
            double vx, vy;
            int nodeIndex = 0;


            //east-Boundary Bounce Back
            for (int dir = 1; dir < 9; dir++) {
                if (LbEQ.ex[dir] == -1) {
                    for (int j = 0; j < grid.ny; j++) {

                        nodeIndex = (grid.nx - 1 + j * grid.nx) * 9;

                        myGrid.f[nodeIndex + dir] = myGrid.ftemp[nodeIndex + LbEQ.invdir[dir]];
                    }
                }
            }//
            //west-Boundary Bounce Back
            for (int dir = 1; dir < 9; dir++) {
                if (LbEQ.ex[dir] == 1) {
                    for (int j = 0; j < grid.ny; j++) {

                        nodeIndex = (0 + j * grid.nx) * 9;

                        myGrid.f[nodeIndex + dir] = myGrid.ftemp[nodeIndex + LbEQ.invdir[dir]];
                    }
                }
            }//
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
    }
    //end: solverthread
}
