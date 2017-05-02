package org.gcube.socialnetworking.socialdataindexer.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.PluginStateNotification;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send a notification to the interested person.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SendNotification extends PluginStateNotification {

	private Map<String, String> pluginInputs;
	private static Logger logger = LoggerFactory.getLogger(SendNotification.class);
	private static final String NOTIFY_METHOD = "2/notifications/notify-job-status";
	private static final String resource = "jersey-servlet";
	private static final String serviceName = "SocialNetworking";
	private static final String serviceClass = "Portal";

	// user to contact on exception via social-networking ws
	private final static String RECIPIENT_KEY = "recipient";

	// service name
	private final static String SERVICE_NAME = "Smart-Executor";

	public SendNotification(Map<String, String> inputs) {
		super(inputs);
		this.pluginInputs = inputs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void pluginStateEvolution(PluginStateEvolution pluginStateEvolution,
			Exception exception) throws Exception {

		switch(pluginStateEvolution.getPluginState()){

		//		case DONE:
		case STOPPED:
		case FAILED:
		case DISCARDED:

			// check what happened
			String recipient = pluginInputs.get(RECIPIENT_KEY);
			String basePath = discoverEndPoint();
			logger.info("Recipient of the notification is " + recipient + ". Base path found for the notification service is " + basePath);

			if(basePath != null && recipient != null){

				basePath = basePath.endsWith("/") ? basePath : basePath + "/";
				basePath += NOTIFY_METHOD + "?gcube-token=" + SecurityTokenProvider.instance.get();
				basePath = basePath.trim();

				try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()){
					JSONObject obj = new JSONObject();
					obj.put("job_id", pluginStateEvolution.getUuid().toString());
					obj.put("recipient", recipient);
					obj.put("job_name", pluginStateEvolution.getPluginDeclaration().getName());
					obj.put("service_name", SERVICE_NAME);
					//					if(pluginStateEvolution.getPluginState().equals(PluginState.DONE))
					//						obj.put("status", "SUCCEEDED");
					//					else{
					obj.put("status", "FAILED");
					obj.put("status_message", "original status reported by " + SERVICE_NAME + " was " + pluginStateEvolution.getPluginState() 
							+ ". Exception is " + exception != null ? exception.getMessage() : null);
					//					}
					logger.debug("Request json is going to be " + obj.toJSONString());

					HttpResponse response = performRequest(httpClient, basePath, obj.toJSONString());

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

						response = performRequest(httpClient, realLocation, obj.toJSONString());

						logger.info(" " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());
					}else
						logger.warn(" " + response.getStatusLine().getStatusCode() + " and response message is " + response.getStatusLine().getReasonPhrase());
				}catch (Exception e) {
					logger.warn("Something failed when trying to notify the user", e);
				}
			}
			break;
		default: logger.info("No notification is going to be sent, because the status of the plugin execution is " + pluginStateEvolution.getPluginState().name());
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
	private static HttpResponse performRequest(CloseableHttpClient httpClient, String path, String params) throws ClientProtocolException, IOException{

		HttpPost request = new HttpPost(path);
		StringEntity paramsEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
		request.setEntity(paramsEntity);
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

			logger.info("found entryname "+basePath+" for ckanResource: "+resource);

		}catch(Exception e){
			logger.error("Unable to retrieve such service endpoint information!", e);
		}

		return basePath;
	}

}
