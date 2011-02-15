///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package irmb.jflowsim.view.writer;
//
//import irmb.jflowsim.model.numerics.lbm.LBMUniformGrid;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
///**
// *
// * @author harwen
// */
//public class PointProbe implements Writer {
//    private String filename;
//    private int run=0;
//    private String outputpath = "C:/Temp/";
//    private File save;
//
//
//    @Override
//    public void write(LBMUniformGrid grid) {
//        filename = outputpath + "PointProbe" +"_"+grid.testcase+"d"+grid.delta+ ".dat";
//        save = new File(filename);
//         int x = grid.nx-1;
//        int y = (int) (grid.ny/4);
//
//
//        try {
//           BufferedWriter out = new BufferedWriter(new FileWriter(save, true));
//            out.append(grid.real_time + "    " + grid.get(x, y).getMomentum()*100 + "\n");
//            out.close();
//            //System.out.println("file: " + filename + " ...done");
//        } catch (IOException e) {
//            System.err.println(e.toString());
//            // JOptionPane.showMessageDialog(null, "Speichern abgebrochen");
//        }
//
//    }
//    }
//
//
//
