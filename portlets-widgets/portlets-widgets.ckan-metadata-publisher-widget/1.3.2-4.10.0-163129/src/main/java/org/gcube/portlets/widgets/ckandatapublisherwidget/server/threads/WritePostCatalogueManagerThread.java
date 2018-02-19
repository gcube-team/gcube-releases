package org.gcube.portlets.widgets.ckandatapublisherwidget.server.threads;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.GCoreEndPointReaderSocial;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.utils.GenericUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.internal.org.apache.http.Header;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.ClientProtocolException;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;


/**
 * Let the Product Catalogue Manager write a post in a VRE and alert there is a new product
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class WritePostCatalogueManagerThread extends Thread {

	private static final String APPLICATION_ID_CATALOGUE_MANAGER = "org.gcube.datacatalogue.ProductCatalogue"; 
	private static final String NOTIFICATION_MESSAGE = "Dear members,<br>The item '$PRODUCT_TITLE' has been just published by $USER_FULLNAME.<br>You can find it here: $PRODUCT_URL <br>";
	private static final String SOCIAL_SERVICE_APPLICATION_TOKEN = "2/tokens/generate-application-token";
	private static final String SOCIAL_SERVICE_WRITE_APPLICATION_POST = "2/posts/write-post-app";
	private static final String MEDIATYPE_JSON = "application/json";
	private static final Log logger = LogFactoryUtil.getLog(WritePostCatalogueManagerThread.class);
	//	private static Logger logger = LoggerFactory.getLogger(WritePostCatalogueManagerThread.class);
	private String username;
	private String scope;
	private String productTitle; 
	private String productUrl;
	private boolean enableNotification;
	private List<String> hashtags;
	private String userFullName;
	private String userCurrentUrl;

	/**
	 * @param token
	 * @param scope
	 * @param productTitle
	 * @param productUrl
	 * @param enableNotification
	 * @param hashtags
	 * @param userFullName
	 */
	public WritePostCatalogueManagerThread(
			String username, String scope,
			String productTitle, String productUrl, boolean enableNotification,
			List<String> hashtags, String userFullName, String userCurrentUrl) {
		super();
		this.username = username;
		this.scope = scope;
		this.productTitle = productTitle;
		this.productUrl = productUrl;
		this.enableNotification = enableNotification;
		this.hashtags = hashtags;
		this.userFullName = userFullName;
		this.userCurrentUrl = userCurrentUrl;
	}

	@Override
	public void run() {

		try{
			// evaluate user's token for this scope
			String token = GenericUtils.tryGetElseCreateToken(username, scope);

			if(token == null){
				logger.warn("Unable to proceed, user's token is not available");
				return;
			}

			logger.info("Started request to write application post "
					+ "for new product created. Scope is " + scope + " and "
					+ "token is " + token.substring(0, 10) + "****************");

			// set token and scope
			ScopeProvider.instance.set(scope);
			SecurityTokenProvider.instance.set(token);

			final String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + extractOrgFriendlyURL(userCurrentUrl) + GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;

			userFullName = "<a class=\"link\" href=\"" + profilePageURL + "?"+
					Base64.getEncoder().encodeToString(GCubeSocialNetworking.USER_PROFILE_OID.getBytes())+"="+
					Base64.getEncoder().encodeToString(username.getBytes())+"\">"+userFullName+
					"</a> ";

			// write
			writeProductPost(
					productTitle, 
					productUrl, 
					userFullName, 
					hashtags, 
					enableNotification
					);

		}catch(Exception e){
			logger.error("Failed to write the post because of the following error ", e);
		}finally{
			SecurityTokenProvider.instance.reset();
			ScopeProvider.instance.reset();
		}
	}

	public static String extractOrgFriendlyURL(String portalURL) {
		String groupRegEx = "/group/";
		if (portalURL.contains(groupRegEx)) {
			String[] splits = portalURL.split(groupRegEx);
			String friendlyURL = splits[1];
			if (friendlyURL.contains("/")) {
				friendlyURL = friendlyURL.split("/")[0];
			} else {
				friendlyURL = friendlyURL.split("\\?")[0].split("\\#")[0];
			}
			return "/"+friendlyURL;
		}
		return null;
	}

	/**
	 * Send notification to vre members about the created product by writing a post.
	 * @param productName the title of the product
	 * @param productUrl the url of the product
	 * @param hashtags a list of product's hashtags
	 */
	private static void writeProductPost(String productName, String productUrl, String userFullname, List<String> hashtags, boolean enablePostNotification){

		// discover service endpoint for the social networking library
		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();

		logger.info("Current scope for writeProductPost is " + currentScope + " and token is " + tokenUser.substring(0, 10) + "***************");
		String basePath = new GCoreEndPointReaderSocial(currentScope).getBasePath();

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");

		}else{

			// check base path form
			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			try(CloseableHttpClient client = HttpClientBuilder.create().build();){

				String pathTokenApp = basePath + SOCIAL_SERVICE_APPLICATION_TOKEN + "?gcube-token=" + tokenUser;
				String tokenApp = requireAppToken(client, pathTokenApp);
				if(tokenApp != null){
					String pathWritePost = basePath + SOCIAL_SERVICE_WRITE_APPLICATION_POST + "?gcube-token=" + tokenApp;
					writePost(client, pathWritePost, productName, productUrl, userFullname, hashtags, enablePostNotification);
				}

			}catch(Exception e){
				logger.error("Failed to create a post", e);
			}
		}
	}

	/**
	 * Require the application token
	 * @param tokenUser 
	 * @param basePath 
	 * @param client 
	 * @return 
	 */
	private static String requireAppToken(CloseableHttpClient client, String path){

		String token = null;
		try{

			JSONObject request = new JSONObject();
			request.put("app_id", APPLICATION_ID_CATALOGUE_MANAGER);
			HttpResponse response = performRequest(client, path,  request.toJSONString());

			int statusTokenGenerate = response.getStatusLine().getStatusCode();

			if(statusTokenGenerate == HttpURLConnection.HTTP_CREATED){

				// extract token
				JSONObject obj = getJSONObject(response);
				if(((Boolean) obj.get("success")))
					token = (String)obj.get("result");	
				else
					return null;

			}else if(statusTokenGenerate == HttpURLConnection.HTTP_MOVED_TEMP
					|| statusTokenGenerate == HttpURLConnection.HTTP_MOVED_PERM
					|| statusTokenGenerate == HttpURLConnection.HTTP_SEE_OTHER){

				// re-execute
				Header[] locations = response.getHeaders("Location");
				Header lastLocation = locations[locations.length - 1];
				String realLocation = lastLocation.getValue();
				logger.debug("New location is " + realLocation);
				token = requireAppToken(client, realLocation);

			}else
				return null;

		}catch(Exception e){
			logger.error("Failed to retrieve application token", e);
		}

		logger.info("Returning app token " + (token != null ? token.substring(0, 10) + "*************************" : null));
		return token;
	}

	/**
	 * Write post request
	 * @param client
	 * @param applicationToken
	 * @param productName
	 * @param productUrl
	 * @param userFullname
	 * @param hashtags
	 */
	private static void writePost(CloseableHttpClient client, String path, String productName, String productUrl, String userFullname, List<String> hashtags,
			boolean enablePostNotification) {

		try{

			// replace
			String message = NOTIFICATION_MESSAGE.replace("$PRODUCT_TITLE", productName).replace("$PRODUCT_URL", productUrl).replace("$USER_FULLNAME", userFullname);

			if(hashtags != null && !hashtags.isEmpty())
				for (String hashtag : hashtags) {
					String modifiedHashtag = hashtag.replaceAll(" ", "_").replace("_+", "_");
					if(modifiedHashtag.endsWith("_"))
						modifiedHashtag = modifiedHashtag.substring(0, modifiedHashtag.length() - 1);
					message += " #" + modifiedHashtag; // ckan accepts tag with empty spaces, we don't
				}

			JSONObject request = new JSONObject();
			request.put("text", message);
			request.put("enable_notification", enablePostNotification);
			logger.info("The post that is going to be written is ->\n" + request.toJSONString());
			HttpResponse response = performRequest(client, path,  request.toJSONString());
			int statusWritePost = response.getStatusLine().getStatusCode();

			if(statusWritePost == HttpURLConnection.HTTP_CREATED){

				// extract token
				JSONObject obj = getJSONObject(response);
				if(((Boolean) obj.get("success")))
					logger.info("Post written");
				else
					logger.info("Failed to write the post " + obj.get("message"));

			}else if(statusWritePost == HttpURLConnection.HTTP_MOVED_TEMP
					|| statusWritePost == HttpURLConnection.HTTP_MOVED_PERM
					|| statusWritePost == HttpURLConnection.HTTP_SEE_OTHER){

				// re-execute
				Header[] locations = response.getHeaders("Location");
				Header lastLocation = locations[locations.length - 1];
				String realLocation = lastLocation.getValue();
				logger.debug("New location is " + realLocation);
				writePost(client, realLocation, productName, productUrl, userFullname, hashtags, enablePostNotification);

			}else
				throw new RuntimeException("Failed to write the post ");

		}catch(Exception e){
			logger.error("Failed to write the post ", e);
		}

	}
	
	/**
	 * Convert the json response to a map
	 * @param response
	 * @return
	 */
	private static JSONObject getJSONObject(HttpResponse response){

		JSONObject toReturn = null;
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			try {
				String jsonString = EntityUtils.toString(response.getEntity());
				JSONParser parser = new JSONParser();
				toReturn = (JSONObject)parser.parse(jsonString);
			}catch(Exception e){
				logger.error("Failed to read json object", e);
			}
		}

		logger.debug("Returning " + toReturn.toJSONString());
		return toReturn;
	}

	/**
	 * Perform an http request post request with json entity
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private static HttpResponse performRequest(CloseableHttpClient client, String path, String entity) throws ClientProtocolException, IOException{

		HttpPost request = new HttpPost(path);
		StringEntity stringEntity = new StringEntity(entity);
		stringEntity.setContentType(MEDIATYPE_JSON);
		request.setEntity(stringEntity);
		return client.execute(request);

	}

}