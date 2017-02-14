package gr.uoa.di.madgik.rr.element.execution;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.infra.HostingNode;

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

public class ExecutionService extends RRElement
{
	private static final Logger logger = LoggerFactory
			.getLogger(ExecutionService.class);
	
	
	private ExecutionServiceDao item = new ExecutionServiceDao();
	private RRContext context = null;
	private HostingNode hostingNode = null;
	
	public RRContext getISContext()
	{
		return this.context;
	}

	public ExecutionService() throws ResourceRegistryException
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

	public Set<String> getScopes()
	{
		return this.item.getScopes();
	}
	
	public void setScopes(Set<String> scopes)
	{
		this.item.setScopes(scopes);
	}
	
	public String getEndpoint()
	{
		return this.item.getEndpoint();
	}
	
	public void setEndpoint(String endpoint)
	{
		this.item.setEndpoint(endpoint);
	}
	
	public HostingNode getHostingNode()
	{
		return this.hostingNode;
	}
	
	@Override
	public ExecutionServiceDao getItem()
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
		if(!(target instanceof ExecutionService)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((ExecutionService)target).item.getID());
		this.item.setFunctionality(((ExecutionService)target).item.getFunctionality());
		this.item.setHostingNode(((ExecutionService)target).item.getHostingNode());
		this.item.setEndpoint(((ExecutionService)target).item.getEndpoint());
		this.item.setScopes(((ExecutionService)target).item.getScopes());
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
					ExecutionService retrieved = (ExecutionService)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, persistencyType, true, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(ExecutionServiceDao.class, this.item.getID()));
			pm.currentTransaction().commit();
			sharedLock.unlock(); locked = false;
			
			HostingNode hn=new HostingNode();
			hn.setID(this.item.getHostingNode());
			if(loadDetails)
			{
				if(hn.exists(persistencyType)) hn.load(loadDetails,persistencyType,overridePrefetched);
				else logger.warn("Hosting node " + hn.getID() + " does not exist!");
			}
			this.hostingNode = hn;
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
		if(deleteDetails) throw new ResourceRegistryException("Unsupported operation");
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		ExecutionService f=new ExecutionService();
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
			ExecutionService item=new ExecutionService();
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
				
				if(storeDetails) this.getHostingNode().store(storeDetails, persistencyType);
				
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
		if(!(target instanceof ExecutionService)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((ExecutionService)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((ExecutionService)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((ExecutionService)target).item.getID()!=null && !this.item.getID().equals(((ExecutionService)target).item.getID())) return false;
		if(this.item.getFunctionality() == null && ((ExecutionService)target).item.getFunctionality()!=null) return false;
		if(this.item.getFunctionality() != null && ((ExecutionService)target).item.getFunctionality()==null) return false;
		if(this.item.getFunctionality() != null && ((ExecutionService)target).item.getFunctionality()!=null && !this.item.getFunctionality().equals(((ExecutionService)target).item.getFunctionality())) return false;
		if(this.item.getEndpoint() == null && ((ExecutionService)target).item.getEndpoint()!=null) return false;
		if(this.item.getEndpoint() != null && ((ExecutionService)target).item.getEndpoint()==null) return false;
		if(this.item.getEndpoint() != null && ((ExecutionService)target).item.getEndpoint()!=null && !this.item.getEndpoint().equals(((ExecutionService)target).item.getEndpoint())) return false;
		if(this.item.getHostingNode() == null && ((ExecutionService)target).item.getHostingNode()!=null) return false;
		if(this.item.getHostingNode() != null && ((ExecutionService)target).item.getHostingNode()==null) return false;
		if(this.item.getHostingNode() != null && ((ExecutionService)target).item.getHostingNode()!=null && !this.item.getID().equals(((ExecutionService)target).item.getHostingNode())) return false;
		if(this.item.getScopes().size()!=((ExecutionService)target).item.getScopes().size()) return false;
		if(!((ExecutionService)target).item.getScopes().containsAll(this.item.getScopes())) return false;
		return true;
	}

	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append(this.item.deepToString());
		buf.append(this.hostingNode.deepToString());
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
			query=pm.newNamedQuery(ExecutionServiceDao.class, "exists");
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
	public static List<ExecutionService> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<ExecutionService> col=new ArrayList<ExecutionService>();
		try {
			if(ResourceRegistry.getContext().isTargetInMemory(ExecutionService.class.getName()))
			{
				sharedLock.lock(); locked=true;
				Set<IRRElement> retrieved = InMemoryStore.getItems(ExecutionService.class);
				sharedLock.unlock(); locked=false;
				if(retrieved != null)
				{
					for(IRRElement item : retrieved)
						col.add((ExecutionService)item);
					return col;
				}
			}
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(ExecutionServiceDao.class, "all");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute();
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				ExecutionService f=new ExecutionService();
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
}
