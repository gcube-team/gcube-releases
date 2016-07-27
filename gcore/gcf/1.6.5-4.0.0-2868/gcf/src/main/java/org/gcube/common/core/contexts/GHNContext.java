package org.gcube.common.core.contexts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.axis.description.ServiceDesc;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.digester.Digester;
import org.apache.commons.io.FileSystemUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.jmx.HierarchyDynamicMBean;
import org.apache.log4j.spi.LoggerRepository;
import org.gcube.common.core.contexts.GCUBEServiceContext.RILifetimeEvent;
import org.gcube.common.core.contexts.ghn.Builder;
import org.gcube.common.core.contexts.ghn.CredentialConsumer;
import org.gcube.common.core.contexts.ghn.CredentialRequestConsumer;
import org.gcube.common.core.contexts.ghn.GHNConsumer;
import org.gcube.common.core.contexts.ghn.Scheduler;
import org.gcube.common.core.contexts.ghn.Events.CredentialDelegationEvent;
import org.gcube.common.core.contexts.ghn.Events.CredentialPayload;
import org.gcube.common.core.contexts.ghn.Events.CredentialRequestEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNLifeTimeEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNRIRegistrationEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNTopic;
import org.gcube.common.core.contexts.ghn.Events.SecurityTopic;
import org.gcube.common.core.contexts.service.Consumer;
import org.gcube.common.core.instrumentation.GHN;
import org.gcube.common.core.monitoring.LocalMonitor;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource.InvalidScopeException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.core.security.context.SecurityContextFactory;
import org.gcube.common.core.security.context.impl.DefaultGHNServerSecurityContext;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.core.utils.proxies.GCUBEProxyFactory;
import org.gcube.common.core.utils.proxies.AccessControlProxyContext.Restricted;
import org.gcube.common.core.utils.proxies.ReadOnlyProxyContext.ReadOnly;
import org.gcube.common.handlers.GCUBEURLStreamHandlerFactory;
import org.globus.wsrf.config.ContainerConfig;
import org.globus.wsrf.container.ServiceContainer;
import org.globus.wsrf.container.ServiceContainerCollection;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.jndi.NamingContext;
import org.globus.wsrf.tools.jndi.JNDIConfigRuleSet;
import org.ietf.jgss.GSSCredential;

/**
 * A specialisation of {@link GCUBEContext} for gCube Hosting Nodes (gHNs).
 * <p>
 * (*) it manages the lifetime of the gHN, from its initialisation to its shutdown and failure. 
 * This functionality supports the internal operation of the gHN and remains transparent to most service developers.<br>
 * (*) it exposes the configuration of the gHN and its hosting environment.
 * This functionality allows service developers to inspect at runtime the environment in which their code is deployed.<br>
 * (*) it mediates between RIs of deployed services.
 * This functionality supports dynamic discovery of RIs and, while it is generically available to all service developers, 
 * it is key for the correct operation of the gHN and distinguished Local Services.<br>
 * <p>
 * Clients can obtain the single instance of the {@link GHNContext} by invoking its static method {@link #getContext()}. 
 * The first invocation of the method triggers the initialisation of the instance.
 * 
 * 
 * @author Manuele Simi (ISTI-CNR), Fabio Simeoni (University of Strathclyde); 
 * 
 */
public class GHNContext extends GCUBEContext {

	/** current gcf version*/
	public static final String GCF_VERSION = "1.6.3";	
	/**Name of Custom Labels JNDI environment.*/
	public static final String CUSTOMLABELS_JNDI_NAME = "labels";
	/**Name of Coordinates JNDI environment.*/
	public static final String COORDINATES_JNDI_NAME = "coordinates";
	/**Name of Country JNDI environment.*/
	public static final String COUNTRY_JNDI_NAME = "country";
	/**Name of Location JNDI environment.*/
	public static final String LOCATION_JNDI_NAME = "location";
	/**Name of Security JNDI environment.*/
	public static final String SECURITY_JNDI_NAME = "securityenabled";
	/**Name of Override service Security JNDI environment.*/
	// merge problem of 12/15/2011 fixed
	public static final String OVERRIDE_SERVICE_SECURITY = "overrideServiceSecurity";
	// END
	/**Name of startScopes Name JNDI environment.*/
	public static final String STARTSCOPES_JNDI_NAME = "startScopes";
	/**Name of allowedScopes Name JNDI environment.*/
	public static final String ALLOWEDSCOPES_JNDI_NAME = "allowedScopes";	
	/**Name of Coordinates JNDI environment.*/
	public static final String INFRASTRUCTURE_NAME = "infrastructure";
	/** Name of the Mode startup JNDI environment */
	public static final String MODE_JNDI_NAME = "mode";
	/** The name of the host to publish in the local profiles, if different from the container's one */
	public static final String PUBLISHED_HOST_NAME = "publishedHost";
	/** The name of the port to publish in the local profiles, if different from the container's one */
	public static final String PUBLISHED_PORT_NAME = "publishedPort";
	/**Name of GHNType JNDI environment.*/
	public static final String GHN_TYPE = "GHNtype";
	/**Name of OpenPorts JNDI environment.*/
	public static final String OPEN_PORTS = "portRange";
	/** Container status system property. */
	public static final String CONTAINER_STATUS_JNDI_NAME = org.globus.wsrf.Constants.JNDI_BASE_NAME+"/status";
	/** Configuration directory name.*/
	public static final String CONFIGDIR_NAME="config";
	/**Name of the file in which the GHN is serialised.*/
	public static final String PROFILE_FILE_NAME = "GHNProfile.xml";
	/**The classpath resource which contains the current implementations of gCF interfaces.*/
	public static final String IMPLEMENTATIONS_RESOURCE = "implementation.properties";
	/**The classpath resource which contains the configuration of the GHN.*/
	public static final String GHN_JNDI_RESOURCE = "GHNConfig.xml";
	/**The classpath resource which contains the configuration of the GHN for client use.*/
	public static final String GHN_CLIENT_JNDI_RESOURCE = "GHNConfig.client.xml";
	/**Number of attempts for GHN updates.*/
	public static final short GHN_UPDATE_ATTEMPTS=3;
	/**Name of the update interval JNDI environment.*/
	public static final String UPDATEINTERVAL_JNDI_NAME="updateInterval";				
	/**Default update interval in seconds.*/
	public static final long DEFAULT_UPDATE_INTERVAL = 300;
	/**Name of the interval for trusted gHNs synchronization */
	public static final String TRUSTEDGHNINTERVAL_JNDI_NAME="trustedGHNSynchInterval";
	/**Name of the interval for trusted gHNs synchronization */
	public static final long DEFAULT_TRUSTEDGHNINTERVAL=600;
	/** Delay before gHN shutdown */
	public static final int SHUTDOWN_DELAY = 10000;
	/**Name of configurable storage root */
	private static final String STORAGE_ROOT_PROPERTY = "storage.root";
	/** The absolute path to the storage root on the local file system. */
	public static final String STORAGE_ROOT = System.getProperty("user.home")+File.separatorChar +
	".gcore" + File.separatorChar + "persisted" + File.separatorChar + ContainerConfig.getContainerID();
	/**Prefix for mbeans.*/ 
	public static final String MBEANS_PREFIX="org.gcube";
	/**Default test interval in seconds*/
	public static final int DEFAULT_TEST_INTERVAL = 1800;
	/**Name of the test interval JNDI environment.*/
	public static final String TESTINTERVAL_JNDI_NAME="testInterval";
	
