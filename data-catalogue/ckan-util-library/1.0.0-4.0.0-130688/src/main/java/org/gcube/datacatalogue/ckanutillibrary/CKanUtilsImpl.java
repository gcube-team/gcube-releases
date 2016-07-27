package org.gcube.datacatalogue.ckanutillibrary;

import java.net.HttpURLConnection;
import java.net.URL;
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

import javax.net.ssl.HttpsURLConnection;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;

import org.gcube.datacatalogue.ckanutillibrary.models.CKanUserWrapper;
import org.gcube.datacatalogue.ckanutillibrary.models.ResourceBean;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesIntoOrganization;
import org.gcube.datacatalogue.ckanutillibrary.models.State;
import org.gcube.datacatalogue.ckanutillibrary.utils.UtilMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.CkanClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpStatus;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.model.CkanDataset;
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
public class CKanUtilsImpl implements CKanUtils{


	// TO BE REMOVED TODO
	public final static String PRODUCTION_SCOPE_ROOT = "/d4science.research-infrastructures.eu";
	public final static String PRODUCTION_CKAN_ORGNAME_ROOT = "d4science";
	public final static String PRODUCTION_LIFERAY_ORGNAME_ROOT = "d4science.research-infrastructures.eu";

	private static final Logger logger = LoggerFactory.getLogger(CKanUtilsImpl.class);

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

	/**
	 * The ckan catalogue url and database will be discovered in this scope
	 * @param scope
	 * @throws Exception if unable to find datacatalogue info
	 */
	public CKanUtilsImpl(String scope) throws Exception{

		CKanRunningCluster runningInstance = new CKanRunningCluster(scope);

		// save information
		CKAN_DB_URL = runningInstance.getDatabaseHosts().get(0);
		CKAN_DB_NAME = runningInstance.getDataBaseName();
		CKAN_DB_USER = runningInstance.getDataBaseUser();
		CKAN_DB_PASSWORD = runningInstance.getDataBasePassword();
		CKAN_TOKEN_SYS = runningInstance.getSysAdminToken();
		CKAN_DB_PORT = runningInstance.getDatabasePorts().get(0);
		CKAN_CATALOGUE_URL = runningInstance.getDataCatalogueUrl().get(0);
		PORTLET_URL_FOR_SCOPE = runningInstance.getPortletUrl();

		logger.debug("Plain sys admin token first 3 chars are " + CKAN_TOKEN_SYS.substring(0, 3));
		logger.debug("Plain db password first 3 chars are " + CKAN_DB_PASSWORD.substring(0, 3));

		// build the client
		client = new CkanClient(CKAN_CATALOGUE_URL);

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

		// in order to avoid errors, the username is always converted
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

		String apiToReturn = null;

		// the connection
		Connection connection = null;

		try{

			connection = getConnection();

			String query = "SELECT \"apikey\" FROM \"user\" WHERE \"name\"=? and \"state\"=?;";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, ckanUsername);
			preparedStatement.setString(2, State.ACTIVE.toString().toLowerCase());

			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				apiToReturn = rs.getString("apikey");
				break;
			}

