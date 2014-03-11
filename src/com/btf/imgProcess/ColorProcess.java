package com.btf.imgProcess;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.btf.utils.Utils;

public class ColorProcess {
	
	/**
	 * simply cut those who are out of [0, 255]
	 * @param data
	 * @return
	 */
	public static int[] scaling(int[] data){
    	for (int i = 0; i < data.length; i++){
    		if (data[i] < 0){
    			data[i] = 0;
    		} else if (data[i] > 255)
    			data[i] = 255;
    	}
    	return data;
	}
	
	public static BufferedImage getKbitImage(int k, Image img){
    	if (k < 1 || k > 8 || img == null)
    		return null;
    	
    	int width = img.getWidth(null);
    	int height = img.getHeight(null);
    	int[] color = new int[width * height];
    	
    	BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.getGraphics().drawImage(img, 0, 0, width, height, null);
		result.getRGB(0, 0, width, height, color, 0, width);
		
		for (int i = 0; i < color.length; i++){
			color[i] = byte2int(getByteByGrayLevel(k, color[i]));
		}
		
		result.setRGB(0, 0, width, height, color, 0, width);
		return result;
    }
	
	/**
	 * 将int_rgb转化为Byte灰度
	 * @param l must be 1 to 8， 灰度等级
	 * @return
	 */
	public static byte getByteByGrayLevel(int l, int int_rgb){
		int temp = 0x01;
		temp <<= l;//temp = 2^l
		int interval = temp - 1;
		interval = 255 / interval;
		
		byte ret = (byte)((int_rgb & 0x000000ff) * 0.299f + ((int_rgb >> 8) & 0x000000ff) * 0.587f + ((int_rgb >> 16) & 0x000000ff) * 0.114f);
		int temp2 = ret & 0xff;
		temp2 >>= (8 - l);
    	
    	for (int i = 0; i < temp; i++){
    		if (temp2 == i){
    			ret = (byte) (interval * i);
    			break;
    		}
    	}
    
		return ret;
	}
	
	/**
	 * 将byte复制3份组成int
	 * @param bt
	 * @return
	 */
	public static int byte2int(byte bt){
		byte[] b = new byte[3];
		b[0] = bt;
		b[1] = bt;
		b[2] = bt;
		return (Utils.constructInt3(b, 0));
	}
	
	/**
	 * 将int低8位复制3份组成int
	 * @param int_gray
	 * @return
	 */
	public static int int2int(int int_gray){
		return byte2int(Utils.int2byte(int_gray));
	}
	
	public static BufferedImage showChanelB(Image img) {
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.getGraphics().drawImage(img, 0, 0, width, height, null);
		int[] color = new int[width * height];
		result.getRGB(0, 0, width, height, color, 0, width);

		for (int i = 0; i < color.length; i++)
			color[i] = color[i] & 0x000000ff;
		
		result.setRGB(0, 0, width, height, color, 0, width);
		return result;
	}

	public static BufferedImage showChanelG(Image img) {
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.getGraphics().drawImage(img, 0, 0, width, height, null);
		int[] color = new int[width * height];
		result.getRGB(0, 0, width, height, color, 0, width);

		for (int i = 0; i < color.length; i++)
			color[i] = color[i] & 0x0000ff00;
		
		result.setRGB(0, 0, width, height, color, 0, width);
		return result;
	}

	public static BufferedImage showChanelR(Image img) {
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.getGraphics().drawImage(img, 0, 0, width, height, null);
		int[] color = new int[width * height];
		result.getRGB(0, 0, width, height, color, 0, width);

		for (int i = 0; i < color.length; i++)
			color[i] = color[i] & 0x00ff0000;
		
		result.setRGB(0, 0, width, height, color, 0, width);
		return result;
	}

}
