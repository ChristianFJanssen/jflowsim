package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.acm.AcmSolver;
import jflowsim.model.numerics.lbm.freesurface.LBMFreeSurfaceSolver;
import jflowsim.model.numerics.lbm.navierstokes.LBMNavierStokesSolver;
import jflowsim.model.numerics.lbm.temperature.LBMTemperatureSolver;
import java.util.ArrayList;
import java.util.TreeMap;
import jflowsim.model.numerics.lbm.shallowwater.LBMShallowWaterSolver;

public class SolverFactory {

    private TreeMap<String, SolverBuilder> builderSet = new TreeMap<String, SolverBuilder>();
    private static SolverFactory instance;

    private SolverFactory() {
        builderSet.put(LBMNavierStokesSolver.class.getSimpleName(), new LBMNavierStokesBuilder());
        //builderSet.put(LBMFreeSurfaceSolver.class.getSimpleName(), new LBMFreeSurfaceBuilder());
        builderSet.put(LBMTemperatureSolver.class.getSimpleName(), new LBMTemperatureBuilder());
        builderSet.put(LBMShallowWaterSolver.class.getSimpleName(), new LBMShallowWaterBuilder());
        //builderSet.put(AcmSolver.class.getSimpleName(), new AcmBuilder());
    }

    public static SolverFactory getInstance() {
        if (instance == null) {
            instance = new SolverFactory();
        }
        return instance;
    }

    public SolverBuilder getBuilder(String name) {
        return builderSet.get(name);
    }

    public ArrayList<String> getKeySet() {
        return new ArrayList<String>(builderSet.keySet());
    }
}
