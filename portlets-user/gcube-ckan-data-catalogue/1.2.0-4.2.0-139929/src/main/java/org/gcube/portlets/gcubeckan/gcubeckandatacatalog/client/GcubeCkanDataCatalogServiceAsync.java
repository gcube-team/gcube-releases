/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import java.util.List;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgGroupRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.ManageProductBean;

import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * The Interface GcubeCkanDataCatalogServiceAsync.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 4, 2016
 */
public interface GcubeCkanDataCatalogServiceAsync {

	/**
	 * Gets the my role.
	 *
	 * @param callback the callback
	 * @return the my role
	 */
	void getMyRole(AsyncCallback<CkanRole> callback);

	/**
	 * Gets the c kan connector.
	 *
	 * @param pathInfoParameters the path info parameters
	 * @param queryStringParameters the query string parameters
	 * @param currentUrl the current url
	 * @param callback the callback
	 * @return the c kan connector
	 */
	void getCKanConnector(
		String pathInfoParameters, String queryStringParameters, String currentUrl,
		AsyncCallback<CkanConnectorAccessPoint> callback);

	/**
	 * Logout from ckan url.
	 *
	 * @param callback the callback
	 */
	void logoutFromCkanURL(AsyncCallback<String> callback);

	/**
	 * Logout uri from ckan.
	 *
	 * @param callback the callback
	 */
	void logoutURIFromCkan(AsyncCallback<String> callback);

	/**
	 * Gets the ckan organizations names and urls for user.
	 *
	 * @param callback the callback
	 * @return the ckan organizations names and urls for user
	 */
	void getCkanOrganizationsNamesAndUrlsForUser(
			AsyncCallback<List<BeanUserInOrgGroupRole>> callback);
	
	/**
	 * Retrieve the list of groups to whom the user belongs and their urls.
	 *
	 * @return the ckan groups names and urls for user
	 */
	void getCkanGroupsNamesAndUrlsForUser(
			AsyncCallback<List<BeanUserInOrgGroupRole>> callback);

	/**
	 * Outside portal.
	 *
	 * @param callback the callback
	 */
	void outsidePortal(AsyncCallback<Boolean> callback);

	/**
	 * Check if the manage product needs to be shown (e.g., for GRSF products)
	 * @return
	 */
	void isManageProductEnabled(AsyncCallback<Boolean> callback);

	/**
	 * Notify product update
	 */
	void notifyProductUpdate(ManageProductBean bean,
			AsyncCallback<String> callback);

	void getProductBeanById(String identifier,
			AsyncCallback<ManageProductBean> callback);
}
