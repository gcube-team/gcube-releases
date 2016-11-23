package gr.cite.repo.auth.app.entities;

import java.io.File;

import org.mockito.Matchers;
import org.mockito.Mockito;

import gr.cite.repo.auth.app.SAMLSecureApp;
import gr.cite.repo.auth.app.SecureAppHelpers;
import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.resources.ProtectedResource;
import gr.cite.repo.auth.app.resources.SessionValidationResource;
import gr.cite.repo.auth.app.utils.LocationResolver;
import io.dropwizard.setup.Environment;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class SampleSamlApp extends SAMLSecureApp<SamlSecurityConfiguration> {

	public SecureAppHelpers secureAppHelper;

	public static void main(String[] args) throws Exception {
		if (args.length < 2)
			args = new String[] { "secureserver",
					"src/test/resources/test-saml-config-simple-session.yaml" };

		SampleSamlApp app = new SampleSamlApp();
		app.runSec(args);
	}

	@Override
	public String getName() {
		return "test-saml";
	}

	@Override
	public void run(SamlSecurityConfiguration configuration,
			Environment environment) throws Exception {

		environment.jersey().register(SessionValidationResource.class);
		environment.jersey().register(ProtectedResource.class);
		
	}

//	protected SamlResourceFactory samlResourceFactoryProvider() {
//		return SamlResourceFactory.defaultFactory();
//	}

	@Override
	public Class<SamlSecurityConfiguration> getClazz() {
		return SamlSecurityConfiguration.class;
	}

	@Override
	public AbstractModule module() {
		return new SampleAppModule();
	}

	@Override
	public AbstractModule moduleWithSession() {
		return new SampleAppModule();
	}
	
	@Override
	public boolean isSecure() {
		return true;
	}
	
	
	static class SampleAppModule extends AbstractModule{
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

		@Override
		protected void configure() {
			// TODO Auto-generated method stub
			
		}
	} 
}
