/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class UrlEncoderUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class UrlEncoderUtil {

	/** The charset. */
	public static String charset = "UTF-8";

	/** The logger. */
	public static final Logger logger = LoggerFactory.getLogger(UrlEncoderUtil.class);

	/**
	 * Encode query.
	 *
	 * @param parameters the parameters
	 * @return the string
	 */
	public static String encodeQuery(Map<String, List<String>> parameters){

		String query = "";

		if(parameters==null)
			return query;

		for (String key : parameters.keySet()) {

			try {
				for (String value : parameters.get(key)) {
					
					if(key.compareToIgnoreCase("DataInputs")==0) {
						query+=String.format(key+"=%s", URLEncoder.encode(value, "ISO-8859-1"))+"&";
					}else {
						query+=String.format(key+"=%s", URLEncoder.encode(value, charset))+"&";
					}
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("Error:", e);
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
	 * Base 64 decode.
	 *
	 * @param valueToDecode the value to decode
	 * @return the long
	 */
	public static String base64Decode(String valueToDecode) throws Exception{
		try {
			return new String(Base64.getDecoder().decode(valueToDecode));
		}catch (Exception e) {
			throw new Exception("Error on decoding the parameter: "+ valueToDecode, e);
		}
	}

}
