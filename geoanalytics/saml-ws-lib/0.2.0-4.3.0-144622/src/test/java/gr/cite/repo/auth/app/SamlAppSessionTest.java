package gr.cite.repo.auth.app;


import static org.fest.assertions.api.Assertions.assertThat;
import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;

import javax.ws.rs.core.Cookie;

import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public abstract class SamlAppSessionTest {
	
	public abstract DropwizardSecureAppRule<SamlSecurityConfiguration> getRule();
	
	@Test
	public void getForbiddenIfAccessProtectedResourceWithoutValidSession() {
		Client client = new Client();
		
		client.setFollowRedirects(false);

		ClientResponse response = client.resource(
					String.format("http://localhost:%d/protected/ping",getRule().getLocalPort()))
					.get(ClientResponse.class);

		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}
	
	
	@Test
	public void accessProtectedResourceWithValidSessionShouldReturnTheResource() {
		
		Client client = new Client();
		
		ClientResponse sessionValidateResponse = client.resource(
				String.format("http://localhost:%d/sessionvalidation/validate",getRule().getLocalPort()))
				.get(ClientResponse.class);
		
		
		assertThat(sessionValidateResponse.getStatus()).isEqualTo(200);
		assertThat(sessionValidateResponse.getCookies().size() > 0);
		
		Cookie securityCookie = sessionValidateResponse.getCookies().get(0);
		
		client.setFollowRedirects(false);
		
		ClientResponse response = client.resource(
					String.format("http://localhost:%d/protected/ping",getRule().getLocalPort()))
					.cookie(securityCookie)
					.get(ClientResponse.class);

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getEntity(String.class)).isEqualTo("pong");
	}
	
	
	@Test
	public void getForbiddenIfAccessProtectedResourceAfterLogout() {
		
		Client client = new Client();
		
		ClientResponse sessionValidateResponse = client.resource(
				String.format("http://localhost:%d/sessionvalidation/validate",getRule().getLocalPort()))
				.get(ClientResponse.class);
		
		
		assertThat(sessionValidateResponse.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(sessionValidateResponse.getCookies().size() > 0);
		
		Cookie securityCookie = sessionValidateResponse.getCookies().get(0);
		
		ClientResponse response = client.resource(
					String.format("http://localhost:%d/protected/ping",getRule().getLocalPort()))
					.cookie(securityCookie)
					.get(ClientResponse.class);

		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getEntity(String.class)).isEqualTo("pong");
		
		
		
		ClientResponse sessionInvalidateResponse = client.resource(
				String.format("http://localhost:%d/sessionvalidation/invalidate",getRule().getLocalPort()))
				.cookie(securityCookie)
				.get(ClientResponse.class);
		
		
		assertThat(sessionInvalidateResponse.getStatus()).isEqualTo(Status.OK.getStatusCode());
		
		client.setFollowRedirects(false);
		ClientResponse newResponse = client.resource(
				String.format("http://localhost:%d/protected/ping",getRule().getLocalPort()))
				.cookie(securityCookie)
				.get(ClientResponse.class);

		assertThat(newResponse.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}
}
