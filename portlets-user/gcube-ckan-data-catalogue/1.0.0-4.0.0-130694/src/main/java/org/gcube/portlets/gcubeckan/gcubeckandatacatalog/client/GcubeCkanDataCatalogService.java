package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import java.util.Map;

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


	CkanConnectorAccessPoint getCKanConnector(
		String pathInfoParameters, String queryStringParameters) throws Exception;

	CkanRole getMyRole() throws Exception;

	String getUser();
	
	/**
	 * Retrieve the list of organizations to whom the user belongs and their urls
	 * @return
	 */
	Map<String, String> getCkanOrganizationsNamesAndUrlsForUser();

	void logoutFromCkan();

	/**
	 * @return
	 */
	String logoutURIFromCkan();
	
	/**
	 * Check if the there is a user logged in
	 * @return
	 */
	boolean outsidePortal();
}