	/** Name of the jndi root for service configurations. */
	public static final String JNDI_SERVICES_BASE_NAME = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME;


	/**Properties set with implementations of the interfaces*/
	protected static Properties implementations = new Properties();
	/**Singleton context.*/
	protected static GHNContext singleton=new GHNContext();;
	/**Singleton context proxy.*/
	private static GHNContext singletonproxy;
	/**The underlying container;*/
	private static ServiceContainer container;
	/**Internal producer for {@link GHNEvent GHNEvents}.*/
	private static GCUBEProducer<GHNTopic,Object> lifetimeProducer = new GCUBEProducer<GHNTopic,Object>();  
	/**Internal producer for {@link SecurityTopic SecurityTopics}.*/
	private static GCUBEProducer<SecurityTopic,Object> securityProducer = new GCUBEProducer<SecurityTopic,Object>();  
	/** Active mode. */
	private static Mode mode;
	/**The registered services, indexed by service class and name.*/
	private static Map<String, GCUBEServiceContext> services = Collections.synchronizedMap(new HashMap<String, GCUBEServiceContext>());
	/**The GCUBE Hosting Node resource associated with the context.*/
	protected static GCUBEHostingNode node;
	/** The current status of the GHN. */
	private static Status status = Status.DEPLOYED;
	/** The message associated to the current status of the GHN. */
	private static String statusMessage;
	/** Start scopes as configured in the GHNConfig file.*/
	private static GCUBEScope[] startScopes = null;	
	/** Allowed scopes as configured in the GHNConfig file.*/
	private static GCUBEScope[] allowedScopes = null;
	/** Updater for node resource*/ 
	private static Scheduler updatescheduler = null;
	/** The management bean of the GHN  */
	private static GHN mbean; 
	/** Internal consumer of RI's lifetime events.*/
	private Consumer RIConsumer = new Consumer() {
		protected synchronized void onRIFailed(RILifetimeEvent e) {
			logger.trace("blocking calls for service " + e.getPayload().getName());
			synchronized(status) {if (getStatus()==Status.CERTIFIED) setStatus(Status.READY);} //move back to READY, no longer CERTIFIED
		}
	};


	/** GHN modes */
	public static enum Mode {
		STANDALONE("STANDALONE"),CONNECTED("CONNECTED"),ROOT("ROOT");
		String mode;
		Mode(String mode) {this.mode = mode;}
		public String toString() {return this.mode;}
	};

	/** GHN types */
	public static enum Type {
		DYNAMIC("DYNAMIC"),STATIC("STATIC"),SELFCLEANING("SELFCLEANING");
		String type;
		Type(String type) {this.type = type;}
		public String toString() {return this.type;}
	};

	/** Enumerates the possible statuses of the GHN. */
	public static enum Status {
		DEPLOYED(){public List<Status> previous(){return Collections.singletonList(UNREACHABLE);} public String toString(){return "deployed";}},
		STARTED(){public List<Status> previous(){return Arrays.asList(DEPLOYED,UNREACHABLE);} public String toString(){return "started";}},
		CERTIFIED(){public List<Status> previous(){return Arrays.asList(READY,UNREACHABLE);} public String toString(){return "certified";}},
		UPDATED(){public List<Status> previous(){return Arrays.asList(DEPLOYED,STARTED,READY,CERTIFIED, FAILED, DOWN,UNREACHABLE);} public String toString(){return "updated";}},
		READY(){public List<Status> previous(){return Arrays.asList(STARTED,CERTIFIED,UNREACHABLE);} public String toString(){return "ready";}},
		FAILED(){public List<Status> previous(){return Arrays.asList(STARTED,READY,CERTIFIED,UNREACHABLE);} public String toString(){return "failed";}},
		DOWN(){public List<Status> previous(){return Arrays.asList(DEPLOYED,STARTED,READY,CERTIFIED,FAILED,UNREACHABLE);} public String toString(){return "down";}},
		UNREACHABLE(){public List<Status> previous(){return Arrays.asList(CERTIFIED,UNREACHABLE);} public String toString(){return "unreachable";}};
		/**Returns the list of statuses from which this status may be reached.
		 * @return the status list.*/
		abstract public List<Status> previous();
	};

	//////////////////////////////////////////////////////////////////////////////////////////////// METHODS


	static {//static initialiser
		try{
			singleton.logger.info("INITIALISING GHN");
			if (singleton.isClientMode()) singleton = new GHNClientContext();
			singletonproxy = GCUBEProxyFactory.getProxy(singleton); //create proxy
			singleton.initialise();
		}
		catch(Throwable e){
			singleton.logger.fatal("gHN could not complete initialisation",e);
			exitProcess();//not much point to set a status this early
		}
	}


	/** Creates an instance */
	protected GHNContext() {}


	/**Returns a context instance.
	 * @return the instance.*/
	public synchronized static GHNContext getContext() {return singletonproxy;}




	//////////////////////////////////////////////////////////////////////////////////// LIFETIME MANAGEMENT


	/**
	 * Used internally to initialise the context at class loading time.
	 * @throws Exception if the context could not be initialised.
	 */
	protected void initialise() throws Exception {
		setStatus(Status.STARTED);
		configureGHN(getFile(GHN_JNDI_RESOURCE));
		configureGHNResource();	
		// merge problem of 12/15/2011 fixed
		initializeSecurity();
		//END
		new Thread("managementInitialiser") {public void run() {initialiseManagement();}}.start();
		//new Thread("monitoringInitialiser") {public void run() {initialiseMonitoring();}}.start();
		new Thread("containerMonitor") {public void run() {monitorContainer();}}.start();
		new Thread("certificationMonitor") {public void run() {certify();}}.start();
		setStatus(Status.UPDATED);	
	}
	
	// merge problem of 12/15/2011 fixed
	private void initializeSecurity ()
	{
		SecurityContextFactory.getInstance().setSecurityContext(new DefaultGHNServerSecurityContext());
	}
	//END
	