			logger.debug("Api key retrieved for user " + ckanUsername);
		}catch(Exception e){
			logger.error("Unable to retrieve key for user " + ckanUsername, e);
		}finally{

			closeConnection(connection);

		}

		return apiToReturn;
	}

	@Override
	public CKanUserWrapper getUserFromApiKey(String apiKey) {
		logger.debug("Request user whose api key is = " + apiKey);
		CKanUserWrapper user = new CKanUserWrapper();

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
	public Map<String, List<RolesIntoOrganization>> getGroupsAndRolesByUser(
			String username, List<RolesIntoOrganization> rolesToMatch) {

		logger.debug("Requested roles that the user " + username + " has into his organizations");
		logger.debug("Roles to check are " + rolesToMatch);

		Map<String, List<RolesIntoOrganization>> toReturn = new HashMap<String, List<RolesIntoOrganization>>();

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
						List<RolesIntoOrganization> rolesIntoOrg = new ArrayList<RolesIntoOrganization>();

						if(rolesToMatch.contains(RolesIntoOrganization.valueOf(capacity.toUpperCase()))){
							RolesIntoOrganization enumRole = RolesIntoOrganization.valueOf(capacity.toUpperCase());
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
	public String findLicenseIdByLicense(String chosenLicense) {
		logger.debug("Requested license id");

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
		List<CkanLicense> licenses = client.getLicenseList();

		return licenses;
	}

	@Override
	public boolean setDatasetPrivate(boolean priv, String organizationId,
			String datasetId, String apiKey) {

		String pathSetPrivate = "/api/3/action/bulk_update_private";
		String pathSetPublic = "/api/3/action/bulk_update_public";


		if(apiKey == null || apiKey.isEmpty()){
			logger.error("The apiKey parameter is mandatory");
			return false;
		}

		// Request parameters to be replaced
		String parameter = "{"
				+ "\"org_id\":\"ORGANIZATION_ID\","
				+ "\"datasets\":[\"DATASET_ID\"]"
				+ "}";

		if(organizationId != null && !organizationId.isEmpty() && datasetId != null && !datasetId.isEmpty()){

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
					StringEntity params =new StringEntity(parameter);
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
		}

		return false;
	}

	@Override
	public String addResourceToDataset(ResourceBean resourceBean, String apiKey) {

		logger.debug("Request to add a resource described by this bean " + resourceBean);

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

		logger.debug("Request for dataset creation");

		CheckedCkanClient client = new CheckedCkanClient(CKAN_CATALOGUE_URL, apiKey);

		// get client from apiKey
		String ckanUsername = getUserFromApiKey(apiKey).getName();

		// create the base dataset and fill it
		CkanDataset dataset = new CkanDataset();

		// get the name from the title
		dataset.setName(UtilMethods.nameFromTitle(title));
		dataset.setTitle(title);

		CkanOrganization orgOwner = client.getOrganization(organizationNameOrId);
		dataset.setOwnerOrg(orgOwner.getId());
		dataset.setAuthor(author);
		dataset.setAuthorEmail(authorMail);
		dataset.setMaintainer(maintainer);
		dataset.setMaintainerEmail(maintainerMail);
		dataset.setVersion(String.valueOf(version));

		// description must be escaped
		Source descriptionEscaped = new Source(description);
		Segment htmlSeg = new Segment(descriptionEscaped, 0, descriptionEscaped.length());
		Renderer htmlRend = new Renderer(htmlSeg);
		dataset.setNotes(htmlRend.toString());

		logger.debug("Description (escaped is ) " + htmlRend.toString());

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
				setDatasetPrivate(
						!setPublic, // swap to private 
						res.getOrganization().getId(), 
						res.getId(), 
						apiKey);

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
	public boolean checkRole(String username, String organizationName,
			RolesIntoOrganization correspondentRoleToCheck) {

		logger.debug("Request for checking if " + username + " into " + organizationName + " has role " + correspondentRoleToCheck);

		// convert ckan username
		String ckanUsername = UtilMethods.fromUsernameToCKanUsername(username);

		// check if this role is already present in ckan for this user within the organization
		String organizationNameToCheck;
		if(organizationName.equals(PRODUCTION_LIFERAY_ORGNAME_ROOT))
			organizationNameToCheck = PRODUCTION_CKAN_ORGNAME_ROOT;
		else
			organizationNameToCheck = organizationName.toLowerCase();

		boolean alreadyPresent = isRoleAlreadySet(ckanUsername, organizationNameToCheck, correspondentRoleToCheck);

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
		return false;
	}

	/**
	 * Check if the user has this role into the organization with name organizationName
	 * @param ckanUsername
	 * @param organizationName
	 * @param correspondentRoleToCheck
	 * @return true if he has the role, false otherwise
	 */
	protected boolean isRoleAlreadySet(String ckanUsername,
			String organizationName,
			RolesIntoOrganization correspondentRoleToCheck) {

		try{

			// get the users (if you try ckanOrganization.getUsers() it returns null.. maybe a bug TODO)
			List<CkanUser> users = client.getOrganization(organizationName).getUsers();

			for (CkanUser ckanUser : users) {
				if(ckanUser.getName().equals(ckanUsername) && ckanUser.getCapacity().equals(correspondentRoleToCheck.toString().toLowerCase()))
					return true;
			}

		}catch(Exception e){
			logger.error("Unable to check if this role was already set", e);
		}

		return false;
	}

	@Override
	public boolean isSysAdmin(String username, String apiKey) {

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
	public boolean createOrganization(String orgName, String token)throws Exception{
		logger.debug("Create organization method call for creation of an organization named " + orgName);

		// we invoke the ckan connector to create this organization
		boolean result = false;

		if(token == null || token.trim().isEmpty()){
			throw new IllegalArgumentException("Gcube Token is missing here!");
		}

		if(orgName == null || orgName.trim().isEmpty()){
			throw new IllegalArgumentException("Organization name is missing here!");
		}

		try{
			String callUrl = CKAN_CATALOGUE_URL + "/ckan-connector/gcube/service/organization/" + orgName + "?gcube-token=" + token;
			URL url = new URL(callUrl);
			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			connection.setRequestMethod("PUT");

			logger.debug("Response code is " + connection.getResponseCode() + " and message is " + connection.getResponseMessage());

			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
				logger.info("CKan organization created [" + connection.getResponseMessage() + "]");
				result = true;
			}

			// close the connection
			connection.disconnect();

		}catch(Exception e){
			logger.error("Unable to create the organization", e);
		}

		return result;
	}
}
