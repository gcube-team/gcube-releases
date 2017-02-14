/**
 *
 */
package org.gcube.datacatalogue.grsf_manage_widget.client;

import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface GRSFManageWidgetServiceAsync {

	void notifyProductUpdate(ManageProductBean bean,
			AsyncCallback<String> callback);

	void getProductBeanById(String identifier,
			AsyncCallback<ManageProductBean> callback);

}
