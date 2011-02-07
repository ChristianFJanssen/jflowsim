package jflowsim.model.algebra;

import java.util.ArrayList;
import jflowsim.model.algebra.jama.Matrix;
import java.io.Serializable;

public class WorldViewTransformator2D implements Serializable {

    public double viewMinX, viewMinY;
    public double viewMaxX, viewMaxY;
    private double worldMinX, worldMinY;
    private double worldMaxX, worldMaxY;
    private ArrayList<Matrix> matrices;
    private Matrix transfMatrix;
    private Matrix inverseTransfMatrix;
    private boolean calculateMatrices = false;
    private double viewFactor;

    public WorldViewTransformator2D() {
        transfMatrix = new Matrix(3, 3, 0.0);
        matrices = new ArrayList<Matrix>();

        calculateMatrices = false;
        viewFactor = 0.9;  //Faktor damit im ViewFenster ein Rand bleibt
    }

    private void buildMatricesForWorldToViewTranformation() {

        matrices.clear();

        double dxView = (viewMaxX - viewMinX);
        double dyView = (viewMaxY - viewMinY);

        double dxWorld = (worldMaxX - worldMinX);
        double dyWorld = (worldMaxY - worldMinY);

        if (dxWorld < 1.E-6 || dyWorld < 1.E-6) {
            return;
        }

        double midXWorld = 0.5 * (worldMaxX + worldMinX);
        double midYWorld = 0.5 * (worldMaxY + worldMinY);

        double vx = (viewFactor * dxView) / dxWorld;
        double vy = (viewFactor * dyView) / dyWorld;

        double sx, sy, tx, ty;
        if (vx < vy) {
            sx = sy = vx;
        } else {
            sx = sy = vy;
        }

        sy = -sy;

        int vorzeichenx = 1;
        int vorzeicheny = 1;

        double centerWorldX = (worldMinX + (worldMaxX - worldMinX) / 2) * sx;
        double centerViewX = (viewMinX + (viewMaxX - viewMinX) / 2);

        if (centerWorldX > centerViewX) {
            vorzeicheny = -1;
        } else {
            vorzeicheny = -1;
        }

        tx = vorzeichenx * ((viewMaxX - viewMinX) / 2 + ((dxWorld * sx) / 2) + (viewMinX - worldMaxX * sx));
        ty = (viewMaxY - viewMinY) / 2 + vorzeicheny * (((dyWorld * Math.abs(sy)) / 2) + (viewMinY - worldMaxY * Math.abs(sy)));

        this.addTranslation(-midXWorld, -midYWorld);

        this.addScaling(sx, sy);

        this.addTranslation(tx, ty);
    }

    public double scaleViewToWorldLength(double l_view, boolean intRounding) {

        double dxView = viewMaxX - viewMinX;
        double dyView = viewMaxY - viewMinY;

        double dxWorld = worldMaxX - worldMinX;
        double dyWorld = worldMaxY - worldMinY;

        double vx = (this.viewFactor * dxView) / dxWorld;
        double vy = (this.viewFactor * dyView) / dyWorld;

        double s;
        if (vx < vy) {
            s = vx;
        } else {
            s = vy;
        }
        double newL = (l_view / s);
        if (intRounding) {
            newL = Math.round(newL);
        }
        return newL;
    }

    public double scaleWorldToViewLength(double l_world, boolean intRounding) {

        double dxView = viewMaxX - viewMinX;
        double dyView = viewMaxY - viewMinY;

        double dxWorld = worldMaxX - worldMinX;
        double dyWorld = worldMaxY - worldMinY;

        double vx = (this.viewFactor * dxWorld) / dxView;
        double vy = (this.viewFactor * dyWorld) / dyView;

        double s;
        if (vx < vy) {
            s = vx;
        } else {
            s = vy;
        }
        double newL = (l_world / s);
        if (intRounding) {
            newL = Math.round(newL);
        }
        return newL;
    }

    private boolean calcTransformationsMatrices() {

        //TranformationsMatrix "reseten"
        transfMatrix = new Matrix(3, 3, 0.0);
        transfMatrix.set(0, 0, 1.0);
        transfMatrix.set(1, 1, 1.0);
        transfMatrix.set(2, 2, 1.0);

        this.buildMatricesForWorldToViewTranformation();

        if (matrices.size() < 1) {
            return false;
        }

        for (int i = 1; i < matrices.size(); i++) {
            transfMatrix = transfMatrix.times(matrices.get(i));
        }
        inverseTransfMatrix = transfMatrix.inverse();

        this.calculateMatrices = true;

        return true;
    }

