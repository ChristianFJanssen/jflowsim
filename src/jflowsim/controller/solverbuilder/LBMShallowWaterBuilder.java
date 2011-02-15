package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.shallowwater.LBMShallowWaterSolver;
import jflowsim.model.numerics.lbm.testcases.shallowwater.CylinderTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCase;
import jflowsim.model.numerics.lbm.testcases.shallowwater.CargoDeckTestCase;

public class LBMShallowWaterBuilder extends SolverBuilder {

    public LBMShallowWaterBuilder() {
        testCaseSet.put(CylinderTestCase.class.getSimpleName(), new CylinderTestCase());
        testCaseSet.put(CargoDeckTestCase.class.getSimpleName(), new CargoDeckTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new LBMShallowWaterSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCase testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}
