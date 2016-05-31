package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.w3c.dom.Document;

/**
 * @author Luca Frosini (ISTI-CNR)
 */
public class FileUtilsExtended {
	
	/** 
	 * Class logger. 
	 */
	protected static final GCUBELog logger = new GCUBELog(FileUtilsExtended.class);

	/**
	 * Remove Recursively a directory
	 * @param directory to remove
	 * @return true is the directory is successully removed
	 */
	public static boolean recursiveDeleteDirectory(final File directory){
		if(directory.exists() && directory.isDirectory()){
			File[] list = directory.listFiles();

			for(File item : list){
				if(item.isFile()){
					boolean deleted = item.delete();
					if(!deleted){
						return false;
					}
				}else if(item.isDirectory()){
					if(!recursiveDeleteDirectory(item)){
						return false;
					}
				}
			}
		}else{
			return false;
		}
		return directory.delete();
	}
	
	/**
	 * @param str string
	 * @param targetFile Target File
	 * @throws IOException if fails
	 */
	public static void stringToFile(String str, File targetFile) throws IOException {
		try {
			FileWriter fw = new FileWriter(targetFile);
			fw.write(str);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 * @param absolutePath of the File
	 * @return the string representation of the content of the File
	 * @throws Exception if fails
	 */
	public static String fileToString(String absolutePath) throws Exception {
		BufferedReader filebuf = null;
		String nextStr = null;
        StringBuilder ret = new StringBuilder();
        try {
        	filebuf = new BufferedReader(new FileReader(absolutePath));
			nextStr = filebuf.readLine(); // Read a line from file
			while (nextStr != null) {
	        	ret.append(nextStr);
				nextStr = filebuf.readLine(); // Read next line 
	        }
			filebuf.close(); // Close File
		} catch (FileNotFoundException e) {
			logger.error(e);
			throw e;
		} catch (IOException e1) {
			logger.error(e1);
			throw e1;
		}
		return ret.toString();
	}
	
	/**
	 * @return string rapresentaton of the name of a directory in the format DD-MM-YYYY
	 */
	public static String dateDirectory(){
		Calendar calendar = new GregorianCalendar();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String dayString = (day<10?"0"+day:""+day);
		/* Month start from 0 in Calendar specification*/
		int month = calendar.get(Calendar.MONTH)+1;
		String monthString = (month<10?"0"+month:""+month);
		int year = calendar.get(Calendar.YEAR);
		String dateDir = dayString+"-"+monthString+"-"+year;
		return dateDir;
	}
	
	
	/**
	 * This method writes a DOM document to a file
	 */
	
	/**
	 * @param document xml document
	 * @param file output file
	 * @throws Exception if fails
	 */
	public static void writeXmlToFile(Document document, File file) throws Exception {
		try {
			// Prepare the DOM document for writing
			Source source = new DOMSource(document);
			
			Result result = new StreamResult(new FileOutputStream(file));
			
			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);
		} catch (Exception e) {
			logger.error("Error while serializing xml on file",e);
			throw e;
		}
		
	}
	
	/**
	 * Copy the source file to the the target file
	 * @param source source file
	 * @param target target file
	 * @throws Exception if fails
	 */
	public static void copyFile(File source, File target) throws Exception {
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(target);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
}
