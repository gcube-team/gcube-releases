package org.gcube.portlets.user.dataminerexecutor;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.data.analysis.dataminermanagercl.server.is.InformationSystemUtils;
import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfo;
import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfoData;
import org.gcube.portlets.user.dataminerexecutor.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class TestInformationSystemRequests extends TestCase {

	private static Logger logger = LoggerFactory.getLogger(TestInformationSystemRequests.class);

	private String wpsToken;
	private String wpsUser;
	private String scope;

	private void retrieveServicesInfo() throws Exception {

		logger.info("Use test user");

		wpsUser = Constants.DEFAULT_USER;
		scope = Constants.DEFAULT_SCOPE;

		ServiceCredentials serviceCredentials = new ServiceCredentials();
		serviceCredentials.setUserName(wpsUser);
		serviceCredentials.setScope(scope);

		List<String> userRoles = new ArrayList<>();
		userRoles.add(Constants.DEFAULT_ROLE);

		try {
			wpsToken = authorizationService().generateUserToken(
					new UserInfo(serviceCredentials.getUserName(), userRoles), serviceCredentials.getScope());
		} catch (Exception e) {
			logger.error("Error generating the token for test: " + e.getLocalizedMessage(), e);

			throw new Exception("Error generating the token for test: " + e.getLocalizedMessage(), e);
		}
		serviceCredentials.setToken(wpsToken);

		String serviceAddress = InformationSystemUtils.retrieveServiceAddress(Constants.DATAMINER_SERVICE_CATEGORY,
				Constants.DATA_MINER_SERVICE_NAME, serviceCredentials.getScope());
		logger.debug("Service Address retrieved:" + serviceAddress);
		if (serviceAddress == null || serviceAddress.isEmpty()) {
			logger.error("No DataMiner service address available!");
			throw new Exception("No DataMiner service address available!");
		} else {
			logger.info("DataMiner service address found: " + serviceAddress);

		}

		ServiceInfo serviceInfo = InformationSystemUtils.retrieveServiceInfo(
				Constants.DATAMINER_SERVICE_CATEGORY, Constants.DATA_MINER_SERVICE_NAME, serviceCredentials.getScope());
		logger.debug("Service Properties retrieved:" + serviceInfo);
		if (serviceInfo == null) {
			logger.error("No DataMiner service properties available!");
			throw new Exception("No DataMiner service properties available!");
		} else {
			logger.info("DataMiner service properties found");
			logger.debug("Service Address: "+serviceInfo.getServiceAddress());
			for (ServiceInfoData serviceInfoData : serviceInfo.getServiceProperties()) {
				logger.debug("Property: " + serviceInfoData);
			}

		}

	}

	public void testExecuteProcess() {
		if (Constants.TEST_ENABLE) {
			try {
				retrieveServicesInfo();
				assertTrue(true);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				assertTrue(false);
			}
		} else {
			assertTrue(true);

		}
	}

}
