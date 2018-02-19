package org.gcube.portlet.user.my_vres.server;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.portal.auth.AuthUtil;
import org.gcube.portlet.user.my_vres.client.MyVREsService;
import org.gcube.portlet.user.my_vres.shared.AuthorizationBean;
import org.gcube.portlet.user.my_vres.shared.UserBelonging;
import org.gcube.portlet.user.my_vres.shared.VRE;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;
import org.gcube.vomanagement.usermanagement.util.ManagementUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 * @author Massimiliano Assante - ISTI CNR
 */
@SuppressWarnings("serial")
public class MyVREsServiceImpl extends RemoteServiceServlet implements	MyVREsService {

	private static final Logger _log = LoggerFactory.getLogger(MyVREsServiceImpl.class);
	/**
	 * 
	 */
	public static final String CACHED_VOS = "CACHED_VRES";
	/**
	 * needed when querying for authorised services in authentication
	 */
	public static final String REDIRECT_URL = "RedirectURL";

	public static final String ADD_MORE_CATEGORY = "Add More";
	public static final String ADD_MORE_IMAGE_PATH= "images/More.png";

	@Override
	public String getSiteLandingPagePath() {
		String toReturn = PortalContext.getConfiguration().getSiteLandingPagePath(getThreadLocalRequest())+GCubePortalConstants.VRES_EXPLORE_FRIENDLY_URL;
		return toReturn;
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}
	/**
	 * first method called by the UI
	 */
	public LinkedHashMap<String, ArrayList<VRE>> getUserVREs() {	
		//_log.trace("getInfrastructureVOs method called");
		if (!isWithinPortal())
			return getFakeVREs();
		//return new ArrayList<VO>();
		else 
			try {
				PortalContext context = PortalContext.getConfiguration();
				String username = context.getCurrentUser(getThreadLocalRequest()).getUsername();
				GroupManager gm = new LiferayGroupManager();

				LinkedHashMap<String, ArrayList<VRE>> toReturn = new LinkedHashMap<String, ArrayList<VRE>>();


				List<VirtualGroup> currentSiteVGroups = gm.getVirtualGroups(ManagementUtils.getSiteGroupIdFromServletRequest(getThreadLocalRequest().getServerName()));
				for (VirtualGroup vg : currentSiteVGroups) {
					String gName = vg.getName();
					ArrayList<VRE> toCreate = new ArrayList<VRE>();
					String cat = gName;
					toReturn.put(cat, toCreate);
				}


				GCubeGroup rootGroupVO = gm.getRootVO();
				try {
					_log.debug("root: " + rootGroupVO.getGroupName() );
				} catch (NullPointerException e) {
					_log.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder, unless your installing the Bundle");
					return toReturn;
				}

				//for each root sub organizations (VO)
				for (GCubeGroup vOrg : rootGroupVO.getChildren()) {
					for (GCubeGroup vre : vOrg.getChildren()) {
						VRE vreToAdd = new VRE();
						vreToAdd.setName(vre.getGroupName());
						vreToAdd.setContext(gm.getInfrastructureScope(vre.getGroupId()));
						long logoId = vre.getLogoId();
						String logoURL = gm.getGroupLogoURL(logoId);
						vreToAdd.setImageURL(logoURL);
						String vreUrl = GCubePortalConstants.PREFIX_GROUP_URL+vre.getFriendlyURL();
						vreToAdd.setFriendlyURL(vreUrl);

						vreToAdd.setUserBelonging(UserBelonging.NOT_BELONGING);
						GCubeUser currUser = new LiferayUserManager().getUserByUsername(username);

						if (gm.listGroupsByUser(currUser.getUserId()).contains(vre)) {
							vreToAdd.setUserBelonging(UserBelonging.BELONGING);

							List<VirtualGroup> vreGroups =  gm.getVirtualGroups(vre.getGroupId());
							for (VirtualGroup vreGroup : vreGroups) {
								for (String category : toReturn.keySet()) {
									//for preserving order we inserted the keys before
									if (vreGroup.getName().compareTo(category)==0) {
										ArrayList<VRE> toUpdate = toReturn.get(category);
										toUpdate.add(vreToAdd);
									}				
								}
							}




						}		
					}
				}

				//sort the vres in the groups
				for (String cat : toReturn.keySet()) {
					ArrayList<VRE> toSort = toReturn.get(cat);
					Collections.sort(toSort);
				}
				HttpServletRequest request = getThreadLocalRequest();
				String gatewayURL = context.getGatewayURL(request);
				String exploreURL = gatewayURL+context.getSiteLandingPagePath(request)+GCubePortalConstants.VRES_EXPLORE_FRIENDLY_URL;
				VRE addMore = new VRE("", "", "", "", exploreURL, UserBelonging.BELONGING);
				//add a fake category and addMoreVRE
				ArrayList<VRE> addMoreVREs = new ArrayList<VRE>();
				addMoreVREs.add(addMore);
				toReturn.put(ADD_MORE_CATEGORY, addMoreVREs);


				return toReturn;

			} 
		catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * simply returns fake VREs for development purpose
	 * @return
	 */
	protected static LinkedHashMap<String, ArrayList<VRE>> getFakeVREs() {
		LinkedHashMap<String, ArrayList<VRE>> toReturn = new LinkedHashMap<String, ArrayList<VRE>>();

		final String categoryNameOne = "gCubeApps";	
		final String categoryNameTwo = "BlueBRIDGE";	
		final String categoryNameThree = "GEMex";	
		//			
		VRE cool_EM_VRE = new VRE();
		cool_EM_VRE.setName("BiodiversityResearchEnvironment");
		cool_EM_VRE.setContext("/d4science.research-infrastructures.eu/EM/COOLEMVRE");
		cool_EM_VRE.setDescription("cool_EM_VRE VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");
		cool_EM_VRE.setImageURL("https://placehold.it/150x150");
		cool_EM_VRE.setUserBelonging(UserBelonging.BELONGING);


		VRE cool_EM_VRE2 = new VRE();
		cool_EM_VRE2.setName("COOL VRE 2");
		cool_EM_VRE2.setContext("/d4science.research-infrastructures.eu/EM/COOLEMVRE2");
		cool_EM_VRE2.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE2.setImageURL("https://placehold.it/150x150");
		cool_EM_VRE2.setUserBelonging(UserBelonging.NOT_BELONGING);

		VRE cool_EM_VRE3 = new VRE();
		cool_EM_VRE3.setName("COOL EM VRE TRE");
		cool_EM_VRE3.setContext("/d4science.research-infrastructures.eu/EM/COOlVRE3");
		cool_EM_VRE3.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		cool_EM_VRE3.setImageURL("https://placehold.it/150x150");
		cool_EM_VRE3.setUserBelonging(UserBelonging.BELONGING);

		ArrayList<VRE> toAdd = new ArrayList<VRE>();
		toAdd.add(cool_EM_VRE);
		toAdd.add(cool_EM_VRE2);
		toAdd.add(cool_EM_VRE3);
		toAdd.add(cool_EM_VRE);
		toAdd.add(cool_EM_VRE2);
		toAdd.add(cool_EM_VRE3);


		VRE demo = new VRE();
		demo.setName("Demo");
		demo.setContext("/d4science.research-infrastructures.eu/EM/Demo");
		demo.setDescription("Cool VRE Description<br />"+ 
				"This Virtual Research Environment is for cool authors, managers and researchers who produce reports containing cool data.");

		demo.setImageURL("http://portal.d4science.research-infrastructures.eu/vologin/html/RedGrid.jpg");
		demo.setUserBelonging(UserBelonging.BELONGING);

		VRE vreGCM = new VRE();
		vreGCM.setName("GCM");
		vreGCM.setContext("/d4science.research-infrastructures.eu/EM/GCM");
		vreGCM.setDescription("Global Ocean Chlorophyll Monitoring (GCM) Virtual Research Environment<br />" 
				+ "The phytoplankton plays a similar role to terrestrial green plants in the photosynthetic process and are credited with removing as much carbon dioxide from the atmosphere as their earthbound counterparts, making it important to monitor and model plankton into calculations of future climate change.");
		vreGCM.setImageURL("https://placehold.it/150x150");
		vreGCM.setUserBelonging(UserBelonging.BELONGING);

		ArrayList<VRE> toAdd2 = new ArrayList<VRE>();
		toAdd2.add(demo);
		toAdd2.add(vreGCM);
		toAdd2.add(cool_EM_VRE3);
		toAdd2.add(cool_EM_VRE);
		toAdd2.add(cool_EM_VRE2);
		toAdd2.add(cool_EM_VRE3);

		ArrayList<VRE> toAdd3 = new ArrayList<VRE>();
		toAdd3.add(demo);
		toAdd3.add(vreGCM);
		toAdd3.add(cool_EM_VRE2);
		toAdd3.add(cool_EM_VRE3);
		toAdd3.add(cool_EM_VRE);
		toAdd3.add(cool_EM_VRE2);
		toAdd3.add(cool_EM_VRE3);

		toReturn.put(categoryNameOne, toAdd);
		toReturn.put(categoryNameTwo, toAdd2);
		toReturn.put(categoryNameThree, toAdd3);

		return toReturn;
	}

	@Override
	public AuthorizationBean getOAuthTempCode(String context, String state, String clientId, String redirectURL) {
		String infraName = PortalContext.getConfiguration().getInfrastructureName();
		if (clientId == null || clientId.compareTo("")== 0) {
			return new AuthorizationBean(null, null, false, "client_id is null, you MUST register your application to allow users connect with their D4Science Credentials");
		}
		if (redirectURL == null || redirectURL.compareTo("")== 0) {
			return new AuthorizationBean(null, null, false, "authorised redirect URL is null, you MUST pass the authorisedRedirectURI related to your client_id registered application to allow users connect with their D4Science Credentials");
		}
		ServiceEndpoint authorisedApp = null;
		try {
			authorisedApp = AuthUtil.getAuthorisedApplicationInfoFromIsICClient(infraName, clientId);
		} catch (Exception e1) {
			e1.printStackTrace();
			return new AuthorizationBean(null, null, false, "Ops!, we failed to check if ("+ clientId + ") is a valid clientId, some error occurred, please try in a few minutes. If the problem persists please open an incident ticket");
		}
		if (authorisedApp == null) {
			return new AuthorizationBean(null, null, false, "Your application ("+ clientId + ") is not authorized in the infrastructure");
		}
		List<String> authorisedRedirectURLs = AuthUtil.getAuthorisedRedirectURLsFromIs(authorisedApp);
		if (authorisedRedirectURLs == null || authorisedRedirectURLs.isEmpty()) {
			return new AuthorizationBean(null, null, false, "Your application ("+ clientId + ") have no authorised redirect URLs");
		}
		boolean urlAuthorised = false;
		for (String authorisedURL : authorisedRedirectURLs) 
			if (authorisedURL.compareTo(redirectURL)==0) {
				urlAuthorised = true;
				break;
			}
		if (! urlAuthorised)
			return new AuthorizationBean(null, null, false, "Invalid redirect URL. This value must match a URL registered with the clientId: " + clientId);
		if (state == null || state.compareTo("")== 0) {
			return new AuthorizationBean(null, null, false, "State is null, please use a unique string value of your choice that is hard to guess (e.g. state=7d12bf13-111c-4f46-ab06-9e9e08ad377b). Used to prevent CSRF attacks");
		}
		if (context == null || context.compareTo("")== 0) {
			return new AuthorizationBean(null, null, false, "Infrastructure Context is null");
		}
		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser currentUser = pContext.getCurrentUser(getThreadLocalRequest());
		String username = currentUser.getUsername();
		long userId = currentUser.getUserId();
		_log.debug("Creating AuthorizationBean for user " + username);
		if (isWithinPortal()) {
			GroupManager gm = new LiferayGroupManager();
			try {
				long groupId = gm.getGroupIdFromInfrastructureScope(context);
				_log.debug("Verifying user permission for scope " + context);
				if (! gm.listGroupsByUser(userId).contains(gm.getGroup(groupId))) {
					return new AuthorizationBean(null, null, false, "User having username: " + username + " is not authorised in context: " + context);
				}
			} catch (IllegalArgumentException | UserManagementSystemException | GroupRetrievalFault e) {
				_log.error("Something wrong in the Context parameter: " + e.getMessage());
				return new AuthorizationBean(null, null, false, "Something wrong in the Context parameter: " + e.getMessage());
			} catch (UserRetrievalFault e) {
				return new AuthorizationBean(null, null, false, "Something wrong in the user retrieval " + e.getMessage());
			}
		}

		if (username == null) {
			_log.error("Something wrong in retrieving the user");
			return new AuthorizationBean(null, null, false, "Something wrong in retrieving the logged in user, is session expired?");
		}
		//no errors, proceed to step 2
		String userToken = pContext.getCurrentUserToken(context, username);
		String appName = authorisedApp.profile().name();
		String qToken = AuthUtil.generateAuthorizationQualifierToken(appName, userToken);
		_log.debug("Received qualifier token for useer " + username + "=" + qToken);
		if (qToken == null) {
			_log.error("Something wrong in retrieving the user qualifier token in this context: " + context + " username="+username);
			return new AuthorizationBean(null, null, false, "Something wrong in retrieving the user qualifier token in this context: " + context + " username="+username);
		}
		String tempCode = UUID.randomUUID().toString();
		if (! authorizeApplication(infraName, qToken, tempCode, clientId, redirectURL)) {
			_log.error("Something wrong in authorizing this application in this context: " + context );
			return new AuthorizationBean(null, null, false, "Something wrong in authorizing this application in this context: " + context + " an error occurred in the oAuth Service");
		}

		_log.debug("Authorisation OAUTH 2 OK returning temporary code in this context: " + context + " username="+username);
		return new AuthorizationBean(tempCode, state, true, null);
	}
	/**
	 * we post to the auth service a temporary code to be used within seconds from the application
	 * @param qToken the user qualifier token
	 * @param tempCode the temporary code
	 * @param clientId tha app id
	 * @param redirectURI the authorised redirect URI
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean authorizeApplication(String infrastructureName, String qToken, String tempCode, String clientId, String redirectURL) {

		String fullPath2oAuthService = null;
		try {
			fullPath2oAuthService = AuthUtil.getOAuthServiceEndPoint(infrastructureName) + 
					"/v2/push-authentication-code?gcube-token=" + qToken;
		} catch (Exception e1) {
			_log.error("failed to discover oauth service endpoint ");
			return false;			
		}

		JSONObject object = new JSONObject();
		object.put("code", tempCode);
		object.put("redirect_uri", redirectURL);
		object.put("client_id", clientId);
		try {
			String USER_AGENT = "Mozilla/5.0";  
			URL obj = new URL(fullPath2oAuthService);  
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();  

			// Setting basic post request  
			con.setRequestMethod("POST");  
			con.setRequestProperty("User-Agent", USER_AGENT);  
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");  
			con.setRequestProperty("Content-Type","application/json");  

			String postJsonData = object.toJSONString();

			con.setDoOutput(true);  
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());  
			wr.writeBytes(postJsonData);  
			wr.flush();  
			wr.close();  

			int responseCode = con.getResponseCode();  

			boolean redirect = false;
			// normally, 3xx is redirect
			int status = con.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}

			if (redirect) 
				// get redirect url from "location" header field
				fullPath2oAuthService = con.getHeaderField("Location");		

			CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
			HttpPost httpPostRequest = new HttpPost(fullPath2oAuthService);
			httpPostRequest.addHeader("Content-type", "application/json");
			StringEntity params = new StringEntity(object.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);
			HttpResponse response = httpClient.execute(httpPostRequest);
			responseCode = response.getStatusLine().getStatusCode();
			if (responseCode < 200 || responseCode >= 300) {
				_log.error("error: response status line from "
						+ fullPath2oAuthService + " was: " + responseCode);
				return false;				
			}
			//			
		}catch(Exception e){
			_log.error("Failed to perform request", e);
			return false;
		}

		return true;
	}



}