package jflowsim.controller.commands;

import jflowsim.model.ModelManager;
import jflowsim.model.geometry2d.utilities.BoundingBox2D;
import jflowsim.view.GraphicViewer;

public class ZoomAllCommand extends Command {

    private ModelManager modelManager;
    private GraphicViewer viewer;

    public ZoomAllCommand(GraphicViewer viewer, ModelManager modelManager) {
        this.modelManager = modelManager;
        this.viewer = viewer;
    }

    public void execute() {
        viewer.getTrafo().setViewWindow(0, 0, viewer.getWidth(), viewer.getHeight());
        BoundingBox2D bounds = modelManager.calculateWorldCoordExtremas();
        viewer.getTrafo().setWorldWindow(bounds.getMin().getX(), bounds.getMin().getY(), bounds.getMax().getX(), bounds.getMax().getY());
        viewer.repaint();
    }

    public void undo() {
    }

    public void redo() {
    }
}
