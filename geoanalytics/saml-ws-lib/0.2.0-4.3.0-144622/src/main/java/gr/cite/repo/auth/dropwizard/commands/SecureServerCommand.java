package gr.cite.repo.auth.dropwizard.commands;

import gr.cite.repo.auth.app.SecureAppHelpers;
import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a application as an HTTP server.
 *
 * @param <T> the {@link Configuration} subclass which is loaded from the configuration file
 */
public class SecureServerCommand<T extends SamlSecurityConfiguration> extends EnvironmentCommand<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecureServerCommand.class);

    private final Class<T> configurationClass;
    public static final String SECURE_SERVER_COMMAND = "secureserver"; 
    
    public SecureServerCommand(Application<T> application) {
        super(application, SECURE_SERVER_COMMAND, "Runs the Dropwizard application as an HTTP server with SAML security");
        LOGGER.debug("Runs the Dropwizard application as an HTTP server with SAML security");
        this.configurationClass = application.getConfigurationClass();
    }

    /*
     * Since we don't subclass ServerCommand, we need a concrete reference to the configuration
     * class.
     */
    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(Environment environment, Namespace namespace, T configuration) throws Exception {
    	LOGGER.debug("Running SecureServerCommand...");
        final Server server = configuration.getServerFactory().build(environment);

        final SecureAppHelpers secureAppHelper = new SecureAppHelpers(environment);
        
		secureAppHelper.applySessionManager(configuration
				.getSessionManager(), server);
		
		if (SecureAppHelpers.hasSessionManager(configuration.getSessionManager()))
			secureAppHelper.applySecurity(configuration.getSecurity());
		
        try {
            server.addLifeCycleListener(new LifeCycleListener());
            cleanupAsynchronously();
            server.start();
            LOGGER.debug("Server has started...");
        } catch (Exception e) {
            LOGGER.error("Unable to start server, shutting down", e);
            server.stop();
            cleanup();
            throw e;
        }
        LOGGER.debug("SecureServerCommand has been run");
    }
    
    private class LifeCycleListener extends AbstractLifeCycle.AbstractLifeCycleListener {
        @Override
        public void lifeCycleStopped(LifeCycle event) {
            cleanup();
        }
    }
}
