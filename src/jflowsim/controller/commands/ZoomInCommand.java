package jflowsim.controller.commands;

import jflowsim.view.GraphicViewer;

public class ZoomInCommand extends Command {    
    
    private GraphicViewer viewer;

    private double zoomFactor = 0.05;


    public ZoomInCommand(GraphicViewer viewer) {        
        this.viewer = viewer;
    }

    public void execute() {
        viewer.getTrafo().zoomWorldWindow(-zoomFactor);
        viewer.repaint();
    }

    public void undo() {
    }

    public void redo() {
    }
}
