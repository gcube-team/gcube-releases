package gr.uoa.di.madgik.rr;

import gr.uoa.di.madgik.rr.RRContext.ReadPolicy;
import gr.uoa.di.madgik.rr.RRContext.WritePolicy;
import gr.uoa.di.madgik.rr.bridge.IRegistryProvider;
import gr.uoa.di.madgik.rr.bridge.RegistryBridge;

import java.util.Set;

public class ResourceRegistry
{
	private static RRContext context=null;
	private static RegistryBridge bridge=null;
	private static IRegistryProvider provider=null;
	
	private static synchronized void initialize() throws ResourceRegistryException
	{
		if(context==null) context=new RRContext();
	}
	
	private static synchronized void initProvider() throws ResourceRegistryException
	{
		if(provider==null)
		{
			try
			{
				Object o = Class.forName(getContext().getRepositoryProvider()).newInstance();
				if(!(o instanceof IRegistryProvider)) throw new ResourceRegistryException("targeted provider not of supported type");
				provider=(IRegistryProvider)o;
				provider.readConfiguration(getContext().getRepositoryProviderConfig());
				boolean foundPolicy = false;
				for(RRContext.ReadPolicy policy : RRContext.ReadPolicy.values())
				{
					if(provider.isReadPolicySupported(policy))
					{
						foundPolicy = true;
						break;
					}
				}
				if(foundPolicy == false) throw new ResourceRegistryException("Could not find any supported read policies for provider " + provider.getClass().getName() + ". Check datastore configuration");
				foundPolicy = false;
				for(RRContext.WritePolicy policy : RRContext.WritePolicy.values())
				{
					if(provider.isWritePolicySupported(policy))
					{
						foundPolicy = true;
						break;
					}
				}
				if(foundPolicy == false) throw new ResourceRegistryException("Could not find any supported write policies for provider " + provider.getClass().getName() + ". Check datastore configuration");
			} catch (Exception e)
			{
				throw new ResourceRegistryException("provider could not be initialized", e);
			}
		}
	}

	private static synchronized void initBridge() throws ResourceRegistryException
	{
		if(context==null)
		{
			bridge=new RegistryBridge(getProvider());
			if(ResourceRegistry.getContext().getBridgingPeriod() != null) 
				bridge.setBridgingPeriod(ResourceRegistry.getContext().getBridgingPeriod());
			if(ResourceRegistry.getContext().getShortBridgingPeriod() != null) 
				bridge.setShortBridgingPeriod(ResourceRegistry.getContext().getShortBridgingPeriod());
			if(ResourceRegistry.getContext().getClearDataStoreOnStartup() != null)
				bridge.setClearDataStoreOnStartup(ResourceRegistry.getContext().getClearDataStoreOnStartup());
			bridge.setOutgoing(ResourceRegistry.getContext().getEditableTargets());
			bridge.setIncoming(ResourceRegistry.getContext().getReadOnlyTargets());
			bridge.setUpdating(ResourceRegistry.getContext().getUpdateTargets());
			bridge.setInMemory(ResourceRegistry.getContext().getInMemoryTargets());
			bridge.setNonUpdateVOScopes(ResourceRegistry.getContext().getNonUpdateVOScopes());
			bridge.start();
		}
	}
	
	private static IRegistryProvider getProvider()throws ResourceRegistryException
	{
		if(provider==null) initProvider();
		return provider;
	}
	
	public static RRContext getContext() throws ResourceRegistryException
	{
		if(context==null) initialize();
		return context;
	}
	
	public static void startBridging() throws ResourceRegistryException
	{
		if(bridge==null) initBridge();
	}
	
	public static boolean isInitialBridgingComplete() throws ResourceRegistryException
	{
		if(bridge!=null) return bridge.isInitialBridgingComplete();
		throw new ResourceRegistryException("Registry bridge not initialized");
	}
	
	public static void retrieveDirect(Class<?> item, String id) throws ResourceRegistryException
	{
		provider.retrieveDirect(item, id);
	}
	
	public static void retrieveDirect(Class<?> item) throws ResourceRegistryException
	{
		provider.retrieveDirect(item);
	}
	
	public static void storeDirect(Class<?> item, String id) throws ResourceRegistryException
	{
		provider.persistDirect(item, id);
	}
	
	public static void storeDirect(Class<?> item) throws ResourceRegistryException
	{
		provider.persistDirect(item);
	}
	
	public static Set<String> getReadOnlyTargets()
	{
		return context.getReadOnlyTargets();
	}
	
	public static Set<String> getUpdateTargets()
	{
		return context.getUpdateTargets();
	}
	
	public static Set<String> getEditableTargets()
	{
		return context.getEditableTargets();
	}
	
	public static void setDirty()
	{
		if(bridge!=null) bridge.update();
	}
	
	public static boolean isWritePolicySupported(WritePolicy policy) throws ResourceRegistryException
	{
		return getProvider().isWritePolicySupported(policy);
		
	}
	
	public static boolean isReadPolicySupported(ReadPolicy policy) throws ResourceRegistryException
	{
		return getProvider().isReadPolicySupported(policy);
	}
	
	public static long getCurrentIteration()
	{
		return bridge.getCurrentIteration();
	}
	
	public static void reset() throws ResourceRegistryException, InterruptedException {
		if(bridge != null)
			bridge.reset();
	}
}
