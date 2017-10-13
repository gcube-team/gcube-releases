package org.gcube.portlets.docxgenerator.utils;

/**
 * Generates a unique, progressive ID to assign to images and numbers.
 * 
 * @author Luca Santocono
 * 
 */
public class IDGenerator {

	private static int imagecounter;

	private static int bookmarkcounter;

	/**
	 * Generates a unique ID to assign to an image, which is going to be
	 * inserted in a docx document.
	 * 
	 * @return The generated image ID.
	 */
	public static int imageIdGenerator() {
		return imagecounter++;
	}

	/**
	 * Generates a unique ID to assign to a bookmark, which is going to be
	 * inserted in a docx document.
	 * 
	 * @return The generated bookmark ID.
	 */
	public static int bookmarkIdGenerator() {
		return bookmarkcounter++;
	}

}
