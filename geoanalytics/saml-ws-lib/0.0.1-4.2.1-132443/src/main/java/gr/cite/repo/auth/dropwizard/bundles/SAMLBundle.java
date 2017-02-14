package gr.cite.repo.auth.dropwizard.bundles;

import gr.cite.repo.auth.app.SecureAppHelpers;
import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.config.Security;
import gr.cite.repo.auth.app.entities.SamlResourceFactory;
import gr.cite.repo.auth.app.resources.SAMLResource;
import gr.cite.repo.auth.dropwizard.commands.SecureServerCommand;
import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

import com.google.inject.Module;
import com.hubspot.dropwizard.guice.GuiceBundle;

public class SAMLBundle<T extends SamlSecurityConfiguration> implements
		ConfiguredBundle<T> {

	final Application<T> app;
	final Class<T> clazz;
	final Module module;
	private GuiceBundle<T> guiceBundle;

	public SAMLBundle(Application<T> app, Class<T> clazz, Module module) {
		this.app = app;
		this.clazz = clazz;
		this.module = module;
	}
	
	@Override
	public void run(T configuration, Environment environment) throws Exception {
		if (SecureAppHelpers.hasSessionManager(configuration.getSessionManager())){
			SamlResourceFactory samlResourceFactory = guiceBundle.getInjector().getInstance(SamlResourceFactory.class);
			
			Security conf = configuration.getSecurity();
			
			SAMLResource samlResource =
					samlResourceFactory.create(
							conf.getSpHost(),
							conf.getIdpMetadataLocation(),
							conf.getPrivateKeyFilename(),
							conf.getCertificateFilename(),
							conf.getInvalidateLocalSessionOnSamlError(),
							conf.getTryRenewSessionOnLogout(),
							conf.getBulkLogout());

			environment.jersey().register(samlResource);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(Bootstrap<?> bootstrap) {
		guiceBundle = GuiceBundle
				.<T> newBuilder()
				.addModule(module)
				.setConfigClass(clazz).build();
		
		bootstrap.addBundle((ConfiguredBundle)guiceBundle);
		
		bootstrap.addCommand(getSecureServerCommand());
		
		bootstrap.addBundle(new ViewBundle());
		
	}
	
	protected SecureServerCommand<T> getSecureServerCommand(){
		return new SecureServerCommand<T>(this.app);
	}

}
