package org.gcube.portal.plugins.thread;
import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class RemoveUserTokenFromVREThread implements Runnable {
	private static Log _log = LogFactoryUtil.getLog(RemoveUserTokenFromVREThread.class);
	private String username;
	private String scope;
	/**
	 * 
	 * @param username
	 * @param scope
	 */
	public RemoveUserTokenFromVREThread(String username, String scope) {
		super();
		this.username = username;
		this.scope = scope;
	}

	@Override
	public void run() {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		try {
			String userToken = authorizationService().resolveTokenByUserAndContext(username, scope);
			SecurityTokenProvider.instance.set(userToken);
			authorizationService().removeAllReleatedToken(username, scope);
			_log.info("*** Removed user token " + username + " in " + scope);
		} catch (Exception e) {
			_log.error("Could not remove user token " + username + " in " + scope, e);
		}
		ScopeProvider.instance.set(currScope); //restore the scope in ThreadLocal
	}

}


