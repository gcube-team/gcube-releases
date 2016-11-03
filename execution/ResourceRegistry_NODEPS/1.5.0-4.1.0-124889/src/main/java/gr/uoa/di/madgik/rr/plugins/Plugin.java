package gr.uoa.di.madgik.rr.plugins;

import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.IRRElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A Resource Registry plugin is a module which performs value added functionality, independent of the specifics of the implementation
 * of each provider. A plugin operates on top of the common model without being aware of the origin of the items it processes, much less about
 * the operations providers perform in order to conform to the model. 
 * Plugins are grouped according to the condition which triggers their execution.
 * The type of plugins whose condition of triggering depends on the beginning or end of bridging cycle phases corresponds to condition of triggering itself.
 * Specifically
 * <ol><li>plugins of type {@link Plugin.Type#PRE_RETRIEVE} are triggered just before the initiation of the retrieval phase of the bridging cycle</li>
 * <li>plugins of type {@link Plugin.Type#POST_RETRIEVE} are triggered just after the completion of the retrieval phase of the bridging cycle</li>
 * <li>plugins of type {@link Plugin.Type#PRE_UPDATE} are triggered just before the initiation of the update phase of the bridging cycle</li> 
 * <li>plugins of type {@link Plugin.Type#POST_UPDATE}are triggered just after the completion of the update phase of the bridging cycle</li></ol>
 * Plugins whose operations do not or cannot depend on a specific bridging cycle phase can be declared with type {@link Plugin.Type#PERIODIC} or {@link Plugin.Type#ONE_OFF}.
 * As the operation of plugins of such types is not supported by the built-in mechanisms of {@link ResourceRegistry}, implementations of this type should be careful 
 * not to corrupt the underlying datastores or process inconsistent data. Specifically
 * <ol><li>Plugins of type {@link Plugin.Type#PERIODIC} should operate only on {@link DatastoreType#LOCAL}</li>
 * <li>Plugins of type {@link Plugin.Type#PERIODIC} should hold a lock on {@link DatastoreType#LOCAL} during the entire time of their operation</li></ol>
 * The same applies for plugins of type {@link Plugin.Type#ONE_OFF}, however such plugins are not expected to perform database operations as their primary purpose is
 * to handle environment setup tasks.
 * 
 * @author gerasimos.farantatos
 *
 */
public abstract class Plugin 
{

	public static class ProcessedItemType
	{
		public final Class<?> itemType;
		public final DatastoreType datastoreType;
		
		public ProcessedItemType(Class<?> itemType, DatastoreType datastoreType)
		{
			this.itemType = itemType;
			this.datastoreType = datastoreType;
		}
		
		@Override
		public boolean equals(Object other)
		{
			if(!(other instanceof ProcessedItemType)) return false;
			if(!this.itemType.getName().equals(((ProcessedItemType)other).itemType.getName())) return false;
			if(this.datastoreType != ((ProcessedItemType)other).datastoreType) return false;
			return true;
		}
		
		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 31*hash + (this.itemType == null ? 0 : this.itemType.getName().hashCode());
			hash = 31*hash + (this.datastoreType == null ? 0 : this.datastoreType.toString().hashCode());
			return hash;
		}
	}
	public static enum Type
	{
		PRE_RETRIEVE,
		POST_RETRIEVE,
		PRE_UPDATE,
		POST_UPDATE,
		PERIODIC,
		ONE_OFF
	}
	
	protected Type type;
	protected long period=-1;
	protected TimeUnit periodUnit=null;
	protected Set<ProcessedItemType> processedItems=new HashSet<ProcessedItemType>();
	protected Map<ProcessedItemType, Set<? extends IRRElement>> items = new HashMap<ProcessedItemType, Set<? extends IRRElement>>();
	protected Map<ProcessedItemType, Set<? extends IDaoElement>> itemDaos = new HashMap<ProcessedItemType, Set<? extends IDaoElement>>();

	public void readConfiguration(String prefix, Properties properties) throws ResourceRegistryException
	{
		if(properties==null) return;
		String propPeriod = properties.getProperty(prefix+".period");
		if(propPeriod != null)
		{
			if(this.type != Type.PERIODIC) throw new ResourceRegistryException("Attempt to set period to non-periodic plugin");
			String propPeriodUnit = properties.getProperty(prefix+".periodUnit");
			
			this.period = Long.parseLong(propPeriod.trim());
			if(this.period <= 0) throw new IllegalArgumentException("Non-positive value for period not allowed");
			this.periodUnit = TimeUnit.valueOf(propPeriodUnit.trim());
		}
	}
	
	/**
	 * Returns the type of the plugin.
	 * Plugins are grouped according to the condition which triggers their execution.
	 * Specifically
	 * <ol><li>plugins of type {@link Plugin.Type#PRE_RETRIEVE} are triggered just before the initiation of the retrieval phase of the bridging cycle</li>
	 * <li>plugins of type {@link Plugin.Type#POST_RETRIEVE} are triggered just after the completion of the retrieval phase of the bridging cycle</li>
	 * <li>plugins of type {@link Plugin.Type#PRE_UPDATE} are triggered just before the initiation of the update phase of the bridging cycle</li> 
	 * <li>plugins of type {@link Plugin.Type#POST_UPDATE}are triggered just after the completion of the update phase of the bridging cycle</li>
	 * <li>plugins whose operations do not or cannot depend on a specific bridging cycle phase can be declared with type {@link Plugin.Type#PERIODIC}.</li></ol>
	 *
	 * @return the type of the plugin
	 */
	public Type getType()
	{
		return this.type;
	}
	
	/**
	 * Returns the set of items this plugin processes during its operation.
	 * Plugins declare these items so that {@link ResourceRegistry} can provide collections of prefetched items 
	 * 
	 * @return
	 */
	public Set<ProcessedItemType> getProcessedItems()
	{
		return new HashSet<ProcessedItemType>(this.processedItems);
	}
	
	private void clearItems()
	{
		this.items.clear();
		this.itemDaos.clear();
	}
	
	public void addProcessedItems(ProcessedItemType type, Set<? extends IRRElement> items)
	{
		this.items.put(type, items);
	}
	
	public void addProcessedItemDaos(ProcessedItemType type, Set<? extends IDaoElement> items)
	{
		this.itemDaos.put(type, items);
	}
	
	public long getPeriod()
	{
		if(this.type != Type.PERIODIC) throw new UnsupportedOperationException();
		return this.period;
	}
	
	public TimeUnit getPeriodUnit()
	{
		if(this.type != Type.PERIODIC) throw new UnsupportedOperationException();
		return this.periodUnit;
	}
	
	public abstract void setup() throws ResourceRegistryException;
	
	protected abstract void execute(Set<Class<?>> targets) throws ResourceRegistryException;
	
	public void executePlugin(Set<Class<?>> targets) throws ResourceRegistryException
	{
		execute(targets);
		clearItems();
	}
}
