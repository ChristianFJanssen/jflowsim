package jflowsim.view;

import jflowsim.controller.mouseeventstrategy.MouseEventStrategy;
import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.view.graphics.GraphicObject;
import java.awt.Graphics;
import java.util.Observable;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.Observer;

public class GraphicViewer extends JPanel implements Observer {

    private LinkedList<GraphicObject> graphicList;
    private WorldViewTransformator2D trafo;
    private MouseEventStrategy mouseEventStrategy;

    // Konstruktor
    public GraphicViewer(WorldViewTransformator2D trafo) {
        this.graphicList = new LinkedList<GraphicObject>();
        this.trafo = trafo;

        this.setLayout(new GridLayout());
        this.setBackground(Color.WHITE);
        //this.setBackground(new Color(84, 89, 109));

        this.setDoubleBuffered(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void setMouseEventStrategy(MouseEventStrategy mouseEventStrategy) {

        this.removeMouseListener(this.mouseEventStrategy);
        this.removeMouseMotionListener(this.mouseEventStrategy);
        this.removeMouseWheelListener(this.mouseEventStrategy);

        this.mouseEventStrategy = mouseEventStrategy;

        this.addMouseListener(mouseEventStrategy);
        this.addMouseMotionListener(mouseEventStrategy);
        this.addMouseWheelListener(mouseEventStrategy);
    }

    public LinkedList<GraphicObject> getGraphicList() {
        return graphicList;
    }

    public GraphicObject getObjectForViewCoordinates(int viewX, int viewY) {

        for (GraphicObject graphic : graphicList) {
            if (graphic.isPointOnBoundary(viewX, viewY, 20, trafo)) {
                return graphic;
            }
        }
        return null;
    }

    public WorldViewTransformator2D getTrafo() {
        return trafo;
    }

    public void add(GraphicObject graphic) {
        graphic.addObserver(this);
        this.graphicList.add(graphic);
        this.repaint();
    }

    public void addFirst(GraphicObject graphic) {
        graphic.addObserver(this);
        this.graphicList.addFirst(graphic);
        this.repaint();
    }

    public void remove(GraphicObject graphic) {
        graphic.deleteObservers();
        this.graphicList.remove(graphic);
        this.repaint();
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        
        for (int i = 0; i < graphicList.size(); i++) {
            graphicList.get(i).paint(g, trafo);
        }
    }

    public void removeAllGraphicObjects() {
        graphicList.clear();
        repaint();
    }

    public void update(Observable o, Object arg) {
        repaint();
    }
}
