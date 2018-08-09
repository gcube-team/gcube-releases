/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client.openlayers;

/**
 * @author ceras
 *
 */
public interface OpenLayersHandler {
	
//	public void clickOnMap(int x, int y, int width, int height);

	/**
	 * @param bbo
	 */
	public void selectBox(double x1, double y1, double x2, double y2);
}
