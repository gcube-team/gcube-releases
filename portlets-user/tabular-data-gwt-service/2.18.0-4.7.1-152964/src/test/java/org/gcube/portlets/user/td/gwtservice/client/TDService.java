package org.gcube.portlets.user.td.gwtservice.client;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TDService {
	
	private static Logger logger = LoggerFactory.getLogger(TDService.class);

	public TabularDataService getService() {
		logger.debug("DEFAULT: " + Constants.DEFAULT_USER + ", "
				+ Constants.DEFAULT_SCOPE + ", " + Constants.DEFAULT_TOKEN);

		//AuthorizationToken authToken = new AuthorizationToken(
		//		Constants.DEFAULT_USER);
		SecurityTokenProvider.instance.set(Constants.DEFAULT_TOKEN);
		ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);
		//AuthorizationProvider.instance.set(authToken);
		TabularDataService service = TabularDataServiceFactory.getService();
		return service;
	}
	
}
