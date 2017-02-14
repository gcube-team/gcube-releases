package org.gcube.data.analysis.dataminermanagercl.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient4WPSBuilder;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClientBuilder;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClientDirector;
import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.gcube.data.analysis.dataminermanagercl.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DataMinerService {

	// private HashMap<String, SClient> cachedSClients;

	private Logger logger = LoggerFactory.getLogger(DataMinerService.class);

	/**
	 * 
	 * @param userName
	 * @param scope
	 * @param token
	 * @return
	 * @throws Exception
	 */

	public DataMinerService() {
		// cachedSClients= new HashMap<>();
	}

	public SClient getClient() throws Exception {

		ServiceCredentials serviceCredential = getServiceCredentials();

		/*
		 * if (cachedSClients.isEmpty() ||
		 * !cachedSClients.containsKey(serviceCredential.getScope())) {
		 */

		SClientBuilder sBuilder = new SClient4WPSBuilder(serviceCredential);
		SClientDirector director = new SClientDirector();
		director.setSClientBuilder(sBuilder);
		director.constructSClient();
		SClient sClient = director.getSClient();
		logger.debug("" + sClient);
		// cachedSClients.put(serviceCredential.getScope(), sClient);
		return sClient;
		/*
		 * } else { return cachedSClients.get(serviceCredential.getScope()); }
		 */

	}
	
	
	public SClient getClient(String token) throws Exception {

		ServiceCredentials serviceCredential = getServiceCredentials(token);

		/*
		 * if (cachedSClients.isEmpty() ||
		 * !cachedSClients.containsKey(serviceCredential.getScope())) {
		 */

		SClientBuilder sBuilder = new SClient4WPSBuilder(serviceCredential);
		SClientDirector director = new SClientDirector();
		director.setSClientBuilder(sBuilder);
		director.constructSClient();
		SClient sClient = director.getSClient();
		logger.debug("" + sClient);
		// cachedSClients.put(serviceCredential.getScope(), sClient);
		return sClient;
		/*
		 * } else { return cachedSClients.get(serviceCredential.getScope()); }
		 */

	}
	
	

	private ServiceCredentials getServiceCredentials() throws ServiceException {
		String userName = null;
		String token = null;
		String scope = null;

		if (Constants.DEBUG) {
			logger.debug("Debug Mode");
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;
		} else {
			logger.debug("Production Mode");
			try {
				logger.debug("Retrieving token credentials");
				// get username from SmartGears
				// ClientInfo tokenInfo = (UserInfo)
				// AuthorizationProvider.instance
				// .get().getClient();
				// userName = tokenInfo.getId();
				token = SecurityTokenProvider.instance.get();
				AuthorizationEntry entry = authorizationService().get(token);
				userName = entry.getClientInfo().getId();
				scope = entry.getContext();
			} catch (Exception e) {
				logger.error("Error Retrieving token credentials: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
				throw new ServiceException(e.getLocalizedMessage(), e);
			}

		}

		ServiceCredentials serviceCredential = new ServiceCredentials(userName,
				scope, token);

		logger.debug("Credential: " + serviceCredential);

		return serviceCredential;

	}
	
	
	private ServiceCredentials getServiceCredentials(String token) throws ServiceException {
		String userName = null;
		String scope = null;

		if (Constants.DEBUG) {
			logger.debug("Debug Mode");
			userName = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			token = Constants.DEFAULT_TOKEN;
		} else {
			logger.debug("Production Mode");
			if(token==null|| token.isEmpty()){
				logger.error("Error Retrieving token credentials: token="+token);
				throw new ServiceException("Error Retrieving token credentials: token="+token);
			}
			
			try {
				logger.debug("Retrieving token credentials");
				AuthorizationEntry entry = authorizationService().get(token);
				userName = entry.getClientInfo().getId();
				scope = entry.getContext();
			} catch (Exception e) {
				logger.error("Error Retrieving token credentials: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
				throw new ServiceException(e.getLocalizedMessage(), e);
			}

		}

		ServiceCredentials serviceCredential = new ServiceCredentials(userName,
				scope, token);

		logger.debug("Credential: " + serviceCredential);

		return serviceCredential;

	}
	

}
