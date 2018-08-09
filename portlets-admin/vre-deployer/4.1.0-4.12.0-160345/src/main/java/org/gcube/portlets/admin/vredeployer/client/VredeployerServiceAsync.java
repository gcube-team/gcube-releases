package org.gcube.portlets.admin.vredeployer.client;

import org.gcube.portlets.admin.vredeployer.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredeployer.shared.VREDeployerStatusType;
import org.gcube.portlets.admin.vredeployer.shared.VREDescrBean;
import org.gcube.portlets.admin.vredeployer.shared.deployreport.ClientDeployReport;

import com.google.gwt.user.client.rpc.AsyncCallback;

/*
 * 
 */
public interface VredeployerServiceAsync {

	void getVRE(AsyncCallback<VREDescrBean> callback);

	void isApprovingModeEnabled(AsyncCallback<VREDeployerStatusType> callback);

	void checkCreateVRE(AsyncCallback<ClientDeployReport> callback);

	void deployVRE(AsyncCallback<Boolean> callback);

	void getHTMLReport(AsyncCallback<String> callback);

	void getFunctionality(AsyncCallback<VREFunctionalityModel> callback);

	void getGHNPerFunctionality(String funcId, AsyncCallback<Void> callback);

}
