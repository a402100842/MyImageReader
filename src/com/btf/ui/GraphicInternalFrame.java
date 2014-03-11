package com.btf.ui;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphicInternalFrame extends JInternalFrame{
	
	private GraphicPane graphicPane;
	
	public GraphicInternalFrame(float[] data, String name){
		super(name, true, true, true, true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		/*
		 * set content
		 */
		JPanel pane = new JPanel();
		pane.setOpaque(true); // content panes must be opaque
		setContentPane(pane);
		
		graphicPane = new GraphicPane(data);
		
		this.getContentPane().add(graphicPane);
	}
	
	public GraphicInternalFrame(float[] data){
		this(data, "Ö±·½Í¼");
	}
	
	public GraphicPane getGraphicPane(){
		return graphicPane;
	}
}
