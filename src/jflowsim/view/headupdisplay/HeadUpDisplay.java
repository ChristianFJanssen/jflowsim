package jflowsim.view.headupdisplay;

import java.awt.Color;
import java.awt.Graphics;


public class HeadUpDisplay {

    private Graphics g;
    private int delta_y = 20;
    private int current_pos;
    private int vertical_spacing = 20;    
    private int horizontal_spacing = 10;

    public HeadUpDisplay() {
        this.current_pos = vertical_spacing;
    }

    public void setGraphicDevice(Graphics g){
        this.g = g;
        this.g.setColor(Color.BLACK);
    }

    public void flushDisplay(){
        this.current_pos = vertical_spacing;
    }

    public void drawText(String textLine){
        g.drawString(textLine, horizontal_spacing, current_pos);
        current_pos+=delta_y;
    }
}
