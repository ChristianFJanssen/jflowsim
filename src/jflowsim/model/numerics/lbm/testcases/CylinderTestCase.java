package jflowsim.model.numerics.lbm.testcases;

import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LBMNoSlipBC;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.shallowwater.LBMHeightSWBC;
import jflowsim.model.numerics.lbm.shallowwater.LBMShallowWaterGrid;

public class CylinderTestCase extends TestCase {

    public UniformGrid getGrid() {

        LBMShallowWaterGrid grid = new LBMShallowWaterGrid(4.0 /* length */, 0.5 /* width */, 0.005 /* dx */);

        grid.testcase = this.getClass().getSimpleName();

        grid.setGravity(0.0, 0.0 /* m/s^2 */);
        grid.setViscosity(0.01 /* m^2/s */);
        grid.setTimeStep(0.00005 /* s */);

        grid.updateLBParameters();

        // Output the parameters and check the stability range
        System.out.println("V_scale = " + grid.v_scale);


        this.initFluid(grid);

       grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.EAST));
       grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.WEST));


        double h0 = 1.0;
        double initialVelo = 0.0;

        System.out.println("v / sqrt(g*h) = " + initialVelo / Math.sqrt(grid.gravity*h0));
        System.out.println("Celerity = " + grid.gravity*h0 /(grid.v_scale*grid.v_scale) );
        System.out.println("Nue real = " + grid.v_scale*grid.v_scale*grid.dt*grid.nue_lbm );    
        System.out.println("initialVelo = " + initialVelo);

        
        //grid.addBC(new LBMGeneralSWBC(grid, BoundaryCondition.EAST, h0, true));
        //grid.addBC(new LBMGeneralSWBC(grid, BoundaryCondition.WEST, 1.1*h0, true));

        //grid.addBC(new LBMHeightSWBC(grid, BoundaryCondition.EAST, h0));
        //grid.addBC(new LBMHeightSWBC(grid, BoundaryCondition.WEST, 1.1*h0));

        //grid.periodicX = true;
        grid.periodicY = true;

        // Initial conditions
        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                double[] feq = new double[9];

                if(i<0.5*grid.nx)
                    LbEQ.getBGKEquilibriumShallowWater(1.5*h0, initialVelo / grid.v_scale, 0.0, feq, grid.v_scale, grid.gravity);
                else
                    LbEQ.getBGKEquilibriumShallowWater(h0, initialVelo / grid.v_scale, 0.0, feq, grid.v_scale, grid.gravity);

                //LbEQ.getBGKEquilibriumShallowWater(h0, initialVelo / grid.v_scale, 0.0, feq, grid.v_scale, grid.gravity);

                for (int dir = 0; dir < 9; dir++) {
                    grid.f[(i + j * grid.nx) * 9 + dir] = feq[dir];
                    grid.ftemp[(i + j * grid.nx) * 9 + dir] = feq[dir];
                }
            }
        }

        return grid;

    }
}
