package org.gcube.datacatalogue.grsf_manage_widget.client;

import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperationInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("grsfmanageservice")
public interface GRSFManageWidgetService extends RemoteService {

	/**
	 * check if the user has the rights to manage the item
	 */
	boolean isAdminUser();

	/**
	 * Get the product bean from the product identifier
	 * @param identifier
	 * @return ManageProductBean
	 * @throws Exception 
	 */
	ManageProductBean getProductBeanById(String identifier) throws Exception;

	/**
	 * Notify product update
	 */
	void notifyProductUpdate(ManageProductBean bean) throws Exception;

	/**
	 * Identifier of the record (UUID)
	 * @param id
	 * @return the url of the record
	 * @throws Exception
	 */
	String checkIdentifierExists(String id) throws Exception;

	/**
	 * Identifier of the record (UUID)
	 * @param id
	 * @param domain (stock or fishery)
	 * @return the url of the record
	 * @throws Exception
	 */
	String checkIdentifierExistsInDomain(String id, String domain) throws Exception;

	/**
	 * Check if the given url for reverting the operation is valid and get back the needed info to proceed
	 * @param url
	 * @throws Exception
	 */
	RevertableOperationInfo validateRevertOperation(String url) throws Exception;
	
	/**
	 * Perform a revert operation
	 * @param rInfo
	 * @return
	 * @throws Exception
	 */
	Boolean performRevertOperation(RevertableOperationInfo rInfo) throws Exception;

}
