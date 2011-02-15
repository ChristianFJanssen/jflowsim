package jflowsim.view.displaystyle;

import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.numerics.UniformGrid;
import java.awt.Graphics;
import java.util.ArrayList;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class DisplayStyleManager{   

    private ArrayList<DisplayStyle> scalarStyles = new ArrayList<DisplayStyle>();
    private ArrayList<DisplayStyle> vectorStyles = new ArrayList<DisplayStyle>();
    

    public DisplayStyleManager(){
        this.scalarStyles.add(new ColorPlotStyle());
        this.scalarStyles.add(new IntersectionStyle());
        this.scalarStyles.add(new GridStyle());
        this.scalarStyles.add(new IsolineBlackPlotStyle());
        this.scalarStyles.add(new IsolineColorPlotStyle());        

        this.vectorStyles.add(new ArrowBlackPlotStyle());
        this.vectorStyles.add(new ArrowColorPlotStyle());
    }

    public void activateScalarStyle(int index, int scalar){
        //System.out.println("activate: "+scalarStyles.get(index).getClass().getSimpleName()+" scalar:"+scalar);
        scalarStyles.get(index).setScalarType(scalar);
        scalarStyles.get(index).setEnabled(true);
    }

    public void deactivateScalarStyle(int index){
        //System.out.println("deactivate: "+scalarStyles.get(index).getClass().getSimpleName());
        scalarStyles.get(index).setEnabled(false);
    }

    public ArrayList<DisplayStyle> getScalarStyles(){
        return scalarStyles;
    }



    public void activateVecotrStyle(int index){
        //System.out.println("activate: "+scalarStyles.get(index).getClass().getSimpleName()+" scalar:"+scalar);
        vectorStyles.get(index).setEnabled(true);
    }

    public void deactivateVecotrStyle(int index){
        //System.out.println("deactivate: "+scalarStyles.get(index).getClass().getSimpleName());
        vectorStyles.get(index).setEnabled(false);
    }

    public ArrayList<DisplayStyle> getVectorStyles(){
        return vectorStyles;
    }

    public void paint(Graphics g, WorldViewTransformator2D trafo, UniformGrid grid, HeadUpDisplay hud) {
        // first paint the scalar styles
        for(DisplayStyle style : scalarStyles){
            style.paint(g, trafo, grid, hud);
        }

        // second paint the vector styles
        for(DisplayStyle style : vectorStyles){
            style.paint(g, trafo, grid, hud);
        }
    }
}
