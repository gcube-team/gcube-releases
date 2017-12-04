/**
 *
 */
package org.gcube.portlets.user.workspace.server.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingSite;
import org.gcube.applicationsupportlayer.social.shared.SocialNetworkingUser;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;
import org.gcube.portlets.user.workspace.server.notifications.NotificationsProducer;
import org.gcube.portlets.user.workspace.server.resolver.UriResolverReaderParameterForResolverIndex;
import org.gcube.portlets.user.workspace.server.resolver.UriResolverReaderParameterForResolverIndex.RESOLVER_TYPE;
import org.gcube.portlets.user.workspace.server.util.resource.PropertySpecialFolderReader;
import org.gcube.portlets.user.workspace.server.util.scope.ScopeUtilFilter;
import org.gcube.portlets.user.workspace.shared.TransferOnThreddsReport;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * The Class WsUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 25, 2016
 */
public class WsUtil {

	/**
	 *
	 */
	public static final String FOLDER_PUBLISHING_ON_THREDDS = "FolderPublishingOnThredds";

	protected static Logger logger = Logger.getLogger(WsUtil.class);

	public static final String FOLDERIMPORTER_ATTRIBUTE = "FOLDER_IMPORTER";
	public static final String METADATACONVERTER_ATTRIBUTE = "METADATA_CONVERTER";
	public static final String WORKSPACE_EVENT_COLLECTOR_ATTRIBUTE = "EVENT_COLLECTOR";
	public static final String WORKSPACEBUILDER_ATTRIBUTE = "WORKSPACEBUILDER";
	public static final String NOTIFICATION_MANAGER = "NOTIFICATION_MANAGER";
	public static final String NOTIFICATION_PRODUCER = "NOTIFICATION_PRODUCER";
	public static final String WS_RUN_IN_TEST_MODE = "WS_RUN_IN_TEST_MODE";
	public static final String WORKSPACE_SCOPE_UTIL = "WORKSPACE_SCOPE_UTIL";
	public static final String URL_SHORTENER_SERVICE = "URL_SHORTENER_SERVICE";
	public static final String URI_RESOLVER_SERVICE = "URI_RESOLVER_SERVICE";
	public static final String PROPERTY_SPECIAL_FOLDER = "PROPERTY_SPECIAL_FOLDER";
	public static final String NOTIFICATION_PORTLET_CLASS_ID = "org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl"; //IN DEV

	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		}
		catch (Exception ex) {
			logger.trace("Development Mode ON");
			return false;
		}
	}


	/**
	 * Gets the portal context.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest httpServletRequest){
		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(httpServletRequest);
		String username = user.getUsername();
		String fullName = user.getFullname();
		String email = user.getEmail();
		String avatarID = user.getUserAvatarId();
		String avatarURL = user.getUserAvatarURL();
		//SESSION
		String currentScope = pContext.getCurrentScope(httpServletRequest);
		String userToken = pContext.getCurrentUserToken(httpServletRequest);
		long currGroupId = pContext.getCurrentGroupId(httpServletRequest);

		return new PortalContextInfo(username, fullName, email, avatarID, avatarURL, currentScope, userToken, currGroupId);
	}


	/**
	 * Gets the portal context.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param overrideScope the override scope
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest httpServletRequest, String overrideScope){
		PortalContextInfo info = getPortalContext(httpServletRequest);
		info.setCurrentScope(overrideScope);
		return info;
	}


	/**
	 * Checks if is session expired.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return true, if is session expired
	 * @throws Exception the exception
	 */
	public static boolean isSessionExpired(HttpServletRequest httpServletRequest) throws Exception {
		logger.trace("workspace session validating...");
		return PortalContext.getConfiguration().getCurrentUser(httpServletRequest)==null;
	}


	/**
	 * Gets the workspace.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(HttpServletRequest httpServletRequest) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		logger.trace("Get Workspace");
		PortalContextInfo info = getPortalContext(httpServletRequest);
		logger.trace("PortalContextInfo: "+info);

		ScopeProvider.instance.set(info.getCurrentScope());
		logger.trace("Scope provider instancied");

		Workspace workspace = HomeLibrary.getUserWorkspace(info.getUsername());
		return workspace;
	}


	/**
	 * Gets the workspace.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param contextID the context id
	 * @param user the user
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	public static Workspace getWorkspace(HttpServletRequest httpServletRequest, String contextID, GCubeUser user) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		logger.info("Get workspace using contextID: "+contextID +", current user: "+user.getUsername());
		String currentScope;

		if(isWithinPortal())
			currentScope = PortalContext.getConfiguration().getCurrentScope(contextID);
		else{
			currentScope = PortalContext.getConfiguration().getCurrentScope(httpServletRequest);
			logger.warn("STARTING IN TEST MODE!!!! USING SCOPE: "+currentScope);
		}

		logger.info("For ContextID: "+contextID +", read scope from Portal Context: "+currentScope);
		PortalContextInfo info = getPortalContext(httpServletRequest, currentScope);
		logger.trace("PortalContextInfo: "+info);

		ScopeProvider.instance.set(info.getCurrentScope());
		logger.trace("Scope provider instancied");

		String username = null;
		try {
			if(user.getUsername().compareTo(info.getUsername())!=0){
				logger.debug("Gcube user read from Portal Context "+user.getUsername()+" is different by GCubeUser passed, using the second one: "+info.getUsername());
				username = user.getUsername();
			}

		} catch (Exception e) {
			logger.error("Error comparing username read from input parameter and Portal context");
		}

		if(username!=null)
			info.setUsername(username);

		Workspace workspace = HomeLibrary.getUserWorkspace(info.getUsername());
		return workspace;

	}

	/**
	 * Gets the GWT workspace builder.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the GWT workspace builder
	 */
	public static GWTWorkspaceBuilder getGWTWorkspaceBuilder(HttpServletRequest httpServletRequest)
	{
		PortalContextInfo info = getPortalContext(httpServletRequest);
		logger.trace("PortalContextInfo: "+info);

		HttpSession session = httpServletRequest.getSession();

		GWTWorkspaceBuilder builder = (GWTWorkspaceBuilder) session.getAttribute(WORKSPACEBUILDER_ATTRIBUTE);
		if (builder == null){
			logger.info("Initializing the workspace area builder");
			builder = new GWTWorkspaceBuilder();
			//ADDED 03/09/2013
			builder.setUserLogged(new InfoContactModel(info.getUsername(), info.getUsername(), info.getUserFullName(), false));
			session.setAttribute(WORKSPACEBUILDER_ATTRIBUTE, builder);
		}

		return builder;
	}


	/**
	 * Gets the notification manager.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the notification manager
	 */
	public static NotificationsManager getNotificationManager(HttpServletRequest httpServletRequest)
	{
		PortalContextInfo info = getPortalContext(httpServletRequest);
		HttpSession session = httpServletRequest.getSession();
		NotificationsManager notifMng = (NotificationsManager) session.getAttribute(NOTIFICATION_MANAGER);

		if (notifMng == null) {
			try{
				logger.trace("Create new NotificationsManager for user: "+info.getUsername());
				logger.trace("New ApplicationNotificationsManager with portlet class name: "+NOTIFICATION_PORTLET_CLASS_ID);
				SocialNetworkingSite site = new SocialNetworkingSite(httpServletRequest);
				SocialNetworkingUser curser = new SocialNetworkingUser(info.getUsername(), info.getUserEmail(), info.getUserFullName(), info.getUserAvatarID());
				notifMng = new ApplicationNotificationsManager(site, info.getCurrentScope(), curser, NOTIFICATION_PORTLET_CLASS_ID);
				session.setAttribute(NOTIFICATION_MANAGER, notifMng);
			}catch (Exception e) {
				logger.error("An error occurred instancing ApplicationNotificationsManager for user: "+info.getUsername(),e);
			}
		}

		return notifMng;
	}

	/**
	 * Gets the notification producer.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the notification producer
	 */
	public static NotificationsProducer getNotificationProducer(HttpServletRequest httpServletRequest)
	{
		PortalContextInfo info = getPortalContext(httpServletRequest);
		HttpSession session = httpServletRequest.getSession();
		NotificationsProducer notifProducer = (NotificationsProducer) session.getAttribute(NOTIFICATION_PRODUCER);

		if (notifProducer == null) {
			logger.trace("Create new Notification Producer for user: "+info.getUsername());
			notifProducer = new NotificationsProducer(httpServletRequest);
			session.setAttribute(NOTIFICATION_PRODUCER, notifProducer);
		}
		return notifProducer;
	}


	/**
	 * Gets the user id.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the user id
	 */
	public static String getUserId(HttpServletRequest httpServletRequest) {

		PortalContextInfo info = getPortalContext(httpServletRequest);
		return info.getUsername();
	}

	/**
	 * Checks if is vre.
	 *
	 * @param scope the scope
	 * @return true, if is vre
	 */
	public static boolean isVRE(String scope){

		int slashCount = StringUtils.countMatches(scope, "/");

		if(slashCount < 3){
			logger.trace("currentScope is not VRE");
			return false;
		}

		logger.trace("currentScope is VRE");
		return true;

	}

	/**
	 * Gets the scope util filter.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the scope util filter
	 */
	public static ScopeUtilFilter getScopeUtilFilter(HttpServletRequest httpServletRequest){

		PortalContextInfo info = getPortalContext(httpServletRequest);
		HttpSession session = httpServletRequest.getSession();
		ScopeUtilFilter scopeUtil = null;
		try{
			scopeUtil = (ScopeUtilFilter) session.getAttribute(WsUtil.WORKSPACE_SCOPE_UTIL);

			if(scopeUtil==null){
				scopeUtil = new ScopeUtilFilter(info.getCurrentScope(),true);

			}
		}catch (Exception e) {
			logger.error("an error occurred in getscope filter "+e);
		}

		return scopeUtil;
	}


	/**
	 * Gets the url shortener.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the url shortener
	 */
	public static UrlShortener getUrlShortener(HttpServletRequest httpServletRequest) {

		HttpSession session = httpServletRequest.getSession();
		PortalContextInfo info = getPortalContext(httpServletRequest);
		UrlShortener shortener = null;
		try{
			shortener = (UrlShortener) session.getAttribute(WsUtil.URL_SHORTENER_SERVICE);

			if(shortener==null){
				shortener = new UrlShortener();
				session.setAttribute(URL_SHORTENER_SERVICE, shortener);
			}

		}catch (Exception e) {
			logger.error("an error occurred in instancing url shortener ",e);
		}

		return shortener;
	}


	/**
	 * Gets the uri resolver.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the uri resolver
	 */
	public static UriResolverReaderParameterForResolverIndex getUriResolver(HttpServletRequest httpServletRequest) {

		HttpSession session = httpServletRequest.getSession();
		PortalContextInfo info = getPortalContext(httpServletRequest);
		UriResolverReaderParameterForResolverIndex uriResolver = null;
		try{
			uriResolver = (UriResolverReaderParameterForResolverIndex) session.getAttribute(WsUtil.URI_RESOLVER_SERVICE);

			if(uriResolver==null){
				uriResolver = new UriResolverReaderParameterForResolverIndex(info.getCurrentScope(),RESOLVER_TYPE.SMP_ID);
				session.setAttribute(URI_RESOLVER_SERVICE, uriResolver);
			}

		}catch (Exception e) {
			logger.error("an error occurred instancing URI Resolver ",e);
		}

		return uriResolver;
	}

	/**
	 * Gets the property special folder reader.
	 *
	 * @param httpServletRequest the http servlet request
	 * @param pathProperty the path property
	 * @return the property special folder reader
	 */
	public static PropertySpecialFolderReader getPropertySpecialFolderReader(HttpServletRequest httpServletRequest, String pathProperty) {
		HttpSession session = httpServletRequest.getSession();

		PropertySpecialFolderReader psFolderReader = null;
		try{
			psFolderReader = (PropertySpecialFolderReader) session.getAttribute(WsUtil.PROPERTY_SPECIAL_FOLDER);

			if(psFolderReader==null){
				psFolderReader = new PropertySpecialFolderReader(pathProperty);
				session.setAttribute(PROPERTY_SPECIAL_FOLDER, psFolderReader);
			}

		}catch (Exception e) {
			logger.error("an error occurred instancing PropertySpecialFolderReader ",e);
		}

		return psFolderReader;
	}


	/**
	 * Sets the folder publishing on thredds.
	 *
	 * @param session the session
	 * @param report the report
	 */
	public static void setTransferPublishingOnThredds(HttpSession session, TransferOnThreddsReport report){

			Map<String, TransferOnThreddsReport> map = getMapTransferPublishingOnThredds(session);

			if(map!=null)
				map.put(report.getTransferId(), report);
	}


	/**
	 * Gets the folder publishing on thredds.
	 *
	 * @param session the session
	 * @return the folder publishing on thredds
	 */
	public static Map<String, TransferOnThreddsReport> getMapTransferPublishingOnThredds(HttpSession session){

		Map<String, TransferOnThreddsReport> map = null;
		try{

			map = (Map<String,TransferOnThreddsReport>) session.getAttribute(FOLDER_PUBLISHING_ON_THREDDS);

			if(map==null){
				logger.info("Creating new map to trace publishing on thredds... ");
				map = new HashMap<String, TransferOnThreddsReport>();
				session.setAttribute(FOLDER_PUBLISHING_ON_THREDDS, map);
			}

		}catch (Exception e) {
			logger.error("an error occurred instancing PropertySpecialFolderReader ",e);
		}

		return map;

	}


	/**
	 * Ge transfer publishing on thredds for id.
	 *
	 * @param session the session
	 * @param transferId the transfer id
	 * @return the transfer on thredds report
	 */
	public static TransferOnThreddsReport geTransferPublishingOnThreddsForId(HttpSession session, String transferId){

		Map<String, TransferOnThreddsReport> map = getMapTransferPublishingOnThredds(session);

		if(map!=null){
			return  map.get(transferId);
		}

		return null;

	}


}
