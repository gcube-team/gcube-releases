/**
 *
 */
package org.gcube.portlets.user.gisviewer.client.util;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2016
 */
public class StringUtil {

	/**
	 * Puts ellipses in input strings that are longer than than maxCharacters. Shorter strings or
	 * null is returned unchanged.
	 *
	 * @param input the input string that may be subjected to shortening
	 * @param maxCharacters the maximum characters that are acceptable for the unshortended string. Must be at least 3, otherwise a string with ellipses is too long already.
	 * @param charactersAfterEllipsis the characters after ellipsis
	 * @return the string
	 * @throws Exception the exception
	 */
	public static String ellipsize(String input, int maxCharacters, int charactersAfterEllipsis) throws Exception{
	  if(maxCharacters < 3) {
	    throw new IllegalArgumentException("maxCharacters must be at least 3 because the ellipsis already take up 3 characters");
	  }
	  if(maxCharacters - 3 > charactersAfterEllipsis) {
	    throw new IllegalArgumentException("charactersAfterEllipsis must be less than maxCharacters");
	  }
	  if (input == null || input.length() < maxCharacters) {
	    return input;
	  }
	  return input.substring(0, maxCharacters - 3 - charactersAfterEllipsis) + "..." + input.substring(input.length() - charactersAfterEllipsis);
	}
}
