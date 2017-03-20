package gr.uoa.di.madgik.rr.element.metadata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;

public class ElementMetadata extends RRElement 
{
	public static final String KeyValueDelimiter="#@emd@#";

	public enum Type 
	{
		DataSource,
		DeletedField,
		FieldRecentDeletion,
		FieldRecentUpdate,
		DeletedSearchable,
		DeletedPresentable
	}
	
	private ElementMetadataDao item = new ElementMetadataDao(); 
	private RRContext context=null;
	private Map<String, String> properties=null;
	
	public ElementMetadata() throws ResourceRegistryException
	{
		this.context = ResourceRegistry.getContext();
		this.item.setID(UUID.randomUUID().toString());
	}
	
	@Override
	public String getID() 
	{
		return item.getID();
	}

	@Override
	public void setID(String id) 
	{
		item.setID(id);
	}

	public Type getType()
	{
		return Type.valueOf(item.getType());
	}
	
	public void setType(Type type)
	{
		item.setType(type.toString());
	}
	
	public long getMetadataTimestamp()
	{
		return item.getMetadataTimestamp();
	}
	
	public void setMetadataTimestamp(long timestamp)
	{
		item.setMetadataTimestamp(timestamp);
	}
	
	public Map<String, String> getProperties()
	{
		if(this.properties==null) buildProperties();
		return this.properties;
	}
	
	@Override
	public ElementMetadataDao getItem()
	{
		return this.item;
	}
	
