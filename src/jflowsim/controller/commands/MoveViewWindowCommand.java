package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;
import jflowsim.view.GraphicViewer;
import java.awt.Cursor;


public class MoveViewWindowCommand extends Command {

    private double x1,  y1;
    private double x0,  y0;
    private double x_start,  y_start;
    
    private GraphicViewer viewer;

    public MoveViewWindowCommand(GraphicViewer viewer, double x, double y) {
        this.x1 = this.x0 = this.x_start = x;
        this.y1 = this.y0 = this.y_start = y;
        
        this.viewer = viewer;
        this.viewer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        CommandQueue.getInstance().add(this);
    }

    public void execute() {

        double deltaX = (x1 - x0);
        double deltaY = (y0 - y1);//(y1 - y0);geändert wegen ZoomAll

        this.viewer.getTrafo().moveViewWindow(deltaX, deltaY);
        this.viewer.repaint();

        this.x0 = x1;
        this.y0 = y1;
    }

    public void setXY(double x1, double y1) {
        this.x1 = x1;
        this.y1 = y1;
    }

    public void undo() {
    }

    public void redo() {
    }
}
