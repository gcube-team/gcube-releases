package org.gcube.portal.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.social.networking.liferay.ws.LiferayJSONWsCredentials;
import org.gcube.portal.social.networking.ws.inputs.MessageInputBean;
import org.gcube.portal.social.networking.ws.inputs.Recipient;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JTests {

	private static final String YOUR_TOKEN_HERE = "";
	private static final String METHOD = "messages/writeMessageToUsers";
	private static final String SCOPE = "/gcube";

	//@Test
	public void readSocialServiceEndPoint() throws Exception {

		String findInContext = SCOPE;
		ScopeProvider.instance.set(findInContext);

		ServiceEndPointReaderSocial readerSE = new ServiceEndPointReaderSocial(findInContext);
		System.out.println("Found base path " + readerSE.getBasePath());

	}


	//@Test
	public void testWithApacheClient() throws Exception {

		ServiceEndPointReaderSocial reader = new ServiceEndPointReaderSocial(SCOPE);
		String requestForMessage = reader.getBasePath() + METHOD + "?gcube-token=" + YOUR_TOKEN_HERE;
		requestForMessage = requestForMessage.replace("http", "https"); // remove the port (or set it to 443) otherwise you get an SSL error

		System.out.println("Request url is going to be " + requestForMessage);

		try(CloseableHttpClient client = HttpClientBuilder.create().build();){

			HttpPost postRequest = new HttpPost(requestForMessage);

			// put the sender, the recipients, subject and body of the mail here
			StringEntity input = new StringEntity("sender=andrea.rossi&recipients=gianpaolo.coro&subject=Sample mail&body=Sample mail object");
			input.setContentType("application/x-www-form-urlencoded");
			postRequest.setEntity(input);

			HttpResponse response = client.execute(postRequest);

			System.out.println("Error is " + response.getStatusLine().getReasonPhrase());

			if (response.getStatusLine().getStatusCode() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			System.out.println(response.toString());

		}catch(Exception e){
			System.err.println("error while performing post method " + e.toString());
		}
	}

	//@Test
	public void parserJSON() throws IOException{

		MessageInputBean message = new MessageInputBean();
		message.setBody("a caso");
		message.setSubject("subject");
		ArrayList<Recipient> recipients = new ArrayList<Recipient>();
		Recipient recipient = new Recipient("recipient1");
		recipients.add(recipient);
		message.setRecipients(recipients);


		//Object mapper instance
		ObjectMapper mapper = new ObjectMapper();

		//Convert POJO to JSON
		String json = mapper.writeValueAsString(message);

		MessageInputBean obje = mapper.readValue(json, MessageInputBean.class);
		System.out.println(json);
		System.out.println(obje);

	}

	//@Test
	public void callLiferayWS() throws Exception{

		HttpHost target = new HttpHost("localhost", 8080, "http");

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
				new AuthScope(target.getHostName(), target.getPort()),
				new UsernamePasswordCredentials("test@liferay.com", "random321"));
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider).build();
		try {

			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();
			// Generate BASIC scheme object and add it to the local
			// auth cache
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(target, basicAuth);

			// Add AuthCache to the execution context
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			HttpGet httpget = new HttpGet("/api/jsonws" + "/user/get-user-by-screen-name/company-id/20155/screen-name/costantino.perciante");

			System.out.println("Executing request " + httpget.getRequestLine() + " to target " + target);
			CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				System.out.println(EntityUtils.toString(response.getEntity()));
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

	}

	//@Test
	public void retrieveCredentials(){

		ScopeProvider.instance.set("/gcube");
		LiferayJSONWsCredentials cred = LiferayJSONWsCredentials.getSingleton();
		System.out.println("Password is " + cred.getPassword());
		System.out.println("Host is " + cred.getHost());

	}

	//@Test
	public void readGcoreEndPoint() throws Exception{

		GcoreEndpointReader reader = new GcoreEndpointReader("/gcube");
		reader.getResourceEntyName();

	}

	//@Test
	public void sendNotification() throws ClientProtocolException, IOException{

		String url ="https://socialnetworking-d-d4s.d4science.org/social-networking-library-ws/rest//2/notifications/notify-job-status?gcube-token=07f5f961-d0e0-4bc4-af90-a305e8b63ac7-98187548";
		CloseableHttpClient client = HttpClientBuilder.create().build();
		JSONObject obj = new JSONObject();
		obj.put("job_id", "bbbbb");
		obj.put("recipient", "costantino.perciante");
		obj.put("job_name", "aaaaaa");
		obj.put("service_name", "Test");
		obj.put("status", "SUCCEEDED");
		
		HttpPost request = new HttpPost(url);
		request.addHeader("Content-type", ContentType.APPLICATION_JSON.toString());
		StringEntity paramsEntity = new StringEntity(obj.toJSONString(), ContentType.APPLICATION_JSON);
		request.setEntity(paramsEntity);
		client.execute(request);

	}

}
