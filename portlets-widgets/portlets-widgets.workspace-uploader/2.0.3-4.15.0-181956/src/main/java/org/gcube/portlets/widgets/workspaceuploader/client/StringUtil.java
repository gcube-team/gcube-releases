/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client;

import com.google.gwt.core.shared.GWT;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 9, 2015
 */
public class StringUtil {
	
	/**
	 * Puts ellipses in input strings that are longer than than maxCharacters. Shorter strings or
	 * null is returned unchanged.
	 * @param input the input string that may be subjected to shortening
	 * @param maxCharacters the maximum characters that are acceptable for the unshortended string. Must be at least 3, otherwise a string with ellipses is too long already.
	 * @param the number of characters that should appear after the ellipsis (0 or larger) 
	 */
	public static String ellipsize(String input, int maxCharacters, int charactersAfterEllipsis) {
	 
		try{
		  if(maxCharacters < 3) {
		    throw new IllegalArgumentException("maxCharacters must be at least 3 because the ellipsis already take up 3 characters");
		  }
		  if(maxCharacters - 3 < charactersAfterEllipsis) {
		    throw new IllegalArgumentException("charactersAfterEllipsis must be less than maxCharacters-3");
		  }
		  if (input == null || input.length() < maxCharacters) {
		    return input;
		  }
		  return input.substring(0, maxCharacters - 3 - charactersAfterEllipsis) + "..." + input.substring(input.length() - charactersAfterEllipsis);
		}catch(Exception e){
			GWT.log(e.getMessage());
			return input;
		}
	}

}
