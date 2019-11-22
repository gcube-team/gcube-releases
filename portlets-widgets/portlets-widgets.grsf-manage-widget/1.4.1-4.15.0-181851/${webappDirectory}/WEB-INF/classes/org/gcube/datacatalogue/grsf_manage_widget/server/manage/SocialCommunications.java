package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.common.Constants;
import org.gcube.datacatalogue.grsf_manage_widget.shared.HashTagsOnUpdate;
import org.gcube.datacatalogue.grsf_manage_widget.shared.ManageProductBean;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperationInfo;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperations;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.HttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.CloseableHttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.LaxRedirectStrategy;
import eu.trentorise.opendata.jackan.internal.org.apache.http.util.EntityUtils;


/**
 * For managing the different interactions with social channels (posts and mails)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SocialCommunications {

	private static final Logger logger = LoggerFactory.getLogger(SocialCommunications.class);

	// for discovering social networking service
	private static final String resource = "jersey-servlet";
	private static final String serviceName = "SocialNetworking";
	private static final String serviceClass = "Portal";

	public static final String USER_PROFILE_OID = "userIdentificationParameter";

	// social operations
	private static final String SOCIAL_SERVICE_APPLICATION_TOKEN = "2/tokens/generate-application-token";
	private static final String SOCIAL_SERVICE_WRITE_APPLICATION_POST = "2/posts/write-post-app";
	private static final String SOCIAL_SEND_EMAIL = "2/messages/write-message";
	private static final String MEDIATYPE_JSON = "application/json";

	// for writing a post in the GRSF admin context
	private static final String APPLICATION_ID_CATALOGUE_MANAGER = "org.gcube.datacatalogue.GRSFNotifier"; 

	// emails to be sent to editors and reviewers and post to be written into the grsf admin vre
	private static final String POST_MESSAGE = "Dear members,"
			+ "\nThe record 'PRODUCT_TITLE' has been just updated by USER_FULLNAME."
			+ "\nYou can inspect it here: LINK_RECORD.";

	private static final String EMAIL_MESSAGE_REVIEWER = "Dear GRSF Reviewer,"
			+ "<br>an update on the record named 'PRODUCT_TITLE' has been requested by USER_FULLNAME."
			+ "<br>You can inspect it here: LINK_RECORD.";

	private static final String EMAIL_MESSAGE_EDITOR = "Dear USER_FULLNAME,"
			+ "<br>your request for the record 'PRODUCT_TITLE' has been accepted."
			+ "<br>You can inspect it here: LINK_RECORD.";

	private static final String ADD_REPORT = "<br> <br>This is a summary of the actions proposed: <br>REPORT_UPDATE<br>";

	// revert link 
	private static final String REVERT_LINK_PIECE = "<br>The request involves a merge operation. You can reject the merge by exploiting this link LINK in the following 24 hours.";

	// on revert operation
	private static final String EMAIL_REVIEWER_REVERT = "Dear GRSF Reviewer,"
			+ "<br>a revert operation (undo merge) has been requested on record RECORD_URL, by ADMIN_WHO_CHANGED.";

	private static final String EMAIL_EDITOR_REVERT = "Dear ORIGINAL_USER,"
			+"<br>a revert operation (undo merge) has been requested on this RECORD_URL you managed by ADMIN_WHO_CHANGED.";

	// post on revert
	private static final String POST_ON_REVERT = "Dear members,"
			+ "\na merge operation has been reverted on this record RECORD_URL by ADMIN_WHO_CHANGED. The merge was originally proposed by ORIGINAL_USER.";

	private static final String SOCIAL_NETWORKING_BASE_URL_SESSION_KEY = "SOCIAL_NETWORKING_SESSION_KEY";

	/**
	 * 
	 * @param httpServletRequest 
	 * @param context
	 * @return
	 */
	public static String getBaseUrlSocialService(HttpServletRequest httpServletRequest){

		String context = ScopeProvider.instance.get();

		String keyPerContext = SOCIAL_NETWORKING_BASE_URL_SESSION_KEY + context;	
		String basePath = (String) httpServletRequest.getSession().getAttribute(keyPerContext);

		if(basePath == null){
			try{

				SimpleQuery query = queryFor(GCoreEndpoint.class);
				query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
				query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
				query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));
				query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+resource+"\"]/text()");

				DiscoveryClient<String> client = client();
				List<String> endpoints = client.submit(query);
				if (endpoints == null || endpoints.isEmpty()) 
					throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+context);


				basePath = endpoints.get(0);
				if(basePath==null)
					throw new Exception("Endpoint:"+resource+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+context);

				httpServletRequest.getSession().setAttribute(keyPerContext, basePath);

			}catch(Exception e){
				logger.error("Unable to retrieve such service endpoint information!", e);
			}
		}
		logger.info("Found base path " + basePath + " for the service");
		return basePath;
	}

	/**
	 * Require a proper application token for writing a post and send messages.
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static String requireApplicationToken(String serviceUrl) throws Exception{

		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();

		logger.info("Current scope for requireApplicationToken is " + currentScope + " and token is " + tokenUser.substring(0, 10) + "***************");
		String basePath = serviceUrl;

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");
			throw new Exception("Unable to discover the social networking service");

		}else{

			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			try(CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();){

				// ask token application
				HttpPost postRequest = new HttpPost(basePath + SOCIAL_SERVICE_APPLICATION_TOKEN + "?gcube-token=" + tokenUser);
				JSONObject requestToken = new JSONObject();
				requestToken.put("app_id", APPLICATION_ID_CATALOGUE_MANAGER);
				StringEntity input = new StringEntity(requestToken.toJSONString());
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
						return (String)mapResponseGeneratedToken.get("result");
					}

				}
			}catch(Exception e){
				logger.error("Failed to create a post", e);
				throw e;
			}
		}
	}

	/**
	 * Notify the users about the required changes.
	 * @param bean
	 * @param url
	 * @param username
	 * @param fullName
	 * @param hashtags
	 * @param enablePostNotification
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void writePostOnRevert(String serviceUrl, RevertableOperationInfo rInfo, boolean enablePostNotification, String userCurrentUrl) throws Exception{

		// discover service endpoint for the social networking library
		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();

		logger.info("Current scope for writePostOnRevert is " + currentScope + " and token is " + tokenUser.substring(0, 10) + "***************");
		String basePath = serviceUrl;

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");
			throw new Exception("Unable to discover the social networking service");

		}else{

			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			try(CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();){

				// require url
				String applicationToken = requireApplicationToken(serviceUrl);

				//see Feature #17576 updated by Francesco
				/*final String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + extractOrgFriendlyURL(userCurrentUrl) + GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
				String userFullNameHighlightedCurrent = "<a class=\"link\" href=\"" + profilePageURL + "?"+
						Base64.getEncoder().encodeToString(USER_PROFILE_OID.getBytes())+"="+
						Base64.getEncoder().encodeToString(rInfo.getUserNameCurrentAdmin().getBytes())+"\">"+rInfo.getFullNameCurrentAdmin()+
						"</a> ";
				*/
				
				String userFullNameHighlightedCurrent = "@"+rInfo.getUserNameCurrentAdmin();

				/*String userFullNameHighlightedOriginal = "<a class=\"link\" href=\"" + profilePageURL + "?"+
						Base64.getEncoder().encodeToString(USER_PROFILE_OID.getBytes())+"="+
						Base64.getEncoder().encodeToString(rInfo.getUserNameOriginalAdmin().getBytes())+"\">"+rInfo.getFullNameOriginalAdmin()+
						"</a> ";*/
				
				String userFullNameHighlightedOriginal = "@"+rInfo.getUserNameOriginalAdmin();
				// replace
				String message  = POST_ON_REVERT.replace("RECORD_URL", rInfo.getRecordUrl()).replace("ADMIN_WHO_CHANGED", userFullNameHighlightedCurrent).replace("ORIGINAL_USER",userFullNameHighlightedOriginal);

				// add hashtag
				message +="\n\n";
				message += " #" + HashTagsOnUpdate.REVERTED_MERGE.getString();

				logger.info("The post that is going to be written is -> " + message);
				HttpPost postRequest = new HttpPost(basePath + SOCIAL_SERVICE_WRITE_APPLICATION_POST + "?gcube-token=" + applicationToken);
				JSONObject object = new JSONObject();
				object.put("text", message);
				object.put("enable_notification", enablePostNotification);
				StringEntity input = new StringEntity(object.toJSONString());
				input.setContentType(MEDIATYPE_JSON);
				postRequest.setEntity(input);
				CloseableHttpResponse response = client.execute(postRequest);

				Map<String, Object> mapResponseWritePost = getResponseEntityAsJSON(response);

				if (response.getStatusLine().getStatusCode() != 201)
					throw new RuntimeException("Failed to write application post : HTTP error code : "
							+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));

			}catch(Exception e){
				logger.error("Failed to create a post", e);
			}
		}
	}

	/**
	 * Notify the users about the required changes.
	 * @param bean
	 * @param url
	 * @param username
	 * @param fullName
	 * @param hashtags
	 * @param enablePostNotification
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static void writeProductPost(String serviceUrl, ManageProductBean bean, String username, String fullName, boolean enablePostNotification, String userCurrentUrl) throws Exception{

		// discover service endpoint for the social networking library
		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();

		logger.info("Current scope for writeProductPost is " + currentScope + " and token is " + tokenUser.substring(0, 10) + "***************");
		String basePath = serviceUrl;

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");
			throw new Exception("Unable to discover the social networking service");

		}else{

			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			try(CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();){

				// require url
				String applicationToken = requireApplicationToken(serviceUrl);
				
				//see Feature #17576 Updated by Francesco
				/*final String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + extractOrgFriendlyURL(userCurrentUrl) + GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;
				String userFullNameHighlighted = "<a class=\"link\" href=\"" + profilePageURL + "?"+
						Base64.getEncoder().encodeToString(USER_PROFILE_OID.getBytes())+"="+
						Base64.getEncoder().encodeToString(username.getBytes())+"\">"+fullName+
						"</a> ";
				*/
				
				String userFullNameHighlighted = "@"+username;

				// replace
				String message  = POST_MESSAGE.replace("PRODUCT_TITLE", bean.getTitle()).replace("LINK_RECORD", bean.getUrl()).
						replace("USER_FULLNAME", userFullNameHighlighted);

				if(bean.getReport() != null && !bean.getReport().isEmpty())
					message += ADD_REPORT.replace("REPORT_UPDATE", bean.getReport());

				Set<String> hashtags = bean.getHashtags();
				logger.debug("Hashtags are " + hashtags);
				if(hashtags != null && !hashtags.isEmpty()){
					message +="\n\n";
					for (String hashtag : hashtags) {
						message += " #" + hashtag;
					}
					//Added by Francesco Mangiacrapa see at Feature #16312
					String normalizedFullName = fullName.trim().replace(" ", "_");
					message += " #" + normalizedFullName;
				}

				logger.info("The post that is going to be written is -> " + message);
				HttpPost postRequest = new HttpPost(basePath + SOCIAL_SERVICE_WRITE_APPLICATION_POST + "?gcube-token=" + applicationToken);
				JSONObject object = new JSONObject();
				object.put("text", message);
				object.put("enable_notification", enablePostNotification);
				StringEntity input = new StringEntity(object.toJSONString());
				input.setContentType(MEDIATYPE_JSON);
				postRequest.setEntity(input);
				CloseableHttpResponse response = client.execute(postRequest);

				Map<String, Object> mapResponseWritePost = getResponseEntityAsJSON(response);

				if (response.getStatusLine().getStatusCode() != 201)
					throw new RuntimeException("Failed to write application post : HTTP error code : "
							+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));
			}catch(Exception e){
				logger.error("Failed to create a post", e);
			}
		}
	}

	private static String extractOrgFriendlyURL(String portalURL) {
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
	 * Send an email to the administrator as well as the 
	 * @param bean
	 * @param catalogue
	 * @param username
	 * @param fullName
	 * @param isMergeInvolved 
	 * @param httpSession 
	 * @throws Exceptio 
	 */
	@SuppressWarnings("unchecked")
	public static void sendEmailAdministrators(
			String serviceUrl,
			ManageProductBean bean,
			DataCatalogue catalogue, 
			String username, 
			String fullName, 
			long groupId, 
			String clientCurrenturl, 
			boolean isMergeInvolved) throws Exception {

		// get the list of GRSF Reviewers to alert them as well
		RoleManager roleManager = new LiferayRoleManager();
		long teamRoleId = roleManager.getTeam(groupId, Constants.GRSF_CATALOGUE_REVIEWER_ROLE).getTeamId();
		List<GCubeUser> reviewersGcube = new LiferayUserManager().listUsersByTeam(teamRoleId);
		logger.debug("Reviewers are " + reviewersGcube);

		List<String> reviewers = new ArrayList<String>(reviewersGcube.size());

		for(GCubeUser gU: reviewersGcube){
			// if the user is a reviewer, then send the email just once
			if(!gU.getUsername().equals(username))
				reviewers.add(gU.getUsername());
		}

		logger.info("List of " + Constants.GRSF_CATALOGUE_REVIEWER_ROLE + " is " + reviewers);

		// build the url that allows to revert the operation 
		RevertableOperations operation = RevertableOperations.MERGE;

		// discover service endpoint for the social networking library
		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();

		logger.info("Current scope for writeProductPost is " + currentScope + " and token is " + tokenUser.substring(0, 10) + "***************");
		String basePath = serviceUrl;

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");
			throw new Exception("Unable to discover the social networking service");

		}else{

			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			try(CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();){

				/// require url
				String applicationToken = requireApplicationToken(serviceUrl);

				String revertUrl = "";
				if(isMergeInvolved)
					revertUrl = getEncodedUrlManage(operation, username, System.currentTimeMillis(), bean.getKnowledgeBaseId(), clientCurrenturl);

				String messageToEditor = (EMAIL_MESSAGE_EDITOR +
						(isMergeInvolved? REVERT_LINK_PIECE : "")).replace("USER_FULLNAME", fullName).replace("PRODUCT_TITLE", bean.getTitle()).
						replace("LINK_RECORD", bean.getUrl()).replace("LINK", revertUrl);
				String messageToReviewer = (EMAIL_MESSAGE_REVIEWER+
						(isMergeInvolved? REVERT_LINK_PIECE : "")).replace("USER_FULLNAME", fullName).replace("PRODUCT_TITLE", bean.getTitle()).
						replace("LINK_RECORD", bean.getUrl()).replace("LINK", revertUrl);


				String subject = "Update request on GRSF Record";

				// append report
				if(bean.getReport() != null){
					messageToEditor += ADD_REPORT.replace("REPORT_UPDATE", bean.getReport());
					messageToReviewer += ADD_REPORT.replace("REPORT_UPDATE", bean.getReport());
				}

				messageToEditor = messageToEditor.replace("<br>", "\n");
				messageToReviewer = messageToReviewer.replace("<br>", "\n");

				// send email to the editor
				logger.info("The message that is going to be send to the editor is\n" + messageToEditor);
				HttpPost postRequest = new HttpPost(basePath + SOCIAL_SEND_EMAIL + "?gcube-token=" + applicationToken);
				JSONObject reqMessage = new JSONObject();
				reqMessage.put("subject", subject);
				reqMessage.put("body", messageToEditor);
				JSONArray recipients = new JSONArray();
				JSONObject recipient = new JSONObject();
				recipient.put("id", username);
				recipients.add(recipient);
				reqMessage.put("recipients", recipients);
				StringEntity input = new StringEntity(reqMessage.toJSONString());
				input.setContentType(MEDIATYPE_JSON);
				postRequest.setEntity(input);

				logger.debug("Whole editor message is going to be " + reqMessage.toJSONString());

				CloseableHttpResponse response = client.execute(postRequest);

				Map<String, Object> mapResponseWritePost = getResponseEntityAsJSON(response);

				if (response.getStatusLine().getStatusCode() != 201){
					logger.error("Failed to send message to editor : HTTP error code : "
							+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));
				}

				// send email to the reviewers
				logger.info("The message that is going to be send to the reviewers is\n" + messageToReviewer);
				postRequest = new HttpPost(basePath + SOCIAL_SEND_EMAIL + "?gcube-token=" + applicationToken);
				reqMessage = new JSONObject();
				reqMessage.put("subject", subject);
				reqMessage.put("body", messageToReviewer);
				recipients = new JSONArray();
				for(String reviewer: reviewers){
					JSONObject recip = new JSONObject();
					recip.put("id", reviewer);
					recipients.add(recip);
				}
				reqMessage.put("recipients", recipients);
				input = new StringEntity(reqMessage.toJSONString());
				input.setContentType(MEDIATYPE_JSON);
				postRequest.setEntity(input);

				logger.debug("Whole reviewers message is going to be " + reqMessage.toJSONString());

				response = client.execute(postRequest);
				mapResponseWritePost = getResponseEntityAsJSON(response);

				if (response.getStatusLine().getStatusCode() != 201){
					logger.error("Failed to send message to reviewers : HTTP error code : "
							+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));
				}

			}catch(Exception e){
				logger.error("Failed to send messages", e);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static void sendEmailAdministratorsOnOperationReverted(
			String serviceUrl,
			RevertableOperationInfo rInfo,
			long groupId
			) throws Exception {

		// get the list of GRSF Reviewers to alert them as well
		RoleManager roleManager = new LiferayRoleManager();
		long teamRoleId = roleManager.getTeam(groupId, Constants.GRSF_CATALOGUE_REVIEWER_ROLE).getTeamId();
		List<GCubeUser> reviewersGcube = new LiferayUserManager().listUsersByTeam(teamRoleId);
		logger.debug("Reviewers are " + reviewersGcube);

		List<String> reviewers = new ArrayList<String>(reviewersGcube.size());

		for(GCubeUser gU: reviewersGcube){
			// if the user is a reviewer, then send the email just once
			if(!gU.getUsername().equals(rInfo.getUserNameOriginalAdmin()))
				reviewers.add(gU.getUsername());
		}

		logger.info("List of " + Constants.GRSF_CATALOGUE_REVIEWER_ROLE + " is " + reviewers);

		// discover service endpoint for the social networking library
		String currentScope = ScopeProvider.instance.get();
		String tokenUser = SecurityTokenProvider.instance.get();

		logger.info("Current scope for writeProductPost is " + currentScope + " and token is " + tokenUser.substring(0, 10) + "***************");
		String basePath = serviceUrl;

		if(basePath == null){

			logger.error("Unable to write a post because there is no social networking service available");
			throw new Exception("Unable to discover the social networking service");

		}else{

			basePath = basePath.endsWith("/") ? basePath : basePath + "/";

			try(CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();){

				String applicationToken = requireApplicationToken(serviceUrl);

				String messageToEditor = EMAIL_EDITOR_REVERT.replace("RECORD_URL", rInfo.getRecordUrl()).replace("ORIGINAL_USER", rInfo.getFullNameOriginalAdmin()).
						replace("ADMIN_WHO_CHANGED", rInfo.getFullNameCurrentAdmin());
				String messageToReviewer = EMAIL_REVIEWER_REVERT.replace("ADMIN_WHO_CHANGED", rInfo.getFullNameCurrentAdmin()).replace("RECORD_URL", rInfo.getRecordUrl()).
						replace("ORIGINAL_USER", rInfo.getFullNameOriginalAdmin());
				String subject = "Revert merge request on GRSF Record";

				messageToEditor = messageToEditor.replace("<br>", "\n");
				messageToReviewer = messageToReviewer.replace("<br>", "\n");

				// send email to the editor
				logger.info("The message that is going to be send to the editor is\n" + messageToEditor);
				HttpPost postRequest = new HttpPost(basePath + SOCIAL_SEND_EMAIL + "?gcube-token=" + applicationToken);
				JSONObject reqMessage = new JSONObject();
				reqMessage.put("subject", subject);
				reqMessage.put("body", messageToEditor);
				JSONArray recipients = new JSONArray();
				JSONObject recipient = new JSONObject();
				recipient.put("id", rInfo.getUserNameOriginalAdmin());
				recipients.add(recipient);
				reqMessage.put("recipients", recipients);
				StringEntity input = new StringEntity(reqMessage.toJSONString());
				input.setContentType(MEDIATYPE_JSON);
				postRequest.setEntity(input);
				CloseableHttpResponse response = client.execute(postRequest);

				Map<String, Object> mapResponseWritePost = getResponseEntityAsJSON(response);

				if (response.getStatusLine().getStatusCode() != 201){
					logger.error("Failed to send message to editor : HTTP error code : "
							+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));
				}

				// send email to the reviewers
				logger.info("The message that is going to be send to the reviewers is\n" + messageToReviewer);
				postRequest = new HttpPost(basePath + SOCIAL_SEND_EMAIL + "?gcube-token=" + applicationToken);
				reqMessage = new JSONObject();
				reqMessage.put("subject", subject);
				reqMessage.put("body", messageToReviewer);
				recipients = new JSONArray();
				for(String reviewer: reviewers){
					JSONObject recip = new JSONObject();
					recip.put("id", reviewer);
					recipients.add(recip);
				}
				reqMessage.put("recipients", recipients);
				input = new StringEntity(reqMessage.toJSONString());
				input.setContentType(MEDIATYPE_JSON);
				postRequest.setEntity(input);
				response = client.execute(postRequest);
				mapResponseWritePost = getResponseEntityAsJSON(response);

				if (response.getStatusLine().getStatusCode() != 201){
					logger.error("Failed to send message to editor : HTTP error code : "
							+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));
				}

			}catch(Exception e){
				logger.error("Failed to send messages", e);
				throw new Exception(e);
			}
		}

	}

	/**
	 * Create the url to be send for reverting the operation
	 * @param httpSession 
	 * @return
	 * @throws Exception 
	 */
	public static String getEncodedUrlManage(RevertableOperations operation, String administrator, long timestamp, String uuid, String clientCurrenturl) throws Exception{
		logger.info("Request for revert link. Client current url is " + clientCurrenturl);
		RevertOperationUrl operationUrl = new RevertOperationUrl(clientCurrenturl, administrator, timestamp, uuid, operation);
		String shortUrl = operationUrl.getShortUrl();
		return shortUrl;
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

}
