package org.gcube.informationsystem.resource_checker.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.resource_checker.ResourceCheckerPluginDeclaration;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send a notification to the interested person or people.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("unchecked")
public class SendNotification extends PluginStateNotification {

	private Map<String, String> pluginInputs;
	private static Logger logger = LoggerFactory.getLogger(SendNotification.class);

	// JOB STATUS NOTIFICATIONS
	private static final String NOTIFY_METHOD_JOB_STATUS = "2/notifications/notify-job-status";

	// FETCH USERS HAVING ROLE METHOD
	private static final String USERS_METHOD_HAVING_ROLE = "2/users/get-usernames-by-global-role";

	// SEND MESSAGE TO USERS
	private static final String NOTIFY_METHOD_MESSAGE = "2/messages/write-message";

	// SNL INFORMATIONs
	private static final String RESOURE_SN = "jersey-servlet";
	private static final String SERVICE_NAME_SN = "SocialNetworking";
	private static final String SERVICE_CLASSE_SN = "Portal";

	// user to contact on exception via social-networking ws
	public final static String RECIPIENT_KEY = "recipient";

	// service name on job notifications
	private final static String SERVICE_NAME = "Smart-Executor";

	public SendNotification(Map<String, String> inputs) {
		super(inputs);
		this.pluginInputs = inputs;
	}

