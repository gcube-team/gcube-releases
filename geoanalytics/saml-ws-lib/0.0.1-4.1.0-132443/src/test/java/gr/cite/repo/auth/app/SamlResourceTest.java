package gr.cite.repo.auth.app;

import static org.fest.assertions.api.Assertions.assertThat;
import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.entities.SamlResponseFactory;
import gr.cite.repo.auth.app.entities.SampleSamlApp;
import gr.cite.repo.auth.app.utils.LocationResolver;
import gr.cite.repo.auth.saml.messages.SamlResponse;
import io.dropwizard.testing.junit.ConfigOverride;

import java.io.File;
import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.representation.Form;

public class SamlResourceTest {
	
	static ConfigOverride co1;
	static ConfigOverride co2;
	
	static {
		try {
			File pkeyFile = File.createTempFile("test-pkeyFile", "saml").getAbsoluteFile();
			File certFile = File.createTempFile("test-certFile", "saml").getAbsoluteFile();
			Files.write("private key".getBytes(), pkeyFile);
			Files.write("".getBytes(), certFile);
			co1 = ConfigOverride.config("security.privateKeyFilename", pkeyFile.getAbsolutePath());
			co2 = ConfigOverride.config("security.certificateFilename", certFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static class SamlResourceTestModule extends AbstractModule {
		
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
				
				SamlResponse mockSamlResponse = Mockito
						.mock(SamlResponse.class);
				Mockito.doReturn(
						ImmutableMap.<String, Object>of
							("cn", "Mocked Username", "mail", "Mocked mail")
						).when(mockSamlResponse).getAttributes();
				
				
				SamlResponseFactory samlResponseFactory = Mockito
						.mock(SamlResponseFactory.class);
				Mockito.when(
						samlResponseFactory.create(
								Matchers.any(byte[].class),
								Matchers.anyString())).thenReturn(
						mockSamlResponse);
				
				return samlResponseFactory;
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
		@Override
		protected void configure() {
		}
	}
	
	public static class MockedSampleSamlApp extends SampleSamlApp {
		final SamlResourceTestModule module = new SamlResourceTestModule();
		
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
	public static final DropwizardSecureAppRule<SamlSecurityConfiguration> MOCKED_APP = new DropwizardSecureAppRule<SamlSecurityConfiguration>(
			MockedSampleSamlApp.class, "src/test/resources/test-saml-config-distributed-session.yaml", co1, co2);
	

	
	@Test
	public void consumerShouldRedirectIfResponseValidAndRelayStateGiven(){
		Client client = Client.create();
		
		
		Form form = new Form();
		form.add("SAMLResponse", "");
		form.add("RelayState", "www.example.com");
		
		String url = String.format("http://localhost:%d/saml/consumer", MOCKED_APP.getLocalPort());
		
		client.setFollowRedirects(false);
		
		ClientResponse response = client
				.resource(url)
				.post(ClientResponse.class, form);
		
		assertThat(response.getStatus()).isEqualTo(303);
	}
	
	
	@Test
	public void consumerShouldNotRedirectIfResponseValidAndRelayStateNotGiven(){
		Client client = Client.create();
		
		
		Form form = new Form();
		form.add("SAMLResponse", "");
		
		String url = String.format("http://localhost:%d/saml/consumer", MOCKED_APP.getLocalPort());
		
		client.setFollowRedirects(false);
		
		ClientResponse response = client
				.resource(url)
				.post(ClientResponse.class, form);
		
		assertThat(response.getStatus()).isEqualTo(200);
	}
	
	@Test
	public void sendRequestShouldRedirect(){
		Client client = Client.create();
		
		String url = String.format("http://localhost:%d/saml/sendLoginRequest", MOCKED_APP.getLocalPort());
		
		client.setFollowRedirects(false);
		
		ClientResponse response = client
				.resource(url)
				.queryParam("target", "www.example.com")
				.get(ClientResponse.class);
		
		assertThat(response.getStatus()).isEqualTo(303);
	}
	
	@Test
	public void metadataSimpleTest(){
		Client client = Client.create();
		String url = String.format("http://localhost:%d/saml/metadata", MOCKED_APP.getLocalPort());
		
		ClientResponse response = client
				.resource(url)
				.get(ClientResponse.class);
		
		assertThat(response.getStatus()).isEqualTo(200);
		
		String expectedMetadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" entityID=\"http://localhost:9180/saml/metadata\"><md:SPSSODescriptor protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\"><md:KeyDescriptor use=\"signing\"><ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:X509Data><ds:X509Certificate/></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:KeyDescriptor use=\"encryption\"><ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:X509Data><ds:X509Certificate/></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:SingleLogoutService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"http://localhost:9180/saml/logoutConsumer\"/><md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"http://localhost:9180/saml/consumer\" index=\"0\" isDefault=\"true\"/></md:SPSSODescriptor></md:EntityDescriptor>";
		
		assertThat(response.getEntity(String.class)).isEqualTo(expectedMetadata);
	}
}