	/**
	 * Indicates whether the context is running within a container or else in client mode.
	 * @return <code>true</code> if the context is running in client mode, <code>false</code> otherwise
	 */
	public boolean isClientMode() {
		//test JNDI to detect client mode and set status to DOWN in case
		try {this.getJNDIContext().lookup(CONTAINER_STATUS_JNDI_NAME);}
		catch(Exception e) {return true;}
		return false;
	}

	/**
	 * Used internally to load external configuration.
	 * @param file the configuration file.
	 * @throws Exception if it could not load external configuration
	 */
	protected void configureGHN(File file) throws Exception {
		logger.trace("parsing gHN configuration in "+file.getName());
		//parse GHNConfiguration
		Digester digester = new Digester();
		digester.setNamespaceAware(true);
		digester.setValidating(false);
		digester.addRuleSet(new JNDIConfigRuleSet("jndiConfig/"));
		digester.push(new NamingContext(getJNDIContext(), null));
		digester.parse(new FileInputStream(file));
		digester.clear();

		
		String infrastructure = (String) getProperty(INFRASTRUCTURE_NAME, true);
		if (this.isSecurityEnabled() && (! GHNContext.getContext().isClientMode())) {
			//processes allowed
			String[] configAllowedScopes = ((String) getProperty(ALLOWEDSCOPES_JNDI_NAME, false)).split(",");	    	    
			allowedScopes = new GCUBEScope[configAllowedScopes.length + 1];
			allowedScopes[0] = GCUBEScope.getScope("/" + infrastructure);
			for (int idx=0; idx<configAllowedScopes.length;idx++)
				allowedScopes[idx+1] = GCUBEScope.getScope("/" + infrastructure + "/" +configAllowedScopes[idx].trim());
			
			//add the infrastructure scope to the startScopes
			startScopes = new GCUBEScope[] {GCUBEScope.getScope("/" + infrastructure)};
		} else {
			//processes scopes
			String[] configScopes = ((String) getProperty(STARTSCOPES_JNDI_NAME, true)).split(",");	    	    
			startScopes = new GCUBEScope[configScopes.length];
			for (int idx=0; idx<configScopes.length;idx++)
				startScopes[idx] = GCUBEScope.getScope("/" + infrastructure + "/" +configScopes[idx].trim());
			allowedScopes = new GCUBEScope[0];
		}
		

		
		
		//load implementations of gCF interfaces.
		implementations.load(new FileInputStream(getFile(IMPLEMENTATIONS_RESOURCE)));		
		
		//sets URL factory
		logger.trace("installing custom URLStreamHandlerFactory");
		
		try {
			URL.setURLStreamHandlerFactory(new GCUBEURLStreamHandlerFactory());
		}catch(Error e) {
			logger.error("could not install custom URLStreamHandlerFactory",e);
		}
		
	}
	/**
	 * Used internally to configure the {@link GCUBEHostingNode} resource that models the local gHN.
	 * @throws Exception if the resource could not be configured
	 */
	protected void configureGHNResource() throws Exception {

		node = GHNContext.getImplementation(GCUBEHostingNode.class);
		node.setLogger(logger);
		File profile = getFile(PROFILE_FILE_NAME); 
		try {
			try {
				loadGHNResource(profile);
			} catch (Exception e) {
				profile.delete();//by deleting the profile, it will try to load from the backup file
				loadGHNResource(profile);
			}
		} catch(Exception e) {
			if (!(e instanceof FileNotFoundException)) //if it existed, explain why could not be used
				logger.warn("could not restore gHN profile from "+profile.getName()+", regenerating it",e);
			Builder.createGHNResource(this);//whether it existed or not, generate it			   					 
			if (addScope(getStartScopes()).size() == 0) throw new InvalidScopeException();
		}

		//schedule future resource updates
		Long interval = (Long) getProperty(UPDATEINTERVAL_JNDI_NAME,false);
		if (interval==null || interval==0) interval = DEFAULT_UPDATE_INTERVAL;
		logger.info("scheduling updates every "+interval+" seconds");
		updatescheduler = new Scheduler(this,interval,GCUBEScheduledHandler.Mode.LAZY);
		updatescheduler.run();
	}
	
	private void loadGHNResource(File profile) throws Exception {
		node.load(new FileReader(profile));//read existing profile
		Builder.updateGHNResource(this, true);//update dynamic part
		logger.trace("restored gHN profile from "+profile.getName());
	}

	/* Initialise Local Monitoring Interface
	private void initialiseMonitoring() {

		HashMap<GCUBEScope,ArrayList<EndpointReferenceType>> monitoredScopes = new HashMap<GCUBEScope,ArrayList<EndpointReferenceType>>();

		for (GCUBEScope scope :startScopes) {
			try {
				if (scope.getServiceMap().getEndpoints(MSGBROKER)!= null) {
					ArrayList<EndpointReferenceType> listScopes = new ArrayList<EndpointReferenceType>();
					for (EndpointReferenceType msgBrokerEpr:scope.getServiceMap().getEndpoints(MSGBROKER)) {
						logger.info("MSG-Broker found: "+msgBrokerEpr.getAddress().toString()+" for scope: "+scope.toString());
						listScopes.add(msgBrokerEpr);
					}
					monitoredScopes.put(scope, listScopes);

				}

			}
			catch (GCUBEScopeNotSupportedException e){      logger.error("Scope not supported",e);  }
			catch (Exception e) {   logger.error("Error during BrokerMap Initialization",e);}
		}
		
		try {
			//configure the Infrastructure scope
			String infrastructure = (String) getProperty(INFRASTRUCTURE_NAME, true);
			GCUBEScope infrastructureScope = GCUBEScope.getScope("/"+infrastructure);
		
			if ( infrastructureScope.getServiceMap().getEndpoints(MSGBROKER)!= null){

				ArrayList<EndpointReferenceType> listScopes = new ArrayList<EndpointReferenceType>();
				for (EndpointReferenceType msgBrokerEpr:infrastructureScope.getServiceMap().getEndpoints(MSGBROKER)) {
					logger.info("MSG-Broker found: "+msgBrokerEpr.getAddress().toString()+" for scope: "+infrastructureScope.toString());
					listScopes.add(msgBrokerEpr);
				}
				monitoredScopes.put(infrastructureScope, listScopes);

			}
		}
		catch (GCUBEScopeNotSupportedException e){      logger.error("Scope not supported",e);  }
		catch (Exception e) {   logger.error("Error during BrokerMap Initialization",e);}


		if (monitoredScopes.isEmpty())  {logger.warn("missing broker configuration for local monitor"); return; }

		Object val = getProperty(TESTINTERVAL_JNDI_NAME,false);
		long interval = val==null?DEFAULT_TEST_INTERVAL:(Long)val;

		LocalMonitor monitor = null;
		try {monitor = GHNContext.getImplementation(LocalMonitor.class);}
		catch (ClassNotFoundException e) {logger.error("no implementation is available for the local monitor");return;}
		catch (Exception e) {logger.error("unable to initialise the local monitor",e);return;}

		monitor.setInterval(interval);
		monitor.setBrokerMap(monitoredScopes);

		try {monitor.run();}
		catch (Exception e) {logger.error("exception running the local monitor",e);}

		logger.info("local monitor started");
	}

	/**
	 * Used internally to initialise the management interface.
	 * @throws Exception if the management interface could not be initialised.
	 */
	
