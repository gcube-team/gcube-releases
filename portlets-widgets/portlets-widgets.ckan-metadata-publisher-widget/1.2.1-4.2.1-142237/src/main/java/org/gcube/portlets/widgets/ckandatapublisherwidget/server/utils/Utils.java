package org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;
import eu.trentorise.opendata.jackan.model.CkanOrganization;

/**
 * Util class with static methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Utils {

	// Logger
	//private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Utils.class);
	private static final Log logger = LogFactoryUtil.getLog(Utils.class);
	private static final String APPLICATION_ID_CATALOGUE_MANAGER = "org.gcube.datacatalogue.ProductCatalogue"; 
	private static final String NOTIFICATION_MESSAGE = "Dear members,<br>The product 'PRODUCT_TITLE' has been just published by USER_FULLNAME.<br>You can find it here: PRODUCT_URL <br>";
	private static final String SOCIAL_SERVICE_APPLICATION_TOKEN = "2/tokens/generate-application-token";
	private static final String SOCIAL_SERVICE_WRITE_APPLICATION_POST = "2/posts/write-post-app";
	private static final String MEDIATYPE_JSON = "application/json";
	public static final String GCUBE_REQUEST_URL = "gcube-request-url";

	/** Gets the gcube item properties.
	 *
	 * @param item the item
	 * @return the gcube item properties
	 */
	public static Map<String, String> getGcubeItemProperties(WorkspaceItem item) {

		if(item instanceof GCubeItem){
			GCubeItem gItem = (GCubeItem) item;
			try {
				if(gItem.getProperties()!=null){
					Map<String, String> map = gItem.getProperties().getProperties();
					HashMap<String, String> properties = new HashMap<String, String>(map.size()); //TO PREVENT GWT SERIALIZATION ERROR
					for (String key : map.keySet())
						properties.put(key, map.get(key));

					return properties;
				}
			} catch (InternalErrorException e) {
				logger.error("Error in server getItemProperties: ", e);
			}
		}
		return null;
	}

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
	 * Send notification to vre members about the created product by writing a post.
	 * @param productName the title of the product
	 * @param productUrl the url of the product
	 * @param hashtags a list of product's hashtags
	 */
	public static void writeProductPost(String productName, String productUrl, String userFullname, List<String> hashtags, boolean enablePostNotification){

		// discover service endpoint for the social networking library
		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();

		logger.info("Current scope for writeProductPost is " + currentScope + " and token is " + tokenUser.substring(0, 10) + "***************");
		String basePath = new ServiceEndPointReaderSocial(currentScope).getBasePath();

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");

		}else{

			try(CloseableHttpClient client = HttpClientBuilder.create().build();){

				// ask token application
				HttpPost postRequest = new HttpPost(basePath + SOCIAL_SERVICE_APPLICATION_TOKEN + "?gcube-token=" + tokenUser);
				StringEntity input = new StringEntity("{\"app_id\":\"" + APPLICATION_ID_CATALOGUE_MANAGER + "\"}");
				input.setContentType(MEDIATYPE_JSON);
				postRequest.setEntity(input);
				HttpResponse response = client.execute(postRequest);

				logger.debug("Url is " + basePath + SOCIAL_SERVICE_APPLICATION_TOKEN + "?gcube-token=" + tokenUser);

				if (response.getStatusLine().getStatusCode() != 201) {
					throw new RuntimeException("Failed to retrieve application token : HTTP error code : "
							+ response.getStatusLine().getStatusCode());
				}else{

					Map<String, Object> mapResponseGeneratedToken = getResponseEntityAsJSON(response);
					boolean successGeneratedToken = (boolean)mapResponseGeneratedToken.get("success");
					if(!successGeneratedToken){

						throw new RuntimeException("Failed to generate the token for the application!"
								+ " Error message is " + mapResponseGeneratedToken.get("message"));

					}else{

						String applicationToken = (String)mapResponseGeneratedToken.get("result");

						// replace
						String message  = NOTIFICATION_MESSAGE.replace("PRODUCT_TITLE", productName).replace("PRODUCT_URL", productUrl).replace("USER_FULLNAME", userFullname);

						if(hashtags != null && !hashtags.isEmpty())
							for (String hashtag : hashtags) {
								String modifiedHashtag = hashtag.replaceAll(" ", "_").replace("_+", "_");
								if(modifiedHashtag.endsWith("_"))
									modifiedHashtag = modifiedHashtag.substring(0, modifiedHashtag.length() - 1);
								message += " #" + modifiedHashtag; // ckan accepts tag with empty spaces, we don't
							}

						logger.info("The post that is going to be written is -> " + message);
						postRequest = new HttpPost(basePath + SOCIAL_SERVICE_WRITE_APPLICATION_POST + "?gcube-token=" + applicationToken);
						input = new StringEntity("{\"text\":\"" + message + "\", \"enable_notification\" : "+ enablePostNotification+ "}");
						input.setContentType(MEDIATYPE_JSON);
						postRequest.setEntity(input);
						response = client.execute(postRequest);

						Map<String, Object> mapResponseWritePost = getResponseEntityAsJSON(response);

						if (response.getStatusLine().getStatusCode() != 201)
							throw new RuntimeException("Failed to write application post : HTTP error code : "
									+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));
					}

				}

			}catch(Exception e){
				logger.error("Failed to create a post", e);
			}
		}
	}

	/**
	 * Convert the json response to a map
	 * @param response
	 * @return
	 */
	public static Map<String, Object> getResponseEntityAsJSON(HttpResponse response){

		Map<String, Object> toReturn = null;
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			try {
				toReturn = new HashMap<String, Object>();
				String jsonString = EntityUtils.toString(response.getEntity());
				logger.debug("Response as string is " + jsonString);
				ObjectMapper objectMapper = new ObjectMapper();
				toReturn = objectMapper.readValue(jsonString, HashMap.class);
				logger.debug("Map is " + toReturn);
			}catch(Exception e){
				logger.error("Failed to read json object", e);
			}
		}

		return toReturn;
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