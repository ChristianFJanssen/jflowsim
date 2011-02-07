package jflowsim.model.numerics;

import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.ModelManager;
import jflowsim.model.geometry2d.Geometry2D;
import jflowsim.model.geometry2d.utilities.Rounding;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public abstract class UniformGrid {

    public int nx, ny;
    private double minX, minY, maxX, maxY;
    public boolean periodicX, periodicY;
    private double length, width; // PRIVATE, damit Zugriff nur ueber getter/setter zwecks min/max-update
    public double dx, dt;
    public double v_scale;
    public double nue_real;
    public double gravity = 9.81;
    public int updateInterval = 25;
    public int timestep;
    public double real_time; // real in secs
    public double mnups;
    public String testcase;

    public int type[];

    public UniformGrid(double _length, double _width, double _dx) {

        this.setLength(_length);
        this.setWidth(_width);
        this.dx = _dx;

        this.nx = (int) Math.round(this.getLength() / this.dx) + 1;
        this.ny = (int) Math.round(this.getWidth() / this.dx) + 1;

        this.allocateMemory();
    }

    public UniformGrid(double _length, double _width, int _nx, int _ny) {

        this.setLength(_length);
        this.setWidth(_width);

        this.nx = _nx;
        this.ny = _ny;

        this.dx = this.getLength() / (this.nx - 1);

        this.allocateMemory();
    }

    protected abstract void allocateMemory();

    public void map2Grid(ModelManager model) {

        for (int x = 0; x < nx; x++) {
            for (int y = 0; y < ny; y++) {
                if (getType(x, y) == GridNodeType.SOLID) {
                    setType(x, y, GridNodeType.FLUID);
                }
            }
        }
        // map Geometry to Grid
        for (Geometry2D geo : model.getGeometryList()) {
            geo.map2Grid(this);
        }
    }

    public void updateHeadUpDisplay(HeadUpDisplay hud) {
    }

    public abstract double getScalar(int x, int y, int scalar_name);

    // checks if (x,y) is inside the grid
    public boolean isPointInside(double x, double y) {
        if (x < minX || x > maxX) {
            return false;
        }
        if (y < minY || y > maxY) {
            return false;
        }
        return true;
    }

    public void setLength(double length) {
        this.minX = 0.0;
        this.maxX = length;

        this.length = length;
    }

    public void setGridResolution(double deltaX) {
        this.dx = deltaX;
    }

    public void setWidth(double width) {
        this.minY = 0.0;
        this.maxY = width;

        this.width = width;
    }

    public double getLength() {
        return this.length;
    }

    public double getWidth() {
        return this.width;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public int transCoord2XIndex(double x, int mode) {
        int ix = Rounding.round(x / dx + 1.0, mode);
        if (ix < 1) {
            ix = 1;
        }
        if (ix > nx - 2) {
            ix = nx - 2;
        }
        return ix;
    }

    public int transCoord2YIndex(double y, int mode) {
        int iy = Rounding.round(y / dx + 1.0, mode);
        if (iy < 1) {
            iy = 1;
        }
        if (iy > ny - 1) {
            iy = ny - 1;
        }
        return iy;
    }

    public double transXIndex2Coord(int x) {
        return minX + dx * x;
    }

    public double transYIndex2Coord(int y) {
        return minY + dx * y;
    }

    public int getType(int x, int y) {
        return this.type[y * this.nx + x];
    }

    public void setType(int x, int y, int type) {
        this.type[y * this.nx + x] = type;
    }
}
