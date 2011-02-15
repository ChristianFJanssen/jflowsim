package jflowsim.view.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.utilities.Scalar;

public class LineProbe extends Writer {

    private String filename;
    private String outputpath = "C:/Temp/";
    private File save;
    private DecimalFormat df = new DecimalFormat("0.000");
    DecimalFormat dfTime = new DecimalFormat("0");

    public String[] getScalars() {
        Field[] fields = Scalar.class.getDeclaredFields();
        String[] str = new String[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            str[i] = fields[i].getName();
        }

        return str;
    }

    public void write(UniformGrid grid) {
        if (enabled) {

            //System.out.println("LineProbeWriter::WRITE");
            int row = (int) grid.ny / 2;

            //String time = dfTime.format(grid.real_time);
            String time = dfTime.format(grid.timestep);

            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputpath + "LineProbe_" + grid.testcase.getClass().getSimpleName() + "_" + time + ".dat")));

                //out.write(String.valueOf(grid.real_time) + " " + "\n");

                //water level
                for (int i = 0; i < grid.nx; i++) {

                    if (grid.getType(i, row) == GridNodeType.FLUID) {

                        double scalar = grid.getScalar(i, row, scalar_type);

                        out.append(String.valueOf(i * grid.dx) + "    " + scalar + "\n");
                    }
                }

                out.close();
                //System.out.println("file: " + filename + " ...done");
            } catch (IOException e) {
                System.err.println(e.toString());
                // JOptionPane.showMessageDialog(null, "Speichern abgebrochen");
            }
        }
    }
}
