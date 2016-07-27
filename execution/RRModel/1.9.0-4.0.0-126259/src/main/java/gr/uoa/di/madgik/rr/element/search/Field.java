package gr.uoa.di.madgik.rr.element.search;

import java.util.ArrayList;
import java.util.Arrays;
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

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.RRContext.ReadPolicy;
import gr.uoa.di.madgik.rr.RRContext.WritePolicy;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.execution.ExecutionService;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata.Type;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.query.SourceHelper;
import gr.uoa.di.madgik.rr.element.search.index.DataSource;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceDao;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

public class Field extends RRElement
{
	public static class Behavior
	{
		private boolean markUpdate = true;
		private boolean markDeletion = true;
		
		public boolean isMarkUpdate() { return markUpdate; }
		public void setMarkUpdate(boolean markUpdate) { this.markUpdate = markUpdate; }
		
		public boolean isMarkDeletion() { return markDeletion; }
		public void setMarkDeletion(boolean markDeletion) { this.markDeletion = markDeletion; }
	}
	
	private FieldDao item=new FieldDao();
	private Set<Searchable> searchables=new HashSet<Searchable>();
	private Set<Presentable> presentables=new HashSet<Presentable>();
	private RRContext context=null;
	private static Behavior behavior = new Behavior();
	
	private static final Logger logger = LoggerFactory
			.getLogger(Field.class);
	
	@Override
	public RRContext getISContext()
	{
		return this.context;
	}
	
	public static Behavior getBehavior()
	{
		return behavior;
	}

	public Field(/*String id*/) throws ResourceRegistryException
	{
		this.item.setID(UUID.randomUUID().toString());
		this.context=ResourceRegistry.getContext();
	}
	
