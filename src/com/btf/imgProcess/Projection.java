package com.btf.imgProcess;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.btf.imgIO.BMPIO;
import com.btf.utils.Utils;

public class Projection {
	
	public static BufferedImage cylindricalProjection(final BufferedImage img, final double angle){
		if (img == null)
			return null;

		int width = img.getWidth(null);
		int height = img.getHeight(null);
		double width5 = width / 2;
		double height5 = height / 2;
		double angle5 = angle / 2;
		
		double r = width / (2 * Math.tan(angle5));
		double k;

		int width2 = (int) (r * angle + 1);
		
		BufferedImage result = new BufferedImage(width2, height, BufferedImage.TYPE_INT_RGB);

		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		int[] res = new int[width2 * height];
		Arrays.fill(res, 0);
		
		int idx1 = 0;
		int x2 = 0;
		int y2 = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				idx1 = y * width + x;
				k = r / (Math.cos(Math.atan((x - width5) / r)));
				y2 = (int) (height5 + r * (y - height5) / k);
				x2 = (int) (r * (angle5 + Math.atan((x - width5) / r)));
				res[y2 * width2 + x2] = color[idx1];
			}
		}

		result.setRGB(0, 0, width2, height, res, 0, width2);
		return result;
	}
	
	public static BufferedImage cylindricalProjection2(final BufferedImage img, final double angle){
		if (img == null)
			return null;

		int width = img.getWidth(null);
		int height = img.getHeight(null);
		double width5 = width / 2;
		double height5 = height / 2;
		double angle5 = angle / 2;
		
		double r = width / (2 * Math.tan(angle5));
		double r2 = r * r;
		double k;
		
		int width2 = (int) (2 * r * Math.sin(angle5) + 1);
		
		
		BufferedImage result = new BufferedImage(width2, height, BufferedImage.TYPE_INT_RGB);

		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		int[] res = new int[width2 * height];
		Arrays.fill(res, 0);
		
		int idx1 = 0;
		int x2 = 0;
		int y2 = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				idx1 = y * width + x;
				k = Math.sqrt(r2 + (width5 - x) * (width5 - x));
				y2 = (int) (height5 + r * (y - height5) / k);
				x2 = (int) (r * (Math.sin(angle5) + Math.sin(Math.atan((x - width5) / r))));
				res[y2 * width2 + x2] = color[idx1];
			}
		}

		result.setRGB(0, 0, width2, height, res, 0, width2);
		return result;
	}
	
	public static BufferedImage imageMosaic(final JFileChooser fileChooser, final Component parent, final double angle){
		int returnVal;
		BufferedImage[] imgs = new BufferedImage[3];
		for (int i = 0; i < 3; i++) {
			returnVal = fileChooser.showOpenDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				BufferedImage img = null;
				
				if (Utils.getExtension(file).contains("tif")) {
					
				}else if (Utils.getExtension(file).equals("bmp")){
					try {
						img = BMPIO.myRead(file.getPath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}else {
					try {
						img = ImageIO.read(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (img == null) {
					JOptionPane.showMessageDialog(parent, "Please select 3 images!");
					return null;
				}else{
					imgs[i] = img;
				}
			}else{
				JOptionPane.showMessageDialog(parent, "Please select 3 images!");
				return null;
			}
		}
		
		int[] widths = new int[3];
		int[] heights = new int[3];
		int[][] colors = new int[3][];
		byte[][] color = new byte[3][];
		
		for (int i = 0; i < 3; i++){
			imgs[i] = cylindricalProjection2(imgs[i], angle);
			widths[i] = imgs[i].getWidth(null);
			heights[i] = imgs[i].getHeight(null);
			colors[i] = new int[widths[i] * heights[i]];
			color[i] = new byte[widths[i] * heights[i]];
			imgs[i].getRGB(0, 0, widths[i], heights[i], colors[i], 0, widths[i]);
			for (int j = 0; j < colors[i].length; j++){
				color[i][j] = ColorProcess.getByteByGrayLevel(8, colors[i][j]);
			}
		}
		
		int height;
		
		if (heights[0] == heights[1] && heights[1] == heights[2]){
			height = heights[0];
		}else{
			JOptionPane.showMessageDialog(parent, "匹配失败！\n请输入3张相同高度的图像。");
			return null;
		}
		
		int tempWidth = widths[1] / 5;	//模板宽度
//		byte[] color0 = new byte[tempWidth * height];
		byte[] color1 = new byte[tempWidth * height];
		
		//得到模板的所有像素存到color1
		for (int i = 0; i < height; i++){
			for (int j = 0; j < tempWidth; j++){
				color1[i * tempWidth + j] = color[1][i * widths[1] + j];
			}
		}
		
		double[] d01 = new double[(widths[0] - tempWidth) / 2 + 1];
		Arrays.fill(d01, 0);
		int idx = 0;
		double min = 1000;
		boolean first = true;
		int minX = widths[0] - tempWidth;
		
		for (int x = widths[0] - tempWidth; x >=0; x-=2){
			for (int i = 0; i < height; i++){
				for (int j = 0; j < tempWidth; j++){
//					color0[i * tempWidth + j] = color[0][i * widths[0] + j + x];
					d01[idx] += 
					Math.abs(color[0][i * widths[0] + j + x] - color1[i * tempWidth + j]);
//					Math.pow((color[0][i * widths[0] + j + x] - color1[i * tempWidth + j]), 2);
				}
			}
			if (first){
				min = d01[idx];
				first = false;
			}
			if (d01[idx] < min){
				min = d01[idx];
				minX = x;
			}
			idx += 1;
		}
		
		if (minX + widths[1] <= widths[0]){
			JOptionPane.showMessageDialog(parent, "匹配失败！\n宽度问题。");
			return null;
		}

		final int width2 = minX + widths[1];
		int[] res = new int[width2 * height];
		
		for (int i = 0; i < height; i++){
			for (int j = 0; j < minX; j++){
				res[i * width2 + j] = colors[0][i * widths[0] + j];
			}
		}
		
		int width5 = widths[0] - minX;
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width5; j++){
				int colour = 0;
				int tempColor0 =  colors[0][i * widths[0] + j + minX];
				int tempColor1 = colors[1][i * widths[1] + j];
				//TODO
//				colour = (tempColor0 & 0xff) + (tempColor1 & 0xff) / 2;
//				colour = (colour << 8) | ((((tempColor0 >> 8) & 0xff) + ((tempColor1 >> 8) & 0xff)) / 2);
//				colour = (colour << 8) | ((((tempColor0 >> 16) & 0xff) + ((tempColor1 >> 16) & 0xff)) / 2);
				if (tempColor0 > tempColor1)
					colour = tempColor0;
				else
					colour = tempColor1;
				res[i * width2 + j + minX] = colour;
			}
		}
		
		for (int i = 0; i < height; i++){
			for (int j = 0; j < widths[1] - widths[0] + minX; j++){
				res[i * width2 + j + widths[0]] = colors[1][i * widths[1] + j + width5];
			}
		}
		
		tempWidth = widths[2] / 5;	//模板宽度
		byte[] color2 = new byte[tempWidth * height];
		
		//得到模板的所有像素存到color2
		for (int i = 0; i < height; i++){
			for (int j = 0; j < tempWidth; j++){
				color2[i * tempWidth + j] = color[2][i * widths[2] + j];
			}
		}
		
		double[] d02 = new double[(width2 - tempWidth) / 2 + 1];
		Arrays.fill(d02, 0);
		idx = 0;
		min = 1000;
		first = true;
		minX = width2 - tempWidth;
		byte[] newColor = new byte[res.length];
		
		for (int i = 0; i < res.length; i++){
			newColor[i] = ColorProcess.getByteByGrayLevel(8, res[i]);
		}
		
		for (int x = width2 - tempWidth; x >=0; x-=2){
			for (int i = 0; i < height; i++){
				for (int j = 0; j < tempWidth; j++){
					d02[idx] += 
					Math.abs(newColor[i * width2 + j + x] - color2[i * tempWidth + j]);
				}
			}
			if (first){
				min = d02[idx];
				first = false;
			}
			if (d02[idx] < min){
				min = d02[idx];
				minX = x;
			}
			idx += 1;
		}
		
		if (minX + widths[2] <= width2){
			JOptionPane.showMessageDialog(parent, "匹配失败！\n宽度问题。");
			return null;
		}

		int width3 = minX + widths[2];
		int[] ret = new int[width3 * height];
		
		for (int i = 0; i < height; i++){
			for (int j = 0; j < minX; j++){
				ret[i * width3 + j] = res[i * width2 + j];
			}
		}
		
		width5 = width2 - minX;
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width5; j++){
				int colour = 0;
				int tempColor0 =  res[i * width2 + j + minX];
				int tempColor1 = colors[2][i * widths[2] + j];
				//TODO
//				colour = (tempColor0 & 0xff) + (tempColor1 & 0xff) / 2;
//				colour = (colour << 8) | ((((tempColor0 >> 8) & 0xff) + ((tempColor1 >> 8) & 0xff)) / 2);
//				colour = (colour << 8) | ((((tempColor0 >> 16) & 0xff) + ((tempColor1 >> 16) & 0xff)) / 2);
				if (tempColor0 > tempColor1)
					colour = tempColor0;
				else
					colour = tempColor1;
				ret[i * width3 + j + minX] = colour;
			}
		}
		
		for (int i = 0; i < height; i++){
			for (int j = 0; j < widths[2] - width2 + minX; j++){
				ret[i * width3 + j + width2] = colors[2][i * widths[2] + j + width5];
			}
		}
		
		BufferedImage result = new BufferedImage(width3, height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, width3, height, ret, 0, width3);
		return result;
	}
}
