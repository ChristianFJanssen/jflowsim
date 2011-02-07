package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.testcases.TestCase;
import java.util.ArrayList;
import java.util.TreeMap;

public abstract class SolverBuilder {

    protected TreeMap<String, TestCase> testCaseSet = new TreeMap<String, TestCase>();

    public SolverBuilder(){    }
    
    public abstract Solver createSolver(UniformGrid grid);

    public abstract UniformGrid createGrid(String testcase);

    public ArrayList<String> getTestCaseKeySet() {
        return new ArrayList<String>(testCaseSet.keySet());
    }
}
