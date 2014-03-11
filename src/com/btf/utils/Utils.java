package com.btf.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {
	public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static String bmp = "bmp";
    
    public static int[] scaling(int[] data){
    	if (data == null || data.length == 0)
    		return data;
    	
    	int min, max;
    	min = max = data[0];
    	//计算出min, max
    	for (int i = 0; i < data.length; i++){
    		if (data[i] < min)
    			min = data[i];
    		else if (data[i] > max)
    			max = data[i];
    	}
    	
    	//将最小值拉回0
    	if (min != 0){
    		min = 0;
    		max -= min;
    		for (int i = 0; i < data.length; i++){
    			data[i] -= min;
    		}
    	}
    	
    	if (max == 0)
    		return data;
    	
    	float delta = 255.0f / (max - min);//max - min == 0?
    	for (int i = 0; i < data.length; i++){
    		data[i] *= delta;
    	}
    	return data;
    }
    
    public static PositionUtils parse(String s){
    	//检查maskHeight是否大于等于3
		String[] strRow = s.split(";");
		int maskHeight = strRow.length;
		if (maskHeight < 3){
			raiseError();
			return null;
		}
		
		//检查maskWidth是否等于maskHeight
		String[] strCol = strRow[0].split(",");
		int maskWidth = strCol.length;
		if (maskWidth != maskHeight){
			raiseError();
			return null;
		}
		
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for (String st : strCol)
			ints.add(Integer.parseInt(st));
		
		for (int i = 1; i < maskHeight; i++){
			strCol = strRow[i].split(",");
			if (strCol.length != maskWidth){
				raiseError();
				return null;
			}
			for (String st : strCol)
				ints.add(Integer.parseInt(st));
		}
		
		int[] datas = new int[ints.size()];
		for (int i = 0; i < ints.size(); i++){
			datas[i] = ints.get(i);
		}
		
		return new PositionUtils(maskWidth, maskHeight, datas);
    }
    
    public static int byte2int(byte b){
    	int ret = 0x000000ff & b;
    	return ret;
    }
    
    public static byte int2byte(int i){
    	i &= 0x000000ff;
    	return (byte)i;
    }
    
    public static String getFileNameWithoutExtension(File f){
    	String ext = null;
    	String s = f.getName();
    	int i = s.lastIndexOf('.');
    	
    	if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(0, i);
        }
        return ext;
    }

    /*
     * Get the extension of a file.
     */  
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
 // build an int from a byte array - convert little to big endian
 	public static int constructInt(byte[] in, int offset) {
 	    int ret = ((int) in[offset + 3] & 0xff);
 	    ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
 	    ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
 	    ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
 	    
 	    return (ret);
 	}

 	// build an int from a byte array - convert little to big endian
     // set high order bytes to 0xfff
     public static int constructInt3(byte[] in, int offset) {
         int ret = 0xff;
         ret = (ret << 8) | ((int) in[offset + 2] & 0xff);
         ret = (ret << 8) | ((int) in[offset + 1] & 0xff);
         ret = (ret << 8) | ((int) in[offset + 0] & 0xff);
         
         return (ret);
     }

     // build an int from a byte array - convert little to big endian
     public static long constructLong(byte[] in, int offset) {
         long ret = ((long) in[offset + 7] & 0xff);
         ret |= (ret << 8) | ((long) in[offset + 6] & 0xff);
         ret |= (ret << 8) | ((long) in[offset + 5] & 0xff);
         ret |= (ret << 8) | ((long) in[offset + 4] & 0xff);
         ret |= (ret << 8) | ((long) in[offset + 3] & 0xff);
         ret |= (ret << 8) | ((long) in[offset + 2] & 0xff);
         ret |= (ret << 8) | ((long) in[offset + 1] & 0xff);
         ret |= (ret << 8) | ((long) in[offset + 0] & 0xff);
         
         return (ret);
     }

     // build an double from a byte array - convert little to big endian
     public static double constructDouble(byte[] in, int offset) {
         long ret = constructLong(in, offset);
         
         return (Double.longBitsToDouble(ret));
     }

     // build an short from a byte array - convert little to big endian
     public static short constructShort(byte[] in, int offset) {
         short ret = (short) ((short) in[offset + 1] & 0xff);
         ret = (short) ((ret << 8) | (short) ((short) in[offset + 0] & 0xff));
         
         return (ret);
     }

     public static void writeInt(FileOutputStream fs, int in) throws IOException{
     	byte[]  res = new byte[4];
     	res[0] = (byte) (in & 0xff);
     	res[1] = (byte) ((in >> 8) & 0xff);
     	res[2] = (byte) ((in >>16) & 0xff);
     	res[3] = (byte) ((in >> 24) & 0xff);
     	fs.write(res);
     }
     
     public static void writeShort(FileOutputStream fs, int in) throws IOException{
     	byte[]  res = new byte[2];
     	res[0] = (byte) (in & 0xff);
     	res[1] = (byte) ((in >> 8) & 0xff);
     	fs.write(res);
     }
     
     private static void raiseError() {
 		System.out.println("Mask size invalid!");
 	}
}
