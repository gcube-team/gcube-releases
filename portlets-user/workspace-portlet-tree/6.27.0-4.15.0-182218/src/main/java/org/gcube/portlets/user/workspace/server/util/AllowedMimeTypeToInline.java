package org.gcube.portlets.user.workspace.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class AllowedMimeTypeToInline.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Apr 4, 2019
 */
public class AllowedMimeTypeToInline {
	
	public static final String filename = "MimeTypePrefixToInline.properties";
	
	protected static Logger logger = LoggerFactory.getLogger(WsUtil.class);

	/**
	 * Load mime type prefix.
	 *
	 * @return the properties
	 */
	private static Properties loadMimeTypePrefix(){

    	Properties prop = new Properties();
    	InputStream input = null;
    	
    	try {
        
    		input = AllowedMimeTypeToInline.class.getResourceAsStream(filename);
    		if(input==null){
    			logger.error("Sorry, unable to find " + filename);
    		    return null;
    		}
    		//load a properties file from class path, inside static method
    		prop.load(input);
    		return prop;
    		
    	} catch (IOException ex) {
    		logger.error("Sorry, error: ", ex);
    		return null;
    		
        } finally{
        	
        	if(input!=null){
        		try {
        			input.close();
				} catch (IOException e) {
					//silent
				}
        	}
        }
    }
	
	
	/**
	 * Gets the allowed mime type prefixes.
	 *
	 * @return the allowed mime type prefixes
	 */
	public static List<String> getAllowedMimeTypePrefixes(){
		Properties prop = loadMimeTypePrefix();
		List<String> prefixes = new ArrayList<String>();
		if(prop==null)
			return prefixes;
		
		for (Object keyObj : prop.keySet()) {
			prefixes.add((String) keyObj);
		}
		
		return prefixes;
	}
}




