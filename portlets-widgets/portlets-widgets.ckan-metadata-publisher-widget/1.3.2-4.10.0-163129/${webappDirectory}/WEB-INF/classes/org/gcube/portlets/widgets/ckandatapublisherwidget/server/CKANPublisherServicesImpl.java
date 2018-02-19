package org.gcube.portlets.widgets.ckandatapublisherwidget.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.SessionCatalogueAttributes;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.UtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherService;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.threads.AssociationToGroupAndNotifyThread;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.threads.WritePostCatalogueManagerThread;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.CatalogueRoleManager;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.DiscoverTagsList;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.GenericUtils;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.MetadataDiscovery;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.WorkspaceUtils;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.licenses.LicenseBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.MetaDataProfileBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanLicense;

/**
 * Server side of the data publisher.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CKANPublisherServicesImpl extends RemoteServiceServlet implements CKanPublisherService{

	private static final long serialVersionUID = 7252248774050361697L;

	// Logger
	//private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CKANPublisherServicesImpl.class);
	private static final Log logger = LogFactoryUtil.getLog(CKANPublisherServicesImpl.class);
	private static final String ITEM_URL_FIELD = "Item URL";
	private static final String SYS_TYPE = "system:type";
	private static final String TAGS_VOCABULARY_KEY = "TAGS_VOCABULARY";

	// map <orgName, scope>
	private ConcurrentHashMap<String, String> mapOrganizationScope = new ConcurrentHashMap<String, String>();

	/**
	 * Retrieve an instance of the library for the scope
	 * @param scope if it is null it is evaluated from the session
	 * @return
	 */
	public DataCatalogue getCatalogue(String scope){

		DataCatalogue instance = null;
		String scopeInWhichDiscover = null;
		try{
			scopeInWhichDiscover = (scope != null && !scope.isEmpty()) ? scope : GenericUtils.getCurrentContext(getThreadLocalRequest(), false);
			logger.debug("Discovering ckan instance into scope " + scopeInWhichDiscover);
			instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);
		}catch(Exception e){
			logger.warn("Unable to retrieve ckan utils in scope " + scopeInWhichDiscover + ". Error is " + e.getLocalizedMessage());
		}
		return instance;
	}

	/**
	 * Retrieve the list of organizations in which the user can publish (roles ADMIN/EDITOR)
	 * @param username
	 * @return the list of organizations
	 * @throws GroupRetrievalFault 
	 * @throws UserManagementSystemException 
	 */
	private List<OrganizationBean> getUserOrganizationsListAdmin(String username, String scope) throws UserManagementSystemException, GroupRetrievalFault {

		logger.debug("Request for user " + username + " organizations list");
		List<OrganizationBean> orgsName = new ArrayList<OrganizationBean>();

		HttpSession httpSession = getThreadLocalRequest().getSession();
		String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGANIZATIONS_PUBLISH_KEY, scope);

		if(httpSession.getAttribute(keyPerScope) != null){
			orgsName = (List<OrganizationBean>)httpSession.getAttribute(keyPerScope);
			logger.info("List of organizations was into session " + orgsName);
		}
		else{
			CatalogueRoleManager.getHighestRole(scope, username, GenericUtils.getGroupFromScope(scope).getGroupName(), this, orgsName);
			httpSession.setAttribute(keyPerScope, orgsName);
			logger.info("Organizations name for user " + username + " has been saved into session " + orgsName);
		}

		return orgsName;
	}

	/**
	 * Online or in development mode?
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			logger.trace("Development Mode ON");
			return false;
		}			
	}

	/**
	 * Find a license id given the license text.
	 * @param chosenLicense
	 * @return
	 */
	private String findLicenseIdByLicense(String chosenLicense) {	

		// get scope from client url
		String scope = GenericUtils.getScopeFromClientUrl(getThreadLocalRequest());
		return getCatalogue(scope).findLicenseIdByLicenseTitle(chosenLicense);

	}

	@Override
	public List<LicenseBean> getLicenses() {

		// get http session
		HttpSession httpSession = getThreadLocalRequest().getSession();
		String scope = GenericUtils.getScopeFromClientUrl(getThreadLocalRequest());
		logger.info("Request for CKAN licenses for scope " + scope);
		String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_LICENSES_KEY, scope);

		List<LicenseBean> licensesBean = null;
		if(httpSession.getAttribute(keyPerScope) != null){
			licensesBean = (List<LicenseBean>)httpSession.getAttribute(keyPerScope);
			logger.info("List of licenses was into session");
		}
		else{
			List<CkanLicense> licenses = getCatalogue(scope).getLicenses();
			licensesBean = new ArrayList<LicenseBean>();
			for (CkanLicense license : licenses) {
				licensesBean.add(new LicenseBean(license.getTitle(), license.getUrl()));
			}

			// sort the list
			Collections.sort(licensesBean, new Comparator<LicenseBean>(){
				public int compare(LicenseBean l1, LicenseBean l2){
					return l1.getTitle().compareTo(l2.getTitle());
				}});

			httpSession.setAttribute(keyPerScope, licensesBean);
			logger.info("List of licenses has been saved into session");
		}

		return licensesBean;
	}

	@Override
	public DatasetBean getDatasetBean(String folderId) throws Exception{

		DatasetBean bean = null;
		String userName = GenericUtils.getCurrentUser(getThreadLocalRequest()).getUsername();

		logger.info("DatasetBean request for " + folderId + " and " + userName);

		if(isWithinPortal()){
			try{

				String scope = GenericUtils.getScopeFromClientUrl(getThreadLocalRequest());

				logger.debug("Scope recovered from session is " + scope);

				logger.debug("Request dataset metadata bean for folder with id " + folderId
						+ " whose owner is " + userName);

				UserManager liferUserManager = new LiferayUserManager();
				GCubeUser userOwner = liferUserManager.getUserByUsername(userName);

				// build bean
				logger.debug("Building bean");
				bean = new DatasetBean();

				bean.setId(folderId);
				bean.setOwnerIdentifier(userName);
				bean.setVersion(1);
				bean.setAuthorName(userOwner.getFirstName());
				bean.setAuthorSurname(userOwner.getLastName());
				bean.setAuthorEmail(userOwner.getEmail());
				bean.setMaintainer(userOwner.getFullname());
				bean.setMaintainerEmail(userOwner.getEmail());
				bean.setOrganizationList(getUserOrganizationsListAdmin(userName, scope));
				bean.setTagsVocabulary(discoverTagsVocabulary(scope));

				// if the request comes from the workspace
				if(folderId != null && !folderId.isEmpty()){
					WorkspaceUtils.handleWorkspaceResources(folderId, userName, bean);
				}

			}catch(Exception e){
				logger.error("Error while retrieving bean information", e);
				throw new Exception("Error while retrieving basic information " + e.getMessage());
			}
		}else{

			logger.info("DEV MODE DETECTED");
			GenericUtils.getCurrentToken(getThreadLocalRequest(), true);

			try{
				bean = new DatasetBean();
				bean.setId(folderId);
				bean.setDescription("This is a fantastic description");
				bean.setVersion(1);
				String onlyAlphanumeric = "test-creation-blablabla".replaceAll("[^A-Za-z0-9]", "");
				bean.setTitle(onlyAlphanumeric + Calendar.getInstance().getTimeInMillis());
				bean.setAuthorName("Costantino");
				bean.setAuthorSurname("Perciante");
				bean.setAuthorEmail("costantino.perciante@isti.cnr.it");
				bean.setMaintainer("Costantino Perciante");
				bean.setMaintainerEmail("costantino.perciante@isti.cnr.it");
				bean.setOrganizationList(Arrays.asList(new OrganizationBean("NextNext", "nextnext", true)));
				bean.setOwnerIdentifier(userName);

				if(folderId != null && !folderId.isEmpty()){
					WorkspaceUtils.handleWorkspaceResources(folderId, userName, bean);
				}
			}catch(Exception e){
				logger.error("Error while building bean into dev mode", e);
				throw new Exception("Error while retrieving basic information " + e.getMessage());
			}
		}

		logger.debug("Returning bean " + bean);
		return bean;
	}

	/**
	 * Discover from the IS the vocabulary of tags for this scope, if present
	 * @return a list of tags vocabulary
	 * @throws Exception 
	 */
	private List<String> discoverTagsVocabulary(String context){

		logger.debug("Looking for vocabulary of tags in this context " + context);
		String keyPerVocabulary = UtilMethods.concatenateSessionKeyScope(TAGS_VOCABULARY_KEY, context); 

		List<String> vocabulary = (List<String>) getThreadLocalRequest().getSession().getAttribute(keyPerVocabulary);
		if(vocabulary == null){
			vocabulary = DiscoverTagsList.discoverTagsList(context);
			if(vocabulary != null)
				getThreadLocalRequest().getSession().setAttribute(keyPerVocabulary, vocabulary);
		}

		logger.debug("Vocabulary for tags is " + vocabulary);

		return vocabulary;
	}

	@Override
	public List<String> getTagsForOrganization(String orgName) throws Exception{
		return discoverTagsVocabulary(getScopeFromOrgName(orgName));
	}

	@Override
	public DatasetBean createCKanDataset(DatasetBean toCreate) throws Exception{

		try{
			logger.info("Request for creating a dataset with these information " + toCreate);
			String userName = GenericUtils.getCurrentUser(getThreadLocalRequest()).getUsername();
			String title = toCreate.getTitle();
			String organizationNameOrId = toCreate.getSelectedOrganization();
			String author = toCreate.getAuthorFullName();
			String authorMail = toCreate.getAuthorEmail();
			String maintainer = toCreate.getMaintainer();
			String maintainerMail = toCreate.getMaintainerEmail();
			long version = toCreate.getVersion();
			String description = toCreate.getDescription();
			String chosenLicense = toCreate.getLicense();
			String licenseId = findLicenseIdByLicense(chosenLicense);
			List<String> listOfTags = toCreate.getTags();
			Map<String, List<String>> customFields = toCreate.getCustomFields();

			// add Type for custom fields
			if(toCreate.getChosenType() != null)
				customFields.put(SYS_TYPE, Arrays.asList(toCreate.getChosenType()));

			boolean setPublic = toCreate.getVisibility();

			// get the list of resources and convert to ResourceBean
			List<ResourceBean> resources = null;
			ResourceElementBean resourcesToAdd = toCreate.getResourceRoot();

			// we need to copy such resource in the .catalogue area of the user's ws
			if(resourcesToAdd != null){
				resources = WorkspaceUtils.copyResourcesToUserCatalogueArea(toCreate.getId(), userName, toCreate);
			}

			logger.debug("The user wants to publish in organization with name " + organizationNameOrId);
			String scope = getScopeFromOrgName(organizationNameOrId);
			DataCatalogue utils = getCatalogue(scope);
			String userApiKey = utils.getApiKeyFromUsername(userName);

			String datasetId = utils.createCKanDatasetMultipleCustomFields
					(userApiKey, title, null, organizationNameOrId, author, authorMail, maintainer, 
							maintainerMail, version, description, licenseId, listOfTags, customFields, resources, setPublic);

			if(datasetId != null){

				logger.info("Dataset created!");
				toCreate.setId(datasetId);

				// retrieve the url
				String datasetUrl =  utils.getUnencryptedUrlFromDatasetIdOrName(datasetId);
				toCreate.setSource(datasetUrl);

				// add also this information as custom field
				Map<String, List<String>> addField = new HashMap<String, List<String>>();
				addField.put(ITEM_URL_FIELD, Arrays.asList(datasetUrl));
				utils.patchProductCustomFields(datasetId, userApiKey, addField, false);

				// start a thread that will associate this dataset with the group
				if(/*toCreate.getChosenType() != null ||*/ toCreate.getGroups() != null){

					AssociationToGroupAndNotifyThread threadAssociationToGroup = 
							new AssociationToGroupAndNotifyThread(
									toCreate.getGroups(),
									toCreate.getGroupsForceCreation(),
									null, //toCreate.getChosenType(), TODO
									datasetUrl,
									datasetId,
									toCreate.getTitle(),
									GenericUtils.getCurrentUser(getThreadLocalRequest()).getFullname(),
									userName, 
									utils, 
									organizationNameOrId,
									getThreadLocalRequest()
									);
					threadAssociationToGroup.start();

				}

				// launch notification thread
				WritePostCatalogueManagerThread threadWritePost = 
						new WritePostCatalogueManagerThread(
								userName,
								scope, 
								toCreate.getTitle(), 
								datasetUrl, 
								false, // send notification to other people 
								toCreate.getTags(), 
								GenericUtils.getCurrentUser(getThreadLocalRequest()).getFullname(),
								GenericUtils.getCurrentClientUrl(getThreadLocalRequest())
								);
				threadWritePost.start();

				return toCreate;
			}else{
				logger.error("Failed to create the dataset");
			}

		}catch(Exception e){
			logger.error("Error while creating item ", e);
			throw new Exception("An error occurred while creating the item " + e.getMessage());
		}

		return null;

	}

	@Override
	public ResourceElementBean addResourceToDataset(ResourceElementBean resource, String datasetId) throws Exception{
		String username = GenericUtils.getCurrentUser(getThreadLocalRequest()).getUsername();

		logger.debug("Incoming request for creating new resource for dataset with id " + datasetId + " and organization name of the dataset is " + resource.getOrganizationNameDatasetParent());
		logger.debug("Owner is " + username + " and resource is " + resource);

		if(!isWithinPortal()){

			logger.warn("Running outside the portal");
			return resource;

		}else{


			ResourceBean resourceBean = new ResourceBean(
					resource.getUrl(), 
					resource.getName(), 
					resource.getDescription(), 
					null, 
					username, 
					datasetId,
					null);

			// get the scope in which we should discover the ckan instance given the organization name in which the dataset was created
			String scope = getScopeFromOrgName(resource.getOrganizationNameDatasetParent());
			DataCatalogue catalogue = getCatalogue(scope);
			String resourceId = catalogue.addResourceToDataset(resourceBean, catalogue.getApiKeyFromUsername(username));

			if(resourceId != null){
				logger.debug("Resource  " + resource.getName() + " is now available");
				// set its id and turn it to the client
				resource.setOriginalIdInWorkspace(resourceId);
				return resource;
			}

			logger.debug("No resource created");
			return null;
		}
	}

	@Override
	public boolean deleteResourceFromDataset(ResourceElementBean resource) throws Exception{

		logger.debug("Request for deleting resource " + resource);
		boolean deleted = false;

		if(!isWithinPortal()){
			logger.warn("Running outside the portal");
			return deleted;
		}else{

			String username = GenericUtils.getCurrentUser(getThreadLocalRequest()).getUsername();
			try{
				// get the scope in which we should discover the ckan instance given the organization name in which the dataset was created
				String scope = getScopeFromOrgName(resource.getOrganizationNameDatasetParent());
				DataCatalogue catalogue = getCatalogue(scope);
				deleted = catalogue.
						deleteResourceFromDataset(resource.getOriginalIdInWorkspace(), catalogue.getApiKeyFromUsername(username));
				if(deleted){
					logger.info("Resource described by " + resource + " deleted");
				}else
					logger.error("Resource described by " + resource + " NOT deleted");
			}catch(Exception e){
				logger.error("Error while trying to delete resource described by " + resource, e);
				throw new Exception("Error while trying to delete resource." + e.getMessage());
			}
			return deleted;
		}
	}

	@Override
	public List<MetaDataProfileBean> getProfiles(String orgName) throws Exception{

		logger.debug("Requested profiles for products into orgName " + orgName);
		List<MetaDataProfileBean> toReturn = new ArrayList<MetaDataProfileBean>();
		try{
			String evaluatedScope = getScopeFromOrgName(orgName);
			logger.debug("Evaluated scope is  " + evaluatedScope);
			toReturn = MetadataDiscovery.getMetadataProfilesList(evaluatedScope, getThreadLocalRequest());
		}catch(Exception e){
			logger.error("Failed to retrieve profiles for scope coming from organization name " + orgName, e);
			throw e;
		}

		return toReturn;
	}

	@Override
	public boolean datasetIdAlreadyExists(String title, String orgName) throws Exception{
		if(title == null || title.isEmpty())
			return true; // it's an error somehow
		try{
			String scopeFromOrgName = getScopeFromOrgName(orgName);
			String idFromTitle = UtilMethods.fromProductTitleToName(title);
			logger.debug("Evaluating if dataset with id " + title + " in context " + scopeFromOrgName + " already exists");
			return getCatalogue(scopeFromOrgName).existProductWithNameOrId(idFromTitle);
		}catch(Exception e){
			logger.error("Unable to check if such a dataset id already exists", e);
			throw new Exception("Unable to check if such a dataset id already exists " + e.getMessage());
		}
	}

	/**
	 * The method tries to retrieve the scope related to the organization using the map first,
	 * if no match is found, it retrieves such information by using liferay
	 */
	private String getScopeFromOrgName(String orgName){

		logger.debug("Request for scope related to orgName " + orgName + "[ map that will be used is " + mapOrganizationScope.toString() + " ]");
		if(orgName == null || orgName.isEmpty())
			throw new IllegalArgumentException("orgName cannot be empty or null!");

		String toReturn = null;

		if(isWithinPortal()){
			if(mapOrganizationScope.containsKey(orgName))
				toReturn = mapOrganizationScope.get(orgName);
			else{
				try{
					String evaluatedScope = GenericUtils.retrieveScopeFromOrganizationName(orgName);
					mapOrganizationScope.put(orgName, evaluatedScope);
					toReturn = evaluatedScope;
				}catch(Exception e){
					logger.error("Failed to retrieve scope from OrgName for organization " + orgName, e);
				}
			}
		}else{
			toReturn = "/gcube/devNext/NextNext";
			mapOrganizationScope.put(orgName, toReturn);
		}
		logger.debug("Returning scope " + toReturn);
		return toReturn;
	}

	@Override
	public List<OrganizationBean> getUserGroups(String orgName) {

		List<OrganizationBean> toReturn = new ArrayList<OrganizationBean>();

		if(isWithinPortal()){
			String username = GenericUtils.getCurrentUser(getThreadLocalRequest()).getUsername();

			logger.debug("Request for user " + username + " groups. Organization name is "  + orgName);

			// get http session
			HttpSession httpSession = getThreadLocalRequest().getSession();
			String scope =  orgName != null ? getScopeFromOrgName(orgName) : GenericUtils.getScopeFromClientUrl(getThreadLocalRequest());

			// check if they are in session
			String keyPerScopeGroups = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_GROUPS_MEMBER, scope);

			if(httpSession.getAttribute(keyPerScopeGroups) != null){
				toReturn = (List<OrganizationBean>)httpSession.getAttribute(keyPerScopeGroups);
				logger.info("Found user's groups in session " + toReturn);
			}else{

				DataCatalogue catalogue = getCatalogue(scope);
				String apiKey = catalogue.getApiKeyFromUsername(username);
				Map<String, Map<CkanGroup, RolesCkanGroupOrOrg>> mapRoleGroup = catalogue.getUserRoleByGroup(username, apiKey);
				Set<Entry<String, Map<CkanGroup, RolesCkanGroupOrOrg>>> set = mapRoleGroup.entrySet();
				for (Entry<String, Map<CkanGroup, RolesCkanGroupOrOrg>> entry : set) {
					Set<Entry<CkanGroup, RolesCkanGroupOrOrg>> subSet = entry.getValue().entrySet();
					for (Entry<CkanGroup, RolesCkanGroupOrOrg> subEntry : subSet) {
						toReturn.add(new OrganizationBean(subEntry.getKey().getTitle(), subEntry.getKey().getName(), false));	
					}
				}
				httpSession.setAttribute(keyPerScopeGroups, toReturn);
			}
		}else{
			logger.warn("Dev mode detected");
			toReturn = Arrays.asList();
		}
		return toReturn;
	}


	@Override
	public boolean isPublisherUser(boolean isWorkspaceRequest) throws Exception{

		String username = GenericUtils.getCurrentUser(getThreadLocalRequest()).getUsername();
		logger.info("Checking if the user " + username + " can publish or not on the catalogue");

		if(!isWithinPortal()){
			logger.warn("OUT FROM PORTAL DETECTED RETURNING TRUE");
			return true;
		}

		try{

			HttpSession httpSession = this.getThreadLocalRequest().getSession();

			// retrieve scope per current portlet url
			String scopePerCurrentUrl = GenericUtils.getScopeFromClientUrl(getThreadLocalRequest());

			// get key per scope
			String keyPerScopeRole = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_HIGHEST_ROLE, scopePerCurrentUrl);
			String keyPerScopeOrganizations = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGANIZATIONS_PUBLISH_KEY, scopePerCurrentUrl);
			String keyPerScopeGroups = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_GROUPS_MEMBER, scopePerCurrentUrl);

			// check if this information was already into session(true means the user has at least in one org
			// the role editor), false that he is just a member so he cannot publish
			RolesCkanGroupOrOrg role = (RolesCkanGroupOrOrg) httpSession.getAttribute(keyPerScopeRole);

			// if the attribute was already set..
			if(role != null)
				return !role.equals(RolesCkanGroupOrOrg.MEMBER);
			else{

				try{

					GroupManager gm = new LiferayGroupManager();
					String groupName = gm.getGroup(gm.getGroupIdFromInfrastructureScope(scopePerCurrentUrl)).getGroupName();

					// we build up also a list that keeps track of the scopes (orgs) in which the user has role ADMIN/EDITOR
					List<OrganizationBean> orgsInWhichAtLeastEditorRole = new ArrayList<OrganizationBean>();
					role = CatalogueRoleManager.getHighestRole(scopePerCurrentUrl, username, groupName, this, orgsInWhichAtLeastEditorRole);

					// if he is an admin/editor preload:
					// 1) organizations in which he can publish (the widget will find these info in session)
					if(!role.equals(RolesCkanGroupOrOrg.MEMBER)){
						httpSession.setAttribute(keyPerScopeOrganizations, orgsInWhichAtLeastEditorRole);
						String orgName = scopePerCurrentUrl.split("/")[scopePerCurrentUrl.split("/").length - 1];
						httpSession.setAttribute(keyPerScopeGroups, getUserGroups(orgName));
					}
				}catch(Exception e){
					logger.error("Unable to retrieve the role information for this user. Returning FALSE", e);
					return false;
				}
			}

			// set role in session for this scope
			httpSession.setAttribute(keyPerScopeRole, role);

			logger.info("Does the user have the right to publish on the catalogue? " + role);
			return !role.equals(RolesCkanGroupOrOrg.MEMBER);

		}catch(Exception e){
			logger.error("Failed to check the user's role", e);
			throw new Exception("Failed to check if you are an Administrator or Editor " + e.getMessage());
		}
	}

	@Override
	public boolean isGeoJSONValid(String geoJson) throws Exception {
		try{
			new ObjectMapper().readValue(geoJson, GeoJsonObject.class);
			return true;
		}catch(Exception e){
			throw new Exception("GeoJSON field with value '" + geoJson + "' seems not valid!");
		}
	}

}