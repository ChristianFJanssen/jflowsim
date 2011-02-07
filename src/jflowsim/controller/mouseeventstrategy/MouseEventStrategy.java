package jflowsim.controller.mouseeventstrategy;

import jflowsim.model.ModelManager;
import jflowsim.view.MainWindow;
import jflowsim.view.GraphicViewer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;

public abstract class MouseEventStrategy implements MouseListener, MouseMotionListener, MouseWheelListener {

    protected GraphicViewer viewer;
    protected ModelManager modelManager;
    protected MainWindow mainWindow;

    public MouseEventStrategy(GraphicViewer viewer, ModelManager modelManager, MainWindow mainWindow) {
        this.viewer = viewer;
        this.modelManager = modelManager;
        this.mainWindow = mainWindow;
    }

    public void mouseMoved(MouseEvent evt) {
        double x_world = viewer.getTrafo().transformViewToWorldXCoord(evt.getX(), evt.getY(), false);
        double y_world = viewer.getTrafo().transformViewToWorldYCoord(evt.getX(), evt.getY(), false);

        DecimalFormat df = new DecimalFormat("0.0");
        String message = df.format(x_world) + ", " + df.format(y_world);

        mainWindow.updateStatusField(message);
    }

    public void mousePressed(MouseEvent evt) {
    }

    public void mouseReleased(MouseEvent evt) {
    }

    public void mouseDragged(MouseEvent evt) {
    }

    public void mouseWheelMoved(MouseWheelEvent evt) {
    }

    public void mouseClicked(MouseEvent evt) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
