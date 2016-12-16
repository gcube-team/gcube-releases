/**
 * 
 */
package org.gcube.common.mycontainer;

import static java.lang.System.*;
import static org.apache.commons.io.FileUtils.*;
import static org.gcube.common.core.contexts.GHNContext.Status.*;
import static org.gcube.common.mycontainer.Utils.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.gcube.common.core.contexts.GHNContext;
import org.globus.wsrf.container.ServiceContainer;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.utils.AnyHelper;

/**
 * An embedded gCore container for integration testing.
 * <p>
 * The container is partially embedded, in that it requires an installation on the local file system. It can be
 * configured with the following properties:
 * 
 * <ul>
 * <li> {@link Utils#CONTAINER_LOCATION_PROPERTY}: the path to the local installation of the container. For test
 * automation, the container installation should be treated as a test resource and its path ought to be relative to the
 * working directory..
 * <li> {@link Utils#PORT_PROPERTY}: the port at which the container will be running. The default port is
 * {@link Utils#DEFAULT_PORT}.
 * <li> {@link Utils#STARTUP_TIMEOUT_PROPERTY}: the time in millisecond within which a started container must reach a
 * ready or certified state (depending on weather services have been deployed in it). The default startup timeout is
 * {@link Utils#DEFAULT_STARTUP_TIMEOUT}.
 * </ul>
 * 
 * <b>Note</b>: since the container relies on statics, starting it and stopping it multiple times within the same
 * runtime may have undesirable effects. For test isolation and reproducibility, the container is best started once per
 * JVM, typically for the execution of a single test suite. IDEs typically fork JVMs per test suites, but build systems
 * such as Ant or Maven may have to be explicitly configured for this.
 * 
 * 
 * @author Fabio Simeoni
 * 
 */
public class MyContainer {

	private static Logger logger = Logger.getLogger(MyContainer.class);

	// factors out the deployment implementation strategy
	private GarDeployer deployer;

	boolean isRunning;
	File location;
	int port;
	long startupTimeout;
	Map<String, Gar> gars = new HashMap<String, Gar>();

	// the underlying Globus container
	ServiceContainer globus;

	/**
	 * 
	 * Creates an instance configured with:
	 * 
	 * <ul>
	 * <li>the properties found in a {@link Utils#PROPERTY_FILE}
	 * <li>zero or more {@link Gar}s
	 * </ul>
	 * 
	 * Note that:
	 * <p>
	 * 
	 * <ul>
	 * <li>the search for the {@link Utils#PROPERTY_FILE} is delegated to {@link Utils#findContainerProperties()}
	 * <li>the Gars are deployed when the container is started. Gars which are already deployed in the container are
	 * re-deployed only if they have changed since. Changes are computed based on the last modification date of their
	 * assets.
	 * </ul>
	 * 
	 * 
	 * @param gars the Gars
	 * 
	 * @throws IllegalStateException if the {@link Utils#PROPERTY_FILE} cannot be found
	 * @throws IllegalArgumentException if the {@link Utils#PROPERTY_FILE} is found but contains invalid properties
	 */
	public MyContainer(Gar... gars) throws IllegalStateException, IllegalArgumentException {
		this(new Properties(), true, gars);
	}

	/**
	 * 
	 * Creates an instance configured:
	 * 
	 * <ul>
	 * <li>a given location
	 * <li>default properties
	 * <li>zero or more {@link Gar}s
	 * </ul>
	 * 
	 * Note that the Gars are deployed when the container is started. Gars which are already deployed in the container
	 * are re-deployed only if they have changed since. Changes are computed based on the last modification date of
	 * their assets. </ul>
	 * 
	 * @param location the location of the container's installation.
	 * @param gars the Gars
	 * 
	 * @throws IllegalStateException if the {@link Utils#PROPERTY_FILE} cannot be found
	 * @throws IllegalArgumentException if the {@link Utils#PROPERTY_FILE} is found but contains invalid properties
	 */
	public MyContainer(String location, Gar... gars) throws IllegalStateException, IllegalArgumentException {
		this(properties(location), true, gars);
	}

	// helps to follow chain of constructors
	private static Properties properties(String location) {
		Properties props = new Properties();
		props.put(CONTAINER_LOCATION_PROPERTY, location);
		return props;
	}

