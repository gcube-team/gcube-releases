/**
 *
 */
package org.gcube.datatransfer.resolver.gis;

import org.gcube.datatransfer.resolver.gis.GeonetworkAccessParameter.GeonetworkLoginLevel;

/**
 * The Interface GeonetworkServiceInterface.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public interface GeonetworkServiceInterface {

	/**
	 * Gets the geonetwork instance.
	 *
	 * @param authenticate the authenticate
	 * @param loginLevel the login level
	 * @return the geonetwork instance
	 * @throws Exception the exception
	 */
	public GeonetworkInstance getGeonetworkInstance(boolean authenticate, GeonetworkLoginLevel loginLevel) throws Exception;



	/**
	 * Gets the geonetwork instance with authenticate = false and LoginLevel = null
	 *
	 * @return the geonetwork instance
	 * @throws Exception the exception
	 */
	public GeonetworkInstance getGeonetworkInstance() throws Exception;

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope();

}