	private void initialiseManagement() {
		try {
			int port = getFreePort();
			LocateRegistry.createRegistry(port);//start RMI server on open port
			//start JMX RMI connector with proxied mbean server
			//proxy sets current context class loader on RMI calls to synchronise with JNDI context (which is bound to context class loader)
			final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			final ClassLoader currentContextLoader = Thread.currentThread().getContextClassLoader();
			final MethodInterceptor interceptor = new MethodInterceptor() {
				public Object intercept(Object proxy, Method method, Object[] input,MethodProxy methodProxy) throws Throwable {
					Thread.currentThread().setContextClassLoader(currentContextLoader);
					return methodProxy.invoke(mbs, input);
				}
			};
			String hostName = "localhost"; //replace with singleton.getHostname() to open up
			String url = "service:jmx:rmi:///jndi/rmi://"+ hostName+":"+port+"/jmxrmi";
			JMXConnectorServer connector = JMXConnectorServerFactory.newJMXConnectorServer(new JMXServiceURL(url),null,(MBeanServer)Enhancer.create(MBeanServer.class, interceptor));
			connector.start();
			//singleton.node.getNodeDescription().setManagementURL(url);
			//if (getStatus()!=Status.DOWN && getStatus()!=Status.FAILED)  setStatus(Status.UPDATED);
			logger.info("INITIALISED MANAGEMENT INTERFACE AT "+url);

			mbean = new GHN();
			ManagementFactory.getPlatformMBeanServer().registerMBean(mbean, new ObjectName(MBEANS_PREFIX+":type=GHN,value="+getContext().getHostname()));
			// Create and Register the top level Log4J MBean
			HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
			ObjectName mbo = new ObjectName("log4j:hierarchy=default");
			mbs.registerMBean(hdm, mbo);

			LoggerRepository r = LogManager.getLoggerRepository();
			Enumeration<?> e = r.getCurrentLoggers();
			while (e.hasMoreElements())  {//add mbean for all loggers with appenders
				Logger l = (Logger) e.nextElement();
				if ((l.getName().startsWith(MBEANS_PREFIX) || l.getName().startsWith("org.globus")) && l.getAllAppenders().hasMoreElements()) {
					Level currentLevel = l.getLevel();
					l.setLevel(Level.OFF);//avoid log pollution
					hdm.addLoggerMBean(l.getName());
					l.setLevel(currentLevel);
				}
			}
		}
		catch(RuntimeException e) {logger.warn("missing configuration for management interface");}
		catch(Exception e) {logger.warn("could not initialise management interface",e);}
	}
	/**
	 * Used internally to monitor end of the startup process of the the underlying container.
	 */
	private void monitorContainer() {
		while (true)
			try {
				Thread.sleep(100);
				container = ServiceContainerCollection.get(getBaseURL()); 
				if (container!=null) {//wait for container's startup
					setStatus(Status.READY);
					System.out.println("gHN started at: "+singleton.getBaseURL()+" with the following services:\n");
					List<String> gPts =singleton.getDeployedPortTypes();
					List<String> pts = singleton.getAllDeployedPortTypes();
					int i;
					System.out.println("GCUBE SERVICES:\n");
					for (i=0;i< gPts.size();i++) {System.out.println("["+(i+1)+"]: "+singleton.getBaseURL()+gPts.get(i));}
					System.out.println("\nOTHER SERVICES:\n");
					for (int j=i;j< pts.size();j++) 
						if (!pts.get(j).contains("gcube"))	
							System.out.println("["+(j+1)+"]: "+singleton.getBaseURL()+pts.get(j));
					return;
				}
			}
		catch(Exception e) {
			logger.fatal("gHN could not complete startup",e);
			setStatus(Status.FAILED);
			return;
		}
	}

	private void certify() {
		outer:while (true)
			try {
				Thread.sleep(2000);
				Set<GCUBEServiceContext> contexts = getServiceContexts();
				if (contexts.size() == 0) continue;
				for (GCUBEServiceContext ctxt : contexts){
					if (ctxt.getStatus()==GCUBEServiceContext.Status.FAILED) return; //never
					if (ctxt.getStatus()!=GCUBEServiceContext.Status.READIED) continue outer;  //maybe later
				}
				setStatus(Status.CERTIFIED);return;//done
			}
	catch(Exception e) {logger.warn("problem during certification monitoring",e);}
	}

	/** Sets the current status of the GHN.
	 * @param newStatus the status.
	 * @param message message associated to the status
	 * @throws IllegalStateException if the transition from the current to the new status is illegal. */
	@SuppressWarnings("unchecked")
	public void setStatus(Status newStatus, String ... message) throws IllegalStateException {

		if (!newStatus.previous().contains(status)) throw new IllegalStateException("transition from "+status+" to "+newStatus+" is illegal");//check legal transitions
		synchronized (status) {//one change at the time, but no need to block the entire context.
			switch(newStatus) {
			case STARTED:status=newStatus;statusMessage=(message!=null && message.length>0)? message[0]:null;break;
			case READY:status=newStatus;statusMessage=(message!=null && message.length>0)? message[0]:null;
			lifetimeProducer.notify(GHNTopic.READY,new GHNLifeTimeEvent());break;				
			case CERTIFIED:status=newStatus;statusMessage=(message!=null && message.length>0)? message[0]:null;
			this.setStatus(Status.UPDATED);break;
			case UPDATED: //short-lived state, update profile and notify
				try{node.store(new FileWriter(this.getFile(PROFILE_FILE_NAME,true)));}
				catch(Exception e) {logger.warn("could not serialise gHN profile",e);}
				lifetimeProducer.notify(GHNTopic.UPDATE,new GHNLifeTimeEvent());break;	
			case FAILED:	
			case DOWN: //short-lived state
				logger.info("the gHN is shutting down in "+(SHUTDOWN_DELAY/1000)+" seconds");
				status=newStatus;
				statusMessage=(message!=null && message.length>0)? message[0]:null;
				updatescheduler.stop();
				Builder.updateGHNResource(this);//force the last update of the node resource
				this.setStatus(Status.UPDATED);
				try {lifetimeProducer.notify(GHNTopic.SHUTDOWN, new GHNLifeTimeEvent());}
				catch(Exception e) {logger.warn("could not inform Running Instances of gHN shutdown");}
				new Thread("GHNKiller") {
					public void run() {						
						try {Thread.sleep(SHUTDOWN_DELAY);exitProcess();}
						catch (Exception e) {logger.error("gHN could not shutdown",e);}
					}
				}.start(); 
			}
		}
		logger.trace("the gHN is "+newStatus.toString().toUpperCase());
	}

