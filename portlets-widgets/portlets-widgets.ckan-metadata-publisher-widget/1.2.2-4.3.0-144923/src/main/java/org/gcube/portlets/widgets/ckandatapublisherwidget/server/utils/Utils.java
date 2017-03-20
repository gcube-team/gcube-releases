package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.ApplicationProfileScopePerUrlReader;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.SessionCatalogueAttributes;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.UtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataValidator;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataVocabulary;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.CKANPublisherServicesImpl;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DataType;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataTypeWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetadataFieldWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.model.CkanOrganization;

/**
 * Util class with static methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Utils {

	// Logger
	//private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Utils.class);
	private static final Log logger = LogFactoryUtil.getLog(Utils.class);
	public static final String GCUBE_REQUEST_URL = "gcube-request-url";

	/**
	 * Retrieve the highest ckan role the user has and also retrieve the list of organizations (scopes) in which the user has the ckan-admin or ckan-editor role
	 * @param currentScope the current scope 
	 * @param username  the current username
	 * @param groupName the current groupName
	 * @param gcubeCkanDataCatalogServiceImpl 
	 * @param orgsInWhichAdminRole 
	 * @param ckanUtils ckanUtils
	 */
	public static RolesCkanGroupOrOrg getHighestRole(String currentScope, String username, String groupName, CKANPublisherServicesImpl gcubeCkanDataCatalogServiceImpl, List<OrganizationBean> orgsInWhichAtLeastEditorRole){

		// base role as default value
		RolesCkanGroupOrOrg toReturn = RolesCkanGroupOrOrg.MEMBER;

		try{

			UserManager userManager = new LiferayUserManager();
			RoleManager roleManager = new LiferayRoleManager();
			GroupManager groupManager = new LiferayGroupManager();

			// user id
			long userid = userManager.getUserId(username);

			// retrieve current group id
			long currentGroupId = groupManager.getGroupIdFromInfrastructureScope(currentScope);

			logger.debug("Group id is " + currentGroupId + " and scope is " + currentScope);

			// retrieve the flat list of organizations for the current user
			List<GCubeGroup> groups = groupManager.listGroupsByUser(userid);

			// root (so check into the root, the VOs and the VRES)
			if(groupManager.isRootVO(currentGroupId)){

				logger.info("The current scope is the Root Vo, so the list of organizations of the user " + username + " is " + groups);

				for (GCubeGroup gCubeGroup : groups) {

					if(!groupManager.isVRE(gCubeGroup.getGroupId()))
						continue;

					// get the name of this group
					String gCubeGroupName = gCubeGroup.getGroupName();

					// get the role of the users in this group
					List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userid, groupManager.getGroupId(gCubeGroupName));

					// get highest role
					RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

					// be sure it is so
					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, gCubeGroup.getGroupId(), 
							correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

					toReturn = RolesCkanGroupOrOrg.getHigher(toReturn, correspondentRoleToCheck);

				}

			}else if(groupManager.isVO(currentGroupId)){

				logger.debug("The list of organizations of the user " + username + " to scan is the one under the VO " + groupName);

				for (GCubeGroup gCubeGroup : groups) {

					// if the gCubeGroup is not under the VO or it is not the VO continue
					if(currentGroupId != gCubeGroup.getParentGroupId() || currentGroupId != gCubeGroup.getGroupId())
						continue;

					String gCubeGroupName = gCubeGroup.getGroupName();

					List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userid, groupManager.getGroupId(gCubeGroupName));

					// get highest role
					RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

					// be sure it is so
					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, gCubeGroup.getGroupId(), 
							correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

					toReturn = RolesCkanGroupOrOrg.getHigher(toReturn, correspondentRoleToCheck);
				}

			}else if(groupManager.isVRE(currentGroupId)){
				List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userManager.getUserId(username), groupManager.getGroupId(groupName));

				logger.debug("The current scope is the vre " + groupName);

				// get highest role
				RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

				// be sure it is so
				checkIfRoleIsSetInCkanInstance(username, groupName, currentGroupId, 
						correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

				toReturn = correspondentRoleToCheck;

			}
		}catch(Exception e){
			logger.error("Unable to retrieve the role information for this user. Returning member role", e);
			return RolesCkanGroupOrOrg.MEMBER;
		}

		// return the role
		logger.debug("Returning role "  + toReturn + " for user " + username);
		return toReturn;
	}

	/**
	 * Check if the role admin is set or must be set into the ckan instance at this scope
	 * @param username
	 * @param gCubeGroupName
	 * @param groupId
	 * @param correspondentRoleToCheck
	 * @param toReturn
	 * @param groupManager
	 * @param ckanPublisherServicesImpl
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	private static void checkIfRoleIsSetInCkanInstance(String username,
			String gCubeGroupName, long groupId,
			RolesCkanGroupOrOrg correspondentRoleToCheck,
			GroupManager groupManager, CKANPublisherServicesImpl ckanPublisherServicesImpl, List<OrganizationBean> orgs) throws UserManagementSystemException, GroupRetrievalFault {

		// with this invocation, we check if the role is present in ckan and if it is not it will be added
		DataCatalogue catalogue = ckanPublisherServicesImpl.getCatalogue(groupManager.getInfrastructureScope(groupId));

		// if there is an instance of ckan in this scope..
		if(catalogue != null){
			boolean res = catalogue.checkRoleIntoOrganization(username, gCubeGroupName, correspondentRoleToCheck);
			if(res && !correspondentRoleToCheck.equals(RolesCkanGroupOrOrg.MEMBER)){
				// get the orgs of the user and retrieve its title and name
				CkanOrganization organization = catalogue.getOrganizationByName(gCubeGroupName.toLowerCase());
				orgs.add(new OrganizationBean(organization.getTitle(), organization.getName()));
			}
		}else
			logger.warn("It seems there is no ckan instance into scope " + groupManager.getInfrastructureScope(groupId));
	}

	/**
	 * Retrieve the ckan roles among a list of liferay roles
	 * @param roles
	 * @return
	 */
	private static RolesCkanGroupOrOrg getLiferayHighestRoleInOrg(
			List<GCubeRole> roles) {
		// NOTE: it is supposed that there is just one role for this person correspondent to the one in the catalog
		for (GCubeRole gCubeRole : roles) {
			if(gCubeRole.getRoleName().equalsIgnoreCase(GatewayRolesNames.CATALOGUE_ADMIN.getRoleName())){
				return RolesCkanGroupOrOrg.ADMIN;
			}
			if(gCubeRole.getRoleName().equalsIgnoreCase(GatewayRolesNames.CATALOGUE_EDITOR.getRoleName())){
				return RolesCkanGroupOrOrg.EDITOR;
			}
		}
		return RolesCkanGroupOrOrg.MEMBER;
	}

	/**
	 * Given a ckan organization name retrieve the infrastructure scope
	 * @param organizationName (prevre, devvre, ...)
	 * @return the scope of the infrastructure
	 */
	public static String retrieveScopeFromOrganizationName(String organizationName) throws Exception {

		logger.debug("Organization name is " + organizationName);

		GroupManager gm = new LiferayGroupManager();
		List<GCubeGroup> groups = gm.listGroups();
		for (GCubeGroup gCubeGroup : groups) {
			if(gCubeGroup.getGroupName().equalsIgnoreCase(organizationName))
				return gm.getInfrastructureScope(gCubeGroup.getGroupId());
		}

		return null;
	}

	/**
	 * Given the scope in the infrastructure the method retrieves the name of the ckan organization
	 * @return the ckan organization name for this scope
	 */
	public static String getOrganizationNameFromScope(String scope) throws Exception {

		if(scope != null){

			GroupManager gm = new LiferayGroupManager();
			return	gm.getGroup(gm.getGroupIdFromInfrastructureScope(scope)).getGroupName().toLowerCase();
		}

		return null;
	}

	/**
	 * Retrieve the list of metadata beans
	 * @return
	 */
	public static List<MetaDataProfileBean> getMetadataProfilesList(String scope, HttpServletRequest request) {

		List<MetaDataProfileBean> beans = new ArrayList<MetaDataProfileBean>();
		String username = getCurrentUser(request).getUsername();
		logger.debug("User in session is " + username);

		// check the scope we need to discover
		String scopeInWhichDiscover = (scope != null && !scope.isEmpty()) ? scope : getCurrentContext(request, false);

		logger.debug("Discovering into scope " + scopeInWhichDiscover);

		// scope in which we need to discover
		String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_PROFILES_KEY, scopeInWhichDiscover);

		HttpSession httpSession = request.getSession();

		if(httpSession.getAttribute(keyPerScope) != null){
			beans = (List<MetaDataProfileBean>)httpSession.getAttribute(keyPerScope);
			logger.info("List of profiles was into session");
		}
		else{

			String oldScope = ScopeProvider.instance.get();

			try {

				// set the scope
				if(oldScope != scopeInWhichDiscover)
					ScopeProvider.instance.set(scopeInWhichDiscover);

				DataCalogueMetadataFormatReader reader = new DataCalogueMetadataFormatReader();

				for (MetadataType mt : reader.getListOfMetadataTypes()) {
					MetadataFormat metadata = reader.getMetadataFormatForMetadataType(mt);

					// we need to wrap the list of metadata
					List<MetadataFieldWrapper> wrapperList = new ArrayList<MetadataFieldWrapper>();
					List<MetadataField> toWrap = metadata.getMetadataFields();
					for(MetadataField metadataField: toWrap){

						MetadataFieldWrapper wrapperObj = new MetadataFieldWrapper();
						wrapperObj.setDefaultValue(metadataField.getDefaultValue());
						wrapperObj.setFieldName(metadataField.getFieldName());
						wrapperObj.setType(DataType.valueOf(metadataField.getDataType().toString()));
						wrapperObj.setMandatory(metadataField.getMandatory());
						wrapperObj.setNote(metadataField.getNote());

						MetadataValidator validator = metadataField.getValidator();
						if(validator != null)
							wrapperObj.setValidator(validator.getRegularExpression());

						MetadataVocabulary vocabulary = metadataField.getVocabulary();

						if(vocabulary != null){
							wrapperObj.setVocabulary(vocabulary.getVocabularyFields());	
							wrapperObj.setMultiSelection(vocabulary.isMultiSelection());
						}

						// add to the list
						wrapperList.add(wrapperObj);

					}

					// wrap the mt as well
					MetaDataTypeWrapper typeWrapper = new MetaDataTypeWrapper();
					typeWrapper.setDescription(mt.getDescription());
					typeWrapper.setId(mt.getId());
					typeWrapper.setName(mt.getName());
					MetaDataProfileBean bean = new MetaDataProfileBean(typeWrapper, wrapperList);
					beans.add(bean);
				}

				logger.debug("List of beans is " + beans);
				httpSession.setAttribute(keyPerScope, beans);
				logger.debug("List of profiles has been saved into session");

			} catch (Exception e) {
				logger.error("Error while retrieving metadata beans ", e);
			}finally{
				if(oldScope != scopeInWhichDiscover)
					ScopeProvider.instance.set(oldScope);
			}
		}

		return beans;
	}

	/**
	 * First check to retrieve the token, else create it
	 * @param username
	 * @param context
	 * @return the user token for the context
	 */
	public static String tryGetElseCreateToken(String username, String context) {
		String token = null;
		try{
			try{
				logger.debug("Checking if token for user " + username + " in context " + context + " already exists...");
				token = authorizationService().resolveTokenByUserAndContext(username, context);
				logger.debug("It exists!");
			}catch(ObjectNotFound e){
				logger.info("Creating token for user " + username + " and context " + context);
				token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
				logger.debug("received token: "+ token.substring(0, 5) + "***********************");
			}
		}catch(Exception e){
			logger.error("Failed both token retrieval and creation", e);
		}
		return token;
	}

	/**
	 * Get the scope in which ckan information needs to be discovered from the url
	 * @param httpServletRequest
	 * @return
	 */
	public static String getScopeFromClientUrl(HttpServletRequest httpServletRequest){

		if(httpServletRequest == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		String scopeToReturn = null;
		try{
			String clientUrl = getCurrentClientUrl(httpServletRequest).split("\\?")[0];
			logger.debug("Client url is " + clientUrl);

			// check if this information is in session, otherwise set it and return
			HttpSession session = httpServletRequest.getSession();

			if((scopeToReturn = (String) session.getAttribute(clientUrl)) != null){
				logger.debug("Scope to return is " + scopeToReturn);
			}else{
				// ask to the ckan library and set it
				scopeToReturn = ApplicationProfileScopePerUrlReader.getScopePerUrl(clientUrl);
				logger.debug("Scope to return is " + scopeToReturn);
				session.setAttribute(clientUrl, scopeToReturn);
			}
		}catch(Exception e){
			scopeToReturn = getCurrentContext(httpServletRequest, false);
			logger.warn("Failed to determine the scope from the client url, returning the current one: " + scopeToReturn);
		}
		return scopeToReturn;
	}

	/**
	 * Needed to get the url of the client
	 * @param httpServletRequest the httpServletRequest object
	 * @return the instance of the user 
	 * @see the url at client side
	 */
	public static String getCurrentClientUrl(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getHeader(GCUBE_REQUEST_URL);
	}

	/**
	 * Retrieve the current user by using the portal manager
	 * @return a GcubeUser object
	 */
	public static GCubeUser getCurrentUser(HttpServletRequest request){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser user = pContext.getCurrentUser(request);
		logger.debug("Returning user " + user);
		return user;
	}

	/**
	 * Retrieve the current scope by using the portal manager
	 * @param b 
	 * @return a GcubeUser object
	 */
	public static String getCurrentContext(HttpServletRequest request, boolean setInThread){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		String context = pContext.getCurrentScope(request);
		logger.debug("Returning context " + context);

		if(context != null && setInThread)
			ScopeProvider.instance.set(context);

		return context;
	}
	
	/**
	 * Retrieve the current token by using the portal manager
	 * @param b 
	 * @return a GcubeUser object
	 */
	public static String getCurrentToken(HttpServletRequest request, boolean setInThread){

		if(request == null)
			throw new IllegalArgumentException("HttpServletRequest is null!");

		PortalContext pContext = PortalContext.getConfiguration();
		String token = pContext.getCurrentUserToken(getCurrentContext(request, false), getCurrentUser(request).getUsername());
		logger.debug("Returning token " + token);

		if(token != null && setInThread)
			SecurityTokenProvider.instance.set(token);

		return token;
	}

	/**
	 * Retrieve the group given the scope
	 * @param scope
	 * @return
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	public static GCubeGroup getGroupFromScope(String scope) throws UserManagementSystemException, GroupRetrievalFault{

		if(scope == null || scope.isEmpty())
			throw new IllegalArgumentException("Scope is missing here!!");

		GroupManager gm = new LiferayGroupManager();
		long groupId = gm.getGroupIdFromInfrastructureScope(scope);
		return gm.getGroup(groupId);

	}

}