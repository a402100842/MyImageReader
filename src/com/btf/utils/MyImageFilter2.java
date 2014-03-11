package com.btf.utils;

import java.io.File;

public class MyImageFilter2 implements java.io.FileFilter{

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return false;
		}

		String extension = Utils.getExtension(f);
		if (extension != null) {
			if (extension.equals(Utils.gif)
//					|| extension.equals(Utils.tiff) || extension.equals(Utils.tif)
					|| extension.equals(Utils.jpeg)
					|| extension.equals(Utils.jpg)
					|| extension.equals(Utils.png)
					|| extension.equals(Utils.bmp)) {
				return true;
			}
		}

		return false;
	}

}
