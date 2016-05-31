package org.gcube.application.framework.oaipmh.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Toolbox {

	/**
	 * 
	 * writes on a file. it always creates a new file. if file exists, it overwrites it
	 * @param fullPath the full filepath (basepath + filename)
	 * @param content content to write on file
	 * @return true if write was successful
	 * @throws IOException
	 */
	public static boolean writeOnFile(String fullPath, String content) {
		File file = new File(fullPath);
		try{
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		}catch(IOException e){
			return false;
		}
		return true;
	}
	
	public static String dateTimeNow(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(Calendar.getInstance().getTime());
	}
	
	
}
