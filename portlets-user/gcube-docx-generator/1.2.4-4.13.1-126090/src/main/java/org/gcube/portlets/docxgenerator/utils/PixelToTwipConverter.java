package org.gcube.portlets.docxgenerator.utils;

/**
 * Converts Pixel to Twips (docx length units).
 * 
 * @author Luca Santocono
 * 
 */
public class PixelToTwipConverter {

	/**
	 * Converts the pixel size to twip size.
	 * 
	 * @param size
	 *            The size in pixel.
	 * @return The equivalent size in Twips.
	 */
	public static int convertToTwip(final String size) {
		return Integer.parseInt(size) * 15;
	}
}
