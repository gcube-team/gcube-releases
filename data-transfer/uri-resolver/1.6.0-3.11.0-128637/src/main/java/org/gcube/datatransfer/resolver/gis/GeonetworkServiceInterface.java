/**
 *
 */
package org.gcube.datatransfer.resolver.gis;

/**
 * The Interface GeonetworkServiceInterface.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 7, 2014
 */
public interface GeonetworkServiceInterface {

	/**
	 * Gets the geonetwork instance.
	 *
	 * @param authenticate the authenticate
	 * @return the geonetwork instance
	 * @throws Exception the exception
	 */
	public GeonetworkInstance getGeonetworkInstance(boolean authenticate) throws Exception;

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope();

}
