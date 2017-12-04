package org.gcube.common.core.plugins;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;
import org.gcube.common.core.contexts.GCUBEPortTypeContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.contexts.service.Builder;
import org.gcube.common.core.plugins.GCUBEPluginContext.TypeMapping;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.Plugin;
import org.gcube.common.core.utils.events.GCUBEConsumer;
import org.gcube.common.core.utils.events.GCUBEEvent;
import org.gcube.common.core.utils.events.GCUBEProducer;
import org.gcube.common.core.utils.events.GCUBETopic;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.config.ContainerConfig;

/**
 * Partial implementation of <em>plugin managers</code>. 
 * A plugin manager handles the registration, un-registration, and persistence of plugins for and on behalf of a service.
 * @author Fabio Simeoni (University of Strathclyde)
 * @param PLUGINCONTEXT the type of {@link GCUBEPluginContext} expected by subclasses.
 */
public abstract class GCUBEPluginManager<PLUGINCONTEXT extends GCUBEPluginContext> {

	/** Class logger. */
	protected GCUBELog logger = new GCUBELog(this);
	
	/**Plugin directory name */
	public static final String PLUGINS_DIRECTORY_NAME = "plugins";
	
	/** The context of the associated service. */ 
	protected GCUBEServiceContext context;
	
	/**The list of registered plugins.*/
	protected Map<String,PLUGINCONTEXT> plugins = new HashMap<String,PLUGINCONTEXT>();
	
	/** Suffix of plugin serialisations */
	protected static final String PLUGIN_PROFILE_SUFFIX = ".profile";

	
	/**Sets the instance logger.
	 * @param logger the logger.*/
	public void setLogger(GCUBELog logger) {this.logger=logger;}
	
