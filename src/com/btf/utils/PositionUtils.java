package com.btf.utils;

import java.awt.image.BufferedImage;

public class PositionUtils {
	public int width;
	public int height;
	public int[] datas;
	
	public static PositionUtils create(BufferedImage img, IPixelProcess pro){
		if (img == null)
			return null;
		int width = img.getWidth(null);
		int height = img.getHeight(null);

		//µÃµ½Ô­Í¼ÏñËØ
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		if (pro != null){
			pro.process(color);
		}
		
		PositionUtils image = new PositionUtils(width, height, color);
		return image;
	}
	
	public PositionUtils(int w, int h, int[] color) {
		width = w;
		height = h;
		datas = color;
	}
	
	/**
	 * Get the position (p.x+dx, p.y+dy)
	 * @param p		current position
	 * @param dx	delta x
	 * @param dy	delta y
	 * @return
	 */
	public int f(int p, int dx, int dy){
		int x = p % width;
		int y = p / width;
		return f2(x, y, dx, dy);
	}
	
	/**
	 * Get the position (x+dx, y+dy)
	 * @param x		current x
	 * @param y		current y
	 * @param dx	delta x
	 * @param dy	delta y
	 * @return
	 */
	public int f2(int x, int dx, int y, int dy){
		if (x < 0 || x >= width || x + dx < 0 || x + dx >= width)
			return 0;
		if (y < 0 || y >= height || y + dy < 0 || y + dy >= height)
			return 0;
		return datas[(y + dy) * width + x + dx];
	}
	
	public int f3(int x, int y){
		return datas[y * width + x];
	}
	
	public int f4(int x, int y){
		return f3(x + width/2, y + height / 2);
	}
}
