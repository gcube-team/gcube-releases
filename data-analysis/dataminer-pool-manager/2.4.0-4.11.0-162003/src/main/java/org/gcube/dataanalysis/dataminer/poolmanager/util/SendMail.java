package org.gcube.dataanalysis.dataminer.poolmanager.util;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.configuration.DMPMClientConfiguratorManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.EMailException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendMail  {

	private Logger logger;
	private final String 	WRITE_MESSAGE_ADDRESS_PATH ="messages/writeMessageToUsers?gcube-token=",
							USER_ROLES_ADDRESS_PATH ="2/users/get-usernames-by-role?role-name=DataMiner-Manager&gcube-token=",
							ENCODING = "UTF-8",
							SENDER_PARAMETER_FORMAT = "&sender=dataminer&recipients=%s&subject=%s&body=%s",
							SOCIAL_SERVICE_QUERY_CONDITION ="$resource/Profile/ServiceName/text() eq 'SocialNetworking'",
							SOCIAL_SERVICE_URI="jersey-servlet",
							JSON_MIME_TYPE = "application/json";
	
	public SendMail() {
		this.logger = LoggerFactory.getLogger(SendMail.class);
	}

	private String getRequestMessage (String addressPath)
	{
		String serviceAddress = this.getSocialService();
		StringBuilder requestMessageBuilder = new StringBuilder(serviceAddress);
		
		if (!serviceAddress.endsWith("/")) requestMessageBuilder.append('/');
		
		requestMessageBuilder.append(addressPath).append(SecurityTokenProvider.instance.get());
		String requestForMessage = requestMessageBuilder.toString();
		this.logger.debug("Request "+requestForMessage);
		return requestForMessage;
	}
	
	public void sendNotification(String subject, String body) throws EMailException 
	{
		this.logger.debug("Sending mail notification for "+subject);
		this.logger.debug("Body "+body);

		


		//AnalysisLogger.getLogger().debug("Emailing System->Request url is going to be " + requestForMessage);

		// put the sender, the recipients, subject and body of the mail here
		
		try
		{
			subject = URLEncoder.encode(subject, ENCODING);
			body = URLEncoder.encode(body, ENCODING);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EMailException(e);
		}

		String requestForMessage = getRequestMessage(WRITE_MESSAGE_ADDRESS_PATH);
		requestForMessage = requestForMessage.replace("http://", "https://").replace(":80", ""); 
		
		String requestParameters = String.format(SENDER_PARAMETER_FORMAT, this.getAdmins(), subject , body);

		
		String response = this.sendPostRequest(requestForMessage, requestParameters);
		//AnalysisLogger.getLogger().debug("Emailing System->Emailing response OK ");
		
		if (response == null) throw new EMailException();
		
	}

	// public void notifySubmitter(String a, String b) throws Exception {
	// NotificationHelper nh = new NotificationHelper();
	// super.sendNotification(nh.getSubject(),
	// nh.getBody());
	// }

	public String username(String token) throws ObjectNotFound, Exception {
		AuthorizationEntry entry = authorizationService().get(token);
		this.logger.debug(entry.getClientInfo().getId());
		return entry.getClientInfo().getId();
	}

//	public String retrieveAdminRole() throws Exception {
//		String serviceAddress = InfraRetrieval.findEmailingSystemAddress(ScopeProvider.instance.get());
//
//		if (!serviceAddress.endsWith("/"))
//			serviceAddress = serviceAddress + "/";
//
//		String requestForMessage = serviceAddress + "2/users/get-usernames-by-global-role";
//		requestForMessage = requestForMessage.replace("https://", "http://").replace(":80", ""); 
//		String requestParameters = "&role-name=Administrator" + "&gcube-token=" + SecurityTokenProvider.instance.get();
//				
//		String response = HttpRequest.sendGetRequest(requestForMessage, requestParameters);
//		System.out.println(response.toString());
//
//		if (response == null) {
//			Exception e = new Exception("Error in querying the recipient");
//			throw e;
//		}
//		return response;
//
//	}
	
	public String getSocialService() {
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition(SOCIAL_SERVICE_QUERY_CONDITION);
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> resources = client.submit(query);
		String a = resources.get(0).profile().endpointMap().get(SOCIAL_SERVICE_URI).uri().toString();
		return a;
	}
	
	
	
	public String sendPostRequest(String endpoint, String requestParameters) {

		this.logger.debug("Sending post request");
		// Build parameter string
		String data = requestParameters;
		try {

			// Send the request
			URL url = new URL(endpoint);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			// write parameters
			writer.write(data);
			writer.flush();

			// Get the response
			StringBuffer answer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				answer.append(line);
			}
			writer.close();
			reader.close();

			this.logger.debug("Operation completed");
			String response = answer.toString(); 
			this.logger.debug("Response "+response);
			// Output the response
			return response;

		} catch (MalformedURLException ex) {
			this.logger.error("Invalid URL",ex);
		} catch (IOException ex) {
			
			this.logger.error("Error in the IO process",ex);
		}
		return null;
	}
	
	
	
	
	public String getAdminRoles() throws Exception{
 
		// discover social gcore endpoint
 
		//GcoreEndpointReader ep = new GcoreEndpointReader(ScopeProvider.instance.get());
 
		
		String serviceAddress = getRequestMessage(USER_ROLES_ADDRESS_PATH);
		//String serviceAddress = InfraRetrieval.findEmailingSystemAddress(ScopeProvider.instance.get());
		
		
		//String serviceAddress = ep.getResourceEntyName();//"https://socialnetworking1.d4science.org/social-networking-library-ws/rest/";
//		serviceAddress = serviceAddress.endsWith("/") ? serviceAddress : serviceAddress + "/";
//		serviceAddress+=  "2/users/get-usernames-by-role?role-name=DataMiner-Manager&gcube-token=" + SecurityTokenProvider.instance.get();
 
		this.logger.debug("Admin roles url is " + serviceAddress);
 
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet getReq = new HttpGet(serviceAddress);
		getReq.setHeader("accept", JSON_MIME_TYPE);
		getReq.setHeader("content-type", JSON_MIME_TYPE);
		this.logger.info(EntityUtils.toString(client.execute(getReq).getEntity()));
		
		return EntityUtils.toString(client.execute(getReq).getEntity());
 
	}
	
	

	public String getAdmins(){
		try{
		List<String> s = new LinkedList<String>();
		JSONObject obj = new JSONObject(this.getAdminRoles());
		JSONArray data = obj.getJSONArray("result");
		if (data != null) {
			String[] names = new String[data.length()];
			for (int i = 0; i < data.length(); i++) {
				names[i] = data.getString(i);
				s.add(names[i]);
			}
			s.add(this.username(SecurityTokenProvider.instance.get()));
		}
		return s.toString().replace("[", "").replace("]", "");
		}
		catch(Exception a){return DMPMClientConfiguratorManager.getInstance().getDefaultAdmins(); }
		
		}
		
		
		
	
