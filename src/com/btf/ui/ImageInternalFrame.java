package com.btf.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ImageInternalFrame extends JInternalFrame{
	private ImagePane imagePane;
	boolean widthEx = false;
	boolean heightEx = false;

	public ImageInternalFrame(BufferedImage img) {
		super("Image", true, true, true, true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/*
		 * set content
		 */
		JPanel pane = new JPanel();
		pane.setOpaque(true); // content panes must be opaque
		setContentPane(pane);
		
		imagePane = new ImagePane(img);
		Dimension dim = new Dimension(imagePane.getPreferredSize());
		
		if (img.getWidth(null) > MainUI.scrSize.width * 0.8){
			dim.width = (int) (MainUI.scrSize.width * 0.8);
			widthEx = true;
		}
		
		if (img.getHeight(null) > MainUI.scrSize.height * 0.8){
			dim.height = (int) (MainUI.scrSize.height * 0.8);
			heightEx = true;
		}

		if (widthEx || heightEx){
			JScrollPane scrollPane = new JScrollPane(imagePane);
			scrollPane.setPreferredSize(dim);
			this.getContentPane().add(scrollPane);
		}else{
			this.getContentPane().add(imagePane);
		}
	}

	public ImagePane getImagePane(){
		return imagePane;
	}
}
