package com.btf.imgIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.btf.utils.Utils;


public class BMPIO {

	public static BufferedImage myRead(String str) throws IOException {
		FileInputStream file = new FileInputStream(str);
		try {
            BitmapHeader bh = new BitmapHeader();
            bh.read(file);
            if (bh.nbitcount == 8)
                return (readBitMap8(file, bh));
            if (bh.nbitcount == 24)
                return (readBitMap24(file, bh));
            if (bh.nbitcount == 32)
                return (readBitMap32(file, bh));        

            file.close();
            
        } catch (IOException e) {
            System.out.println("Caught exception in loading bitmap!");
        }
		return null;
	}

	public static BufferedImage myWrite(BufferedImage img, String str) throws IOException {
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		int[] color = new int[width * height];
		BitmapHeader bh = getPixels(img, color, 24);
		
		if (bh != null){
			byte[] rgb = bh.toPaddingBytes(color);
			File file = new File(str);
            file.createNewFile();
            FileOutputStream fs = new FileOutputStream(file);
            bh.write(fs, rgb);
		}

		return img;
	}

	private static BitmapHeader getPixels(BufferedImage img, int[] color, int bitcount){
		if (img == null)
			return null;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		img.getRGB(0, 0, width, height, color, 0, width);
		
		if (bitcount != 24)
			return null;
		BitmapHeader bh = new BitmapHeader(bitcount);
		bh.nwidth = width;
		bh.nheight = height;
        bh.nsizeimage = ((((bh.nwidth * bh.nbitcount) + 31) & ~31) >> 3);
        bh.nsizeimage *= bh.nheight;
        bh.nsize = bh.nsizeimage + 54;
        return bh;
	}

	private static BufferedImage readBitMap32(FileInputStream file, BitmapHeader bh)  throws IOException{
		if (bh.nsizeimage == 0){
            bh.nsizeimage = ((((bh.nwidth * bh.nbitcount) + 31) & ~31) >> 3);
            bh.nsizeimage *= bh.nheight;
		}
		int result[] = new int[bh.nheight * bh.nwidth];
		byte[] argb = new byte[bh.nheight * bh.nwidth * 4];
		file.read(argb);
		int idx = 0;
		for (int i = 0; i < bh.nheight; i++)
			for (int j = 0; j < bh.nwidth; j++){
				result[bh.nwidth * (bh.nheight - i - 1) + j] = 
						Utils.constructInt(argb,idx);
				idx += 4;
			}
		
		BufferedImage img = new BufferedImage(bh.nwidth, bh.nheight, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, bh.nwidth, bh.nheight, result, 0, bh.nwidth);
		file.close();
		return img;
	}


	private static BufferedImage readBitMap24(FileInputStream file, BitmapHeader bh)  throws IOException{
		if (bh.nsizeimage == 0){
            bh.nsizeimage = ((((bh.nwidth * bh.nbitcount) + 31) & ~31) >> 3);
            bh.nsizeimage *= bh.nheight;
		}
		int result[] = new int[bh.nheight * bh.nwidth];
		int npad = (bh.nsizeimage / bh.nheight) - bh.nwidth * 3;
		byte[] rgb = new byte[(bh.nwidth * 3 + npad) * bh.nheight];
		file.read(rgb);
		int idx = 0;
		for (int i = 0; i < bh.nheight; i++){
			for (int j = 0; j < bh.nwidth; j++){
				result[bh.nwidth * (bh.nheight - i - 1) + j] = 
						Utils.constructInt3(rgb,idx);
				idx += 3;
			}
			idx += npad;
		}
		
		BufferedImage img = new BufferedImage(bh.nwidth, bh.nheight, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, bh.nwidth, bh.nheight, result, 0, bh.nwidth);
		file.close();
		return img;
	}


	private static BufferedImage readBitMap8(FileInputStream file, BitmapHeader bh)  throws IOException{
		
		int nNumColors = 0;
		if (bh.nclrused > 0)
            nNumColors = bh.nclrused;
		else
			nNumColors = (1 & 0xff) << bh.nbitcount;
		if (bh.nsizeimage == 0){
            bh.nsizeimage = ((((bh.nwidth * bh.nbitcount) + 31) & ~31) >> 3);
            bh.nsizeimage *= bh.nheight;
		}
		int npalette[] = new int[nNumColors];
		byte bpalette[] = new byte[nNumColors * 4];
		file.read(bpalette);
		int idx = 0;
		for (int i = 0; i < nNumColors; i++){
			npalette[i] = Utils.constructInt3(bpalette,idx);
			idx += 4;
		}
		
		int npad = (bh.nsizeimage / bh.nheight) - bh.nwidth;
		int result[] = new int[bh.nheight * bh.nwidth];
		byte color[] = new byte[(bh.nwidth + npad) * bh.nheight];
		file.read(color);
		idx = 0;
		for (int i = 0; i < bh.nheight; i++){
			for (int j = 0; j < bh.nwidth; j++){
				result[bh.nwidth * (bh.nheight - i - 1) + j] = 
						npalette[((int) color[idx] & 0xff)];
				idx++;
			}
			idx += npad;
		}
		BufferedImage img = new BufferedImage(bh.nwidth, bh.nheight, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, bh.nwidth, bh.nheight, result, 0, bh.nwidth);
//		img = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(bh.nwidth, bh.nheight,
//		        result, 0, bh.nwidth));
		file.close();
		return img;
	}

}