//	public String getRootToken() throws Exception {
//
//		//ApplicationContext ctx = ContextProvider.get(); // get this info from
//														// SmartGears
//		//System.out.println(ctx.container().configuration().infrastructure());
//		String a = "";
//		SimpleQuery query2 = queryFor(ServiceEndpoint.class);
//		query2.addCondition("$resource/Profile/Name/text() eq 'SAIService'").setResult("$resource");
//				
//		DiscoveryClient<ServiceEndpoint> client2 = clientFor(ServiceEndpoint.class);
//		List<ServiceEndpoint> df = client2.submit(query2);
//
//		for (ServiceEndpoint b : df) {	
//			a = StringEncrypter.getEncrypter().decrypt(b.profile().accessPoints().iterator().next().password());
//			}
//		return a;
//	}
	

	


	public static void main(String[] args) throws Exception {

	//ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab");
	//SecurityTokenProvider.instance.set("3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462");

		//ScopeProvider.instance.set("/gcube/devNext");
		//SecurityTokenProvider.instance.set("aa6eec71-fe07-43ab-bd1c-f03df293e430-98187548");

		//NotificationHelper nh = new NotificationHelper();

		SendMail sm = new SendMail();
		
		//ScopeProvider.instance.set("/gcube/devNext/NextNext");
		//SecurityTokenProvider.instance.set("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");
		//System.out.println(sm.getRootToken());
		//sm.getGenericResourceByName("");
		
		ScopeProvider.instance.set("/gcube/preprod/preVRE");
		SecurityTokenProvider.instance.set("2eceaf27-0e22-4dbe-8075-e09eff199bf9-98187548");
		
		
		//sm.sendNotification(nh.getFailedSubject(), nh.getFailedBody("test failed"));
		// sm.username(SecurityTokenProvider.instance.get());
		//sm.retrieveAdminRole();
		//sm.getAdminRoles();
		System.out.println(sm.getAdmins());
		//System.out.println(sm.getAdmins());
		//sm.sendNotification("test", "test");
		//System.out.println(sm.getSocialService());
	}
}
