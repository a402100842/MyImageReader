package com.btf.imgIO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.btf.utils.Utils;

public class BitmapHeader {

    public int nsize;	
    public int nbisize;	
    public int nwidth;	
    public int nheight;	
    public int nplanes;
    public int nbitcount;
    public int ncompression;
    public int nsizeimage;	
    public int nxpm;
    public int nypm;
    public int nclrused;
    public int nclrimp;
    
    public BitmapHeader(){
    	
    }
	
	/**
     * this method does not initialize 
     * (nsize, nwidth, nheight, nsizeimage)
     * @param pbitcount
     */
    public BitmapHeader(int pbitcount){
    	nbisize = 0x28;
		nplanes = 0x01;
		
		if (pbitcount == 24)
			nbitcount = 0x18;//up to now we assume it is 24-bit Color
		else if (pbitcount == 32)
			nbitcount = 0x20;
		else if (pbitcount == 8)
			nbitcount = 0x08;
		else if (pbitcount == 4)
			nbitcount = 0x04;
		else if (pbitcount == 1)
			nbitcount = 0x01;
		
		ncompression = 0;
		
		nxpm = 0xb12;
		nypm = 0xb12;
		nclrused = 0;
		nclrimp = 0;
    }
    
    
    // read in the bitmap header
    public void read(FileInputStream fs) throws IOException
    {
        final int bflen = 14; // 14 byte BITMAPFILEHEADER
        byte bf[] = new byte[bflen];
        fs.read(bf, 0, bflen);
        
        final int bilen = 40; // 40-byte BITMAPINFOHEADER
        byte bi[] = new byte[bilen];
        fs.read(bi, 0, bilen);

        // Interpret data.
        nsize = Utils.constructInt(bf, 2);

        //  System.out.println("File type is :"+(char)bf[0]+(char)bf[1]);
        //  System.out.println("Size of file is :"+nsize);

        nbisize = Utils.constructInt(bi, 0);

        //   System.out.println("Size of bitmapinfoheader is :"+nbisize);

        nwidth = Utils.constructInt(bi, 4);

        //   System.out.println("Width is :"+nwidth);

        nheight = Utils.constructInt(bi, 8);

        //  System.out.println("Height is :"+nheight);

        nplanes = Utils.constructShort(bi, 12); //(((int)bi[13]&0xff)<<8) |
        // (int)bi[12]&0xff;

        //  System.out.println("Planes is :"+nplanes);

        nbitcount = Utils.constructShort(bi, 14); //(((int)bi[15]&0xff)<<8) |
        // (int)bi[14]&0xff;

        //  System.out.println("BitCount is :"+nbitcount);

        // Look for non-zero values to indicate compression
        ncompression = Utils.constructInt(bi, 16);

        //  System.out.println("Compression is :"+ncompression);

        nsizeimage = Utils.constructInt(bi, 20);

        //  System.out.println("SizeImage is :"+nsizeimage);

        nxpm = Utils.constructInt(bi, 24);

        // System.out.println("X-Pixels per meter is :"+nxpm);

        nypm = Utils.constructInt(bi, 28);

        // System.out.println("Y-Pixels per meter is :"+nypm);

        nclrused = Utils.constructInt(bi, 32);

        //  System.out.println("Colors used are :"+nclrused);

        nclrimp = Utils.constructInt(bi, 36);

        //  System.out.println("Colors important are :"+nclrimp);

    }
    
	/**
	 * with padding
	 * @param str file path
	 * @param color int_rgb
	 * @throws IOException
	 */
	public byte[] toPaddingBytes(int[] color) throws IOException{
        int npad = (nsizeimage / nheight) - nwidth * 3;
		int idx = 0;
        byte[] rgb = new byte[nsizeimage];
        
        for (int i = 0; i < nheight; i++){
			for (int j = 0; j < nwidth; j++){
				int int_rgb = color[nwidth * (nheight - i - 1) + j];
				rgb[idx++] = (byte) (int_rgb & 0xff);
				rgb[idx++] = (byte) ((int_rgb >> 8) & 0xff) ;
				rgb[idx++] = (byte) ((int_rgb >> 16) & 0xff);
			}
			for (int k = 0; k < npad; k++){
				rgb[idx++] = 0;
			}
		}
        
        return rgb;
	}
    
	/**
	 * 
	 * @param fs file path
	 * @param b	bytes with padding!
	 * @throws IOException
	 */
    public void write(FileOutputStream fs, byte[] b) throws IOException{
    	fs.write(0x42);
    	fs.write(0x4d);
    	Utils.writeInt(fs, nsize);
    	Utils.writeInt(fs, 0);
    	Utils.writeInt(fs, 54);
    	
    	Utils.writeInt(fs, 40);
    	Utils.writeInt(fs, nwidth);
    	Utils.writeInt(fs, nheight);
    	Utils.writeShort(fs, nplanes);
    	Utils.writeShort(fs, nbitcount);
    	Utils.writeInt(fs, ncompression);
    	Utils.writeInt(fs, nsizeimage);
    	Utils.writeInt(fs, nxpm);
    	Utils.writeInt(fs, nypm);
    	Utils.writeInt(fs, nclrused);
    	Utils.writeInt(fs, nclrimp);
    	
    	fs.write(b);
    	fs.close();
    }
    
    
}
