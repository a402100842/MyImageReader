package com.btf.imgProcess;


import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.btf.utils.Utils;

public class Noise {
	
	public static BufferedImage gaussian(BufferedImage img, double d, double mean){
		if (img == null)
			return null;
		
		ArrayList<Double> a = new ArrayList<Double>();
		ArrayList<Integer> b = new ArrayList<Integer>();
		double k1 = 1 / (Math.sqrt(2 * Math.PI) * d);
		double k2 = 2 * d * d;
		double p;
		double sum = 0;
		int low = (int) (mean - 3 * d);
		int high = (int) (mean + 3 * d);
		
		//计算【mean-3d, mean+3d】中每个值对应的概率
		for (int z = low; z < high; z++){
			p = k1 * Math.exp(-(z - mean) * (z - mean) / k2);
			sum += p;
//			System.out.println(p);
			if (p != 0){
				a.add(sum);
				b.add(z);
			}
		}
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);

		//得到原图像素
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		for (int i = 0; i < color.length; i++){
			color[i] = Utils.byte2int(ColorProcess.getByteByGrayLevel(8, color[i]));
		}
		
		for (int j = 0; j < color.length; j++){
			p = Math.random();
//			System.out.println(p);
			for (int i = 0 ; i < a.size(); i++){
				if (p < a.get(i)){
					color[j] += b.get(i);
					if (color[j] < 0)
						color[j] = 0;
					else if (color[j] > 255)
						color[j] = 255;
					break;
				}
			}
		}
		
		for (int j = 0; j < color.length; j++)
			color[j] = ColorProcess.int2int(color[j]);
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, width, height, color, 0, width);
		return result;
	}
	
	public static BufferedImage salt_and_pepper(BufferedImage img, double pa, double pb){
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		//得到原图像素
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		double p;
		double pc = pa + pb;
		for (int j = 0; j < color.length; j++){
			p = Math.random();
//			System.out.println(p);
			if (p < pa){
				color[j] = 0;
			} else if (p < pc){
				color[j] = 0xffffffff;
			}
		}
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, width, height, color, 0, width);
		return result;
	}
}
