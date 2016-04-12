/**
 * 
 */
package org.gcube.vremanagement.executor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Profile;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.ServiceEndpoint.Runtime;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.AdvancedScopedPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleEvent;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceFactory;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.pluginmanager.PluginManager;
import org.gcube.vremanagement.executor.scheduler.SmartExecutorScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
@XmlRootElement(name = "plugin-registration-handler")
public class SmartExecutorInitializator extends ApplicationLifecycleHandler {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(SmartExecutorInitializator.class);
	
	public static final long JOIN_TIMEOUT = 1000;
	
	/* *
	 * Contains the ServiceEnpoint Resource to be published/unpublished on IS
	 * /
	private static ServiceEndpoint serviceEndpoint;
	*/
	
	/* *
	 * The application context
	 * /
	protected static ApplicationContext ctx;
	
	/**
	 * @return the ctx
	 * /
	public static ApplicationContext getCtx() {
		return ctx;
	}
	*/
	
	/*
	protected static ScheduledTaskConfiguration launchConfiguration;
	
	/**
	 * @return the configuredTasks
	 * /
	public static ScheduledTaskConfiguration getConfiguredTasks() {
		return launchConfiguration;
	}
	*/
	
	/**
	 * Publish the provided resource on all Service Scopes retrieved from 
	 * Context
	 * @param resource to be published
	 * @throws RegistryNotFoundException if the Registry is not found so the
	 * resource has not be published
	 */
	private static void publishScopedResource(Resource resource, List<String> scopes) throws RegistryNotFoundException, Exception {
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(resource, stringWriter);
		
		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
	    try {
			logger.debug("Trying to publish to {}:\n{}", scopes, stringWriter);
	    	scopedPublisher.create(resource, scopes);
		} catch (RegistryNotFoundException e) {
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
	private static void unPublishScopedResource(Resource resource) throws RegistryNotFoundException, Exception {
		//StringWriter stringWriter = new StringWriter();
		//Resources.marshal(resource, stringWriter);
		
	    ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
	    AdvancedScopedPublisher advancedScopedPublisher = new AdvancedScopedPublisher(scopedPublisher);
	    
	    String id = resource.id();
	    logger.debug("Trying to remove {} with ID {} from {}", resource.getClass().getSimpleName(), id, ScopeProvider.instance.get());
	    
		//scopedPublisher.remove(resource, scopes);
		advancedScopedPublisher.forceRemove(resource);
		
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
	protected static ServiceEndpoint createServiceEndpoint(){
		logger.debug("Getting Available Plugins and their own supported capabilities");
		PluginManager pluginManager = PluginManager.getInstance();

		logger.debug("Creating ServiceEndpoint to publish on IS available plugins and their own supported capabilities");
		ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
		Profile profile = serviceEndpoint.newProfile();
		profile.category(SmartExecutorImpl.ctx.configuration().serviceClass());
		profile.name(SmartExecutorImpl.ctx.configuration().name());
		String version = SmartExecutorImpl.ctx.configuration().version();
		profile.version(version);
		profile.description(SmartExecutorImpl.ctx.configuration().description());
		
		String runningOn = getRunningOn(SmartExecutorImpl.ctx.container().configuration());
		Platform platform = profile.newPlatform();
		platform.name(runningOn);
		
		short[] versionSlices = getVersionSlice(version, 4);
		platform.version(versionSlices[0]);
		platform.minorVersion(versionSlices[1]);
		platform.buildVersion(versionSlices[2]);
		platform.revisionVersion(versionSlices[3]);
		
		Runtime runtime = profile.newRuntime();
		runtime.hostedOn(runningOn);
		runtime.status(SmartExecutorImpl.ctx.configuration().mode().toString());
		
		Group<AccessPoint> accessPoints = profile.accessPoints();
		Map<String, PluginDeclaration> availablePlugins = pluginManager.getAvailablePlugins();
		
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
	
	public static List<String> getScopes(ApplicationContext applicationContext){
		Collection<String> scopes; 
		
		ScopeGroup<String> scopeGroup = applicationContext.profile(GCoreEndpoint.class).scopes();
		if(scopeGroup==null || scopeGroup.isEmpty()){
			Set<String> applicationScopes = applicationContext.configuration().startScopes();
			Set<String> containerScopes = applicationContext.container().configuration().startScopes();

			if(applicationScopes==null || applicationScopes.isEmpty()){
				scopes = containerScopes;
				logger.debug("Application Scopes ({}). The Container Scopes ({}) will be used.", applicationScopes, scopes);
			} else{
				logger.debug("Container Scopes ({}). Application Scopes ({}) will be used.", containerScopes, applicationScopes);
				scopes = new HashSet<String>(applicationScopes);
			}
		}else {
			scopes = scopeGroup.asCollection();
		}
		
		return new ArrayList<String>(scopes);
	}
	
	private void cleanServiceEndpoints(String scope){
		try {
			SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class)
			.addCondition(String.format("$resource/Profile/Category/text() eq '%s'", SmartExecutorImpl.ctx.configuration().serviceClass()))
			.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", SmartExecutorImpl.ctx.configuration().name()))
			.addCondition(String.format("$resource/Profile/RunTime/HostedOn/text() eq '%s'", getRunningOn(SmartExecutorImpl.ctx.container().configuration())))
			.setResult("$resource");
			 
			DiscoveryClient<ServiceEndpoint> client = ICFactory.clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> serviceEndpoints = client.submit(query);
			
			for (ServiceEndpoint serviceEndpoint : serviceEndpoints) {
				try {
					logger.debug("Trying to unpublish the old ServiceEndpoint with ID {} from scope {}", 
							serviceEndpoint.id(), scope);
					unPublishScopedResource(serviceEndpoint);
				} catch(Exception e){
					logger.debug("Exception tryng to unpublish the old ServiceEndpoint with ID {} from scope {}", 
							serviceEndpoint.id(), scope, e);
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
	public void onStart(ApplicationLifecycleEvent.Start applicationLifecycleEventStart) {
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor is Starting\n"
				+ "-------------------------------------------------------");
		
		SmartExecutorImpl.ctx = applicationLifecycleEventStart.context();
		
		ServiceEndpoint serviceEndpoint = createServiceEndpoint();
		
		// Checking if there are old unpublished ServiceEndpoints related to 
		// this vHN and trying to unpublish them
		List<String> scopes = getScopes(SmartExecutorImpl.ctx);
		
		for(String scope : scopes){
			ScopeProvider.instance.set(scope);
			cleanServiceEndpoints(scope);
			try {
				SmartExecutorPersistenceFactory.getPersistenceConnector();
			} catch (Exception e) {
				logger.error("Unable to isntantiate {} for scope {}",  
						SmartExecutorPersistenceConnector.class.getSimpleName(), scope, e);
				throw new RuntimeException(e);
			}
		}
		
		// TODO set task that are still on running state on DB to have a clear
		// room
		
		try {
			publishScopedResource(serviceEndpoint, scopes);
		} catch (RegistryNotFoundException e) {
			logger.error("Unable to Create ServiceEndpoint. the Service will be aborted", e);
			return;
		} catch (Exception e) {
			logger.error("Unable to Create ServiceEndpoint. the Service will be aborted", e);
			return;
		}
		
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor Started Successfully\n"
				+ "-------------------------------------------------------");
		
		// TODO Launch initializer thread

		
	}
	
	/** 
	 * {@inheritDoc} 
	 * This function is invoked before the service will stop and unpublish the 
	 * resource from the IS to maintain the infrastructure integrity.
	 * Furthermore close the connection to DB.
	 */
	@Override
	public void onStop(ApplicationLifecycleEvent.Stop applicationLifecycleEventStop) {
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor is Stopping\n"
				+ "-------------------------------------------------------");
		
		SmartExecutorScheduler.getInstance().stopAll();
		
		List<String> scopes = getScopes(SmartExecutorImpl.ctx);
		
		for(String scope : scopes){
			ScopeProvider.instance.set(scope);
			cleanServiceEndpoints(scope);
			try {
				SmartExecutorPersistenceFactory.getPersistenceConnector().close();
			} catch (Exception e) {
				logger.error("Unable to correctly close {} for scope {}",  
						SmartExecutorPersistenceConnector.class.getSimpleName(), scope, e);
			}
		}
		
		logger.trace(
				"\n-------------------------------------------------------\n"
				+ "Smart Executor Stopped Successfully\n"
				+ "-------------------------------------------------------");
		
	}
}
