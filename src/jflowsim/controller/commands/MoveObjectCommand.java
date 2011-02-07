package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;
import jflowsim.model.geometry2d.Geometry2D;

public class MoveObjectCommand extends Command {

    private Geometry2D geoObj;
    private double x0,  y0;
    private double x1,  y1;
    private double x_start,  y_start;

    public MoveObjectCommand(Geometry2D geoObj, double x_world, double y_world) {
        this.x_start = x0 = x_world;
        this.y_start = y0 = y_world;
        this.geoObj = geoObj;

        CommandQueue.getInstance().add(this);
    }

    public void execute() {

        double deltaX = x1 - x0;
        double deltaY = y1 - y0;

        double x = geoObj.getCenterX();
        double y = geoObj.getCenterY();

        x += deltaX;
        y += deltaY;

        geoObj.moveObject(x, y);

        x0 = x1;
        y0 = y1;
    }

    public void setNewPoint(double x1, double y1) {
        this.x1 = x1;
        this.y1 = y1;
    }

    public void undo() {

        double deltaX = (x_start - x1);
        double deltaY = (y_start - y1);

        double x = geoObj.getCenterX();
        double y = geoObj.getCenterY();

        x += deltaX;
        y += deltaY;

        geoObj.moveObject(x, y);

        this.x0 = x_start;
        this.y0 = y_start;
    }

    public void redo() {

        double deltaX = (x1 - x_start);
        double deltaY = (y1 - y_start);

        double x = geoObj.getCenterX();
        double y = geoObj.getCenterY();

        x += deltaX;
        y += deltaY;

        geoObj.moveObject(x, y);

        this.x0 = x1;
        this.y0 = y1;
    }
}
