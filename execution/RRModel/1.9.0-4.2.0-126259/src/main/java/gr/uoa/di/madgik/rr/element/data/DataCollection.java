package gr.uoa.di.madgik.rr.element.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.ReadPolicy;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;

public class DataCollection extends RRElement
{
	private static final Logger logger = LoggerFactory
			.getLogger(DataCollection.class);
	
	private DataCollectionDao item=new DataCollectionDao();
	private RRContext context=null;

	private static boolean retrieved = false;
	
	public RRContext getISContext()
	{
		return this.context;
	}

	public DataCollection(/*String id*/) throws ResourceRegistryException
	{
		this.item.setID(UUID.randomUUID().toString());
		this.context=ResourceRegistry.getContext();
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

	public String getName()
	{
		return this.item.getName();
	}
	
	public void setName(String name)
	{
		this.item.setName(name);
	}

	public Set<String> getScopes()
	{
		return this.item.getScopes();
	}
	
	public void setScopes(Set<String> scopes)
	{
		this.item.setScopes(scopes);
	}

	public String getDescription()
	{
		return this.item.getDescription();
	}
	
	public void setDescription(String description)
	{
		this.item.setDescription(description);
	}
	
	public String getCollectionType()
	{
		return this.item.getCollectionType();
	}
	
	public void setCollectionType(String collectionType)
	{
		this.item.setCollectionType(collectionType);
	}

	public Calendar getCreationTime()
	{
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(Long.parseLong(this.item.getCreationTime()));
		return c;
	}
	
	public void setCreationTime(Calendar creationTime)
	{
		this.item.setCreationTime(Long.toString(creationTime.getTimeInMillis()));
	}
	
	@Override
	public DataCollectionDao getItem()
	{
		return this.item;
	}
	
	@Override
	public void setDirty()
	{
		this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	private void apply(IRRElement target, boolean applyDetails, RRContext.DatastoreType persistencyType, boolean doStore) throws ResourceRegistryException
	{
		if(!(target instanceof DataCollection)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setCreationTime(((DataCollection)target).item.getCreationTime());
		this.item.setDescription(((DataCollection)target).item.getDescription());
		this.item.setID(((DataCollection)target).item.getID());
		this.item.setCollectionType(((DataCollection)target).item.getCollectionType());
		this.item.setName(((DataCollection)target).item.getName());
		this.item.setScopes(((DataCollection)target).item.getScopes());
		if(doStore) this.store(applyDetails, persistencyType);
	}
	
	@Override
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return this.load(loadDetails, persistencyType, false);
	}
	
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType, boolean overridePrefetched) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		PersistenceManager pm = null;
		boolean locked = false;
		
		if(!this.exists(persistencyType)) return false;
	
		try
		{
			if(!overridePrefetched && persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
			{
				sharedLock.lock(); locked = true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
				{
					DataCollection retrieved = (DataCollection)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, loadDetails, persistencyType, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			pm.currentTransaction().begin();
			sharedLock.lock(); locked = true;
			this.item=pm.detachCopy(pm.getObjectById(DataCollectionDao.class, this.item.getID()));
			sharedLock.unlock(); locked = false;
			pm.currentTransaction().commit();
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
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		DataCollection c=new DataCollection();
		c.setID(this.getID());
		c.load(deleteDetails,persistencyType,true);
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			pm.currentTransaction().begin();
			pm.deletePersistent(c.item);
			pm.currentTransaction().commit();
			pm.flush();
		}finally
		{
			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			pm.close();
		}
	}
	
	@Override
	public void store(boolean storeDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		this.store(storeDetails, persistencyType, false);
	}
	
	private void store(boolean storeDetails, DatastoreType persistencyType, boolean writeThrough) throws ResourceRegistryException
	{
		if(this.exists(persistencyType) && !writeThrough)
		{
			DataCollection item=new DataCollection();
			item.setID(this.getID());
			item.load(storeDetails, persistencyType, true);
			item.apply(this, storeDetails, persistencyType, true);
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
		if(!(target instanceof DataCollection)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((DataCollection)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((DataCollection)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((DataCollection)target).item.getID()!=null && !this.item.getID().equals(((DataCollection)target).item.getID())) return false;
		if(this.item.getName() == null && ((DataCollection)target).item.getName()!=null) return false;
		if(this.item.getCollectionType() == null && ((DataCollection)target).item.getCollectionType()!=null) return false;
		if(this.item.getName() != null && ((DataCollection)target).item.getName()==null) return false;
		if(this.item.getName() != null && ((DataCollection)target).item.getName()!=null && !this.item.getName().equals(((DataCollection)target).item.getName())) return false;
		if(this.item.getScopes().size()!=((DataCollection)target).item.getScopes().size()) return false;
		if(!((DataCollection)target).item.getScopes().containsAll(this.item.getScopes())) return false;
		return true;
	}
	
	@Override
	public boolean equals(Object target)
	{
		if(target==null) return false;
		if(target==this) return true;
		if(!(target instanceof DataCollection)) return false;
		boolean res = false;
		try
		{
			this.isEqual((IRRElement)target, false);
		}catch(ResourceRegistryException e)
		{
			logger.warn("Unexpected error from isEqual", e);
			res = false;
		}
		return res;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 1;
		hash = hash * 17 + this.item.getID().hashCode();
		hash = hash * 31 + this.item.getName().hashCode();
		hash = hash * 13 + this.item.getScopes().hashCode();
		return hash;
	}
	
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append(this.item.deepToString());
		return buf.toString();
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
				sharedLock.lock(); locked=true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
					return true;
				sharedLock.unlock(); locked=false;
			}
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(DataCollectionDao.class, "exists");
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
	
	public static List<DataCollection> getAllCollections(boolean loadDetails) throws ResourceRegistryException
	{
		ReadPolicy policy;
		if(ResourceRegistry.isReadPolicySupported(ReadPolicy.REFRESH_AHEAD)) policy = ReadPolicy.REFRESH_AHEAD;
		else if(ResourceRegistry.isReadPolicySupported(ReadPolicy.READ_THROUGH))
		{
			if(!DataCollection.retrieved) policy = ReadPolicy.READ_THROUGH;
			else policy = ReadPolicy.READ_LOCAL;
		}else throw new ResourceRegistryException("Could not infer read policy");
		
		List<DataCollection> col = DataCollection.getAllCollections(loadDetails, policy);
		if(policy == ReadPolicy.READ_THROUGH) DataCollection.retrieved = true;
		return col;
	}
	
	@SuppressWarnings("unchecked")
	public static List<DataCollection> getAllCollections(boolean loadDetails, ReadPolicy policy) throws ResourceRegistryException
	{
		DatastoreType persistencyType;
		if(!ResourceRegistry.isReadPolicySupported(policy)) throw new ResourceRegistryException("Read policy not supported");
		if(policy == ReadPolicy.READ_LOCAL || policy == ReadPolicy.REFRESH_AHEAD) persistencyType = DatastoreType.LOCAL;
		else if(policy == ReadPolicy.READ_THROUGH)
		{
			if(ResourceRegistry.getContext().isDatastoreSupportedForRead(DatastoreType.REMOTE)) persistencyType = DatastoreType.REMOTE;
			else
			{
				ResourceRegistry.retrieveDirect(DataCollectionDao.class);
				return getAllCollections(loadDetails, ReadPolicy.READ_LOCAL);
			}
		}
		else throw new ResourceRegistryException("Unsupported read policy");
		
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<DataCollection> col=new ArrayList<DataCollection>();
		try 
		{
			if(ResourceRegistry.getContext().isTargetInMemory(DataCollection.class.getName()))
			{
				sharedLock.lock(); locked=true;
				Set<IRRElement> retrieved = InMemoryStore.getItems(DataCollection.class);
				sharedLock.unlock(); locked=false;
				if(retrieved != null)
				{
					for(IRRElement item : retrieved)
						col.add((DataCollection)item);
					return col;
				}
			}
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(DataCollectionDao.class, "allCollections");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute();
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				DataCollection f=new DataCollection();
				f.setID(item);
				f.load(loadDetails);
				col.add(f);
			}
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
		
		return col;
	}
	
	public static List<DataCollection> getCollectionsOfScope(boolean loadDetails, String scope) throws ResourceRegistryException
	{
		List<DataCollection> cols=DataCollection.getAllCollections(loadDetails);
		List<DataCollection> res=new ArrayList<DataCollection>();
		for(DataCollection col : cols) 
			if(col.getScopes().contains(scope)) res.add(col);
		return res;
	}
}
