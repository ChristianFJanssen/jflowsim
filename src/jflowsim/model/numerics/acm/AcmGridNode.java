package jflowsim.model.numerics.acm;

import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.utilities.Scalar;


public class AcmGridNode{

   private double u[] = new double[2];
   private double v[] = new double[2];
   private double p[] = new double[2];
   private double turbvisc[] = new double[2];

   private double medicine;

   private double velo;

   private int type = GridNodeType.FLUID;

    public void calcMomentum() { throw new UnsupportedOperationException("Not supported."); }

    public void setType(int type) {
        if (this.type != GridNodeType.BOUNDARY) {
            this.type = type;
        }
    }

    public void initBoundary() {
        this.type = GridNodeType.BOUNDARY;
    }

    public void setP( double P ){ this.p[0] = P; this.p[1] = P; }
    public void setU( double U ){ this.u[0] = U; this.u[1] = U; }
    public void setV( double V ){ this.v[0] = V; this.v[1] = V; }
    public void setturbvisc( double Turbvisc){ this.turbvisc[0]=Turbvisc; this.turbvisc[1]=Turbvisc;}

    public void setP( double P, int id ){ this.p[id] = P; }
    public void setU( double U, int id ){ this.u[id] = U; }
    public void setV( double V, int id ){ this.v[id] = V; }
    public void setturbvisc( double Turbvisc, int id ){ this.turbvisc[id]=Turbvisc; }

    public double getP( int id ){ return this.p[id]; }
    public double getU( int id ){ return this.u[id]; }
    public double getV( int id ){ return this.v[id]; }
    public double getTurbulentViscosity( int id ){ return this.turbvisc[id]; }

    public int getType() {
        return type;
    }

    public double getMedicine(){ return this.medicine; }
    public void   setMedicine( double medi ){ this.medicine = medi; }


    public double getVelo() {
        return Math.sqrt( this.u[0]*this.u[0] + this.v[0]*this.v[0] );
    }

    public double getVeloX() {
        return this.u[0];
    }

    public double getVeloY() {
        return this.v[0];
    }

    public double getScalar(int type) {
        if(type == Scalar.V_X){
            return getVeloX();
        }else if(type == Scalar.V_Y){
            return getVeloY();
        }else if(type == Scalar.V){
            return getVelo();
        }else if(type == Scalar.RHO){
            return 0.0;
        }else if(type == Scalar.GRID_TYPE){
            return this.getType();
        }else if(type == Scalar.T){
            return 0.0;
        }
        else{
            System.out.println("unknown scalar value " + type);
            System.exit(-1);
            return -1;

        }
    }
}
