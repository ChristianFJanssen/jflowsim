package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.testcases.TestCaseCreator;
import java.util.ArrayList;
import java.util.TreeMap;

public abstract class SolverBuilder {

    protected TreeMap<String, TestCaseCreator> testCaseSet = new TreeMap<String, TestCaseCreator>();

    public SolverBuilder(){    }
    
    public abstract Solver createSolver(UniformGrid grid);

    public abstract UniformGrid createGrid(String testcase);

    public ArrayList<String> getTestCaseKeySet() {
        return new ArrayList<String>(testCaseSet.keySet());
    }
}
