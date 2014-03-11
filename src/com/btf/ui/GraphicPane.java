package com.btf.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GraphicPane extends JComponent{
	private float width, height;
	private float[] data;
	
	public GraphicPane(float[] data) {
		Dimension dim = MainUI.scrSize;
		width =  dim.width * 0.5f;
		height = dim.height * 0.7f;
		this.data = data;
	}
	
	public float[] getData(){
		return data;
	}
	
	@Override
    public Dimension getPreferredSize() {
        return new Dimension((int)width + 10, (int)height + 10);
    }
    
    @Override
    public void paint(Graphics g) {
    	Graphics2D g2 = (Graphics2D) g;
    	float rectWidth = width / data.length;
    	float rectHeight;
    	float x = 5;
    	float y = height - 5;
    	
    	g2.draw(new Line2D.Float(x, y, width + 5, y));
    	g2.draw(new Line2D.Float(x, 5, x, y));
    	
    	float highest = 0;
    	for (int i = 0; i < data.length; i++){
    		if (highest < data[i])
    			highest = data[i];
    	}
    	highest = 0.5f * height / highest;
    	
    	g2.setPaint(Color.black);
    	for (int i = 0; i < data.length; i++){
    		rectHeight = data[i] * highest;
    		g2.fill(new Rectangle2D.Float(x, y - rectHeight, rectWidth, rectHeight));
    		x += rectWidth;
    	}
    }
}
