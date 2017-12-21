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
import org.gcube.dataanalysis.dataminer.poolmanager.util.exception.EMailException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.json.JSONArray;
import org.json.JSONObject;


public class SendMail  {

	public SendMail() {
		// TODO Auto-generated constructor stub
	}

	public void sendNotification(String subject, String body) throws EMailException {

		//AnalysisLogger.getLogger().debug("Emailing System->Starting request of email in scope " + ScopeProvider.instance.get());

		//String serviceAddress = InfraRetrieval.findEmailingSystemAddress(ScopeProvider.instance.get());

		
		
		String serviceAddress = this.getSocialService();
		
		
		if (!serviceAddress.endsWith("/"))
			serviceAddress = serviceAddress + "/";

		String requestForMessage = serviceAddress + "messages/writeMessageToUsers" + "?gcube-token="
				+ SecurityTokenProvider.instance.get();
		requestForMessage = requestForMessage.replace("http://", "https://").replace(":80", ""); 

		//AnalysisLogger.getLogger().debug("Emailing System->Request url is going to be " + requestForMessage);

		// put the sender, the recipients, subject and body of the mail here
		
		try
		{
			subject = URLEncoder.encode(subject, "UTF-8");
			body = URLEncoder.encode(body, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new EMailException(e);
		}

		String requestParameters = "&sender=dataminer&recipients=" + this.getAdmins() + "&subject=" + subject + "&body="
				+ body;

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
		System.out.println(entry.getClientInfo().getId());
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
		query.addCondition("$resource/Profile/ServiceName/text() eq 'SocialNetworking'");
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> resources = client.submit(query);
		String a = resources.get(0).profile().endpointMap().get("jersey-servlet").uri().toString();
		return a;
	}
	
	
	
	public String sendPostRequest(String endpoint, String requestParameters) {

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

			// Output the response
			return answer.toString();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	
	
	public String getAdminRoles() throws Exception{
 
		// discover social gcore endpoint
 
		//GcoreEndpointReader ep = new GcoreEndpointReader(ScopeProvider.instance.get());
 
		
		String serviceAddress = this.getSocialService();
		//String serviceAddress = InfraRetrieval.findEmailingSystemAddress(ScopeProvider.instance.get());
		
		
		//String serviceAddress = ep.getResourceEntyName();//"https://socialnetworking1.d4science.org/social-networking-library-ws/rest/";
		serviceAddress = serviceAddress.endsWith("/") ? serviceAddress : serviceAddress + "/";
		serviceAddress+=  "2/users/get-usernames-by-role?";
		serviceAddress+= "role-name=DataMiner-Manager" + "&gcube-token=" + SecurityTokenProvider.instance.get();
 
		System.out.println("Url is " + serviceAddress);
 
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet getReq = new HttpGet(serviceAddress);
		getReq.setHeader("accept", "application/json");
		getReq.setHeader("content-type", "application/json");
		System.out.println(EntityUtils.toString(client.execute(getReq).getEntity()));
		
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
		catch(Exception a){return "ciro.formisano, lucio.lelii, roberto.cirillo, gianpaolo.coro, giancarlo.panichi, scarponi"; }
		
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
