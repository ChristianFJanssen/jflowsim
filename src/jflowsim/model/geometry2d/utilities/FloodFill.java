package jflowsim.model.geometry2d.utilities;

import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.UniformGrid;

public class FloodFill {

    public static void fill(int x, int y, UniformGrid grid) {
        if (grid.getType(x, y) == GridNodeType.FLUID) {

            grid.setType(x, y, GridNodeType.SOLID);


            fill(x, y + 1, grid); // unten
            fill(x - 1, y, grid); // links
            fill(x, y - 1, grid); // oben
            fill(x + 1, y, grid); // rechts
        }
    }
}
