package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client;

import java.util.List;

import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.BeanUserInOrgGroupRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanConnectorAccessPoint;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.ManageProductBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The Interface GcubeCkanDataCatalogService.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 4, 2016
 */
@RemoteServiceRelativePath("ckandatacatalogue")
public interface GcubeCkanDataCatalogService extends RemoteService {


	/**
	 * Get the ckan connector access point.
	 *
	 * @param pathInfoParameters the path info parameters
	 * @param queryStringParameters the query string parameters
	 * @param currentUrl the current url
	 * @return the c kan connector
	 * @throws Exception the exception
	 */
	CkanConnectorAccessPoint getCKanConnector(
			String pathInfoParameters, String queryStringParameters, String currentUrl) throws Exception;

	/**
	 * Get the current role in CKAN for this user.
	 *
	 * @return the my role
	 * @throws Exception the exception
	 */
	CkanRole getMyRole() throws Exception;

	/**
	 * Retrieve the list of organizations to whom the user belongs and their urls.
	 *
	 * @return the ckan organizations names and urls for user
	 */
	List<BeanUserInOrgGroupRole> getCkanOrganizationsNamesAndUrlsForUser();

	/**
	 * Retrieve the list of groups to whom the user belongs and their urls.
	 *
	 * @return the ckan groups names and urls for user
	 */
	List<BeanUserInOrgGroupRole> getCkanGroupsNamesAndUrlsForUser();

	/**
	 * Logout from ckan.
	 *
	 * @return the string
	 */
	String logoutFromCkanURL();

	/**
	 * Remove auth cookie for ckan of this user.
	 *
	 * @return the string
	 */
	String logoutURIFromCkan();

	/**
	 * Check if the there is a user logged in.
	 *
	 * @return true, if successful
	 */
	boolean outsidePortal();

	/**
	 * Check if the manage product needs to be shown (e.g., for GRSF products)
	 * @return
	 */
	boolean isManageProductEnabled();

	/**
	 * Notify product update
	 */
	String notifyProductUpdate(ManageProductBean bean);

	/**
	 * Get the product bean from the product identifier
	 * @param identifier
	 * @return ManageProductBean
	 * @throws Exception 
	 */
	ManageProductBean getProductBeanById(String identifier) throws Exception;
}
