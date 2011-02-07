package jflowsim.model;

import jflowsim.model.geometry2d.utilities.BoundingBox2D;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.Point2D;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class ModelManager extends Observable implements Serializable, Observer {

    private LinkedList<Geometry2D> geometryList;
    private WorldViewTransformator2D trafo;
    public UniformGrid grid = null;
    public Solver solver;

    public ModelManager(WorldViewTransformator2D trafo) {
        this.geometryList = new LinkedList<Geometry2D>();
        this.trafo = trafo;
    }

    public boolean hasGrid() {
        return grid != null;
    }

    public void addGrid(UniformGrid grid) {
        if (this.grid == null) {
            this.grid = grid;
            map2Grid();
        }
    }

    public void map2Grid() {
        
        if (this.grid != null) {

            for (int x = 0; x < grid.nx; x++) {
                for (int y = 0; y < grid.ny; y++) {
                    if( grid.getType(x, y) == GridNodeType.SOLID )
                        grid.setType(x, y, GridNodeType.FLUID);
                }
            }
            // map Geometry to Grid
            for (Geometry2D geo : geometryList) {
                geo.map2Grid(grid);
            }
        }
    }

    public void removeGrid() {
        if (this.grid != null) {            
            this.grid = null;
        }
    }

    public void startSimulation(Solver solver) {
        if (grid != null) {
            this.solver = solver;
            this.solver.startSimulation();
        }
    }

    public void stopSimulation() {
        if (solver != null) {
            solver.deleteObservers();
            solver.interrupt();
            solver = null;
        }
    }

    public ModelManager clone() {
        ModelManager modelManager = new ModelManager(trafo);
        // copy geometry list
        LinkedList<Geometry2D> tmpGeometryList = new LinkedList<Geometry2D>();
        for (Geometry2D geo : geometryList) {
            tmpGeometryList.add(geo);
        }
        modelManager.geometryList = tmpGeometryList;
        return modelManager;
    }

    public BoundingBox2D calculateWorldCoordExtremas() {
        Point2D min = new Point2D(Double.MAX_VALUE, Double.MAX_VALUE);
        Point2D max = new Point2D(-Double.MAX_VALUE, -Double.MAX_VALUE);

        for (Geometry2D geo : geometryList) {
            if (geo.getMinX() < min.getX()) {
                min.setX(geo.getMinX());
            }
            if (geo.getMinY() < min.getY()) {
                min.setY(geo.getMinY());
            }
            if (geo.getMaxX() > max.getX()) {
                max.setX(geo.getMaxX());
            }
            if (geo.getMaxY() > max.getY()) {
                max.setY(geo.getMaxY());
            }
        }

        if(grid !=null){
            if(grid.getMinX() < min.getX()){
                min.setX(grid.getMinX());
            }
            if(grid.getMinY() < min.getY()){
                min.setY(grid.getMinY());
            }

            if(grid.getMaxX() > max.getX()){
                max.setX(grid.getMaxX());
            }
            if(grid.getMaxY() > max.getY()){
                max.setY(grid.getMaxY());
            }
        }

        return new BoundingBox2D(min, max);
    }

    public void add(Geometry2D geo) {
        this.geometryList.add(geo);
        geo.addObserver(this);

        map2Grid();
        super.setChanged();
        super.notifyObservers();
    }

    public void remove(Geometry2D geo) {
        geo.deleteObserver(this);
        geometryList.remove(geo);

        map2Grid();
        super.setChanged();
        super.notifyObservers();
    }

    public void removeAllGeometryObjects() {
        this.geometryList.clear();

        map2Grid();
        super.setChanged();
        super.notifyObservers();
    }

    public LinkedList<Geometry2D> getGeometryList() {
        return geometryList;
    }

    public WorldViewTransformator2D getTrafo() {
        return trafo;
    }

    public void update(Observable o, Object arg) {
        map2Grid();
        super.setChanged();
        super.notifyObservers();
    }
}
