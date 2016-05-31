package org.gcube.application.framework.http.content.access.tools;

public class ContentConstants {

	/**
	 * this is the string by which the main urls are stored within the returned TreeMap of the parsers.
	 */
	public static final String MAIN_URLs = "mainURLs";
	
	/**
	 * this is the string by which the alternative urls are stored within the returned TreeMap of the parsers.
	 */
	public static final String ALTERNATIVE_URLs = "alternativeURLs";
	
	
	/**
	 * if a mime string contains IMAGE_MIME, then it should be considered an image
	 */
	public static final String IMAGE_MIME = "image/";
	
	/**
	 * if a mime string contains WEBPAGE_MIME, then it should be considered a webpage
	 */
	public static final String WEBPAGE_MIME = "text/html";
	
	/**
	 * if a mime string contains PDF_MIME, then it should be considered a pdf document
	 */
	public static final String PDF_MIME = "application/pdf";
	
	
}
