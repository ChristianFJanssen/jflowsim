package jflowsim.controller.mouseeventstrategy;

import jflowsim.controller.commands.MoveObjectCommand;
import jflowsim.controller.commands.RemoveObjectCommand;
import jflowsim.controller.commands.MoveViewWindowCommand;
import jflowsim.controller.commands.ZoomInCommand;
import jflowsim.controller.commands.ZoomOutCommand;
import jflowsim.model.ModelManager;
import jflowsim.view.MainWindow;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.GraphicViewer;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class DefaultMouseEventStrategy extends MouseEventStrategy {

    private MoveObjectCommand moveObjCommand;
    private MoveViewWindowCommand moveViewCommand;

    public DefaultMouseEventStrategy(GraphicViewer viewer, ModelManager modelManager, MainWindow mainWindow) {
        super(viewer, modelManager, mainWindow);
    }

    public void mousePressed(MouseEvent evt) {

        GraphicObject graphic = viewer.getObjectForViewCoordinates(evt.getX(), evt.getY());

        double x_world = viewer.getTrafo().transformViewToWorldXCoord(evt.getX(), evt.getY(), false);
        double y_world = viewer.getTrafo().transformViewToWorldYCoord(evt.getX(), evt.getY(), false);

        switch (evt.getButton()) {
            case 1:
                if (graphic != null) {
                    moveObjCommand = new MoveObjectCommand(graphic.getGeometry2D(), x_world, y_world);
                }
                break;
            case 2:
                moveViewCommand = new MoveViewWindowCommand(viewer, evt.getX(), evt.getY());
                break;
            case 3:
                if (graphic != null) {
                    new RemoveObjectCommand(graphic.getGeometry2D(), viewer, modelManager).execute();
                }
                break;
        }
    }

    public void mouseReleased(MouseEvent evt) {
        this.viewer.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        moveObjCommand = null;
        moveViewCommand = null;
    }

    public void mouseDragged(MouseEvent evt) {
        super.mouseMoved(evt);

        if (moveObjCommand != null) {
            double x = viewer.getTrafo().transformViewToWorldXCoord(evt.getX(), evt.getY(), false);
            double y = viewer.getTrafo().transformViewToWorldYCoord(evt.getX(), evt.getY(), false);

            moveObjCommand.setNewPoint(x, y);
            moveObjCommand.execute();
        } else if (moveViewCommand != null) {
            moveViewCommand.setXY(evt.getX(), evt.getY());
            moveViewCommand.execute();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent evt) {
        if (evt.getWheelRotation() < 0) {
            new ZoomInCommand(viewer).execute();
        } else {
            new ZoomOutCommand(viewer).execute();
        }
    }
}
