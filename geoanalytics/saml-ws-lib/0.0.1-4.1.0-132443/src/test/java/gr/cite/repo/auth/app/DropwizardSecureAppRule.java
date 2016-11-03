package gr.cite.repo.auth.app;

import gr.cite.repo.auth.app.config.SamlSecurityConfiguration;
import gr.cite.repo.auth.app.entities.SamlAuthRequestFactory;
import gr.cite.repo.auth.app.entities.SamlResourceFactory;
import gr.cite.repo.auth.app.entities.SamlResponseFactory;
import gr.cite.repo.auth.dropwizard.commands.SecureServerCommand;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.ConfigOverride;

import java.util.Enumeration;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;

import net.sourceforge.argparse4j.inf.Namespace;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class DropwizardSecureAppRule <C extends SamlSecurityConfiguration> implements TestRule {

	 private final Class<? extends Application<C>> applicationClass;
	    private final String configPath;

	    private C configuration;
	    private Application<C> application;
	    private Environment environment;
	    private Server jettyServer;

	    public DropwizardSecureAppRule(Class<? extends Application<C>> applicationClass,
	                             @Nullable String configPath,
	                             ConfigOverride... configOverrides) {
	        this.applicationClass = applicationClass;
	        this.configPath = configPath;
	        for (ConfigOverride configOverride: configOverrides) {
	            configOverride.addToSystemProperties();
	        }
	    }

	    @Override
	    public Statement apply(final Statement base, Description description) {
	        return new Statement() {
	            @Override
	            public void evaluate() throws Throwable {
	                startIfRequired();
	                try {
	                    base.evaluate();
	                } finally {
	                    resetConfigOverrides();
	                    jettyServer.stop();
	                }
	            }
	        };
	    }

	    private void resetConfigOverrides() {
	        for (Enumeration<?> props = System.getProperties().propertyNames(); props.hasMoreElements();) {
	            String keyString = (String) props.nextElement();
	            if (keyString.startsWith("dw.")) {
	                System.clearProperty(keyString);
	            }
	        }
	    }
	    
	    public SecureServerCommand<C> createSecureServerCommand(Application<C> application){
	    	return new SecureServerCommand<>(application);
	    }

	    private void startIfRequired() {
	        if (jettyServer != null) {
	            return;
	        }

	        try {
	            application = newApplication();

	            final Bootstrap<C> bootstrap = new Bootstrap<C>(application) {
	                @Override
	                public void run(C configuration, Environment environment) throws Exception {
	                    environment.lifecycle().addServerLifecycleListener(new ServerLifecycleListener() {
	                                    @Override
	                                    public void serverStarted(Server server) {
	                                        jettyServer = server;
	                                    }
	                                });
	                    DropwizardSecureAppRule.this.configuration = configuration;
	                    DropwizardSecureAppRule.this.environment = environment;
	                    super.run(configuration, environment);
	                }
	            };

	            application.initialize(bootstrap);
	            final SecureServerCommand<C> command = createSecureServerCommand(application);
	            

	            ImmutableMap.Builder<String, Object> file = ImmutableMap.builder();
	            if (!Strings.isNullOrEmpty(configPath)) {
	                file.put("file", configPath);
	            }
	            final Namespace namespace = new Namespace(file.build());

	            command.run(bootstrap, namespace);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    public C getConfiguration() {
	        return configuration;
	    }

	    public int getLocalPort() {
	        return ((ServerConnector) jettyServer.getConnectors()[0]).getLocalPort();
	    }

	    public int getAdminPort() {
	        return ((ServerConnector) jettyServer.getConnectors()[1]).getLocalPort();
	    }

	    public Application<C> newApplication() {
	        try {
	            return applicationClass.newInstance();
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    @SuppressWarnings("unchecked")
	    public <A extends Application<C>> A getApplication() {
	        return (A) application;
	    }

	    public Environment getEnvironment() {
	        return environment;
	    }
}
