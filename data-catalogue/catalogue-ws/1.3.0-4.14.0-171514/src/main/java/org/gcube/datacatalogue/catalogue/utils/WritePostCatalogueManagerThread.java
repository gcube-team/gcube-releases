package org.gcube.datacatalogue.catalogue.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Let the Product Catalogue Manager write a post in a VRE and alert there is a new product.
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class WritePostCatalogueManagerThread extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(WritePostCatalogueManagerThread.class);
	
	private static final String APPLICATION_ID_CATALOGUE_MANAGER = "org.gcube.datacatalogue.ProductCatalogue";
	private static final String NOTIFICATION_MESSAGE = "Dear members,<br>The item '$PRODUCT_TITLE' has been just published by $USER_FULLNAME.<br>You can find it here: $PRODUCT_URL <br>";
	private static final String SOCIAL_SERVICE_APPLICATION_TOKEN = "/2/tokens/generate-application-token";
	private static final String SOCIAL_SERVICE_WRITE_APPLICATION_POST = "/2/posts/write-post-app";
	private static final String MEDIATYPE_JSON = "application/json";
	
	private String productName;
	private String productUrl;
	private boolean enableNotification;
	private List<String> hashtags;
	private String userFullName;
	
	/**
	 * @param productTitle
	 * @param productUrl
	 * @param enableNotification
	 * @param hashtags
	 * @param userFullName
	 */
	public WritePostCatalogueManagerThread(String productName, String productUrl, boolean enableNotification,
			List<String> hashtags, String userFullName) {
		super();
		this.productName = productName;
		this.productUrl = productUrl;
		this.enableNotification = enableNotification;
		this.hashtags = hashtags;
		this.userFullName = userFullName;
	}
	
	@Override
	public void run() {
		
		try {
			logger.info(
					"Started request to write application post for new product created. Scope is {} and  token is {}****************",
					ScopeProvider.instance.get(), SecurityTokenProvider.instance.get().substring(0, 10));
			
			// write
			writeProductPost();
			
		} catch(Exception e) {
			logger.error("Failed to write the post because of the following error ", e);
		} finally {
			SecurityTokenProvider.instance.reset();
			ScopeProvider.instance.reset();
		}
	}
	
	/**
	 * Send notification to vre members about the created product by writing a post.
	 * @param productName the title of the product
	 * @param productUrl the url of the product
	 * @param hashtags a list of product's hashtags
	 * @throws Exception 
	 */
	private void writeProductPost() throws Exception {
		
		// discover service endpoint for the social networking library
		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();
		
		logger.debug("Current scope for writeProductPost is " + currentScope + " and token is "
				+ tokenUser.substring(0, 10) + "***************");
		
		GcoreEndpointReaderSNL socialService = new GcoreEndpointReaderSNL();
		String basePath = socialService.getServiceBasePath();
		
		if(basePath == null) {
			logger.error("Unable to write a post because there is no social networking service available");
		} else {
			// check base path form
			basePath = basePath.endsWith("/") ? basePath : basePath + "/";
			
			try(CloseableHttpClient client = HttpClientBuilder.create().build();) {
				String pathTokenApp = basePath + SOCIAL_SERVICE_APPLICATION_TOKEN + "?"
						+ Constants.GCUBE_TOKEN_PARAMETER + "=" + tokenUser;
				String tokenApp = requireAppToken(client, pathTokenApp);
				if(tokenApp != null) {
					String pathWritePost = basePath + SOCIAL_SERVICE_WRITE_APPLICATION_POST + "?gcube-token="
							+ tokenApp;
					writePost(client, pathWritePost, productName, productUrl, userFullName, hashtags,
							enableNotification);
				}
				
			} catch(Exception e) {
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
	private static String requireAppToken(CloseableHttpClient client, String path) {
		
		String token = null;
		try {
			
			HttpResponse response = performRequest(client, path,
					"{\"app_id\":\"" + APPLICATION_ID_CATALOGUE_MANAGER + "\"}");
			
			int statusTokenGenerate = response.getStatusLine().getStatusCode();
			
			if(statusTokenGenerate == HttpURLConnection.HTTP_CREATED) {
				
				// extract token
				JSONObject obj = getJSONObject(response);
				if(((Boolean) obj.get("success")))
					token = (String) obj.get("result");
				else
					return null;
				
			} else if(statusTokenGenerate == HttpURLConnection.HTTP_MOVED_TEMP
					|| statusTokenGenerate == HttpURLConnection.HTTP_MOVED_PERM
					|| statusTokenGenerate == HttpURLConnection.HTTP_SEE_OTHER) {
				
				// re-execute
				Header[] locations = response.getHeaders("Location");
				Header lastLocation = locations[locations.length - 1];
				String realLocation = lastLocation.getValue();
				logger.debug("New location is " + realLocation);
				token = requireAppToken(client, realLocation);
				
			} else
				return null;
			
		} catch(Exception e) {
			logger.error("Failed to retrieve application token", e);
		}
		
		logger.info(
				"Returning app token " + (token != null ? token.substring(0, 10) + "*************************" : null));
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
	@SuppressWarnings("unchecked")
	private static void writePost(CloseableHttpClient client, String path, String productName, String productUrl,
			String userFullname, List<String> hashtags, boolean enablePostNotification) {
		
		try {
			
			// replace
			String message = NOTIFICATION_MESSAGE.replace("$PRODUCT_TITLE", productName)
					.replace("$PRODUCT_URL", productUrl).replace("$USER_FULLNAME", userFullname);
			
			if(hashtags != null && !hashtags.isEmpty())
				for(String hashtag : hashtags) {
					String modifiedHashtag = hashtag.replaceAll(" ", "_").replace("_+", "_");
					if(modifiedHashtag.endsWith("_"))
						modifiedHashtag = modifiedHashtag.substring(0, modifiedHashtag.length() - 1);
					message += " #" + modifiedHashtag; // ckan accepts tag with empty spaces, we don't
				}
			
			logger.info("The post that is going to be written is -> " + message);
			
			JSONObject objRequest = new JSONObject();
			objRequest.put("text", message);
			objRequest.put("enable_notification", enablePostNotification);
			HttpResponse response = performRequest(client, path, objRequest.toJSONString());
			int statusWritePost = response.getStatusLine().getStatusCode();
			
			if(statusWritePost == HttpURLConnection.HTTP_CREATED) {
				
				// extract token
				JSONObject obj = getJSONObject(response);
				if(((Boolean) obj.get("success")))
					logger.info("Post written");
				else
					logger.info("Failed to write the post " + obj.get("message"));
				
			} else if(statusWritePost == HttpURLConnection.HTTP_MOVED_TEMP
					|| statusWritePost == HttpURLConnection.HTTP_MOVED_PERM
					|| statusWritePost == HttpURLConnection.HTTP_SEE_OTHER) {
				
				// re-execute
				Header[] locations = response.getHeaders("Location");
				Header lastLocation = locations[locations.length - 1];
				String realLocation = lastLocation.getValue();
				logger.debug("New location is " + realLocation);
				writePost(client, realLocation, productName, productUrl, userFullname, hashtags,
						enablePostNotification);
				
			} else
				throw new RuntimeException("Failed to write the post");
			
		} catch(Exception e) {
			logger.error("Failed to retrieve application token", e);
		}
		
	}
	
	/**
	 * Convert the json response to a map
	 * @param response
	 * @return
	 */
	private static JSONObject getJSONObject(HttpResponse response) {
		
		JSONObject toReturn = null;
		HttpEntity entity = response.getEntity();
		
		if(entity != null) {
			try {
				JSONParser parser = new JSONParser();
				return (JSONObject) parser.parse(EntityUtils.toString(response.getEntity()));
			} catch(Exception e) {
				logger.error("Failed to read json object", e);
			}
		}
		
		logger.trace("Returning " + toReturn);
		return toReturn;
	}
	
	/**
	 * Perform an http request post request with json entity
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private static HttpResponse performRequest(CloseableHttpClient client, String path, String entity)
			throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(path);
		StringEntity stringEntity = new StringEntity(entity);
		stringEntity.setContentType(MEDIATYPE_JSON);
		request.setEntity(stringEntity);
		return client.execute(request);
	}
	
}