package org.gcube.opensearch.opensearchlibrary.utils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A URL encoder utility that is used to URL-encode strings only if they are not already URL-encoded
 * 
 * @author gerasimos.farantatos
 *
 */
public class URLEncoder {

	private static Pattern hexPattern = Pattern.compile(" |%[^0-9a-fA-F]");
	private static Pattern twoHexPattern = Pattern.compile("%[0-9a-fA-F][^0-9a-fA-F]");
	//private static Pattern templatePattern = Pattern.compile("\\{[^\\}]*\\}");
	
	private URLEncoder() { }
	/**
	 * URL-encodes a String only if it is not found already URL-encoded
	 * 
	 * @param value The String to be URL-encoded
	 * @param encoding The encoding to be used
	 * @return The URL-encoded string
	 * @throws UnsupportedEncodingException If the encoding given is not supported
	 */
	public static String UrlEncode(String value, String encoding) throws UnsupportedEncodingException {
		Matcher m1 = hexPattern.matcher(value);
		Matcher m2 = twoHexPattern.matcher(value);
		if(m1.find() == false) {
			if(m2.find() == true)
				return java.net.URLEncoder.encode(value, encoding);
		}else
			return java.net.URLEncoder.encode(value, encoding);
		return value;
	}

	
}
