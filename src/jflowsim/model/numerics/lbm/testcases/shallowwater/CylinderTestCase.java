package jflowsim.model.numerics.lbm.testcases.shallowwater;

import jflowsim.model.numerics.BoundaryCondition;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.lbm.LBMBounceForwardBC;
import jflowsim.model.numerics.lbm.shallowwater.LBMGeneralSWBC;
import jflowsim.model.numerics.lbm.shallowwater.LBMHeightSWBC;
import jflowsim.model.numerics.lbm.shallowwater.LBMShallowWaterGrid;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class CylinderTestCase extends TestCase {

    public UniformGrid getGrid() {

        LBMShallowWaterGrid grid = new LBMShallowWaterGrid(4.0 /* length */, 4.0 /* width */, 0.04 /* dx */);

        grid.testcase = this.getClass().getSimpleName();

        double h0 = 0.185;
        double Q = 0.248; //m^3 /s
        double flux = 0.248 / 2.0;
        double initialVelo = flux / h0;

        double targetMacVelo = 10*initialVelo;

        double targetTimeStep = grid.dx / targetMacVelo;

        double radius = 0.11;

        // use Reynolds number to determine flow viscosity
        double Re = 10;

        double nue = initialVelo*2*radius / Re;

        grid.setGravity(0.0, 0.0 /* m/s^2 */);
        grid.setViscosity(nue /* m^2/s */);
        //grid.setTimeStep(0.0001 /* s */);
        grid.setTimeStep(targetTimeStep);

        grid.updateParameters();

        double initialVeloLB = initialVelo / grid.dv;

        // Output the parameters and check the stability range
        System.out.println("V_scale = " + grid.dv);
        System.out.println("Re (real) = " + initialVelo*2*radius/nue);
        System.out.println("Re (LBM)  = " + initialVeloLB*(2*radius/grid.dx)/grid.nue_lbm);

        this.initCircle(grid, 2.0, 2.0, radius);

        //grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.EAST));
        //grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.WEST));

        //grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.NORTH));
        //grid.addBC(new LBMNoSlipBC(grid, BoundaryCondition.SOUTH));   

        System.out.println("v / sqrt(g*h) = " + initialVeloLB / Math.sqrt(grid.gravity * h0));
        System.out.println("Celerity = " + grid.gravity * h0 / (grid.dv * grid.dv));
        System.out.println("Nue real = " + grid.dv * grid.dv * grid.dt * grid.nue_lbm);
        System.out.println("initialVelo = " + initialVeloLB);
        
        // INFLOW BOUNDARY
        //grid.addBC(new LBMHeightSWBC(grid, BoundaryCondition.WEST, h0));
        grid.addBC(new LBMGeneralSWBC(grid, BoundaryCondition.WEST, h0 * initialVeloLB * grid.dv, false));
        //grid.addBC(new LBMEquilibriumBC(grid, BoundaryCondition.WEST, h0, initialVeloLB, 0.0));
        //grid.addBC(new LBMEquilibriumExtrapolBC(grid, BoundaryCondition.WEST, h0*initialVeloLB));

        // OUTFLOW BOUNDARY
        //grid.addBC(new LBMGeneralSWBC(grid, BoundaryCondition.EAST, 1.1*h0, true));
        grid.addBC(new LBMHeightSWBC(grid, BoundaryCondition.EAST, h0));

        //NORTH and SOUTH
        grid.addBC(new LBMBounceForwardBC(grid, BoundaryCondition.NORTH));
        grid.addBC(new LBMBounceForwardBC(grid, BoundaryCondition.SOUTH));
        

        //grid.periodicX = true;
        //grid.periodicY = true;

        // Initial conditions
        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {
                grid.init(i,j,h0,initialVelo,0.0);
            }
        }

        return grid;

    }
}
