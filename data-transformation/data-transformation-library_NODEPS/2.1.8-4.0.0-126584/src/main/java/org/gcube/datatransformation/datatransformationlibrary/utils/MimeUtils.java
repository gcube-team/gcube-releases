package org.gcube.datatransformation.datatransformationlibrary.utils;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.PropertiesManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Helper class for mapping file extensions to mimetypes and mimetypes to file extensions.
 * </p>
 * <p>
 * The file which contains the mappings should be set in the utils.mime_mappings_file property.
 * </p>
 */
public class MimeUtils {
	
	/**
	 * Simple test.
	 * 
	 * @param args nothing.
	 */
	public static void main(String[] args) {
		System.out.println(getFileExtension("video/x-flv"));
//		System.out.println(getMimeType("body"));
	}
	
	private static HashMap<String, String> mime2ext = new HashMap<String, String>();
	
	private static HashMap<String, String> ext2mime = new HashMap<String, String>();
	
	static {
		load();
	}
	
	/**
	 * Returns the file extension of a mimetype.
	 * 
	 * @param mimeType The mimetype whose extension will be returned.
	 * @return The file extension.
	 */
	public static String getFileExtension(String mimeType){
		String ext = mime2ext.get(mimeType.toLowerCase());
		if(ext==null){
			return mimeType.split("/")[1];
		}else{
			return ext;
		}
	}
	
	/**
	 * Returns a mimetype of a file extension.
	 * @param extension The file extension whose mimetype will be returned.
	 * @return The mimetype.
	 */
	public static String getMimeType(String extension){
		String mime = ext2mime.get(extension.toLowerCase());
		if(mime==null){
			return "unknown/unknown";
		} else {
			return mime;
		}
	}
	
	private static Logger log = LoggerFactory.getLogger(MimeUtils.class);
	private static void load(){
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document resourceDoc = builder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(PropertiesManager.getPropertyValue("utils.mime_mappings_file", "dts_mime_mappings.xml")));
			NodeList mappings = resourceDoc.getElementsByTagName("mime-mapping");
			for(int i=0;i<mappings.getLength();i++){
				try {
					Element mapping = (Element)mappings.item(i);
					String extension = ((Element)mapping.getElementsByTagName("extension").item(0)).getTextContent();
					String mimetype = ((Element)mapping.getElementsByTagName("mime-type").item(0)).getTextContent();
					mime2ext.put(mimetype.toLowerCase(), extension.toLowerCase());
					ext2mime.put(extension.toLowerCase(), mimetype.toLowerCase());
				} catch (Exception e) { log.error("Caught exception",e); }
			}
		} catch (Throwable e) {
			log.error("Did not manage to read mime - extension map");
		}
	}

}
