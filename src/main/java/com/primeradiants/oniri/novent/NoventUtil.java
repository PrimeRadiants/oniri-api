package com.primeradiants.oniri.novent;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class NoventUtil {
	
	public final static String NOVENT_EXTENSION = "novent";

	public static boolean isValidCoverImg(File cover) {
		try {
			Image image = ImageIO.read(cover);
			if(image == null)
				return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidNoventFile(File novent) {
		if(getFileExtension(novent).toLowerCase().equals(NOVENT_EXTENSION))
			return true;
		
		return false;
	}
	
	private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
}
