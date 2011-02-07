package jflowsim.model.geometry2d.utilities;


public class Rounding {
    public static final int FLOOR = 10;
    public static final int CEIL = 11;
    public static final int ROUND = 12;

    public static int round(double value, int mode){
        if(mode == FLOOR){
            return (int)Math.floor(value);
        }
        else if(mode == CEIL){
            return (int)Math.ceil(value);
        }
        else {
            return (int)Math.round(value);
        }
    }

}
