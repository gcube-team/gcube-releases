package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceUtils {

	private static DateFormat dateFormatter= new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS_z");
	private static DateFormat shortDateFormatter=new SimpleDateFormat("yyyy_MM_dd");
	final static Logger logger= LoggerFactory.getLogger(ServiceUtils.class);

	public static String fileToString(String path) throws IOException {


		BufferedReader filebuf = null;
		String nextStr = null;
		StringBuilder ret = new StringBuilder();

		filebuf = new BufferedReader(new FileReader(path));
		nextStr = filebuf.readLine(); // legge una riga dal file
		while (nextStr != null) {
			ret.append(nextStr);
			nextStr = filebuf.readLine(); // legge la prossima riga 
		}
		filebuf.close(); // chiude il file 

		return ret.toString();
	}

	public static String URLtoString(String path) throws IOException{
		URL yahoo = new URL(path);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						yahoo.openStream()));

		String inputLine;
		StringBuilder toReturn=new StringBuilder(); 


		while ((inputLine = in.readLine()) != null)
			toReturn.append(inputLine);

		in.close();
		return toReturn.toString();
	}


	public static void deleteFile(String path) throws IOException {		
		File f=new File(path);
		if(f.exists()){
			if(f.isDirectory()){
				logger.info("Deleting directory "+path);
				FileUtils.cleanDirectory(f);
				FileUtils.deleteDirectory(f);
			}else{
				logger.info("Deleting file "+path);
				File dir = f.getParentFile();
				FileUtils.forceDelete(f);
				if(dir.list().length==0){
					logger.info("Deleting empty parent "+dir.getAbsolutePath());
					FileUtils.deleteDirectory(dir);
				}
			}		 
		}else logger.info("Path not found "+path);

	}

	public static String generateId(String prefix,String suffix){
		//		return prefix+(uuidGen.nextUUID()).replaceAll("-", "_")+suffix;
		return prefix+getTimeStamp().replaceAll("-", "_")+suffix;
	}
	public static String getTimeStamp(){
		return formatTimeStamp(System.currentTimeMillis());
	}
	public static String getDate(){
		return shortDateFormatter.format(System.currentTimeMillis());
	}
	public static String formatTimeStamp(long time){
		return dateFormatter.format(time);
	}

}
