package org.gcube.datacatalogue.ckanutillibrary;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.gcube.datacatalogue.ckanutillibrary.models.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.models.CkanDatasetRelationship;
import org.gcube.datacatalogue.ckanutillibrary.models.DatasetRelationships;
import org.gcube.datacatalogue.ckanutillibrary.models.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg;
import org.gcube.datacatalogue.ckanutillibrary.models.State;
import org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpStatus;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpGet;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
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
public class DataCatalogueImpl implements DataCatalogue{

	private static final Logger logger = LoggerFactory.getLogger(DataCatalogueImpl.class);

	private String CKAN_CATALOGUE_URL;
	private String CKAN_DB_NAME;
	private String CKAN_DB_USER;
	private String CKAN_DB_PASSWORD;
	private String CKAN_DB_URL;
	private Integer CKAN_DB_PORT;
	private String PORTLET_URL_FOR_SCOPE;
	private String CKAN_TOKEN_SYS;

	// ckan client
	private CkanClient client;

	// hashmap for ckan api keys
	private ConcurrentHashMap<String, CKANTokenBean> apiKeysMap;

	// apikey bean expires after X minutes in the above map
	private static final int EXPIRE_KEY_TIME = 5 * 60 * 1000;

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
		CKAN_DB_PORT = runningInstance.getDatabasePorts().get(0);
		CKAN_CATALOGUE_URL = runningInstance.getDataCatalogueUrl().get(0).trim();
		PORTLET_URL_FOR_SCOPE = runningInstance.getPortletUrl().trim();

		logger.debug("Plain sys admin token first 3 chars are " + CKAN_TOKEN_SYS.substring(0, 3));
		logger.debug("Plain db password first 3 chars are " + CKAN_DB_PASSWORD.substring(0, 3));

		// build the client
		client = new CkanClient(CKAN_CATALOGUE_URL);

		// init map
		apiKeysMap = new ConcurrentHashMap<String, CKANTokenBean>();

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
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

