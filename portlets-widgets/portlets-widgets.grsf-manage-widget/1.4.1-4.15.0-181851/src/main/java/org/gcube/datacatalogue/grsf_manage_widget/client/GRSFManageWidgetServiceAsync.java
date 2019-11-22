/**
 *
 */
package org.gcube.datacatalogue.grsf_manage_widget.client;

import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperationInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interface
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface GRSFManageWidgetServiceAsync {

	void notifyProductUpdate(ManageProductBean bean,
			AsyncCallback<Void> callback);

	void getProductBeanById(String identifier,
			boolean isRequestForRevertingMerge,
			AsyncCallback<ManageProductBean> callback);

	void isAdminUser(AsyncCallback<Boolean> callback);

	void checkIdentifierExists(String id,
			AsyncCallback<String> callback);

	void checkIdentifierExistsInDomain(String id,
			String domain, AsyncCallback<String> callback);

	void validateRevertOperation(String url, AsyncCallback<RevertableOperationInfo> callback);

	void performRevertOperation(RevertableOperationInfo rInfo,
			AsyncCallback<Boolean> callback);

}
