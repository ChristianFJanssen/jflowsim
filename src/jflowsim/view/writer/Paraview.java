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
//public class Paraview implements Writer{
//    private String filename;
//    private String outputpath = "C:/Temp/";
//    private File save;
//    int run = 0;
//
//    @Override
//    public void write(LBMUniformGrid grid) {
//        String time = "";
//        DecimalFormat df = new DecimalFormat("0.00");
//
//        time= df.format(grid.real_time);
//        System.out.println("writer paraview");
//        try {
//           BufferedWriter out = new BufferedWriter(new FileWriter(new File(outputpath+grid.testcase.getClass().getSimpleName()+time+".vtk")));
//           double rho,ux,uy,tmp;
//            //System.out.println(outputpath+grid.testcase.getClass().getSimpleName()+time+".vtk");
//
//        out.write( "# vtk DataFile Version 3.0\n");
//	out.write( grid.timestep + "\n");
//	out.write( "ASCII\n");
//	out.write( "DATASET STRUCTURED_POINTS\n");
//	out.write( "DIMENSIONS " + (grid.nx) + " " + (grid.ny) + " 1\n");
//	out.write( "ORIGIN 0.0 0.0 0.0\n");
//	out.write( "SPACING " + grid.delta + " " + grid.delta + " " + grid.delta + "\n");
//
//	out.write( "POINT_DATA " + (grid.nx)*(grid.ny) + "\n");
//
//	out.write( "SCALARS Geo float\n");
//	out.write( "LOOKUP_TABLE default\n");
//
//	for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//
//			tmp = grid.get(i, j).getType();
//                        out.write( tmp + "\n");
//
//		}
//	}
//
//
//	out.write( "SCALARS waterheight float\n");
//	out.write( "LOOKUP_TABLE default\n");
//	for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//
//			if( grid.get(i, j).getType()==LBMGridNode.SOLID||grid.get(i, j).getType()==LBMGridNode.RUNUP ||grid.get(i, j).getType()==LBMGridNode.BOUNDARY){
//                            rho=0.0;
//                        }
//			else {
//                            rho = grid.get(i, j).getMomentum();
//                        }
//			out.write( rho + "\n");
//		}
//	}
//
//	out.write( "SCALARS  Ux float\n");
//	out.write( "LOOKUP_TABLE default\n");
//	for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//
//			if( grid.get(i, j).getType()==LBMGridNode.SOLID||(grid.get(i, j).getType()==LBMGridNode.RUNUP ||grid.get(i, j).getType()==LBMGridNode.BOUNDARY) ){
//                            ux=0.0;
//                        }
//			else{
//				rho = grid.get(i, j).getMomentum();
//				ux = grid.get(i, j).calcPIxFromF();
//				ux/=rho;
//			}
//			out.write(  ux*grid.v_scale + "\n");
//		}
//	}
//
//	out.write( "SCALARS Uy float\n");
//	out.write( "LOOKUP_TABLE default\n");
//	for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//
//			if( grid.get(i, j).getType()==LBMGridNode.SOLID||(grid.get(i, j).getType()==LBMGridNode.RUNUP ||grid.get(i, j).getType()==LBMGridNode.BOUNDARY) ){
//                            uy=0.0;
//                        }else{
//                            rho = grid.get(i, j).getMomentum();
//                            uy = grid.get(i, j).calcPIyFromF();
//                            uy/=rho;
//                        }
//
//
//			out.write(  uy*grid.v_scale + "\n");
//		}
//            }
//
//
//	out.write( "SCALARS bottomelevation float\n");
//	out.write( "LOOKUP_TABLE default\n");
//	for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//			out.write( grid.get(i, j).getBt() + "\n");
//		}
//	}
//
//
//	out.write( "SCALARS waterlevel float\n");
//	out.write( "LOOKUP_TABLE default\n");
//	for(int j=0 ; j< grid.ny ; j++){
//            for(int i=0 ; i< grid.nx ; i++){
//
//			if( grid.get(i, j).getType()==LBMGridNode.SOLID||(grid.get(i, j).getType()==LBMGridNode.RUNUP||grid.get(i, j).getType()==LBMGridNode.BOUNDARY) )rho=0.0;
//			else rho = grid.get(i, j).getMomentum() +grid.get(i, j).getBt() ;
//			out.write( rho + "\n");
//		}
//	}
//        out.close();
//        } catch (IOException e) {
//            System.err.println(e.toString());
//             //JOptionPane.showMessageDialog(null, "Speichern abgebrochen");
//        }
//        System.out.println(outputpath+grid.testcase.getClass().getSimpleName()+time+".vtk");
//
//    }
//
//
//
//
//}
