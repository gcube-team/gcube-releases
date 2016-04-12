package org.gcube.datatransfer.agent.impl.utils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * 
 * @author andrea
 *
 */
public class GdalConverter {

	static GCUBELog logger = new GCUBELog(GdalConverter.class);
	
	
	static String gdalExecutorWin = "C:/Program Files (x86)/GDAL/gdal_translate";
	static String gdalExecutorLin = "/usr/bin/gdal_translate";
	
	public static boolean convertToGeoTiff(String fullPathToFile){
		String gdalConverter = "";
		if (fullPathToFile.endsWith("tiff"))
			return true;
		
		if (System.getProperty("os.name").contains("Win"))
			gdalConverter = gdalExecutorWin;
		else
			gdalConverter = gdalExecutorLin;
		
		logger.debug("Executing transformation in "+System.getProperty("os.name")+"->"+gdalConverter);
		
		int pointIndex = fullPathToFile.lastIndexOf(".");
		if (pointIndex<0)
			pointIndex = fullPathToFile.length();
		
		String geoTiffFile = fullPathToFile.substring(0,pointIndex)+".tiff";
		
		String executionResult =  ExecuteGetLine(gdalConverter+" -of GTiff "+fullPathToFile+" "+geoTiffFile);
		if (executionResult.equalsIgnoreCase("error"))
			return false;
		else
			return true;
	}
	
	public static String ExecuteGetLine(String cmd){
			
		 Process process = null;
		 String lastline = "";
		 try {
			 logger.debug("OSCommand-> Executing Control ->"+cmd);
			 
			 process = Runtime.getRuntime().exec(cmd);
			 
			 BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			 String line = br.readLine();
			 logger.debug("OSCommand->  line->"+line);
			 while (line!=null){
				 try{
				 lastline = line;
				 logger.debug("OSCommand-> line->"+line);
				 line = br.readLine();
				 }catch(EOFException e){
					 logger.error("OSCommand -> Process Finished with EOF");
					 break;
				 }
				 catch(Exception e){
					 line = "ERROR";
					 break;
				 }
			 }
			 logger.debug("OSCommand -> Process Finished");
		} catch (Throwable e) {
			logger.error("OSCommand-> error ");
			 e.printStackTrace();
			 lastline = "ERROR";
		}
		 process.destroy();
		 logger.debug("OSCommand-> Process destroyed ");
		 return lastline;
	 } 
	 
	 public static boolean FileCopy (String origin,String destination){
		 try{
			 
		 File inputFile = new File(origin);
		 logger.debug("OSCommand-> FileCopy-> "+inputFile.length()+" to "+inputFile.canRead());
		 int counterrors=0;
		 while ((inputFile.length()==0)&&(counterrors<10)){
			 Thread.sleep(20);
			 counterrors++;
		 }
		 
		    File outputFile = new File(destination);

		    FileReader in = new FileReader(inputFile);
		    FileWriter out = new FileWriter(outputFile);
		    int c;

		    while ((c = in.read()) != -1)
		      out.write(c);

		    in.close();
		    out.close();
		    return true;
		 }catch(Exception e){
			 e.printStackTrace();
			 return false;
		 }
	 }
}
