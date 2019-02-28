package org.gcube.gcat.social;

import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.cache.Cache;
import javax.ws.rs.core.MediaType;

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
	
	// String.format(NOTIFICATION_MESSAGE, title, fullName, url)
	protected static final String NOTIFICATION_MESSAGE = "Dear members,<br>The item '%s' has been just published by %s.<br>You can find it at: %s <br>";
	
	protected static final String RESULT_KEY = "result";
	protected static final String FULLNAME_IN_PROFILE_KEY = "fullname";
	protected static final String SOCIAL_POST_TEXT_KEY = "text";
	protected static final String SOCIAL_POST_ENABLE_NOTIFICATION_KEY = "enable_notification";
	protected static final String SOCIAL_POST_RESPONSE_SUCCESS_KEY = "success";
	protected static final String SOCIAL_POST_RESPONSE_MESSAGE_KEY = "message";
	
	protected String id;
	protected String url;
	protected String title;
	protected List<String> tags;
	protected final GcoreEndpointReaderSNL socialService;
	protected final ObjectMapper objectMapper;
	
	public SocialService() throws Exception {
		super();
		this.socialService = new GcoreEndpointReaderSNL();
		this.objectMapper = new ObjectMapper();
	}
	
	public SocialService(String id, String url, List<String> tags, String title) throws Exception {
		this();
		this.id = id;
		this.url = url;
		this.tags = tags;
		this.title = title;
	}
	
	public SocialService(String id, String url, ArrayNode arrayNode, String title) throws Exception {
		this();
		this.id = id;
		this.url = url;
		
		this.tags = new ArrayList<>();
		if(arrayNode != null && arrayNode.size() > 0) {
			for(int i = 0; i < arrayNode.size(); i++) {
				String tagName = arrayNode.get(i).get("display_name").asText();
				tags.add(tagName);
			}
		}
		
		this.title = title;
	}
	
	/**
	 * Execute the GET http request at this url, and return the result as string
	 * @return
	 * @throws Exception 
	 */
	public JsonNode getGCubeUserProfile() throws Exception {
		String username = ContextUtility.getUsername();
		return getGCubeUserProfile(username);
	}
	
	public JsonNode getGCubeUserProfile(String username) throws Exception {
		Cache<String,JsonNode> userCache = CachesManager.getUserCache();
		
		if(userCache.containsKey(username))
			return userCache.get(username);
		else {
			String socialServiceBasePath = socialService.getServiceBasePath();
			
			GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(socialServiceBasePath); 
			gxhttpStringRequest.header("User-Agent", Constants.CATALOGUE_NAME);
			gxhttpStringRequest.header("Accept", MediaType.APPLICATION_JSON);
			gxhttpStringRequest.path(SOCIAL_SERVICE_GET_USER_INFO_PATH);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			
			String message = httpURLConnection.getResponseMessage();
			JsonNode jsonNode = objectMapper.readTree(message);
			userCache.put(username, jsonNode);
			return jsonNode;
		}
	}
	
	public String getFullName() throws Exception {
		if(!ContextUtility.isApplication()) {
			JsonNode jsonNode = getGCubeUserProfile();
			JsonNode result = jsonNode.get(RESULT_KEY);
			return result.get(FULLNAME_IN_PROFILE_KEY).asText();
		} else {
			return ContextUtility.getUsername();
		}
	}
	
	@Override
	public void run() {
		
		try {
			DataCatalogue dataCatalogue = CKAN.getCatalogue();
			
			if(!dataCatalogue.isSocialPostEnabled()) {
				logger.info("Social Post are disabled in the context {}", ContextUtility.getCurrentContext());
				return;
			}
			logger.info("Going to send Social Post about the Item {} available at {}", id, url);
			
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
			
			String basePath = socialService.getServiceBasePath();
			if(basePath == null) {
				logger.info("Unable to write a post because there is no social networking service available");
				return;
			}
			basePath = basePath.endsWith("/") ? basePath : basePath + "/";
			
			StringWriter messageWriter = new StringWriter();
			messageWriter.append(String.format(NOTIFICATION_MESSAGE, title, fullName, url));
			
			for(String tag : tags) {
				tag = tag.trim();
				tag = tag.replaceAll(" ", "_").replace("_+", "_");
				if(tag.endsWith("_")) {
					tag = tag.substring(0, tag.length() - 1);
				}
				messageWriter.append(" #");
				messageWriter.append(tag);
			}
			String message = messageWriter.toString();
			
			logger.debug("The post that is going to be written is {} " + message);
			
			ObjectNode objectNode = objectMapper.createObjectNode();
			objectNode.put(SOCIAL_POST_TEXT_KEY, message);
			objectNode.put(SOCIAL_POST_ENABLE_NOTIFICATION_KEY, notifyUsers);
			
			// Do not use ApplicationMode class here because is a thread and change the current token could impact 
			// on the other threads.
			
			
			GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(basePath); 
			gxhttpStringRequest.header("User-Agent", Constants.CATALOGUE_NAME);
			gxhttpStringRequest.setSecurityToken(Constants.getCatalogueApplicationToken());
			gxhttpStringRequest.path(SOCIAL_SERVICE_WRITE_APPLICATION_POST_PATH);
			
			
			HttpURLConnection httpURLConnection = gxhttpStringRequest.post(objectMapper.writeValueAsString(objectNode));
			String ret = httpURLConnection.getResponseMessage();
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