		// check in the hashmap first
		if(apiKeysMap.containsKey(ckanUsername)){
			CKANTokenBean bean = apiKeysMap.get(ckanUsername);
			long currentTime = System.currentTimeMillis();
			if(bean.timestamp + EXPIRE_KEY_TIME < currentTime) // it's still ok
				return bean.apiKey;
		}

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

		}catch(Exception e){
			logger.error("Unable to retrieve key for user " + ckanUsername, e);
		}finally{
			closeConnection(connection);
		}

		return apiToReturn;
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
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

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
			String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

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

						if(rolesToMatch.contains(RolesCkanGroupOrOrg.valueOf(capacity.toUpperCase()))){
							RolesCkanGroupOrOrg enumRole = RolesCkanGroupOrOrg.valueOf(capacity.toUpperCase());
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
	public String getCatalogueUrl() {
		return CKAN_CATALOGUE_URL;
	}


	@Override
	public String getPortletUrl() {
		return PORTLET_URL_FOR_SCOPE;
	}

	@Override
	public List<String> getOrganizationsNamesByUser(String username) {

		logger.debug("Requested organizations for user " + username);

		// checks
		checkNotNull(username);

		// in order to avoid errors, the username is always converted
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

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
	 * Set dataset private
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

		String pathSetPrivate = "/api/3/action/bulk_update_private";
		String pathSetPublic = "/api/3/action/bulk_update_public";

		// Request parameters to be replaced
		String parameter = "{"
				+ "\"org_id\":\"ORGANIZATION_ID\","
				+ "\"datasets\":[\"DATASET_ID\"]"
				+ "}";

		// replace with right data
		parameter = parameter.replace("ORGANIZATION_ID", organizationId);
		parameter = parameter.replace("DATASET_ID", datasetId);

		if(priv){
			try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
				HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + pathSetPrivate);
				request.addHeader("Authorization", apiKey);
				StringEntity params = new StringEntity(parameter);
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);
				logger.debug("[PRIVATE]Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					return true;

			}catch (Exception ex) {
				logger.error("Error while trying to set private the dataset ", ex);
			}
		}else
		{
			try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
				HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + pathSetPublic);
				StringEntity params = new StringEntity(parameter);
				request.addHeader("Authorization", apiKey);
				request.setEntity(params);
				HttpResponse response = httpClient.execute(request);
				logger.debug("[PUBLIC]Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					return true;

			}catch (Exception ex) {
				logger.error("Error while trying to set public the dataset ", ex);
			}
		}

		return false;
	}

	@Override
	public String addResourceToDataset(ResourceBean resourceBean, String apiKey) {

		logger.debug("Request to add a resource described by this bean " + resourceBean);

		// checks
		checkNotNull(resourceBean);
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		try{
			if(UtilMethods.resourceExists(resourceBean.getUrl())){

				// in order to avoid errors, the username is always converted
				String ckanUsername = UtilMethods.fromUsernameToCKanUsername(resourceBean.getOwner());

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
				CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);
				CkanResource createdRes = client.createResource(resource);

				if(createdRes != null){

					logger.debug("Resource  " + createdRes.getName() + " is now available");

					return createdRes.getId();

				}
			}else
				logger.error("There is no resource at this url " + resourceBean.getUrl());
		}catch(Exception e){
			logger.error("Unable to create the resource described by the bean " + resourceBean, e);
		}
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

			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);
			client.deleteResource(resourceId);

			return true;

		}catch(Exception e){
			logger.error("Unable to delete resource whose id is " + resourceId, e);
		}

		return false;
	}

	@Override
	public String createCKanDataset(String apiKey,
			String title, String organizationNameOrId, String author,
			String authorMail, String maintainer, String maintainerMail,
			long version, String description, String licenseId,
			List<String> tags, Map<String, String> customFields,
			List<ResourceBean> resources, boolean setPublic) {

		// checks (minimum)
		checkNotNull(apiKey);
		checkNotNull(title);
		checkNotNull(organizationNameOrId);
		checkArgument(!apiKey.isEmpty());
		checkArgument(!title.isEmpty());
		checkArgument(!organizationNameOrId.isEmpty());

		logger.debug("Request for dataset creation");

		CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);

		// get client from apiKey
		String ckanUsername = getUserFromApiKey(apiKey).getName();

		// create the base dataset and fill it
		CkanDataset dataset = new CkanDataset();

		// get the name from the title
		String name = UtilMethods.fromProductTitleToName(title);
		logger.debug("Name of the dataset is going to be " + name);
		dataset.setName(name);
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

			logger.debug("Description (escaped is ) " + htmlRend.toString());
		}

		dataset.setLicenseId(licenseId);

		// set the tags, if any
		if(tags != null && !tags.isEmpty()){

			// convert to ckan tags
			List<CkanTag> ckanTags = new ArrayList<CkanTag>(tags.size());
			for (String stringTag : tags) {
				ckanTags.add(new CkanTag(stringTag));
			}

			dataset.setTags(ckanTags);
		}

		// set the custom fields, if any
		if(customFields != null && !customFields.isEmpty()){

			// iterate and create 
			Iterator<Entry<String, String>> iterator = customFields.entrySet().iterator();

			List<CkanPair> extras = new ArrayList<CkanPair>(customFields.entrySet().size());

			while (iterator.hasNext()) {

				Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
				extras.add(new CkanPair(entry.getKey(), entry.getValue()));

			}

			dataset.setExtras(extras);
		}

		// check if we need to add the resources
		if(resources != null && !resources.isEmpty()){

			logger.debug("We need to add resources to the dataset");

			try{

				List<CkanResource> resourcesCkan = new ArrayList<CkanResource>();


				for(ResourceBean resource: resources){

					CkanResource newResource = new CkanResource();
					newResource.setDescription(resource.getDescription());
					newResource.setId(resource.getId());
					newResource.setUrl(resource.getUrl());
					newResource.setName(resource.getName());
					newResource.setMimetype(resource.getMimeType());
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
		try{

			res = client.createDataset(dataset);

			if(res != null){

				logger.debug("Dataset with name " + res.getName() + " has been created. Setting visibility");

				// set visibility
				boolean visibilitySet = setDatasetPrivate(
						!setPublic, // swap to private 
						res.getOrganization().getId(), 
						res.getId(), 
						CKAN_TOKEN_SYS); // use sysadmin api key to be sure it will be set

				logger.debug("Was visibility set to " + (setPublic ? "public" : "private") + "? " + visibilitySet);

				return res.getId();
			}


		}catch(Exception e){

			// try to update
			logger.error("Error while creating the dataset.", e);

		}

		return null;
	}

	@Override
	public String getUrlFromDatasetIdOrName(String apiKey, String datasetIdOrName, boolean withoutHost) {

		logger.debug("Request coming for dataset url of dataset with name/id " + datasetIdOrName);

		// checks
		checkNotNull(apiKey);
		checkNotNull(datasetIdOrName);
		checkArgument(!apiKey.isEmpty());
		checkArgument(!datasetIdOrName.isEmpty());

		// the url of the dataset looks like "getCatalogueUrl() + /dataset/  + dataset name"  
		try{

			// get the dataset from name
			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);
			CkanDataset dataset = client.getDataset(datasetIdOrName);

			if(dataset != null){

				if(withoutHost)
					return "/dataset/" + dataset.getName();
				else
					return CKAN_CATALOGUE_URL + "/dataset/" + dataset.getName();
			}
		}catch(Exception e){
			logger.error("Error while retrieving dataset with id/name=" + datasetIdOrName, e);
		}
		return null;
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
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

		// check if this role is already present in ckan for this user within the organization
		String organizationNameToCheck = organizationName.toLowerCase();

		try{
			boolean alreadyPresent = isRoleAlreadySet(ckanUsername, organizationNameToCheck, correspondentRoleToCheck, false);

			if(alreadyPresent)
				return true; // just return
			else{

				// we need to use the apis to make it
				String path = "/api/3/action/organization_member_create";

				// Request parameters to be replaced
				String parameter = "{"
						+ "\"id\":\"ORGANIZATION_ID_NAME\","
						+ "\"username\":\"USERNAME_ID_NAME\","
						+ "\"role\":\"ROLE\""
						+ "}";

				// replace those values
				parameter = parameter.replace("ORGANIZATION_ID_NAME", organizationNameToCheck);
				parameter = parameter.replace("USERNAME_ID_NAME", ckanUsername);
				parameter = parameter.replace("ROLE", correspondentRoleToCheck.toString().toLowerCase());

				logger.debug("API request for organization membership is going to be " + parameter);

				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
					HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
					request.addHeader("Authorization", CKAN_TOKEN_SYS); // sys token
					StringEntity params = new StringEntity(parameter);
					request.setEntity(params);
					HttpResponse response = httpClient.execute(request);
					logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

					return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);

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
				if(ckanUser.getCapacity().equals(correspondentRoleToCheck.toString().toLowerCase()))
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
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

		// check if this role is already present in ckan for this user within the group
		String groupNameToCheck = UtilMethods.fromGroupTitleToName(groupName);

		try{
			boolean alreadyPresent = isRoleAlreadySet(ckanUsername, groupNameToCheck, correspondentRoleToCheck, true);

			if(alreadyPresent)
				return true; // just return
			else{

				// we need to use the apis to make it
				String path = "/api/3/action/group_member_create";

				// Request parameters to be replaced
				String parameter = "{"
						+ "\"id\":\"ORGANIZATION_ID_NAME\","
						+ "\"username\":\"USERNAME_ID_NAME\","
						+ "\"role\":\"ROLE\""
						+ "}";

				// replace those values
				parameter = parameter.replace("ORGANIZATION_ID_NAME", groupNameToCheck);
				parameter = parameter.replace("USERNAME_ID_NAME", ckanUsername);
				parameter = parameter.replace("ROLE", correspondentRoleToCheck.toString().toLowerCase());

				logger.debug("API request for organization membership is going to be " + parameter);

				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();) {
					HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
					request.addHeader("Authorization", CKAN_TOKEN_SYS); // sys token
					StringEntity params = new StringEntity(parameter);
					request.setEntity(params);
					HttpResponse response = httpClient.execute(request);
					logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

					return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);

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
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);
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

			// Request parameters to be replaced
			String parameter = "{"
					+ "\"subject\":\"SUBJECT\","
					+ "\"object\":\"OBJECT\","
					+ "\"type\":\"RELATIONSHIP\","
					+ "\"comment\" : \"COMMENT\""
					+ "}";

			// replace those values
			parameter = parameter.replace("SUBJECT", datasetIdSubject);
			parameter = parameter.replace("OBJECT", datasetIdObject);
			parameter = parameter.replace("RELATIONSHIP", relation.toString());
			if(relationComment != null && !relationComment.isEmpty())
				parameter = parameter.replace("COMMENT", relationComment);

			logger.debug("API request for relationship create is going to be " + parameter);

			HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
			request.addHeader("Authorization", apiKey);
			StringEntity params = new StringEntity(parameter);
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);

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

			// Request parameters to be replaced
			String parameter = "{"
					+ "\"subject\":\"SUBJECT\","
					+ "\"object\":\"OBJECT\","
					+ "\"type\":\"RELATIONSHIP\""
					+ "}";

			// replace those values
			parameter = parameter.replace("SUBJECT", datasetIdSubject);
			parameter = parameter.replace("OBJECT", datasetIdObject);
			parameter = parameter.replace("RELATIONSHIP", relation.toString());

			logger.debug("API request for delete relationship is going to be " + parameter);

			HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
			request.addHeader("Authorization", apiKey);
			StringEntity params = new StringEntity(parameter);
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
		}catch(Exception e){
			logger.error("Failed to delete the relationship between dataset subject = "  + datasetIdSubject 
					+ " and " + " dataset subject " + datasetIdObject, e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
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

			String parameter;

			// Request parameters to be replaced
			if(datasetIdObject == null || datasetIdObject.isEmpty())
				parameter = "{"
						+ "\"id\":\"SUBJECT\""
						+ "}";
			else
				parameter = "{"
						+ "\"id\":\"SUBJECT\","
						+ "\"id2\":\"OBJECT\""
						+ "}";

			// replace those values
			parameter = parameter.replace("SUBJECT", datasetIdSubject);
			if(datasetIdObject != null && !datasetIdObject.isEmpty())
				parameter = parameter.replace("OBJECT", datasetIdObject);

			logger.debug("API request for getting relationship is going to be " + parameter);

			HttpPost request = new HttpPost(CKAN_CATALOGUE_URL + path);
			request.addHeader("Authorization", apiKey);
			StringEntity params = new StringEntity(parameter);
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			logger.debug("Response code is " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

				// parse the json and convert to java beans
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));

				String output;
				String res = "";
				while ((output = br.readLine()) != null) {
					res += output;
				}

				if(res == "")
					return toReturn;

				// parse the json object returned
				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(res);
				JSONArray resultJson = (JSONArray) json.get("result");
				Iterator<JSONObject> it = resultJson.iterator();

				while (it.hasNext()) {
					JSONObject object = (JSONObject) it.next();
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
			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
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
		CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);

		logger.debug("Request for creating group with name " + nameOrId + " title " + title + " and description " + description);

		String name = UtilMethods.fromGroupTitleToName(nameOrId);
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
	private CkanGroup groupExists(String nameOrId, CheckedCkanClient client){

		CkanGroup toReturn = null;

		try{

			toReturn = client.getGroup(nameOrId);

		}catch(JackanException je){
			logger.error("This group doesn't exist");
		}

		return toReturn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean assignDatasetToGroup(String groupNameOrId, String datasetNameOrId, String apiKey) {

		// checks
		checkNotNull(groupNameOrId);
		checkArgument(!groupNameOrId.isEmpty());
		checkNotNull(datasetNameOrId);
		checkArgument(!datasetNameOrId.isEmpty());
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		String groupNameToCheck = UtilMethods.fromGroupTitleToName(groupNameOrId);

		try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){

			// TODO unfortunately it doesn't work properly.. We use the APIs
			//			// the association is performed by using the sysadmi api key, since from apis only group admin can perform this operation...
			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, CKAN_TOKEN_SYS);
			//
			//			// retrieve the dataset
			//			CkanDataset dataset = client.getDataset(datasetNameOrId);
			//			List<CkanGroup> datasetGroups = dataset.getGroups();
			//			CkanGroup destinationGroup = client.getGroup(groupNameToCheck);
			//			datasetGroups.add(destinationGroup);
			//
			//			// patch the dataset
			//			client.patchUpdateDataset(dataset);

			// check the group exists
			CkanGroup group = client.getGroup(groupNameToCheck);

			// we need to use the apis to make it
			String pathPackageShow = CKAN_CATALOGUE_URL + "/api/3/action/package_show?id=" + datasetNameOrId;
			HttpGet getRequest = new HttpGet(pathPackageShow);
			getRequest.addHeader("Authorization", CKAN_TOKEN_SYS);
			HttpResponse response = httpClient.execute(getRequest);
			List<String> fetchedGroups = new ArrayList<String>();

			logger.debug("Response is " + response.getStatusLine().getStatusCode() + " and message is " + response.getStatusLine().getReasonPhrase());

			// read the json dataset and fetch the groups and fetch the groups' names, if any
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

				// parse the json and convert to java beans
				BufferedReader br = new BufferedReader(
						new InputStreamReader((response.getEntity().getContent())));

				String output;
				String res = "";
				while ((output = br.readLine()) != null) {
					res += output;
				}

				if(res == "")
					throw new Exception("Unable to retrieve the package!");

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(res);
				JSONObject resultJson =  (JSONObject) json.get("result");
				JSONArray groupsJson = (JSONArray)resultJson.get("groups");
				Iterator<JSONObject> it =  groupsJson.iterator();

				while (it.hasNext()) {
					JSONObject object = (JSONObject) it.next();
					try{
						if(object.containsKey("name"))
							fetchedGroups.add((String)object.get("name"));
					}catch(Exception e){
						logger.error("Error while building CkanRelationship bean from object " +  object, e);
					}
				}

				// add the new one
				if(!fetchedGroups.contains(group.getName()))
					fetchedGroups.add(group.getName());

				logger.debug("Groups to be added are " + fetchedGroups);

				// now we patch the dataset with the new group
				String pathUpdatePatch = CKAN_CATALOGUE_URL + "/api/3/action/package_patch";
				String parameterPostPatch = "{\"id\":\"PACKAGE_ID\", \"groups\":[GROUPS]}";
				parameterPostPatch = parameterPostPatch.replace("PACKAGE_ID", datasetNameOrId);
				String singleGroup = "{\"name\":\"GROUP_ID\"}";

				// evaluate parameterPostPatch
				String replaceGROUPS = "";
				for (int i = 0;  i < fetchedGroups.size(); i++) {
					replaceGROUPS += singleGroup.replace("GROUP_ID", fetchedGroups.get(i));
					if(i != fetchedGroups.size() - 1)
						replaceGROUPS += ",";
				}

				// replace this into parameterPostPatch
				parameterPostPatch = parameterPostPatch.replace("GROUPS", replaceGROUPS);

				logger.debug("Request for patch is going to be " + parameterPostPatch);

				HttpPost request = new HttpPost(pathUpdatePatch);
				request.addHeader("Authorization", CKAN_TOKEN_SYS);
				StringEntity params = new StringEntity(parameterPostPatch);
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
	public Map<String, List<String>> getRolesAndUsersGroup(String groupName) {

		// checks
		checkNotNull(groupName);
		checkArgument(!groupName.isEmpty());

		Map<String, List<String>> capacityAndUsers = new HashMap<String, List<String>>();
		CkanGroup org = client.getGroup(groupName);
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
	public String getRoleOfUserInOrganization(String username, String orgName, String apiKey) {

		String toReturn = null;

		String usernameCkan = UtilMethods.fromUsernameToCKanUsername(username);

		try{

			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);
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

		String usernameCkan = UtilMethods.fromUsernameToCKanUsername(username);

		try{

			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);
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

			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);
			client.deleteDataset(datasetId);
			logger.info("Dataset with id " + datasetId + " deleted!");

			if(purge){

				logger.debug("Purging also ....");
				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build();){
					String path = CKAN_CATALOGUE_URL + "/api/3/action/dataset_purge";
					HttpPost request = new HttpPost(path);
					request.addHeader("Authorization", CKAN_TOKEN_SYS); // this must be a sys_admin key
					String entityBody = "{\"id\": \"" + datasetId + "\"}";
					StringEntity params = new StringEntity(entityBody);
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
	public CkanDataset getDataset(String datasetId, String apiKey) {

		logger.info("Request ckan dataset with id " + datasetId);

		// checks
		checkNotNull(datasetId);
		checkArgument(!datasetId.isEmpty());
		checkNotNull(apiKey);
		checkArgument(!apiKey.isEmpty());

		try{
			CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);
			return client.getDataset(datasetId);
		}catch(Exception e){
			logger.error("Unable to retrieve such dataset, returning null ...", e);
		}

		return null;
	}

}
