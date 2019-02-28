package org.gcube.portlets.user.accountingdashboard;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.accountingdashboard.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class AuthTest {
	private static Logger logger = LoggerFactory.getLogger(AuthTest.class);

	public static void setToken(){
		try{
			AuthorizationEntry entry = authorizationService().get(Constants.DEFAULT_TOKEN);
			ScopeProvider.instance.set(entry.getContext());
			SecurityTokenProvider.instance.set(Constants.DEFAULT_TOKEN);
		}catch(Throwable e) {
			logger.error("Unable to set token: "+e.getLocalizedMessage(),e);
			throw new RuntimeException("Unable to set token: "+Constants.DEFAULT_TOKEN,e);
		}
	}
}
