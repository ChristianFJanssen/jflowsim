package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesSolver;
import jflowsim.model.numerics.lbm.testcases.ChannelFlowTestCase;
import jflowsim.model.numerics.lbm.testcases.CouetteFlowTestCase;
import jflowsim.model.numerics.lbm.testcases.DrivenCavityTestCase;
import jflowsim.model.numerics.lbm.testcases.PoiseuilleTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class LBMNavierStokesBuilder extends SolverBuilder {

    public LBMNavierStokesBuilder() {
        testCaseSet.put(ChannelFlowTestCase.class.getSimpleName(), new ChannelFlowTestCase());
        testCaseSet.put(PoiseuilleTestCase.class.getSimpleName(), new PoiseuilleTestCase());
        testCaseSet.put(DrivenCavityTestCase.class.getSimpleName(), new DrivenCavityTestCase());
        testCaseSet.put(CouetteFlowTestCase.class.getSimpleName(), new CouetteFlowTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new LBMNavierStokesSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCase testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}
