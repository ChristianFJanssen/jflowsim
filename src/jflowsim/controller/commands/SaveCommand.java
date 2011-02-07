package jflowsim.controller.commands;

import jflowsim.controller.CommandQueue;
import jflowsim.model.ModelManager;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.view.GraphicViewer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class SaveCommand extends Command {

    private ModelManager modelManager;
    private GraphicViewer viewer;
    private File save;

    public SaveCommand(GraphicViewer viewer, ModelManager modelManager) {
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
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        int state = fc.showSaveDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            save = fc.getSelectedFile();
            String path = save.getPath();
            if (!path.toLowerCase().endsWith(".dat")) {
                path = path + ".dat";
                this.save = new File(path);
            }
        }
        try {
            FileOutputStream file = new FileOutputStream(save);
            ObjectOutputStream os = new ObjectOutputStream(file);
            os.writeObject(this.modelManager);
            os.close();
            JOptionPane.showMessageDialog(null, "Gespeichert");
        } catch (IOException e) {
            System.err.println(e.toString());
            JOptionPane.showMessageDialog(null, "Speichern abgebrochen");
        }
    }

    public void undo() {
    }

    public void redo() {
    }
}
