package com.btf.imgProcess;

import java.awt.image.BufferedImage;

public class HalfToning {
	private static final HalfToning INSTANCE = new HalfToning();
	public static HalfToning getInstance(){
		return INSTANCE;
	}
	
	private static final int[] level0 = new int[]{0,0,0,0,0,0,0,0,0};
	private static final int[] level1 = new int[]{0,0,0,0,0,0,0,0xffffffff,0};
	private static final int[] level2 = new int[]{0,0,0xffffffff,0,0,0,0,0xffffffff,0};
	private static final int[] level3 = new int[]{0,0,0xffffffff,0,0,0,0xffffffff,0xffffffff,0};
	private static final int[] level4 = new int[]{0xffffffff,0,0xffffffff,0,0,0,0xffffffff,0xffffffff,0};
	private static final int[] level5 = new int[]{0xffffffff,0,0xffffffff,0,0,0,0xffffffff,0xffffffff,0xffffffff};
	private static final int[] level6 = new int[]{0xffffffff,0,0xffffffff,0,0,0xffffffff,0xffffffff,0xffffffff,0xffffffff};
	private static final int[] level7 = new int[]{0xffffffff,0xffffffff,0xffffffff,0,0,0xffffffff,0xffffffff,0xffffffff,0xffffffff};
	private static final int[] level8 = new int[]{0xffffffff,0xffffffff,0xffffffff,0xffffffff,0,0xffffffff,0xffffffff,0xffffffff,0xffffffff};
	private static final int[] level9 = new int[]{0xffffffff,0xffffffff,0xffffffff,0xffffffff,0xffffffff,0xffffffff,0xffffffff,0xffffffff,0xffffffff};
	
	
	public BufferedImage process(BufferedImage img){
		if (img == null)
			return img;
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		int[] color = new int[width * height];
		int[] ret = new int[width * height * 9];
		img.getRGB(0, 0, width, height, color, 0, width);
		int idx = width * 3;
		int idx2 = 0;
		int[] color2;
		
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++){

				color2 = getGrayLevel(color[i * width + j]);
				
				idx2 = idx * 3 * i + 3 * j;
				ret[idx2] = color2[0];
				ret[idx2+1] = color2[1];
				ret[idx2+2] = color2[2];
				
				
				idx2 = idx * (3 * i + 1) + 3 * j;
				ret[idx2] = color2[3];
				ret[idx2+1] = color2[4];
				ret[idx2+2] = color2[5];
				
				
				idx2 = idx * (3 * i + 2) + 3 * j;
				ret[idx2] = color2[6];
				ret[idx2+1] = color2[7];
				ret[idx2+2] = color2[8];
				
			}
		}

		BufferedImage res = new BufferedImage(3 * width, 3 * height, BufferedImage.TYPE_INT_RGB);
		res.setRGB(0, 0, 3 * width, 3 * height, ret, 0, 3 * width);
		return res;
	}
	
	public BufferedImage generateWedge(){
		int width = 256;
		int height = 256;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] color = new int[width * height];
		int idx = 0;
		
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++){
				idx = i * width + j;
				color[idx] = 0;
				color[idx] = (color[idx] << 8) | j;
				color[idx] = (color[idx] << 8) | j;
				color[idx] = (color[idx] << 8) | j;
			}
		}
		
		img.setRGB(0, 0, width, height, color, 0, width);
		
		return img;
	}
	
	
	private int[] getGrayLevel(int int_rgb){
		int ret = (int) ((int_rgb & 0x000000ff) * 0.299f + ((int_rgb >> 8) & 0x000000ff) * 0.587f + ((int_rgb >> 16) & 0x000000ff) * 0.114f) ;
		ret = ret & 0xff;
		if (ret < 25)
			return level0;
		else if (ret < 51)
			return level1;
		else if (ret < 77)
			return level2;
		else if (ret < 102)
			return level3;
		else if (ret < 128)
			return level4;
		else if (ret < 153)
			return level5;
		else if (ret < 179)
			return level6;
		else if (ret < 204)
			return level7;
		else if (ret < 230)
			return level8;
		else
			return level9;
	}
}
