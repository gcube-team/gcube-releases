package org.gcube.portlets.user.results.client.util;

import org.gcube.portlets.user.results.client.constants.ImageConstants;

import com.google.gwt.user.client.ui.Image;
/**
 * <code> MimeTypeImagecreator </code> simply associate a mimetype to its image in the system
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (2.0) 
 * 
 * TODO: add URI ICON
 */
public class MimeTypeImagecreator {

	public static final String PDF = "application/pdf";
	public static final String HTML = "text/html";
	public static final String XML = "text/xml";
	public static final String GIF = "image/gif";
	public static final String PNG = "image/png";
	public static final String JPEG = "image/jpeg";
	public static final String JPG = "image/jpg";	
	public static final String TIFF = "image/tiff";
	public static final String BMP = "image/bmp";
	public static final String URI = "text/uri-list";
	public static final String URL = "text/url";
	public static final String GOOGLE = "google/url";
	
	
	public static Image getThumbImage(String mimetype) {
		if (mimetype.equals(HTML))
			return new Image(ImageConstants.HTML_ICON);
		else if (mimetype.equals(XML))
			return new Image(ImageConstants.XML_ICON);
		else if (mimetype.equals(PDF))
			return new Image(ImageConstants.PDF_ICON);
		else if (mimetype.equals(GIF))
			return new Image(ImageConstants.GIF_ICON);
		else if (mimetype.equals(PNG))
			return new Image(ImageConstants.PNG_ICON);
		else if (mimetype.equals(JPG))			
			return new Image(ImageConstants.JPG_ICON);
		else if (mimetype.equals(JPEG))
			return new Image(ImageConstants.JPG_ICON);
		else if (mimetype.equals(TIFF))
			return new Image(ImageConstants.TIFF_ICON);
		else if (mimetype.equals(URI))
			return new Image(ImageConstants.URI_ICON);
		else if (mimetype.equals(URL))
			return new Image(ImageConstants.HTML_ICON);
		else if (mimetype.equals(BMP))
			return new Image(ImageConstants.BMP_ICON);
		else if (mimetype.equals(GOOGLE))
			return new Image(ImageConstants.GOOGLE_ICON);
		else 
			return new Image(ImageConstants.UNKNOWN_ICON);
		
	}
	
}
