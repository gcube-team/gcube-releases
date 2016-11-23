package org.gcube.portlets.widgets.ckandatapublisherwidget.server;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.models.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.utils.SessionCatalogueAttributes;
import org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherService;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.Utils;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.WorkspaceUtils;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetMetadataBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.LicensesBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

import eu.trentorise.opendata.jackan.model.CkanLicense;

/**
 * Server side of the data publisher.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("serial")
public class CKANPublisherServicesImpl extends RemoteServiceServlet implements CKanPublisherService{

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CKANPublisherServicesImpl.class);

	public static final String TEST_SCOPE = "/gcube/devsec/devVRE";
	public static final String TEST_USER = "test.user";
	private final static String TEST_SEC_TOKEN = "a1e19695-467f-42b8-966d-bf83dd2382ef";

	// map <orgName, scope>
	private ConcurrentHashMap<String, String> mapOrganizationScope = new ConcurrentHashMap<String, String>();

	/**
	 * Retrieve an instance of the library for the scope
	 * @param scope if it is null it is evaluated from the session
	 * @return
	 */
	public DataCatalogue getCatalogue(String scope){

		DataCatalogue instance = null;
		try{
			String scopeInWhichDiscover = (scope != null && !scope.isEmpty()) ? scope : getASLSession().getScope();
			logger.debug("Discovering ckan instance into scope " + scopeInWhichDiscover);
			instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scopeInWhichDiscover);
		}catch(Exception e){
			logger.error("Unable to retrieve ckan utils", e);
		}
		return instance;
	}

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {
			logger.warn("USER IS NULL setting testing user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(TEST_SCOPE);
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = TEST_USER;
		//		user = "costantino.perciante";
		return user;
	}

	/**
	 * Get current user's token (for a given scope)
	 * @param scope if it is not specified it will be retrieved from the asl
	 * @return String the ckan user's token
	 */
	private String getUserCKanTokenFromSession(String scope){

		String token = null;

		if(!isWithinPortal()){
			logger.warn("You are running outside the portal");
			token = TEST_SEC_TOKEN;
		}else{

			ASLSession aslSession = getASLSession();
			String username = aslSession.getUsername();

			// store info in the http session
			HttpSession httpSession = getThreadLocalRequest().getSession();

			// check the scope we need to discover
			String scopeInWhichDiscover = (scope != null && !scope.isEmpty()) ? scope : getASLSession().getScope();

			// get the key per scope
			String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_TOKEN_KEY, scopeInWhichDiscover);

			// check if session expired
			if(username.equals(TEST_USER)){

				logger.warn("Session expired, returning null token");
				token = null;

			}else{
				try{
					logger.debug("User in session is " + username);
					if(httpSession.getAttribute(keyPerScope) != null){
						token = (String)httpSession.getAttribute(keyPerScope);
						logger.debug("Found ckan token into session");
					}
					else{
						token = getCatalogue(scopeInWhichDiscover).getApiKeyFromUsername(username);
						httpSession.setAttribute(keyPerScope, token);
						logger.debug("Ckan token has been set for user " + username);
					}
					logger.debug("Found ckan token " + token.substring(0, 3) + "************************" + 
							" for user " + username + " into scope " + scopeInWhichDiscover);
				}catch(Exception e){
					logger.error("Error while retrieving the key" , e);
				}
			}
		}
		return token;
	}

	/**
	 * Retrieve the list of organizations in which the user can publish (roles ADMIN)
	 * @param username
	 * @return the list of organizations
	 */
	private List<OrganizationBean> getUserOrganizationsListAdmin(String username, String scope) {

		logger.debug("Request for user " + username + " organizations list");
		List<OrganizationBean> orgsName = new ArrayList<OrganizationBean>();

		// get http session
		HttpSession httpSession = getThreadLocalRequest().getSession();

		// get key 
		String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_ORGANIZATIONS_PUBLISH_KEY, scope);

		if(httpSession.getAttribute(keyPerScope) != null){
			orgsName = (List<OrganizationBean>)httpSession.getAttribute(keyPerScope);
			logger.info("List of organizations was into session " + orgsName);
		}
		else{
			orgsName = Utils.getUserOrganizationsListAdminEditor(scope, username, getASLSession().getGroupName(), this);
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

		String scope = (String)getThreadLocalRequest().getSession().getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);
		return getCatalogue(scope).findLicenseIdByLicenseTitle(chosenLicense);

	}

	@Override
	public LicensesBean getLicenses() {

		ASLSession session = getASLSession();
		String username = session.getUsername();

		// get http session
		HttpSession httpSession = getThreadLocalRequest().getSession();
		String scope = (String)httpSession.getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);

		logger.info("Request for CKAN licenses for scope " + scope);

		// get key per scope
		String keyPerScope = UtilMethods.concatenateSessionKeyScope(SessionCatalogueAttributes.CKAN_LICENSES_KEY, scope);

		LicensesBean licensesBean = null;
		if(httpSession.getAttribute(keyPerScope) != null){
			licensesBean = (LicensesBean)httpSession.getAttribute(keyPerScope);
			logger.debug("List of licenses was into session");
		}
		else{
			List<CkanLicense> titlesLicenses = getCatalogue(scope).getLicenses();
			List<String> titles = new ArrayList<String>();
			List<String> urls = new ArrayList<String>();
			for (CkanLicense license : titlesLicenses) {
				titles.add(license.getTitle());
				String url = (license.getUrl() != null && !license.getUrl().isEmpty()) ? license.getUrl() : "";
				urls.add(url);
			}
			licensesBean = new LicensesBean(titles, urls);
			httpSession.setAttribute(keyPerScope, licensesBean);
			logger.debug("List of licenses has been saved into session");
		}

		return licensesBean;
	}

	@Override
	public DatasetMetadataBean getDatasetBean(String folderId){

		DatasetMetadataBean bean = null;

		ASLSession aslSession = getASLSession();
		String userName = aslSession.getUsername();

		logger.info("DatasetBean request for " + folderId + " and " + userName);

		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String scope = (String)httpSession.getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);

		logger.debug("Scope recovered from session is " + scope);

		if(isWithinPortal()){
			try{

				// check if session expired
				if(userName.equals(TEST_USER)){
					logger.debug("SESSION EXPIRED");
					return null;
				}

				logger.debug("Request dataset metadata bean for folder with id " + folderId
						+ " whose owner is " + userName);
				// get usermanager (liferay)
				UserManager liferUserManager = new LiferayUserManager();
				GCubeUser userOwner = liferUserManager.getUserByUsername(userName);

				// build bean
				logger.debug("Building bean");
				bean = new DatasetMetadataBean();

				bean.setId(folderId);
				bean.setOwnerIdentifier(userName);
				bean.setVersion(1);
				bean.setAuthorName(userOwner.getFirstName());
				bean.setAuthorSurname(userOwner.getLastName());
				bean.setAuthorEmail(userOwner.getEmail());
				bean.setMaintainer(userOwner.getFullname());
				bean.setMaintainerEmail(userOwner.getEmail());
				bean.setOrganizationList(getUserOrganizationsListAdmin(userName, scope));

				// if the request comes from the workspace
				if(folderId != null && !folderId.isEmpty()){

					WorkspaceUtils.handleWorkspaceResources(folderId, userName, bean);

				}

			}catch(Exception e){
				logger.error("Error while retrieving folder information", e);
			}
		}else{

			try{

				bean = new DatasetMetadataBean();
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
				bean.setOrganizationList(getUserOrganizationsListAdmin(userName, TEST_SCOPE));
				bean.setOwnerIdentifier(userName);

				if(folderId != null && !folderId.isEmpty()){

					WorkspaceUtils.handleWorkspaceResources(folderId, userName, bean);

				}

			}catch(Exception e){
				logger.error("Error while building bean into dev mode", e);
			}
		}

		return bean;
	}

	@Override
	public DatasetMetadataBean createCKanDataset(DatasetMetadataBean toCreate) {

		logger.debug("Request for creating a dataset with these information " + toCreate);

		ASLSession aslSession = getASLSession();
		String userName = aslSession.getUsername();

		try{

			// check if session expired
			if(userName.equals(TEST_USER)){
				logger.debug("SESSION EXPIRED");
				return null;
			}

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
			Map<String, String> customFields = toCreate.getCustomFields();
			boolean setPublic = toCreate.getVisibility();

			// get the list of resources and convert to ResourceBean
			List<ResourceBean> resources = null;
			List<ResourceBeanWrapper> resourcesToAdd = toCreate.getResources();

			// we need to copy such resource in the .catalogue area of the user's ws
			if(resourcesToAdd != null && !resourcesToAdd.isEmpty()){

				resources = WorkspaceUtils.copyResourcesToUserCatalogueArea(resourcesToAdd, toCreate.getId(), userName, toCreate);

			}

			logger.debug("The user wants to publish in organization with name " + organizationNameOrId);
			String scope = getScopeFromOrgName(organizationNameOrId);
			DataCatalogue utils = getCatalogue(scope);

			String datasetId = utils.createCKanDataset(getUserCKanTokenFromSession(scope), title, organizationNameOrId,  author,
					authorMail,  maintainer,  maintainerMail,  version, description, licenseId,
					listOfTags, customFields, resources, setPublic);

			if(datasetId != null){

				logger.debug("Dataset created!");
				toCreate.setId(datasetId);

				// retrieve the url
				String datasetUrl =  utils.getPortletUrl() + "?" + URLEncoder.encode("path=" +  utils.getUrlFromDatasetIdOrName(getUserCKanTokenFromSession(scope), datasetId, true), "UTF-8");
				toCreate.setSource(datasetUrl);

				// start a thread that will associate this dataset with the group
				if(toCreate.getChosenProfile() != null){

					AssociationToGroupThread threadAssociationToGroup = 
							new AssociationToGroupThread(
									toCreate.getChosenProfile(), 
									datasetId, 
									userName, 
									utils, 
									organizationNameOrId
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
								toCreate.getAuthorFullName()
								);
				threadWritePost.start();

				return toCreate;

			}else{

				logger.error("Failed to create the dataset");

			}

		}catch(Exception e){
			logger.error("Unable to create the dataset", e);
		}

		return null;
	}

	@Override
	public ResourceBeanWrapper addResourceToDataset(ResourceBeanWrapper resource, String datasetId) {


		ASLSession session = getASLSession();
		String username = session.getUsername();

		logger.debug("Incoming request for creating new resource for dataset with id " + datasetId + " and organization name of the dataset is " + resource.getOrganizationNameDatasetParent());
		logger.debug("Owner is " + username + " and resource is " + resource);

		if(!isWithinPortal()){

			logger.warn("Running outside the portal");
			return resource;

		}else{

			if(username.equals(TEST_USER)){

				logger.warn("SESSION EXPIRED! ");
				return null;

			}else{
				try{

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
					String resourceId = getCatalogue(scope).addResourceToDataset(resourceBean, getUserCKanTokenFromSession(scope));

					if(resourceId != null){
						logger.debug("Resource  " + resource.getName() + " is now available");
						// set its id and turn it to the client
						resource.setId(resourceId);
						return resource;
					}
				}catch(Exception e){
					logger.error("Unable to create new resource", e);
				}
			}
			logger.debug("No resource created");
			return null;
		}
	}

	@Override
	public boolean deleteResourceFromDataset(ResourceBeanWrapper resource) {

		logger.debug("Request for deleting resource " + resource);
		boolean deleted = false;

		if(!isWithinPortal()){
			logger.warn("Running outside the portal");
			return deleted;
		}else{

			ASLSession session = getASLSession();
			String username = session.getUsername();

			if(username.equals(TEST_USER)){
				logger.warn("SESSION EXPIRED!");
				return deleted;
			}else{
				try{
					// get the scope in which we should discover the ckan instance given the organization name in which the dataset was created
					String scope = getScopeFromOrgName(resource.getOrganizationNameDatasetParent());
					deleted = getCatalogue(scope).
							deleteResourceFromDataset(resource.getId(), getUserCKanTokenFromSession(scope));
					if(deleted){
						logger.debug("Resource described by " + resource + " deleted");
					}else
						logger.error("Resource described by " + resource + " NOT deleted");
				}catch(Exception e){
					logger.error("Error while trying to delete resource described by " + resource, e);
				}
				return deleted;
			}
		}
	}

	@Override
	public List<MetaDataProfileBean> getProfiles(String orgName) {

		logger.debug("Requested profiles for products into orgName " + orgName);

		List<MetaDataProfileBean> toReturn = new ArrayList<MetaDataProfileBean>();
		try{
			String evaluatedScope = getScopeFromOrgName(orgName);
			logger.debug("Evaluated scope is  " + evaluatedScope);
			toReturn = Utils.getMetadataProfilesList(evaluatedScope, getThreadLocalRequest().getSession(), getASLSession());
		}catch(Exception e){
			logger.error("Failed to retrieve profiles for scope coming from organization name " + orgName, e);
		}

		return toReturn;
	}

	@Override
	public boolean datasetIdAlreadyExists(String title) {

		if(title == null || title.isEmpty())
			return true; // it's an error somehow

		try{

			String scope = (String)getThreadLocalRequest().getSession().getAttribute(SessionCatalogueAttributes.SCOPE_CLIENT_PORTLET_URL);
			String idFromTitle = org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods.fromProductTitleToName(title);
			return getCatalogue(scope).existProductWithNameOrId(idFromTitle);

		}catch(Exception e){
			logger.error("Unable to check if such a dataset id already exists", e);
		}

		return false;
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

		if(mapOrganizationScope.containsKey(orgName))
			toReturn = mapOrganizationScope.get(orgName);
		else{
			try{

				String evaluatedScope = Utils.retrieveScopeFromOrganizationName(orgName);
				mapOrganizationScope.put(orgName, evaluatedScope);
				toReturn = evaluatedScope;

			}catch(Exception e){
				logger.error("Failed to retrieve scope from OrgName for organization " + orgName, e);
			}
		}


		logger.debug("Returning scope " + toReturn);
		return toReturn;

	}
}