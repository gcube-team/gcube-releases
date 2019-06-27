package org.gcube.gcat.social;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.cache.Cache;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.gxhttp.request.GXHTTPStringRequest;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.gcat.oldutils.CachesManager;
import org.gcube.gcat.persistence.ckan.CKAN;
import org.gcube.gcat.utils.Constants;
import org.gcube.gcat.utils.ContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SocialService extends Thread {
	
	private static final Logger logger = LoggerFactory.getLogger(SocialService.class);
	
	public static final String ITEM_URL = "Item URL";
	
	// https://wiki.gcube-system.org/gcube/Social_Networking_Service#Write_application_post_2
	protected static final String SOCIAL_SERVICE_WRITE_APPLICATION_POST_PATH = "/2/posts/write-post-app";
	// https://wiki.gcube-system.org/gcube/Social_Networking_Service
	protected static final String SOCIAL_SERVICE_GET_USER_INFO_PATH = "2/users/get-profile";
	
	// String.format(NOTIFICATION_MESSAGE, fullName, title, url)
	protected static final String NOTIFICATION_MESSAGE = "%s just published the item \"%s\"\n" + 
			"Please find it at %s\n";
	
	protected static final String RESULT_KEY = "result";
	protected static final String FULLNAME_IN_PROFILE_KEY = "fullname";
	protected static final String SOCIAL_POST_TEXT_KEY = "text";
	protected static final String SOCIAL_POST_ENABLE_NOTIFICATION_KEY = "enable_notification";
	protected static final String SOCIAL_POST_RESPONSE_SUCCESS_KEY = "success";
	protected static final String SOCIAL_POST_RESPONSE_MESSAGE_KEY = "message";
	
	protected final GcoreEndpointReaderSNL gcoreEndpointReaderSNL;
	protected final ObjectMapper objectMapper;
	
	protected String itemID;
	protected String itemURL;
	protected String itemTitle;
	protected List<String> tags;
	protected JsonNode gCubeUserProfile;
	
	public SocialService() throws Exception {
		super();
		this.gcoreEndpointReaderSNL = new GcoreEndpointReaderSNL();
		this.objectMapper = new ObjectMapper();
	}
	
	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getItemURL() {
		return itemURL;
	}

	public void setItemURL(String itemURL) {
		this.itemURL = itemURL;
	}
	
	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	public void setTags(ArrayNode tags) {
		this.tags = new ArrayList<>();
		if(tags != null && tags.size() > 0) {
			for(int i = 0; i < tags.size(); i++) {
				JsonNode jsonNode = tags.get(i);
				String tagName = "";
				if(jsonNode.has("display_name")) {
					tagName = jsonNode.get("display_name").asText();
				}else {
					tagName = jsonNode.get("name").asText();
				}
				this.tags.add(tagName);
			}
		}
	}
	
	/**
	 * Execute the GET http request at this url, and return the result as string
	 * @return
	 * @throws Exception 
	 */
	public JsonNode getGCubeUserProfile() throws Exception {
		if(gCubeUserProfile==null) {
			String username = ContextUtility.getUsername();
			gCubeUserProfile = getGCubeUserProfile(username);
		}
		return gCubeUserProfile;
	}
	
	protected StringBuilder getStringBuilder(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		}
		
		return result;
	}
	
	protected String getResultAsString(HttpURLConnection httpURLConnection) throws IOException {
		int responseCode  = httpURLConnection.getResponseCode();
		if(responseCode >= Status.BAD_REQUEST.getStatusCode()) {
			Status status = Status.fromStatusCode(responseCode);
			InputStream inputStream = httpURLConnection.getErrorStream();
			StringBuilder result = getStringBuilder(inputStream);
			logger.trace(result.toString());
			throw new WebApplicationException(status);
		}
		InputStream inputStream = httpURLConnection.getInputStream();
		String ret = getStringBuilder(inputStream).toString();
		logger.trace("Got Respose is {}", ret);
		return ret;
	}
	
	protected JsonNode getGCubeUserProfile(String username) throws Exception {
		Cache<String,JsonNode> userCache = CachesManager.getUserCache();
		
		if(userCache.containsKey(username))
			return userCache.get(username);
		else {
			String socialServiceBasePath = gcoreEndpointReaderSNL.getServiceBasePath();
			
			GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(socialServiceBasePath); 
			gxhttpStringRequest.header("User-Agent", Constants.CATALOGUE_NAME);
			gxhttpStringRequest.header("Accept", MediaType.APPLICATION_JSON);
			gxhttpStringRequest.path(SOCIAL_SERVICE_GET_USER_INFO_PATH);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			
			
			String ret = getResultAsString(httpURLConnection);
			
			JsonNode jsonNode = objectMapper.readTree(ret);
			userCache.put(username, jsonNode);
			return jsonNode;
		}
	}
	
	public String getFullName() throws Exception {
		try {
			if(!ContextUtility.isApplication()) {
				JsonNode jsonNode = getGCubeUserProfile();
				JsonNode result = jsonNode.get(RESULT_KEY);
				return result.get(FULLNAME_IN_PROFILE_KEY).asText();
			}
		}catch (Exception e) {
			logger.warn("Unable to get the full name of the requesting user via Social Service. The username corresponsing to the requester token will be used.");
		}
		return ContextUtility.getUsername();
	}
	
	
	@Override
	public void run() {
		
		try {
			DataCatalogue dataCatalogue = CKAN.getCatalogue();
			
			if(!dataCatalogue.isSocialPostEnabled()) {
				logger.info("Social Post are disabled in the context {}", ContextUtility.getCurrentContext());
				return;
			}
			logger.info("Going to send Social Post about the Item {} available at {}", itemID, itemURL);
			
			boolean notifyUsers = dataCatalogue.isNotificationToUsersEnabled();
			// write notification post
			sendSocialPost(notifyUsers);
			
		} catch(Exception e) {
			logger.error("Error while executing post creation actions", e);
		}
	}
	
	public void sendSocialPost(boolean notifyUsers) {
		
		try {
			String fullName = getFullName();
			
			String basePath = gcoreEndpointReaderSNL.getServiceBasePath();
			if(basePath == null) {
				logger.info("Unable to write a post because there is no social networking service available");
				return;
			}
			basePath = basePath.endsWith("/") ? basePath : basePath + "/";
			
			StringWriter messageWriter = new StringWriter();
			messageWriter.append(String.format(NOTIFICATION_MESSAGE, fullName, itemTitle, itemURL));
			
			for(String tag : tags) {
				tag = tag.trim();
				tag = tag.replaceAll(" ", "_").replace("_+", "_");
				if(tag.endsWith("_")) {
					tag = tag.substring(0, tag.length() - 1);
				}
				messageWriter.append("#");
				messageWriter.append(tag);
				messageWriter.append(" ");
			}
			String message = messageWriter.toString();
			
			logger.debug("The post that is going to be written is\n{}",message);
			
			ObjectNode objectNode = objectMapper.createObjectNode();
			objectNode.put(SOCIAL_POST_TEXT_KEY, message);
			objectNode.put(SOCIAL_POST_ENABLE_NOTIFICATION_KEY, notifyUsers);
			
			// Do not use ApplicationMode class here because is a thread and change the current token could impact 
			// on the other threads.
			
			
			GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(basePath); 
			gxhttpStringRequest.from(Constants.CATALOGUE_NAME);
			gxhttpStringRequest.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			gxhttpStringRequest.setSecurityToken(Constants.getCatalogueApplicationToken());
			gxhttpStringRequest.path(SOCIAL_SERVICE_WRITE_APPLICATION_POST_PATH);
			
			
			HttpURLConnection httpURLConnection = gxhttpStringRequest.post(objectMapper.writeValueAsString(objectNode));
			String ret = getResultAsString(httpURLConnection);
			JsonNode jsonNode = objectMapper.readTree(ret);
			if(jsonNode.get(SOCIAL_POST_RESPONSE_SUCCESS_KEY).asBoolean()) {
				logger.info("Post written : {}", message);
			} else {
				logger.info("Failed to write the post {}. Reason {}", message,
						jsonNode.get(SOCIAL_POST_RESPONSE_MESSAGE_KEY).asText());
			}
		} catch(Exception e) {
			logger.error("Unable to send Social Post", e);
		}
		
	}
}
