package org.gcube.portlets.user.gisviewer.client;

import java.util.Map;

/**
 * The Interface GisViewerSaveHandler.
 * 
 * @author "Federico De Faveri defaveri@isti.cnr.it" 
 * updated By Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 28, 2015
 */
public interface GisViewerSaveHandler {

	/**
	 * Save layer image.
	 *
	 * @param name
	 *            the name
	 * @param contentType
	 *            the content type
	 * @param url
	 *            the url
	 * @param zIndex
	 *            the z-index to show Dialog
	 */
	public void saveLayerImage(String name, String contentType, String url,
			int zIndex);

	/**
	 * Save map image.
	 *
	 * @param fileName
	 *            the file name
	 * @param outputFormat
	 *            the output format
	 * @param parameters
	 *            the parameters
	 * @param zIndex
	 *            the z-index to show Dialog
	 */
	public void saveMapImage(String fileName, String outputFormat,
			Map<String, String> parameters, int zIndex);
}
