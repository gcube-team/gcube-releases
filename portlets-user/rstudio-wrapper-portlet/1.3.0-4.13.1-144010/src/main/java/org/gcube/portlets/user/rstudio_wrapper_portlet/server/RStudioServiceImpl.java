package org.gcube.portlets.user.rstudio_wrapper_portlet.server;

import static org.gcube.data.analysis.rconnector.client.Constants.rConnector;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.rstudio_wrapper_portlet.client.RStudioService;
import org.gcube.portlets.user.rstudio_wrapper_portlet.shared.RStudioInstance;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RStudioServiceImpl extends RemoteServiceServlet implements RStudioService {

	private static final Logger _log = LoggerFactory.getLogger(RStudioServiceImpl.class);

	private static final String RSTUDIO_INSTANCES = "RStudio-Instances";
	private static final String RSTUDIO_URL = "RStudio-URL";
	private static final String RCONNECTOR_EntryName = "org.gcube.data.analysis.rconnector.RConnector";
	private static final String PATH_TO_RCONNECTOR = "/r-connector/gcube/service/connect";

	private static final String SERVICE_EP_NAME = "RConnector";
	private static final String CATEGORY = "DataAnalysis";
	public static final String USER_ID_ATTR_NAME = "gcube-userId";
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
		//		user = "costantino.perciante";
		return user;
	}
	/**
	 * 
	 */
	@Override
	public String retrieveRStudioSecureURL() throws IllegalArgumentException {
		String toReturn = "";
		PortalContext pContext = PortalContext.getConfiguration();
		String userIdNo = getThreadLocalRequest().getHeader(USER_ID_ATTR_NAME);
		long userId = -1;
		if (userIdNo != null) {
			try {
				_log.debug("The userIdNo is " + userIdNo);
				userId = Long.parseLong(userIdNo);

			} catch (NumberFormatException e) {
				_log.error("The userId is not a number -> " + userId);
			} catch (Exception e) {
				_log.error("Could not read the current userid from header param, either session expired or user not logged in, exception: " + e.getMessage());
			}
		}
		GCubeUser curUser = null;
		try {
			curUser = new LiferayUserManager().getUserById(userId);
		} catch (UserManagementSystemException | UserRetrievalFault e1) {
			e1.printStackTrace();
		}
		String scope = pContext.getCurrentScope(getThreadLocalRequest());
		String token = pContext.getCurrentUserToken(scope, userId);
		try {

			_log.debug("calling rConnector with scope = " + scope + " and token = "+token );
			List<ServiceEndpoint> resources = getRStudioServiceEndpoints(scope);
			if (resources.size() == 0){
				_log.warn("There is no Service Endpoint having CATEGORY " + CATEGORY +" and NAME " + SERVICE_EP_NAME + " in this scope. Returning default instance");
				toReturn = rConnector().build().connect().toURL().toExternalForm();
			} else {
				UserManager um = new LiferayUserManager();
				_log.debug("Checking if user " + curUser.getFullname() + " has already an RStudio Instance set ... ");
				String hostedOnSet = null;
				
				//check if an RStudio instance was previously set (RETRO-COMPATIBILY)
				if (um.readCustomAttr(userId, RSTUDIO_URL) != null && um.readCustomAttr(userId, RSTUDIO_URL).toString().compareTo("") != 0) {
					_log.debug("User had already an RStudio Instance set, upgrading to new version ");
					hostedOnSet = (String) um.readCustomAttr(userId, RSTUDIO_URL);
					writeRStudioInstanceInScope(um, curUser, scope, hostedOnSet);
					um.saveCustomAttr(userId, RSTUDIO_URL, ""); //reset the old value to blank so that from now on it will read from the new  field
				}
				
				hostedOnSet = getUserRStudioInstances(um,curUser).get(scope);
				
				_log.info("**** Checking if still exist on this scope: " + scope);
				//if the instance exists and is still available in the given context
				if (hostedOnSet != null && checkRStudioInstanceExistence(curUser, hostedOnSet, resources) ) {
					toReturn = getRConnectorURL(hostedOnSet, token);
					_log.info("User " + curUser.getFullname() + " has RStudio Instance set and is valid, returning rConnector URL " + toReturn);					
				}
				else {//need to find the RStudio
					_log.info("User " + curUser.getFullname() + " DOES NOT have RStudio Instance set or the instance previous set no longer exists, calculating allocation ... ");
					HashMap<String, Integer> rStudioDistributionMap = new HashMap<>();
					for (ServiceEndpoint res : resources) {
						String hostedOn = res.profile().runtime().hostedOn();
						rStudioDistributionMap.put(hostedOn, 0);
					}
					List<GCubeUser> vreUsers = um.listUsersByGroup(pContext.getCurrentGroupId(getThreadLocalRequest()), false);
					_log.debug("VRE " + scope + " has totalUsers = " + vreUsers.size());
					for (GCubeUser gCubeUser : vreUsers) {
						if (getUserRStudioInstances(um,gCubeUser).get(scope) != null) {
							String hostedOn = getUserRStudioInstances(um,gCubeUser).get(scope);
							if (rStudioDistributionMap.containsKey(hostedOn)) {
								int noToSet = rStudioDistributionMap.get(hostedOn)+1;
								rStudioDistributionMap.put(hostedOn, noToSet);
							}
						}
					}
					_log.info("VRE - RStudio allocaiton map as follows: ");
					int min = 0;
					int i = 0;
					String host2Select = "";
					for (String host : rStudioDistributionMap.keySet()) {
						_log.info("Host " + host + " has # users=" + rStudioDistributionMap.get(host));
						if (i==0) {
							host2Select =  host;
							min = rStudioDistributionMap.get(host);
						} else {
							int usersNo = rStudioDistributionMap.get(host);
							if (usersNo < min) {
								_log.info("Host " + host + " has LESS users than " + host2Select + " updating");
								host2Select = host;
							}
						}
						i++;						
					}
					writeRStudioInstanceInScope(um, curUser, scope, host2Select);
					_log.debug("User " + curUser.getFullname() + " RStudio Instance set calculated = " + host2Select + " for context " + scope);
					toReturn = getRConnectorURL(host2Select, token);
					_log.debug("User " + curUser.getFullname() + " has RStudio Instance set, returning rConnector URL " + toReturn);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		_log.info("returning URL from rConnector = "+toReturn);
		return toReturn;
	}
	
	/**
	 * 
	 * @param um
	 * @param gCubeUser
	 * @return a map containing the scope and the RStudio Instances set for this user
	 * @throws UserRetrievalFault
	 */
	private Map<String, String> getUserRStudioInstances(UserManager um, GCubeUser gCubeUser) throws UserRetrievalFault {
		Map<String, String> theInstances = new HashMap<>();
		if (um.readCustomAttr(gCubeUser.getUserId(), RSTUDIO_INSTANCES) != null && um.readCustomAttr(gCubeUser.getUserId(), RSTUDIO_INSTANCES).toString().compareTo("") != 0) {
			String[] values = (String[]) um.readCustomAttr(gCubeUser.getUserId(), RSTUDIO_INSTANCES);
			if (values != null && values.length > 0) {
				for (int i = 0; i < values.length; i++) {
					String[] splits = values[i].split("\\|");
					RStudioInstance istance = new RStudioInstance(splits);
					theInstances.put(istance.getContext(), istance.getHostedOn());
				}					
			}
		}
		return theInstances;
	}
	/**
	 * write the RStudio Instance
	 * @param um
	 * @param gCubeUser
	 * @param context the infra scope
	 * @param hostedOn
	 * @throws UserRetrievalFault 
	 */
	private void writeRStudioInstanceInScope(UserManager um, GCubeUser gCubeUser, String context, String hostedOn) throws UserRetrievalFault {
		Map<String, String> theInstances = getUserRStudioInstances(um, gCubeUser);
		theInstances.put(context, hostedOn);
		String[] theValues = new String[theInstances.size()];
		int i = 0;
		for (String instanceScope : theInstances.keySet()) {
			String toPut = instanceScope+"|"+theInstances.get(instanceScope); //-> /gcube/devNext/NextNext|rstudio.d4science.org:443
			theValues[i] = toPut;
			i++;
		}
		//overwrite the values
		um.saveCustomAttr(gCubeUser.getUserId(), RSTUDIO_INSTANCES, theValues);
	}

	private String getRConnectorURL(String hostedOn, String token) throws MalformedURLException, IllegalArgumentException {
		String[] splits = hostedOn.split(":"); 
		int port = 80;
		try {
			port = Integer.parseInt(splits[1]);
		} catch (Exception e) {
			_log.warn("Could not find an integer after :,  using default port 80");
		}
		//http://rstudio-dev.d4science.org:80/r-connector/gcube/service/connect?gcube-token=6fa92d94-6568-4510-8443-a1c5ecdf1c7d-98187548s
		if (port == 443 || port == 8443)
			return "https://"+hostedOn+PATH_TO_RCONNECTOR+"?gcube-token="+token;
		else {
			return "http://"+hostedOn+PATH_TO_RCONNECTOR+"?gcube-token="+token;
		}
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
	
	private static boolean checkRStudioInstanceExistence(GCubeUser curUser, String hostedOn, List<ServiceEndpoint> resources) {
		for (ServiceEndpoint serviceEndpoint : resources) 
			if (serviceEndpoint.profile().runtime().hostedOn().equals(hostedOn)) {
				_log.info("**** The instance previously set for user " + curUser.getFullname() + " on  " + hostedOn + " is still valid");
				return true;
			}
		return false;
	}
}