	@Override
	public void setDirty()
	{
		this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	private void buildProperties()
	{
		this.properties=new HashMap<String, String>();
		for(String k : this.item.getPropertyKeys())
		{
			if(this.properties.containsKey(k)) continue;
			for(String v:this.item.getPropertyValues())
			{
				String[] keyVal = v.split(KeyValueDelimiter);
				if(keyVal.length != 2) continue;
				if(keyVal[0].trim().equals(k))
				{
					this.properties.put(k, keyVal[1]);
					break;
				}
			}
		}
	}
	
	@Override
	public boolean exists(DatastoreType persistencyType) throws ResourceRegistryException 
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm=null;
		Query query=null;
		try 
		{
			if(persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName())) {
				sharedLock.lock(); locked = true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
					return true;
				sharedLock.unlock(); locked = false;
			}
			pm=ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(ElementMetadataDao.class, "exists");
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
	
	@Override
	public boolean load(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException 
	{
		return this.load(loadDetails, persistencyType, false);
	}
	
	public boolean load(boolean loadDetails, DatastoreType persistencyType, boolean overridePrefetched) throws ResourceRegistryException 
	{
		if(!this.exists(persistencyType)) return false;
		PersistenceManager pm = null;
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		try
		{
			if(!overridePrefetched && persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
			{
				sharedLock.lock(); locked = true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
				{
					ElementMetadata retrieved = (ElementMetadata)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, true, DatastoreType.LOCAL, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(ElementMetadataDao.class, this.item.getID()));
			pm.currentTransaction().commit();
			sharedLock.unlock(); locked = false;
//			for(Map.Entry<String, String> prop : this.item.getProperties().entrySet())
//			{
//				if(prop.getValue() == null)
//					this.item.getProperties().put(prop.getKey(), "");
//			}
				
		}finally
		{
			if(locked) sharedLock.unlock();
			if (pm!=null && pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			if(pm!=null) pm.close();
		}
		return true;
	}
	
	@Override
	public void store(boolean storeDetails, DatastoreType persistencyType) throws ResourceRegistryException 
	{
		this.store(storeDetails, persistencyType, false);
		
	}
	
	private void store(boolean storeDetails, DatastoreType persistencyType, boolean writeThrough) throws ResourceRegistryException
	{
		if(this.exists(persistencyType) && !writeThrough)
		{
			ElementMetadata item=new ElementMetadata();
			item.setID(this.getID());
			item.load(storeDetails,persistencyType,true);
			item.apply(this, storeDetails, persistencyType, true);
		}
		else
		{
			PersistenceManager pm = null;
			Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
			boolean locked = false;
			try
			{
				this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
				this.apply(this, storeDetails, persistencyType, false); //to update underlying key and value sets
				if(persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
				{
					InMemoryStore.setItem(this.getClass(), this);
				}
				pm = this.getISContext().getManagerForWrite(persistencyType);
				sharedLock.lock(); locked = true;
				pm.currentTransaction().begin();
				this.item=pm.detachCopy(pm.makePersistent(this.item));
				pm.currentTransaction().commit();
				sharedLock.unlock(); locked = false;
				pm.flush();
			}finally
			{
				if (pm!=null && pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
				if(pm!=null) pm.close();
				if(locked) sharedLock.unlock();
			}
		}
	}

	protected void apply(IRRElement target, boolean applyDetails, RRContext.DatastoreType persistencyType, boolean doStore) throws ResourceRegistryException
	{
		if(!(target instanceof ElementMetadata)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((ElementMetadata)target).item.getID());
		this.item.setType(((ElementMetadata)target).item.getType());
		this.item.setMetadataTimestamp(((ElementMetadata)target).item.getMetadataTimestamp());
		this.item.getPropertyKeys().clear();
		this.item.getPropertyValues().clear();
		for(Map.Entry<String, String> prop : ((ElementMetadata)target).getProperties().entrySet())
		{
			this.item.getPropertyKeys().add(prop.getKey());
			this.item.getPropertyValues().add(prop.getKey()+KeyValueDelimiter+prop.getValue());
		}
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
//	private void apply(IRRElement target, boolean applyDetails) throws ResourceRegistryException
//	{
//		if(!(target instanceof ElementMetadata)) throw new ResourceRegistryException("cannot apply to target of "+target);
//		if(this.isEqual(target,applyDetails)) return;
//		this.item.setID(((ElementMetadata)target).getID());
//		this.item.setMetadataTimestamp(((ElementMetadata)target).getMetadataTimestamp());
//		this.item.setType(((ElementMetadata)target).getType().toString());
//		this.item.getProperties().putAll(((ElementMetadata)target).getProperties());
//	}

	@Override
	public void delete(boolean deleteDetails, DatastoreType persistencyType) throws ResourceRegistryException 
	{
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		ElementMetadata s=new ElementMetadata();
		s.setID(this.getID());
		s.load(deleteDetails,persistencyType,true);
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			pm.currentTransaction().begin();
			pm.deletePersistent(s.item);
			pm.currentTransaction().commit();
			pm.flush();
		}finally
		{
			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			pm.close();
		}
	}

	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException 
	{
		if(!(target instanceof ElementMetadata)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((ElementMetadata)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((ElementMetadata)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((ElementMetadata)target).item.getID()!=null && !this.item.getID().equals(((ElementMetadata)target).item.getID())) return false;
		if(this.item.getType() == null && ((ElementMetadata)target).item.getType()!=null) return false;
		if(this.item.getType() != null && ((ElementMetadata)target).item.getType()==null) return false;
		if(this.item.getType() != null && ((ElementMetadata)target).item.getType()!=null && !this.item.getID().equals(((ElementMetadata)target).item.getType())) return false;
		if(this.item.getMetadataTimestamp() != (((ElementMetadata)target).item.getMetadataTimestamp())) return false;
		for(Map.Entry<String, String> property:  this.getProperties().entrySet())
		{
			if(!((ElementMetadata)target).getProperties().containsKey(property.getKey())) return false;
			if(!((ElementMetadata)target).getProperties().get(property).equals(property.getValue())) return false;
		}
		return true;
	}

	@Override
	public RRContext getISContext() 
	{
		return this.context;
	}
	
	public static ElementMetadata getById(boolean loadDetails, DatastoreType persistencyType, String id) throws ResourceRegistryException
	{
		ElementMetadata s = new ElementMetadata();
		s.setID(id);
		if(!s.load(loadDetails, persistencyType)) return null;
		return s;
	}
	
	public static ElementMetadata getById(boolean loadDetails, String id) throws ResourceRegistryException
	{
		return ElementMetadata.getById(loadDetails, DatastoreType.LOCAL, id);
	}
	
	public static List<ElementMetadata> getDeletedFieldsMetadata(boolean loadDetails) throws ResourceRegistryException
	{
		return ElementMetadata.getMetadataByType(loadDetails, ElementMetadata.Type.FieldRecentDeletion);
	}
	
	public static List<ElementMetadata> getUpdatedFieldsMetadata(boolean loadDetails) throws ResourceRegistryException
	{
		return ElementMetadata.getMetadataByType(loadDetails, ElementMetadata.Type.FieldRecentUpdate);
	}
	
	public static List<ElementMetadata> getDeletedSearchablesMetadata(boolean loadDetails) throws ResourceRegistryException
	{
		return ElementMetadata.getMetadataByType(loadDetails, ElementMetadata.Type.DeletedSearchable);
	}
	
	public static List<ElementMetadata> getDeletedPresentablesMetadata(boolean loadDetails) throws ResourceRegistryException
	{
		return ElementMetadata.getMetadataByType(loadDetails, ElementMetadata.Type.DeletedPresentable);
	}
	
	@SuppressWarnings("unchecked")
	public static List<ElementMetadata> getMetadataByType(boolean loadDetails, ElementMetadata.Type type) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try {
			pm=ResourceRegistry.getContext().getManagerForRead(DatastoreType.LOCAL);
			query=pm.newNamedQuery(ElementMetadataDao.class, "metadataOfType");
			query.compile();
			Map<String, Object> args=new HashMap<String, Object>();
			args.put("type", type.toString());
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			List<ElementMetadata> col = new ArrayList<ElementMetadata>();
			for(String item : res)
			{
				ElementMetadata m=new ElementMetadata();
				m.setID(item);
				m.load(loadDetails);
				col.add(m);
			}
			return col;
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if (pm!=null && pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			if(pm!=null) pm.close();
		}
	}

}
