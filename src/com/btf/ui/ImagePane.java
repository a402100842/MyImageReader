package com.btf.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class ImagePane extends JComponent{
	private BufferedImage image;
	private int width, height;
	public boolean clicked;
	
    /*
    public ImagePane(int w, int h){
    	image = null;
    	width = w;
    	height = h;
    }*/
    
    public ImagePane(Image img, MouseListener mouseListener){
    	setImage(img);
    	clicked = false;
    	this.addMouseListener(mouseListener);
    }
    
    public ImagePane(Image img){
    	this(img, null);
    }
    
    public ImagePane(MouseListener mouseListener){
    	this(null, mouseListener);
    }
    
    public ImagePane(){
    	this(null, null);
    }
    
    public ImagePane setImage(Image img){
    	if (img == null){
    		image = null;
    		width = 0;
    		height = 0;
    		return this;
    	}
    	width = img.getWidth(null);
    	height = img.getHeight(null);
    	if (img instanceof BufferedImage)
    		image = (BufferedImage) img;
    	else{
    		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);;
    		image.getGraphics().drawImage(img, 0, 0, width, height, null);
    	}
		this.setSize(getPreferredSize());
		
		return this;
    }
    
    public BufferedImage getImage(){
    	return image;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }
    
    @Override
    public void paint(Graphics g) {
    	if (image != null){
    		g.drawImage(image, 0, 0, null);
    		if (clicked){
    			Graphics2D g2 = (Graphics2D) g;
    			Dimension dim = this.getSize();
    			double w = dim.getWidth();
    			double h = dim.getHeight();
    			g2.setStroke(new BasicStroke(5.0f));
    			g2.setColor(Color.GREEN);
    			g2.draw(new QuadCurve2D.Double(0, h/2, w/3, h, w, h/3));
    		}
    	}
    }
    
    
}
