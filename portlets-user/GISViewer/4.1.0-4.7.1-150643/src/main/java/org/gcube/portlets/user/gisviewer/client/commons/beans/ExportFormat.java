/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client.commons.beans;

import org.gcube.portlets.user.gisviewer.client.resources.Images;

import com.google.gwt.user.client.ui.AbstractImagePrototype;


/**
 * The Enum ExportFormat.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 30, 2015
 */
public enum ExportFormat {
	
	ATOMPUB("AtomPub", "AtomPub", "application/atom+xml", Images.iconAtompub(), false),
	GIF("GIF", "GIF", "image/gif",Images.iconGif(), true),
	GEORSS("GeoRSS", "GeoRSS", "application/rss+xml",Images.iconGeorss(), false),
	JPEG("JPEG", "JPEG", "image/jpeg",Images.iconJpeg(), false),
	KML_COMPRESSED("KML_COMPRESSED", "KML (compressed)", "application/vnd.google-earth.kmz+xml",Images.iconKml(), true),
	KML_PLAIN("KML_PLAIN", "KML (plain)", "application/vnd.google-earth.kml+xml",Images.iconKml(), true),
	PDF("PDF", "PDF", "application/pdf",Images.iconPdf(), false),
	PNG("PNG", "PNG", "image/png",Images.iconPng(), true),
	SVG("SVG", "SVG", "image/svg+xml",Images.iconSvg(), true),
	TIFF("TIFF", "TIFF", "image/tiff",Images.iconTiff(), true);

	String id;
	String label;
	String format;
	AbstractImagePrototype img;
	boolean supportTransparency;
	
	/**
	 * Instantiates a new export format.
	 *
	 * @param id the id
	 * @param label the label
	 * @param format the format
	 * @param img the img
	 * @param supportTransparency the support transparency
	 */
	ExportFormat(String id, String label, String format, AbstractImagePrototype img, boolean supportTransparency){
		this.id = id;
		this.label = label;
		this.format = format;
		this.img = img;
		this.supportTransparency = supportTransparency;
	}


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}


	/**
	 * Gets the img.
	 *
	 * @return the img
	 */
	public AbstractImagePrototype getImg() {
		return img;
	}
	
	/**
	 * @return the supportTransparency
	 */
	public boolean isSupportTransparency() {
		return supportTransparency;
	}
}
