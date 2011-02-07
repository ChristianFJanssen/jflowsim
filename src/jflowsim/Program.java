package jflowsim;

import jflowsim.controller.mouseeventstrategy.DefaultMouseEventStrategy;
import jflowsim.model.ModelManager;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.view.GraphicViewer;
import jflowsim.view.MainWindow;
import java.io.FileNotFoundException;

public class Program {

    public static void main(String args[]) throws FileNotFoundException {

        WorldViewTransformator2D trafo = new WorldViewTransformator2D();
        trafo.setWorldWindow(0.0, 0.0, +1.0, +0.5);

        ModelManager modelManager = new ModelManager(trafo);

        GraphicViewer viewer = new GraphicViewer(trafo);
        modelManager.addObserver(viewer);

        MainWindow mainWindow = new MainWindow(viewer, modelManager);
        mainWindow.setVisible(true);

        trafo.setViewWindow(0, 0, viewer.getWidth(), viewer.getHeight());
        viewer.setMouseEventStrategy(new DefaultMouseEventStrategy(viewer, modelManager, mainWindow));
    }
}
