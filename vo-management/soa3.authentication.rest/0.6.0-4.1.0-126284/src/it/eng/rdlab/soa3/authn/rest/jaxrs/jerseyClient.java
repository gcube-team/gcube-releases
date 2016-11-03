package it.eng.rdlab.soa3.authn.rest.jaxrs;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class jerseyClient {

	public static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/SpringLDAPAuthn/")
				.build();
	}

	public static void main(String[] args) throws JsonGenerationException,JsonMappingException,IOException 
	{
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);

		WebResource service = client.resource(getBaseURI());

		/** Test Authentication
		 Service****************************************************/
		String clearText = "test:test";
		System.out.println(new
				String(Base64.encodeBase64(clearText.getBytes())));

		System.out.println(service.path("authenticate").header("Authorization",
				"Basic "+new
				String(Base64.encodeBase64(clearText.getBytes()))).get(String.class).toString());

		/******************************************************************************/

	}
}
