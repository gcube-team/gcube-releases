package gr.uoa.di.madgik.rr.element.search;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata.Type;

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

public class Presentable extends RRElement
{
	private static final Logger logger = LoggerFactory
			.getLogger(Presentable.class);
	
	private PresentableDao item=new PresentableDao();
	private RRContext context=null;
	
	@Override
	public RRContext getISContext()
	{
		return this.context;
	}

	public Presentable(/*String id*/) throws ResourceRegistryException
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
	
	public String getExpression()
	{
		return this.item.getExpression();
	}
	
	public void setExpression(String expression)
	{
		this.item.setExpression(expression);
	}
	
	public Set<String> getPresentationInfo()
	{
		return this.item.getPresentationInfo();
	}
	
	public void setPresentationInfo(Set<String> presentationInfo)
	{
		this.item.setPresentationInfo(presentationInfo);
	}
	
	public String getField()
	{
		return this.item.getField();
	}
	
	public void setField(String field)
	{
		this.item.setField(field);
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
	public PresentableDao getItem()
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
					Presentable retrieved = (Presentable)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, true, DatastoreType.LOCAL, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(PresentableDao.class, this.item.getID()));
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
		logger.info("Presentable with id : " + this.getID() + " has been deleted");
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		Presentable p=new Presentable();
		p.setID(this.getID());
		p.load(deleteDetails,persistencyType,true);
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			ElementMetadata metadata = new ElementMetadata();
			metadata.setID(this.getID());
			logger.info("Presentable with id : " + this.getID() + " has been marked as DELETED_PRESENTABLE");
			metadata.setType(Type.DeletedPresentable);
			metadata.getProperties().put("field_id", this.getField());
			metadata.getProperties().put("presentable_id", this.getID());
			metadata.getProperties().put("collection", this.getCollection());
			metadata.getProperties().put("locator", this.getLocator());
			metadata.store(true);
			
			pm.currentTransaction().begin();
			pm.deletePersistent(p.item);
			pm.currentTransaction().commit();
			pm.flush();
			
			try {
				Field f = Field.getById(true, this.getField());
				Presentable fp = f.getPresentable(this.getID());
				if (fp == null) {
					logger.warn("Cannot get presentable : " + this.getID() +  " from field " + this.getField() + ". Maybe dangling");
				} else {
					logger.info("In delete presentable field presentable delete : " + fp.getID());
					logger.info("In delete presentable field presentable before delete : " + f.getSearchables().size());
					f.getPresentables().remove(fp);
					logger.info("In delete presentable field presentable after delete : " + f.getSearchables().size());
					f.store(true);
				}
			} catch (Exception e) {
				logger.warn("Error while deleting presentable with id : " + this.getID() + " from fields", e);
			}
			
		}catch (Exception e) {
			logger.warn("Error while deleting presentable with id : " + this.getID());
			
		}finally
		{
			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			pm.close();
		}
	}

	public static void saveAll(List<Presentable> elements, RRContext.DatastoreType persistencyType) throws ResourceRegistryException{
		PersistenceManager pm = ResourceRegistry.getContext().getManagerForWrite(persistencyType);
		try
		{
			pm.currentTransaction().begin();
			for (Presentable element : elements){
					element.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
					if(persistencyType.equals(DatastoreType.LOCAL) && ResourceRegistry.getContext().isTargetInMemory(Presentable.class.getName()))
						InMemoryStore.setItem(Presentable.class, element);
					pm.detachCopy(pm.makePersistent(element.item));
			}
			pm.currentTransaction().commit();
			pm.flush();
		} catch (Exception e){
			logger.warn("error while saving all presentables", e);
		}
		finally
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
			Presentable item=new Presentable();
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
				this.item= pm.detachCopy(pm.makePersistent(this.item));
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
		if(!(target instanceof Presentable)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setCollection(((Presentable)target).item.getCollection());
		this.item.setField(((Presentable)target).item.getField());
		this.item.setLocator(((Presentable)target).item.getLocator());
		this.item.setExpression(((Presentable)target).item.getExpression());
		this.item.setPresentationInfo(((Presentable)target).item.getPresentationInfo());
		this.item.setOrder(((Presentable)target).item.isOrder());
		this.item.setDatasourceScopes(((Presentable)target).item.getDatasourceScopes());
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException
	{
		if(!(target instanceof Presentable)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((Presentable)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((Presentable)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((Presentable)target).item.getID()!=null && !this.item.getID().equals(((Presentable)target).item.getID())) return false;
		if(this.item.getCollection() == null && ((Presentable)target).item.getCollection()!=null) return false;
		if(this.item.getCollection() != null && ((Presentable)target).item.getCollection()==null) return false;
		if(this.item.getCollection() != null && ((Presentable)target).item.getCollection()!=null && !this.item.getCollection().equals(((Presentable)target).item.getCollection())) return false;
		if(this.item.getField() == null && ((Presentable)target).item.getField()!=null) return false;
		if(this.item.getField() != null && ((Presentable)target).item.getField()==null) return false;
		if(this.item.getField() != null && ((Presentable)target).item.getField()!=null && !this.item.getField().equals(((Presentable)target).item.getField())) return false;
		if(this.item.getLocator() == null && ((Presentable)target).item.getLocator()!=null) return false;
		if(this.item.getLocator() != null && ((Presentable)target).item.getLocator()==null) return false;
		if(this.item.getLocator() != null && ((Presentable)target).item.getLocator()!=null && !this.item.getLocator().equals(((Presentable)target).item.getLocator())) return false;
		if(this.item.getExpression() == null && ((Presentable)target).item.getExpression()!=null) return false;
		if(this.item.getExpression() != null && ((Presentable)target).item.getExpression()==null) return false;
		if(this.item.getExpression() != null && ((Presentable)target).item.getExpression()!=null && !this.item.getExpression().equals(((Presentable)target).item.getExpression())) return false;
		if(this.item.getPresentationInfo() == null && ((Presentable)target).item.getPresentationInfo()!=null) return false;
		if(this.item.getPresentationInfo() != null && ((Presentable)target).item.getPresentationInfo()==null) return false;
		if(this.item.getPresentationInfo() != null && ((Presentable)target).item.getPresentationInfo()!=null && !this.item.getPresentationInfo().equals(((Presentable)target).item.getPresentationInfo())) return false;
		if(!(this.item.isOrder()==((Presentable)target).item.isOrder())) return false;
		if(this.item.getDatasourceScopes().size()!=((Presentable)target).item.getDatasourceScopes().size()) return false;
		if(!this.item.getDatasourceScopes().containsAll(((Presentable)target).getDatasourceScopes())) return false;
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
			query=pm.newNamedQuery(PresentableDao.class, "exists");
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