	/** Returns the current status of the GHN.
	 * @return the status.*/
	public Status getStatus() {synchronized (status){return status;}}

	/** Returns the message associated to the current status of the GHN.
	 * @return the status message.*/
	public String getStatusMessage() {synchronized (statusMessage){return statusMessage;}}

	/**Restarts the gHN.*/
	@Restricted(Restricted.GCORE) public void restart(String ... message) {
		logger.info("the gHN is going to be restarted...");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(getFile(".restart")));
			out.write(" ");out.close();
			this.shutdown(message);
		} catch (Exception e) {logger.error("could not restart the container",e);}	
	}

	@Restricted(Restricted.GCORE) public void restartAndClean(String ... message) {
		logger.info("the gHN is going to be restarted and cleaned up...");
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(getFile(".restartAndClean")));
			out.write(" ");out.close();
			this.shutdown(message);
		} catch (Exception e) {logger.error("could not restart the container",e);}	
	}	

	/**Shuts down the gHN.**/
	@Restricted(Restricted.GCORE) public void shutdown(String ... message) {this.setStatus(Status.DOWN, message);}

	/** Used internally to quit the current process. */
	static private void exitProcess() {
		if (container!=null) try {container.stop();} 
		catch(Exception e) {singleton.logger.warn("gHN could not gracefully stop the container",e);}
		singleton.logger.info("the gHN is shutting down now");
		System.exit(0);//assume a problem
	}

	////////////////////////////////////////////////////////////////////////////////////////////////  RI MANAGEMENT

	/**Registers a service context in the current node.
	 * @param context the context.
	 * @throws Exception if the service is already registered or the registration could not be completed.
	 * @throws IllegalStateException if the container is down. */
	@SuppressWarnings("unchecked")
	@Restricted(Restricted.GCORE) public synchronized void registerService(GCUBEServiceContext context) throws Exception {

		if (services.containsKey(context.getServiceClass() + context.getName()))
			throw new Exception("(" + context.getServiceClass() + "," + context.getName() + ") has already registered");

		logger.info("REGISTERED RI OF (" + context.getServiceClass().toUpperCase() + "," + context.getName().toUpperCase() + ")");
		services.put(context.getServiceClass() + context.getName(), context);

		//subscribe for RI lifetime events
		context.subscribeLifetTime(RIConsumer);

		//notifies the registration to interested partners.
		lifetimeProducer.notify(GHNTopic.RIREGISTRATION, new GHNRIRegistrationEvent(context));
	}

	/**
	 * Returns the context of a registered service from its class and name. 
	 * @param serviceClass the service class.
	 * @param serviceName the service name.
	 * @return the service context.
	 * @throws Exception if the service is not registered.
	 * @throws IllegalStateException if the gHN is down.
	 */
	public synchronized GCUBEServiceContext getServiceContext(String serviceClass, String serviceName) throws Exception {
		GCUBEServiceContext context = services.get(serviceClass+serviceName);    	
		if (context==null) throw new Exception("service "+serviceName+" is unknown");
		return GCUBEProxyFactory.getProxy(context,GCUBEServiceContext.class);
	}

	/**
	 * Returns all the registered service contexts (no matter about which status they have)
	 * @return the Set of all service contexts
	 * @throws Exception if there is no service registered.
	 * @throws IllegalStateException if the gHN is down.
	 */
	@Restricted(Restricted.GCORE) public synchronized Set<GCUBEServiceContext> getServiceContexts() throws Exception {
		Set<GCUBEServiceContext> set = new HashSet<GCUBEServiceContext>();
		set.addAll(services.values()); //no proxies due to restricted access.
		return Collections.unmodifiableSet(set);
	}

	/**Subscribes a consumer to one or more {@link GHNTopic GHNTopics}.
	 * @param consumer the consumer.
	 * @param topics the topics of interest.
	 * @throws Exception if the subscription could not be completed.*/
	public void subscribeGHNEvents(GHNConsumer consumer, GHNTopic ...topics) throws Exception {
		if (topics==null || topics.length==0) topics = GHNTopic.values();	
		lifetimeProducer.subscribe(consumer,topics);
	}

	/**
	 * Unsubscribe a consumer to one or more {@link GHNTopic GHNTopics}.
	 * @param consumer the consumer.
	 * @param topics the topics.
	 */
	public void unsubscribeGHNEvents(GHNConsumer consumer, GHNTopic ...topics) {
		lifetimeProducer.unsubscribe(consumer, topics);
	}

	/////////////////////////////////////////////////////////////////////////////////// SECURITY MANAGEMENT

	/**Indicates whether the GHN is operating in a secure infrastructure.
	 * @return <code>true</code> if its is, <code>false</code> otherwise.*/
	public boolean isSecurityEnabled() {return (Boolean) this.getProperty(SECURITY_JNDI_NAME, true);}

	// merge problem of 12/15/2011 fixed
	/**Indicates whether the GHN security status must override the security status of the services.
	 * @return <code>true</code> if its is, <code>false</code> otherwise.*/
	public boolean overrideServiceSecurity() 
	{
		Boolean override = (Boolean) this.getProperty(OVERRIDE_SERVICE_SECURITY);
		
		if (override == null) return false;
		else return override;
	}
	//END

	/**Subscribes a consumer to {@link CredentialRequestEvent CredentialRequestEvents}.
	 * @param consumer the consumer.
	 * @throws Exception if the subscription could not be completed.*/
	@Restricted public void subscribeForCredentialRequest(CredentialRequestConsumer consumer) throws Exception {
		securityProducer.subscribe(consumer,SecurityTopic.CREDENTIAL_REQUEST);
	}

	/**Subscribes a consumer to {@link CredentialDelegationEvent CredentialDelegationEvents}.
	 * @param consumer the consumer.
	 * @throws Exception if the subscription could not be completed.
	 */
	@Restricted public void subscribeForCredential(CredentialConsumer consumer) throws Exception {
		logger.info("subscribing service "+consumer.getServiceContext().getName()+" for credential delegation");
		securityProducer.subscribe(consumer,SecurityTopic.CREDENTIAL_DELEGATION);
		securityProducer.notify(SecurityTopic.CREDENTIAL_REQUEST, new CredentialRequestEvent(consumer.getServiceContext()));
	}

	/**
	 * Delegates credentials to a service.
	 * @param context the service context.
	 * @param credentials the credentials.
	 */
	@Restricted	 public void delegateCredentials(GCUBEServiceContext context,GSSCredential credentials){
		logger.debug("delegating credentials to service "+context.getName());
		//sanity check to make sure this context is known.
		try {this.getServiceContext(context.getServiceClass(),context.getName());}//no need to assign if check passes
		catch(Exception e) {logger.warn("could not delegate credentials",e);}		 
		securityProducer.notify(SecurityTopic.CREDENTIAL_DELEGATION, new CredentialDelegationEvent(new CredentialPayload(context,credentials)));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////  SCOPE MANAGEMENT

	/**
	 * Adds one or more scopes to the GHN.  
	 * 
	 * @param scopes the scopes.
	 * @return the list of scopes actually added to the GHN. 
	 */
	@Restricted synchronized public Set<GCUBEScope> addScope(GCUBEScope ... scopes) throws InvalidScopeException  {
		if (this.isSecurityEnabled() && (! GHNContext.getContext().isClientMode()))
			if (! this.acceptScopes(scopes)) {
				logger.error("One of the scope(s) was not accepted by this GHN");
				throw new InvalidScopeException();
			}
		
		Set<GCUBEScope> validScopes = this.getGHN().addScope(scopes);
		if (validScopes.size()>0) this.setStatus(Status.UPDATED);		
		return validScopes; //feedback to callers
	}
	
	private boolean acceptScopes(GCUBEScope ... scopes) {
		if (scopes == null)
			return false;		
		for (GCUBEScope scope : scopes) {
			if (! this.containScope(this.getAllowedScopes(), scope) ) {
				logger.error("Scope " + scope.toString() + " is not in the list of allowed Scopes for this gHN (" + Arrays.toString(this.getAllowedScopes()) + ")");
				return false; //even if just one scopes is not in the list, the whole request is not validated
			}
		}
		return true;
	}

	
	private boolean containScope(GCUBEScope[] scopes, GCUBEScope scope) {
		for (GCUBEScope itemScope : scopes) {
			if (itemScope == null) continue;
			logger.trace("Checking " + itemScope.getName());
			if (itemScope.getName().compareToIgnoreCase(scope.getName()) == 0)
				return true;
		}
		return false;
	}


	/**
	 * Removes one or more scopes from the GHN.
	 * Only scopes currently associated with the instance are actually removed.
	 */
	@Restricted synchronized public Set<GCUBEScope> removeScope(GCUBEScope ... scopes) {
		Set<GCUBEScope> validScopes = this.getGHN().removeScope(scopes);
		if (validScopes.size()>0) this.setStatus(Status.UPDATED);//update only if some scope was actually removed.
		return validScopes;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////    CONFIGURATION MANAGEMENT
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an implementation for a gCF interface. The
	 * implementation class is configured in the classpath resource <code>implementations.property</code>
	 * found in the GHN's configuration folder.
	 * 
	 * @param <INTERFACE>  the interface.
	 * @param interfaceClass the {@link java.lang.Class} of the interface.
	 * @return the implementation, or <code>null</code> if an implementation could not be found.
	 * @throws Exception  if the implementation is not compatible with the interface or could not be instantiated.*/
	@SuppressWarnings("unchecked")
	public static synchronized <INTERFACE> INTERFACE getImplementation(Class<INTERFACE> interfaceClass) throws Exception  {    
		String className = implementations.getProperty(interfaceClass.getSimpleName());	    
		if (className==null) return null;	    
	
		//uses thread's classloader in case interface implementations are governed by their own classloader
		Class<? extends INTERFACE> clazz = (Class<INTERFACE>) Thread.currentThread().getContextClassLoader().loadClass(className);
		if (!interfaceClass.isAssignableFrom(clazz))  throw new Exception(className+" does not implement "+interfaceClass.getName());
		// invoke constructor reflectively
		Constructor<? extends INTERFACE> constructor = clazz.getConstructor(new Class[] {});
		return constructor.newInstance(new Object[]{}); 
	}

	/**{@inheritDoc}*/
	public File getFile(String fileName, boolean ... writeMode) throws IllegalArgumentException {
		return super.getFile(this.getLocation()+File.separatorChar+CONFIGDIR_NAME+File.separatorChar+fileName,writeMode);
	}
	/** {@inheritDoc} */
	public InputStream getResource(String resourceName)  {
		return this.getClass().getResourceAsStream("/"+CONFIGDIR_NAME+"/"+resourceName); 
	}

	/**Returns the GHN identifier.
	 * @return the identifier.*/
	public String getGHNID() {return node.getID();}

	/**Checks if the GHN has completed the startup procedure.
	 * @return <code>true</code> if the GHN is started, <code>false</code> otherwise. */
	public boolean isGHNReady() {return this.getStatus()==Status.READY || this.getStatus().previous().contains(Status.READY);}

	/**Return the management bean of the GHN.
	 * @return the bean.*/
	public GHN getManagementBean() {return mbean;}

	/** Sets the GCUBE Hosting Node resource associated with the context.
	 * @return the resource*/
	@ReadOnly public GCUBEHostingNode getGHN() {return node;}

	/**Gets the GHN mode.
	 * @return the mode.*/
	public Mode getMode() {
		if (mode == null) {			
			if (((String) getProperty(MODE_JNDI_NAME,true)).compareToIgnoreCase(Mode.STANDALONE.toString()) == 0) return Mode.STANDALONE; 
			if (((String) getProperty(MODE_JNDI_NAME,true)).compareToIgnoreCase(Mode.ROOT.toString()) == 0)	return Mode.ROOT;
			else return Mode.CONNECTED;	
		}					
		return mode; }


	/**Returns the GHN type.
	 * @return the type.*/
	public Type getType() {		
		if (((String) getProperty(MODE_JNDI_NAME,true)).compareToIgnoreCase(Type.STATIC.toString()) == 0) return Type.STATIC; 			
		else return Type.DYNAMIC; }

	/**Sets the GHN mode.
	 * @param mode the new mode.*/
	public void setMode(Mode mode) {GHNContext.mode = mode;}

	/**
	 * Returns the number of the first free port on the gHN in the range specified in the GHN configuration (<code>openPorts</code>) or, if no such range exist,
	 * in the range 60000-65000;
	 * @return the port number.
	 * @throws Exception if no free port could be found.
	 * @throws RuntimeException if no free port-configuration was found.
	 */
	public Integer getFreePort() throws RuntimeException,Exception {
		String portRange =  (String) this.getProperty(OPEN_PORTS,true);//let RTE indicate no configuration
		String[] bounds =  portRange.split("-");
		int left=Integer.parseInt(bounds[0]); int right = Integer.parseInt(bounds[1]);
		for (int port = left; port<right; port++) {
			try {ServerSocket s = new ServerSocket(port);s.close();return port;}
			catch (IOException ex) {continue;} 
		}
		throw new Exception("No free port in range "+left+"-"+right);
	}


	/**
	 * Returns the names of the port-types of all the services deployed in the gHN.
	 * @return the names.
	 * @throws Exception if the names could not be returned.
	 * @throws IllegalStateException if the gHN is down. 
	 */
	private List<String> getAllDeployedPortTypes() throws Exception, IllegalStateException {
		if (!this.isGHNReady()) throw new IllegalStateException();
		List<String> eprs = new ArrayList<String>();
		Iterator<?> i = container.getEngine().getConfig().getDeployedServices();
		while (i.hasNext()) eprs.add(((ServiceDesc) i.next()).getName());
		Collections.sort(eprs);
		return eprs;
	}

	/**
	 * Returns the names of the port-types of the gCube services deployed in the GHN.
	 * @return the names.
	 * @throws Exception if the names could not be returned.
	 * @throws IllegalStateException if the query fails
	 */
	public List<String> getDeployedPortTypes() throws Exception, IllegalStateException {
		List<String> entryNames = new ArrayList<String>();
		for (String portType : getAllDeployedPortTypes())			
			if (portType.contains("gcube")) entryNames.add(portType);
		return entryNames;	
	}

	/**Returns the start scopes of the GHN.
	 * @return the start scopes.*/
	public synchronized GCUBEScope[] getStartScopes() {return startScopes;}
	
	/**Returns the allowed scopes of the GHN.
	 * @return the allowed scopes.*/
	public synchronized GCUBEScope[] getAllowedScopes() {return allowedScopes;}
		
	/**
	 * Returns the synchronization interval for the local list of trusted gHNs
	 * @return the synchronization interval for the local list of trusted gHNs
	 */
	public long getTrustedGHNSynchInterval() { 
		try {
			return (Long) getProperty(GHNContext.TRUSTEDGHNINTERVAL_JNDI_NAME,true);
		} catch (Exception e) { return DEFAULT_TRUSTEDGHNINTERVAL;}
	}
	
	/**
	 * Returns the installation folder of the gHN.
	 * @return the installation folder.
	 */
	public String getLocation() {
		String loc = System.getenv("GLOBUS_LOCATION");
		return loc!=null?loc:System.getProperty("GLOBUS_LOCATION");
	}
	
	/**
	 * Returns the virtual platforms folder of the gHN.
	 * @return the folder where local virtual platforms are deployed
	 */
	public String getVirtualPlatformsLocation() {return this.getLocation()+ File.separator +"virtual-platforms";}

	/**
	 * Returns the base endpoint of the gHN.
	 * @return the endpoint in the form <em>host:port</em>.
	 * @throws IOException if the endpoint could not be identified.
	 */
	public String getBaseURL() throws IOException {
		return ServiceHost.getBaseURL().toString();				
	}

	/**
	 * Returns the base endpoint of the gHN.
	 * @return the endpoint in the form <em>host:port</em>.
	 * @throws IOException if the endpoint could not be identified.
	 */
	public String getBaseURLToPublish() throws IOException {
		String baseURL = this.getBaseURL();		
		if (getProperty(PUBLISHED_HOST_NAME, false) != null) 			
			baseURL = baseURL.replace(ServiceHost.getHost(), (String) getProperty(PUBLISHED_HOST_NAME, false));		
		if (getProperty(PUBLISHED_PORT_NAME, false) != null)
			baseURL = baseURL.replace(Integer.toString(ServiceHost.getPort()), Integer.toString((Integer) getProperty(PUBLISHED_PORT_NAME, false)));
		return baseURL;
	}
	/**
	 * Returns the port of the gHN.
	 * @return the port.
	 */
	public  int getPublishedPort() {
		if ( getProperty(PUBLISHED_PORT_NAME, false) != null)
			return (Integer) getProperty(PUBLISHED_PORT_NAME, false);
		else 
			return this.getPort();
	}

	/**
	 * Returns the port of the gHN.
	 * @return the port.
	 */
	public  int getPort() {
		return ServiceHost.getPort();
	}
	
	/**
	 * Returns the hostname of the gHN.
	 * @return the hostname.
	 * @throws IOException if the hostname could not be returned.
	 */
	public  String getPublishedHostname() {
		if ( getProperty(PUBLISHED_HOST_NAME, false) != null)
			return (String) getProperty(PUBLISHED_HOST_NAME, false);
		else {
			return this.getHostname();
		}
	}
	
	public LocalInstanceContext getLocalInstanceContext() {
		return LocalInstanceContext.getContext();
	}
	
	/**
	 * Returns the hostname of the gHN.
	 * @return the hostname.
	 * @throws IOException if the hostname could not be returned.
	 */
	public  String getHostname() {
			try {return ServiceHost.getHost();}
			catch(Exception e) {return "unknown";}		
	}
	
	/**
	 * Returns the prefix to the relative endpoint of services.
	 * @return the  prefix.
	 */
	public  String getServiceEndpointPrefix() {return "/wsrf/services/";}

	/**
	 * Returns the absolute path to the storage root folder on local the file system.
	 * @return the path.
	 */
	public String getStorageRoot() {
		String root = (String) System.getProperty(STORAGE_ROOT_PROPERTY);
		return root==null?STORAGE_ROOT:root;
	}

	/**
	 * Returns the amount of free space on a partition of the underlying file system.
	 * @param localFS the partition name.
	 * @return the free available space in bytes in a String format
	 */
	public long getFreeSpace(String localFS) {
		long free = 0;
		//logger.trace("calculating free space on partition: " + localFS);
		try {free =FileSystemUtils.freeSpace(localFS);}
		catch (IOException ioe) {logger.warn("unable to detect the free space on the disk", ioe);} 		
		return free;
	}

	/**
	 * Returns the length of time since the underlying machine.
	 * @return the uptime of the machine.
	 */
	public  String getUptime() {
		String lines = "", linetemp = null;
		try {							
			Process p = Runtime.getRuntime().exec("uptime");
			p.waitFor();			
			BufferedReader input =  new BufferedReader (new InputStreamReader(p.getInputStream()));
			while ((linetemp = input.readLine()) != null) lines += linetemp;
			input.close();
			p.destroy();
			lines = lines.split(",")[0].split("up")[1].trim();
		} catch (Exception e) {
			logger.warn("unable to detect the uptime of this machine", e);
			lines ="unable to detect";
		}
		return lines;
	}
	/**
	 * Returns virtual memory statistics.
	 * 
	 * @return a map with the following keys: 
	 *  <ul>
	 *  <li> "ramVirtualAvailable"
	 *  <li> "ramVirtualSize"
	 *  </ul> 
	 */
	public  Map<String, Long> getMemoryUsage() {
		Map<String, Long> map = new HashMap<String, Long>();
		//logger.trace("Analysing memory status...");
		java.lang.management.OperatingSystemMXBean mxbean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
		com.sun.management.OperatingSystemMXBean sunmxbean = (com.sun.management.OperatingSystemMXBean) mxbean;
		long freeMemory = sunmxbean.getFreePhysicalMemorySize()/1048576; // in MB
		long availableMemory = sunmxbean.getTotalPhysicalMemorySize()/1048576;	 //in MB	 	
		map.put("MemoryAvailable", freeMemory);		
		map.put("MemoryTotalSize", availableMemory);
		long ramVirtualAvailable = Runtime.getRuntime().freeMemory()/1048576; // in MB									
		long ramVirtualSize = Runtime.getRuntime().totalMemory()/1048576; // in MB			
		map.put("VirtualAvailable", ramVirtualAvailable);		
		map.put("VirtualSize", ramVirtualSize);	
		return map;
	}

	/**
	 * Returns information about the processors underlying of the gHN.
	 * @return a list of processor-specific maps with the following keys:
	 * <ul>
	 * 	<li> "vendor_id" 
	 *  <li> "cpu_family"
	 *  <li> "model"
	 *  <li> "model_name"
	 *  <li> "cpu_MHz"
	 *  <li> "cache_size"
	 *  <li> "bogomips"
	 *  </ul>
	 */
	@SuppressWarnings("unchecked")
	public  ArrayList<HashMap<String, String>> getCPUInfo() {

		ArrayList<HashMap<String, String>> map = new ArrayList<HashMap<String, String>>();		
		BufferedReader input = null;
		try {
			input = new BufferedReader( new FileReader(new File("/proc/cpuinfo")));			
			String line = null;
			HashMap<String, String> currentProcessor = null;
			while (( line = input.readLine()) != null){
				if ((line.startsWith("processor"))) {		//add the current processor to the map
					if (currentProcessor != null) map.add((HashMap<String, String>)currentProcessor.clone());
					currentProcessor = new HashMap<String, String>();					
				} 
				try {if (line.contains("vendor_id")) currentProcessor.put("vendor_id", line.split(":")[1].trim());} catch (Exception ex){}
				try {if (line.contains("cpu family")) currentProcessor.put("cpu_family", line.split(":")[1].trim());} catch (Exception ex){}
				try {if ((line.contains("model\t")) || (line.contains("model\b")))   currentProcessor.put("model", line.split(":")[1].trim());}catch (Exception ex){}
				try {if (line.contains("model name")) currentProcessor.put("model_name", line.split(":")[1].trim());} catch (Exception ex){}
				try {if (line.contains("cpu MHz")) currentProcessor.put("cpu_MHz", line.split(":")[1].trim());} catch (Exception ex){}
				try {if (line.contains("cache size")) currentProcessor.put("cache_size", line.split(":")[1].trim().split(" ")[0]);} catch (Exception ex){}
				try {if (line.contains("bogomips")) currentProcessor.put("bogomips", line.split(":")[1].trim());} catch (Exception ex){}
			}
			if (currentProcessor!=null) map.add(currentProcessor);
		}
		catch(FileNotFoundException e) {logger.warn("unable to acquire CPU info");}
		catch (Exception ex) {logger.warn("unable to acquire CPU info", ex);}
		finally {
			try {if (input!= null) input.close();}
			catch (IOException ex) {logger.warn("unable to release the CPU resource reader", ex);}
		}
		return map;
	}


	/**
	 * Returns load statistics for the gHN.
	 *  
	 * @return a map with the following keys:<br/>
	 * <ul>
	 * <li><em>1min</em>: report the load of the last minute <br/>
	 * <li><em>5mins</em>: report the load of the last 5 minutes<br/>
	 * <li><em>15mins</em>: report the load of the last 1S5 minutes<br/>
	 * </ul>
	 */
	public Map<String, Double> getLoadStatistics() {

		Map<String, Double> result = new HashMap<String, Double>();
		try {							
			File loadadv = new File("/proc/loadavg");
			if (loadadv.exists()) {
				Reader reader = new FileReader(loadadv);
				int c;
				StringBuilder content = new StringBuilder();
				while ((c = reader.read()) != -1)
					content.append((char)c);
				reader.close();
				Pattern p = Pattern.compile("^(.*?)\\s{1}(.*?)\\s{1}(.*?)\\s{1}(.*)$");
				Matcher matcher = p.matcher(content.toString());
				if ((matcher.matches()) && (matcher.groupCount() > 3)) {				
					result.put("1min", new Double(matcher.group(1)));
					result.put("5mins", new Double(matcher.group(2)));					
					result.put("15mins", new Double(matcher.group(3).split("\\s")[0]));					
				}
			}
		} catch (Exception ioe) {logger.warn("unable to detect the load values of this machine", ioe);}
		return result;
	}


	/**
	 * Returns the IP address.
	 * @return the address.
	 */
	public String getIP() {	    
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			return localMachine.getHostAddress();
		} catch (UnknownHostException e) {logger.warn("unable to detect the IP address of the host");return "";}
	}

	/**
	 * Returns the hostname and port of the gHN.
	 * @return a string with the format <em>hostname:port</em>.
	 */
	public  String getHostnameAndPort() {
		return this.getHostname() + ":"+ this.getPort();
	}

	/**
	 * Returns the hostname and port of the gHN to publish. 
	 * These could be different from the effective ones when a proxy or dispatcher is dedicated to manage incoming calls
	 * @return a string with the format <em>hostname:port</em>.
	 */
	public  String getPublishedHostnameAndPort() {
		return this.getPublishedHostname() + ":"+ this.getPublishedPort();
	}
	
	/**
	 * Returns the host domain of the gHN.
	 * @return the host domain.
	 * @throws IOException if the host domain could not identified.
	 */
	public  String getHostDomain() throws IOException {	 
		return trimDomain(this.getHostname());
	}

	/**
	 * Returns the host domain of the gHN.
	 * @return the host domain.
	 * @throws IOException if the host domain could not identified.
	 */
	public  String getPublishedHostDomain() throws IOException {	 
		return trimDomain(this.getPublishedHostname());
		 
	}
	
	private String trimDomain(String hostname) {        
		Pattern pattern = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
	    java.util.regex.Matcher regexMatcher = pattern.matcher(hostname);
	    if (regexMatcher.matches()) //it's an IP address, nothing to trim
	    	return hostname;
		String[] tokens = hostname.split("\\.");
		if (tokens.length < 2) 
			return hostname;
		else 			
			return tokens[tokens.length-2]+ "." + tokens[tokens.length-1];			    		
	}
}