	public Field(FieldDao item) throws ResourceRegistryException
	{
		this.item=item;
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
	
	public String getDescription()
	{
		return this.item.getDescription();
	}
	
	public void setDescription(String description)
	{
		this.item.setDescription(description);
	}
	
	public Set<Searchable> getSearchables()
	{
		return this.searchables;
	}
	
	public Set<Presentable> getPresentables()
	{
		return this.presentables;
	}
	
	@Override
	public FieldDao getItem()
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
	
		boolean memLoaded = false;
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
					Field retrieved = (Field)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, true, persistencyType, false);
					memLoaded = true;
				}
				sharedLock.unlock(); locked=false;
			}
			if(!memLoaded)
			{
				pm = this.getISContext().getManagerForRead(persistencyType);
				sharedLock.lock(); locked = true;
				pm.currentTransaction().begin();
				this.item=pm.detachCopy(pm.getObjectById(FieldDao.class, this.item.getID()));
				pm.currentTransaction().commit();
				sharedLock.unlock(); locked = false;
			}
		}finally
		{
			if(locked) sharedLock.unlock();
			if (pm!=null && pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			if(pm!=null) pm.close();
		}
		this.searchables.clear();
		for(String item : this.item.getSearchables())
		{
			Searchable w=new Searchable();
			w.setID(item);
			if(w.exists(persistencyType))
			{
				if(loadDetails) w.load(loadDetails,persistencyType,overridePrefetched);
				this.searchables.add(w);
			}
		}
		this.presentables.clear();
		for(String item : this.item.getPresentables())
		{
			Presentable w=new Presentable();
			w.setID(item);
			if(w.exists(persistencyType))
			{
				if(loadDetails) w.load(loadDetails,persistencyType,overridePrefetched);
				this.presentables.add(w);
			}
		}
		return true;
	}
	
	
	public boolean load(boolean loadDetails, String scope) throws ResourceRegistryException
	{
		if(ResourceRegistry.isReadPolicySupported(RRContext.ReadPolicy.REFRESH_AHEAD))  
			return this.load(loadDetails, scope, DatastoreType.LOCAL);
		else if(ResourceRegistry.isReadPolicySupported(RRContext.ReadPolicy.READ_THROUGH) && ResourceRegistry.isReadPolicySupported(ReadPolicy.READ_LOCAL))
		{
			if(this.load(loadDetails, scope, ReadPolicy.READ_LOCAL)) return true;
			return this.load(loadDetails, scope, ReadPolicy.READ_THROUGH);
		}
		
		throw new ResourceRegistryException("Failed to find supported read policy");	
	}
	
	public boolean load(boolean loadDetails, String scope, RRContext.ReadPolicy policy) throws ResourceRegistryException
	{
		if(!ResourceRegistry.isReadPolicySupported(policy)) throw new ResourceRegistryException("Read policy not supported");
		if(policy == ReadPolicy.READ_LOCAL || policy == ReadPolicy.REFRESH_AHEAD)
			return this.load(loadDetails, scope, DatastoreType.LOCAL);
		else if(policy == ReadPolicy.READ_THROUGH)
		{
			boolean result = false;
			if(ResourceRegistry.getContext().isDatastoreSupportedForRead(DatastoreType.REMOTE))
			{
				result = this.load(loadDetails, scope, DatastoreType.REMOTE);
				if(result) this.store(loadDetails, scope, DatastoreType.LOCAL);
				return result;
			}
			else 
			{
				ResourceRegistry.retrieveDirect(this.getClass(), this.getID());
				return this.load(loadDetails, scope, DatastoreType.LOCAL);
			}
		}
		else throw new ResourceRegistryException("Unsupported read policy");
	}
	
	public boolean load(boolean loadDetails, String scope, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		if(!this.load(loadDetails, persistencyType))
			return false;
		
		if(loadDetails)
		{
			Set<Searchable> filteredSearchables = new HashSet<Searchable>();
			for(Searchable s : this.searchables) 
			{
				IRRElement source = QueryHelper.GetSourceById(s.getLocator());
				if(source!=null) 
				{
					if(SourceHelper.getScopesOfSource(source).contains(scope))
						filteredSearchables.add(s);
				}else logger.debug( "DataSource with id " + s.getLocator() + " not found");
			}
			Set<Presentable> filteredPresentables = new HashSet<Presentable>();
			for(Presentable p : this.presentables) 
			{
				IRRElement source = QueryHelper.GetSourceById(p.getLocator());
				if(source!=null)
				{
					if(SourceHelper.getScopesOfSource(source).contains(scope))
						filteredPresentables.add(p);
				}else logger.debug( "DataSource with id " + p.getLocator() + " not found");
			}
			this.searchables = filteredSearchables;
			this.presentables = filteredPresentables;
		}
		return true;
	}
	
	@Override
	public void delete(boolean deleteDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		Field f=new Field();
		f.setID(this.getID());
		f.load(deleteDetails,persistencyType,true);
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			ElementMetadata metadata = new ElementMetadata();
			metadata.setID(f.getName());
			metadata.setType(Type.DeletedField);
			metadata.store(true);
			
			if(Field.behavior.isMarkDeletion())
			{
				ElementMetadata deleteMetadata = new ElementMetadata();
				deleteMetadata.setID(this.getID()+Type.FieldRecentDeletion);
				deleteMetadata.setType(Type.FieldRecentDeletion);
				deleteMetadata.getProperties().put("id", f.getID());
				deleteMetadata.store(true);
			}
			
			pm.currentTransaction().begin();
			pm.deletePersistent(f.item);
			pm.currentTransaction().commit();
			pm.flush();
			
		}finally
		{
			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			pm.close();
		}
		if(deleteDetails)
		{
			for(Searchable item : f.searchables) item.delete(deleteDetails,persistencyType);
			for(Presentable item : f.presentables) item.delete(deleteDetails,persistencyType);
		}
	}
	
	public void delete(boolean deleteDetails, String scope) throws ResourceRegistryException
	{
		if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_BEHIND))  
			this.delete(deleteDetails, scope, DatastoreType.LOCAL);
		else if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_THROUGH))
			this.delete(deleteDetails, scope, WritePolicy.WRITE_THROUGH);
		else
			throw new ResourceRegistryException("Failed to find supported write policy");	
	}
	
	public void delete(boolean deleteDetails, String scope, WritePolicy policy) throws ResourceRegistryException
	{
		if(!ResourceRegistry.isWritePolicySupported(policy)) throw new ResourceRegistryException("Write policy not supported");
		if(policy == WritePolicy.WRITE_LOCAL || policy == WritePolicy.WRITE_BEHIND)
			this.delete(deleteDetails, scope, DatastoreType.LOCAL);
		else if(policy == WritePolicy.WRITE_THROUGH)
		{
			if(ResourceRegistry.getContext().isDatastoreSupportedForWrite(DatastoreType.REMOTE))
			{
				this.delete(deleteDetails, scope, DatastoreType.REMOTE);
				this.delete(deleteDetails, scope, DatastoreType.LOCAL);
			}
			else 
			{
				throw new ResourceRegistryException("This element does not support deletion");
			}
		}
		else throw new ResourceRegistryException("Unsupported write policy");
	}
	
	public void delete(boolean deleteDetails, String scope, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		if(!this.exists(persistencyType)) return;
		Field f=new Field();
		f.setID(this.getID());
		f.load(deleteDetails,persistencyType,true);
		
		Set<Searchable> filteredSearchables = new HashSet<Searchable>();
		boolean shouldDelete = true;
		for(Searchable item : f.searchables)
		{
			IRRElement source = QueryHelper.GetSourceById(item.getLocator());
			if(source==null) 
			{
				ElementMetadata metadata = null;
				if((metadata = ElementMetadata.getById(true, item.getLocator())) == null) continue;
				if(!Arrays.asList(metadata.getProperties().get("scopes").split(" ")).contains(scope)) 
				{
					shouldDelete = false;
					filteredSearchables.add(item);
				}
			}
			else if(!SourceHelper.getScopesOfSource(source).contains(scope))
			{
				shouldDelete = false;
				filteredSearchables.add(item);
			}
		}
		
		Set<Presentable> filteredPresentables = new HashSet<Presentable>();
		for(Presentable item : f.presentables)
		{
			IRRElement source = QueryHelper.GetSourceById(item.getLocator());
			if(source==null)
			{
				ElementMetadata metadata = null;
				if((metadata = ElementMetadata.getById(true, item.getLocator())) == null) continue;
				if(!Arrays.asList(metadata.getProperties().get("scopes").split(" ")).contains(scope))
				{
					shouldDelete = false;
					filteredPresentables.add(item);
				}
			}
			else if(!SourceHelper.getScopesOfSource(source).contains(scope))
			{
				shouldDelete = false;
				filteredPresentables.add(item);
			}
		}
		
		if(shouldDelete) 
		{
			logger.trace("Field " + this.getID() + 
					" was left without searchables and presentables after scoped deletion. Deleting field...");
			this.delete(deleteDetails, persistencyType);
		}
		else this.store(deleteDetails, scope);

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
			Field item=new Field();
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
				Set<String> sid=new HashSet<String>();
				Set<String> pid=new HashSet<String>();
				for(Searchable item : this.searchables) sid.add(item.getID());
				for(Presentable item : this.presentables) pid.add(item.getID());
				this.item.setSearchables(sid);
				this.item.setPresentables(pid);
				
				logger.trace("Field id=" + this.getID() + " name=" + this.getName() + " searchables=" + sid);
				logger.trace("Field id=" + this.getID() + " name=" + this.getName() + " presentables=" + pid);

	
				if(Field.behavior.isMarkUpdate())
				{
					ElementMetadata updateMetadata = ElementMetadata.getById(true, this.getID()+Type.FieldRecentUpdate);
					if(updateMetadata == null)
					{
						updateMetadata = new ElementMetadata();
						updateMetadata.setID(this.getID()+Type.FieldRecentUpdate);
						updateMetadata.setType(Type.FieldRecentUpdate);
						updateMetadata.getProperties().put("id", this.getID());
						updateMetadata.store(true, persistencyType);
					}
				}
				
				pm.currentTransaction().begin();
				this.item=pm.detachCopy(pm.makePersistent(this.item));
				pm.currentTransaction().commit();
				pm.flush();
			}finally
			{
				if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
				pm.close();
			}
			if(storeDetails)
			{
				for(Searchable item : this.searchables)
				{
					item.setField(this.getID());
					item.store(storeDetails,persistencyType);
				}
				for(Presentable item : this.presentables)
				{
					item.setField(this.getID());
					item.store(storeDetails,persistencyType);
				}
			}
		}
	}

	public void store(boolean storeDetails, String scope) throws ResourceRegistryException
	{
		if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_BEHIND)){
			logger.warn("Field store with write behind");
			this.store(storeDetails, scope, DatastoreType.LOCAL);
		} else if(ResourceRegistry.isWritePolicySupported(RRContext.WritePolicy.WRITE_THROUGH)){
			logger.warn("Field store with write through");
			this.store(storeDetails, scope, WritePolicy.WRITE_THROUGH);
		} else throw new ResourceRegistryException("Failed to find supported write policy");	
	}
	
	public void store(boolean storeDetails, String scope, WritePolicy policy) throws ResourceRegistryException
	{
		if(!ResourceRegistry.isWritePolicySupported(policy)) throw new ResourceRegistryException("Write policy not supported");
		if(policy == WritePolicy.WRITE_LOCAL || policy == WritePolicy.WRITE_BEHIND)
			this.store(storeDetails, scope, DatastoreType.LOCAL);
		else if(policy == WritePolicy.WRITE_THROUGH)
		{
			if(ResourceRegistry.getContext().isDatastoreSupportedForWrite(DatastoreType.REMOTE))
			{
				this.store(storeDetails, scope, DatastoreType.REMOTE);
				this.store(storeDetails, scope, DatastoreType.LOCAL);
			}
			else 
			{
				throw new ResourceRegistryException("This element does not support storing");
			}
		}
		else throw new ResourceRegistryException("Unsupported read policy");
	}
	
	public void store(boolean storeDetails, String scope, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		this.store(storeDetails, scope, persistencyType, false);
	}
	
	private void store(boolean storeDetails, String scope, RRContext.DatastoreType persistencyType, boolean writeThrough) throws ResourceRegistryException 
	{
		if(this.exists(persistencyType) && !writeThrough)
		{
			logger.warn("Field store and apply");
			Field item=new Field();
			item.setID(this.getID());
			item.load(storeDetails,persistencyType,true);
			item.apply(this, storeDetails, scope, persistencyType, true);
		}
		else {
			logger.warn("Field store not apply");
			this.store(storeDetails, persistencyType, true);
		}
	}
	
	protected void apply(IRRElement target, boolean applyDetails, RRContext.DatastoreType persistencyType, boolean doStore) throws ResourceRegistryException
	{
		if(!(target instanceof Field)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setDescription(((Field)target).item.getDescription());
		this.item.setName(((Field)target).item.getName());
		this.item.setPresentables(((Field)target).item.getPresentables());
		this.item.setSearchables(((Field)target).item.getSearchables());
		
		if(applyDetails)
		{
			Set<Searchable> toDelSearchable=new HashSet<Searchable>();
			Set<Searchable> toAddSearchable=new HashSet<Searchable>();
			for(Searchable item : this.searchables)
			{
				Searchable s=((Field)target).getSearchable(item.getID());
				if(s==null) toDelSearchable.add(item);
				else
				{
					item.apply(s, applyDetails,persistencyType, false);
				}
			}
			for(Searchable item : ((Field)target).searchables)
			{
				if(this.getSearchable(item.getID())==null) toAddSearchable.add(item);
			}
			for(Searchable item : toDelSearchable)
			{
				item.delete(true);
				this.searchables.remove(item);
			}
			for(Searchable item : toAddSearchable)
			{
				this.searchables.add(item);
			}
			Set<Presentable> toDelPresentable=new HashSet<Presentable>();
			Set<Presentable> toAddPresentable=new HashSet<Presentable>();
			for(Presentable item : this.presentables)
			{
				Presentable s=((Field)target).getPresentable(item.getID());
				if(s==null) toDelPresentable.add(item);
				else
				{
					item.apply(s, applyDetails,persistencyType,false);
				}
			}
			for(Presentable item : ((Field)target).presentables)
			{
				if(this.getPresentable(item.getID())==null) toAddPresentable.add(item);
			}
			for(Presentable item : toDelPresentable)
			{
				item.delete(true);
				this.presentables.remove(item);
			}
			for(Presentable item : toAddPresentable)
			{
				this.presentables.add(item);
			}
			logger.trace( "Field id=" + this.getID() + " name=" + this.getName() + " To add searchables=" + toAddSearchable.size());
			logger.trace( "Field id=" + this.getID() + " name=" + this.getName() + " To del searchables=" + toDelSearchable.size());
			logger.trace( "Field id=" + this.getID() + " name=" + this.getName() + " To add presentables=" + toAddPresentable.size());
			logger.trace( "Field id=" + this.getID() + " name=" + this.getName() + " To del presentables=" + toDelPresentable.size());
			
		}
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
	protected void apply(IRRElement target, boolean applyDetails, String scope, RRContext.DatastoreType persistencyType, boolean doStore) throws ResourceRegistryException 
	{
		if(!(target instanceof Field)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setDescription(((Field)target).item.getDescription());
		this.item.setName(((Field)target).item.getName());
		this.item.setPresentables(((Field)target).item.getPresentables());
		this.item.setSearchables(((Field)target).item.getSearchables());
		
		if(applyDetails)
		{
			Set<Searchable> toDelSearchable=new HashSet<Searchable>();
			Set<Searchable> toAddSearchable=new HashSet<Searchable>();
			for(Searchable item : this.searchables)
			{
				Searchable s=((Field)target).getSearchable(item.getID());
				
				if(s==null) 
				{	//If the updated item does not contain this searchable, and the provided scope matches one of the scopes
					//of the source of the absent item, delete the searchable
					IRRElement source = QueryHelper.GetSourceById(item.getLocator());
					if(source == null) 
						logger.warn( "No source with the provided id (" + item.getLocator() + ") exists");
					else if(SourceHelper.getScopesOfSource(source).contains(scope)) 
						toDelSearchable.add(item);
				}
				else
				{
					//If the updated item does contain this searchable, update it only if one if the scopes of its source matches the provided scope
					IRRElement source = QueryHelper.GetSourceById(s.getLocator());
					if(source == null)
						throw new ResourceRegistryException("No source with the provided id (" + s.getLocator() + ") exists");
					if(SourceHelper.getScopesOfSource(source).contains(scope)) item.apply(s, applyDetails,persistencyType, false);
				}
			}
			for(Searchable item : ((Field)target).searchables)
			{
				//Add new searchables only if the scopes of their sources match the provided scope
				if(this.getSearchable(item.getID())==null) 
				{
					
					IRRElement source = QueryHelper.GetSourceById(item.getLocator()); //TODO The source should already be stored
					if(source == null)
						throw new ResourceRegistryException("No source with the provided id (" + item.getLocator() + ") exists");
					if(SourceHelper.getScopesOfSource(source).contains(scope)) toAddSearchable.add(item);
				}
			}
			
			logger.warn("toDelSearchable : " + toDelSearchable);
			logger.warn("toAddSearchable : " + toAddSearchable);
			
			for(Searchable item : toDelSearchable)
			{
				item.delete(true);
				this.searchables.remove(item);
			}
			for(Searchable item : toAddSearchable)
			{
				this.searchables.add(item);
			}
			Set<Presentable> toDelPresentable=new HashSet<Presentable>();
			Set<Presentable> toAddPresentable=new HashSet<Presentable>();
			for(Presentable item : this.presentables)
			{
				Presentable s=((Field)target).getPresentable(item.getID());
				if(s==null) 
				{
					//If the updated item does not contain this presentable and the provided scope matches one of the 
					//scopes of the source of the absent item, delete the presentable
					IRRElement source = QueryHelper.GetSourceById(item.getLocator());
					if(source == null)
						logger.warn( "No source with the provided id (" + item.getLocator() + ") exists");
					else if(SourceHelper.getScopesOfSource(source).contains(scope)) 
						toDelPresentable.add(item);
				}
				else
				{
					//If the updated item does contain this presentable, update it only if one if the scopes of its source match the provided scope
					IRRElement source = QueryHelper.GetSourceById(s.getLocator());
					if(source == null)
						throw new ResourceRegistryException("No source with the provided id (" + s.getLocator() + ") exists");
					if(SourceHelper.getScopesOfSource(source).contains(scope)) item.apply(s, applyDetails,persistencyType,false);
				}
			}
			for(Presentable item : ((Field)target).presentables)
			{
				//Add new presentables only if the scopes of their sources match the provided scope
				
				if(this.getPresentable(item.getID())==null) 
				{
					IRRElement source = QueryHelper.GetSourceById(item.getLocator()); //TODO the source should already be stored
					if(source == null)
						throw new ResourceRegistryException("No source with the provided id (" + item.getLocator() + ") exists");
					if(SourceHelper.getScopesOfSource(source).contains(scope)) toAddPresentable.add(item);
				}
			}
			for(Presentable item : toDelPresentable)
			{
				item.delete(true);
				this.presentables.remove(item);
			}
			for(Presentable item : toAddPresentable)
			{
				this.presentables.add(item);
			}
		}
		if(doStore) this.store(applyDetails, scope, persistencyType, true);	
	}
	
	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException
	{
		if(!(target instanceof Field)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((Field)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((Field)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((Field)target).item.getID()!=null && !this.item.getID().equals(((Field)target).item.getID())) return false;
		if(this.item.getDescription() == null && ((Field)target).item.getDescription()!=null) return false;
		if(this.item.getDescription() != null && ((Field)target).item.getDescription()==null) return false;
		if(this.item.getDescription() != null && ((Field)target).item.getDescription()!=null && !this.item.getDescription().equals(((Field)target).item.getDescription())) return false;
		if(this.item.getName() == null && ((Field)target).item.getName()!=null) return false;
		if(this.item.getName() != null && ((Field)target).item.getName()==null) return false;
		if(this.item.getName() != null && ((Field)target).item.getName()!=null && !this.item.getName().equals(((Field)target).item.getName())) return false;
		if(this.item.getPresentables().size()!=((Field)target).getPresentables().size()) 
			if(this.getPresentables().size()!=((Field)target).getPresentables().size()) return false;
		if(!((Field)target).getPresentables().containsAll(this.item.getPresentables())) 
		{
			Set<String> thisPresentables = new HashSet<String>();
			Set<String> targetPresentables = new HashSet<String>();
			for(Presentable p : this.getPresentables())
				thisPresentables.add(p.getID());
			for(Presentable p : ((Field)target).getPresentables())
				targetPresentables.add(p.getID());
			if(!targetPresentables.containsAll(thisPresentables)) return false;
		}
		if(this.item.getSearchables().size()!=((Field)target).getSearchables().size()) return false;
		if(!((Field)target).getSearchables().containsAll(this.item.getSearchables()))
		{
			Set<String> thisSearchables = new HashSet<String>();
			Set<String> targetSearchables = new HashSet<String>();
			for(Searchable s : this.getSearchables())
				thisSearchables.add(s.getID());
			for(Searchable s : ((Field)target).getSearchables())
				targetSearchables.add(s.getID());
			if(!targetSearchables.containsAll(thisSearchables)) return false;
		}
		
		if(includeDetails)
		{
			if(this.searchables.size()!=((Field)target).searchables.size()) return false;
			for(Searchable item : ((Field)target).searchables)
			{
				Searchable tmp=this.getSearchable(item.getID());
				if(tmp==null) return false;
				if(!tmp.isEqual(item,includeDetails)) return false;
			}
	
			if(this.presentables.size()!=((Field)target).presentables.size()) return false;
			for(Presentable item : ((Field)target).presentables)
			{
				Presentable tmp=this.getPresentable(item.getID());
				if(tmp==null) return false;
				if(!tmp.isEqual(item,includeDetails)) return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof Field)) return false;
		return this.item.getID().equals(((Field)other).getID());
	}
	
	public Searchable getSearchable(String id)
	{
		for(Searchable item : this.searchables) if(item.getID().equals(id)) return item;
		return null;
	}
	
	public Presentable getPresentable(String id)
	{
		for(Presentable item : this.presentables) if(item.getID().equals(id)) return item;
		return null;
	}

	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append(this.item.deepToString());
		for(Searchable item : this.searchables) buf.append(item.deepToString());
		for(Presentable item : this.presentables) buf.append(item.deepToString());
		return buf.toString();
	}

	@Override
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm=null;
		Query query=null;
		try 
		{
			if(persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
			{
				sharedLock.lock(); locked = true;
				if(InMemoryStore.hasItem(this.getClass(), this.getID()))
					return true;
				sharedLock.unlock(); locked = false;
			}
			pm=ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(FieldDao.class, "exists");
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
	public static List<Field> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm=null;
		Query query=null;
		List<Field> col=new ArrayList<Field>();
		boolean memLoaded = false;
		try
		{
			if(persistencyType.equals(DatastoreType.LOCAL) && ResourceRegistry.getContext().isTargetInMemory(Field.class.getName()))
			{
				sharedLock.lock(); locked=true;
				Set<IRRElement> retrieved = InMemoryStore.getItems(Field.class);
				sharedLock.unlock(); locked=false;
				if(retrieved != null)
				{
					for(IRRElement item : retrieved)
						col.add((Field)item);
					memLoaded = true;
				}
			}
			if(memLoaded == false)
			{
				pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
				query=pm.newNamedQuery(FieldDao.class, "allFields");
				query.compile();
				sharedLock.lock(); locked = true;
				Collection<String> res=(Collection<String>)query.execute();
				sharedLock.unlock(); locked = false;
				for(String item : res)
				{
					Field f=new Field();
					f.setID(item);
					f.load(loadDetails, persistencyType);
					col.add(f);
				}
			}else
			{
				if(loadDetails)
				{
					for(Field f : col)
					{
						for(Searchable s : f.getSearchables())
							s.load(true);
						for(Presentable p : f.getPresentables())
							p.load(true);
					}
				}
			}
		}
		finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
		return col;
	}
	
	public static List<Field> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return Field.getAll(loadDetails, DatastoreType.LOCAL);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Field> getAll(boolean loadDetails, String scope) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		List<Field> col = new ArrayList<Field>();
		PersistenceManager pm = null;
		Query query = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(FieldDao.class, "allFields");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute();
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				Field f=new Field();
				f.setID(item);
				f.load(loadDetails, scope);
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
	
	public static Field getById(boolean loadDetails, String id) throws ResourceRegistryException
	{
		Field f = new Field();
		f.setID(id);
		return f.load(loadDetails) == true ? f : null;
	}
	
	@SuppressWarnings("unchecked")
	public static String getFieldNameById(String id) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(FieldDao.class, "fieldName");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("id", id);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			if(res.size() == 0)
				return null;
			return res.iterator().next();
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Field> getFieldsWithName(boolean loadDetails, String name) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<Field> col=new ArrayList<Field>();
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(FieldDao.class, "fieldsWithName");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("name", name);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				Field f=new Field();
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
	
	@SuppressWarnings("unchecked")
	public static List<Field> getSearchableFieldsOfCollection(boolean loadDetails, String collection) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<Field> col=new ArrayList<Field>();
		try 
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(FieldDao.class, "searchableFieldsOfCollection");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("collection", collection);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				Field f=new Field();
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
	
	@SuppressWarnings("unchecked")
	public static List<Field> getSearchableFieldsOfCollections(boolean loadDetails, boolean filter, Set<String> collections) throws ResourceRegistryException
	{
		PersistenceManager pm = null;
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		Query query = null;
		List<Field> col=new ArrayList<Field>();
		Set<IDaoElement> allDatasources = null;
		if(loadDetails && filter)
		{
			try
			{
				allDatasources = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
			}catch(Exception e)
			{
				throw new ResourceRegistryException("Could not load data source info", e);
			}
		}
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			Set<String> fieldIDs=Searchable.getFieldsOfCollections(collections);
			query=pm.newNamedQuery(FieldDao.class, "fields");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute(fieldIDs);
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				Field f=new Field();
				f.setID(item);
				f.load(loadDetails);
				if (loadDetails && filter)
				{
					Set<Searchable> filterOut=new HashSet<Searchable>();
					for(Searchable s : f.searchables)
					{
						if(!collections.contains(s.getCollection())) filterOut.add(s);
						DataSourceDao ds = null;
						for(IDaoElement d : allDatasources)
						{
							if(((DataSourceDao)d).getID().equals(s.getLocator()))
							{
								ds = (DataSourceDao)d;
								break;
							}
						}
						if(ds == null) filterOut.add(s);
					}
					f.searchables.removeAll(filterOut);
				}
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
	
	public static List<Field> getBrowsableFieldsOfCollection(boolean loadDetails, String collection) throws ResourceRegistryException
	{
		List<Field> filteredCol = new ArrayList<Field>();
		List<Field> col=getSearchableFieldsOfCollection(loadDetails, collection);
		
		logger.debug("getting browsable fields of collection : " + collection);
		
		Set<IDaoElement> allDatasources = null;
		try
		{
			allDatasources = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
			
		}catch(Exception e)
		{
			logger.warn( "Could not load data source info", e);
			throw new ResourceRegistryException("Could not load data source info", e);
		}
		for(Field f: col)
		{
			logger.debug( "checking if field : " + f.getID() + " : " + f.getName());
			
			for(Searchable s: f.getSearchables())
			{
				logger.debug( "s  : " + s.getID() + " : " + s.getField() + " is searchable of field : " + f.getID() + " : " + f.getName() + " and belongs to collection : " + s.getCollection());
				
				
				if(s.getCollection().equals(collection))
				{
					DataSourceDao ds = null;
					for(IDaoElement d : allDatasources)
					{
						if(((DataSourceDao)d).getID().equals(s.getLocator())) 
						{
							ds = (DataSourceDao)d;
							break;
						}
					}
					
					if (ds != null) {
						logger.debug( "found datasource for searchable  : " + s.getID() + " : " + s.getField() + " with type : " + ds.getType());
					} else
					{
						logger.warn( "Could not find datasource " + s.getLocator() + " of searchable " + s.getID());
						continue;
					}
					if(ds.getType().equals(DataSource.Type.FullTextIndex.toString()))
					{
						filteredCol.add(f);
						break;
					}
				}
			}
		}
		
		String foundFields = new String();
		for (Field f : filteredCol){
			String val = f.getID() + " : " + f.getName();
			foundFields += val + ", ";
		}
		logger.info("browsable fields of collection : " + collection + " : " + foundFields);
		
		return filteredCol;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Field> getBrowsableFieldsOfCollections(boolean loadDetails, boolean filter, Set<String> collections) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		List<Field> col=new ArrayList<Field>();
		PersistenceManager pm = null;
		Query query = null;
		Set<IDaoElement> allDatasources = null;
		try
		{
			allDatasources = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not load data source info", e);
		}
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			Set<String> fieldIDs=Searchable.getFieldsOfCollections(collections);
			query=pm.newNamedQuery(FieldDao.class, "fields");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute(fieldIDs);
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				Field f=new Field();
				f.setID(item);
				f.load(loadDetails);
				boolean omit = false;
				for(Searchable s: f.getSearchables())
				{
					DataSourceDao ds = null;
					for(IDaoElement d : allDatasources)
					{
						if(((DataSourceDao)d).getID().equals(s.getLocator()))
						{
							ds = (DataSourceDao)d;
							break;
						}
					}
					if(ds == null) continue;
//					if(!ds.getType().equals(DataSource.Type.ForwardIndex.toString()))
//					{
//						omit = true;
//						break;
//					}
				}
				if(omit) continue;
				
				if (loadDetails && filter)
				{
					Set<Searchable> filterOut=new HashSet<Searchable>();
					for(Searchable s : f.searchables)
					{
						if(!collections.contains(s.getCollection())) filterOut.add(s);
					}
					f.searchables.removeAll(filterOut);
				}
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
	
	@SuppressWarnings("unchecked")
	public static List<Field> getPresentableFieldsOfCollection(boolean loadDetails, String collection) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<Field> col=new ArrayList<Field>();
		Set<IDaoElement> allDatasources = null;
		try
		{
			allDatasources = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not load data source info", e);
		}
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(FieldDao.class, "presentableFieldsOfCollection");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("collection", collection);
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.executeWithMap(args);
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				Field f=new Field();
				f.setID(item);
				f.load(loadDetails);
				Set<Presentable> filterOut=new HashSet<Presentable>();
				for(Presentable s : f.presentables) {
					DataSourceDao ds = null;
					for(IDaoElement d : allDatasources)
					{
						if(((DataSourceDao)d).getID().equals(s.getLocator()))
						{
							ds = (DataSourceDao)d;
						}
					}
					if(ds == null) filterOut.add(s);
				}
				f.presentables.removeAll(filterOut);
				if(!f.presentables.isEmpty())
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
	
	@SuppressWarnings("unchecked")
	public static List<Field> getPresentableFieldsOfCollections(boolean loadDetails, boolean filter, Set<String> collections) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<Field> col=new ArrayList<Field>();
		Set<IDaoElement> allDatasources = null;
		if(loadDetails && filter)
		{
			try
			{
				allDatasources = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
			}catch(Exception e)
			{
				throw new ResourceRegistryException("Could not load data source info", e);
			}
		}
		try 
		{
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			Set<String> fieldIDs=Searchable.getFieldsOfCollections(collections);
			query=pm.newNamedQuery(FieldDao.class, "fields");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute(fieldIDs);
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				Field f=new Field();
				f.setID(item);
				f.load(loadDetails);
				if (loadDetails && filter)
				{
					Set<Presentable> filterOut=new HashSet<Presentable>();
					for(Presentable s : f.presentables)
					{
						if(!collections.contains(s.getCollection())) filterOut.add(s);
						DataSourceDao ds = null;
						for(IDaoElement d : allDatasources)
						{
							if(((DataSourceDao)d).getID().equals(s.getLocator()))
							{
								ds = (DataSourceDao)d;
								break;
							}
						}
						if(ds == null) filterOut.add(s);
					}
					f.presentables.removeAll(filterOut);
				}
				if(!f.presentables.isEmpty())
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
	
	public static List<Field> getSearchableFieldsOfCollectionByCapabilities(boolean loadDetails, String collection, Set<String> capabilities) throws ResourceRegistryException 
	{
		List<Field> allSearchableFields = getSearchableFieldsOfCollection(loadDetails, collection);
		Set<IDaoElement> allDatasources = null;
		try
		{
			allDatasources = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not load data source info", e);
		}
		List<Field> filteredSearchableFields = new ArrayList<Field>();
		for(Field f : allSearchableFields) {
			boolean found = false;
			for(Searchable s : f.getSearchables()) {
				if(s.getCollection().equals(collection)) {
					DataSourceDao ds = null;
					for(IDaoElement d : allDatasources)
					{
						if(((DataSourceDao)d).getID().equals(s.getLocator()))
						{
							ds = (DataSourceDao)d;
							break;
						}
					}
					if(ds==null) continue;
					for(String capability : capabilities) {
						if(ds.getCapabilities().contains(capability)) {
							filteredSearchableFields.add(f);
							found = true;
							break;
						}
					}
				}
				if(found == true) break;
			}
		}
		return filteredSearchableFields;
	}
}
