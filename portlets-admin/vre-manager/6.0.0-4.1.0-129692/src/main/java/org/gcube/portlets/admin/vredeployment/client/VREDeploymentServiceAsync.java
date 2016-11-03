package org.gcube.portlets.admin.vredeployment.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.vredeployment.shared.VREDefinitionBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>VREDeploymentService</code>.
 */
public interface VREDeploymentServiceAsync {
	void getVREDefinitions(AsyncCallback<ArrayList<VREDefinitionBean>> callback);

	void doApprove(String vreId, AsyncCallback<Boolean> callback);

	void doRemove(String vreId, AsyncCallback<Boolean> callback);

	void doEdit(String vreId, AsyncCallback<Boolean> callback);

	void doViewDetails(String vreId, AsyncCallback<String> callback);

	void doViewReport(String vreId, AsyncCallback<Boolean> callback);

	void getHTMLReport(String vreId, AsyncCallback<String> callback);

	void postPone(String vreId, AsyncCallback<Boolean> callback);

	void doUndeploy(String vreId, AsyncCallback<Boolean> callback);
}
