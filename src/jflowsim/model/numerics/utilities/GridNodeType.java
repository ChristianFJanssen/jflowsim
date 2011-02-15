package jflowsim.model.numerics.utilities;

public interface GridNodeType {

    public static final int BOUNDARY  = -2;
    public static final int SOLID     = -1;
    public static final int INTERFACE = 1;
    public static final int FLUID     = 2;  
    public static final int INFLOWH   = 3;
    public static final int RUNUP     = 4;
    public static final int GAS       = 5;
    public static final int SOLIDN     = 6;
    public static final int SOLIDS     = 7;
    public static final int SOLIDE     = 8;
    public static final int SOLIDW     = 9;
    public static final int VELO       = 10;
    public static final int PRESS      = 11;
}
