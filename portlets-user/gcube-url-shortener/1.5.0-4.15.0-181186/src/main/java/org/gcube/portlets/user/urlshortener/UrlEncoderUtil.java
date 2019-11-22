/**
 *
 */
package org.gcube.portlets.user.urlshortener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class UrlEncoderUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 14, 2017
 */
public class UrlEncoderUtil {

	public static String charset = "UTF-8";

	protected static Logger logger = LoggerFactory.getLogger(UrlEncoderUtil.class);

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

				logger.error("encodeQuery error: ", e);
				return query;
			} catch (Exception e) {

				logger.error("encodeQuery error: ", e);
				return query;
			}


		}
		return removeLastChar(query);
	}



	/**
	 * Encode query.
	 *
	 * @param parameters the parameters
	 * @return the string
	 */
	public static String encodeQuery(Map<String, String> parameters){

		String query = "";

		if(parameters==null)
			return query;

		for (String key : parameters.keySet()) {

			try {

				query+=String.format(key+"=%s", URLEncoder.encode(parameters.get(key), charset))+"&";
			} catch (UnsupportedEncodingException e) {

				logger.error("encodeQuery error: ", e);
				return query;
			} catch (Exception e) {

				logger.error("encodeQuery error: ", e);
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
