package com.btf.graphics;

import java.awt.image.BufferedImage;

public class Charts {
	
	public static BufferedImage histogram_equalization_average(BufferedImage img){
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		//�õ�ԭͼ����
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		//����һ��������������ܶȵ����鲢��ʼ��Ϊ0
		float[][] p = new float[3][];
		for (int i = 0; i < p.length; i++){
			p[i] = new float[256];
			for (int j = 0; j < 256; j++)
				p[i][j] = 0;
		}
		
		//����һ����������ԭͼRGBֵ������
		int[][] rgb = new int[3][color.length];
		
		//��������ܶ�ͬʱ��RGB�������
		for (int i = 0; i < color.length; i++){
			rgb[2][i] = color[i] & 0x000000ff;
			p[2][rgb[2][i]] += 1;
			
			rgb[1][i] = (color[i] >> 8) & 0x000000ff;
			p[1][rgb[1][i]] += 1;
			
			rgb[0][i] = (color[i] >> 16) & 0x000000ff;
			p[0][rgb[0][i]] += 1;
		}
		for (int i = 0; i < p.length; i++){
			p[0][i] = (p[0][i] + p[1][i] + p[2][i]) / color.length / 3;
		}
		
		//����һ����������ӳ��֮��RGBֵ������
		int[] palette = new int[256];
		//����ֱ��ͼ���⻯֮���RGB��Ӧֵ
		float temp = 0;
		for (int j = 0; j < 256; j++){
			temp += p[0][j];
			palette[j] = (int) (temp * 255);
		}
		
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < color.length; j++){
				rgb[i][j] = palette[rgb[i][j]];
			}
		}
		
		for (int i = 0; i < color.length; i++){
			color[i] = (rgb[0][i] << 16) | (rgb[1][i] << 8) | rgb[2][i];
		}
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, width, height, color, 0, width);
		
		return result;
	
		
	}
	
	public static BufferedImage histogram_equalization(BufferedImage img){
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		//�õ�ԭͼ����
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		//����һ��������������ܶȵ����鲢��ʼ��Ϊ0
		float[][] p = new float[3][];
		for (int i = 0; i < p.length; i++){
			p[i] = new float[256];
			for (int j = 0; j < 256; j++)
				p[i][j] = 0;
		}
		
		//����һ����������ԭͼRGBֵ������
		int[][] rgb = new int[3][color.length];
		
		//��������ܶ�ͬʱ��RGB�������
		for (int i = 0; i < color.length; i++){
			rgb[2][i] = color[i] & 0x000000ff;
			p[2][rgb[2][i]] += 1;
			
			rgb[1][i] = (color[i] >> 8) & 0x000000ff;
			p[1][rgb[1][i]] += 1;
			
			rgb[0][i] = (color[i] >> 16) & 0x000000ff;
			p[0][rgb[0][i]] += 1;
		}
		for (int i = 0; i < p.length; i++){
			for (int j = 0; j < 256; j++)
				p[i][j] /= color.length;
		}
		
		//����һ����������ӳ��֮��RGBֵ������
		int[][] palette = new int[3][256];
		//����ֱ��ͼ���⻯֮���RGB��Ӧֵ
		for (int i = 0; i < 3; i++){
			float temp = 0;
			for (int j = 0; j < 256; j++){
				temp += p[i][j];
				palette[i][j] = (int) (temp * 255);
			}
		}
		
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < color.length; j++){
				rgb[i][j] = palette[i][rgb[i][j]];
			}
		}
		
		for (int i = 0; i < color.length; i++){
			color[i] = (rgb[0][i] << 16) | (rgb[1][i] << 8) | rgb[2][i];
		}
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		result.setRGB(0, 0, width, height, color, 0, width);
		
		return result;
	}
	
	public static float[][] histogram(BufferedImage img){
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		//�õ�ԭͼ����
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		float[][] res = new float[3][];
		for (int i = 0; i < res.length; i++){
			res[i] = new float[256];
			for (int j = 0; j < 256; j++)
				res[i][j] = 0;
		}
		
//		int[] red = new int[color.length];
//		int[] green = new int[color.length];
//		int[] blue = new int[color.length];
		for (int i = 0; i < color.length; i++){
//			blue[i] = color[i] & 0x000000ff;
//			green[i] = (color[i] >> 8) & 0x000000ff;
//			red[i] = (color[i] >> 16) & 0x000000ff;
			res[2][color[i] & 0x000000ff] += 1;
			res[1][(color[i] >> 8) & 0x000000ff] += 1;
			res[0][(color[i] >> 16) & 0x000000ff] += 1;
		}
		
		for (int i = 0; i < res.length; i++){
			for (int j = 0; j < 256; j++)
				res[i][j] /= color.length;
		}
		
		return res;
	}
	
	/**
	 * ����������ܶ�
	 * @param img
	 * @return ����int[][]�еĵ�4����������������
	 */
	public static int[][] histogram2(BufferedImage img){
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		//�õ�ԭͼ����
		int[] color = new int[width * height];
		img.getRGB(0, 0, width, height, color, 0, width);
		
		int[][] res = new int[4][];
		for (int i = 0; i < res.length - 1; i++){
			res[i] = new int[256];
			for (int j = 0; j < 256; j++)
				res[i][j] = 0;
		}
		res[3] = new int[1];
		res[3][0] = width * height;
		
//		int[] red = new int[color.length];
//		int[] green = new int[color.length];
//		int[] blue = new int[color.length];
		for (int i = 0; i < color.length; i++){
//			blue[i] = color[i] & 0x000000ff;
//			green[i] = (color[i] >> 8) & 0x000000ff;
//			red[i] = (color[i] >> 16) & 0x000000ff;
			res[2][color[i] & 0x000000ff] += 1;
			res[1][(color[i] >> 8) & 0x000000ff] += 1;
			res[0][(color[i] >> 16) & 0x000000ff] += 1;
		}
		
//		for (int i = 0; i < res.length; i++){
//			for (int j = 0; j < 256; j++)
//				res[i][j] /= color.length;
//		}
		
		return res;
	}
}
