package org.gcube.portlets.admin.dataminermanagerdeployer.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.admin.dataminermanagerdeployer.client.rpc.DataMinerDeployerService;
import org.gcube.portlets.admin.dataminermanagerdeployer.server.poolmanager.DataMinerPoolManager;
import org.gcube.portlets.admin.dataminermanagerdeployer.server.util.ServiceCredentials;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.config.DMDeployConfig;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.exception.ServiceException;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.session.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@SuppressWarnings("serial")
public class DataMinerDeployerServiceImpl extends RemoteServiceServlet implements DataMinerDeployerService {

	private static Logger logger = LoggerFactory.getLogger(DataMinerDeployerServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("DataMiner Deployer Service started!");

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UserInfo hello(String token) throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest(),
					token);
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(), serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(), serviceCredentials.getScope(), serviceCredentials.getEmail(),
					serviceCredentials.getFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	
	@Override
	public String startDeploy(String token, DMDeployConfig dmDeployConfig) throws ServiceException {

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest, token);
			DataMinerPoolManager dmPoolManager=new DataMinerPoolManager(serviceCredentials);
			String operationId=dmPoolManager.deployAlgorithm(dmDeployConfig);
			return operationId;
			
			
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("An error occurred starting deploy: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String monitorDeploy(String token, String operationId)
			throws ServiceException {

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest, token);
			DataMinerPoolManager dmPoolManager=new DataMinerPoolManager(serviceCredentials);
			String status=dmPoolManager.getDeployOperationStatus(operationId);
			
			return status;
			
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("An error occurred monitoring deploy: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String retrieveError(String token, String operationId)
			throws ServiceException {

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(httpRequest, token);
			DataMinerPoolManager dmPoolManager=new DataMinerPoolManager(serviceCredentials);
			String error=dmPoolManager.getDeployOperationLogsLink(operationId);
			return error;

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		} catch (Throwable e) {
			logger.error("An error occurred retrieving deploy error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	

}
