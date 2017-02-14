package org.gcube.portal.test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.social.networking.ws.inputs.MessageInputBean;
import org.gcube.portal.social.networking.ws.inputs.Recipient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

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
	public void testWithJerseyClient() throws Exception {

		ServiceEndPointReaderSocial reader = new ServiceEndPointReaderSocial(SCOPE);
		String requestForMessage = reader.getBasePath() + METHOD + "?gcube-token=" + YOUR_TOKEN_HERE;

		Client client = Client.create();
		client.setFollowRedirects(true);
		WebResource webResource = client.resource(requestForMessage);

		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("recipients", "costantino.perciante");
		formData.add("subject", "Sample mail");
		formData.add("body", "Sample mail object");

		webResource.addFilter(new RedirectFilter());
		ClientResponse response = webResource.entity(formData).post(ClientResponse.class);

		System.out.println(response.toString());
	}

	class RedirectFilter extends ClientFilter {

		@Override
		public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
			ClientHandler ch = getNext();
			ClientResponse resp = ch.handle(cr);

			// try location
			String redirectTarget = resp.getHeaders().getFirst("Location");
			//System.out.println("Location is "  + redirectTarget);
			cr.setURI(UriBuilder.fromUri(redirectTarget).build());
			return ch.handle(cr);

		}
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
		message.setSender("sender a");
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

}
