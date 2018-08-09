package org.gcube.data.spd.plugin;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.caching.MyCacheEventListener;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.service.types.PluginDescriptions;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.remoteplugin.RemotePlugin;
import org.gcube.data.spd.utils.Utils;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManager{

	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	private static final int CACHE_ENTRIES_PER_PLUGIN =500;
	private static final String RESOURCE_CATEGORY ="BiodiversityRepository";
	
	private ServiceLoader<AbstractPlugin> loader;
	private Map<String,AbstractPlugin> plugins = new HashMap<String, AbstractPlugin>();
	
	private ApplicationContext ctx;
	
	private EnumMap<Capabilities, Set<AbstractPlugin>> pluginsPerCapability= new EnumMap<Capabilities, Set<AbstractPlugin>>(Capabilities.class);
		
			
	public Set<AbstractPlugin> getPluginsPerCapability(Capabilities capability, Collection<AbstractPlugin>  plugins){
		Set<AbstractPlugin> returnSet = new HashSet<AbstractPlugin>();
		if (pluginsPerCapability.containsKey(capability)){
			for (AbstractPlugin plugin : plugins)
				if (pluginsPerCapability.get(capability).contains(plugin)) returnSet.add(plugin);
			return Collections.unmodifiableSet(returnSet);
		}else return Collections.emptySet();		
	}
	
	public Set<AbstractPlugin> getPluginsPerCapability(Capabilities capability){
		if (pluginsPerCapability.containsKey(capability))
			return Collections.unmodifiableSet(pluginsPerCapability.get(capability));
		else return Collections.emptySet();
	}
	
	/**
	 * Creates a new instance, installing all the plugins found on the classpath.
	 */
	public PluginManager(ApplicationContext context) { 
		log.debug("creating the plugin manager");
		this.ctx = context;
		initializePlugins();
	}

	//update the pluginManager with a new plugin when a runtimeresource is added in its scope
	public void addRemotePlugins(List<PluginDescription> remotePluginDescriptions, String gCoreEndpointId){
		for (PluginDescription description : remotePluginDescriptions )
			try{
				if (!plugins.containsKey(description.getName()) && !description.isRemote()){
					RemotePlugin plugin = new RemotePlugin();
					plugin.remoteIntitializer(description, gCoreEndpointId);
					log.debug("found remote plugin for "+plugin.getRepositoryName());
					checkPlugin(plugin);
					//initializing cache per plugin
					if (plugin.isUseCache()) createCache(plugin.getRepositoryName());
					log.trace("created remote plugin "+plugin.getRepositoryName()+" with endpoints id "+plugin.getRemoteUris());
				}else {
					AbstractPlugin plugin = plugins.get(description.getName());
					if(plugin.isRemote()){
						((RemotePlugin) plugin).addUrl(gCoreEndpointId);
						log.trace("added remote Plugin "+plugin.getRepositoryName()+" from endpoint id "+gCoreEndpointId); 
					}

				}
			}catch (Exception e) {
				log.error("initialization failed for remote plugin "+description.getName(),e);
			}
	}
	
	/*public void update(ServiceEndpoint resource){
		try {
			if (!resource.scopes().contains(this.scope.toString()))
				this.removePlugin(resource.profile().name());
			else if (!plugins.containsKey(resource.profile().name())) {
				add(resource);
			}else
				plugins.get(resource.profile().name()).initialize(resource);
		} catch (Exception e) {
			log.error("error updateting plugin "+resource.profile().name(),e);
		}
	}*/
	
	/**
	 * Returns the installed plugins, indexed by name.
	 * @return the plugins
	 */
	public Map<String,AbstractPlugin> plugins() {
		return plugins;
	}
		
	private void retrievePlugins(Map<String, ServiceEndpoint> runtimeResourcePerPlugin){
		for (AbstractPlugin plugin : loader) {
			
			ServiceEndpoint resource=null;
			
			if ((resource=runtimeResourcePerPlugin.get(plugin.getRepositoryName()))==null)
				continue;
			
			log.debug("found a repo plugin for "+plugin.getRepositoryName());
			if (plugin.getRepositoryName()==null) {
				log.error("plugin "+plugin.getClass().getSimpleName()+" has a null repository name");
				continue;
			}
			
			if (plugin.getRepositoryName().contains(":")) {
				log.error("plugin "+plugin.getClass().getSimpleName()+" contains an invalid character");
				continue;
			}
			
			if (plugin.getDescription()==null) {
				log.warn("plugin "+plugin.getClass().getSimpleName()+" has a null description");
				continue;
			}	
			try{
				if(!plugin.isInitialized()){
					plugin.initialize(resource);
					log.debug("initialization finished for plugin "+plugin.getRepositoryName());
				}
				
				checkPlugin(plugin);
					
				//initializing cache per plugin
				if (plugin.isUseCache()) createCache(plugin.getRepositoryName());
			}catch (Exception e) {
				log.error("initialization failed for plugin "+plugin.getRepositoryName(),e);
			}
		}
				
	}

	
	public void retrieveRemotePlugins(){
		
		List<PluginDescription> descriptions = new ArrayList<PluginDescription>(plugins.size());
		for (AbstractPlugin plugin : plugins.values())
			if (!plugin.isRemote())
				descriptions.add(Utils.getPluginDescription(plugin));
		PluginDescriptions myDescriptions = new PluginDescriptions(descriptions);
		
		
		
		for (GCoreEndpoint address: retrieveTwinServicesAddresses()) {
			String endpointId = ctx.profile(GCoreEndpoint.class).id();
			List<PluginDescription> pluginDescriptions =null;
			URI uri = null;
			try {
				for (Endpoint endpoint : address.profile().endpoints())
					if (endpoint.name().equals("remote-dispatcher")){
						uri = endpoint.uri();
						break;
					}
				if (uri!=null){
					//TODO : call remote rest service
					//RemoteDispatcher remoteDispatcher = org.gcube.data.spd.client.Constants.getRemoteDispatcherService(uri.toString());
					//pluginDescriptions = remoteDispatcher.exchangePlugins(myDescriptions, endpointId).getDescriptions();
				}
			}catch (Throwable e) {
				log.warn("error contacting remote plugin hosted on a Whn id "+address.profile().ghnId());
				continue;
			}
			
			if (pluginDescriptions==null) continue;
			
			
			log.trace("plugins in Pluginmanager are {} ",plugins.keySet().toString());
			
			addRemotePlugins(pluginDescriptions, endpointId);
		}
	}
		
	
	private List<GCoreEndpoint> retrieveTwinServicesAddresses(){
		List<GCoreEndpoint> addresses = Collections.emptyList();
		log.info("retreiving twin services in context {} ",ScopeProvider.instance.get());
		try{
			SimpleQuery query = queryFor(GCoreEndpoint.class);

			query.addCondition("$resource/Profile/ServiceName/text() eq '"+Constants.SERVICE_NAME+"'")
				.addCondition("$resource/Profile/ServiceClass/text() eq '"+Constants.SERVICE_CLASS+"'")
				.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'") 
				.addCondition("not($resource/Profile/GHN[@UniqueID='"+ctx.container().profile(HostingNode.class).id()+"'])");
			//gcube/data/speciesproductsdiscovery/manager
			DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
						
			addresses = client.submit(query);
				
		}catch(Exception e){
			log.warn("error discoverying twin services",e);
		}
		
		log.trace("retieved "+addresses.size()+" gcore endpoints");
		
		return addresses;
	}
	
	private void checkPlugin(AbstractPlugin plugin){
		plugins.put(plugin.getRepositoryName(),plugin);
		for (Capabilities capability :plugin.getSupportedCapabilities()){
			if (pluginsPerCapability.containsKey(capability))
				pluginsPerCapability.get(capability).add(plugin);
			else {
				HashSet<AbstractPlugin> pluginsSet = new HashSet<AbstractPlugin>();
				pluginsSet.add(plugin);
				pluginsPerCapability.put(capability, pluginsSet);
			}
		}
		
	}
	
	private  void createCache(String pluginName){
		try{

			Cache pluginCache = new Cache( new CacheConfiguration(pluginName, CACHE_ENTRIES_PER_PLUGIN)
			.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
			.overflowToDisk(false)
			.eternal(false)
			.timeToLiveSeconds(60*60*24*7)
			.timeToIdleSeconds(0)
			.diskPersistent(true)
			.diskExpiryThreadIntervalSeconds(0)
			.diskStorePath(ctx.persistence().location()));

			pluginCache.getCacheEventNotificationService().registerListener(new MyCacheEventListener());

			CacheManager.getInstance().addCache(pluginCache);
			log.trace("cache created for plugin "+ pluginName);
		}catch (ObjectExistsException e) {
			log.warn("the cache for plugin "+pluginName+" already exists");
			log.trace("the size is "+ CacheManager.getInstance().getCache(pluginName).getSize());
		}
	}
	
	private void  initializePlugins(){

		log.trace("initializing plugins");
		if (loader==null){
			log.warn("ServiceLoader is null intializing plugins");
			loader=ServiceLoader.load(AbstractPlugin.class);
		}
				
		Map<String, ServiceEndpoint> runtimeResourcePerPlugin = new HashMap<String, ServiceEndpoint>();
		try{
			SimpleQuery query = queryFor(ServiceEndpoint.class);

			query.addCondition("$resource/Profile/Category/text() eq '"+RESOURCE_CATEGORY+"'");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> resources = client.submit(query);

			for (ServiceEndpoint resource: resources)
				runtimeResourcePerPlugin.put(resource.profile().name(), resource);
		}catch(Exception e){
			log.warn("error discoverying runtime resources",e);
		}

		retrievePlugins(runtimeResourcePerPlugin);
		//TODO : reintroduce it to discovery twin services // retrieveRemotePlugins();
	}	

		
	public void removePlugin(String pluginName){
		AbstractPlugin plugin = this.plugins.get(pluginName);
		
		for (Capabilities capability :plugin.getSupportedCapabilities()){
			if (pluginsPerCapability.containsKey(capability)){
				pluginsPerCapability.get(capability).remove(plugin);
				if (pluginsPerCapability.get(capability).size()==0)
					pluginsPerCapability.remove(capability);
			}
		}
		this.plugins.remove(pluginName);
	}
	
	public void removePlugins() {
		initializePlugins();		
	}

	public void removeRemotePlugin(String gCoreEndpointId) {
		List<String> pluginToRemove = new ArrayList<String>();
		for (AbstractPlugin plugin : plugins.values())
			if (plugin.isRemote()){
				RemotePlugin rPlugin =(RemotePlugin) plugin;
				rPlugin.getRemoteUris().remove(gCoreEndpointId);
				if (rPlugin.getRemoteUris().isEmpty())
					pluginToRemove.add(rPlugin.getRepositoryName());
				
			}
		for (String pluginName: pluginToRemove){
			log.info("removing remote plugin {}", pluginName);
			this.removePlugin(pluginName);
		}
	}
	
	public void shutdown(){
		notifyRemoteServicesOnShutdown();
	}
	
	private void notifyRemoteServicesOnShutdown(){
		for (GCoreEndpoint address: retrieveTwinServicesAddresses()) {
			String endpointId = ctx.profile(GCoreEndpoint.class).id();
			URI uri = null;
			try {
				for (Endpoint endpoint : address.profile().endpoints())
					if (endpoint.name().equals("remote-dispatcher")){
						uri = endpoint.uri();
						break;
					}
				if (uri!=null){
					//TODO : call remote rest service
					//RemoteDispatcher remoteDispatcher = org.gcube.data.spd.client.Constants.getRemoteDispatcherService(uri.toString());
					//remoteDispatcher.removeAll(endpointId);
				}
			}catch (Throwable e) {
				log.warn("error contacting remote plugin hosted on a Whn id "+address.profile().ghnId());
				continue;
			}
		}
	}
}
