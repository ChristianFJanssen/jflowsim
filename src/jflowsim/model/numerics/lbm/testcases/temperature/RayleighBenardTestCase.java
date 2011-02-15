package jflowsim.model.numerics.lbm.testcases.temperature;

import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.temperature.LBMTemperatureGrid;
import jflowsim.model.numerics.lbm.testcases.TestCase;
import jflowsim.model.numerics.utilities.GridNodeType;

public class RayleighBenardTestCase extends TestCase {

    public UniformGrid getGrid() {

        LBMTemperatureGrid grid = new LBMTemperatureGrid(2.0 /* length */, 1.0 /* width */, 0.01 /* dx */);

        grid.testcase = this.getClass().getSimpleName();

        grid.periodicX = true;
        grid.periodicY = false;

        double Pr = 1.0;
        double Ra = 2000000.; // Rayleigh number

        double gr = 0.001;  // Gravity

        grid.bouyancyX = 0.0;
        grid.bouyancyY = gr;

        grid.Thot = 1; // Heating on bottom wall
        grid.Tcold = 0; // Cooling on top wall
        grid.T0 = (grid.Thot + grid.Tcold) / 2;

        grid.dt = Math.sqrt(grid.dx * gr); //war vorher "*gr"...was is da los?

        // nu: kinematic viscosity in lattice units
        grid.nue_lbm = Math.sqrt(Pr / Ra) * grid.dt / (grid.dx * grid.dx);

        // k: thermal diffusivity
        grid.k = Math.sqrt(1. / (Pr * Ra)) * grid.dt / (grid.dx * grid.dx);

        System.out.println("nue_lbm: " + grid.nue_lbm);
        System.out.println("alpha:   " + grid.k);
        System.out.println("Grid: nx:" + grid.nx + " ny:" + grid.ny + " - " + grid.nx * grid.ny);


        for (int i = 0; i < grid.nx * grid.ny; i++) {
            grid.type[i] = GridNodeType.FLUID;
        }

        // Boundary conditions
        for (int x = 0; x < grid.nx; x++) {
            grid.type[x] = GridNodeType.BOUNDARY;
            grid.type[x + (grid.ny - 1) * grid.nx] = GridNodeType.BOUNDARY;
        }
//        for (int y = 0; y < grid.ny; y++) {
//
//            grid.type[0 + y * grid.nx] = GridNodeType.BOUNDARY;
//            grid.type[grid.nx - 1 + y * grid.nx] = GridNodeType.BOUNDARY;
//        }


        grid.dv = grid.viscosity / grid.nue_lbm * grid.nx / grid.getLength();

        double[] feq = new double[9];
        double[] geq = new double[5];

        for (int i = 0; i < grid.nx; i++) {

            LbEQ.getBGKEquilibrium(1.0, 0.0, 0.0, feq);

            for (int j = 0; j < grid.ny; j++) {


                if (j == 0) {
                    LbEQ.getBGKEquilibriumTemperature(grid.Thot, 0.0, 0.0, geq);
                } else if (j == 1) {
                    // random values at the bottom
                    double randomTempValue = grid.Tcold + (grid.Thot - grid.Tcold) * Math.random();
                    LbEQ.getBGKEquilibriumTemperature(randomTempValue, 0.0, 0.0, geq);

                } else {
                    LbEQ.getBGKEquilibriumTemperature(grid.Tcold, 0.0, 0.0, geq);
                }

                for (int dir = 0; dir < 9; dir++) {
                    grid.f[(i + j * grid.nx) * 9 + dir] = feq[dir];
                    grid.ftemp[(i + j * grid.nx) * 9 + dir] = feq[dir];
                }

                for (int dir = 0; dir < 5; dir++) {
                    grid.g[(i + j * grid.nx) * 5 + dir] = geq[dir];
                    grid.gtemp[(i + j * grid.nx) * 5 + dir] = geq[dir];
                }
            }
        }
        return grid;
    }
}
