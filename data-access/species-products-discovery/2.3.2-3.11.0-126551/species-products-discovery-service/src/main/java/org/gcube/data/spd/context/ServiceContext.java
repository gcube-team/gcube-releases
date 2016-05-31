package org.gcube.data.spd.context;



import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.namespace.QName;

import net.sf.ehcache.CacheManager;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.resources.GCUBEResource.AddScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.RemoveScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.ResourceConsumer;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.impl.ScopedTasks;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.consumers.RuntimeResourceConsumer;
import org.gcube.data.spd.executor.ExecutorPT;
import org.gcube.data.spd.executor.jobs.SpeciesJob;
import org.gcube.data.spd.manager.Manager;
import org.gcube.data.spd.manager.ManagerPTContext;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;

public class ServiceContext extends GCUBEServiceContext{
	
	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	public static ServiceContext getContext() {return cache;}
	
	private static final int MAX_SEARCH_THREAD_POOL= 100;
	
	private static final int MAX_JOB_POOL= 10;
	
	private ISNotifier notifier;
	
	private ExecutorService searchThreadPool;
	
	private ExecutorService jobThreadPool;

	
	private CacheManager cacheManager;
	
	/** Prevents accidental creation of more instances */
	private ServiceContext(){}

	private String runtimeResourceCategory;
	
	@Override
	protected String getJNDIName() {
		return Constants.JNDI_NAME;
	};
	
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Override
	protected void onInitialisation() throws Exception {
		runtimeResourceCategory = (String)ServiceContext.getContext().getProperty("runtimeResourceCategory", true);
		notifier= GHNContext.getImplementation(ISNotifier.class);
		DBSession.initialize("org.gcube.dbinterface.h2", "sa", "", "file:"+this.getPersistenceRoot().getAbsolutePath()+"/spd-cache;MVCC=true");
		
		this.searchThreadPool = Executors.newFixedThreadPool(MAX_SEARCH_THREAD_POOL);
		
		this.jobThreadPool = Executors.newFixedThreadPool(MAX_JOB_POOL);
		
		this.cacheManager = CacheManager.getInstance();
		ExecutorPT.loadJobMap();
		super.onInitialisation();
	}

	@Override
	protected void onReady() throws Exception {
		super.onReady();
		//creating singleton instance of caches
		ServiceContext.getContext().getInstance().subscribeResourceEvents(new RINotificationConsumer());
		
	}
		
	public String getRuntimeResourceCategory() {
		return runtimeResourceCategory;
	}

	public void activatePlugin(ServiceEndpoint resource){
		AbstractPlugin plugin =PluginManager.get().add(resource);
		try {
			Manager manager =(Manager) ManagerPTContext.getContext().getWSHome().find(ManagerPTContext.getContext().makeKey(Constants.FACTORY_RESORCE_NAME));
			manager.loadPluginDescription(plugin);
		} catch (Exception e) {
			logger.error("errror retrieving the resource",e);
		} 
	}
	
	public void updatePlugin(ServiceEndpoint resource){
		AbstractPlugin plugin = PluginManager.get().plugins().get(resource);
		try {
			Manager manager =(Manager) ManagerPTContext.getContext().getWSHome().find(ManagerPTContext.getContext().makeKey(Constants.FACTORY_RESORCE_NAME));
			manager.loadPluginDescription(plugin);
		} catch (Exception e) {
			logger.error("errror retrieving the resource",e);
		} 
	}
	
	public void removePlugin(GCUBEScope scope){
		
		PluginManager.get().removePlugins();
		try {
			Manager manager =(Manager) ManagerPTContext.getContext().getWSHome().find(ManagerPTContext.getContext().makeKey(Constants.FACTORY_RESORCE_NAME));
			manager.removePluginsDescription();
		} catch (Exception e) {
			logger.error("errror retrieving the resource",e);
		} 
	}
	
	private boolean createResourceInScope(GCUBEScope scope){
		try{
			logger.info("creating resource in scope "+scope.getName());
			ServiceContext.getContext().setScope(scope);
			ManagerPTContext.getContext().getWSHome().create(ManagerPTContext.getContext().makeKey(Constants.FACTORY_RESORCE_NAME));
			GCUBENotificationTopic runtimeResourceTopic = new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry","RuntimeResource"));
			runtimeResourceTopic.setPrecondition("//profile[contains(.,'<Category>"+runtimeResourceCategory+"</Category>')] and //invocationScope/text()='"+scope.toString()+"'");
			notifier.registerToISNotification(RuntimeResourceConsumer.getConsumer(scope), Collections.singletonList(runtimeResourceTopic), this, scope.getType()==Type.VRE?scope.getEnclosingScope():scope);
			
		}catch (Exception e) {
			logger.warn("error creating resource in scope "+scope,e);
			return false;
		}
		return true;
	}
	
	private boolean removeResourceInScope(GCUBEScope scope){
		try{
			logger.info("removing resource in scope "+scope.getName());
			ServiceContext.getContext().setScope(scope);
			ManagerPTContext.getContext().getWSHome().remove(ManagerPTContext.getContext().makeKey(Constants.FACTORY_RESORCE_NAME));
			GCUBENotificationTopic runtimeResourceTopic = new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry","RuntimeResource"));
			runtimeResourceTopic.setPrecondition("//profile[contains(.,'<Category>"+runtimeResourceCategory+"</Category>')] and //invocationScope/text()='"+scope.toString()+"'");
			notifier.unregisterFromISNotification(this, Collections.singletonList(runtimeResourceTopic), scope.getType()==Type.VRE?scope.getEnclosingScope():scope);
		}catch (Exception e) {
			logger.warn("error removing resource in scope "+scope,e);
			return false;
		}
		return true;
	}
	
	/** Base implementation of a {@link ResourceConsumer}.*/
	public class RINotificationConsumer extends ResourceConsumer {
				
		@Override
		protected void onAddScope(AddScopeEvent event) {
			for (GCUBEScope scope: event.getPayload())
				createResourceInScope(scope);
			super.onAddScope(event);
		}

		@Override
		protected void onRemoveScope(RemoveScopeEvent event) {
			for (GCUBEScope scope: event.getPayload())
				removeResourceInScope(scope);
			super.onRemoveScope(event);
		}
	}
	
	public void executeJob(SpeciesJob job){
		this.jobThreadPool.execute(ScopedTasks.bind(job));
	}
	
	
	public ExecutorService getSearchThreadPool() {
		return searchThreadPool;
	}

	@Override
	protected void onShutdown() throws Exception {
		for (GCUBEScope scope: ServiceContext.getContext().getInstance().getScopes().values())
			removeResourceInScope(scope);
		this.searchThreadPool.shutdown();
		this.jobThreadPool.shutdown();
		this.cacheManager.shutdown();
		ExecutorPT.storeJobMap();
		super.onShutdown();
	}

	
	
	
}