	/**
	 * Creates an instance configured with:
	 * 
	 * <ul>
	 * <li>given properties
	 * <li>zero or more {@link Gar}s
	 * </ul>
	 * 
	 * Note that the Gars are deployed when the container is started. Gars which are already deployed in the container
	 * are re-deployed only if they have changed since. Changes are computed based on the last modification date of
	 * their assets. </ul>
	 * 
	 * @param properties the properties
	 * @param gars the Gars
	 * @throws IllegalArgumentException if the the properties are invalid.
	 */
	public MyContainer(Properties properties, Gar... gars) throws IllegalArgumentException {
		this(properties, false, gars);
	}

	/**
	 * An overload of {@link #MyContainer(Properties, Gar...)} where the configuration properties passed in input can
	 * augment or override those in a {@link Utils#PROPERTY_FILE}.
	 * 
	 * @param properties the properties
	 * @param merge <code>true</code> if the properties are to augment or override those in a
	 *            {@link Utils#PROPERTY_FILE}. The discovery of {@link Utils#PROPERTY_FILE} is delegated to
	 *            {@link Utils#findContainerProperties()}
	 * @param gars the Gars.
	 * 
	 * @throws IllegalArgumentException if the properties are invalid
	 * @throws IllegalStateException if the properties cannot be merged because {@link Utils#PROPERTY_FILE} cannot be
	 *             found
	 */
	public MyContainer(Properties properties, boolean merge, Gar... gars) throws IllegalArgumentException,
			IllegalStateException {

		if (merge)
			try {
				Properties common = findContainerProperties();
				common.putAll(properties);
				properties = common;
			} catch (IllegalStateException e) {
				System.out.println("not using " + PROPERTY_FILE + " as none was found on the classpath");
			}

		// configure location
		String path = (String) properties.get(CONTAINER_LOCATION_PROPERTY);

		if (path == null)
			path = DEFAULT_INSTALL_DIRECTORY;

		try {
			location = new File((String) path).getCanonicalFile();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		checkDir(location);

		// sets GLOBUS_LOCATION for container code that expects it
		System.setProperty(GLOBUS_LOCATION, location.getAbsolutePath());
		try {
			addToEnvironment(location);
		} catch (Exception e) {
			throw new RuntimeException("could not add GLOBUS_LOCATION to environment ", e);
		}

		// load logging configuration immediately
		// configure container's logging
		File logConfig = new File(location, "container-log4j.properties");
		if (logConfig.exists())
			try {
				PropertyConfigurator.configure(logConfig.toURI().toURL());
			} catch (Exception e) {
				logger.warn("could not configure logging from " + logConfig);
			}

		StringWriter report = new StringWriter();
		properties.list(new PrintWriter(report));
		logger.trace("configuring container in " + location + "\n" + report);

		// configure port
		String portprop = (String) properties.get(PORT_PROPERTY);
		port = portprop == null ? DEFAULT_PORT : Integer.valueOf(portprop);

		// configure startup timeout
		String timeout = (String) properties.get(STARTUP_TIMEOUT_PROPERTY);
		startupTimeout = timeout == null ? DEFAULT_STARTUP_TIMEOUT : Long.valueOf(timeout);

		// check storage location
		File storageLocation = storageLocation();
		checkDir(storageLocation);
		System.setProperty(STORAGE_LOCATION_PROPERTY, storageLocation.getAbsolutePath());

		deployer = new GarDeployer(this);

		for (Gar gar : gars)
			this.gars.put(gar.id(), gar);
	}

	/**
	 * Returns the location of the container's installation.
	 * 
	 * @return the location
	 */
	public File location() {
		return location;
	}

	/**
	 * Returns the container's storage folder.
	 * 
	 * @return the folder
	 */
	public File storageLocation() {
		return new File(location, STORAGE_DIR);
	}

	/**
	 * Returns the container's configuration folder.
	 * 
	 * @return the folder
	 */
	public File configLocation() {
		return new File(location, CONFIG_DIR);
	}

	/**
	 * Returns the container's deployment folders.
	 * 
	 * @return the folder
	 */
	public File deploymentsLocation() {
		return new File(location, ETC_DIR);
	}

	/**
	 * Returns the container's library folder.
	 * 
	 * @return the folder
	 */
	public File libLocation() {
		return new File(location, LIB_DIR);
	}

	/**
	 * Returns the implementation of service deployed in the container with <code>application</code>
	 * scope.
	 * 
	 * @param <T> the type of the implementation
	 * @param name the WSDD name of the service
	 * @param clazz the runtime representation of the implementation type
	 * 
	 * @throws IllegalStateException if the container is not running or a service with the WSDD name
	 *             is not running in the container
	 * @throws ClassCastException if the port-type implementation cannot be cast to the given type
	 */
	public <T> T endpoint(String name, Class<T> clazz) throws IllegalStateException {

		checkIsRunning();

		Object instance = globus.getEngine().getApplicationSession().get(name);

		if (instance == null)
			throw new IllegalStateException("unknown port-type " + name);

		return clazz.cast(instance);

	}

	public void setEndpoint(String name, Object endpoint) throws IllegalStateException {

		checkIsRunning();

		globus.getEngine().getApplicationSession().set(name, endpoint);

	}

	/**
	 * Returns the address of a service running in the container started at the default port.
	 * 
	 * @param wsddName the WSDD name of the service
	 * @return the address
	 * @throws IllegalStateException if the container is not running or a service with a the given WSDD name
	 *             is not running in the container
	 */
	public URI address(String wsddName) throws IllegalStateException {

		return address(wsddName,DEFAULT_PORT);
	}
	
	/**
	 * Returns the address of a service running in a container started at a given port.
	 * 
	 * @param name the WSDD name of the service
	 * @param port the port
	 * @return the address
	 * @throws IllegalStateException if the container is not running or a service with a the given WSDD name
	 *             is not running in the container
	 */
	public URI address(String name, int port) throws IllegalStateException {

		checkIsRunning();

		// check if port-type exists
		endpoint(name, Object.class);

		return URI.create("http://localhost:" + port + "/wsrf/services/" + name);
	}
	
	/**
	 * Returns a reference to a service running in the container on the default port.
	 * 
	 * @param name the WSDD name of the service
	 * @return the reference
	 * @throws IllegalStateException if the container is not running or a service with the given WSDD name
	 *             is not running in the container
	 */
	public EndpointReferenceType reference(String name) throws IllegalStateException {
		return reference(name, port);

	}

	
	/**
	 * Returns a reference to a service running in the container started at a given port.
	 * 
	 * @param name the WSDD name of the service
	 * @param port the port
	 * @return the reference
	 * @throws IllegalStateException if the container is not running or a service with the given WSDD name
	 *             is not running in the container
	 */
	public EndpointReferenceType reference(String name, int port) throws IllegalStateException {

		checkIsRunning();

		// check if port-type exists
		endpoint(name, Object.class);

		try {
			AttributedURI uri = new AttributedURI(address(name).toString());
			EndpointReferenceType epr = new EndpointReferenceType();
			epr.setAddress(uri);
			return epr;
		} catch (MalformedURIException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a reference to an instance of a service running in the container started at the default port.
	 * 
	 * @param name the WSDD name of the service
	 * @param key the namespace of the service
	 * @param key the key of the service instance
	 * @return the reference
	 * @throws IllegalStateException if the container is not running or a service with a given WSDD name
	 *             is not running in the container
	 */
	public EndpointReferenceType reference(String name, String namespace, String key) throws IllegalStateException {
		return reference(name, namespace, key, port);

	}

	/**
	 * Returns a reference to an instance of a service running in the container started at a given port.
	 * 
	 * @param name the WSDD name of the service
	 * @param namespace the namespace of the service
	 * @param key the key of the service instance
	 * @param port the port
	 * @return the reference
	 * @throws IllegalStateException if the container is not running or a service with the given WSDD name
	 *             is not running in the container
	 */
	public EndpointReferenceType reference(String name, String namespace, String key, int port)
			throws IllegalStateException {

		EndpointReferenceType epr = reference(name, port);

		SimpleResourceKey resourceKey = new SimpleResourceKey(new QName(namespace, "ResourceKey"), key);
		ReferencePropertiesType props = new ReferencePropertiesType();
		try {
			AnyHelper.setAny(props, resourceKey.toSOAPElement());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		epr.setProperties(props);

		return epr;
	}

	/** {@inheritDoc} */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Starts the container, deploying the GARs with which it was configured.
	 */
	public void start() {

		if (isRunning)
			return;

		logger.trace("starting container in " + location);

		try {

			// cleans state
			cleanState();

			deployGars();

			// start container in a dedicated child classloader whose classpath
			// includes
			// the container's location and the jars in the lib directory of the
			// installation
			URL[] classpath = getClassPath();
			URLClassLoader loader = new URLClassLoader(classpath, MyContainer.class.getClassLoader());

			// sets classloader on thread, for clients who use it to load
			// classes dynamically
			// (e.g. for JNDI first and foremost)
			Thread.currentThread().setContextClassLoader(loader);

			// start container reflectively in new classloader
			Class<?> containerClass = loader.loadClass(ServiceContainer.class.getName());
			Method createMethod = containerClass.getMethod("createContainer", Map.class);

			// configure globus
			Map<Object, Object> props = new HashMap<Object, Object>();
			props.put(ServiceContainer.PORT, port);
			props.put(ServiceContainer.MAIN_THREAD, true);
			globus = (ServiceContainer) createMethod.invoke(containerClass, props);

			// blocks until gHN has completed startup, successfully or
			// unsuccessfully, or the timeout has expired
			GHNContext ghn = GHNContext.getContext();

			Long start = currentTimeMillis();
			while (ghn.getStatus() != CERTIFIED) {

				Thread.sleep(200);

				GHNContext.Status status = ghn.getStatus();

				// exit on failures
				if (status == DOWN || status == FAILED)
					throw new RuntimeException("could not start container successfully");

				boolean noServices = globus.getEngine().getApplicationSession().getKeys() == null;

				// no point waiting timeout if no service has been deployed
				if (status == READY && noServices)
					break;

				// enforce timeout
				if (((currentTimeMillis() - start) > startupTimeout))
					throw new RuntimeException("could not start container in given time interval");

			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			stop();
			throw e;
		} catch (Throwable t) {
			Throwable cause = t.getCause();
			if (cause != null) {
				Throwable innerCause = cause.getCause();
				if (innerCause != null)
					innerCause.printStackTrace();
				else
					cause.printStackTrace();
			} else
				t.printStackTrace();
			stop();
			throw new RuntimeException("could not start the container", t);
		}

		isRunning = true;
	}

	// helper
	private void deployGars() {

		File undeploymentsDir = new File(deploymentsLocation(), "globus_packages");
		File[] currentDeployments = undeploymentsDir.exists() ? undeploymentsDir.listFiles() : new File[0];

		// undeploys previously deployed Gars that are not to be redeployed
		for (File deployment : currentDeployments)
			if (!gars.containsKey(deployment.getName()))
				deployer.undeploy(deployment.getName());

		outer: for (Gar gar : gars.values()) {
			for (File deployedGar : currentDeployments)
				// re-deploys only if newer
				if (deployedGar.getName().equals(gar.id()) && lastModified(deployedGar) > gar.lastModified()) {
					logger.info("skipping deployment of " + gar.id() + " because it is unchanged");
					continue outer;
				}
			logger.info("deploying Gar " + gar.id());
			deployer.deploy(gar);
		}
	}

	/**
	 * Stops the container.
	 */
	public void stop() {

		if (!isRunning)
			return;

		try {
			globus.stop();
			logger.trace("stopped container in " + location());
		} catch (Throwable e) {
			logger.warn("could not stop container properly", e);
		} finally {

			isRunning = false;

		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	// helper
	// this is a serious hack to deal with legacy code, we are reflectively
	// changing the
	// unmodifiable map that contains the env vars in Java
	private void addToEnvironment(File location) throws Exception {
		Class<?>[] classes = Collections.class.getDeclaredClasses();
		Map<String, String> env = System.getenv();
		for (Class<?> cl : classes) {
			if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
				Field field = cl.getDeclaredField("m");
				field.setAccessible(true);
				Map<String, String> map = (Map) field.get(env);
				map.put(GLOBUS_LOCATION, location.getAbsolutePath());
			}
		}
	}

	// helper
	private URL[] getClassPath() throws Exception {

		List<URL> urls = new ArrayList<URL>();

		urls.add(location.toURI().toURL());

		File libs = libLocation();
		checkDir(libs);

		try {
			FilenameFilter filter = new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return (name.endsWith(".jar"));
				}
			};
			File[] jars = libs.listFiles(filter);
			for (int i = 0; i < jars.length; i++)
				urls.add(jars[i].toURI().toURL());

		} catch (IOException e) {
			throw new RuntimeException("Error during startup processing", e);
		}

		return urls.toArray(new URL[0]);
	}

	// helper
	public void cleanState() {

		File defaultConfigLocation = new File(location, "defaultconfig");

		checkDir(defaultConfigLocation);

		try {
			copyDirectory(defaultConfigLocation, configLocation());
		} catch (Exception e) {
			logger.error("could not restore default configuration");
		}

		try {
			if (storageLocation().exists()) {
				cleanDirectory(storageLocation());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		logger.trace("cleaned container state");

	}

	// helper
	private void checkIsRunning() throws IllegalStateException {
		if (!isRunning)
			throw new IllegalStateException("container is not running");
	}
}
