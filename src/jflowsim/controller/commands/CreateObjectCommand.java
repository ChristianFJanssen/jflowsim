package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;
import jflowsim.model.ModelManager;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.controller.geobuilder.Geometry2DFactory;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.GraphicViewer;

public class CreateObjectCommand extends Command {

    private Geometry2D new_geo;
    private String name;
    private ModelManager modelManager;
    private GraphicViewer viewer;

    public CreateObjectCommand(String name, GraphicViewer viewer, ModelManager modelManager) {
        this.name = name;
        this.modelManager = modelManager;
        this.viewer = viewer;

        CommandQueue.getInstance().add(this);
    }

    public CreateObjectCommand(Geometry2D geo, GraphicViewer viewer, ModelManager modelManager) {
        this.new_geo = geo;
        this.modelManager = modelManager;
        this.viewer = viewer;

        CommandQueue.getInstance().add(this);
    }

    public void execute() {
        if (new_geo == null) {
            new_geo = Geometry2DFactory.getInstance().createGeoObj(name);
            GraphicObject graphic = Geometry2DFactory.getInstance().createGraphicObj(name, new_geo);
            modelManager.add(new_geo);
            viewer.add(graphic);
        }
    }

    public void undo() {
        for (GraphicObject graphics : viewer.getGraphicList()) {
            if (graphics.getGeometry2D().equals(new_geo)) {
                viewer.remove(graphics);
                break;
            }
        }
        modelManager.remove(new_geo);
    }

    public void redo() {
        GraphicObject graphic = Geometry2DFactory.getInstance().createGraphicObj(new_geo.getObjectType(), new_geo);
        modelManager.add(new_geo);
        viewer.add(graphic);
    }
}
