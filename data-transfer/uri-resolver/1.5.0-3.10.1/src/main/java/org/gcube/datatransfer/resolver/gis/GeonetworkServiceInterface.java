/**
 * 
 */
package org.gcube.datatransfer.resolver.gis;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 7, 2014
 *
 */
public interface GeonetworkServiceInterface {
	
	/**
	 * @param scope 
	 * @param geonetworkInstance 
	 * @return
	 */
	public GeonetworkInstance getGeonetworkInstance(boolean authenticate) throws Exception;

	public String getScope();

}
