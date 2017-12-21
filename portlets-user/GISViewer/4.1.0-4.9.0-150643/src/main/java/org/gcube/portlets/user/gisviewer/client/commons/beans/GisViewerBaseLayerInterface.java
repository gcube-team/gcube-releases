package org.gcube.portlets.user.gisviewer.client.commons.beans;


/**
 * The Interface GisViewerBaseLayerInterface.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 27, 2015
 */
public interface GisViewerBaseLayerInterface {

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle();
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the wms service base url.
	 *
	 * @return the wms service base url
	 */
	public String getWmsServiceBaseURL();
	
	/**
	 * Checks if is display.
	 *
	 * @return true, if is display
	 */
	public boolean isDisplay();
	
}