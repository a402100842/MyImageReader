package com.btf.imgProcess;

import java.awt.image.BufferedImage;

public class PseudoColor {
	
	public static BufferedImage toYellow(BufferedImage img, int low, int high){
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		//µÃµ½Ô­Í¼ÏñËØ
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		int temp;
		for (int i = 0; i < color.length; i++){
			temp = color[i] & 0x000000ff;
			if (temp >= low && temp <= high)
				color[i] = 0x00ffff00;
		}
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, width, height, color, 0, width);
		return result;
	}
}
