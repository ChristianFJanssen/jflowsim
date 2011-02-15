///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package irmb.jflowsim.view.writer;
//
//import irmb.jflowsim.model.numerics.lbm.LBMGridNode;
//import irmb.jflowsim.model.numerics.lbm.LBMUniformGrid;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.DecimalFormat;
//
///**
// *
// * @author harwen
// */
//public class x3d implements Writer{
//    private String filename;
//    private String outputpath = "C:/Temp/";
//    private File save;
//    int run = 0;
//
//    @Override
//    public void write(LBMUniformGrid grid) {
//        String time = "";
//        DecimalFormat df = new DecimalFormat("##.####");
//
//        time= df.format(grid.real_time);
////        System.out.println("writer paraview");
//        try {
//           BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputpath+grid.testcase.getClass().getSimpleName()+time+".x3d")));
//           double rho,ux,uy,tmp;
//            //System.out.println(outputpath+grid.testcase.getClass().getSimpleName()+time+".vtk");
//
//
//	// General part
//
//   //Root Element
//   out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//   out.write("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.1//EN\"   \"http://www.web3d.org/specifications/x3d-3.1.dtd\">\n");
//   out.write("<X3D profile='Interchange' version='3.1' xmlns:xsd='http://www.w3.org/2001/XMLSchema-instance' xsd:noNamespaceSchemaLocation=' http://www.web3d.org/specifications/x3d-3.1.xsd '>\n");
//
//   //Head
//   out.write("<head>\n");
//   out.write("<meta content='Simple X3D Writer for blender'/>\n");
//   out.write("</head>\n\n");
//
//	//Scene, Shape beginn
//   out.write("<Scene>\n");
//   out.write("<Shape>\n");
//
//	//IndexedFaceSet => Polylinien der Dreiecke
//	out.write("<IndexedFaceSet  coordIndex=\"\n");
//
//        for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//
//
//
//
//			int index = grid.nx*j+i;
//
//			//Dreieck 1 \_
//			out.write("      "+index+" "+(index+1)+" "+(index+grid.nx)+" -1\n");
//			// Dreieck 2 _\_
//			out.write("      "+(index+1)+" "+(index+grid.nx+1)+" "+(index+grid.nx)+" -1\n");
//		}
//            }
//	out.write("\">\n");
//    //Coordinates
//	out.write("<Coordinate  point=\"\n");
//	// Loop ueber Knoten
//	for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//			int h= -999;
//
//                        if( grid.get(i, j).getType()==LBMGridNode.SOLID||grid.get(i, j).getType()==LBMGridNode.BOUNDARY ){
//                            rho=0.0;
//                        }
//			else rho = grid.get(i, j).getMomentum() +grid.get(i, j).getBt() ;
//			out.write("      "+i*grid.delta+", "+j*grid.delta+", "+rho+", \n");
//
//		}
//	}
//        out.write("\"/>\n");
//	//Footer
//	out.write("</IndexedFaceSet>\n");
//	out.write("</Shape>\n");
//	out.write("</Scene>\n");
//	out.write("</X3D>\n");
//
//
//        out.close();
//        } catch (IOException e) {
//            System.err.println(e.toString());
//             //JOptionPane.showMessageDialog(null, "Speichern abgebrochen");
//        }
//        System.out.println(outputpath+grid.testcase.getClass().getSimpleName()+time+".x3d");
//
//    }
//
//
//
//
//}
