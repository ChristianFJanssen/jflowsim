package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.temperature.LBMTemperatureGrid;
import jflowsim.model.numerics.lbm.temperature.LBMTemperatureSolver;
import jflowsim.model.numerics.lbm.testcases.temperature.RayleighBenardTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCase;


public class LBMTemperatureBuilder extends SolverBuilder{

    public LBMTemperatureBuilder() {
        testCaseSet.put(RayleighBenardTestCase.class.getSimpleName(), new RayleighBenardTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new LBMTemperatureSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCase testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}
