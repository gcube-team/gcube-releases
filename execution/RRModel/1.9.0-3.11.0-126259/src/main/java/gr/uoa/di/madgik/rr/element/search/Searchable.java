package gr.uoa.di.madgik.rr.element.search;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.execution.ExecutionService;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata.Type;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Searchable extends RRElement
{
	private SearchableDao item=new SearchableDao();
	private RRContext context=null;
	private static final Logger logger = LoggerFactory
			.getLogger(ExecutionService.class);
	
	
	@Override
	public RRContext getISContext()
	{
		return this.context;
	}

	public Searchable(/*String id*/) throws ResourceRegistryException
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
	
	public String getLocator()
	{
		return this.item.getLocator();
	}
	
	public void setLocator(String locator)
	{
		this.item.setLocator(locator);
	}
	
	public String getExpression()
	{
		return this.item.getExpression();
	}
	
	public void setExpression(String expression)
	{
		this.item.setExpression(expression);
	}
	
	public Boolean isOrder()
	{
		if (this.item.isOrder() == null)
			return false;
		return this.item.isOrder();
	}
	
	public void setOrder(Boolean order)
	{
		this.item.setOrder(order);
	}
	
	public String getField()
	{
		return this.item.getField();
	}
	
	public void setField(String field)
	{
		this.item.setField(field);
	}
	
	public Set<String> getCapabilities()
	{
		return this.item.getCapabilities();
	}
	
	public Set<String> getDatasourceScopes()
	{
		return item.getDatasourceScopes();
	}
	
	public void setDatasourceScopes(Set<String> datasourceScopes)
	{
		item.setDatasourceScopes(datasourceScopes);
	}
	
	@Override
	public SearchableDao getItem()
	{
		return this.item;
	}
	
	@Override
	public void setDirty()
	{
		this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
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
					Searchable retrieved = (Searchable)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, true, DatastoreType.LOCAL, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(SearchableDao.class, this.item.getID()));
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
		logger.info("Searchable with id : " + this.getID() + " has been deleted");
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		Searchable s=new Searchable();
		s.setID(this.getID());
		s.load(deleteDetails,persistencyType,true);
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			ElementMetadata metadata = new ElementMetadata();
			metadata.setID(this.getID());
			logger.info("Searchable with id : " + this.getID() + " has been marked as DELETED_SEARCHABLE");
			metadata.setType(Type.DeletedSearchable);
			metadata.getProperties().put("field_id", this.getField());
			metadata.getProperties().put("searchable_id", this.getID());
			metadata.getProperties().put("collection", this.getCollection());
			metadata.getProperties().put("locator", this.getLocator());
			metadata.store(true);
			
			
			pm.currentTransaction().begin();
			pm.deletePersistent(s.item);
			pm.currentTransaction().commit();
			pm.flush();
			
			try {
				Field f = Field.getById(true, this.getField());
				Searchable fs = f.getSearchable(this.getID());
				if (fs == null) {
					logger.warn("Cannot get searchable : " + this.getID() +  " from field " + this.getField() + ". Maybe dangling");
				} else {
					logger.info("In delete searchable field searchable delete : " + fs.getID());
					logger.info("In delete searchable field searchable before delete : " + f.getSearchables().size());
					f.getSearchables().remove(fs);
					logger.info("In delete searchable field searchable after delete : " + f.getSearchables().size());
					f.store(true);
				}
			} catch (Exception e) {
				logger.warn("Error while deleting searchable with id : " + this.getID() + " from fields",e );
			}
			
		} catch (Exception e) {
			logger.warn("Error while deleting searchable with id : " + this.getID());
			
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
	
	public static void saveAll(List<Searchable> elements, RRContext.DatastoreType persistencyType) throws ResourceRegistryException{
		PersistenceManager pm = ResourceRegistry.getContext().getManagerForWrite(persistencyType);
		try
		{
			pm.currentTransaction().begin();
			for (Searchable element : elements){
					element.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
					if(persistencyType.equals(DatastoreType.LOCAL) && ResourceRegistry.getContext().isTargetInMemory(Searchable.class.getName()))
						InMemoryStore.setItem(Searchable.class, element);
					pm.detachCopy(pm.makePersistent(element.item));
			}
			pm.currentTransaction().commit();
			pm.flush();
		} catch (Exception e){
			logger.warn("error while saving all searchables", e);
		}
		finally
		{
			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			pm.close();
		}
	}
	
	private void store(boolean storeDetails, RRContext.DatastoreType persistencyType, boolean writeThrough) throws ResourceRegistryException
	{
		if(this.exists(persistencyType) && !writeThrough)
		{
			Searchable item=new Searchable();
			item.setID(this.getID());
			item.load(storeDetails,persistencyType,true);
			item.apply(this, storeDetails, persistencyType, true);
		}
		else
		{
			PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
			try
			{
				this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
				if(persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
					InMemoryStore.setItem(this.getClass(), this);
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

	protected void apply(IRRElement target, boolean applyDetails, RRContext.DatastoreType persistencyType, boolean doStore) throws ResourceRegistryException
	{
		if(!(target instanceof Searchable)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setCollection(((Searchable)target).item.getCollection());
		this.item.setField(((Searchable)target).item.getField());
		this.item.setLocator(((Searchable)target).item.getLocator());
		this.item.setExpression(((Searchable)target).item.getExpression());
		this.item.setOrder(((Searchable)target).item.isOrder());
		this.item.setCapabilities(((Searchable)target).item.getCapabilities());
		this.item.setDatasourceScopes(((Searchable)target).item.getDatasourceScopes());
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException
	{
		if(!(target instanceof Searchable)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((Searchable)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((Searchable)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((Searchable)target).item.getID()!=null && !this.item.getID().equals(((Searchable)target).item.getID())) return false;
		if(this.item.getCollection() == null && ((Searchable)target).item.getCollection()!=null) return false;
		if(this.item.getCollection() != null && ((Searchable)target).item.getCollection()==null) return false;
		if(this.item.getCollection() != null && ((Searchable)target).item.getCollection()!=null && !this.item.getCollection().equals(((Searchable)target).item.getCollection())) return false;
		if(this.item.getField() == null && ((Searchable)target).item.getField()!=null) return false;
		if(this.item.getField() != null && ((Searchable)target).item.getField()==null) return false;
		if(this.item.getField() != null && ((Searchable)target).item.getField()!=null && !this.item.getField().equals(((Searchable)target).item.getField())) return false;
		if(this.item.getLocator() == null && ((Searchable)target).item.getLocator()!=null) return false;
		if(this.item.getLocator() != null && ((Searchable)target).item.getLocator()==null) return false;
		if(this.item.getLocator() != null && ((Searchable)target).item.getLocator()!=null && !this.item.getLocator().equals(((Searchable)target).item.getLocator())) return false;
		if(this.item.getExpression() == null && ((Searchable)target).item.getExpression()!=null) return false;
		if(this.item.getExpression() != null && ((Searchable)target).item.getExpression()==null) return false;
		if(this.item.getExpression() != null && ((Searchable)target).item.getExpression()!=null && !this.item.getExpression().equals(((Searchable)target).item.getExpression())) return false;
		if(!(this.item.isOrder()==((Searchable)target).item.isOrder())) return false;
		if(this.item.getCapabilities().size()!=((Searchable)target).getCapabilities().size()) return false;
		if(!((Searchable)target).getCapabilities().containsAll(this.item.getCapabilities())) return false;
		if(this.item.getDatasourceScopes().size()!=((Searchable)target).item.getDatasourceScopes().size()) return false;
		if(!this.item.getDatasourceScopes().containsAll(((Searchable)target).getDatasourceScopes())) return false;
		return true;
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
				sharedLock.lock(); locked = true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
					return true;
				sharedLock.unlock(); locked = false;
			}
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(SearchableDao.class, "exists");
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
	public static Set<String> getFieldsOfCollections(Set<String> collections) throws ResourceRegistryException
	{
		Set<String> col=new HashSet<String>();
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(SearchableDao.class, "fieldsOfCollections");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute(collections);
			sharedLock.unlock(); locked = false;
			for(String i : res) col.add(i);
			return col;
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}
}
