/**
 *
 */

package org.gcube.portlets.user.workspace.server.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class StringUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jul 9, 2013
 */
public class StringUtil {

	/** The Constant UTF_8. */
	public static final String UTF_8 = "UTF-8";
	public static String regx = ",@+^'?!\"%&$£/#()";
	protected static Logger logger = LoggerFactory.getLogger(StringUtil.class);

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
	/*
	 * public static void main(String[] args) { String input =
	 * "Just to clarify, Iì13ì? will have strings of varying " +
	 * "lengths. I want to strip characters from it, the exact " +
	 * "ones to be determined at !\"%&$£/ runtime, and return the " +
	 * "resulting string..."; input = removeSpecialCharacters(input);
	 * System.out.println(input); System.out.println(replaceAllWhiteSpace(input,
	 * "_")); }
	 */
}
