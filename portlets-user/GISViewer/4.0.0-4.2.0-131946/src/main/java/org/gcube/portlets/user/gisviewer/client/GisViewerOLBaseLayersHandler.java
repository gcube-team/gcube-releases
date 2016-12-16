/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client;

import java.util.List;

import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 21, 2014
 *
 * Gis Viewer - Open Layers base layers handler
 */
public interface GisViewerOLBaseLayersHandler {
	
	List<GisViewerBaseLayerInterface> loadOpenLayersBaseLayers();

}
