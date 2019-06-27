package org.gcube.datacatalogue.grsf_manage_widget.client;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.datacatalogue.grsf_manage_widget.server.manage.GRSFNotificationService;
import org.gcube.datacatalogue.grsf_manage_widget.server.manage.SocialCommunications;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.CloseableHttpResponse;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.methods.HttpPost;
import eu.trentorise.opendata.jackan.internal.org.apache.http.entity.StringEntity;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.CloseableHttpClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.HttpClientBuilder;
import eu.trentorise.opendata.jackan.internal.org.apache.http.impl.client.LaxRedirectStrategy;

public class SendNotification {
	
	private static String context = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab";
	
	private static String token = "57b42a99-6239-44c4-9a68-591c18363222-843339462"; ///d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab
	
	//private static String token = "8e74a17c-92f1-405a-b591-3a6090066248-98187548"; //devVRE
	
	private static String username = "francesco.mangiacrapa";
	
	private static final String MEDIATYPE_JSON = "application/json";
	
	private static final Logger logger = LoggerFactory.getLogger(SendNotification.class);

	// for discovering social networking service
	private static final String resource = "jersey-servlet";
	private static final String serviceName = "SocialNetworking";
	private static final String serviceClass = "Portal";
	
	private static final String SOCIAL_SEND_EMAIL = "2/messages/write-message";
	
	
	public static void main(String[] args){
		
		try {
			ScopeProvider.instance.set(context);
			SecurityTokenProvider.instance.set(token);
			GRSFNotificationService grsfNotificationService = new GRSFNotificationService();
			
			DataCatalogue catalogue = DataCatalogueFactory.getFactory().getUtilsPerScope(context);
			logger.info("The Catalogue in the scope {} is {}", context, catalogue.getCatalogueUrl());
			
			// require social networking url
			final String baseUrlSocial = getBaseUrlSocialService();
			
			logger.info("Base URL SOCIAL IS {}", baseUrlSocial);
	
			// and the user current browser url
			final String currentBrowserUrl = "http://127.0.0.1"; // ignore other parameters		
	
			// manage interactions through a separated thread but set there security token and context (and then reset them)
			Thread t = new Thread(new Runnable() {
	
	
	
				@Override
				public void run() {
					try{
						JSONObject message = new JSONObject();
						message.put("hello", "world");
						sendEmail(baseUrlSocial, "");
	
						//	create a post about the operation 
						//SocialCommunications.writeProductPost(baseUrlSocial, bean, username, fullName, false, currentBrowserUrl);
	
					}catch(Exception e){
						logger.error("Something failed while alerting editors/reviewers", e);
					}finally{
						ScopeProvider.instance.reset();
						SecurityTokenProvider.instance.reset();
					}
				}
			});
			t.start();
		}catch (Exception e) {
			System.err.println("Error: ");
			e.printStackTrace();
		}
		
	}
	
	private static String getBaseUrlSocialService() throws Exception{
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
		query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
		query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));
		query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+resource+"\"]/text()");

		DiscoveryClient<String> client = client();
		List<String> endpoints = client.submit(query);
		if (endpoints == null || endpoints.isEmpty()) 
			throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+context);


		String basePath = endpoints.get(0);
		if(basePath==null)
			throw new Exception("Endpoint:"+resource+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+context);
		
		return basePath;
	}
	
	
	private static void sendEmail(String basePath, String messageToEditor) {
		
		try(CloseableHttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();){
			String subject = "TEST: just a test of email notification";
			
			// send email to the editor
			logger.info("The message that is going to be send to the editor is\n" + messageToEditor);
			String postRequestURL = basePath + SOCIAL_SEND_EMAIL + "?gcube-token=" + token;
			logger.info("The post URL is {}",postRequestURL);
			HttpPost postRequest = new HttpPost(postRequestURL);
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
			
			logger.info("The response code is {} ",response.getStatusLine().getStatusCode());
	
			Map<String, Object> mapResponseWritePost = SocialCommunications.getResponseEntityAsJSON(response);
	
			if (response.getStatusLine().getStatusCode() != 201){
				logger.error("Failed to send message to editor : HTTP error code : "
						+ response.getStatusLine().getStatusCode() + mapResponseWritePost.get("message"));
			}
		}catch(Exception e){
			logger.error("Failed to send messages", e);
		}
	}
}
