/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.server;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class StringUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 16, 2016
 */
public class StringUtil {

	/**
	 * 
	 */
	public static final String UTF_8 = "UTF-8";
	public static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

	/**
	 * Readable file size.
	 *
	 * @param size the size
	 * @return the string
	 */
	public static String readableFileSize(long size) {
		if(size < 0) return "Unknown";
	    if(size == 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))+units[digitGroups];
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

}
