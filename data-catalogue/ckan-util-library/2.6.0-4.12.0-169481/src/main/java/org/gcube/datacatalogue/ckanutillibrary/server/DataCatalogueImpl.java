package org.gcube.datacatalogue.ckanutillibrary.server;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.CatalogueUtilMethods;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.url.EntityContext;
import org.gcube.datacatalogue.ckanutillibrary.shared.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.shared.CkanDatasetRelationship;
import org.gcube.datacatalogue.ckanutillibrary.shared.DatasetRelationships;
import org.gcube.datacatalogue.ckanutillibrary.shared.LandingPages;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.ckanutillibrary.shared.State;
import org.gcube.datacatalogue.ckanutillibrary.shared.Statistics;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.CkanQuery;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpStatus;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpGet;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.ContentType;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.mime.MultipartEntityBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;
import eu.trentorise.opendata.jackan.model.CkanDataset;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanLicense;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanPair;
import eu.trentorise.opendata.jackan.model.CkanResource;
import eu.trentorise.opendata.jackan.model.CkanTag;
import eu.trentorise.opendata.jackan.model.CkanUser;

/**
 * This is the Ckan Utils implementation class.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("unchecked")
public class DataCatalogueImpl implements DataCatalogue{

	private static final Logger logger = LoggerFactory.getLogger(DataCatalogueImpl.class);

	private String CKAN_CATALOGUE_URL;
	private String CKAN_DB_NAME;
	private String CKAN_DB_USER;
	private String CKAN_DB_PASSWORD;
	private String CKAN_DB_URL;
	private Integer CKAN_DB_PORT;
	private String PORTLET_URL_FOR_SCOPE;
	private String SOLR_URL;
	private String CKAN_TOKEN_SYS;
	private String CKAN_EMAIL;
	private String URI_RESOLVER_URL;
	private boolean MANAGE_PRODUCT_BUTTON;
	private boolean ALERT_USERS_ON_POST_CREATION;
	private String CONTEXT;
	private Map<String, String> extendRoleInOrganization;

	private final static String PATH_SET_PRIVATE_DATASET = "/api/3/action/bulk_update_private";
	private final static String PATH_SET_PUBLIC_DATASET = "/api/3/action/bulk_update_public";
	private static final String CATALOGUE_TAB_ENDING_URL = "/catalogue";

	// ckan client
	private CkanClient client;

	// hashmap for ckan api keys
	private ConcurrentHashMap<String, CKANTokenBean> apiKeysMap;

	// apikey bean expires after X minutes in the above map
	private static final int EXPIRE_KEY_TIME = 60 * 60 * 1000;

	private class CKANTokenBean{
		private String apiKey;
		private long timestamp;
		public CKANTokenBean(String apiKey, long timestamp) {
			super();
			this.apiKey = apiKey;
			this.timestamp = timestamp;
		}
	}

	/**
	 * The ckan catalogue url and database will be discovered in this scope
	 * @param scope
	 * @throws Exception if unable to find datacatalogue info
	 */
	public DataCatalogueImpl(String scope) throws Exception{

		DataCatalogueRunningCluster runningInstance = new DataCatalogueRunningCluster(scope);

		// save information
		CKAN_DB_URL = runningInstance.getDatabaseHosts().get(0).trim();
		CKAN_DB_NAME = runningInstance.getDataBaseName().trim();
		CKAN_DB_USER = runningInstance.getDataBaseUser().trim();
		CKAN_DB_PASSWORD = runningInstance.getDataBasePassword().trim();
		CKAN_TOKEN_SYS = runningInstance.getSysAdminToken().trim();
		CKAN_EMAIL = runningInstance.getEmailCatalogue().trim();
		CKAN_DB_PORT = runningInstance.getDatabasePorts().get(0);
		CKAN_CATALOGUE_URL = runningInstance.getDataCatalogueUrl().get(0).trim();
		PORTLET_URL_FOR_SCOPE = runningInstance.getPortletUrl().trim();
		MANAGE_PRODUCT_BUTTON = runningInstance.isManageProductEnabled();
		URI_RESOLVER_URL = runningInstance.getUrlResolver();
		ALERT_USERS_ON_POST_CREATION = runningInstance.isAlertEnabled();
		SOLR_URL = runningInstance.getUrlSolr();

		logger.debug("Plain sys admin token first 3 chars are " + CKAN_TOKEN_SYS.substring(0, 3));
		logger.debug("Plain db password first 3 chars are " + CKAN_DB_PASSWORD.substring(0, 3));

		// build the client
		client = new CkanClient(CKAN_CATALOGUE_URL);

		// init map
		apiKeysMap = new ConcurrentHashMap<String, CKANTokenBean>();

		// save the context
		CONTEXT = scope;

		// extended roles
		extendRoleInOrganization = runningInstance.getExtendRoleInOrganization();
	}

	@Override
	public String getCatalogueUrl() {
		return CKAN_CATALOGUE_URL;
	}


	@Override
	public String getPortletUrl() {
		ScopeBean context = new ScopeBean(CONTEXT);
		String buildedUrl = PORTLET_URL_FOR_SCOPE.endsWith("/") ? PORTLET_URL_FOR_SCOPE : PORTLET_URL_FOR_SCOPE + "/";
		buildedUrl += context.name().toLowerCase() + CATALOGUE_TAB_ENDING_URL;
		return buildedUrl;
	}

	@Override
	public String getUriResolverUrl() {
		return URI_RESOLVER_URL;
	}

	/**
	 * Check if the manage product is enabled
	 * @return
	 */
	@Override
	public boolean isManageProductEnabled() {
		return MANAGE_PRODUCT_BUTTON;
	}

	/**
	 * Retrieve connection from the pool
	 * @return a connection available within the pool
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private Connection getConnection() throws SQLException, ClassNotFoundException{

		logger.debug("CONNECTION REQUEST");
		// create db connection
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(
				"jdbc:postgresql://" + CKAN_DB_URL + ":" + CKAN_DB_PORT + "/" + CKAN_DB_NAME, CKAN_DB_USER, CKAN_DB_PASSWORD);
		return connection;

	}

	/**
	 * Tries to close a connection
	 * @param connection
	 */
	private void closeConnection(Connection connection){

		if(connection != null){
			try{
				connection.close();
			}catch(Exception e){
				logger.error("Unable to close this connection ", e);
			}
		}

	}

	@Override
	public String getApiKeyFromUsername(String username) {

		logger.debug("Request api key for user = " + username);

		// checks
		checkNotNull(username);
		checkArgument(!username.isEmpty());

		// in order to avoid errors, the username is always converted
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		// check in the hashmap first
		if(apiKeysMap.containsKey(ckanUsername)){
			CKANTokenBean bean = apiKeysMap.get(ckanUsername);
			if(bean.timestamp + EXPIRE_KEY_TIME > System.currentTimeMillis()){ // it's still ok
				return bean.apiKey;
			}
		}

		logger.debug("Api key was not in cache or it expired");

		// the connection
		Connection connection = null;
		String apiToReturn = null;

		try{

			connection = getConnection();

			String query = "SELECT \"apikey\" FROM \"user\" WHERE \"name\"=? and \"state\"=?;";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, ckanUsername);
			preparedStatement.setString(2, State.ACTIVE.toString().toLowerCase());

			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				apiToReturn = rs.getString("apikey");
				logger.debug("Api key retrieved for user " + ckanUsername);
				break;
			}

			// save into the hashmap
			if(apiToReturn != null)
				apiKeysMap.put(ckanUsername, new CKANTokenBean(apiToReturn, System.currentTimeMillis()));

			return apiToReturn;

		}catch(Exception e){
			logger.error("Unable to retrieve key for user " + ckanUsername, e);
		}finally{
			closeConnection(connection);
		}

		return null;
	}

	@Override
	public CKanUserWrapper getUserFromApiKey(String apiKey) {

		logger.debug("Request user whose api key is = " + apiKey.substring(0, 3) + "*************");

		// checks
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		CKanUserWrapper user = null;

		// the connection
		Connection connection = null;

		try{

			connection = getConnection();
			String query = "SELECT * FROM \"user\" WHERE \"apikey\"=? and \"state\"=?;";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, apiKey);
			preparedStatement.setString(2, State.ACTIVE.toString().toLowerCase());

			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {

				user = new CKanUserWrapper();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setApiKey(apiKey);
				user.setCreationTimestamp(rs.getTimestamp("created").getTime());
				user.setAbout(rs.getString("about"));
				user.setOpenId(rs.getString("openid"));
				user.setFullName(rs.getString("fullname"));
				user.setEmail(rs.getString("email"));
				user.setAdmin(rs.getBoolean("sysadmin"));

				logger.debug("User retrieved");
				break;
			}
		}catch(Exception e){
			logger.error("Unable to retrieve user with api key " + apiKey, e);
		}finally{
			closeConnection(connection);
		}
		return user;
	}

	@Override
	public List<CkanOrganization> getOrganizationsByUser(String username) {

		logger.debug("Requested organizations for user " + username);

		// checks
		checkNotNull(username);

		// in order to avoid errors, the username is always converted
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		// list to return
		List<CkanOrganization> toReturn = new ArrayList<CkanOrganization>();

		try{

			// get the list of all organizations
			List<CkanOrganization> organizations = client.getOrganizationList();

			// iterate over them
			for (CkanOrganization ckanOrganization : organizations) {

				// get the list of users in it (if you try ckanOrganization.getUsers() it returns null.. maybe a bug TODO)
				List<CkanUser> users = client.getOrganization(ckanOrganization.getName()).getUsers();

				// check if the current user is among them
				for (CkanUser ckanUser : users) {
					if(ckanUser.getName().equals(ckanUsername)){

						logger.debug("User " + ckanUsername + " is into " + ckanOrganization.getName());
						toReturn.add(ckanOrganization);
						break;

					}
				}
			}
		}catch(Exception e){
			logger.error("Unable to get user's organizations", e);
		}
		return toReturn;
	}

	@Override
	public List<CkanGroup> getGroupsByUser(String username) {
		logger.debug("Requested groups for user " + username);

		// checks
		checkNotNull(username);

		// in order to avoid errors, the username is always converted
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		// list to return
		List<CkanGroup> toReturn = new ArrayList<CkanGroup>();

		try{

			// get the list of all organizations
			List<CkanGroup> groups = client.getGroupList();

			// iterate over them
			for (CkanGroup ckanGroup : groups) {

				List<CkanUser> users = client.getGroup(ckanGroup.getName()).getUsers();

				// check if the current user is among them
				for (CkanUser ckanUser : users) {
					if(ckanUser.getName().equals(ckanUsername)){

						logger.debug("User " + ckanUsername + " is into " + ckanGroup.getName());
						toReturn.add(ckanGroup);
						break;

					}
				}
			}
		}catch(Exception e){
			logger.error("Unable to get user's groups", e);
		}
		return toReturn;
	}


	@Override
	public Map<String, List<RolesCkanGroupOrOrg>> getOrganizationsAndRolesByUser(
			String username, List<RolesCkanGroupOrOrg> rolesToMatch) {

		// checks
		checkNotNull(username);
		checkArgument(!rolesToMatch.isEmpty());

		logger.debug("Requested roles that the user " + username + " has into his organizations");
		logger.debug("Roles to check are " + rolesToMatch);

		Map<String, List<RolesCkanGroupOrOrg>> toReturn = new HashMap<String, List<RolesCkanGroupOrOrg>>();

		try{

			// in order to avoid errors, the username is always converted
			String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

			// get the list of organizations in which the user is present
			List<CkanOrganization> organizationsByUser = getOrganizationsByUser(ckanUsername);

			// iterate over them
			for (CkanOrganization ckanOrganization : organizationsByUser) {

				// get the list of users in it (if you try ckanOrganization.getUsers() it returns null.. maybe a bug TODO)
				List<CkanUser> users = client.getOrganization(ckanOrganization.getName()).getUsers();

				// check if the current user is among them
				for (CkanUser ckanUser : users) {
					if(ckanUser.getName().equals(ckanUsername)){

						// the role (admin, editor, member)
						String capacity = ckanUser.getCapacity();

						// list of roles
						List<RolesCkanGroupOrOrg> rolesIntoOrg = new ArrayList<RolesCkanGroupOrOrg>();

						if(rolesToMatch.contains(RolesCkanGroupOrOrg.convertFromCapacity(capacity))){
							RolesCkanGroupOrOrg enumRole = RolesCkanGroupOrOrg.convertFromCapacity(capacity);
							rolesIntoOrg.add(enumRole);
							logger.debug("User " + ckanUsername + " has role " + enumRole +
									" into organization with name " + ckanOrganization.getName());
						}

						// save it
						if(!rolesIntoOrg.isEmpty()){
							String orgName = ckanOrganization.getName();
							toReturn.put(orgName, rolesIntoOrg);
						}
						break;

					}
				}
			}

			logger.debug("Result is " + toReturn);

			return toReturn;
		}catch(Exception e){
			logger.error("Unable to analyze user's roles", e);
		}

		return null;
	}

	@Override
	public List<String> getOrganizationsIds(){

		List<String> toReturn = new ArrayList<String>();
		List<CkanOrganization> orgs = client.getOrganizationList();

		for (CkanOrganization ckanOrganization : orgs) {
			logger.debug("Retrieved org " + ckanOrganization.getName());
			toReturn.add(ckanOrganization.getId());
		}

		return toReturn;
	}

	@Override
	public List<String> getOrganizationsNames(){

		List<String> toReturn = new ArrayList<String>();
		List<CkanOrganization> orgs = client.getOrganizationList();

		for (CkanOrganization ckanOrganization : orgs) {
			logger.debug("Retrieved org " + ckanOrganization.getName());
			toReturn.add(ckanOrganization.getName());
		}

		return toReturn;
	}

	@Override
	public List<String> getOrganizationsNamesByUser(String username) {

		logger.debug("Requested organizations for user " + username);

		// checks
		checkNotNull(username);

		// in order to avoid errors, the username is always converted
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		List<CkanOrganization> orgs = getOrganizationsByUser(ckanUsername);

		List<String> orgsName = new ArrayList<String>();

		for (CkanOrganization ckanOrganization : orgs) {
			orgsName.add(ckanOrganization.getName());
			logger.debug("Organization name is " + ckanOrganization.getName());
		}

		return orgsName;

	}

	@Override
	public String findLicenseIdByLicenseTitle(String chosenLicense) {
		logger.debug("Requested license id");

		// checks
		checkNotNull(chosenLicense);

		//retrieve the list of available licenses
		List<CkanLicense> licenses = client.getLicenseList();

		for (CkanLicense ckanLicense : licenses) {
			if(ckanLicense.getTitle().equals(chosenLicense))
				return ckanLicense.getId();
		}

		return null;
	}

	@Override
	public List<String> getLicenseTitles() {

		logger.debug("Request for CKAN licenses");

		// get the url and the api key of the user
		List<String> result = new ArrayList<String>();

		//retrieve the list of available licenses
		List<CkanLicense> licenses = client.getLicenseList();

		for (CkanLicense ckanLicense : licenses) {

			result.add(ckanLicense.getTitle());
			logger.debug("License is " + ckanLicense.getTitle() + " and id " + ckanLicense.getId());

		}

		return result;
	}

	@Override
	public List<CkanLicense> getLicenses() {
		logger.debug("Request for CKAN licenses (original jackan objects are going to be retrieved)");

		//retrieve the list of available licenses
		return client.getLicenseList();
	}

	/**
	 * Set dataset private/public
	 * @param priv
	 * @param organizationId (NOTE: The ID, not the name!)
	 * @param datasetId (NOTE: The ID, not the name!)
	 * @param apiKey the user's api key
	 * @return true on success, false otherwise
	 */
	protected boolean setDatasetPrivate(boolean priv, String organizationId,
			String datasetId, String apiKey) {

		// checks
		checkNotNull(organizationId);
		checkNotNull(apiKey);
		checkNotNull(datasetId);
		checkArgument(!apiKey.isEmpty());
		checkArgument(!datasetId.isEmpty());
		checkArgument(!organizationId.isEmpty());

		JSONObject obj = new JSONObject();
		obj.put("org_id", organizationId);

		JSONArray array = new JSONArray();
		array.add(datasetId);
		obj.put("datasets", array);

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
			HttpPost request;

			if(priv)
				request = new HttpPost(CKAN_CATALOGUE_URL + PATH_SET_PRIVATE_DATASET);
			else
				request = new HttpPost(CKAN_CATALOGUE_URL + PATH_SET_PUBLIC_DATASET);

			logger.info("Excuting request for making dataset with id " + datasetId + " " + (priv? "private" : "public"));

			request.addHeader("Authorization", apiKey);
			StringEntity params = new StringEntity(obj.toJSONString());
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);

			logger.info("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return true;

		}catch (Exception ex) {
			logger.error("Error while trying to set private the dataset ", ex);
		}


		return false;
	}

	@Override
	public String addResourceToDataset(ResourceBean resourceBean, String apiKey) throws Exception {

		logger.debug("Request to add a resource described by this bean " + resourceBean);

		// checks
		checkNotNull(resourceBean);
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		if(CatalogueUtilMethods.resourceExists(resourceBean.getUrl())){

			// in order to avoid errors, the username is always converted
			String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(resourceBean.getOwner());

			CkanResource resource = new CkanResource(CKAN_CATALOGUE_URL, resourceBean.getDatasetId());
			resource.setName(resourceBean.getName());

			// escape description
			Source description = new Source(resourceBean.getDescription());
			Segment htmlSeg = new Segment(description, 0, description.length());
			Renderer htmlRend = new Renderer(htmlSeg);

			resource.setDescription(htmlRend.toString());
			resource.setUrl(resourceBean.getUrl());
			resource.setOwner(ckanUsername);

			// Checked client
			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			CkanResource createdRes = client.createResource(resource);

			if(createdRes != null){

				logger.debug("Resource  " + createdRes.getName() + " is now available");

				return createdRes.getId();

			}
		}else
			throw new Exception("It seems there is no is no resource at this url " + resourceBean.getUrl());

		return null;
	}

	@Override
	public boolean deleteResourceFromDataset(String resourceId, String apiKey) {

		logger.error("Request to delete a resource with id " + resourceId + " coming by user with key " + apiKey);

		// checks
		checkNotNull(apiKey);
		checkNotNull(resourceId);
		checkArgument(!apiKey.isEmpty());
		checkArgument(!resourceId.isEmpty());

		try{

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			client.deleteResource(resourceId);

			return true;

		}catch(Exception e){
			logger.error("Unable to delete resource whose id is " + resourceId, e);
		}

		return false;
	}

	@Override
	public String createCKanDataset(String apiKey,
			String title, String name, String organizationNameOrId, String author,
			String authorMail, String maintainer, String maintainerMail,
			long version, String description, String licenseId,
			List<String> tags, Map<String, String> customFields,
			List<ResourceBean> resources, boolean setPublic) throws Exception{

		// delegate the private method
		return createCkanDatasetBody(apiKey,
				title, name, organizationNameOrId, author,
				authorMail, maintainer,maintainerMail,
				version, description, licenseId,
				tags, customFields, null,
				resources,  setPublic);
	}

	@Override
	public String createCKanDatasetMultipleCustomFields(String apiKey,
			String title, String name, String organizationNameOrId, String author,
			String authorMail, String maintainer, String maintainerMail,
			long version, String description, String licenseId,
			List<String> tags, Map<String, List<String>> customFieldsMultiple,
			List<ResourceBean> resources, boolean setPublic) throws Exception{

		// delegate the private method
		return createCkanDatasetBody(apiKey,
				title, name, organizationNameOrId, author,
				authorMail, maintainer,maintainerMail,
				version, description, licenseId,
				tags, null, customFieldsMultiple,
				resources,  setPublic);
	}

	// the body of the actual dataset creation methods
	private String createCkanDatasetBody(String apiKey,
			String title, String name, String organizationNameOrId, String author,
			String authorMail, String maintainer, String maintainerMail,
			long version, String description, String licenseId,
			List<String> tags, Map<String, String> customFields,
			Map<String, List<String>> customFieldsMultipleValues,
			List<ResourceBean> resources, boolean setPublic) throws Exception{

		// checks (minimum)
		checkNotNull(apiKey);
		checkNotNull(organizationNameOrId);
		checkArgument(!apiKey.isEmpty());
		checkArgument(!organizationNameOrId.isEmpty());
		checkArgument(!(title == null && name == null || title.isEmpty() && name.isEmpty()), "Name and Title cannot be empty/null at the same time!");

		logger.debug("Request for dataset creation");

		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);

		String ckanUsername = getUserFromApiKey(apiKey).getName();
		CkanDataset dataset = new CkanDataset();

		String nameToUse = name;
		if(nameToUse == null)
			nameToUse = CatalogueUtilMethods.fromProductTitleToName(title);

		logger.debug("Name of the dataset is going to be " + nameToUse + ". Title is going to be " + title);

		dataset.setName(nameToUse);
		dataset.setTitle(title);

		CkanOrganization orgOwner = client.getOrganization(organizationNameOrId);
		dataset.setOwnerOrg(orgOwner.getId());
		dataset.setAuthor(author);
		dataset.setAuthorEmail(authorMail);
		dataset.setMaintainer(maintainer);
		dataset.setMaintainerEmail(maintainerMail);
		dataset.setVersion(String.valueOf(version));

		// description must be escaped
		if(description != null && !description.isEmpty()){
			Source descriptionEscaped = new Source(description);
			Segment htmlSeg = new Segment(descriptionEscaped, 0, descriptionEscaped.length());
			Renderer htmlRend = new Renderer(htmlSeg);
			dataset.setNotes(htmlRend.toString());

			logger.debug("Description escaped " + htmlRend.toString());
		}

		dataset.setLicenseId(licenseId);

		// set the tags, if any
		if(tags != null && !tags.isEmpty()){
			List<CkanTag> ckanTags = new ArrayList<CkanTag>(tags.size());
			for (String stringTag : tags) {
				ckanTags.add(new CkanTag(stringTag));
			}
			dataset.setTags(ckanTags);
		}

		// set the custom fields, if any
		List<CkanPair> extras = new ArrayList<CkanPair>();

		if(customFields != null && !customFields.isEmpty()){

			Iterator<Entry<String, String>> iterator = customFields.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				extras.add(new CkanPair(entry.getKey(), entry.getValue()));
			}

		}else if(customFieldsMultipleValues != null && !customFieldsMultipleValues.isEmpty()){

			Iterator<Entry<String, List<String>>> iterator = customFieldsMultipleValues.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, List<String>> entry = iterator.next();
				List<String> valuesForEntry = entry.getValue();
				for (String value : valuesForEntry) {
					extras.add(new CkanPair(entry.getKey(), value));
				}
			}
		}

		dataset.setExtras(extras);

		// check if we need to add the resources
		if(resources != null && !resources.isEmpty()){

			logger.debug("We need to add resources to the dataset");

			try{
				List<CkanResource> resourcesCkan = new ArrayList<CkanResource>();
				for(ResourceBean resource: resources){

					logger.debug("Going to add resource described by " + resource);
					CkanResource newResource = new CkanResource();
					newResource.setDescription(resource.getDescription());
					newResource.setId(resource.getId());
					newResource.setUrl(resource.getUrl());
					newResource.setName(resource.getName());
					newResource.setMimetype(resource.getMimeType());
					newResource.setFormat(resource.getMimeType());
					newResource.setOwner(ckanUsername);
					resourcesCkan.add(newResource);
				}

				// add to the dataset
				dataset.setResources(resourcesCkan);

			}catch(Exception e){
				logger.error("Unable to add those resources to the dataset", e);
			}

		}

		// try to create
		CkanDataset res = null;
		res = client.createDataset(dataset);

		if(res != null){
			logger.debug("Dataset with name " + res.getName() + " has been created. Setting visibility");

			// set visibility
			boolean visibilitySet = setDatasetPrivate(
					!setPublic, // swap to private
					res.getOrganization().getId(),
					res.getId(),
					CKAN_TOKEN_SYS); // use sysadmin api key to be sure it will be set

			logger.info("Was visibility set to " + (setPublic ? "public" : "private") + "? " + visibilitySet);

			// set searchable to true if dataset visibility is private
			if(!setPublic){ // (swap to private)
				boolean searchableSet = setSearchableField(res.getId(), true);
				logger.info("Was searchable set to True? " + searchableSet);
			}
			return res.getId();
		}

		return null;
	}

	@Override
	public String updateCKanDataset(String apiKey,
			String id, String title, String name, String organizationNameOrId,
			String author, String authorMail, String maintainer,
			String maintainerMail, long version, String description,
			String licenseId, List<String> tags, List<String> groupNames,
			Map<String, List<String>> customFields,
			List<ResourceBean> resources, boolean setPublic) throws Exception {

		// checks (minimum)
		checkNotNull(apiKey);
		checkNotNull(id);
		checkNotNull(organizationNameOrId);
		checkArgument(!apiKey.isEmpty());
		checkArgument(!organizationNameOrId.isEmpty());
		checkArgument(!id.isEmpty());
		checkArgument(!(title == null && name == null || title.isEmpty() && name.isEmpty()), "Name and Title cannot be empty/null at the same time!");

		logger.debug("Request for dataset update");

		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);

		String ckanUsername = getUserFromApiKey(apiKey).getName();

		// get dataset from id
		CkanDataset dataset = client.getDataset(id);

		CkanOrganization orgOwner = client.getOrganization(organizationNameOrId);
		dataset.setOwnerOrg(orgOwner.getId());
		dataset.setAuthor(author);
		dataset.setAuthorEmail(authorMail);
		dataset.setMaintainer(maintainer);
		dataset.setMaintainerEmail(maintainerMail);
		dataset.setVersion(String.valueOf(version));

		// description must be escaped
		dataset.setNotes("");
		if(description != null && !description.isEmpty()){
			Source descriptionEscaped = new Source(description);
			Segment htmlSeg = new Segment(descriptionEscaped, 0, descriptionEscaped.length());
			Renderer htmlRend = new Renderer(htmlSeg);
			dataset.setNotes(htmlRend.toString());
		}

		// update license id
		dataset.setLicenseId(licenseId);

		// remove any tags
		dataset.setTags(new ArrayList<CkanTag>(0));

		// set the tags, if any
		if(tags != null && !tags.isEmpty()){
			List<CkanTag> ckanTags = new ArrayList<CkanTag>(tags.size());
			for (String stringTag : tags) {
				ckanTags.add(new CkanTag(stringTag));
			}
			dataset.setTags(ckanTags);
		}

		// remove extras
		dataset.setExtras(new ArrayList<CkanPair>(0));

		if(customFields != null && !customFields.isEmpty()){

			logger.debug("Provided custom fields are " + customFields);

			// set the custom fields, if any
			List<CkanPair> extras = new ArrayList<CkanPair>();

			Iterator<Entry<String, List<String>>> iterator = customFields.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, List<String>> entry = iterator.next();
				List<String> valuesForEntry = entry.getValue();
				for (String value : valuesForEntry) {
					extras.add(new CkanPair(entry.getKey(), value));
				}
			}
			dataset.setExtras(extras);
		}

		// remove resources
		dataset.setResources(new ArrayList<CkanResource>(0));

		// check if we need to add the resources
		if(resources != null && !resources.isEmpty()){
			try{
				List<CkanResource> resourcesCkan = new ArrayList<CkanResource>();
				for(ResourceBean resource: resources){
					logger.trace("Going to add resource described by " + resource);
					CkanResource newResource = new CkanResource();
					newResource.setDescription(resource.getDescription());
					newResource.setId(resource.getId());
					newResource.setUrl(resource.getUrl());
					newResource.setName(resource.getName());
					newResource.setMimetype(resource.getMimeType());
					newResource.setFormat(resource.getMimeType());
					newResource.setOwner(ckanUsername);
					resourcesCkan.add(newResource);
				}
				// add to the dataset
				dataset.setResources(resourcesCkan);
			}catch(Exception e){
				logger.error("Unable to add those resources to the dataset", e);
			}

		}

		// remove groups
		dataset.setGroups(new ArrayList<CkanGroup>(0));

		// add groups if any
		if(groupNames != null){
			List<CkanGroup> groups = new ArrayList<CkanGroup>();
			for (String groupName : groupNames) {
				CkanGroup group = client.getGroup(groupName);
				groups.add(group);
			}
			dataset.setGroups(groups);
		}

		// try to update
		CkanDataset updated = client.updateDataset(dataset);

		// reset visibility and searchability
		if(updated != null){
			logger.debug("Dataset with name " + updated.getName() + " has been updated. Setting visibility");

			// set visibility
			boolean visibilitySet = setDatasetPrivate(
					!setPublic, // swap to private
					updated.getOrganization().getId(),
					updated.getId(),
					CKAN_TOKEN_SYS); // use sysadmin api key to be sure it will be set

			logger.info("Was visibility set to " + (setPublic ? "public" : "private") + "? " + visibilitySet);

			if(!setPublic){
				boolean searchableSet = setSearchableField(updated.getId(), true);
				logger.info("Was searchable set to True? " + searchableSet);
			}
			return updated.getId();
		}

		return null;

	}

	@Override
	public String getUrlFromDatasetIdOrName(String datasetIdOrName) {

		logger.debug("Request coming for getting dataset url (encrypted) of dataset with name/id " + datasetIdOrName);

		// checks
		checkNotNull(datasetIdOrName);
		checkArgument(!datasetIdOrName.isEmpty());
		String url = null;
		try{

			// get the dataset from name
			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			CkanDataset dataset = client.getDataset(datasetIdOrName);
			String name = dataset.getName();

			if(dataset != null){

				if(getUriResolverUrl() != null)
					url = getUrlForProduct(CONTEXT, EntityContext.PRODUCT, name, false);

				if(url == null || url.isEmpty())
					url = getPortletUrl() + "?" + URLEncoder.encode("path=/dataset/"  + name, "UTF-8");

			}
		}catch(Exception e){
			logger.error("Error while retrieving dataset with id/name=" + datasetIdOrName, e);
		}
		return url;
	}

	@Override
	public String getUnencryptedUrlFromDatasetIdOrName(String datasetIdOrName) {
		logger.debug("Request coming for getting dataset url (not encrypted) of dataset with name/id " + datasetIdOrName);

		// checks
		checkNotNull(datasetIdOrName);
		checkArgument(!datasetIdOrName.isEmpty());
		String url = null;
		try{

			// get the dataset from name
			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			CkanDataset dataset = client.getDataset(datasetIdOrName);
			String name = dataset.getName();

			if(dataset != null){

				if(getUriResolverUrl() != null)
					url = getUrlForProduct(CONTEXT, EntityContext.PRODUCT, name, true);

				if(url == null || url.isEmpty())
					url = getPortletUrl() + "?" + URLEncoder.encode("path=/dataset/"  + name, "UTF-8");

			}
		}catch(Exception e){
			logger.error("Error while retrieving dataset with id/name=" + datasetIdOrName, e);
		}
		return url;
	}

	/**
	 * Retrieve an url for the tuple scope, entity, entity name
	 * @param context
	 * @param entityContext
	 * @param entityName
	 * @return the url for the product
	 */
	private String getUrlForProduct(String context, EntityContext entityContext, String entityName, boolean unencrypted){

		String toReturn = null;

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			HttpPost httpPostRequest = new HttpPost(getUriResolverUrl());

			JSONObject requestEntity = new JSONObject();
			requestEntity.put("gcube_scope", context);
			requestEntity.put("entity_context", entityContext.toString());
			requestEntity.put("entity_name", entityName);
			requestEntity.put("clear_url", Boolean.toString(unencrypted));

			StringEntity params = new StringEntity(requestEntity.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if(response.getStatusLine().getStatusCode() != 200)
				throw new Exception("There was an error while creating an url " + response.getStatusLine());

			toReturn = EntityUtils.toString(response.getEntity());
			logger.debug("Result is " + toReturn);

		}catch(Exception e){
			logger.error("Failed to get an url for this product", e);
		}

		return  toReturn;
	}

	@Override
	public boolean checkRoleIntoOrganization(String username, String organizationName,
			RolesCkanGroupOrOrg correspondentRoleToCheck) {

		logger.debug("Request for checking if " + username + " into organization " + organizationName + " has role " + correspondentRoleToCheck);

		// checks
		checkNotNull(username);
		checkNotNull(organizationName);
		checkNotNull(correspondentRoleToCheck);
		checkArgument(!username.isEmpty());
		checkArgument(!organizationName.isEmpty());

		// convert ckan username
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		// check if this role is already present in ckan for this user within the organization
		String organizationNameToCheck = organizationName.toLowerCase();

		try{
			boolean alreadyPresent = isRoleAlreadySet(ckanUsername, organizationNameToCheck, correspondentRoleToCheck, false);

			if(alreadyPresent)
				return true; // just return
			else{

				// we need to use the apis to make it
				String path = "/api/3/action/organization_member_create";

				JSONObject obj = new JSONObject();
				obj.put("id", organizationNameToCheck);
				obj.put("username", ckanUsername);
				obj.put("role", RolesCkanGroupOrOrg.convertToCkanCapacity(correspondentRoleToCheck));

				logger.debug("API request for organization membership is going to be " + obj.toJSONString());

				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
					HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
					request.addHeader("Authorization", CKAN_TOKEN_SYS); // sys token
					StringEntity params = new StringEntity(obj.toJSONString());
					request.setEntity(params);
					HttpResponse response = httpClient.execute(request);
					logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

					return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

				}catch (Exception ex) {
					logger.error("Error while trying to change the role for this user ", ex);
				}
			}
		}catch (Exception ex) {
			logger.error("Unable to check if this role was already set, please check your parameters! ", ex);
		}

		return false;
	}

	/**
	 * Check if the user has this role into the organization/group with groupOrOrganization name
	 * @param ckanUsername
	 * @param organizationName
	 * @param correspondentRoleToCheck
	 * @return true if he has the role, false otherwise
	 */
	protected boolean isRoleAlreadySet(String ckanUsername, String groupOrOrganization, RolesCkanGroupOrOrg correspondentRoleToCheck, boolean group) throws Exception{

		// get the users (if you try ckanOrganization.getUsers() it returns null.. maybe a bug TODO)
		List<CkanUser> users;

		if(group)
			users = client.getGroup(groupOrOrganization).getUsers();
		else
			users = client.getOrganization(groupOrOrganization).getUsers();

		for (CkanUser ckanUser : users) {
			if(ckanUser.getName().equals(ckanUsername))
				if(ckanUser.getCapacity().equals(RolesCkanGroupOrOrg.convertToCkanCapacity(correspondentRoleToCheck)))
					return true;
				else
					break;
		}

		return false;
	}

	@Override
	public boolean checkRoleIntoGroup(String username, String groupName, RolesCkanGroupOrOrg correspondentRoleToCheck) {
		logger.debug("Request for checking if " + username + " into group " + groupName + " has role " + correspondentRoleToCheck);

		// checks
		checkNotNull(username);
		checkNotNull(groupName);
		checkNotNull(correspondentRoleToCheck);
		checkArgument(!username.isEmpty());
		checkArgument(!groupName.isEmpty());

		// convert ckan username
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		// check if this role is already present in ckan for this user within the group
		String groupNameToCheck = CatalogueUtilMethods.fromGroupTitleToName(groupName);

		try{
			boolean alreadyPresent = isRoleAlreadySet(ckanUsername, groupNameToCheck, correspondentRoleToCheck, true);

			if(alreadyPresent)
				return true; // just return
			else{

				// we need to use the apis to make it
				String path = "/api/3/action/group_member_create";

				JSONObject obj = new JSONObject();
				obj.put("id", groupNameToCheck);
				obj.put("username", ckanUsername);
				obj.put("role", RolesCkanGroupOrOrg.convertToCkanCapacity(correspondentRoleToCheck));

				logger.debug("API request for organization membership is going to be " + obj.toJSONString());

				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
					HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
					request.addHeader("Authorization", CKAN_TOKEN_SYS); // sys token
					StringEntity params = new StringEntity(obj.toJSONString());
					request.setEntity(params);
					HttpResponse response = httpClient.execute(request);
					logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

					return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

				}catch (Exception ex) {
					logger.error("Error while trying to change the role for this user ", ex);
				}
			}
		}catch (Exception ex) {
			logger.error("Unable to check if this role was already set, please check your parameters! ", ex);
		}

		return false;
	}

	@Override
	public boolean isSysAdmin(String username) {

		// checks
		checkNotNull(username);
		checkArgument(!username.isEmpty());

		// in order to avoid errors, the username is always converted
		String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);
		try{
			return client.getUser(ckanUsername).isSysadmin();
		}catch(Exception e){
			logger.error("Failed to check if the user " + username + " has role sysadmin", e);
		}

		return false;
	}

	@Override
	public boolean createDatasetRelationship(String datasetIdSubject,
			String datasetIdObject, DatasetRelationships relation, String relationComment, String apiKey) {

		// checks
		checkNotNull(datasetIdSubject);
		checkNotNull(datasetIdObject);
		checkNotNull(relation);
		checkNotNull(apiKey);
		checkArgument(!datasetIdSubject.isEmpty());
		checkArgument(!datasetIdObject.isEmpty());
		checkArgument(!apiKey.isEmpty());

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			// we need to use the apis to make it
			String path = "/api/3/action/package_relationship_create";

			JSONObject obj = new JSONObject();
			obj.put("subject", datasetIdSubject);
			obj.put("object", datasetIdObject);
			obj.put("type", relation.toString());
			if(relationComment != null && !relationComment.isEmpty())
				obj.put("comment", relationComment);

			logger.debug("API request for relationship create is going to be " + obj.toJSONString());

			HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
			request.addHeader("Authorization", apiKey);
			StringEntity params = new StringEntity(obj.toJSONString());
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

		}catch(Exception e){
			logger.error("Failed to create the relationship between dataset subject = "  + datasetIdSubject
					+ " and " + " dataset subject " + datasetIdObject, e);
		}
		return false;
	}

	@Override
	public boolean deleteDatasetRelationship(String datasetIdSubject,
			String datasetIdObject, DatasetRelationships relation, String apiKey) {

		// checks
		checkNotNull(datasetIdSubject);
		checkNotNull(datasetIdObject);
		checkNotNull(relation);
		checkNotNull(apiKey);
		checkArgument(!datasetIdSubject.isEmpty());
		checkArgument(!datasetIdObject.isEmpty());
		checkArgument(!apiKey.isEmpty());

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			// we need to use the apis to make it
			String path = "/api/3/action/package_relationship_delete";

			JSONObject obj = new JSONObject();
			obj.put("subject", datasetIdSubject);
			obj.put("object", datasetIdObject);
			obj.put("type", relation.toString());

			logger.debug("API request for delete relationship is going to be " + obj.toJSONString());

			HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
			request.addHeader("Authorization", apiKey);
			StringEntity params = new StringEntity(obj.toJSONString());
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
		}catch(Exception e){
			logger.error("Failed to delete the relationship between dataset subject = "  + datasetIdSubject
					+ " and " + " dataset subject " + datasetIdObject, e);
		}
		return false;
	}

	@Override
	public List<CkanDatasetRelationship> getRelationshipDatasets(
			String datasetIdSubject, String datasetIdObject, String apiKey) {

		// checks
		checkNotNull(datasetIdSubject);
		checkNotNull(apiKey);
		checkArgument(!datasetIdSubject.isEmpty());
		checkArgument(!apiKey.isEmpty());

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			List<CkanDatasetRelationship> toReturn = new ArrayList<CkanDatasetRelationship>();

			// we need to use the apis to make it
			String path = "/api/3/action/package_relationships_list";

			JSONObject obj = new JSONObject();
			obj.put("id", datasetIdSubject);

			// Request parameters to be replaced
			if(datasetIdObject != null && !datasetIdObject.isEmpty())
				obj.put("id2", datasetIdObject);

			logger.debug("API request for getting relationship is going to be " + obj.toJSONString());

			HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
			request.addHeader("Authorization", apiKey);
			StringEntity params = new StringEntity(obj.toJSONString());
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

				String res = EntityUtils.toString(response.getEntity());

				// parse the json object returned
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(res);
				JSONArray resultJson = (JSONArray) json.get("result");
				Iterator<JSONObject> it = resultJson.iterator();

				while (it.hasNext()) {
					JSONObject object = it.next();
					try{
						CkanDatasetRelationship relation = new CkanDatasetRelationship(object);
						toReturn.add(relation);
					}catch(Exception e){
						logger.error("Error while building CkanRelationship bean from object " +  object, e);
					}
				}
			}

			return toReturn;

		}catch(Exception e){
			logger.error("Failed to retrieve the relationship between dataset subject = "  + datasetIdSubject
					+ " and " + " dataset object " + datasetIdObject, e);
		}
		return null;
	}

	@Override
	public boolean existProductWithNameOrId(String nameOrId) {

		// checks
		checkNotNull(nameOrId);
		checkArgument(!nameOrId.isEmpty());

		try{
			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			CkanDataset product = client.getDataset(nameOrId);
			return product != null;
		}catch(Exception e){
			logger.debug("A dataset with name " + nameOrId + " doesn't exist");
			return false;
		}
	}

	@Override
	public CkanGroup createGroup(String nameOrId, String title, String description) {

		// checks
		checkNotNull(nameOrId);
		checkArgument(!nameOrId.trim().isEmpty());

		// check if it exists
		CkanGroup toCreate = null;
		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);

		logger.debug("Request for creating group with name " + nameOrId + " title " + title + " and description " + description);

		String name = CatalogueUtilMethods.fromGroupTitleToName(nameOrId);
		if((toCreate = groupExists(name, client))!= null)
			return toCreate;
		else{
			try{

				CkanGroup group = new CkanGroup(name);
				group.setTitle(title);
				group.setDisplayName(title);
				group.setDescription(description);
				toCreate = client.createGroup(group);

			}catch(JackanException je){
				logger.error("Unable to create such a group", je);
			}
		}

		return toCreate;
	}

	/**
	 * Just check if the group exists
	 * @param nameOrId
	 * @param client
	 * @return
	 */
	private CkanGroup groupExists(String nameOrId, ExtendCkanClient client){

		CkanGroup toReturn = null;

		try{

			toReturn = client.getGroup(nameOrId);

		}catch(JackanException je){
			logger.error("This group doesn't exist");
		}

		return toReturn;
	}

	@Override
	public boolean assignDatasetToGroup(String groupNameOrId, String datasetNameOrId, String apiKey) {

		return assignDatasetToGroupBody(groupNameOrId, datasetNameOrId, apiKey, false);

	}

	@Override
	public boolean assignDatasetToGroup(String groupNameOrId, String datasetNameOrId, String apiKey, boolean addOnParents) {

		return assignDatasetToGroupBody(groupNameOrId, datasetNameOrId, apiKey, addOnParents);

	}

	/**
	 * Find the hierarchy of trees
	 * @param uniqueGroups
	 * @param catalogue
	 * @param user's api key
	 */
	private void findHierarchyGroups(
			List<String> groupsTitles,
			String apiKey) {
		ListIterator<String> iterator = groupsTitles.listIterator();
		while (iterator.hasNext()) {
			String group = iterator.next();

			List<CkanGroup> parents = getParentGroups(group, apiKey);

			if(parents == null || parents.isEmpty())
				return;

			for (CkanGroup ckanGroup : parents) {
				List<String> parentsList = new ArrayList<String>(Arrays.asList(ckanGroup.getName()));
				findHierarchyGroups(parentsList, apiKey);
				for (String parent : parentsList) {
					iterator.add(parent);
				}
			}
		}

	}

	/**
	 * The real body of the assignDatasetToGroup
	 * @param groupNameOrId
	 * @param datasetNameOrId
	 * @param apiKey
	 * @param addOnParents
	 * @return
	 */
	private boolean assignDatasetToGroupBody(String groupNameOrId, String datasetNameOrId, String apiKey, boolean addOnParents) {

		// checks
		checkNotNull(groupNameOrId);
		checkArgument(!groupNameOrId.isEmpty());
		checkNotNull(datasetNameOrId);
		checkArgument(!datasetNameOrId.isEmpty());
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		String groupNameToCheck = CatalogueUtilMethods.fromGroupTitleToName(groupNameOrId);

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);

			// check the group exists
			CkanGroup group = client.getGroup(groupNameToCheck);

			// move to a list
			List<String> groupNames = new ArrayList<String>();
			groupNames.add(group.getName());
			if(group != null && addOnParents){
				findHierarchyGroups(groupNames, CKAN_TOKEN_SYS);
			}

			// we need to use the apis to make it
			String pathPackageShow = CKAN_CATALOGUE_URL + "/api/3/action/package_show?id=" + datasetNameOrId;
			HttpGet getRequest = new HttpGet(pathPackageShow);
			getRequest.addHeader("Authorization", CKAN_TOKEN_SYS);
			HttpResponse response = httpClient.execute(getRequest);

			logger.debug("Response is " + response.getStatusLine().getStatusCode() + " and message is " + response.getStatusLine().getReasonPhrase());

			// read the json dataset and fetch the groups and fetch the groups' names, if any
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

				// parse the json and convert to java beans
				String jsonAsString = EntityUtils.toString(response.getEntity());
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(jsonAsString);
				JSONObject resultJson =  (JSONObject) json.get("result");
				JSONArray groupsJson = (JSONArray)resultJson.get("groups");
				Iterator<JSONObject> it =  groupsJson.iterator();

				while (it.hasNext()) {
					JSONObject object = it.next();
					try{
						if(object.containsKey("name"))
							groupNames.add((String)object.get("name"));
					}catch(Exception e){
						logger.error("Error", e);
					}
				}

				// remove duplicates
				Set<String> groupNamesSet = new HashSet<String>(groupNames);

				logger.debug("Groups to be added are " + groupNamesSet);

				// now we patch the dataset with the new group
				String pathUpdatePatch = CKAN_CATALOGUE_URL + "/api/3/action/package_patch";

				JSONObject req = new JSONObject();
				req.put("id", datasetNameOrId);

				JSONArray groups = new JSONArray();
				Iterator<String> iteratorNameSet = groupNamesSet.iterator();
				while (iteratorNameSet.hasNext()) {
					String groupName = iteratorNameSet.next();
					JSONObject groupJSON = new JSONObject();
					groupJSON.put("name", groupName);
					groups.add(groupJSON);
				}
				req.put("groups", groups);

				logger.debug("Request for patch is going to be " + req.toJSONString());

				HttpPost request = new HttpPost(pathUpdatePatch);
				request.addHeader("Authorization", CKAN_TOKEN_SYS);
				StringEntity params = new StringEntity(req.toJSONString());
				request.setEntity(params);
				HttpResponse responsePatch = httpClient.execute(request);
				logger.debug("Response code is " + responsePatch.getStatusLine().getStatusCode() + " and response message is " + responsePatch.getStatusLine().getReasonPhrase());

				if(responsePatch.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					logger.info("Dataset Added to the group!!");
					return true;
				}

			}

		}catch(Exception e){
			logger.error("Unable to make this association", e);
		}

		return false;
	}


	@Override
	public boolean removeDatasetFromGroup(String groupNameOrId,
			String datasetNameOrId, String apiKey) {

		// checks
		checkNotNull(groupNameOrId);
		checkNotNull(datasetNameOrId);
		checkNotNull(apiKey);

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/package_patch";
			HttpPost httpPostRequest = new HttpPost(apiRequestUrl);
			httpPostRequest.setHeader("Authorization", CKAN_TOKEN_SYS);

			// Request parameters to be replaced
			JSONObject jsonRequest = new JSONObject();
			JSONArray groupsAsJson = new JSONArray();

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			CkanDataset dataset = client.getDataset(datasetNameOrId);
			List<CkanGroup> currentGroups = dataset.getGroups();


			// get the id of the group to be removed
			String groupId = client.getGroup(groupNameOrId).getId();

			for(CkanGroup ckanGroup : currentGroups){
				if(!ckanGroup.getId().equals(groupId)){
					JSONObject obj = new JSONObject();
					obj.put("display_name", ckanGroup.getDisplayName());
					obj.put("description", ckanGroup.getDescription());
					obj.put("image_display_url", ckanGroup.getImageDisplayUrl());
					obj.put("title", ckanGroup.getTitle());
					obj.put("id", ckanGroup.getId());
					obj.put("name", ckanGroup.getName());
					groupsAsJson.add(obj);
				}
			}

			// perform the request
			jsonRequest.put("id", datasetNameOrId);
			jsonRequest.put("groups", groupsAsJson);

			logger.debug("Request param is going to be " + jsonRequest);

			StringEntity params = new StringEntity(jsonRequest.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to patch the product. response status line from "
						+ apiRequestUrl + " was: " + response.getStatusLine());
			}

			return true;

		}catch(Exception e){
			logger.error("Failed to remove the group " + groupNameOrId + " from product " + datasetNameOrId, e);
		}

		return false;
	}

	@Override
	public Map<String, List<String>> getRolesAndUsersOrganization(String organizationName) {

		// checks
		checkNotNull(organizationName);
		checkArgument(!organizationName.isEmpty());

		Map<String, List<String>> capacityAndUsers = new HashMap<String, List<String>>();
		CkanOrganization org = client.getOrganization(organizationName);
		List<CkanUser> users = org.getUsers();
		for (CkanUser ckanUser : users) {

			logger.debug(ckanUser.getName());

			List<String> listUsers;
			if(capacityAndUsers.containsKey(ckanUser.getCapacity())){
				listUsers = capacityAndUsers.get(ckanUser.getCapacity());
			}else
				listUsers = new ArrayList<String>();

			listUsers.add(ckanUser.getName());
			capacityAndUsers.put(ckanUser.getCapacity(), listUsers);

		}

		return capacityAndUsers;
	}

	@Override
	public Map<RolesCkanGroupOrOrg, List<String>> getRolesAndUsersGroup(String groupName) {

		// checks
		checkNotNull(groupName);
		checkArgument(!groupName.isEmpty());
		Map<RolesCkanGroupOrOrg, List<String>> capacityAndUsers = null;
		String groupNameToCheck = CatalogueUtilMethods.fromGroupTitleToName(groupName);

		CkanGroup group = client.getGroup(groupNameToCheck);

		if(group != null){
			capacityAndUsers = new HashMap<RolesCkanGroupOrOrg, List<String>>();
			List<CkanUser> users = group.getUsers();
			for (CkanUser ckanUser : users) {
				List<String> listUsers;
				if(capacityAndUsers.containsKey(RolesCkanGroupOrOrg.convertFromCapacity(ckanUser.getCapacity()))){
					listUsers = capacityAndUsers.get(RolesCkanGroupOrOrg.convertFromCapacity(ckanUser.getCapacity()));
				}else
					listUsers = new ArrayList<String>();

				listUsers.add(ckanUser.getName());
				capacityAndUsers.put(RolesCkanGroupOrOrg.convertFromCapacity(ckanUser.getCapacity()), listUsers);

			}

			logger.info("Returning " + capacityAndUsers);
		}
		return capacityAndUsers;
	}

	@Override
	public String getRoleOfUserInOrganization(String username, String orgName, String apiKey) {

		String toReturn = null;

		String usernameCkan = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		try{

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			List<CkanUser> users = client.getOrganization(orgName).getUsers();
			for (CkanUser ckanUser : users) {
				if(ckanUser.getName().equals(usernameCkan)){
					toReturn = ckanUser.getCapacity();
					break;
				}
			}

		}catch(Exception e){
			logger.error("Unable to retrieve the role the user has into this organization", e);
		}

		return toReturn;
	}

	@Override
	public String getRoleOfUserInGroup(String username, String groupName, String apiKey) {

		String toReturn = null;

		String usernameCkan = CatalogueUtilMethods.fromUsernameToCKanUsername(username);

		try{

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			List<CkanUser> users = client.getGroup(groupName).getUsers();
			for (CkanUser ckanUser : users) {
				if(ckanUser.getName().equals(usernameCkan)){
					toReturn = ckanUser.getCapacity();
					break;
				}
			}

		}catch(Exception e){
			logger.error("Unable to retrieve the role the user has into this group", e);
		}

		return toReturn;
	}


	@Override
	public boolean deleteProduct(String datasetId, String apiKey, boolean purge) {

		// checks
		checkNotNull(datasetId);
		checkArgument(!datasetId.isEmpty());
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		logger.debug("Incoming request of deleting dataset with id " + datasetId);

		try{

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			client.deleteDataset(datasetId);
			logger.info("Dataset with id " + datasetId + " deleted!");

			if(purge){

				logger.debug("Purging also ....");
				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){
					String path = CKAN_CATALOGUE_URL + "/api/3/action/dataset_purge";
					HttpPost request = new HttpPost(path);
					request.addHeader("Authorization", CKAN_TOKEN_SYS); // this must be a sys_admin key
					JSONObject object = new JSONObject();
					object.put("id", datasetId);
					StringEntity params = new StringEntity(object.toJSONString());
					request.setEntity(params);
					HttpResponse response = httpClient.execute(request);

					logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

					if(response.getStatusLine().getStatusCode() == 200){

						logger.info("Dataset with id " + datasetId + " delete and purged!");
						return true;
					}

					return false;
				}

			}
			return true;

		}catch(Exception e){
			logger.error("Unable to delete such dataset ", e);
		}
		return false;
	}

	@Override
	public CkanResource getResource(String id, String apiKey) {
		logger.info("Request ckan resource with id " + id);

		// checks
		checkNotNull(id);
		checkArgument(!id.isEmpty());
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		try{
			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			return client.getResource(id);
		}catch(Exception e){
			logger.error("Unable to retrieve such resource, returning null ...", e);
		}

		return null;
	}

	@Override
	public CkanDataset getDataset(String datasetId, String apiKey) {

		logger.info("Request ckan dataset with id " + datasetId);

		// checks
		checkNotNull(datasetId);
		checkArgument(!datasetId.isEmpty());
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		try{
			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			return client.getDataset(datasetId);
		}catch(Exception e){
			logger.error("Unable to retrieve such dataset, returning null ...", e);
		}

		return null;
	}

	@Override
	public CkanDataset getDataset(String datasetId) throws Exception{
		logger.info("Request ckan dataset with id " + datasetId);

		// checks
		checkNotNull(datasetId);
		checkArgument(!datasetId.isEmpty());

		CkanClient client = new CkanClient(CKAN_CATALOGUE_URL);
		return client.getDataset(datasetId);

	}

	@Override
	public boolean setSearchableField(String datasetId, boolean searchable) {

		// checks
		checkNotNull(datasetId);
		checkArgument(!datasetId.isEmpty());
		String searchableAsString = searchable ? "True" : "False";

		// Patch package path
		String patchPackage = CKAN_CATALOGUE_URL + "/api/3/action/package_patch";

		JSONObject obj = new JSONObject();
		obj.put("id", datasetId);
		obj.put("searchable", searchableAsString);

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
			HttpPost request = new HttpPost(patchPackage);
			request.addHeader("Authorization", CKAN_TOKEN_SYS);
			StringEntity params = new StringEntity(obj.toJSONString());
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return true;

		}catch (Exception ex) {
			logger.error("Error while trying to set searchable the dataset ", ex);
		}

		return false;
	}

	@Override
	public List<CkanGroup> getGroups() {

		try{
			CkanClient client = new CkanClient(CKAN_CATALOGUE_URL);
			return client.getGroupList();
		}catch(Exception e){
			logger.error("Failed to retrieve the list groups", e);
			return null;
		}

	}

	@Override
	public CkanResource uploadResourceFile(File file, String packageId,
			String token, String name, String description, String mimeType, String format) {

		// checks
		checkNotNull(file);
		checkNotNull(packageId);
		checkNotNull(token);
		checkNotNull(name);

		String returnedId = null;

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/resource_create";
			HttpPost httpPostRequest = new HttpPost(apiRequestUrl);
			httpPostRequest.setHeader("Authorization", token);
			HttpEntity mpEntity =
					MultipartEntityBuilder.create()
					.addTextBody("package_id", packageId, ContentType.TEXT_PLAIN)
					.addTextBody("url", "upload", ContentType.TEXT_PLAIN)
					.addTextBody("description", description == null ? "" : description, ContentType.TEXT_PLAIN)
					.addTextBody("name", name, ContentType.TEXT_PLAIN)
					.addTextBody("mimetype", mimeType)
					.addTextBody("format", format)
					.addBinaryBody("upload", file,
							ContentType.create(
									"application/octet-stream",
									Charset.forName("UTF-8")),
									name)
									.build();

			httpPostRequest.setEntity(mpEntity);
			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to add the file to CKAN storage. response status line from "
						+ apiRequestUrl + " was: " + response.getStatusLine());
			}

			logger.info("Returned message is " + response.getStatusLine());

			String json = EntityUtils.toString(response.getEntity());
			Object obj = JSONValue.parse(json);
			JSONObject finalResult=(JSONObject)obj;
			JSONObject result = (JSONObject)finalResult.get("result");
			logger.debug("Returned json is " + result.get("id"));
			returnedId = (String) result.get("id");
			return new ExtendCkanClient(CKAN_CATALOGUE_URL, token).getResource(returnedId);
		} catch (Exception e) {
			logger.error("Error while uploading file");
			return null;
		}
	}


	@Override
	public boolean patchResource(String resourceId, String url,
			String name, String description, String urlType, String apiKey) {

		// checks
		checkNotNull(resourceId);
		checkNotNull(apiKey);

		logger.debug("Going to change resource with id " + resourceId +"."
				+ " Request comes from user with key " + apiKey.substring(0, 5) + "****************");


		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/resource_patch";
			HttpPost httpPostRequest = new HttpPost(apiRequestUrl);
			httpPostRequest.setHeader("Authorization", apiKey);

			// Request parameters to be replaced
			JSONObject jsonRequest = new JSONObject();
			Map<String, String> requestMap = new HashMap<String, String>();

			requestMap.put("id", resourceId);
			if(url != null && !url.isEmpty())
				requestMap.put("url", url);
			if(description != null)
				requestMap.put("description", description);
			if(name != null && !name.isEmpty())
				requestMap.put("name", name);
			if(urlType != null)
				requestMap.put("url_type", urlType);

			jsonRequest.putAll(requestMap);
			StringEntity params = new StringEntity(jsonRequest.toJSONString());
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to patch the resource. response status line from "
						+ apiRequestUrl + " was: " + response.getStatusLine());
			}

			return true;

		}catch(Exception e){
			logger.error("Failed to update the resource ", e);
		}

		return false;
	}

	@Override
	public boolean patchProductCustomFields(String productId, String apiKey,
			Map<String, List<String>> customFieldsToChange, boolean removeOld) {

		// checks
		checkNotNull(productId);
		checkNotNull(apiKey);

		if(customFieldsToChange == null || customFieldsToChange.isEmpty()) // TODO.. remove all custom fields maybe?!
			return true;

		logger.info("Going to change product with id " + productId +"."
				+ " Request comes from user with key " + apiKey.substring(0, 5) + "****************");

		logger.info("The new values are " + customFieldsToChange);

		// Get already available custom fields
		Map<String, List<String>> fromCKANCustomFields = new HashMap<String, List<String>>();
		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
		List<CkanPair> extras = client.getDataset(productId).getExtras();

		if(extras == null)
			extras = new ArrayList<CkanPair>();

		// fill the above map with these values
		for (CkanPair ckanPair : extras) {
			List<String> forThisValue = null;
			String key = ckanPair.getKey();
			if(fromCKANCustomFields.containsKey(key))
				forThisValue = fromCKANCustomFields.get(key);
			else
				forThisValue = 	new ArrayList<String>();
			forThisValue.add(ckanPair.getValue());
			fromCKANCustomFields.put(key, forThisValue);
		}

		logger.info("The generated map from jackan looks like " + fromCKANCustomFields + ". Going to merge them");

		// merge them with the new values
		Iterator<Entry<String, List<String>>> iteratorUserMap = customFieldsToChange.entrySet().iterator();
		while (iteratorUserMap.hasNext()) {
			Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = iteratorUserMap
					.next();

			String key = entry.getKey();
			List<String> newValues = entry.getValue();

			// get the unique set of values
			Set<String> uniqueValues = new HashSet<String>();

			if(fromCKANCustomFields.containsKey(key))
				if(!removeOld)
					uniqueValues.addAll(fromCKANCustomFields.get(key));

			uniqueValues.addAll(newValues);
			fromCKANCustomFields.put(key, new ArrayList<String>(uniqueValues));
		}

		logger.info("After merging it is " + fromCKANCustomFields);

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/package_patch";
			HttpPost httpPostRequest = new HttpPost(apiRequestUrl);
			httpPostRequest.setHeader("Authorization", apiKey);

			// Request parameters to be replaced
			JSONObject jsonRequest = new JSONObject();

			// build the json array for the "extras" field.. each object looks like {"key": ..., "value": ...}
			JSONArray extrasObject = new JSONArray();

			Iterator<Entry<String, List<String>>> iteratorNewFields = fromCKANCustomFields.entrySet().iterator();
			while (iteratorNewFields.hasNext()) {
				Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = iteratorNewFields
						.next();

				String key = entry.getKey();
				List<String> values = entry.getValue();

				for (String value : values) {
					JSONObject obj = new JSONObject();
					obj.put("value", value);
					obj.put("key", key);
					extrasObject.add(obj);
				}
			}

			// perform the request
			jsonRequest.put("id", productId);
			jsonRequest.put("extras", extrasObject);

			logger.debug("Request param is going to be " + jsonRequest);

			StringEntity params = new StringEntity(jsonRequest.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to patch the product. response status line from "
						+ apiRequestUrl + " was: " + response.getStatusLine());
			}

			return true;

		}catch(Exception e){
			logger.error("Failed to patch the product ", e);
		}

		return false;
	}


	@Override
	public boolean removeCustomField(String productId, String key,
			String value, String apiKey) {

		// checks
		checkNotNull(productId);
		checkNotNull(apiKey);
		checkNotNull(value);
		checkNotNull(key);

		logger.info("Going to change product with id " + productId +"."
				+ " Request comes from user with key " + apiKey.substring(0, 5) + "****************");

		logger.info("The couple key/value to remove from custom fields is  [" + key + "," + value +"]");

		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
		List<CkanPair> extras = client.getDataset(productId).getExtras();

		Iterator<CkanPair> iterator = extras.iterator();
		while (iterator.hasNext()) {
			CkanPair ckanPair = iterator.next();

			if(ckanPair.getKey().equals(key) && ckanPair.getValue().equals(value)){
				logger.info("Removed from the ckan pairs list");
				iterator.remove();
				break;
			}

		}

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/package_patch";
			HttpPost httpPostRequest = new HttpPost(apiRequestUrl);
			httpPostRequest.setHeader("Authorization", apiKey);

			// Request parameters to be replaced
			JSONObject jsonRequest = new JSONObject();

			// build the json array for the "extras" field.. each object looks like {"key": ..., "value": ...}
			JSONArray extrasObject = new JSONArray();

			for(CkanPair extra: extras){
				JSONObject obj = new JSONObject();
				obj.put("value", extra.getValue());
				obj.put("key", extra.getKey());
				extrasObject.add(obj);
			}

			// perform the request
			jsonRequest.put("id", productId);
			jsonRequest.put("extras", extrasObject);

			logger.debug("Request param is going to be " + jsonRequest);

			StringEntity params = new StringEntity(jsonRequest.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to patch the product. response status line from "
						+ apiRequestUrl + " was: " + response.getStatusLine());
			}

			return true;

		}catch(Exception e){
			logger.error("Failed to remove the custom field for this product ", e);
		}

		return false;

	}

	@Override
	public boolean removeTag(String productId, String apiKey, String tagToRemove) {

		// checks
		checkNotNull(productId);
		checkNotNull(apiKey);
		checkNotNull(tagToRemove);

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/package_patch";
			HttpPost httpPostRequest = new HttpPost(apiRequestUrl);
			httpPostRequest.setHeader("Authorization", apiKey);

			// Request parameters to be replaced
			JSONObject jsonRequest = new JSONObject();
			JSONArray tagsAsJson = new JSONArray();

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			CkanDataset dataset = client.getDataset(productId);
			List<CkanTag> currentTags = dataset.getTags();

			// build the json array for the "tags" field.. each object looks like
			//			{
			//
			//			    "vocabulary_id": null,
			//			    "state": "active",
			//			    "display_name": "....",
			//			    "id": "....",
			//			    "name": "..."
			//
			//			}

			for(CkanTag ckanTag : currentTags){
				if(!ckanTag.getName().equals(tagToRemove)){
					JSONObject obj = new JSONObject();
					obj.put("vocabulary_id", ckanTag.getVocabularyId());
					obj.put("state", ckanTag.getState().toString());
					obj.put("display_name", ckanTag.getDisplayName());
					obj.put("id", ckanTag.getId());
					obj.put("name", ckanTag.getName());
					tagsAsJson.add(obj);
				}
			}

			// perform the request
			jsonRequest.put("id", productId);
			jsonRequest.put("tags", tagsAsJson);

			logger.debug("Request param is going to be " + jsonRequest);

			StringEntity params = new StringEntity(jsonRequest.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to patch the product. response status line from "
						+ apiRequestUrl + " was: " + response.getStatusLine());
			}

			return true;

		}catch(Exception e){
			logger.error("Failed to remove the tag " + tagToRemove, e);
		}

		return false;
	}

	@Override
	public boolean addTag(String productId, String apiKey, String tagToAdd) {

		// checks
		checkNotNull(productId);
		checkNotNull(apiKey);
		checkNotNull(tagToAdd);

		try{

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			CkanDataset dataset = client.getDataset(productId);
			List<CkanTag> currentTags = dataset.getTags();

			Iterator<CkanTag> tagsIterator = currentTags.iterator();

			boolean added = true;

			// check if it is already there ...
			while (tagsIterator.hasNext()) {
				CkanTag ckanTag = tagsIterator.next();
				if(ckanTag.getName().equals(tagToAdd)){
					added = false;
					break;
				}
			}


			if(added){
				currentTags.add(new CkanTag(tagToAdd));
				dataset.setTags(currentTags);
				client.patchUpdateDataset(dataset);
			}

			return true;
		}catch(Exception e){
			logger.error("Failed to add the tag " + tagToAdd, e);
		}

		return false;
	}

	@Override
	public List<CkanGroup> getParentGroups(String groupName, String apiKey) {
		// checks
		checkNotNull(groupName);
		checkNotNull(apiKey);

		try{
			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);
			return client.getGroup(groupName).getGroups();
		}catch(Exception e){
			logger.error("Something went wrong, returning null", e);
		}

		return null;
	}


	@Override
	public boolean setGroupParent(String parentName, String groupName) {

		// checks
		checkNotNull(parentName);
		checkNotNull(groupName);

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/group_patch";
			HttpPost httpPostRequest = new HttpPost(apiRequestUrl);
			httpPostRequest.setHeader("Authorization", CKAN_TOKEN_SYS);

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			List<CkanGroup> alreadyAvailableParents = client.getGroup(groupName).getGroups();

			Set<String> parentsNames = new HashSet<String>();
			parentsNames.add(parentName);

			for (CkanGroup alreadyAvailableParent : alreadyAvailableParents) {
				parentsNames.add(alreadyAvailableParent.getName());
			}

			logger.info("Setting as parents of group " + groupName + " :" + parentsNames);

			// Request parameters to be replaced
			JSONObject jsonRequest = new JSONObject();
			JSONArray parentsAsJson = new JSONArray();

			for(String parent : parentsNames){
				JSONObject obj = new JSONObject();
				obj.put("name", parent);
				parentsAsJson.add(obj);
			}

			// perform the request
			jsonRequest.put("id", groupName);
			jsonRequest.put("groups", parentsAsJson);

			logger.debug("Request param is going to be " + jsonRequest);

			StringEntity params = new StringEntity(jsonRequest.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to patch the group. response status line from "
						+ apiRequestUrl + " was: " + response.getStatusLine());
			}

			return true;

		}catch(Exception e){
			logger.error("Failed to set parents groups", e);
		}

		return false;
	}

	@Override
	public boolean isDatasetInGroup(String groupName, String datasetId) {

		// checks
		checkNotNull(datasetId);
		checkNotNull(groupName);

		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
		List<CkanGroup> groups = client.getDataset(datasetId).getGroups();

		for (CkanGroup ckanGroup : groups) {
			if(ckanGroup.getName().equals(groupName))
				return true;
		}

		return false;
	}

	@Override
	public List<CkanDataset> getProductsInGroup(String groupName) {

		checkNotNull(groupName);

		List<CkanDataset> toReturn = null;

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			String apiRequestUrl = CKAN_CATALOGUE_URL + "/api/3/action/group_show" + "?id=" + groupName + "&include_datasets=true";
			HttpGet httpGetRequest = new HttpGet(apiRequestUrl);
			httpGetRequest.setHeader("Authorization", CKAN_TOKEN_SYS);
			HttpResponse response = httpClient.execute(httpGetRequest);
			String jsonString = EntityUtils.toString(response.getEntity());
			JSONParser parser = new JSONParser();
			JSONObject parsedJson = (JSONObject)parser.parse(jsonString);
			logger.debug("JSONObject looks like " + parsedJson);

			// get "packages" array
			toReturn = new ArrayList<CkanDataset>();
			JSONObject result = (JSONObject)parsedJson.get("result");
			JSONArray packages = (JSONArray)result.get("packages");

			logger.debug("Packages looks like " + packages);

			for (int i = 0, size = packages.size(); i < size; i++){
				JSONObject objectInArray = (JSONObject)packages.get(i);
				String packageId = (String)objectInArray.get("id");
				toReturn.add(client.getDataset(packageId));
			}

		}catch(Exception e){
			logger.error("Failed to get groups information", e);
		}

		return  toReturn;

	}

	@Override
	public String patchProductWithJSON(String productId, JSONObject jsonRequest,
			String apiKey) {

		checkNotNull(productId);
		checkNotNull(jsonRequest);
		checkNotNull(apiKey);

		logger.info("Request of patching product " + productId + " with json " + jsonRequest);

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			String pathUpdatePatch = getCatalogueUrl() + "/api/3/action/package_patch";
			HttpPost httpPostRequest = new HttpPost(pathUpdatePatch);
			httpPostRequest.setHeader("Authorization", CKAN_TOKEN_SYS);

			// be sure the id of the item is there
			jsonRequest.put("id", productId);
			StringEntity params = new StringEntity(jsonRequest.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to patch the product. response status line from "
						+ pathUpdatePatch + " was: " + response.getStatusLine());
			}

			return null;

		}catch(Exception e){
			logger.error("Error while trying to patch grsf record " + e.getMessage());
			return e.getMessage();
		}
	}

	@Override
	public List<String> getProductsIdsInGroupOrOrg(String orgOrGroupName, boolean isOrganization, int start, int rows) throws ClassNotFoundException, SQLException {

		List<String> toReturn = new ArrayList<String>();

		checkNotNull(orgOrGroupName);
		checkArgument(start >= 0);
		checkArgument(rows >= 0);

		Connection connection = getConnection();
		try{

			ResultSet rs;
			if(isOrganization){

				String joinQuery = "SELECT \"package\".\"id\" AS \"dataset_id\" FROM \"package\" INNER JOIN \"group\" ON"
						+ " \"package\".\"owner_org\"=\"group\".\"id\" WHERE \"group\".\"name\"=? "
						+ "AND \"group\".\"is_organization\"=? AND \"package\".\"type\"=? AND \"package\".\"state\"=? LIMIT ? OFFSET ?; ";

				PreparedStatement preparedStatement = connection.prepareStatement(joinQuery);
				preparedStatement.setString(1, orgOrGroupName);
				preparedStatement.setBoolean(2, isOrganization);
				preparedStatement.setString(3, "dataset");
				preparedStatement.setString(4, "active");
				preparedStatement.setBigDecimal(5, new BigDecimal(rows));
				preparedStatement.setBigDecimal(6, new BigDecimal(start));

				rs = preparedStatement.executeQuery();

			}else{

				/**
				 * Inner join between the member table and the package table.
				 * Basically every time a dataset is added to a group, a new row is added to the table
				 * where table_id is the package_id, and group_id is the owner group identifier (not the name)
				 */
				String groupId = client.getGroup(orgOrGroupName).getId();
				String joinQuery = "SELECT \"table_id\" AS \"dataset_id\" FROM \"package\" INNER JOIN \"member\" ON"
						+ " \"member\".\"table_id\"=\"package\".\"id\" WHERE \"group_id\"=? "
						+ "AND \"member\".\"state\"=? LIMIT ? OFFSET ?;";

				PreparedStatement preparedStatement = connection.prepareStatement(joinQuery);
				preparedStatement.setString(1, groupId);
				preparedStatement.setString(2, "active");
				preparedStatement.setBigDecimal(3, new BigDecimal(rows));
				preparedStatement.setBigDecimal(4, new BigDecimal(start));

				rs = preparedStatement.executeQuery();

			}

			while (rs.next()) {
				toReturn.add(rs.getString("dataset_id"));
			}

		}catch(Exception e){
			logger.error("Failed to retrieve the ids of products in group/org. Error is " + e.getMessage());
			return null;
		}finally{
			closeConnection(connection);
		}
		return toReturn;


	}

	@Override
	public boolean deleteGroup(String groupName, boolean purge) {

		checkNotNull(groupName);

		logger.info("Request of deleting group " + groupName + ". Purge is " + Boolean.toString(purge));

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			String deletePath = getCatalogueUrl() + "/api/3/action/group_delete";
			String purgePath = getCatalogueUrl() + "/api/3/action/group_purge";

			String requestToPerform = purge ? purgePath : deletePath;

			HttpPost httpPostRequest = new HttpPost(requestToPerform);
			httpPostRequest.setHeader("Authorization", CKAN_TOKEN_SYS);

			JSONObject obj = new JSONObject();
			obj.put("id", groupName);

			StringEntity params = new StringEntity(obj.toJSONString(), ContentType.APPLICATION_JSON);
			httpPostRequest.setEntity(params);

			HttpResponse response = httpClient.execute(httpPostRequest);

			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("failed to delete/purge the group. response status line from "
						+ requestToPerform + " was: " + response.getStatusLine());
			}

			return true;

		}catch(Exception e){
			logger.error("Error while trying to delete/purge the group " + e.getMessage());
			return false;
		}
	}

	@Override
	public CkanOrganization getOrganizationByName(String name) {

		checkNotNull(name);

		String ckanName = name.toLowerCase();
		try{

			return client.getOrganization(ckanName);

		}catch(Exception e){
			logger.error("Failed to retrieve the organization with name" + name, e);
		}
		return null;
	}

	@Override
	public Map<String, Map<CkanGroup, RolesCkanGroupOrOrg>> getUserRoleByGroup(
			String username, String apiKey) {

		checkNotNull(username);
		checkNotNull(apiKey);

		Map<String, Map<CkanGroup, RolesCkanGroupOrOrg>> toReturn = new HashMap<String, Map<CkanGroup,RolesCkanGroupOrOrg>>();

		try{

			String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);
			Map<CkanGroup, RolesCkanGroupOrOrg> partialResult = getGroupsByUserFromDB(ckanUsername);

			Set<Entry<CkanGroup, RolesCkanGroupOrOrg>> set = partialResult.entrySet();
			for (Entry<CkanGroup, RolesCkanGroupOrOrg> entry : set) {

				String groupName = entry.getKey().getName();
				HashMap<CkanGroup, RolesCkanGroupOrOrg> subMap = new HashMap<CkanGroup, RolesCkanGroupOrOrg>();
				subMap.put(entry.getKey(), entry.getValue());
				toReturn.put(groupName, subMap);

			}

			logger.debug("Returning map " + toReturn);
		}catch(Exception e){
			logger.error("Failed to retrieve roles of user in his/her own groups",e);
		}

		return toReturn;
	}

	@Override
	public Map<String, Map<CkanOrganization, RolesCkanGroupOrOrg>> getUserRoleByOrganization(
			String username, String apiKey) {

		checkNotNull(username);
		checkNotNull(apiKey);

		Map<String, Map<CkanOrganization, RolesCkanGroupOrOrg>> toReturn = new HashMap<String, Map<CkanOrganization,RolesCkanGroupOrOrg>>();

		try{

			String ckanUsername = CatalogueUtilMethods.fromUsernameToCKanUsername(username);
			Map<CkanOrganization, RolesCkanGroupOrOrg> partialResult = getOrganizationsByUserFromDB(ckanUsername);

			Set<Entry<CkanOrganization, RolesCkanGroupOrOrg>> set = partialResult.entrySet();
			for (Entry<CkanOrganization, RolesCkanGroupOrOrg> entry : set) {

				String groupName = entry.getKey().getName();
				HashMap<CkanOrganization, RolesCkanGroupOrOrg> subMap = new HashMap<CkanOrganization, RolesCkanGroupOrOrg>();
				subMap.put(entry.getKey(), entry.getValue());
				toReturn.put(groupName, subMap);

			}

			logger.debug("Returning map " + toReturn);
		}catch(Exception e){
			logger.error("Failed to retrieve roles of user in his/her own groups",e);
		}

		return toReturn;
	}

	/**
	 * Retrieve the list of groups(plus capacities) the user belongs by querying directly the database.
	 * @param username
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private Map<CkanGroup, RolesCkanGroupOrOrg> getGroupsByUserFromDB(String username){
		checkNotNull(username);

		Map<CkanGroup, RolesCkanGroupOrOrg> toReturn = new HashMap<CkanGroup, RolesCkanGroupOrOrg>();
		Connection connection = null;
		try{

			connection = getConnection();
			ResultSet rs;

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			String userId = client.getUser(username).getId();
			String joinQuery = "SELECT \"group_id\",\"capacity\" FROM \"public\".\"member\" "
					+ "JOIN \"public\".\"group\" ON \"member\".\"group_id\" = \"group\".\"id\" where \"table_id\"=?"
					+ " and \"table_name\"='user' and \"member\".\"state\"='active' and \"group\".\"state\"='active' and \"group\".\"is_organization\"=?;";


			PreparedStatement preparedStatement = connection.prepareStatement(joinQuery);
			preparedStatement.setString(1, userId);
			preparedStatement.setBoolean(2, false);
			rs = preparedStatement.executeQuery();

			while (rs.next()) {

				toReturn.put(client.getGroup(rs.getString("group_id")), RolesCkanGroupOrOrg.convertFromCapacity(rs.getString("capacity")));
			}

		}catch(Exception e){
			logger.error("Failed to retrieve the groups to whom the user belongs. Error is " + e.getMessage());
			return null;
		}finally{
			closeConnection(connection);
		}
		return toReturn;
	}

	/**
	 * Retrieve the list of  organizations(plus capacities) the user belongs by querying directly the database.
	 * @param username
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private Map<CkanOrganization, RolesCkanGroupOrOrg> getOrganizationsByUserFromDB(String username){
		checkNotNull(username);

		Map<CkanOrganization, RolesCkanGroupOrOrg> toReturn = new HashMap<CkanOrganization, RolesCkanGroupOrOrg>();
		Connection connection = null;
		try{

			connection = getConnection();
			ResultSet rs;

			ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			String userId = client.getUser(username).getId();
			String joinQuery = "SELECT \"group_id\",\"capacity\" FROM \"public\".\"member\" "
					+ "JOIN \"public\".\"group\" ON \"member\".\"group_id\" = \"group\".\"id\" where \"table_id\"=?"
					+ " and \"table_name\"='user' and \"member\".\"state\"='active' and \"group\".\"state\"='active' and \"group\".\"is_organization\"=?;";


			PreparedStatement preparedStatement = connection.prepareStatement(joinQuery);
			preparedStatement.setString(1, userId);
			preparedStatement.setBoolean(2, true);
			rs = preparedStatement.executeQuery();

			while (rs.next()) {

				toReturn.put(client.getOrganization(rs.getString("group_id")), RolesCkanGroupOrOrg.convertFromCapacity(rs.getString("capacity")));
			}

		}catch(Exception e){
			logger.error("Failed to retrieve the groups to whom the user belongs. Error is " + e.getMessage());
			return null;
		}finally{
			closeConnection(connection);
		}
		return toReturn;
	}

	@Override
	public CkanGroup getGroupByName(String name) {
		String ckanName = CatalogueUtilMethods.fromGroupTitleToName(name);
		try{
			return client.getGroup(ckanName);
		}catch(Exception e){
			logger.error("Failed to retrieve the group with name" + name, e);
		}
		return null;
	}

	@Override
	public boolean isNotificationToUsersEnabled() {
		return ALERT_USERS_ON_POST_CREATION;
	}

	@Override
	public void assignRolesOtherOrganization(String username,
			String sourceOrganization, RolesCkanGroupOrOrg currentRole) {

		checkNotNull(username);
		checkNotNull(sourceOrganization);
		checkNotNull(currentRole);

		logger.info("Request for assigning other roles for user " + username + ", whose current role is " + currentRole + " and organization " + sourceOrganization);

		Iterator<Entry<String, String>> iterator = extendRoleInOrganization.entrySet().iterator();
		logger.debug("List of entries to check is " + extendRoleInOrganization);
		while (iterator.hasNext()) {
			Map.Entry<java.lang.String, java.lang.String> entry = iterator
					.next();

			String sourceOrg = entry.getKey();

			logger.debug("Found organization source " + sourceOrg);
			if(sourceOrg.equals(sourceOrganization)){

				String[] values = entry.getValue().split(DataCatalogueRunningCluster.TUPLES_SEPARATOR);

				for(int i = 0; i < values.length; i++){

					String destOrg = values[i].split("\\"+DataCatalogueRunningCluster.ROLE_ORGANIZATION_SEPARATOR)[0];
					String role = values[i].split("\\"+DataCatalogueRunningCluster.ROLE_ORGANIZATION_SEPARATOR)[1];

					logger.debug("Role is " + role + " and organization is " + destOrg);
					RolesCkanGroupOrOrg ckanRole;
					if(role.equals(DataCatalogueRunningCluster.CKAN_GENERIC_ROLE))
						ckanRole = currentRole;
					else
						ckanRole = RolesCkanGroupOrOrg.convertFromCapacity(role);

					logger.info("Checking for extra role: role is " + ckanRole + " and organization is " + destOrg);
					checkRoleIntoOrganization(username, destOrg, ckanRole);
				}
			}

		}

	}

	@Override
	public List<CkanDataset> searchForPackage(String apiKey, String query, int start, int offset)  throws Exception{

		checkNotNull(apiKey);
		checkNotNull(query);
		checkArgument(start >= 0);
		checkArgument(offset >= 0);

		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);

		try{

			CkanQuery queryCkan = CkanQuery.filter().byText(query);
			return client.searchDatasets(queryCkan, offset, start).getResults();

		}catch(Exception e){
			logger.error("Error while executing query", e);
			throw e;
		}
	}

	@Override
	public List<CkanDataset> searchForPackageInOrganization(String apiKey, String query, int start, int offset, String organization) throws Exception{

		checkNotNull(apiKey);
		checkNotNull(query);
		checkArgument(start >= 0);
		checkArgument(offset >= 0);

		ExtendCkanClient client = new ExtendCkanClient(CKAN_CATALOGUE_URL, apiKey);

		try{

			CkanQuery queryCkan = CkanQuery.filter().byOrganizationName(organization.toLowerCase()).byText(query);
			return client.searchDatasets(queryCkan, offset, start).getResults();

		}catch(Exception e){
			logger.error("Error while executing query", e);
			throw e;
		}
	}

	@Override
	public Statistics getStatistics() throws Exception {
		Statistics stats = new Statistics();

		int numGroups = getGroups().size();
		int numOrganizations = getOrganizationsNames().size();

		logger.debug("SOLR address is " + SOLR_URL);
		HttpSolrServer solr = new HttpSolrServer(SOLR_URL);

		// ask solr for the current counter of the dataset -> dataset_type:"dataset" AND state:"active"
		SolrQuery queryNumItems = new SolrQuery();
		queryNumItems.setRows(0); // do not require data
		queryNumItems.setQuery("dataset_type:\"dataset\" AND state:\"active\"");
		long numItems;
		try{
			QueryResponse response = solr.query(queryNumItems);
			numItems = response.getResults().getNumFound();
		}catch(Exception e){
			logger.error("Failed to retrieve the number of items", e);
			throw e;
		}

		// ask solr for types
		SolrQuery queryNumTypes = new SolrQuery("dataset_type:\"dataset\" AND state:\"active\"");
		queryNumTypes.addFacetField("systemtype");
		queryNumItems.setRows(0); // do not require data
		queryNumTypes.setFacet(true);
		long numTypes = 0;
		try{
			QueryResponse response = solr.query(queryNumTypes);
			Set<String> notEmptyTypes = new HashSet<String>();
			List<FacetField> facet = response.getFacetFields();
			for (FacetField facetField : facet) {
				List<Count> values = facetField.getValues(); // the different types, even the ones with zero datasets associated
				for (Count count : values) {
					if(count.getCount() > 0)
						notEmptyTypes.add(count.getName());
				}
			}
			numTypes = notEmptyTypes.size();
		}catch(Exception e){
			logger.error("Failed to retrieve the number of types", e);
			throw e;
		}

		// build the urls
		stats.setNumGroups(numGroups);
		stats.setNumItems(numItems);
		stats.setNumOrganizations(numOrganizations);
		stats.setNumTypes(numTypes);

		return stats;
	}

	@Override
	public LandingPages getLandingPages() throws Exception {

		LandingPages landingPages = new LandingPages();
		landingPages.setUrlGroups(PORTLET_URL_FOR_SCOPE + "?path=/group/");
		landingPages.setUrlItems(PORTLET_URL_FOR_SCOPE + "?path=/dataset/");
		landingPages.setUrlOrganizations(PORTLET_URL_FOR_SCOPE + "?path=/organization/");
		landingPages.setUrlTypes(PORTLET_URL_FOR_SCOPE + "?path=/type/");
		return landingPages;
	}

	@Override
	public String getCatalogueEmail() {

		return CKAN_EMAIL;

	}

}