package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;
import jflowsim.controller.geobuilder.Geometry2DFactory;
import jflowsim.model.ModelManager;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.view.GraphicViewer;
import jflowsim.view.graphics.GraphicObject;

public class RemoveObjectCommand extends Command {

    private Geometry2D geoObj;
    private GraphicViewer viewer;
    private ModelManager modelManager;

    public RemoveObjectCommand(Geometry2D geoObj, GraphicViewer viewer, ModelManager modelManager) {
        this.geoObj = geoObj;
        this.viewer = viewer;
        this.modelManager = modelManager;

        CommandQueue.getInstance().add(this);
    }

    public void execute() {
        for (GraphicObject graphics : viewer.getGraphicList()) {
            if (graphics.getGeometry2D() != null && graphics.getGeometry2D().equals(geoObj)) {
                viewer.remove(graphics);
                break;
            }
        }
        modelManager.remove(geoObj);
    }

    public void undo() {
        GraphicObject graphic = Geometry2DFactory.getInstance().createGraphicObj(geoObj.getObjectType(), geoObj);
        modelManager.add(geoObj);
        viewer.add(graphic);
    }

    public void redo() {
        for (GraphicObject graphics : viewer.getGraphicList()) {
            if (graphics.getGeometry2D().equals(geoObj)) {
                viewer.remove(graphics);
                break;
            }
        }
        modelManager.remove(geoObj);
    }
}