    public void setViewWindow(double viewMinX, double viewMinY, double viewMaxX, double viewMaxY) {

        this.viewMinX = viewMinX;
        this.viewMinY = viewMinY;
        this.viewMaxX = viewMaxX;
        this.viewMaxY = viewMaxY;
        this.calculateMatrices = false;
    }

    public void setWorldWindow(double worldMinX, double worldMinY, double worldMaxX, double worldMaxY) {

        this.worldMinX = worldMinX;
        this.worldMinY = worldMinY;
        this.worldMaxX = worldMaxX;
        this.worldMaxY = worldMaxY;
        this.calculateMatrices = false;
    }

    public double transformWorldToViewXCoord(double worldX, double worldY, boolean intRounding) {

        if (!this.calculateMatrices) {
            this.calcTransformationsMatrices();
        }

        double viewX = worldX * transfMatrix.get(0, 0) + worldY * transfMatrix.get(1, 0) + transfMatrix.get(2, 0);

        if (intRounding) {
            viewX = Math.round(viewX);
        }
        return viewX;
    }

    public double transformWorldToViewYCoord(double worldX, double worldY, boolean intRounding) {

        if (!this.calculateMatrices) {
            this.calcTransformationsMatrices();
        }

        double viewY = worldX * transfMatrix.get(0, 1) + worldY * transfMatrix.get(1, 1) + transfMatrix.get(2, 1);

        if (intRounding) {
            viewY = Math.round(viewY);
        }
        return viewY;
    }

    public double transformViewToWorldXCoord(double viewX, double viewY, boolean intRounding) {

        if (!this.calculateMatrices) {
            this.calcTransformationsMatrices();
        }

        double worldX = viewX * inverseTransfMatrix.get(0, 0) + viewY * inverseTransfMatrix.get(1, 0) + inverseTransfMatrix.get(2, 0);

        if (intRounding) {
            worldX = Math.round(worldX);
        }
        return worldX;
    }

    public double transformViewToWorldYCoord(double viewX, double viewY, boolean intRounding) {

        if (!this.calculateMatrices) {
            this.calcTransformationsMatrices();
        }

        double worldX = viewX * inverseTransfMatrix.get(0, 1) + viewY * inverseTransfMatrix.get(1, 1) + inverseTransfMatrix.get(2, 1);

        if (intRounding) {
            worldX = Math.round(worldX);
        }
        return worldX;
    }

    public boolean zoomWorldWindow(double zoomFactor) {

        double deltaX = zoomFactor * (worldMaxX - worldMinX);
        double deltaY = zoomFactor * (worldMaxY - worldMinY);

        double minX = worldMinX - deltaX;
        double minY = worldMinY - deltaY;
        double maxX = worldMaxX + deltaX;
        double maxY = worldMaxY + deltaY;

        if ((maxX - minX) > 1.E-6 && (maxY - minY) > 1.E-6) {
            this.setWorldWindow(minX, minY, maxX, maxY);
            return true;
        }
        System.out.println("WorldViewCoordTransformator2D::zoomWorldWindow - no zoom, because worldwindow would get zero or negative");
        return false;
    }

    public void moveViewWindow(double viewDX, double viewDY) {

        viewMinX += viewDX;
        viewMinY += viewDY;
        viewMaxX += viewDX;
        viewMaxY += viewDY;

        calculateMatrices = false;
    }

    private void addScaling(double sx, double sy) {

        double[][] M = {
            {sx, 0.0, 0.0},
            {0.0, sy, 0.0},
            {0.0, 0.0, 1.0}};

        matrices.add(new Matrix(M));
        calculateMatrices = false;
    }

    private void addTranslation(double tx, double ty) {

        double[][] M = {
            {1.0, 0.0, 0.0},
            {0.0, 1.0, 0.0},
            {tx, ty, 1.0}};

        matrices.add(new Matrix(M));
        calculateMatrices = false;
    }

    private void addRotation(double phi /*clockwise*/) {

        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);

        double[][] M = {
            {cosPhi, -sinPhi, 0.0},
            {sinPhi, cosPhi, 0.0},
            {0.0, 0.0, 1.0}};

        matrices.add(new Matrix(M));
        calculateMatrices = false;
    }
}
