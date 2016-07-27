/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import java.util.Map;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Interface GcubeCkanDataCatalogServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 20, 2016
 */
public interface GcubeCkanDataCatalogServiceAsync {


	void getMyRole(AsyncCallback<CkanRole> callback);


	void getUser(AsyncCallback<String> callback);

	void getCKanConnector(
		String pathInfoParameters, String queryStringParameters,
		AsyncCallback<CkanConnectorAccessPoint> callback);


	/**
	 *
	 */
	void logoutFromCkan(AsyncCallback<Void> callback);


	void logoutURIFromCkan(AsyncCallback<String> callback);


	void getCkanOrganizationsNamesAndUrlsForUser(
			AsyncCallback<Map<String, String>> callback);


	void outsidePortal(AsyncCallback<Boolean> callback);

}
