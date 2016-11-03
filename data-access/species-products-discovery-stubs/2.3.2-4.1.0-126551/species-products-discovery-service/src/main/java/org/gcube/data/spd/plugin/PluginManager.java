package org.gcube.data.spd.plugin;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringReader;
import java.net.URI;
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
import net.sf.ehcache.ObjectExistsException;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.types.VOID;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.caching.MyCacheEventListener;
import org.gcube.data.spd.context.ServiceContext;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.remoteplugin.RemotePlugin;
import org.gcube.data.spd.stubs.GetSupportedPluginsResponse;
import org.gcube.data.spd.stubs.ManagerPortType;
import org.gcube.data.spd.stubs.service.ManagerServiceAddressingLocator;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class PluginManager{

	private static Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	private static final int CACHE_ENTRIES_PER_PLUGIN =500;
	
	private ServiceLoader<AbstractPlugin> loader;
	private Map<String,AbstractPlugin> plugins = new HashMap<String, AbstractPlugin>();
	private String scope;
	
	private static Map<String, PluginManager> pluginManagersCache = new HashMap<String, PluginManager>();
	
	private EnumMap<Capabilities, Set<AbstractPlugin>> pluginsPerCapability= new EnumMap<Capabilities, Set<AbstractPlugin>>(Capabilities.class);
	
	
	public static void removeInstance(){
		String scope = ScopeProvider.instance.get();
		if(pluginManagersCache.containsKey(scope))
			pluginManagersCache.remove(scope);
	}
	
	public static PluginManager get(){
		String scope = ScopeProvider.instance.get();
		log.trace("the scope for plugin is "+scope);
		if(pluginManagersCache.containsKey(scope))
			return pluginManagersCache.get(scope);
		else {
			PluginManager pm = new PluginManager(scope);
			pluginManagersCache.put(scope, pm);
			return pm;
		}
	}
	
	
	
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
	private PluginManager(String scope) { 
		log.debug("entering in the PluginManager");
		this.scope = scope;
		initializePlugins();
	}

	//update the pluginManager with a new plugin when a runtimeresource is added in its scope
	public AbstractPlugin add(ServiceEndpoint resource){
		retrievePlugins(Collections.singletonMap(resource.profile().name(), resource));
		return plugins.get(resource.profile().name());
	}
	
	public void update(ServiceEndpoint resource){
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
	}
	
	/**
	 * Returns the installed plugins, indexed by name.
	 * @return the plugins
	 */
	public Map<String,AbstractPlugin> plugins() {
		return plugins;
	}
		
	public String getScope() {
		return scope;
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
						
		for (GCoreEndpoint address: retrieveTwinServicesAddresses()) {
			PluginDescription[] pluginDescriptions =null;
			try {
				URI uri = null;
				for (Endpoint endpoint : address.profile().endpoints())
					if (endpoint.name().equals("gcube/data/speciesproductsdiscovery/manager")){
						uri = endpoint.uri();
						break;
					}
				if (uri!=null){
					ManagerPortType managerPt = new ManagerServiceAddressingLocator().getManagerPortTypePort(new EndpointReferenceType(new Address(uri.toString())));
					managerPt = GCUBERemotePortTypeContext.getProxy(managerPt);
					GetSupportedPluginsResponse supportedPlugins = managerPt.getSupportedPlugins(new VOID());
					if (supportedPlugins==null || supportedPlugins.getPluginDescriptions()==null){
						log.warn("error contacting twin service in "+uri);
						break;
					}
					String[] streamedDescriptions = supportedPlugins.getPluginDescriptions();
					pluginDescriptions = new PluginDescription[streamedDescriptions.length];
					int i=0;
					for (String streamedDescr : streamedDescriptions)
						pluginDescriptions[i++]= (PluginDescription)new XStream().fromXML(new StringReader(streamedDescr));
					
				}
			}catch (Exception e) {
				log.warn("error contacting address "+address,e);
				continue;
			}
			
			if (pluginDescriptions==null) continue;
			
			URI dispatcherUri = null;
			for (Endpoint endpoint : address.profile().endpoints())
				if (endpoint.name().equals("gcube/data/speciesproductsdiscovery/remotedispatcher")){
					dispatcherUri = endpoint.uri();
					break;
				}
			
			log.trace("plugins in Pluginmanager are "+plugins.keySet());
			
			for (PluginDescription description : pluginDescriptions )
				try{
					if (!plugins.containsKey(description.getName()) && !description.isRemote()){
						RemotePlugin plugin = new RemotePlugin();
						plugin.remoteIntitializer(description, dispatcherUri.toString());
						log.debug("found remote plugin for "+plugin.getRepositoryName());
						checkPlugin(plugin);
						//initializing cache per plugin
						if (plugin.isUseCache()) createCache(plugin.getRepositoryName());
						log.trace("created remote plugin "+plugin.getRepositoryName()+" with uri "+plugin.getRemoteUris());
					}else {
						AbstractPlugin plugin = plugins.get(description.getName());
						if(plugin.isRemote()){
							((RemotePlugin) plugin).addUrl(dispatcherUri.toString());
							log.trace("added uri "+dispatcherUri.toString()+" to remote plugin "+dispatcherUri.toString()); 
						}

					}
				}catch (Exception e) {
					log.error("initialization failed for remote plugin "+description.getName(),e);
				}
		}
	}

	
	
	private List<GCoreEndpoint> retrieveTwinServicesAddresses(){
		List<GCoreEndpoint> addresses = Collections.emptyList();
		
		try{
			SimpleQuery query = queryFor(GCoreEndpoint.class);

			query.addCondition("$resource/Profile/ServiceName/text() eq '"+Constants.SERVICE_NAME+"'")
				.addCondition("$resource/Profile/ServiceClass/text() eq '"+Constants.SERVICE_CLASS+"'")
				.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'") 
				.addCondition("not($resource/Profile/GHN[@UniqueID='"+GHNContext.getContext().getGHNID()+"'])");
			//gcube/data/speciesproductsdiscovery/manager
			DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
						
			addresses = client.submit(query);
				
		}catch(Exception e){
			log.warn("error discoverying twin services",e);
		}
		
		log.trace("retieved "+addresses.size()+" gcore endpoints in "+ScopeProvider.instance.get());
		
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
			.diskStorePath(ServiceContext.getContext().getPersistenceRoot().getAbsolutePath()));

			pluginCache.getCacheEventNotificationService().registerListener(new MyCacheEventListener());

			ServiceContext.getContext().getCacheManager().addCache(pluginCache);
			log.trace("cache created for plugin "+ pluginName);
		}catch (ObjectExistsException e) {
			log.warn("the cache for plugin "+pluginName+" already exists");
			log.trace("the size is "+ ServiceContext.getContext().getCacheManager().getCache(pluginName).getSize());
		}
	}
	
	private void  initializePlugins(){

		log.trace("initializing plugins");
		if (loader==null){
			log.trace("loader is null");
			loader=ServiceLoader.load(AbstractPlugin.class);
		}
		
		log.trace("plugin laoded");
		
		ScopeProvider.instance.set(this.scope);
		log.trace("scope rpovider set");
		
		Map<String, ServiceEndpoint> runtimeResourcePerPlugin = new HashMap<String, ServiceEndpoint>();
		try{
			SimpleQuery query = queryFor(ServiceEndpoint.class);

			query.addCondition("$resource/Profile/Category/text() eq '"+ServiceContext.getContext().getRuntimeResourceCategory()+"'");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> resources = client.submit(query);

			for (ServiceEndpoint resource: resources)
				runtimeResourcePerPlugin.put(resource.profile().name(), resource);
		}catch(Exception e){
			log.warn("error discoverying runtime resources",e);
		}

		log.trace("runtime resources retrieved");
		retrievePlugins(runtimeResourcePerPlugin);
		log.trace("retrieving remote plugin");
		retrieveRemotePlugins();
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
}
