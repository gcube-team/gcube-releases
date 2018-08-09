/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client.beans;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 15, 2014
 *
 * Implements this interface to show only the geoxplorer styles read from RR
 */
public interface GeoexplorerMetadataStyleInterface {
	
	public String getName();
	public String getStyle();
	public String getScope();
	public boolean isDisplay();

}
