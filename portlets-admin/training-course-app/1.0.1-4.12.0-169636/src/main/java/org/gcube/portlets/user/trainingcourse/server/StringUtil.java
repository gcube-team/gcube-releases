/**
 *
 */

package org.gcube.portlets.user.trainingcourse.server;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


// TODO: Auto-generated Javadoc
/**
 * The Class StringUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 8, 2018
 */
public class StringUtil {

	/** The Constant UTF_8. */
	public static final String UTF_8 = "UTF-8";
	
	/** The regx. */
	public static String regx = ",@+^'?!\"%&$£/#()";
	
	/** The logger. */
	protected static Logger logger = Logger.getLogger(StringUtil.class);

	/**
	 * Removes the special characters.
	 *
	 * @param input
	 *            the input
	 * @return the string
	 */
	public static String removeSpecialCharacters(String input) {

		char[] ca = regx.toCharArray();
		for (char c : ca) {
			input = input.replace("" + c, "");
		}
		return input;
	}

	/**
	 * Replace all white space.
	 *
	 * @param input
	 *            the input
	 * @param replacement
	 *            the replacement
	 * @return the string
	 */
	public static String replaceAllWhiteSpace(String input, String replacement) {

		return input.replaceAll("\\s", replacement);
	}


	/**
	 * Base64 decode string.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String base64DecodeString(String s) {

		try {
			return new String(Base64.decodeBase64(s.getBytes(UTF_8)));
		}
		catch (UnsupportedEncodingException e) {
			logger.error("Failed to decode the String", e);
			logger.error("Returning input string: " + s);
			return s;
		}
	}

	/**
	 * Base64 encode string url safe.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String base64EncodeStringURLSafe(String s) {

		try {
			return Base64.encodeBase64URLSafeString(s.getBytes(UTF_8));
		}
		catch (UnsupportedEncodingException e) {
			logger.error("Failed to decode the String", e);
			logger.error("Returning input string: " + s);
			return s;
		}
	}

	/**
	 * Base64 encode string.
	 *
	 * @param s the s
	 * @return the string
	 */
	public static String base64EncodeString(String s) {

		try {
			return new String(Base64.encodeBase64(s.getBytes(UTF_8)));
		}
		catch (UnsupportedEncodingException e) {
			logger.error("Failed to encode the String", e);
			logger.error("Returning input string: " + s);
			return s;
		}
	}
}
