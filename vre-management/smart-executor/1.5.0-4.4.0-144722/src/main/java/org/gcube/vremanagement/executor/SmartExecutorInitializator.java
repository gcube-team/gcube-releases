package org.gcube.vremanagement.executor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.ClientType;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.ServiceEndpoint.Runtime;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.smartgears.ApplicationManager;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.pluginmanager.PluginManager;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;
import org.gcube.vremanagement.executor.scheduler.SmartExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class SmartExecutorInitializator implements ApplicationManager {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorInitializator.class);
	
	public static final long JOIN_TIMEOUT = 1000;
	
	public static String getCurrentScope(){
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch (Exception e) {
			return ScopeProvider.instance.get();
		}
		return authorizationEntry.getContext();
	}
	
	public static ClientInfo getClientInfo() {
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch (Exception e) {
			return new ClientInfo() {
				
				/**
				 * Generated Serial Version UID
				 */
				private static final long serialVersionUID = 8311873203596762883L;

				@Override
				public ClientType getType() {
					return ClientType.USER;
				}
				
				@Override
				public List<String> getRoles() {
					return new ArrayList<>();
				}
				
				@Override
				public String getId() {
					return "UNKNOWN";
				}
			};
		}
		return authorizationEntry.getClientInfo();
	}
	
	
	/**
	 * Publish the provided resource on all Service Scopes retrieved from 
	 * Context
	 * @param resource to be published
	 * @throws RegistryNotFoundException if the Registry is not found so the
	 * resource has not be published
	 */
	private static void publishResource(Resource resource) throws Exception {
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(resource, stringWriter);
		
		RegistryPublisher registryPublisher = RegistryPublisherFactory.create();
		
	    try {
	    	logger.debug("Trying to publish to {}:\n{}", getCurrentScope(), stringWriter);
	    	registryPublisher.create(resource);
		} catch (Exception e) {
			logger.error("The resource was not published", e);
			throw e;
		}
	}
	
	/**
	 * Remove the resource from IS
	 * @param resource to be unpublished
	 * @throws RegistryNotFoundException if the Registry is not found so the
	 * resource has not be published
	 */
	private static void unPublishResource(Resource resource) throws Exception {
		//StringWriter stringWriter = new StringWriter();
		//Resources.marshal(resource, stringWriter);
		
		RegistryPublisher registryPublisher = RegistryPublisherFactory.create();
	    
		String id = resource.id();
	    logger.debug("Trying to remove {} with ID {} from {}", resource.getClass().getSimpleName(), id, getCurrentScope());
	    
		registryPublisher.remove(resource);
		
		logger.debug("{} with ID {} removed successfully", resource.getClass().getSimpleName(), id);
	}
	
	/**
	 * Return the parsed version string as array of short.
	 * @param version the version as String
	 * @param wantedLenght if the length is equals to dot (.) separated 
	 * number in the string. Otherwise the version is padded or truncated to
	 * the required version
	 * @return the parsed version as array of short. If on slicing some of the
	 * version cannot be parsed as short 1 is used for the first number, 0 is 
	 * used instead or for padding 
	 */
	private static short[] getVersionSlice(String version, int wantedLenght){
		logger.trace("Trying to parse {}", version);
		
		short[] versionSlices = new short[wantedLenght];
		for(int j=0; j<wantedLenght; j++){
			versionSlices[j] = (short) (j==0 ? 1 : 0);
		}
		
		try {
			String[] stringSlices = version.split("[.-]");
			for(int i=0; i<stringSlices.length; i++){
				logger.trace("Parsing version slice n. {} wich is '{}'", i, stringSlices[i]);
				if(i>=wantedLenght){
					break;
				}
				try {
					short n = Short.parseShort(stringSlices[i]);
					versionSlices[i] = n;
					logger.trace("Version slice n. {} wich is '{}' parsed as short {}", i, stringSlices[i], n);
				} catch(NumberFormatException nfe){
					logger.trace("Version slice n. {} wich is '{}' failed to parse. The default value {} will be used", i, stringSlices[i], versionSlices[i]);
				}
			} 
		} catch(Exception e){
			logger.trace("Error parsing the supplied version the default will be used", versionSlices);
		}
		
		logger.trace("Version {} parsed as {}", version, versionSlices);
		return versionSlices;
	}
	
	private static String getRunningOn(ContainerConfiguration containerConfiguration){
		return String.format("%s:%s", containerConfiguration.hostname(), containerConfiguration.port());
	}
	
	/**
	 * Create the Service Endpoint using information related to discovered 
	 * available plugins and their own discovered capabilities 
	 * @return the created {@link ServiceEndpoint}
	 */
	protected static ServiceEndpoint createServiceEndpoint(Map<String, PluginDeclaration> availablePlugins){
		logger.debug("Creating ServiceEndpoint to publish on IS available plugins and their own supported capabilities");
		ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
		Profile profile = serviceEndpoint.newProfile();
		profile.category(ContextProvider.get().configuration().serviceClass());
		profile.name(ContextProvider.get().configuration().name());
		String version = ContextProvider.get().configuration().version();
		profile.version(version);
		profile.description(ContextProvider.get().configuration().description());
		
		String runningOn = getRunningOn(ContextProvider.get().container().configuration());
		Platform platform = profile.newPlatform();
		platform.name(runningOn);
		
		short[] versionSlices = getVersionSlice(version, 4);
		platform.version(versionSlices[0]);
		platform.minorVersion(versionSlices[1]);
		platform.buildVersion(versionSlices[2]);
		platform.revisionVersion(versionSlices[3]);
		
		Runtime runtime = profile.newRuntime();
		runtime.hostedOn(runningOn);
		runtime.status(ContextProvider.get().configuration().mode().toString());
		
		Group<AccessPoint> accessPoints = profile.accessPoints();
		
		for(String pluginName : availablePlugins.keySet()){
			AccessPoint accessPointElement = new AccessPoint();
			accessPointElement.name(pluginName);
			
			PluginDeclaration pluginDeclaration = availablePlugins.get(pluginName);
			
			accessPointElement.description(pluginDeclaration.getDescription());
			
			Group<Property> properties = accessPointElement.properties();
			Property propertyVersionElement = new Property();
			propertyVersionElement.nameAndValue("Version", pluginDeclaration.getVersion());
			properties.add(propertyVersionElement);
			
			
			Map<String, String> pluginCapabilities = pluginDeclaration.getSupportedCapabilities();
			for(String capabilityName : pluginCapabilities.keySet()){
				Property propertyElement = new Property();
				propertyElement.nameAndValue(capabilityName, pluginCapabilities.get(capabilityName));
				properties.add(propertyElement);
			}
			accessPoints.add(accessPointElement);
		}
		
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(serviceEndpoint, stringWriter);
		logger.debug("The created ServiceEndpoint profile is\n{}", stringWriter.toString());
		
		return serviceEndpoint;
	}
	
	private void cleanServiceEndpoints(){
		try {
			SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class)
			.addCondition(String.format("$resource/Profile/Category/text() eq '%s'", ContextProvider.get().configuration().serviceClass()))
			.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", ContextProvider.get().configuration().name()))
			.addCondition(String.format("$resource/Profile/RunTime/HostedOn/text() eq '%s'", getRunningOn(ContextProvider.get().container().configuration())))
			.setResult("$resource");
			 
			DiscoveryClient<ServiceEndpoint> client = ICFactory.clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> serviceEndpoints = client.submit(query);
			
			for (ServiceEndpoint serviceEndpoint : serviceEndpoints) {
				try {
					logger.debug("Trying to unpublish the old ServiceEndpoint with ID {} from scope {}", 
							serviceEndpoint.id(), getCurrentScope());
					unPublishResource(serviceEndpoint);
				} catch(Exception e){
					logger.debug("Exception tryng to unpublish the old ServiceEndpoint with ID {} from scope {}", 
							serviceEndpoint.id(), getCurrentScope(), e);
				}
			}
		}catch(Exception e){
			logger.debug("An Exception occur while checking and/or unpublishing old ServiceEndpoint", e);
		}
	}
	
	/** 
	 * {@inheritDoc}
	 * The method discover the plugins available on classpath and their own 
	 * supported capabilities and publish a ServiceEndpoint with the 
	 * discovered information.
	 * Furthermore create/connect to DB
	 */
	@Override
	public void onInit() {
		String scope = getCurrentScope();
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor is Starting on scope {}\n"
				+ "-------------------------------------------------------", 
				scope);
		
		logger.debug("Getting Available Plugins and their own supported capabilities");
		PluginManager pluginManager = PluginManager.getInstance();
		Map<String, PluginDeclaration> availablePlugins = pluginManager.getAvailablePlugins();
		ServiceEndpoint serviceEndpoint = createServiceEndpoint(availablePlugins);
		
		cleanServiceEndpoints();
		
		try {
			publishResource(serviceEndpoint);
		} catch (Exception e) {
			logger.error("Unable to Create ServiceEndpoint for scope {}. The Service will be aborted", scope, e);
			throw new RuntimeException(e);
		}
		
		final SmartExecutorPersistenceConnector smartExecutorPersistenceConnector;
		try {
			smartExecutorPersistenceConnector = SmartExecutorPersistenceFactory.getPersistenceConnector();
		} catch (Exception e) {
			logger.error("Unable to instantiate {} for scope {}. The Service will be aborted",  
					SmartExecutorPersistenceConnector.class.getSimpleName(), scope, e);
			throw new RuntimeException(e);
		}
		
		// TODO set task that are still on running state to FAILED state on 
		// Persistence to clean previous situation of a failure of HostingNode  
		
		try {
			logger.debug("Going to get Orphan Scheduled Tasks");
		
			List<ScheduledTask> scheduledTasks = smartExecutorPersistenceConnector.getOrphanScheduledTasks(availablePlugins.values());
			for(final ScheduledTask scheduledTask : scheduledTasks){
				try {
					// Reserving the task.
					smartExecutorPersistenceConnector.reserveScheduledTask(scheduledTask);
				}catch (Exception e) {
					logger.debug("someone else is going to take in charge the scheduled task. Skipping.");
					continue;
				}

				Thread thread = new Thread(){
					
					@Override
					public void run(){
						LaunchParameter launchParameter = scheduledTask.getLaunchParameter();
						
						SmartExecutorScheduler smartExecutorScheduler = SmartExecutorScheduler.getInstance();
						
						String scheduledTasktoken = scheduledTask.getToken();
						SecurityTokenProvider.instance.set(scheduledTasktoken);
						try {
							// A new Scheduled Task will be persisted due to launch. Removing it 
							smartExecutorPersistenceConnector.removeScheduledTask(scheduledTask);
							smartExecutorScheduler.schedule(launchParameter, scheduledTask.getUUID());
						} catch (Exception e) {
							logger.error("Error while trying to relaunch scheduled task.", e);
							try {
								smartExecutorPersistenceConnector.addScheduledTask(scheduledTask);
							} catch (Exception ex) {
								logger.error("Unable to ");
							}
						}
						
					}
					
				};

				thread.start();

			}
			
		} catch (Exception e) {
			logger.error("Unable to get Orphan Scheduled Tasksfor scope {}.", scope, e);
			return;
		}
		
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor Started Successfully on scope {}\n"
				+ "-------------------------------------------------------", scope);
		
		
	}
	
	/** 
	 * {@inheritDoc} 
	 * This function is invoked before the service will stop and unpublish the 
	 * resource from the IS to maintain the infrastructure integrity.
	 * Furthermore close the connection to DB.
	 */
	@Override
	public void onShutdown(){
		
		
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor is Stopping on scope {}\n"
				+ "-------------------------------------------------------", 
				getCurrentScope());
		
		// TODO release scheduled tasks
		SmartExecutorScheduler.getInstance().stopAll();

		
		cleanServiceEndpoints();
		try {
			SmartExecutorPersistenceFactory.getPersistenceConnector().close();
		} catch (Exception e) {
			logger.error("Unable to correctly close {} for scope {}",  
					SmartExecutorPersistenceConnector.class.getSimpleName(), 
					getCurrentScope(), e);
		}
		
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor Stopped Successfully on scope {}\n"
				+ "-------------------------------------------------------", 
				getCurrentScope());
		
	}
}
