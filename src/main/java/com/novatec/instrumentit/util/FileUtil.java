package com.novatec.instrumentit.util;

import java.io.File;

public class FileUtil {
	
	public static String getExtensionOfFile(File file) {
		String fileExtension = "";
		String fileName = file.getName();
		
		if(fileName.contains(".") && fileName.lastIndexOf(".") != 0) {
			fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
		}
		
		return fileExtension;
	}
	
	public static boolean equalFileExtension(File f, String extension) {
		return FileUtil.getExtensionOfFile(f).equals(extension);
	}

}
