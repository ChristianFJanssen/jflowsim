package jflowsim.view.displaystyle;

import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.numerics.UniformGrid;
import java.awt.Graphics;
import jflowsim.view.headupdisplay.HeadUpDisplay;


public abstract class DisplayStyle {

   protected double min, max, tmp_min, tmp_max;
   protected int scalar_type;
   protected boolean enabled = false;

    public DisplayStyle() {
        this.min = Double.MAX_VALUE;
        this.max = -Double.MAX_VALUE;
        this.tmp_min = Double.MAX_VALUE;
        this.tmp_max = -Double.MAX_VALUE;
        this.enabled = false;
    }

    public void setEnabled(boolean enabled){
        this.min = Double.MAX_VALUE;
        this.max = -Double.MAX_VALUE;
        this.tmp_min = Double.MAX_VALUE;
        this.tmp_max = -Double.MAX_VALUE;
        this.enabled = enabled;
    }

    public abstract String[] getScalars();

    public void setScalarType(int scalar_type){        
        this.scalar_type = scalar_type;
    }
    
    public abstract void paint(Graphics g, WorldViewTransformator2D trafo, UniformGrid grid, HeadUpDisplay hud);

}
