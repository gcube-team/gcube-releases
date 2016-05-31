package app;

import static org.gcube.smartgears.Constants.*;
import static utils.TestUtils.*;

import java.io.File;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.configuration.Mode;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.ApplicationExtensions;
import org.gcube.smartgears.configuration.application.ApplicationHandlers;
import org.gcube.smartgears.configuration.application.DefaultApplicationConfiguration;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.configuration.container.Site;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.managers.ContainerManager;
import org.gcube.smartgears.provider.ProviderFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

import utils.TestProvider;
import utils.TestUtils;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Simulates a single-servlet application to be transformed into a gCube resource.
 * <p>
 * <ul>
 * <li>uses a default configuration that can be customised (cf. {@link #configuration()});
 * <li>can be configured with the default handlers (cf. {@link #useDefaultHandlers()}) or custom ones (
 * {@link #handlers()}), including those that are not deployable through standard means, such as mocks (
 * {@link #bypassHandlerDeployment()};
 * <li>can be started to have a default behaviour when called, ({@link #start()}) or else a custom behaviour (
 * {@link #startWith(Runnable)};
 * </ul>
 * can be called in a default scope ({@link #call()} or in a specific scope ({@link #callIn(String)}). Calls are blocking but always
 * configured and executed in a separate thread;
 * 
 * @author Fabio Simeoni
 * 
 */
public class SomeApp {

	private final Tomcat tomcat = new Tomcat();

	private WebArchive war = defaultWar();

	private ContainerConfiguration containerConfiguration;
	private ApplicationConfiguration configuration;
	private ApplicationHandlers handlers = new ApplicationHandlers();
	private ApplicationExtensions extensions = new ApplicationExtensions();
	private TestProvider provider = new TestProvider();
	private boolean deployHandlers = true;
	private boolean deployExtensions = true;
	private boolean deployConfiguration = true;
	private boolean clean=true;
	
	public SomeApp() {

		if (ContainerManager.instance!=null)
			ContainerManager.instance.stop(true);
		
		tomcat.getConnector().setPort(0);
		tomcat.setBaseDir(location);
		
		System.setProperty(ghn_home_property,location);
						
		containerConfiguration = defaultContainerConfiguration();
		configuration = defaultConfiguration();
	}

	/**
	 * Sets a {@link TestProvider} to resolve dependencies at runtime.
	 * 
	 * @param provider the provider
	 */
	public void set(TestProvider provider) {
		this.provider = provider;
	}

	/**
	 * Runs the test with the state left by previous run.
	 * <p>
	 * Use only within a single test!
	 */
	public void dirtyRun() {
		this.clean = false;
	}
	
	/**
	 * Returns the configuration of the application.
	 * <p>
	 * The initial configuration is based on defaults, but can be changed and extended.
	 * 
	 * @return the configuration
	 */
	public ApplicationConfiguration configuration() {
		return configuration;
	}
	
	/**
	 * Returns the configuration of the containerConfiguration.
	 * <p>
	 * The initial configuration is based on defaults, but can be changed and extended.
	 * 
	 * @return the configuration
	 */
	public ContainerConfiguration containerConfiguration() {
		return containerConfiguration;
	}

	/**
	 * Returns the handlers that manage the application, none by default.
	 * 
	 * @return the handlers
	 */
	public ApplicationHandlers handlers() {
		return handlers;
	}

	/**
	 * Avoids deployment of the configured handlers in the application's WAR.
	 * <p>
	 * The handlers will instead be directly available at runtime.
	 */
	public void bypassHandlerDeployment() {

		provider.use(handlers());
		deployHandlers = false;
	}
	

	/**
	 * Avoids resource configuration deployment.
	 */
	public void asExternal() {

		configuration.context(context_root_path);
		containerConfiguration.app(configuration);

		bypassConfigurationDeployment();
		bypassExtensionsDeployment();
		bypassHandlerDeployment();
	}
	
	/**
	 * Avoids resource configuration deployment.
	 */
	public void withExternal(ApplicationConfiguration config) {

		config.context(context_root_path);
		
		containerConfiguration.app(config.context()).merge(config);
	}
	
	public void bypassConfigurationDeployment() {
		
		deployConfiguration = false;
		
	}
	
	
	public void usePublisher(ScopedPublisher publisher) {
		
		provider.use(publisher);
	}
	
	/**
	 * Uses default handlers.
	 */
	public void useDefaultHandlers() {

		deployHandlers = false;
	}

	
	/**
	 * Returns the extensions of the application, none by default.
	 * 
	 * @return the handlers
	 */
	public ApplicationExtensions extensions() {
		return extensions;
	}
	
	/**
	 * Uses default extensions.
	 */
	public void useDefaultExtensions() {

		deployExtensions = false;
	}
	
	/**
	 * Avoids deployment of the configured extensions in the application's WAR.
	 * <p>
	 * The extensions will instead be directly available at runtime.
	 */
	public void bypassExtensionsDeployment() {

		provider.use(extensions());
		deployExtensions = false;
	}

	/**
	 * Starts the application.
	 * 
	 * @return the context of the application
	 */
	public ApplicationContext start() {

		return startWith(new Runnable() {

			@Override
			public void run() {
				System.err.println("test servlet invoked with no particular task");
			}
		});
	}

	/**
	 * Starts the application, injecting test logic in its single servlet
	 * 
	 * @param test test logic
	 * @return the context of the application
	 */
	public ApplicationContext startWith(Runnable test) {

		// install provider
		ProviderFactory.testProvider(provider);
				
		if (clean)
			cleanupInstallation();

		installContainerConfiguration();
		
		if (deployConfiguration)
			deployConfiguration();

		if (deployHandlers)
			deployHandlers();
		
		if (deployExtensions)
			deployExtensions();

		try {

			System.err.println("deploying with " + war.toString(true));

			StandardContext ctx = (StandardContext) tomcat.addWebapp(context_root_path, warFile().getAbsolutePath());

			// tells tomcat to look also for exploded directories such as this project's (finds initializer)
			((StandardJarScanner) ctx.getJarScanner()).setScanAllDirectories(true);

			//this starts webapp and always comes back
			tomcat.start();
			
			ApplicationContext context = provider.context;
			
			if (context==null)
				throw new RuntimeException("app failed @ startup");
			
			Wrapper webapp = (Wrapper) tomcat.getHost().findChild(context_root_path).findChild(servlet_name);
			
			if (webapp!=null) {
					
				webapp.setServlet(new TestServlet(test));
				
				context.container().configuration().port(port());
				
				containerConfiguration = context.container().configuration();
			}

			return context;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Retuens <code>true</code> if the application was successfully start in the container.
	 * 
	 * @return <code>true</code> if the application was successfully start in the container
	 */
	public boolean isActive() {
		return tomcat.getHost().findChild("/" + context_root).findChild(servlet_name) != null;
	}
	
	/**
	 * Makes a request to the application.
	 */
	
	public String send(Request call) {
		return call.make(port()).getEntity(String.class);
	}
	
	/**
	 * Makes a request to the application.
	 */
	
	public ClientResponse httpSend(Request call) {
		return call.make(port());
	}


	/**
	 * Stops the application
	 */
	public void stop() {
		
		try {
			tomcat.stop();
			tomcat.destroy();
		
		} catch (Exception e) {
			System.err.println("WARNING: could not clearly stop container:");
			e.printStackTrace();
		}
	}

	public File containerConfigurationFile() {
		
		return new File(location,Constants.container_configuraton_file_path);
	}
	// helpers

	/**
	 * Installs the container configuration.
	 */
	private void installContainerConfiguration() {

		TestUtils.serialise(containerConfiguration(),containerConfigurationFile());

	}
	
	/**
	 * Includes the configuration in the application's WAR.
	 */
	private void deployConfiguration() {

		String xml = TestUtils.bind(configuration());

		System.err.println("deploying with configuration:\n" + xml);

		war.addAsWebResource(new StringAsset(xml), new BasicPath(configuration_file_path));
	}

	/**
	 * Includes the handlers in the application's WAR.
	 */
	private void deployHandlers() {

		String xml = TestUtils.bind(handlers());

		System.err.println("deploying with handlers:\n" + xml);

		war.addAsWebResource(new StringAsset(xml), new BasicPath(handlers_file_path));
	}
	
	/**
	 * Includes the extensions in the application's WAR.
	 */
	private void deployExtensions() {

		String xml = TestUtils.bind(extensions());

		System.err.println("deploying with extensions:\n" + xml);

		war.addAsWebResource(new StringAsset(xml), new BasicPath(extensions_file_path));
	}

	private File warFile() {

		File warFile = new File(location, "test.war");

		if (warFile.exists() && !warFile.delete())
			System.out.println("could not delete old deployment");; // seems safer than plain overwrite to avoid war corruption

		war.as(ZipExporter.class).exportTo(warFile, true);

		return warFile;

	}

	private WebArchive defaultWar() {

		WebArchive war = ShrinkWrap.create(WebArchive.class);
		war.setWebXML(new File("src/test/java/app/web.xml"));
		return war;

	}

	private ApplicationConfiguration defaultConfiguration() {

		return new DefaultApplicationConfiguration().mode(Mode.offline).serviceClass("test-class").name("test-app").version("1.0");

	}
	
	private ContainerConfiguration defaultContainerConfiguration() {

		return new ContainerConfiguration().mode(Mode.offline).hostname("localhost").port(port()).infrastructure("gcube").startVOs("devsec","devNext")
								.site(new Site().country("it").location("rome").latitude("41.9000").longitude("12.5000"))
								.property("test-prop1","foo")
								.property("test-prop2","bar")
								.publicationFrequency(5);

	}

	public int port() {
		return tomcat.getConnector().getLocalPort();	
	}
	
	private void cleanupInstallation() {
				
		System.out.println("cleaning installation in location "+location);
		
		File installation = new File(location);
		
		if (installation.exists())
			try {
				FileUtils.deleteDirectory(installation);
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		
		installation.mkdirs();
		
	}
}
