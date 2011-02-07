package jflowsim.view.displaystyle;

import jflowsim.model.algebra.WorldViewTransformator2D;
import jflowsim.model.numerics.UniformGrid;
import java.awt.Graphics;


public abstract class DisplayStyle {

   protected double min, max, tmp_min, tmp_max;
   protected int scalar_type;
   protected boolean enabled = false;

    public DisplayStyle() {
        this.enabled = false;
    }

    public void setEnabled(boolean enabled){        
        this.enabled = enabled;
    }

    public abstract String[] getScalars();

    public void setScalarType(int scalar_type){        
        this.scalar_type = scalar_type;
    }
    
    public abstract void paint(Graphics g, WorldViewTransformator2D trafo, UniformGrid grid);

}
