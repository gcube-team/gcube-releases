/**
 * 
 */
package org.gcube.portlets.user.workspace.server.shortener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * The Class UrlEncoderUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 13, 2016
 */
public class UrlEncoderUtil {
	
	public static String charset = "UTF-8";
	public static Logger logger = Logger.getLogger(UrlEncoderUtil.class);
	
	/**
	 * Encode query.
	 *
	 * @param parameters the parameters
	 * @return the string
	 */
	public static String encodeQuery(String... parameters){
		
		String query = "";
		for (String string : parameters) {
			try {
				query+=URLEncoder.encode(string, charset)+"&";
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				return query;
			}
		}
		return removeLastChar(query);
	}
	

	/**
	 * Encode query value.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String encodeQueryValue(String value){
		
		if(value==null || value.isEmpty())
			return value;
		
		try {
			return URLEncoder.encode(value, charset);
		}
		catch (UnsupportedEncodingException e) {
			logger.error(e);
			return null;
		}
	}


	/**
	 * Encode query values.
	 *
	 * @param parameters the parameters
	 * @return the string
	 */
	public static String encodeQueryValues(Map<String, String> parameters){
		
		String query = "";
		
		if(parameters==null)
			return query;
		
		for (String key : parameters.keySet()) {
			try {
				query+=String.format(key+"=%s", URLEncoder.encode(parameters.get(key), charset))+"&";
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				return query;
			}
		}
		return removeLastChar(query);
	}
	
	/**
	 * Removes the last char.
	 *
	 * @param string the string
	 * @return the string
	 */
	public static String removeLastChar(String string){
		
		if(string == null)
			return null;
		
		if(string.length()>0)
			return string.substring(0, string.length()-1);
		
		return string;
	}
	
	/**
	 * Gets the charset.
	 *
	 * @return the charset
	 */
	public static String getCharset() {
	
		return charset;
	}
	
	/**
	 * Sets the charset.
	 *
	 * @param charset the charset to set
	 */
	public static void setCharset(String charset) {
	
		UrlEncoderUtil.charset = charset;
	}
	
	/*
	public static void main(String[] args) {
	//	System.out.println(UrlEncoderUtil.encodeQuery("request=GetStyles", "layers=test Name", "service=WMS", "version=1.1.1"));
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("request", "GetStyles");
		parameters.put("layers", "test Name");
		parameters.put("version", "1.1.1");
		System.out.println(UrlEncoderUtil.encodeQuery(parameters));
	}*/
}
