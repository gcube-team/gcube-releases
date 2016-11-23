package gr.cite.repo.auth.app;

import static org.fest.assertions.api.Assertions.assertThat;
import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.entities.SamlResponseFactory;
import gr.cite.repo.auth.app.entities.SampleSamlApp;
import gr.cite.repo.auth.app.utils.LocationResolver;
import gr.cite.repo.auth.saml.messages.SamlResponse;

import java.io.File;

import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.representation.Form;

public class SamlResourceInvalidTest {

	static class SamlResourceInvalidTestModule extends AbstractModule {

		@Provides
		LocationResolver locationResolver(){
			LocationResolver locationResolver = Mockito.mock(LocationResolver.class);
			
			try {
				String xml = Files.toString(new File(
						"src/test/resources/idp-metadata.xml"), Charsets.UTF_8);
				Mockito.doReturn(xml).when(locationResolver).getContents(Matchers.anyString());
				
				return locationResolver;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
		@Provides
		SamlResponseFactory samlResponseFactory(){
			try {
				SamlResponse mockSamlInvalidResponse = Mockito
						.mock(SamlResponse.class);
				Mockito.doThrow(
						new Exception("This response is invalid on purpose"))
						.when(mockSamlInvalidResponse).validate();

				SamlResponseFactory samlInvalidResponseFactory2 = Mockito
						.mock(SamlResponseFactory.class);
				Mockito.when(
						samlInvalidResponseFactory2.create(
								Matchers.any(byte[].class),
								Matchers.anyString())).thenReturn(
						mockSamlInvalidResponse);
				
				return samlInvalidResponseFactory2;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
		@Override
		protected void configure() {
		}
	}

	public static class MockedSampleSamlAppWithInvalidResponses extends
			SampleSamlApp {

		final SamlResourceInvalidTestModule module = new SamlResourceInvalidTestModule();

		@Override
		public AbstractModule module() {
			return module;
		}

		@Override
		public AbstractModule moduleWithSession() {
			return module;
		}
	}

	@ClassRule
	public static final DropwizardSecureAppRule<SamlSecurityConfiguration> MOCKED_APP_INVALID_RESP = new DropwizardSecureAppRule<SamlSecurityConfiguration>(
			MockedSampleSamlAppWithInvalidResponses.class,
			"src/test/resources/test-saml-config-distributed-session.yaml");

	@Test
	public void consumerShouldReturnErrorWhenResponseInvalid() {
		Client client = Client.create();

		Form form = new Form();
		form.add("SAMLResponse", "");

		String url = String.format("http://localhost:%d/saml/consumer",
				MOCKED_APP_INVALID_RESP.getLocalPort());

		ClientResponse response = client.resource(url).post(
				ClientResponse.class, form);

		assertThat(response.getStatus()).isEqualTo(500);
	}

}
