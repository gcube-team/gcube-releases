package org.gcube.portlets.admin.dataminermanagerdeployer.client.rpc;

import org.gcube.portlets.admin.dataminermanagerdeployer.shared.config.DMDeployConfig;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.exception.ServiceException;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.session.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("dmdeployer")
public interface DataMinerDeployerService extends RemoteService {

	public UserInfo hello(String token) throws ServiceException;
	
	public String startDeploy(String token, DMDeployConfig dmConfig) throws ServiceException;

	public String retrieveError(String token, String operationId) throws ServiceException;

	public String monitorDeploy(String token,  String operationId) throws ServiceException;

}
