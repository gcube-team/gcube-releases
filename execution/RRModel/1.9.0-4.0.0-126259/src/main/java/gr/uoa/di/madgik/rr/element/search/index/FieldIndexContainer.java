package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldIndexContainer extends RRElement
{
	private static final Logger logger = LoggerFactory
			.getLogger(FieldIndexContainer.class);
	
	public enum FieldType
	{
		Presentable,
		Searchable
	}
	
	private FieldIndexContainerDao item=new FieldIndexContainerDao();
	private RRContext context=null;

	@Override
	public RRContext getISContext()
	{
		return this.context;
	}
	
	public FieldIndexContainer() throws ResourceRegistryException
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

	public String getCollection()
	{
		return this.item.getCollection();
	}

	public void setCollection(String collection)
	{
		this.item.setCollection(collection);
	}

	public boolean isSearchable()
	{
		return this.item.getFieldType().equalsIgnoreCase("s");
	}

	public void setSearchable(boolean isSearchable)
	{
		this.item.setFieldType("s");
	}

	public boolean isPresentable()
	{
		return this.item.getFieldType().equalsIgnoreCase("p");
	}

	public void setPresentable(boolean isPresentable)
	{
		this.item.setFieldType("p");
	}

	public String getLanguage()
	{
		return this.item.getLanguage();
	}

	public void setLanguage(String language)
	{
		this.item.setLanguage(language);
	}

	public String getField()
	{
		return this.item.getField();
	}

	public void setField(String field)
	{
		this.item.setField(field);
	}
	
	public String getExpression()
	{
		return this.item.getExpression();
	}
	
	public void setExpression(String expression)
	{
		this.item.setExpression(expression);
	}
	
	@Override
	public FieldIndexContainerDao getItem()
	{
		return this.item;
	}
	
	@Override
	public void setDirty()
	{
		this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	private void apply(IRRElement target, boolean applyDetails, DatastoreType persistencyType, boolean doStore) throws ResourceRegistryException
	{
		if(!(target instanceof FieldIndexContainer)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((FieldIndexContainer)target).item.getID());
		this.item.setCollection(((FieldIndexContainer)target).item.getCollection());
		this.item.setField(((FieldIndexContainer)target).item.getField());
		this.item.setFieldType(((FieldIndexContainer)target).item.getFieldType());
		this.item.setLanguage(((FieldIndexContainer)target).item.getLanguage());
		this.item.setExpression(((FieldIndexContainer)target).item.getExpression());
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
	@Override
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return this.load(loadDetails, persistencyType, false);
	}
	
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType, boolean overridePrefetched) throws ResourceRegistryException
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
					FieldIndexContainer retrieved = (FieldIndexContainer)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, true, persistencyType, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(FieldIndexContainerDao.class, this.item.getID()));
			pm.currentTransaction().commit();
			sharedLock.unlock(); locked = false;
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
		FieldIndexContainer f=new FieldIndexContainer();
		f.setID(this.getID());
		f.load(deleteDetails,persistencyType,true);
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			pm.currentTransaction().begin();
			pm.deletePersistent(f.item);
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
	
	private void store(boolean storeDetails, RRContext.DatastoreType persistencyType, boolean writeThrough) throws ResourceRegistryException
	{
		if(this.exists(persistencyType) && !writeThrough)
		{
			FieldIndexContainer item=new FieldIndexContainer();
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
		if(!(target instanceof FieldIndexContainer)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((FieldIndexContainer)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((FieldIndexContainer)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((FieldIndexContainer)target).item.getID()!=null && !this.item.getID().equals(((FieldIndexContainer)target).item.getID())) return false;
		if(this.item.getCollection() == null && ((FieldIndexContainer)target).item.getCollection()!=null) return false;
		if(this.item.getCollection() != null && ((FieldIndexContainer)target).item.getCollection()==null) return false;
		if(this.item.getCollection() != null && ((FieldIndexContainer)target).item.getCollection()!=null && !this.item.getCollection().equals(((FieldIndexContainer)target).item.getCollection())) return false;
		if(this.item.getLanguage() == null && ((FieldIndexContainer)target).item.getLanguage()!=null) return false;
		if(this.item.getLanguage() != null && ((FieldIndexContainer)target).item.getLanguage()==null) return false;
		if(this.item.getLanguage() != null && ((FieldIndexContainer)target).item.getLanguage()!=null && !this.item.getLanguage().equals(((FieldIndexContainer)target).item.getLanguage())) return false;
		if(this.item.getField() == null && ((FieldIndexContainer)target).item.getField()!=null) return false;
		if(this.item.getField() != null && ((FieldIndexContainer)target).item.getField()==null) return false;
		if(this.item.getField() != null && ((FieldIndexContainer)target).item.getField()!=null && !this.item.getField().equals(((FieldIndexContainer)target).item.getField())) return false;
		if(this.item.getFieldType() == null && ((FieldIndexContainer)target).item.getFieldType()!=null) return false;
		if(this.item.getFieldType() != null && ((FieldIndexContainer)target).item.getFieldType()==null) return false;
		if(this.item.getFieldType() != null && ((FieldIndexContainer)target).item.getFieldType()!=null && !this.item.getFieldType().equals(((FieldIndexContainer)target).item.getFieldType())) return false;
		return true;
	}
	
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("FieldIndexContainer - ID : "+this.getID()+"\n");
		buf.append("FieldIndexContainer - Collection : "+this.getCollection()+"\n");
		buf.append("FieldIndexContainer - Language : "+this.getLanguage()+"\n");
		buf.append("FieldIndexContainer - Field : "+this.getField()+"\n");
		buf.append("FieldIndexContainer - isPresentable : "+this.isPresentable()+"\n");
		buf.append("FieldIndexContainer - isSearchable : "+this.isSearchable()+"\n");
		if(this.getExpression() != null && !this.getExpression().trim().equals("")) buf.append("FieldIndexContainer - expression : " +this.getExpression()+"\n");
		
		return buf.toString();
	}

	public static List<FieldIndexContainer> queryByFieldIDAndType(String fieldID, FieldType type) throws ResourceRegistryException
	{
		String stype="s"; 
		switch(type)
		{
			case Presentable:
			{
				stype="p";
				break;
			}
			case Searchable:
			default:
			{
				stype="s";
				break;
			}
		}
		return FieldIndexContainer.queryByFieldIDAndType(RRContext.DatastoreType.LOCAL, fieldID, stype);
	}
	
	public static List<FieldIndexContainer> queryByFieldIDAndTypeAndScope(String fieldID, FieldType type, String scope) throws ResourceRegistryException
	{
		String stype="s"; 
		switch(type)
		{
			case Presentable:
			{
				stype="p";
				break;
			}
			case Searchable:
			default:
			{
				stype="s";
				break;
			}
		}
		return FieldIndexContainer.queryByFieldIDAndTypeAndScope(RRContext.DatastoreType.LOCAL, fieldID, stype, scope);
	}

	public static List<FieldIndexContainer> queryByFieldIDAndTypeAndScope(RRContext.DatastoreType persistencyType, String fieldID, String type, String scope) throws ResourceRegistryException
	{
		List<FieldIndexContainer> filteredFieldInfos = new ArrayList<FieldIndexContainer>();
		List<FieldIndexContainer> fieldInfos = FieldIndexContainer.queryByFieldIDAndType(persistencyType, fieldID, type);
		List<DataSource> all=DataSource.getAll(true);
		for(FieldIndexContainer nfo : fieldInfos)
		{
			boolean found = false;
			for(DataSource ind:all)
			{
				if(!ind.getScopes().contains(scope)) continue; 
				for(FieldIndexContainer indFieldInfo : ind.getFieldInfo()) {
					if(indFieldInfo.getID().equals(nfo.getID()))
					{
						filteredFieldInfos.add(nfo); 
						found = true;
						break;
					}
				}
				if(found == true) break;
			}
		}
		return filteredFieldInfos;
	}
	
	public static List<FieldIndexContainer> queryByFieldIDAndTypeAndCollection(String fieldID, String collection, FieldType type) throws ResourceRegistryException
	{
		String stype="s"; 
		switch(type)
		{
			case Presentable:
			{
				stype="p";
				break;
			}
			case Searchable:
			default:
			{
				stype="s";
				break;
			}
		}
		return FieldIndexContainer.queryByFieldIDAndTypeAndCollection(RRContext.DatastoreType.LOCAL, fieldID, collection, stype);
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
			query=pm.newNamedQuery(FieldIndexContainerDao.class, "exists");
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

	@SuppressWarnings("unchecked")
	public static List<FieldIndexContainer> queryByFieldIDAndType(RRContext.DatastoreType persistencyType, String fieldID, String type) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(FieldIndexContainerDao.class, "queryByFieldIDAndType");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("id", fieldID);
			args.put("type", type);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			ArrayList<FieldIndexContainer> items=new ArrayList<FieldIndexContainer>();
			for(String id : res)
			{
				FieldIndexContainer item=new FieldIndexContainer();
				item.setID(id);
				item.load(true);
				items.add(item);
			}
			return items;
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<FieldIndexContainer> queryByFieldIDAndTypeAndCollection(RRContext.DatastoreType persistencyType, String fieldID,String collection, String type) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try 
		{
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(FieldIndexContainerDao.class, "queryByFieldIDAndTypeAndCollection");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("id", fieldID);
			args.put("type", type);
			args.put("collection", collection);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			ArrayList<FieldIndexContainer> items=new ArrayList<FieldIndexContainer>();
			for(String id : res)
			{
				FieldIndexContainer item=new FieldIndexContainer();
				item.setID(id);
				item.load(true);
				items.add(item);
			}
			return items;
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<FieldIndexContainer> queryByFieldIDAndTypeAndLanguage(RRContext.DatastoreType persistencyType, String fieldID,String language, String type) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(FieldIndexContainerDao.class, "queryByFieldIDAndTypeAndLanguage");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("id", fieldID);
			args.put("type", type);
			args.put("language", language);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			ArrayList<FieldIndexContainer> items=new ArrayList<FieldIndexContainer>();
			for(String id : res)
			{
				FieldIndexContainer item=new FieldIndexContainer();
				item.setID(id);
				item.load(true);
				items.add(item);
			}
			return items;
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<FieldIndexContainer> queryByFieldIDAndTypeAndCollectionAndLanguage(RRContext.DatastoreType persistencyType, String fieldID,String collection, String language, String type) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try 
		{
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(FieldIndexContainerDao.class, "queryByFieldIDAndTypeAndCollectionAndLanguage");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("id", fieldID);
			args.put("type", type);
			args.put("collection", collection);
			args.put("language", language);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			ArrayList<FieldIndexContainer> items=new ArrayList<FieldIndexContainer>();
			for(String id : res)
			{
				FieldIndexContainer item=new FieldIndexContainer();
				item.setID(id);
				item.load(true);
				items.add(item);
			}
			return items;
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}
}
