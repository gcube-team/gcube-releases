package org.gcube.portlets.user.rstudio_wrapper_portlet.server;

import static org.gcube.data.analysis.rconnector.client.Constants.rConnector;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.rstudio_wrapper_portlet.client.RStudioService;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RStudioServiceImpl extends RemoteServiceServlet implements RStudioService {

	private static final Logger _log = LoggerFactory.getLogger(RStudioServiceImpl.class);
	private static final String SERVICE_EP_NAME = "RConnector";
	private static final String CATEGORY = "DataAnalysis";
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting testing user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
//		user = "costantino.perciante";
		return user;
	}
	@Override
	public String retrieveRStudioSecureURL() throws IllegalArgumentException {
		String toReturn = "";
		String token = getASLSession().getSecurityToken();
		try {
			String scope = getASLSession().getScope();
			_log.debug("calling rConnector with scope = " + scope + " and token = "+token );
			List<ServiceEndpoint> resources = getRStudioServiceEndpoints(scope);
			if (resources.size() > 1) {
				_log.error("Too many Service Endpoints having name " + SERVICE_EP_NAME +" in this scope: " + scope);
				throw new TooManyRStudioResourcesException("There exist more than 1 Runtime Resource in this scope having name " 
						+ SERVICE_EP_NAME + " and Category " + CATEGORY + ". Only one allowed per scope.");
			}
			else if (resources.size() == 0){
				_log.warn("There is no Service Endpoint having name " + SERVICE_EP_NAME +" and Category " + CATEGORY + " in this scope. Returning default instance");
				toReturn = rConnector().build().connect().toURL().toExternalForm();
			} else {
				ServiceEndpoint res = resources.get(0);
				String hostedOn = res.profile().runtime().hostedOn();
				String[] splits = hostedOn.split(":"); 
				String host = splits[0]; 
				int port = 80;
				try {
					port = Integer.parseInt(splits[1]);
				} catch (Exception e) {
					_log.warn("Could not find an integer after :,  using default port 80");
				}
				toReturn = 	rConnector().at(host, port).build().connect().toURL().toExternalForm();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		_log.debug("returning URL from rConnector = "+toReturn);
		return toReturn;
	}
	
	/**
	 * 
	 * @return the
	 * @throws Exception
	 */
	private List<ServiceEndpoint> getRStudioServiceEndpoints(String scope) throws Exception  {
		_log.debug("getRStudioServiceEndpoints on scope="+scope );
		String currScope = 	ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ SERVICE_EP_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		ScopeProvider.instance.set(currScope);
		return toReturn;
	}	

}
