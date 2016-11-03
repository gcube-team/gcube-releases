package org.gcube.portlets.user.geoexplorer.server;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 29, 2013
 *
 */
public interface GeoExplorerServiceInterface {

	/**
	 * @param scope 
	 * @param geonetworkInstance 
	 * @return
	 */
	public GeonetworkInstance getGeonetworkInstance(HttpSession httpSession, String scope) throws Exception;

	/**
	 * 
	 * @param httpSession
	 * @throws Exception
	 */
	public void updateGeonetworkInstance(HttpSession httpSession, String scope) throws Exception;

	/**
	 * 
	 * @return
	 */
	public boolean isValidGeoInstance();

	/**
	 * 
	 * @param isValid
	 */
	public void setValidGeoInstance(boolean isValid);
	
	
	
	/**
	 * @param url
	 */
	public void setGeoNetworkUrl(String url);

	/**
	 * @param user
	 */
	public void setGeoNetworkUser(String user);

	/**
	 * @param password
	 */
	public void setGeoNetworkPwd(String password);
	
	/**
	 * @param url
	 */
	public void setGeoServerUrl(String url);

	/**
	 * @param user
	 */
	public void setGeoServerUser(String user);

	/**
	 * @param password
	 */
	public void setGeoServerPwd(String password);
	
	/**
	 * @param scope
	 */
	public void setScope(String scope);
	
	/**
	 * 
	 * @return
	 */
	public String getScope();
}
