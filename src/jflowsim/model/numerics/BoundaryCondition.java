package jflowsim.model.numerics;

public abstract class BoundaryCondition {

    public static final int VOID = 0;
    public static final int EAST = 1;
    public static final int WEST = 2;
    public static final int NORTH = 3;
    public static final int SOUTH = 4;

    protected int type;

    public abstract void apply();
}
