package jflowsim.controller.mouseeventstrategy;

import jflowsim.model.ModelManager;
import jflowsim.controller.geobuilder.Geometry2DBuilder;
import jflowsim.controller.geobuilder.Geometry2DFactory;
import jflowsim.controller.commands.CreateObjectCommand;
import jflowsim.view.MainWindow;
import jflowsim.view.graphics.GraphicObject;
import jflowsim.view.GraphicViewer;
import java.awt.event.MouseEvent;

public class BuildObjectStrategy extends MouseEventStrategy {

    private Geometry2DBuilder builder;
    private String objName;

    public BuildObjectStrategy(String objName, GraphicViewer viewer, ModelManager modelManager, MainWindow mainWindow) {
        super(viewer, modelManager, mainWindow);
        this.objName = objName;
    }

    public void mousePressed(MouseEvent evt) {

        double x_world = viewer.getTrafo().transformViewToWorldXCoord(evt.getX(), evt.getY(), false);
        double y_world = viewer.getTrafo().transformViewToWorldYCoord(evt.getX(), evt.getY(), false);


        switch (evt.getButton()) {
            case 1:
                if (builder == null) {
                    builder = Geometry2DFactory.getInstance().getBuilder(objName);

                    builder.setPoint(x_world, y_world, true);
                    modelManager.add(builder.getGeoObj());

                    GraphicObject graphic = builder.createGraphic2D(builder.getGeoObj());
                    viewer.add(graphic);

                } else {
                    if (!builder.isObjFinished()) {
                        builder.setPoint(x_world, y_world, true);
                    }
                }
                break;
            case 2:
                break;
            case 3:
                if (builder != null) {
                    builder.finishObject();
                }
                break;
        }

        if (builder.isObjFinished()) {
            new CreateObjectCommand(builder.getGeoObj(), viewer, modelManager).execute();
            builder = null;
            viewer.setMouseEventStrategy(new DefaultMouseEventStrategy(viewer, modelManager, mainWindow));
        }
    }

    public void mouseMoved(MouseEvent evt) {
        super.mouseMoved(evt);
        double x = viewer.getTrafo().transformViewToWorldXCoord(evt.getX(), evt.getY(), false);
        double y = viewer.getTrafo().transformViewToWorldYCoord(evt.getX(), evt.getY(), false);

        if (builder != null && !builder.isObjFinished()) {
            builder.setPoint(x, y, false);
        }
    }
}
