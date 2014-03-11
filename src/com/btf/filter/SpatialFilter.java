package com.btf.filter;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.btf.imgProcess.ColorProcess;
import com.btf.utils.IPixelProcess;
import com.btf.utils.PositionUtils;
import com.btf.utils.Utils;

public class SpatialFilter {
	/**将原图像素变为int灰度**/
	public static IPixelProcess pro = new IPixelProcess(){
		
		@Override
		public void process(int[] datas) {
			for (int i = 0; i < datas.length; i++)
				datas[i] = Utils.byte2int(ColorProcess.getByteByGrayLevel(8, datas[i]));
		}
	};
	
	public static BufferedImage average(BufferedImage img, int i){
		if (i < 3 || i % 2 == 0 || img == null)
			return null;
		
		int i2 = i * i;
		int[] maskData = new int[i2];
		for (int j = 0; j < maskData.length; j++)
			maskData[j] = 1;
		
		PositionUtils mask = new PositionUtils(i, i, maskData);
		PositionUtils image = PositionUtils.create(img, pro);
		int[] res = correlation(image, mask);
		
		//灰度转化为int_rgb
		for (int j = 0; j < res.length; j++){
			float k = (float)res[j] / i2;
			res[j] = ColorProcess.int2int((int) k);
		}
		
		BufferedImage result = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, image.width, image.height, res, 0, image.width);
		
		return result;
	}
		
	public static BufferedImage laplacian(BufferedImage img, String s){
		if (img == null)
			return null;
		
		int i = Integer.parseInt(s);
		int[] maskData = new int[9];
		if (i == 8){
			maskData[0] = maskData[2] = maskData[6] = maskData[8] = -1;
		} else if (i == 4){
			maskData[0] = maskData[2] = maskData[6] = maskData[8] = 0;
		} else
			return null;
		
		maskData[1] = maskData[3] = maskData[5] = maskData[7] = -1;
		maskData[4] = i+1;
		
		PositionUtils mask = new PositionUtils(3, 3, maskData);
		PositionUtils image = PositionUtils.create(img, pro);
		int[] res = correlation(image, mask);
		
		//标定到【0，255】
		Utils.scaling(res);
		//灰度转化为int_rgb
		for (int j = 0; j < res.length; j++)
			res[j] = ColorProcess.int2int(res[j]);
		
		BufferedImage result = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, image.width, image.height, res, 0, image.width);
		
		return result;
	}
	
	public static BufferedImage filter(BufferedImage img, String s) {
		if (img == null)
			return null;
		
		PositionUtils mask = Utils.parse(s);
		if (mask == null)
			return null;
		PositionUtils image = PositionUtils.create(img, pro);
		int[] res = correlation(image, mask);
		
		//标定到【0，255】
		Utils.scaling(res);
		//灰度转化为int_rgb
		for (int i = 0; i < res.length; i++)
			res[i] = ColorProcess.int2int(res[i]);
		
		BufferedImage result = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, image.width, image.height, res, 0, image.width);
		
		return result;
	}
	
	private static int[] correlation(PositionUtils img, PositionUtils mask) {
		int[] res = new int[img.width * img.height];
		for (int i = 0; i < res.length; i++)
			res[i] = 0;
		int a = (mask.height - 1) / 2;
		int b = (mask.width - 1) / 2;

		for (int y = 0; y < img.height; y++){
			for (int x = 0; x < img.width; x++){
				for (int i = -a; i < a; i++) {
					for (int j = -b; j < b; j++) {
						res[x + y * img.width] += mask.f4(i, j) * img.f2(x, i, y, j);
					}
				}
			}
		}

		return res;
	}

	public static BufferedImage median(BufferedImage img, int size){
		if (size < 3 || size % 2 == 0 || img == null)
			return null;
		
		PositionUtils image = PositionUtils.create(img, pro);
		int a = (size - 1) / 2;
		int b = (size - 1) / 2;
		int[] temp = new int[size * size];
		int[] res = new int[image.width * image.height];
		int idx;
		for (int y = 0; y < image.height; y++){
			for (int x = 0; x < image.width; x++){
				idx = 0;
				for (int i = -a; i < a; i++) {
					for (int j = -b; j < b; j++) {
						temp[idx++] = image.f2(x, i, y, j);
					}
				}
				Arrays.sort(temp);
				res[x + y * image.width] = temp[a];
			}
		}
		
		//灰度转化为int_rgb
		for (int i = 0; i < res.length; i++)
			res[i] = ColorProcess.int2int(res[i]);
		
		BufferedImage result = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, image.width, image.height, res, 0, image.width);
		return result;
	}
}
