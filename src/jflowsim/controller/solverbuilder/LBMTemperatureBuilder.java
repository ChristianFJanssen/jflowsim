package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.temperature.LBMTemperatureGrid;
import jflowsim.model.numerics.lbm.temperature.LBMTemperatureSolver;
import jflowsim.model.numerics.lbm.testcases.RayleighBenardTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCaseCreator;


public class LBMTemperatureBuilder extends SolverBuilder{

    public LBMTemperatureBuilder() {
        testCaseSet.put(RayleighBenardTestCase.class.getSimpleName(), new RayleighBenardTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new LBMTemperatureSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCaseCreator testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}
