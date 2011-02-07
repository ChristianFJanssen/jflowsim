package jflowsim.controller.commands;

import jflowsim.controller.geobuilder.Geometry2DFactory;
import jflowsim.model.ModelManager;
import jflowsim.view.GraphicViewer;
import jflowsim.view.graphics.GraphicObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class OpenCommand extends Command {

    private ModelManager modelManager;
    private GraphicViewer viewer;
    private File open;

    public OpenCommand(GraphicViewer viewer, ModelManager modelManager) {
        this.modelManager = modelManager;
        this.viewer = viewer;
    }

    public void execute() {

        JFileChooser fc = new JFileChooser();

        fc.setFileFilter(new FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".dat");
            }

            public String getDescription() {
                return "FlowSim (*.dat)";
            }
        });
        int state = fc.showOpenDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            open = fc.getSelectedFile();
        }

        try {
            FileInputStream file = new FileInputStream(open);
            ObjectInputStream is = new ObjectInputStream(file);
            ModelManager moMa = (ModelManager) is.readObject();
            for (int i = 0; i < moMa.getGeometryList().size(); i++) {
                GraphicObject graphic = Geometry2DFactory.getInstance().createGraphicObj(moMa.getGeometryList().get(i).getObjectType(), moMa.getGeometryList().get(i));
                viewer.add(graphic);
                modelManager.add(moMa.getGeometryList().get(i));
            }
            is.close();
        } catch (ClassNotFoundException e) {
            System.err.println(e.toString());
            JOptionPane.showMessageDialog(null, "Fehler");
        } catch (IOException e) {
            System.err.println(e.toString());
            JOptionPane.showMessageDialog(null, "Fehler");
        }
    }

    public void undo() {
    }

    public void redo() {
    }
}
