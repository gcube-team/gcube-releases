package org.gcube.common.geoserverinterface.geonetwork.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class InputStreamUtil {
	
	public static InputStream stringToInputStream(String text){
		
		InputStream inputSt = null;
		  /*
         * Convert String to InputStream using ByteArrayInputStream 
         * class. This class constructor takes the string byte array 
         * which can be done by calling the getBytes() method.
         */
        try {
           inputSt = new ByteArrayInputStream(text.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return inputSt;
	}

}
