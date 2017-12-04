package org.gcube.datacatalogue.grsf_manage_widget.client;

import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;

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
	String notifyProductUpdate(ManageProductBean bean);

}
