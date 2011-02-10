package jflowsim.model.numerics.lbm;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesGrid;
import jflowsim.model.numerics.utilities.GridNodeType;
import java.util.concurrent.CyclicBarrier;
import jflowsim.model.numerics.BoundaryCondition;

/* ======================================================================================= */
/* ======================================================================================= */
public abstract class LBMSolverThread extends Thread {

    protected int myrank;
    protected Solver solver;
    protected int startX, endX;
    protected LBMUniformGrid grid;
    protected int num_of_threads;
    protected CyclicBarrier barrier;

    /* ======================================================================================= */
    public LBMSolverThread(int myrank, int num_of_threads, Solver solver, LBMUniformGrid grid, CyclicBarrier barrier) {
        super("SolverThread-" + myrank);
        this.myrank = myrank;
        this.num_of_threads = num_of_threads;
        this.solver = solver;
        this.grid = grid;
        this.barrier = barrier;

        startX = myrank * grid.nx / num_of_threads;
        endX = (myrank + 1) * grid.nx / num_of_threads;

        System.out.println(startX + " " + endX);
    }


    /* ======================================================================================= */
    protected void applyBCsNEW() {
        LBMUniformGrid myGrid = (LBMUniformGrid) grid;

        for (BoundaryCondition bc : myGrid.bcList) {
            bc.apply();
            //System.out.println(bc.getClass().getSimpleName() + "...DONE");
        }

    }
    /* ======================================================================================= */
    protected void collision(double s_nu) {
        double[] feq = new double[9];
        int nodeIndex = -1;

        try {

            for (int i = startX; i < endX; i++) {
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

        } catch (Exception ex) {
            System.out.println("Collision " + ex);
            return;

        }
    }
    /* ======================================================================================= */

    protected void collisionMRT(double s_nu) {
        double[] feq = new double[9];
        int nodeIndex = -1;

        for (int i = startX; i < endX; i++) {
            for (int j = 0; j < grid.ny; j++) {

                nodeIndex = (i + j * grid.nx) * 9;

                if (grid.getType(i, j) != GridNodeType.SOLID) {

                    double rho = grid.f[nodeIndex + LbEQ.ZERO]
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
                            - grid.f[nodeIndex + LbEQ.NW]) / rho;


                    double vy = (+grid.f[nodeIndex + LbEQ.N]
                            - grid.f[nodeIndex + LbEQ.S]
                            + grid.f[nodeIndex + LbEQ.NE]
                            + grid.f[nodeIndex + LbEQ.NW]
                            - grid.f[nodeIndex + LbEQ.SE]
                            - grid.f[nodeIndex + LbEQ.SW]) / rho;

                    double R = grid.f[nodeIndex + LbEQ.ZERO];
                    double E = grid.f[nodeIndex + LbEQ.E];
                    double W = grid.f[nodeIndex + LbEQ.W];
                    double N = grid.f[nodeIndex + LbEQ.N];
                    double S = grid.f[nodeIndex + LbEQ.S];
                    double Ne = grid.f[nodeIndex + LbEQ.NE];
                    double Sw = grid.f[nodeIndex + LbEQ.SW];
                    double Nw = grid.f[nodeIndex + LbEQ.NW];
                    double Se = grid.f[nodeIndex + LbEQ.SE];


                    double P = (1. / 12. * (rho * (vx * vx + vy * vy) - E - N - S - W - 2 * (Se + Sw + Ne + Nw - 1. / 3. * rho)));
                    double NE = (s_nu * 0.25 * (N + S - E - W + rho * (vx * vx - vy * vy)));
                    double V = (s_nu * ((Ne + Sw - Nw - Se) - vx * vy * rho) * 0.25);
                    double UP = (-(.25 * (Se + Sw - Ne - Nw - 2. * vx * vx * vy * rho + vy * (rho - N - S - R)) + vx * ((Ne - Nw - Se + Sw) * .5)));
                    double RIGHT = (-(.25 * (Sw + Nw - Se - Ne - 2. * vy * vy * vx * rho + vx * (rho - R - W - E)) + vy * ((Ne + Sw - Se - Nw) * .5)));
                    double NP = (0.25 * (rho * (1. / 9.) - Ne - Nw - Se - Sw - 8 * P
                            + 2 * (vx * (Ne - Nw + Se - Sw - 4 * RIGHT) + vy * (Ne + Nw - Se - Sw - 4 * UP))
                            + 4 * vx * vy * (-Ne + Nw + Se - Sw + 4 * V)
                            + vx * vx * (-N - Ne - Nw - S - Se - Sw + 2 * NE - 6 * P)
                            + vy * vy * ((-E - Ne - Nw - Se - Sw - W - 2 * NE - 6 * P) + 3 * vx * vx * rho)));

                    grid.ftemp[nodeIndex + LbEQ.NW] = Nw + 2 * P + NP + V - UP + RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.W] = W - P - 2 * NP + NE - 2 * RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.SW] = Sw + 2 * P + NP - V + UP + RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.S] = S - P - 2 * NP - NE - 2 * UP;
                    grid.ftemp[nodeIndex + LbEQ.SE] = Se + 2 * P + NP + V + UP - RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.E] = E - P - 2 * NP + NE + 2 * RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.NE] = Ne + 2 * P + NP - V - UP - RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.N] = N - P - 2 * NP - NE + 2 * UP;
                    grid.ftemp[nodeIndex + LbEQ.ZERO] = R + (4 * (-P + NP));

                } else {
                    //nodal bounce back
                    for (int dir = 1; dir < 9; dir++) {
                        grid.ftemp[nodeIndex + dir] = grid.f[nodeIndex + LbEQ.invdir[dir]];
                    }

                }
            }
        }
    }

    /* ======================================================================================= */
    protected void collisionFCM(double s_nu) {
        int nodeIndex = -1;

        //try {

        for (int i = startX; i < endX; i++) {
            for (int j = 0; j < grid.ny; j++) {

                nodeIndex = (i + j * grid.nx) * 9;

                if (grid.getType(i, j) != GridNodeType.SOLID) {

                    double rho = grid.f[nodeIndex + LbEQ.ZERO]
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
                            - grid.f[nodeIndex + LbEQ.NW]) / rho;


                    double vy = (+grid.f[nodeIndex + LbEQ.N]
                            - grid.f[nodeIndex + LbEQ.S]
                            + grid.f[nodeIndex + LbEQ.NE]
                            + grid.f[nodeIndex + LbEQ.NW]
                            - grid.f[nodeIndex + LbEQ.SE]
                            - grid.f[nodeIndex + LbEQ.SW]) / rho;

                    double R = grid.f[nodeIndex + LbEQ.ZERO];
                    double E = grid.f[nodeIndex + LbEQ.E];
                    double W = grid.f[nodeIndex + LbEQ.W];
                    double N = grid.f[nodeIndex + LbEQ.N];
                    double S = grid.f[nodeIndex + LbEQ.S];
                    double Ne = grid.f[nodeIndex + LbEQ.NE];
                    double Sw = grid.f[nodeIndex + LbEQ.SW];
                    double Nw = grid.f[nodeIndex + LbEQ.NW];
                    double Se = grid.f[nodeIndex + LbEQ.SE];


                    double P = (1. / 12. * (rho * (vx * vx + vy * vy) - E - N - S - W - 2 * (Se + Sw + Ne + Nw - 1. / 3. * rho)));
                    double NE = (s_nu * 0.25 * (N + S - E - W + rho * (vx * vx - vy * vy)));
                    double V = (s_nu * ((Ne + Sw - Nw - Se) - vx * vy * rho) * 0.25);
                    double kxxyy = (E + Ne + Nw + Se + Sw + W - vx * vx * rho + 2 * NE + 6 * P) / rho * (N + Ne + Nw + S + Se + Sw - vy * vy * rho - 2 * NE + 6 * P) / rho;
                    double UP = (-(.25 * (Se + Sw - Ne - Nw - 2. * vx * vx * vy * rho + vy * (rho - N - S - R)) - vy * .5 * (-3. * P - NE) + vx * ((Ne - Nw - Se + Sw) * .5 - 2 * V)));
                    double RIGHT = (-(.25 * (Sw + Nw - Se - Ne - 2. * vy * vy * vx * rho + vx * (rho - R - W - E)) - vx * .5 * (-3. * P + NE) + vy * ((Ne + Sw - Se - Nw) * .5 - 2 * V)));
                    double NP = (0.25 * (rho * (kxxyy) - Ne - Nw - Se - Sw - 8 * P
                            + 2 * (vx * (Ne - Nw + Se - Sw - 4 * RIGHT) + vy * (Ne + Nw - Se - Sw - 4 * UP))
                            + 4 * vx * vy * (-Ne + Nw + Se - Sw + 4 * V)
                            + vx * vx * (-N - Ne - Nw - S - Se - Sw + 2 * NE - 6 * P)
                            + vy * vy * ((-E - Ne - Nw - Se - Sw - W - 2 * NE - 6 * P) + 3 * vx * vx * rho)));

                    grid.ftemp[nodeIndex + LbEQ.NW] = Nw + 2 * P + NP + V - UP + RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.W] = W - P - 2 * NP + NE - 2 * RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.SW] = Sw + 2 * P + NP - V + UP + RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.S] = S - P - 2 * NP - NE - 2 * UP;
                    grid.ftemp[nodeIndex + LbEQ.SE] = Se + 2 * P + NP + V + UP - RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.E] = E - P - 2 * NP + NE + 2 * RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.NE] = Ne + 2 * P + NP - V - UP - RIGHT;
                    grid.ftemp[nodeIndex + LbEQ.N] = N - P - 2 * NP - NE + 2 * UP;
                    grid.ftemp[nodeIndex + LbEQ.ZERO] = R + (4 * (-P + NP));

                } else {
                    //nodal bounce back
                    for (int dir = 1; dir < 9; dir++) {
                        grid.ftemp[nodeIndex + dir] = grid.f[nodeIndex + LbEQ.invdir[dir]];
                    }

                }
            }
        }

//        } catch (Exception ex) {
//            System.out.println(ex);
//            return;
//        }
    }

    protected void addForcing() {

        int nodeindex;

        for (int i = startX; i < endX; i++) {
            for (int j = 0; j < grid.ny; j++) {

                nodeindex = (i + j * grid.nx) * 9;

                for (int dir = 1; dir < 9; dir++) {
                    grid.ftemp[nodeindex + dir] += LbEQ.getForcingForDirection(dir, grid.forcingX1, grid.forcingX2);
                }
            }
        }
    }

    /* ======================================================================================= */
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

            if (myrank > 0) {
                minusx = 0;
            }
            if (myrank < num_of_threads - 1) {
                plusx = 0;
            }

            int offset = (LbEQ.ex[dir] + LbEQ.ey[dir] * grid.nx) * 9;

            for (int i = startX + minusx; i < endX - plusx; i++) {
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

            for (int i = startX; i < endX; i++) {

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
/* ======================================================================================= */
/* ======================================================================================= */
