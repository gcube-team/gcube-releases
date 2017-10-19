package org.gcube.portlets.admin.dataminermanagerdeployer.client.rpc;

import org.gcube.portlets.admin.dataminermanagerdeployer.shared.config.DMDeployConfig;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.session.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface DataMinerDeployerServiceAsync {

	// public static DataMinerDeployerServiceAsync INSTANCE =
	// (DataMinerDeployerServiceAsync) GWT
	// .create(DataMinerDeployerService.class);

	void hello(String token, AsyncCallback<UserInfo> callback);

	void startDeploy(String token, DMDeployConfig dmConfig, AsyncCallback<String> callback);

	void retrieveError(String token, String operationId,
			AsyncCallback<String> callback);

	void monitorDeploy(String token, String operationId, AsyncCallback<String> callback);

}
