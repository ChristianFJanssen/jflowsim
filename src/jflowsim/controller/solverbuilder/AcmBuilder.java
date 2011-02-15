package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.acm.AcmSolver;
import jflowsim.model.numerics.lbm.testcases.acm.SonjasTestCase;
import jflowsim.model.numerics.lbm.testcases.acm.TaylorGreenVortexTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class AcmBuilder extends SolverBuilder {

    public AcmBuilder() {
        testCaseSet.put(TaylorGreenVortexTestCase.class.getSimpleName(), new TaylorGreenVortexTestCase());
        testCaseSet.put(SonjasTestCase.class.getSimpleName(), new SonjasTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new AcmSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCase testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}
