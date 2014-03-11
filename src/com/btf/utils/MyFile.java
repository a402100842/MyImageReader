package com.btf.utils;

import java.io.File;

public class MyFile implements Comparable<MyFile>{
	public File file;
	public float similarity;

	public MyFile(File f, float s) {
		file = f;
		similarity = s;
	}

	@Override
	public int compareTo(MyFile o) {
		if (this.similarity > o.similarity)
			return 1;
		else if (this.similarity < o.similarity)
			return -1;
		else
			return 0;
	}

	
}
