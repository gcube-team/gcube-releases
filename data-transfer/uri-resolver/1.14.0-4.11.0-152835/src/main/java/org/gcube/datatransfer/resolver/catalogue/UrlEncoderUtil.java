/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * The Class UrlEncoderUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 20, 2016
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
		for (int i = 0; i < parameters.length-1; i++) {
			try {
				query+=URLEncoder.encode(parameters[i], charset)+"%26";
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				return query;
			}
		}

		try {
			query+=URLEncoder.encode(parameters[parameters.length-1], charset);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			return query;
		}

		return query;
	}


	/**
	 * Encode parameters.
	 *
	 * @param parameters the parameters
	 * @return the string
	 */
	public static String encodeParameters(Map<String, String> parameters){

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
}