	@Override
	/**
	 * This is sent on failures.
	 */
	public void pluginStateEvolution(PluginStateEvolution pluginStateEvolution, Exception exception) throws Exception {

		switch(pluginStateEvolution.getPluginState()){

		case STOPPED:
		case FAILED:
		case DISCARDED:

			// check what happened
			String recipient = pluginInputs.get(RECIPIENT_KEY);
			String basePath = discoverEndPoint();
			logger.info("Recipient of the notification is " + recipient + ". Base path found for the notification service is " + basePath);

			if(basePath != null && recipient != null){

				basePath = basePath.endsWith("/") ? basePath : basePath + "/";
				basePath += NOTIFY_METHOD_JOB_STATUS + "?gcube-token=" + SecurityTokenProvider.instance.get();
				basePath = basePath.trim();

				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
					JSONObject obj = new JSONObject();
					obj.put("job_id", pluginStateEvolution.getUuid().toString());
					obj.put("recipient", recipient);
					obj.put("job_name", pluginStateEvolution.getPluginDeclaration().getName());
					obj.put("service_name", SERVICE_NAME);
					obj.put("status", "FAILED");
					obj.put("status_message", "original status reported by " + SERVICE_NAME + " was " + pluginStateEvolution.getPluginState() 
							+ ". Exception is " + exception != null ? exception.getMessage() : null);
					//					}
					logger.debug("Request json is going to be " + obj.toString());

					HttpResponse response = performRequestPost(httpClient, basePath, obj.toString());

					logger.info(response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());

					int status = response.getStatusLine().getStatusCode();

					if(status == HttpURLConnection.HTTP_OK){
						logger.info("Notification sent");
					}
					else if(status == HttpURLConnection.HTTP_MOVED_TEMP
							|| status == HttpURLConnection.HTTP_MOVED_PERM
							|| status == HttpURLConnection.HTTP_SEE_OTHER){

						// redirect -> fetch new location
						Header[] locations = response.getHeaders("Location");
						Header lastLocation = locations[locations.length - 1];
						String realLocation = lastLocation.getValue();
						logger.info("New location is " + realLocation);

						response = performRequestPost(httpClient, realLocation, obj.toString());

						logger.info(" " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());
					}else
						logger.warn(" " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());
				}catch (Exception e) {
					logger.warn("Something failed when trying to notify the user", e);
				}
			}else
				logger.error("No notification is going to be sent because social service is missing or recipient is not specified");
			break;
		default: 
			logger.info("No notification is going to be sent because the status of the plugin execution is " + pluginStateEvolution.getPluginState().name());
		}
	}

	/**
	 * Send a message to the portal administrators via social-networking-service
	 * @param missingResourcesPerContext
	 * @param otherFailures 
	 * @param role 
	 */
	public static void sendMessage(Map<String, List<BasicFunctionalityBean>> missingResourcesPerContext, String otherFailures, String role) {

		String basePath = discoverEndPoint();

		logger.debug("Sending notification to users having role = " + role + " using social service at path " + basePath);

		if(basePath != null && role != null){

			basePath = basePath.endsWith("/") ? basePath : basePath + "/";
			basePath = basePath.trim();
			String fetchUsersPath = basePath + USERS_METHOD_HAVING_ROLE + "?gcube-token=" + SecurityTokenProvider.instance.get() + "&role-name=" + role;
			String sendMessagePath = basePath + NOTIFY_METHOD_MESSAGE + "?gcube-token=" + SecurityTokenProvider.instance.get();

			// fetch these users
			try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()){

				HttpResponse response = performRequestGet(httpClient, fetchUsersPath);
				int status = response.getStatusLine().getStatusCode();

				logger.debug("Status code is " + status + " and response message is " + response.getStatusLine().getReasonPhrase());

				if(status == HttpURLConnection.HTTP_OK){

					String resultUsers = EntityUtils.toString(response.getEntity());
					JSONParser parser = new JSONParser();
					JSONObject resultJson = (JSONObject) parser.parse(resultUsers);
					JSONArray array = (JSONArray)resultJson.get("result");
					logger.info("list of users to notify is " + array);
					
					if(array.isEmpty()){
						logger.warn("No one has role " + role + " .. exiting");
						return;
					}

					JSONArray arrayRecipients = new JSONArray();
					
					for (int i = 0; i < array.size(); i++) {
						String user = (String) array.get(i);
						JSONObject objR = new JSONObject();
						objR.put("id", user);
						arrayRecipients.add(objR);
					}

					SimpleDateFormat sdf = new SimpleDateFormat();
					sdf.applyPattern("dd/MM/yy HH:mm:ss");
					String dataStr = sdf.format(new Date());

					StringBuilder sb = new StringBuilder();
					sb.append("Dear ");
					sb.append(role).append(",").append("\n");
					sb.append("this is the report of the last running of the " + ResourceCheckerPluginDeclaration.NAME.toUpperCase() + " (" + dataStr + ").\nThe following missing resources have been found:\n");

					Iterator<Entry<String, List<BasicFunctionalityBean>>> iterator = missingResourcesPerContext.entrySet().iterator();
					
					if(!iterator.hasNext())
						sb.append("none.");
					
					while (iterator.hasNext()) {
						Map.Entry<java.lang.String, java.util.List<org.gcube.informationsystem.resource_checker.utils.BasicFunctionalityBean>> entry = (Map.Entry<java.lang.String, java.util.List<org.gcube.informationsystem.resource_checker.utils.BasicFunctionalityBean>>) iterator
								.next();
						List<BasicFunctionalityBean> list = entry.getValue();
						for (BasicFunctionalityBean basicFunctionalityBean : list) {
							sb.append("- resource's name ");
							sb.append(basicFunctionalityBean.getName());
							sb.append(" and resource's class/category ");
							sb.append(basicFunctionalityBean.getCategory());
							sb.append(" (in context " + entry.getKey() + ")");
						}
						if(iterator.hasNext())
							sb.append(";\n");
						else
							sb.append(".\n");

					}
					sb.append("\n\n");
					sb.append(otherFailures + "\n");
					sb.append("Best,\n" + ResourceCheckerPluginDeclaration.NAME);

					String message = sb.toString();
					logger.debug("Going to send this message \n" + message);

					// send message
					JSONObject obj = new JSONObject();
					obj.put("body", message);
					obj.put("subject", "Resource Checker SE - Report (" + dataStr + ")");
					obj.put("recipients", arrayRecipients);				
					logger.debug("Request json is going to be " + obj.toString());
					response = performRequestPost(httpClient, sendMessagePath, obj.toString());
					logger.info(response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());
					status = response.getStatusLine().getStatusCode();
					if(status == HttpURLConnection.HTTP_OK){
						logger.info("Message sent");
					}
					else if(status == HttpURLConnection.HTTP_MOVED_TEMP
							|| status == HttpURLConnection.HTTP_MOVED_PERM
							|| status == HttpURLConnection.HTTP_SEE_OTHER){

						// redirect -> fetch new location
						Header[] locations = response.getHeaders("Location");
						Header lastLocation = locations[locations.length - 1];
						String realLocation = lastLocation.getValue();
						logger.info("New location is " + realLocation);

						response = performRequestPost(httpClient, realLocation, obj.toString());
					}
				}
			}catch (Exception e) {
				logger.warn("Failed to notify users...", e);
			}

		}

	}

	/**
	 * Perform the post/json request
	 * @param httpClient
	 * @param path
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpResponse performRequestPost(CloseableHttpClient httpClient, String path, String params) throws ClientProtocolException, IOException{

		HttpPost request = new HttpPost(path);
		StringEntity paramsEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
		request.setEntity(paramsEntity);
		return httpClient.execute(request);

	}

	/**
	 * Perform the get request
	 * @param httpClient
	 * @param path
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpResponse performRequestGet(CloseableHttpClient httpClient, String path) throws ClientProtocolException, IOException{

		HttpGet request = new HttpGet(path);
		return httpClient.execute(request);

	}

	/**
	 * Discover the social networking service base path.
	 * @return base path of the service.
	 */
	private static String discoverEndPoint(){
		String context = ScopeProvider.instance.get();
		String basePath = null;
		try{

			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",SERVICE_CLASSE_SN));
			query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",SERVICE_NAME_SN));
			query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+RESOURE_SN+"\"]/text()");

			DiscoveryClient<String> client = client();
			List<String> endpoints = client.submit(query);
			if (endpoints == null || endpoints.isEmpty()) 
				throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+SERVICE_NAME_SN +", serviceClass: " +SERVICE_CLASSE_SN +", in scope: "+context);

			basePath = endpoints.get(0);
			if(basePath==null)
				throw new Exception("Endpoint:"+RESOURE_SN+", is null for serviceName: "+SERVICE_NAME_SN +", serviceClass: " +SERVICE_CLASSE_SN +", in scope: "+context);

			logger.info("found entryname "+basePath+" for ckanResource: "+RESOURE_SN);

		}catch(Exception e){
			logger.error("Unable to retrieve such service endpoint information!", e);
		}

		return basePath;
	}

}
