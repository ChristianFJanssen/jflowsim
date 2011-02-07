package jflowsim.model.numerics.acm;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.utilities.GridNodeType;

public class AcmSolver extends Solver {

    private AcmGrid grid;

    public AcmSolver(UniformGrid grid) {
        this.grid = (AcmGrid) grid;
    }

    public void interrupt(){
        
    }

    public void run() {

        long timer = System.currentTimeMillis();
        long counter = 0;
        while (true) {
            grid.timestep++;

            this.checkerboard_cure(0);
            eulerForward(0,1,grid.dt);
            eulerBackward(0,1,grid.dt);
            applyBCs(0,1);
            calc_turbvisc(0);

            grid.timestep++;

            this.checkerboard_cure(1);
            eulerForward(1, 0, grid.dt);
            eulerBackward(1, 0, grid.dt);
            calc_turbvisc(1);

            applyBCs(1,0);
//            try {
//                Thread.currentThread().sleep(10000);
//            }
//            catch (InterruptedException ie) {}

            counter += (grid.nx * grid.ny);

            if (grid.timestep % grid.updateInterval == 0) {
                grid.real_time = grid.timestep * grid.dt;

                grid.mnups = counter / ((System.currentTimeMillis() - timer)) * 1000;

                super.setChanged();
                super.notifyObservers();
            }

            // check if thread is interrupted
            if (Thread.interrupted()) {
                break;
            }
        }
        System.out.println("stop LBM Thread !!!");
    }


    private void eulerForward(int in, int out, double step) {
        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                AcmGridNode node = grid.get(i, j);

                node.setU(node.getU(in) + step * grid.getDtU(i,j,in), out);
                node.setV(node.getV(in) + step * grid.getDtV(i,j,in), out);
            }
        }
    }

    private void eulerBackward(int in, int out, double step) {
        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                AcmGridNode node = grid.get(i, j);

                node.setP(node.getP(in) + step * grid.getDtP(i,j,out), out);
            }
        }
    }

    private void applyBCs(int in, int out) {
        int nodeBC;
        double neuesU, neuesV, neuerdruck, neighU, neighV;
        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                AcmGridNode node = grid.get(i, j);
                nodeBC=node.getType();
                if (nodeBC==GridNodeType.SOLIDN)
                {
                    AcmGridNode neighNode = grid.get(i, j-1); //bc in N-Richtung heißt, dass in S-Richtung (nach unten) ein FluidKnoten da ist
                    neuerdruck=neighNode.getP(in);
                    node.setP(neuerdruck,out);
                    neighU=node.getU(in);
                    neuesU=neighU*0.5;
                    //neighV=node.getV(in);
                    neuesV=0.0;
                    node.setU(neuesU,out);
                    node.setV(neuesV,out);
                }
                 if (nodeBC==GridNodeType.SOLIDS)
                {
                    AcmGridNode neighNode = grid.get(i, j+1); //bc in N-Richtung heißt, dass in S-Richtung (nach unten) ein FluidKnoten da ist
                    neuerdruck=neighNode.getP(in);
                    node.setP(neuerdruck,out);
                    neighU=node.getU(in);
                    neuesU=neighU*0.5;
                    //neighV=node.getV(in);
                    neuesV=0.0;
                    node.setU(neuesU,out);
                    node.setV(neuesV,out);
                }
                 if (nodeBC==GridNodeType.SOLIDE)
                {
                    AcmGridNode neighNode = grid.get(i-1, j); //bc in N-Richtung heißt, dass in S-Richtung (nach unten) ein FluidKnoten da ist
                    neuerdruck=neighNode.getP(in);
                    node.setP(neuerdruck,out);
                    //neighU=node.getU(in);
                    neuesU=0.0;//neighU*0.5;
                    neighV=node.getV(in);
                    neuesV=neighV*0.5;
                    node.setU(neuesU,out);
                    node.setV(neuesV,out);
                }
                 if (nodeBC==GridNodeType.SOLIDN)
                {
                   AcmGridNode neighNode = grid.get(i+1, j); //bc in N-Richtung heißt, dass in S-Richtung (nach unten) ein FluidKnoten da ist
                    neuerdruck=neighNode.getP(in);
                    node.setP(neuerdruck,out);
                    //neighU=node.getU(in);
                    neuesU=0.0;//neighU*0.5;
                    neighV=node.getV(in);
                    neuesV=neighV*0.5;
                    node.setU(neuesU,out);
                    node.setV(neuesV,out);
                }
                 if (nodeBC==GridNodeType.SOLID)
                {
                   AcmGridNode neighNode = grid.get(i, j); //bc in N-Richtung heißt, dass in S-Richtung (nach unten) ein FluidKnoten da ist
                    neuerdruck=neighNode.getP(in);
                    node.setP(neuerdruck,out);
                    //neighU=node.getU(in);
                    neuesU=0.0;//neighU*0.5;
                    neighV=0.0;//node.getV(in);
                    neuesV=neighV*0.5;
                    node.setU(neuesU,out);
                    node.setV(neuesV,out);
                }
            }
        }
    }

    void checkerboard_cure(int in) {
        double ux, uy, vx, vy, dpxx, dpyy;

        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                AcmGridNode node = grid.get(i, j);

                ux = grid.getDxU(i, j, in);
                vx = grid.getDxV(i, j, in);
                uy = grid.getDyU(i, j, in);
                vy = grid.getDyV(i, j, in);

                dpxx = grid.getDxxP(i, j, in);
                dpyy = grid.getDyyP(i, j, in);

                node.setMedicine( dpxx + dpyy + 2.0 * (vx * uy - ux * vy) );
            }
        }
    }

     void calc_turbvisc(int in) {
        double dux, duy, dvx, dvy,S;

        for (int i = 0; i < grid.nx; i++) {
            for (int j = 0; j < grid.ny; j++) {

                AcmGridNode node = grid.get(i, j);

                AcmGridNode nodeN = grid.get(i,Math.min(j+1,grid.ny));
                AcmGridNode nodeS = grid.get(i,Math.max(j-1,0));
                AcmGridNode nodeE = grid.get(Math.min(i+1,grid.nx), j);
                AcmGridNode nodeW = grid.get(Math.max(i-1,0), j);

                dux = grid.getDxU(i, j, in);
                dvx = grid.getDxV(i, j, in);
                duy = grid.getDyU(i, j, in);
                dvy = grid.getDyV(i, j, in);

               S=grid.dx*grid.dx*0.18*0.18*Math.sqrt(dux*dux+(dvx+duy)*(dvx+duy)*2.0*0.5*0.5+dvy*dvy);

                node.setturbvisc(S);
            }
        }
    }



}
