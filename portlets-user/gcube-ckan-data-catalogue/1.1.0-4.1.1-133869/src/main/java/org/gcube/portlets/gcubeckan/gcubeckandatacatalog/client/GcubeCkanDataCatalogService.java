package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import java.util.List;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 20, 2016
 */
@RemoteServiceRelativePath("ckandatacatalogue")
public interface GcubeCkanDataCatalogService extends RemoteService {


	/**
	 * Get the ckan connector access point
	 * @param pathInfoParameters
	 * @param queryStringParameters
	 * @param currentUrl
	 * @return
	 * @throws Exception
	 */
	CkanConnectorAccessPoint getCKanConnector(
		String pathInfoParameters, String queryStringParameters, String currentUrl) throws Exception;

	/**
	 * Get the current role in CKAN for this user
	 * @return
	 * @throws Exception
	 */
	CkanRole getMyRole() throws Exception;
	
	/**
	 * Retrieve the list of organizations to whom the user belongs and their urls
	 * @return
	 */
	List<BeanUserInOrgRole> getCkanOrganizationsNamesAndUrlsForUser();

	/**
	 * Logout from ckan
	 */
	void logoutFromCkan();

	/**
	 * Remove auth cookie for ckan of this user
	 * @return
	 */
	String logoutURIFromCkan();
	
	/**
	 * Check if the there is a user logged in
	 * @return
	 */
	boolean outsidePortal();
}
