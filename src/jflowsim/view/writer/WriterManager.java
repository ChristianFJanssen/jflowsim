package jflowsim.view.writer;

import java.util.ArrayList;
import jflowsim.model.numerics.UniformGrid;

public class WriterManager {

    private UniformGrid grid;
    private ArrayList<Writer> writers = new ArrayList<Writer>();

    public WriterManager() {
        this.writers.add(new LineProbe());
    }

    public void activateWriter(int index, int scalar) {
        //System.out.println("activate: "+scalarStyles.get(index).getClass().getSimpleName()+" scalar:"+scalar);
        writers.get(index).setScalarType(scalar);
        writers.get(index).setEnabled(true);
    }

    public void deactivateWriter(int index) {
        //System.out.println("deactivate: "+scalarStyles.get(index).getClass().getSimpleName());
        writers.get(index).setEnabled(false);
    }

    public ArrayList<Writer> getWriters() {
        return writers;
    }

    public void write() {      
        if (grid != null) {
            for (Writer writer : writers) {
                writer.write(grid);
            }
        }
    }

    public void setGrid(UniformGrid _grid) {
        this.grid = _grid;
    }
}
