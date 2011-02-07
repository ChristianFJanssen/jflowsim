package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;
import jflowsim.controller.geobuilder.Geometry2DFactory;
import jflowsim.model.ModelManager;
import jflowsim.view.GraphicViewer;

public class ClearAllCommand extends Command {

    private ModelManager modelManager;
    private GraphicViewer viewer;

    private ModelManager savedModel;

    public ClearAllCommand(GraphicViewer viewer, ModelManager modelManager) {
        this.viewer = viewer;
        this.modelManager = modelManager;
        CommandQueue.getInstance().add(this);
    }

    public void execute() {
        // Speichern des Modells
        this.savedModel = modelManager.clone();
        // Entfernen aller view und geometry objekte
        viewer.removeAllGraphicObjects();
        modelManager.removeAllGeometryObjects();
    }

    public void undo() {
        for (int i = 0; i < savedModel.getGeometryList().size(); i++) {
            modelManager.add(savedModel.getGeometryList().get(i));
            viewer.add( Geometry2DFactory.getInstance().createGraphicObj(savedModel.getGeometryList().get(i).getObjectType(), savedModel.getGeometryList().get(i))  );
        }
    }

    public void redo() {
        // Speichern des Modells
        this.savedModel = modelManager.clone();
        // Entfernen aller view und geometry objekte
        viewer.removeAllGraphicObjects();
        modelManager.removeAllGeometryObjects();
    }
}
