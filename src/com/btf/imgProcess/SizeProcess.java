package com.btf.imgProcess;

import java.awt.image.BufferedImage;

public class SizeProcess {
	
	public static BufferedImage zoomOut(BufferedImage img) {
		if (img == null)
			return null;

		int width = img.getWidth(null);
		int height = img.getHeight(null);

		int width2 = width / 2;
		int height2 = height / 2;
		
		BufferedImage result = new BufferedImage(width2, height2, BufferedImage.TYPE_INT_RGB);

		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		int[] res = new int[width2 * height2];
		int idx1 = 0;
		// int idx2 = 0;

		for (int i = 0; i < height2; i++) {
			for (int j = 0; j < width2; j++) {
				idx1 = 2 * i * width + 2 * j;
				// idx2 = (2 * i + 1) * width + 2 * j;
				// res[i * width2 + j] = (color[idx1] + color[idx1+1] +
				// color[idx2] + color[idx2+1])/4;
				res[i * width2 + j] = color[idx1];
			}
		}

		result.setRGB(0, 0, width2, height2, res, 0, width2);
		return result;
	}
}
