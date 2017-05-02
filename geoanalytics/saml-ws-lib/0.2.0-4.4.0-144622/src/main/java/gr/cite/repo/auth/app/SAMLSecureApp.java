package gr.cite.repo.auth.app;

import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.utils.LocationResolver;
import gr.cite.repo.auth.app.utils.UrlLocationResolver;
import gr.cite.repo.auth.dropwizard.bundles.SAMLBundle;
import gr.cite.repo.auth.dropwizard.commands.SecureServerCommand;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.hubspot.dropwizard.guice.GuiceBundle;

public abstract class SAMLSecureApp<T extends SamlSecurityConfiguration>
		extends Application<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SAMLSecureApp.class);
	
	private boolean isSecure = false;
	
	public boolean isSecure(){
		return this.isSecure;
	}
	
	public void runSec(String[] args) throws Exception{
		this.isSecure = this.checkIfSecurityDeclared(args);
		LOGGER.info("is application running in secure mode ? " + this.isSecure);
		this.run(args);
	}
	
	@Override
	public void initialize(Bootstrap<T> bootstrap) {
		LOGGER.debug("Initializing SAMLSecureApp...");
		if (this.isSecure()) {
			bootstrap.addBundle(createSAMLBundle());
		} else {
			bootstrap.addBundle(createSimpleBundle());
		}
		LOGGER.debug("Initialized SAMLSecureApp");
	}

	public boolean checkIfSecurityDeclared(String[] args){
		return (args[0].equals(SecureServerCommand.SECURE_SERVER_COMMAND));
	}

	public abstract Class<T> getClazz();

	public abstract Module module();

	public abstract Module moduleWithSession();

	public SAMLBundle<T> createSAMLBundle() {
		SAMLBundle<T> samlBundle = new SAMLBundle<T>(this, getClazz(),
				Modules.override(defaultModule).with(moduleWithSession()));

		return samlBundle;
	}

	public GuiceBundle<T> createSimpleBundle() {
		GuiceBundle<T> guiceBundle = GuiceBundle.<T> newBuilder()
				.addModule(Modules.override(defaultModule).with(module()))
				.setConfigClass(getClazz())
				.build();

		return guiceBundle;
	}

	
	private Module defaultModule = new AbstractModule() {
		@Override
		protected void configure() {
			bind(LocationResolver.class).to(UrlLocationResolver.class);
		}
	};
}