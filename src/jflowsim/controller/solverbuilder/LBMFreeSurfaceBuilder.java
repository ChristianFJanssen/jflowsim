package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.freesurface.LBMFreeSurfaceSolver;
import jflowsim.model.numerics.lbm.testcases.freesurface.BreakingDamTestCase;
import jflowsim.model.numerics.lbm.testcases.freesurface.FallingDropTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class LBMFreeSurfaceBuilder extends SolverBuilder {

    public LBMFreeSurfaceBuilder() {
        testCaseSet.put(BreakingDamTestCase.class.getSimpleName(), new BreakingDamTestCase());
        testCaseSet.put(FallingDropTestCase.class.getSimpleName(), new FallingDropTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new LBMFreeSurfaceSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCase testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}
