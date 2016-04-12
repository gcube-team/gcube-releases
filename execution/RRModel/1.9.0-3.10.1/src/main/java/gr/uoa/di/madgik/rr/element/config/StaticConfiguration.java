package gr.uoa.di.madgik.rr.element.config;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class StaticConfiguration extends RRElement
{

	private static StaticConfiguration instance = null;
	private StaticConfigurationDao item=new StaticConfigurationDao();
	private RRContext context=null;
	private Map<String, Set<String>> presentationInfoKeywords=null;
	
	private static final String KeyValueDelimiter="##scdelim##";
	
	public static final String SemanticGroupName = "semantic";
	
	private static String instanceID = null;
	
	private static long lastValidIteration = -1;
	
	private StaticConfiguration(/*String id*/) throws ResourceRegistryException
	{
		this.item.setID(UUID.randomUUID().toString());
		this.context=ResourceRegistry.getContext();
	}

	public static StaticConfiguration getInstance() throws ResourceRegistryException
	{
		if(!ResourceRegistry.isInitialBridgingComplete()) throw new ResourceRegistryException("Initial bridging is not complete");
		if(instance==null)
		{
			instance = new StaticConfiguration();
			instance.load(true);
			lastValidIteration = ResourceRegistry.getCurrentIteration();
			StaticConfiguration.instanceID = instance.getID();
		}
		if(lastValidIteration < ResourceRegistry.getCurrentIteration()) instance.load(true);
		return instance;
	}
	
	@Override
	public String getID()
	{
		return this.item.getID();
	}
	
	@Override
	public void setID(String id)
	{
		this.item.setID(id);
	}
	
	@Override
	public RRContext getISContext() {
		return context;
	}
	
	/**
	 * Refreshes static configuration info if necessary
	 * 
	 * @throws ResourceRegistryException
	 */
	private void refresh() throws ResourceRegistryException
	{
		/*TODO this method is not used because only a single updater exists. If this
		 * situation changes in the future, all static config operations should call
		 * this method before performing any modifications
		 */
		if(lastValidIteration < ResourceRegistry.getCurrentIteration())
		{
			this.presentationInfoKeywords = null;
			instance.load(true);
		}
	}
	
	public Set<String> getPresentationInfoGroups() throws ResourceRegistryException
	{
		if(this.presentationInfoKeywords==null) this.buildPresentationInfoKeywords();
		return new HashSet<String>(this.presentationInfoKeywords.keySet());
	}
	
	public void addPresentationInfoGroup(String group) throws ResourceRegistryException
	{
		if(this.presentationInfoKeywords==null) this.buildPresentationInfoKeywords();
		if(this.presentationInfoKeywords.containsKey(group)) throw new ResourceRegistryException("Presentation info keyword group \"" + group + "\" already exists");
		this.presentationInfoKeywords.put(group, new HashSet<String>());
	}
	
	public void deletePresentationInfoGroup(String group) throws ResourceRegistryException
	{
		if(this.presentationInfoKeywords==null) this.buildPresentationInfoKeywords();
		if(!this.presentationInfoKeywords.containsKey(group)) throw new ResourceRegistryException("Presentation info keyword group \"" + group + "\" does not exist");
		if(!this.presentationInfoKeywords.get(group).isEmpty()) throw new ResourceRegistryException("Non-empty presentation info keyword group \"" + group + "\"; cannot delete");
		this.presentationInfoKeywords.remove(group);
	}
	
	public Set<String> getPresentationInfoKeywords(String group)
	{
		if(this.presentationInfoKeywords==null) this.buildPresentationInfoKeywords();
		if(!this.presentationInfoKeywords.containsKey(group)) return new HashSet<String>();
		return new HashSet<String>(this.presentationInfoKeywords.get(group));
	}
	
	public void addPresentationInfoKeyword(String group, String keyword) throws ResourceRegistryException
	{
		if(this.presentationInfoKeywords==null) this.buildPresentationInfoKeywords();
		if(!this.presentationInfoKeywords.containsKey(group)) throw new ResourceRegistryException("Presentation info keyword group \"" + group + "\" not found");
		if(this.presentationInfoKeywords.get(group).contains(keyword)) throw new ResourceRegistryException("Presentation info keyword group \"" + group + "\" already contains keyword");
		this.presentationInfoKeywords.get(group).add(keyword);
	}
	
	public void deletePresentationInfoKeyword(String group, String keyword) throws ResourceRegistryException
	{
		if(this.presentationInfoKeywords==null) this.buildPresentationInfoKeywords();
		if(!this.presentationInfoKeywords.containsKey(group)) throw new ResourceRegistryException("Presentable info keyword group " + group + " not found");
		if(!this.presentationInfoKeywords.get(group).contains(keyword)) throw new ResourceRegistryException("Presentation info keyword \"" + keyword + "\" was not found in group \"" + group + "\"");
		this.presentationInfoKeywords.get(group).remove(keyword);
	}
	
	private void buildPresentationInfoKeywords()
	{
		this.presentationInfoKeywords=new HashMap<String, Set<String>>();
		for(String k : this.item.getPresentationInfoGroups())
		{
			if(!this.presentationInfoKeywords.containsKey(k)) this.presentationInfoKeywords.put(k, new HashSet<String>());
			for(String v:this.item.getPresentationInfoKeywords())
			{
				String[] keyVal = v.split(KeyValueDelimiter);
				if(keyVal.length != 2) continue;
				if(keyVal[0].trim().equals(k))
					this.presentationInfoKeywords.get(k).add(keyVal[1]);
			}
		}
	}
	
	@Override
	public StaticConfigurationDao getItem()
	{
		return this.item;
	}
	
	@Override
	public void setDirty()
	{
		this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	private void apply(IRRElement target, DatastoreType persistencyType, boolean applyDetails, boolean doStore) throws ResourceRegistryException
	{
		if(!(target instanceof StaticConfiguration)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((StaticConfiguration)target).item.getID());
		this.item.setPresentationInfoGroups(((StaticConfiguration)target).item.getPresentationInfoGroups());
		this.item.setPresentationInfoKeywords(((StaticConfiguration)target).item.getPresentationInfoKeywords());
		
		Set<String> toAddGroups = new HashSet<String>();
		for(String group : ((StaticConfiguration)target).item.getPresentationInfoGroups())
		{
			if(!this.getPresentationInfoGroups().contains(group)) toAddGroups.add(group);
		}
		for(String group : toAddGroups)
		{
			this.addPresentationInfoGroup(group);
			this.item.getPresentationInfoGroups().add(group);
		}
		
		Map<String, Set<String>> toDelKeywords = new HashMap<String, Set<String>>();
		Map<String, Set<String>> toAddKeywords = new HashMap<String, Set<String>>();
		for(String group : this.getPresentationInfoGroups())
		{
			for(String keyword : this.getPresentationInfoKeywords(group))
			{
				if(!((StaticConfiguration)target).getPresentationInfoKeywords(group).contains(keyword))
				{
					if(!toDelKeywords.containsKey(group)) toDelKeywords.put(group, new HashSet<String>());
					toDelKeywords.get(group).add(keyword);
				}
			}
		}
		for(String group : ((StaticConfiguration)target).getPresentationInfoGroups())
		{
			for(String keyword : ((StaticConfiguration)target).getPresentationInfoKeywords(group))
			{
				if(!this.getPresentationInfoKeywords(group).contains(keyword))
				{
					if(!toAddKeywords.containsKey(group)) toAddKeywords.put(group, new HashSet<String>());
					toAddKeywords.get(group).add(keyword);
				}
			}
		}
		
		for(Map.Entry<String, Set<String>> toDel : toDelKeywords.entrySet())
		{
			for(String keyword : toDel.getValue())
			{
				this.deletePresentationInfoKeyword(toDel.getKey(), keyword);
				this.item.getPresentationInfoKeywords().remove(toDel.getKey()+KeyValueDelimiter+keyword);
			}
		}
		for(Map.Entry<String, Set<String>> toAdd : toAddKeywords.entrySet())
		{
			for(String keyword : toAdd.getValue())
			{
				this.addPresentationInfoKeyword(toAdd.getKey(), keyword);
				this.item.getPresentationInfoKeywords().add(toAdd.getKey()+KeyValueDelimiter+keyword);
			}
		}
		
		Set<String> toDelGroups = new HashSet<String>();
		for(String group : this.getPresentationInfoGroups())
		{
			if(!((StaticConfiguration)target).getPresentationInfoGroups().contains(group)) toDelGroups.add(group);
		}
	
		for(String group : toDelGroups)
		{
			this.deletePresentationInfoGroup(group);
			this.item.getPresentationInfoGroups().remove(group);
		}
		
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
	@Override
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return this.load(loadDetails, persistencyType, false);
	}
	
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType, boolean overridePrefetched) throws ResourceRegistryException
	{
		
		//if(!this.exists(persistencyType)) return false;
		
		PersistenceManager pm = null;
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		try
		{
			Set<IDaoElement> items = DatastoreHelper.getItems(persistencyType, StaticConfigurationDao.class, true);
			if(items.size() > 1) throw new ResourceRegistryException("More than instance of static configuration was found!");
			if(items.size() == 0) throw new ResourceRegistryException("No static configuration instance was found");
			this.setID(items.iterator().next().getID());
			
			if(!overridePrefetched && persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
			{
				sharedLock.lock(); locked = true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
				{
					StaticConfiguration retrieved = (StaticConfiguration)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, persistencyType, true, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(StaticConfigurationDao.class, this.item.getID()));
			pm.currentTransaction().commit();
			sharedLock.unlock(); locked = false;
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not load static configuration", e);
		}finally
		{
			if(locked) sharedLock.unlock();
			if (pm!=null && pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			if(pm!=null) pm.close();
		}
		return true;
	}

	@Override
	public void delete(boolean deleteDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("This element does not support deletion");
//		if(!this.exists(persistencyType)) return;
//		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
//			InMemoryStore.removeItem(this.getClass(), this.getID());
//		StaticConfiguration n=new StaticConfiguration();
//		n.setID(this.getID());
//		n.load(deleteDetails,persistencyType,true);
//		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
//		try
//		{
//			pm.currentTransaction().begin();
//			pm.deletePersistent(n.item);
//			pm.currentTransaction().commit();
//			pm.flush();
//			
//		}finally
//		{
//			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
//			pm.close();
//		}
	}

	@Override
	public void store(boolean storeDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		this.store(storeDetails, persistencyType, false);
	}
	
	private void store(boolean storeDetails, RRContext.DatastoreType persistencyType, boolean writeThrough) throws ResourceRegistryException
	{
		if(!this.getID().equals(StaticConfiguration.instanceID)) 
			throw new ResourceRegistryException("Attempt to store duplicate static configuration. This object is a singleton");
		if(this.exists(persistencyType) && !writeThrough)
		{
			StaticConfiguration item=new StaticConfiguration();
			item.setID(this.getID());
			item.load(storeDetails, persistencyType, true);
			item.apply(this, persistencyType, storeDetails, true);
		}
		else
		{
			this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
			if(persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
				InMemoryStore.setItem(this.getClass(), this);
			
			PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
			try
			{
				pm.currentTransaction().begin();
				this.item=pm.detachCopy(pm.makePersistent(this.item));
				pm.currentTransaction().commit();
				pm.flush();
			}finally
			{
				if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
				pm.close();
			}
		}
	}
	
	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException
	{
		if(!(target instanceof StaticConfiguration)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((StaticConfiguration)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((StaticConfiguration)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((StaticConfiguration)target).item.getID()!=null && !this.item.getID().equals(((StaticConfiguration)target).item.getID())) return false;
		if(this.getPresentationInfoGroups().size()!= ((StaticConfiguration)target).getPresentationInfoGroups().size()) return false;
		if(!this.getPresentationInfoGroups().containsAll(((StaticConfiguration)target).getPresentationInfoGroups())) return false;
		for(String group : this.getPresentationInfoGroups())
		{
			if(this.getPresentationInfoKeywords(group).size() != ((StaticConfiguration)target).getPresentationInfoKeywords(group).size()) return false;
			if(!this.getPresentationInfoKeywords(group).containsAll(((StaticConfiguration)target).getPresentationInfoKeywords(group))) return false;
		}
		//if(this.item.getPresentationInfoGroups().size()!=((StaticConfiguration)target).item.getPresentationInfoGroups().size()) return false;
		//if(!((StaticConfiguration)target).item.getPresentationInfoGroups().containsAll(this.item.getPresentationInfoGroups())) return false;
		//if(this.item.getPresentationInfoKeywords().size()!=((StaticConfiguration)target).item.getPresentationInfoKeywords().size()) return false;
		//if(!((StaticConfiguration)target).item.getPresentationInfoKeywords().containsAll(this.item.getPresentationInfoKeywords())) return false;
		return true;
	}

	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try
		{
			if(persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
			{
				sharedLock.lock(); locked = true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
					return true;
				sharedLock.unlock(); locked = false;
			}
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(StaticConfigurationDao.class, "exists");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("id", this.getID());
			sharedLock.lock(); locked = true;
			Collection<?> res=(Collection<?>)query.executeWithMap(args);
			return res.size()==1;
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}
}
