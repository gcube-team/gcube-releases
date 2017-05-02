package org.gcube.data_catalogue.grsf_publish_ws.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data_catalogue.grsf_publish_ws.utils.cache.CacheImpl;
import org.gcube.data_catalogue.grsf_publish_ws.utils.cache.CacheInterface;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueImpl;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpGet;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.LaxRedirectStrategy;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;
import eu.trentorise.opendata.jackan.model.CkanLicense;


/**
 * Helper methods
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public abstract class HelperMethods {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HelperMethods.class);
	private static final String APPLICATION_ID_CATALOGUE_MANAGER = "org.gcube.datacatalogue.ProductCatalogue"; 
	private static final String NOTIFICATION_MESSAGE = "Dear members,<br>The product 'PRODUCT_TITLE' has been just published by USER_FULLNAME.<br>You can find it here: PRODUCT_URL <br>";
	private static final String SOCIAL_SERVICE_APPLICATION_TOKEN = "/2/tokens/generate-application-token/";
	private static final String SOCIAL_SERVICE_WRITE_APPLICATION_POST = "/2/posts/write-post-app/";
	private static final String MEDIATYPE_JSON = "application/json";

	// to be retrieved from the web.xml
	public static final String MANAGE_CONTEX_KEY = "ManageVRE";
	public static final String PUBLIC_CONTEX_KEY = "PublicVRE";
	private static final String CSV_MIME = "text/csv";
	private static final String PATH_SEPARATOR = "/";

	// caches
	private static CacheInterface<String, String> userEmailCache = new CacheImpl<String, String>(1000 * 60 * 120);
	private static CacheInterface<String, String> userFullnameCache = new CacheImpl<String, String>(1000 * 60 * 120);

	/**
	 * Convert a group name to its id on ckan
	 * @param origName
	 * @return
	 */
	public static String getGroupNameOnCkan(String origName){

		if(origName == null)
			throw new IllegalArgumentException("origName cannot be null");

		String modified = origName.trim().toLowerCase().replaceAll("[^A-Za-z0-9-]", "-");
		if(modified.startsWith("-"))
			modified = modified.substring(1);
		if(modified.endsWith("-"))
			modified = modified.substring(0, modified.length() -1);

		logger.info("Group name generated is " + modified);

		return modified;
	}

	/**
	 * Retrieve the running instance of the data catalogue for this scope
	 * @return
	 * @throws Exception 
	 */
	public static DataCatalogue getDataCatalogueRunningInstance(String scope){

		try{
			DataCatalogueImpl instance = DataCatalogueFactory.getFactory().getUtilsPerScope(scope);
			return instance;
		}catch(Exception e){
			logger.error("Failed to instanciate data catalogue lib", e);
		}

		return null;
	}

	/**
	 * Retrieve the organization name in which the user wants to publish starting from the scope
	 * @param contextInWhichPublish
	 * @return
	 */
	public static String retrieveOrgNameFromScope(String scope) {

		String[] splittedScope = scope.split("/");
		return splittedScope[splittedScope.length - 1].toLowerCase();

	}

	/**
	 * Validate the name the product will have
	 * @param futureName
	 * @return
	 */
	public static boolean isNameValid(String futureName) {

		if(futureName == null || futureName.isEmpty())
			return false;
		else{
			return futureName.matches("[\\sA-Za-z0-9_.-]+");
		}
	}

	/**
	 * Retrieve the user's email given his/her username
	 * @param context
	 * @param token
	 * @return
	 * @throws Exception 
	 */
	public static String getUserEmail(String context, String token){

		// check in cache
		String result = null;
		if((result = (String) userEmailCache.get(token)) != null){
			return result;
		}else{
			String baseUrl = new ServiceEndPointReaderSocial(context).getBasePath();
			// fallback: check for gcore endpoint
			if(baseUrl == null || baseUrl.isEmpty())
				baseUrl = new GcoreEndPointReaderSocial(context).getBasePath();
			String url = baseUrl.endsWith("/") ?  baseUrl + "users/getUserEmail?gcube-token=" + token :
				baseUrl + "/users/getUserEmail?gcube-token=" + token;
			logger.debug("Request url is " + url);
			result = executGETHttpRequest(url, 200);
			userEmailCache.insert(token, result);
		}
		return result;
	}

	/**
	 * Retrieve the user's fullname given his/her username
	 * @param context 
	 * @param token 
	 * @return
	 * @throws Exception 
	 */
	public static String getUserFullname(String context, String token){

		// check in cache
		String result = null;
		if((result = (String) userFullnameCache.get(token)) != null){
			return result;
		}else{
			String baseUrl = new ServiceEndPointReaderSocial(context).getBasePath();
			// fallback: check for gcore endpoint
			if(baseUrl == null || baseUrl.isEmpty())
				baseUrl = new GcoreEndPointReaderSocial(context).getBasePath();
			String url = baseUrl.endsWith("/") ?  baseUrl + "users/getUserFullname?gcube-token=" + token :
				baseUrl + "/users/getUserFullname?gcube-token=" + token;
			logger.debug("Request url is " + url);
			result = executGETHttpRequest(url, 200);
			userFullnameCache.insert(token, result);
		}
		return result;
	}

	/**
	 * Execute the GET http request at this url, and return the result as string
	 * @return
	 */
	private static String executGETHttpRequest(String url, int expectedCodeOnSuccess){

		try(CloseableHttpClient client = HttpClientBuilder.create().build();){

			HttpGet getRequest = new HttpGet(url);
			HttpResponse response = client.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != expectedCodeOnSuccess) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));

			String res = "";
			String temp = null;

			while ((temp = br.readLine()) != null) {
				res += temp;
			}

			return res;
		}catch(Exception e){
			logger.error("error while performing get method " + e.toString());
		}

		return null;

	}

	/**
	 * Retrieve the list of ckan licenses and build up a map <license_id, license_title>
	 * @return
	 * @throws Exception 
	 */
	public static Map<String, String> getLicenses(DataCatalogue catalogue) throws Exception {

		Map<String, String> toReturn = new HashMap<String, String>();
		List<CkanLicense> licenses = catalogue.getLicenses();

		for (CkanLicense ckanLicense : licenses) {
			toReturn.put(ckanLicense.getId(), ckanLicense.getTitle());
		}
		return toReturn;
	}

	/**
	 * Check that the given license id is in CKAN
	 * @param license id to check
	 * @return
	 * @throws Exception 
	 */
	public static boolean existsLicenseId(String license, DataCatalogue catalogue) throws Exception {

		List<CkanLicense> licenses = catalogue.getLicenses();
		for (CkanLicense ckanLicense : licenses) {
			if(ckanLicense.getId().equals(license))
				return true;
		}
		return false;
	}

	/**
	 * Upload a file in the shared folder
	 * @param resourceFormatFolder
	 * @param resourceToAttachName
	 * @param description
	 * @param csvFile
	 * @return
	 */
	public static ExternalFile uploadExternalFile(WorkspaceFolder resourceFormatFolder, String resourceToAttachName, String description, File csvFile) {
		try {
			return resourceFormatFolder.createExternalFileItem(resourceToAttachName, description, CSV_MIME, csvFile);
		} catch (InsufficientPrivilegesException | ItemAlreadyExistException
				| InternalErrorException e) {
			logger.error("Failed to upload the file into the workspace shared folder for " + resourceToAttachName, e);
		}
		return null;
	}

	/**
	 * Create subfolders in cascade, returning the last created ones
	 * It could be also used for getting them if they already exists
	 * @param folder
	 * @param subPath
	 * @return null if an error occurred
	 */
	public static WorkspaceFolder createOrGetSubFoldersByPath(WorkspaceFolder folder, String subPath){

		WorkspaceFolder parentFolder = folder;
		if(folder == null)
			throw new IllegalArgumentException("Root folder is null!");

		if(subPath == null || subPath.isEmpty())
			throw new IllegalArgumentException("subPath is null/empty!");

		try{
			if(subPath.startsWith(PATH_SEPARATOR))
				subPath = subPath.replaceFirst(PATH_SEPARATOR, "");

			if(subPath.endsWith(PATH_SEPARATOR))
				subPath = subPath.substring(0, subPath.length() - 1);

			logger.debug("Splitting path " + subPath);

			String[] splittedPaths = subPath.split(PATH_SEPARATOR);

			for (String path : splittedPaths) {
				WorkspaceFolder createdFolder = getFolderOrCreate(parentFolder, path, "");
				logger.debug("Created subfolder with path " + createdFolder.getPath());
				parentFolder = createdFolder;
			}

		}catch(Exception e){
			logger.error("Failed to create the subfolders by path " + subPath);
			return null;
		}

		return parentFolder;
	}

	/**
	 * Get a folder within the catalogue folder or create it if it doesn't exist.
	 * @return
	 */
	public static WorkspaceFolder getFolderOrCreate(WorkspaceFolder folder, String relativePath, String descriptionFolder){
		WorkspaceFolder result = null;
		try {
			WorkspaceItem foundFolder = folder.find(relativePath);
			if(foundFolder != null && foundFolder.isFolder())
				result = (WorkspaceFolder)foundFolder;

			if(result != null)
				logger.debug("Folder found with name " + result.getName() + ", it has id " + result.getId());
			else
				throw new Exception("There is no folder with name " + relativePath + " under folder " + folder.getName());
		} catch (Exception e) {
			logger.debug("Probably the folder doesn't exist");
			try{
				result = folder.createFolder(relativePath, descriptionFolder);
			} catch (InsufficientPrivilegesException | InternalErrorException | ItemAlreadyExistException e2) {
				logger.error("Failed to get or generate this folder", e2);
			}
		}
		return result;
	}

	/**
	 * Strip out HTML code
	 * @param html
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String removeHTML(String html) {
		if(html == null || html.isEmpty())
			return html;

		// remove html and clean
		String withoutHTML = Jsoup.parse(html).text();
		withoutHTML = Jsoup.clean(withoutHTML, Whitelist.basic());

		// remove non ascii chars ...
		withoutHTML = withoutHTML.replaceAll("[^\\p{ASCII}]", " ");
		return withoutHTML;
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
		// fallback: check for gcore endpoint
		if(basePath == null || basePath.isEmpty())
			basePath = new GcoreEndPointReaderSocial(currentScope).getBasePath();

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");

		}else{

			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			try(CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();){

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
	@SuppressWarnings("unchecked")
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
	 * Retrieve the identifiers of the products in a given group. It doesn't use CKAN API because they would return at most 1000 ids.
	 * @param string
	 * @param catalogue
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static List<String> getProductsInGroup(String groupName,
			DataCatalogue catalogue) throws ClassNotFoundException, SQLException {

		return catalogue.getProductsIdsInGroupOrOrg(groupName, false, 0, Integer.MAX_VALUE);

	}
}
