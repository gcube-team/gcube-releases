package org.gcube.dataanalysis.geo.utils;


import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class GdalConverter {

	public static void main1(String[] args){
//		-projwin -10 10 10 -10
		System.out.println(convertToGeoTiff("p_edulis_map.img"));
	}
	
	public static void main(String[] args){
		System.out.println(convertToGeoTiff(args[0]));
	}
	static String gdalExecutorWin = "C:/Program Files (x86)/GDAL/gdal_translate";
	static String gdalExecutorLin = "/usr/bin/gdal_translate";
	
	public static String convertToGeoTiff(String fullPathToFile){
		String gdalConverter = "";
		if (fullPathToFile.endsWith("tiff"))
			return fullPathToFile;
		
		if (System.getProperty("os.name").contains("Win"))
			gdalConverter = gdalExecutorWin;
		else
			gdalConverter = gdalExecutorLin;
		
		System.out.println("Executing transformation in "+System.getProperty("os.name")+"->"+gdalConverter);
		
		int pointIndex = fullPathToFile.lastIndexOf(".");
		if (pointIndex<0)
			pointIndex = fullPathToFile.length();
		
		String geoTiffFile = fullPathToFile.substring(0,pointIndex)+".tiff";
		
		String executionResult =  ExecuteGetLine(gdalConverter+" -of GTiff "+fullPathToFile+" "+geoTiffFile);
		if (executionResult.equalsIgnoreCase("error"))
			return null;
		else
			return geoTiffFile;
	}
	
	public static String convertToASC(String fullPathToFile,int nodata){
		String gdalConverter = "";
		
		
		if (System.getProperty("os.name").contains("Win"))
			gdalConverter = gdalExecutorWin;
		else
			gdalConverter = gdalExecutorLin;
		
		System.out.println("Executing transformation in "+System.getProperty("os.name")+"->"+gdalConverter);
		
		int pointIndex = fullPathToFile.lastIndexOf(".");
		if (pointIndex<0)
			pointIndex = fullPathToFile.length();
		
		String ascTiffFile = fullPathToFile.substring(0,pointIndex)+".asc";
		
		String executionResult =  ExecuteGetLine(gdalConverter+" -of AAIGrid "+fullPathToFile+" -a_nodata "+nodata+" "+ascTiffFile);
		if (executionResult.equalsIgnoreCase("error"))
			return null;
		else
			return ascTiffFile;
	}
	
	public static String ExecuteGetLine(String cmd){
			
		 Process process = null;
		 String lastline = "";
		 try {
			 System.out.println("OSCommand-> Executing Control ->"+cmd);
			 
			 process = Runtime.getRuntime().exec(cmd);
			 
			 BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			 String line = br.readLine();
			 System.out.println("OSCommand->  line->"+line);
			 while (line!=null){
				 try{
				 lastline = line;
				 System.out.println("OSCommand-> line->"+line);
				 line = br.readLine();
				 }catch(EOFException e){
					 System.out.println("OSCommand -> Process Finished with EOF");
					 break;
				 }
				 catch(Exception e){
					 line = "ERROR";
					 break;
				 }
			 }
			 System.out.println("OSCommand -> Process Finished");
		} catch (Throwable e) {
			System.out.println("OSCommand-> error ");
			 e.printStackTrace();
			 lastline = "ERROR";
		}
		 process.destroy();
		 System.out.println("OSCommand-> Process destroyed ");
		 return lastline;
	 } 
	 
	
	 public static boolean FileCopy (String origin,String destination){
		 try{
			 
		 File inputFile = new File(origin);
		 System.out.println("OSCommand-> FileCopy-> "+inputFile.length()+" to "+inputFile.canRead());
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
