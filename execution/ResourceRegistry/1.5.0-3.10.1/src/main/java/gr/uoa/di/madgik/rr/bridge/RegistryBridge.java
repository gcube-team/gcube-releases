package gr.uoa.di.madgik.rr.bridge;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.ReadPolicy;
import gr.uoa.di.madgik.rr.RRContext.WritePolicy;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.plugins.Plugin;
import gr.uoa.di.madgik.rr.plugins.PluginManager;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;
import gr.uoa.di.madgik.rr.access.InMemoryStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistryBridge extends Thread 
{
	private static final Logger logger = LoggerFactory
			.getLogger(RegistryBridge.class);
	
	private static Semaphore sem=new Semaphore(1);
	private static Semaphore resetSem = new Semaphore(1);
	private static Lock writeLock=null;
	private static long BridgingPeriod=1000*60*10;
	private static long ShortBridgingPeriod=1000*60*2;
	private static boolean clearDatastoreOnStartup=false;
	private static boolean forceReset=false;
	private static long lastUpdate=-1;

	private HashMap<String,Class<?>> incoming=new HashMap<String,Class<?>>();
	private HashMap<String,Class<?>> outgoing=new HashMap<String,Class<?>>();
	private HashMap<String,Class<?>> updating=new HashMap<String,Class<?>>();
	
	private HashMap<String,Class<?>> inMemory=new HashMap<String,Class<?>>();
	
	private HashSet<String> nonUpdateVOScopes=new HashSet<String>();
	
	private IRegistryProvider provider=null;
	
	private boolean isInitialBridging=true;
	private boolean incomingBridgingSuccess=true;
	
	private Holder<Long> currentIteration=new Holder<Long>(0l);
	
	private static class Holder<T>
	{
		private T obj;
		
		public Holder(T obj)
		{
			this.obj = obj;
		}
		
		public T get()
		{
			return obj;
		}
		
		public void set(T val)
		{
			obj = val;
		}
	}
	
	public RegistryBridge(IRegistryProvider provider) throws ResourceRegistryException
	{
		this.setDaemon(true);
		this.setName("Repository Bridge Daemon Thread");
		this.provider=provider;
		RegistryBridge.writeLock = ResourceRegistry.getContext().getExclusiveLock();
	}
	
	public void setIncoming(Set<String> incoming) throws ResourceRegistryException
	{
		this.incoming.clear();
		try
		{
			for(String s : incoming)
			{
				this.incoming.put(s, Class.forName(s));
			}
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("Could not load incoming", ex);
		}
	}
	
	public void setOutgoing(Set<String> outgoing) throws ResourceRegistryException
	{
		this.outgoing.clear();
		try
		{
			for(String s : outgoing)
			{
				this.outgoing.put(s, Class.forName(s));
			}
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("Could not load outgoing", ex);
		}
	}
	
	public void setUpdating(Set<String> updating) throws ResourceRegistryException
	{
		this.updating.clear();
		try
		{
			for(String s : updating)
			{
				this.updating.put(s, Class.forName(s));
			}
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("Could not load updating", ex);
		}
	}
	
	public void setInMemory(Set<String> inMemory) throws ResourceRegistryException
	{
		this.inMemory.clear();
		try
		{
			for(String s : inMemory)
			{
				this.inMemory.put(s, Class.forName(s));
			}
			this.provider.setInMemoryTargets(new HashSet<Class<?>>(this.inMemory.values()));
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("Could not load in-memory items", ex);
		}
	}
	
	public void setNonUpdateVOScopes(Set<String> nonUpdateVOScopes)
	{
		this.nonUpdateVOScopes.clear();
		this.nonUpdateVOScopes.addAll(nonUpdateVOScopes);
	}
	
	public void setBridgingPeriod(long millis)
	{
		this.BridgingPeriod = millis;
	}
	
	public void setShortBridgingPeriod(long millis)
	{
		this.ShortBridgingPeriod = millis;
	}
	
	public void setClearDataStoreOnStartup(boolean value)
	{
		this.clearDatastoreOnStartup = value;
	}
	
	public boolean getClearDataStoreOnStartup()
	{
		return this.clearDatastoreOnStartup;
	}
	
	public long getCurrentIteration()
	{
		synchronized(currentIteration)
		{
			return currentIteration.get();
		}
	}
	
	public void update()
	{
		boolean acquired = false;
		logger.info("starting updating");
		try
		{
			logger.trace( "Trying to acquire reset lock.");
			/* Acquire the semaphore before bridging, in order not to interleave with the reset operation. */
			RegistryBridge.resetSem.acquire();
			logger.trace( "Acquired reset lock. Continuing with update operation.");
			
			logger.trace( "Trying to acquire lock");
			if(!RegistryBridge.sem.tryAcquire()) 
			{
				logger.trace( "Failed to acquire lock because a bridging iteration is ongoing. Outgoing items will be updated during next iteration");
				return;
			}
			acquired = true;
			logger.trace( "Acquired lock. Continuing with update operation");
			this.doBridge(new HashSet<Class<?>>(this.updating.values()), new HashSet<Class<?>>(this.outgoing.values()));
		}
		catch(Exception ex)
		{
			logger.warn("Could not complete bridging iteration", ex);
		}
		finally
		{
			if(acquired)
				RegistryBridge.sem.release();
			
			RegistryBridge.resetSem.release();
			logger.trace( "Released reset lock.");
		}
		logger.info("completed bridging iteration");
	}
	
	public void run()
	{
		Runtime.getRuntime().addShutdownHook(
			new Thread() 
			{
				public void run()
				{
					RegistryBridge.writeLock.lock(); //acquire write lock to prevent shutdown while updating local db
				}
			}
		);
		try {
			if(this.clearDatastoreOnStartup)
			{
				logger.info( "Clearing local data store");
				DatastoreHelper.clear(RRContext.DatastoreType.LOCAL, new HashSet<Class<?>>(this.incoming.values()));
				DatastoreHelper.clear(RRContext.DatastoreType.LOCAL, new HashSet<Class<?>>(this.outgoing.values()));
			}
			DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, new HashSet<Class<?>>(this.incoming.values()));
			DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, new HashSet<Class<?>>(this.outgoing.values()));
		}
		catch(Exception e)
		{
			logger.warn("Unable to perform initial database cleanup", e);
		}
		
		boolean first = true;
		while(true)
		{
			logger.info("starting bridging iteration");
			boolean acquired = false;
			boolean shouldSleep = true;
			try 
			{
				if(!ResourceRegistry.isReadPolicySupported(ReadPolicy.REFRESH_AHEAD) || !ResourceRegistry.isWritePolicySupported(WritePolicy.WRITE_BEHIND)) this.isInitialBridging = false;
			}catch(ResourceRegistryException e) { logger.warn("Could not determine read/write policy");}

			boolean isInitBridging = this.isInitialBridging;
			try
			{
				logger.trace( "Trying to acquire reset lock.");
				/* Acquire the semaphore before bridging, in order not to interleave with the reset operation. */
				RegistryBridge.resetSem.acquire();
				logger.trace( "Acquired reset lock. Continuing with bridging operation.");
				
				while(!acquired){
					try 
					{ 
						RegistryBridge.sem.acquire(); 
						acquired = true;
					} catch(InterruptedException e ) { }
				}
				
				if (forceReset) {
					logger.info( "Resource Registry forced to reset");
					throw new Exception("Resource Registry forced to reset");
				}
				if(ResourceRegistry.isReadPolicySupported(ReadPolicy.REFRESH_AHEAD))
					this.doBridge(new HashSet<Class<?>>(this.incoming.values()), new HashSet<Class<?>>(this.outgoing.values()));
				else
					logger.info("REFRESH_AHEAD/WRITE_BEHIND policies not supported, nothing to do");
				
				lastUpdate = System.currentTimeMillis();
				shouldSleep = true;
				if (first) 
					shouldSleep = false;
					
				first = false;
				
			}catch(Exception ex)
			{
				logger.warn("Could not complete bridging iteration", ex);
				
				logger.warn("Resetting resource registry");
				this.isInitialBridging = true;
				this.incomingBridgingSuccess=true;
				//System.out.println("incoming : " + this.incoming);
				//System.out.println("outgoing : " + this.outgoing);
				//System.out.println("inMemory : " + this.inMemory);
				
				try {
					DatastoreHelper.clear(RRContext.DatastoreType.LOCAL, new HashSet<Class<?>>(this.incoming.values()));
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}
				try {
					DatastoreHelper.clear(RRContext.DatastoreType.LOCAL, new HashSet<Class<?>>(this.outgoing.values()));
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}
				try {
					DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, new HashSet<Class<?>>(this.incoming.values()));
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}
				try {
					DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, new HashSet<Class<?>>(this.outgoing.values()));
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}
				
				/*try {
					DatastoreHelper.clear(RRContext.DatastoreType.REMOTE, new HashSet<Class<?>>(this.incoming.values()));
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}
				try {
					DatastoreHelper.clear(RRContext.DatastoreType.REMOTE, new HashSet<Class<?>>(this.outgoing.values()));
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}*/
				
				/*try {
					inMemory.clear();
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}*/
				
				try {
					InMemoryStore.clear();
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}
				
				/* Clean the derby database. */
				try {
					ResourceRegistry.getContext().reset();
				} catch (Exception ex1) {
					logger.warn("Error resetting", ex1);
				}
				
				shouldSleep = false;
				
				forceReset = false;
				
			}finally
			{
				RegistryBridge.sem.release();
				
				RegistryBridge.resetSem.release();
				logger.trace( "Released reset lock.");
				
				synchronized(currentIteration)
				{
					currentIteration.set(currentIteration.get()+1);
				}
			}
			logger.info("completed bridging iteration");
			try
			{
				if (shouldSleep) {
					if(this.incomingBridgingSuccess && !isInitBridging) 
						Thread.sleep(RegistryBridge.BridgingPeriod);
					else 
						Thread.sleep(RegistryBridge.ShortBridgingPeriod);
				}
			}catch(Exception ex){}
		}
	}
	
	public boolean isInitialBridgingComplete()
	{
		return !this.isInitialBridging;
	}
	
	public void setInitialBridging(boolean value) {
		this.isInitialBridging = value;
	}
	
	private void doBridge(HashSet<Class<?>> incomingItems, HashSet<Class<?>> outgoingItems) throws ResourceRegistryException, InterruptedException
	{	
		HashSet<Class<?>> savedIncomingItems = null;
		logger.info("starting purging buffer");
		DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, incomingItems);
		if(this.isInitialBridging)
		{
			logger.info("initial bridging, omiting outgoing");
			DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, outgoingItems);
			
			savedIncomingItems = new HashSet<Class<?>>(incomingItems);
			incomingItems.addAll(outgoingItems);
		}
		else
		{
			if(this.incomingBridgingSuccess==true) 
			{
				int pluginCount = PluginManager.getPluginsOfType(Plugin.Type.PRE_UPDATE).size();
				logger.info( pluginCount != 0 ? 
						"Executing " + pluginCount + " plugins of type " + Plugin.Type.PRE_UPDATE : 
						"No " + Plugin.Type.PRE_UPDATE + " plugins to execute");
				PluginManager.executePluginsOfType(Plugin.Type.PRE_UPDATE, outgoingItems);
				if(pluginCount != 0) logger.info( "Finished executing " + Plugin.Type.PRE_UPDATE + " plugins");
				
				logger.info("starting bridging outgoing");
				this.provider.persist(outgoingItems, nonUpdateVOScopes);
				ResourceRegistry.getContext().getExclusiveLock().lock();
				try 
				{
					DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, outgoingItems);
					DatastoreHelper.replicate(RRContext.DatastoreType.LOCAL, RRContext.DatastoreType.LOCALBUFFER, outgoingItems);
				}finally 
				{
					ResourceRegistry.getContext().getExclusiveLock().unlock();
				}
				
				pluginCount = PluginManager.getPluginsOfType(Plugin.Type.POST_UPDATE).size();
				logger.info( pluginCount != 0 ? 
						"Executing " + pluginCount + " plugins of type " + Plugin.Type.POST_UPDATE : 
						"No " + Plugin.Type.POST_UPDATE + " plugins to execute");
				PluginManager.executePluginsOfType(Plugin.Type.POST_UPDATE, outgoingItems);
				if(pluginCount != 0) logger.info( "Finished executing " + Plugin.Type.POST_UPDATE + " plugins");
			}
			else logger.info( "incoming bridging was not successful, omiting outgoing");
		}
		
		try 
		{
			int pluginCount = PluginManager.getPluginsOfType(Plugin.Type.PRE_RETRIEVE).size();
			logger.info( pluginCount != 0 ? 
					"Executing " + pluginCount + " plugins of type " + Plugin.Type.PRE_RETRIEVE : 
					"No " + Plugin.Type.PRE_RETRIEVE + " plugins to execute");
			PluginManager.executePluginsOfType(Plugin.Type.PRE_RETRIEVE, incomingItems);
			if(pluginCount != 0) logger.info( "Finished executing " + Plugin.Type.PRE_RETRIEVE + " plugins");
			
			logger.info( "starting bridging incoming");
			this.provider.retrieve(incomingItems);
			
			logger.info( "IncomingItems contain:");
			for(Class<?> c: incomingItems) {
				logger.info( "- " + c.getName());
			}
			
			pluginCount = PluginManager.getPluginsOfType(Plugin.Type.POST_RETRIEVE).size();
			logger.info( pluginCount != 0 ? 
					"Executing " + pluginCount + " plugins of type " + Plugin.Type.POST_RETRIEVE : 
					"No " + Plugin.Type.POST_RETRIEVE + " plugins to execute");
			PluginManager.executePluginsOfType(Plugin.Type.POST_RETRIEVE, incomingItems);
			if(pluginCount != 0) logger.info( "Finished executing " + Plugin.Type.POST_RETRIEVE + " plugins");
			
		}catch(ResourceRegistryException e)
		{
			this.incomingBridgingSuccess=false;
			throw e;
		}
		logger.info("starting purging local");
		ResourceRegistry.getContext().getExclusiveLock().lock();
		try 
		{
			
			DatastoreHelper.resolveUpdateConflicts(RRContext.DatastoreType.LOCALBUFFER, RRContext.DatastoreType.LOCAL, outgoingItems);
			logger.info("starting replication from buffer to local");
			DatastoreHelper.clear(RRContext.DatastoreType.LOCAL, this.isInitialBridging ? savedIncomingItems : incomingItems);
			DatastoreHelper.replicate(RRContext.DatastoreType.LOCALBUFFER, RRContext.DatastoreType.LOCAL, incomingItems);
		}finally
		{
			ResourceRegistry.getContext().getExclusiveLock().unlock();
		}
		
		this.provider.prefetchInMemoryItems();
		
		logger.info("finished client bridging");
		this.incomingBridgingSuccess=true;
		this.isInitialBridging=false;
	}
	
	public static void forceReset(){
		logger.info( "Resource Registry force reset has been enabled. Next bridging will clear the database and in memory structures");
		forceReset = true;
	}
	
	public void reset() throws ResourceRegistryException, InterruptedException {
		logger.info("Starting reset operation.");
		
		try
		{
			logger.trace( "Trying to acquire reset lock.");
			
			/* Acquire the semaphore before the reset operation, in order not to interleave with bridging. */
			RegistryBridge.resetSem.acquire();
			logger.info( "Acquired reset lock. Continuing with reset operation.");
			
			logger.info( "Acquired reset lock. Continuing with reset operation.");
			
			/* Set initial bridging values to default, in order for initial
			 * bridging to start over. */
			this.isInitialBridging = true;
			this.incomingBridgingSuccess=true;
			
			/* Clear all stored in memory information. */
			try {
				DatastoreHelper.clear(RRContext.DatastoreType.LOCAL, new HashSet<Class<?>>(this.incoming.values()));
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}
			try {
				DatastoreHelper.clear(RRContext.DatastoreType.LOCAL, new HashSet<Class<?>>(this.outgoing.values()));
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}
			try {
				DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, new HashSet<Class<?>>(this.incoming.values()));
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}
			try {
				DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, new HashSet<Class<?>>(this.outgoing.values()));
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}
			
			/*
			try {
				DatastoreHelper.clear(RRContext.DatastoreType.REMOTE, new HashSet<Class<?>>(this.incoming.values()));
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}
			try {
				DatastoreHelper.clear(RRContext.DatastoreType.REMOTE, new HashSet<Class<?>>(this.outgoing.values()));
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}*/
			
			/*try {
				inMemory.clear();
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}*/
			
			try {
				InMemoryStore.clear();
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}
			
			
			
			/* Clean the derby database. */
			/* Clean the derby database. */
			try {
				ResourceRegistry.getContext().reset();
			} catch (Exception ex1) {
				logger.warn("Error resetting", ex1);
			}
		}
		finally
		{
			RegistryBridge.resetSem.release();
			logger.trace( "Released reset lock.");
		}
		logger.info("Completed reset operation.");		
	}
	
	public static long getLastUpdate(){
		return lastUpdate;
	}
}