	/**
	 * Initialises the manager with the context of the associated service.
	 * @param context the context.
	 * @param managerProfile the profile of the plugin manager.
	 * @throws Exception if the manager could not be initialised.
	 */
	public void initialise(GCUBEServiceContext context, GCUBEPluginManagerProfile managerProfile) throws Exception{
		this.context=context;
		File[] plugins = this.context.getPersistentFile(PLUGINS_DIRECTORY_NAME).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(PLUGIN_PROFILE_SUFFIX);}});
		if (plugins!=null) 
			for (File profile : plugins) { 
				try{
					GCUBEService plugin = GHNContext.getImplementation(GCUBEService.class);
					plugin.load(new FileReader(profile));
					this.registerPlugin(plugin,false);
				}
				catch(Exception e) {
					logger.warn("could not register plugin in "+profile,e);
				}
			}
	}
	
	/**
	 * Returns the type of {@link GCUBEPluginContext} expected by the associated service.
	 * @return the type.
	 */
	protected abstract Class<PLUGINCONTEXT> getMainClass();
	
	/**
	 * Validates and instantiate the main class of the plugin.
	 * <p>
	 * By default, the class is valid if it is compatible with {@link GCUBEPluginContext} and can be reflectively instantiated. 
	 * Subclasses <em>may</code> extend the default implementation to guarantee that further constraints are met (e.g. that it implements required interfaces).
	 * @return an instance of the main class<code>true</code> if the main class is valid, <code>false</code> otherwise.
	 * @throws Exception if the class is not valid.
	 */
	public PLUGINCONTEXT validateMainClass(Class<?> contextClass) throws Exception {
		if (!getMainClass().isAssignableFrom(contextClass)) throw new Exception(contextClass.getSimpleName()+" is not a subclass of "+GCUBEPluginContext.class.getSimpleName());
		try {return (PLUGINCONTEXT) contextClass.newInstance();} 
		catch(Exception e) {throw new Exception(contextClass.getSimpleName()+" cannot be reflectively instantiated",e);}
	}
	
	/**
	 * Returns the contexts of all the plugins that are currently registered with the manager.
	 * @return the plugin contexts, indexed by name.
	 */
	public Map<String,PLUGINCONTEXT> getPlugins() {return Collections.unmodifiableMap(plugins);}
	
	/**
	 * Register a plugin with the manager.
	 * @param plugin the {@link GCUBEService} resource that models the plugin.
	 * @param persisted (optional) <code>true</code> if the plugin ought to be persisted (default), <code>false</code> otherwise.
	 * @throws PluginAlreadyRegisteredException if the plugin is already registered 
	 * @throws Exception if the plugin could not be registered.
	 */
	public synchronized void registerPlugin(GCUBEService plugin, boolean ... persisted) throws PluginAlreadyRegisteredException, Exception {
		
		//validate input
		if (plugin==null) throw new IllegalArgumentException();
		if (plugins.containsKey(plugin.getServiceName())) 
			throw new PluginAlreadyRegisteredException("plugin "+plugin.getServiceName()+" already registered");
		
		//obtain main class name
		String mainClassName=null;
		try {
			mainClassName = ((Plugin) plugin.getPackages().get(0)).getEntryPoint();
		}
		catch(Exception e) {throw new Exception("invalid plugin: could not find main class",e);}
		

		//validate main class
		Class<?> mainClass = null;
		try{
			//uses the thread's classloader in case plugins are governed by a separate classloader
			mainClass = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);
		}
		catch(Exception e) {
			throw new Exception("class "+mainClassName+" not on classpath",e);
		}
		PLUGINCONTEXT context = null;
		try {context = validateMainClass(mainClass);}
		catch(Exception e) {throw new Exception(mainClass.getSimpleName()+" is not compatible with "+this.getMainClass().getSimpleName()+" expected by "+this.context.getName(),e);}
		context.initialise(plugin);
		
		//add type mappings
		//registering new type bindings, if any
		WSDDEngineConfiguration engineConfig = (WSDDEngineConfiguration) ContainerConfig.getEngine().getConfig(); 
		
		for (GCUBEPortTypeContext ptContext : this.getPortTypeContexts()) {
			SOAPService service = engineConfig.getDeployment().getService(new QName(ptContext.getJNDIName()));
			TypeMappingRegistry registry = service.getTypeMappingRegistry();
			if (registry!=null)
			  for (TypeMapping mapping : context.getTypeMappings())
				try {
					registry.getTypeMapping("").register(mapping.clazz,mapping.qname,mapping.sFactory,mapping.dfactory);
					logger.info("registered type mapping for "+mapping.clazz.getSimpleName()+" on "+ptContext.getName());
				}catch (Exception e) {
					logger.warn("could not register plugin type mapping for "+mapping.clazz.getSimpleName()+" on "+ptContext.getName(),e);
				}
		}	
		
		if (persisted==null || persisted.length==0 || persisted[0]==true)
			plugin.store(new FileWriter(this.context.getPersistentFile(PLUGINS_DIRECTORY_NAME+File.separatorChar+plugin.getServiceName()+PLUGIN_PROFILE_SUFFIX,true)));
		
		plugins.put(plugin.getServiceName(),context);
		this.producer.notify(PluginTopic.REGISTRATION, new PluginEvent(context));
		new Builder(this.context).addPlugin(plugin);
		this.context.setStatus(Status.UPDATED);
		logger.info("registered plugin "+plugin.getServiceName());
		
	}
	
	/**
	 * DeRegister a plugin from the manager.
	 * @param name the name of the plugin.
	 * @throws Exception if the plugin did not exist or could not be deregistered.
	 */
	public synchronized void deregisterPlugin(String name) throws Exception {
		PLUGINCONTEXT context = plugins.get(name); 
		if (context==null) throw new Exception("plugin "+name+" is not registered");
		if (!this.context.getPersistentFile(PLUGINS_DIRECTORY_NAME+File.separatorChar+name+PLUGIN_PROFILE_SUFFIX,true).delete())
			throw new Exception("Plugin could not be removed form persisted store");
			
		plugins.remove(name);
		new Builder(this.context).removePlugin(context.getPlugin());		
		this.context.setStatus(Status.UPDATED);
		this.producer.notify(PluginTopic.DEREGISTRATION,new PluginEvent(context));
		
	}
	/**
	 * Returns the contexts of the port-types of the associated service, 
	 * so as to configure type mappings for them if any are provided by
	 * plugins via the associated {@link GCUBEPluginContext}).
	 * @return the contexts.
	 */
	abstract protected GCUBEPortTypeContext[] getPortTypeContexts();
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////Event Management
	
	/** Enumeration of plugin management topics.*/
	public static enum PluginTopic implements GCUBETopic {REGISTRATION,DEREGISTRATION}
	
	/** A model of plugin management events with a plugin context payload.*/
	public class PluginEvent  extends GCUBEEvent<PluginTopic, PLUGINCONTEXT>{
		/**Creates a new instance from a plugin context.
		 * @param c the context.*/
		PluginEvent(PLUGINCONTEXT c) {this.setPayload(c);}
	}

	/**The inner producer of plugin management event.*/
	protected GCUBEProducer<PluginTopic,PLUGINCONTEXT> producer = new GCUBEProducer<PluginTopic,PLUGINCONTEXT>();

	/**
	 * Base class for consumers of plugin management events.
	 * @param <PLUGINCONTEXT> the type of of plugin context.
	 */
	public static class PluginConsumer<PLUGINCONTEXT extends GCUBEPluginContext> implements GCUBEConsumer<PluginTopic, PLUGINCONTEXT> {
		/**{@inheritDoc}*/
		public <T1 extends GCUBEPluginManager.PluginTopic, P1 extends PLUGINCONTEXT> void onEvent(GCUBEEvent<T1,P1> ... events) {
			for (GCUBEEvent<T1, P1> event : events) {
				PluginTopic topic = event.getTopic();
				switch(topic) {
					case REGISTRATION : onRegistration(event);break;
					case DEREGISTRATION : onDeregistration(event);break;
				}
			}
		};
		/**Callback for a registration event.
		 * @param event the event.*/
		protected void onRegistration(GCUBEEvent<? extends PluginTopic, ? extends PLUGINCONTEXT> event) {}
		/**Callback for a deregistration event.
		 * @param event the event.*/
		protected void onDeregistration(GCUBEEvent<? extends PluginTopic, ? extends PLUGINCONTEXT> event) {}
	}

	/**
	 * Subscribes a consumer to one or more registration events.
	 * @param c the consumer.
	 * @param topics (optional) topics the topics, all if omitted. 
	 */
	public void subscribe(PluginConsumer<PLUGINCONTEXT> c,PluginTopic ... topics) {this.producer.subscribe(c, topics.length==0?PluginTopic.values():topics);}
	/**
	 * Unsubscribes a consumer from one or more registration events.
	 * @param c the consumer.
	 * @param topics (optional) topics the topics, all if omitted. 
	 */
	public void unsubscribe(PluginConsumer<PLUGINCONTEXT> c, PluginTopic ... topics) {this.producer.subscribe(c, topics.length==0?PluginTopic.values():topics);}

	/** Plugin already registered exception*/
	public static class PluginAlreadyRegisteredException extends Exception{
		private static final long serialVersionUID = 1L;
		public PluginAlreadyRegisteredException(String msg) {super(msg);}
	}

}
