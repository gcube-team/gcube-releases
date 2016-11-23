package gr.uoa.di.madgik.rr.element.execution;

import gr.uoa.di.madgik.commons.infra.HostingNodeAdapter;
import gr.uoa.di.madgik.commons.infra.nodefilter.ConstraintType;
import gr.uoa.di.madgik.commons.infra.nodefilter.Facet;
import gr.uoa.di.madgik.commons.infra.nodefilter.NodeFilter;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.random.RandomNodeSelector;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionServer extends RRElement
{
	private static final Logger logger = LoggerFactory
			.getLogger(ExecutionServer.class);
	
	private ExecutionServerDao item = new ExecutionServerDao();
	private RRContext context = null;
	private HostingNode hostingNode = null;
	
	public RRContext getISContext()
	{
		return this.context;
	}

	public ExecutionServer() throws ResourceRegistryException
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
	
	public String getPort()
	{
		return this.item.getPort();
	}
	
	public void setPort(String port)
	{
		this.item.setPort(port);
	}
	
	public HostingNode getHostingNode()
	{
		return this.hostingNode;
	}
	
	@Override
	public ExecutionServerDao getItem()
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
		if(!(target instanceof ExecutionServer)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((ExecutionServer)target).item.getID());
		this.item.setFunctionality(((ExecutionServer)target).item.getFunctionality());
		this.item.setHostingNode(((ExecutionServer)target).item.getHostingNode());
		this.item.setHostname(((ExecutionServer)target).item.getHostname());
		this.item.setPort(((ExecutionServer)target).item.getPort());
		this.item.setScopes(((ExecutionServer)target).item.getScopes());
		if(doStore) this.store(applyDetails, persistencyType, true);
	}

	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType, boolean overridePrefetched) throws ResourceRegistryException
	{
		logger.info("loading ExecutionServer : " + loadDetails);
		
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
					ExecutionServer retrieved = (ExecutionServer)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, persistencyType, true, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(ExecutionServerDao.class, this.item.getID()));
			pm.currentTransaction().commit();
			sharedLock.unlock(); locked = false;
			
			HostingNode hn=new HostingNode();
			hn.setID(this.item.getHostingNode());
			if(loadDetails)
			{
				if(hn.exists(persistencyType)) hn.load(loadDetails,persistencyType, overridePrefetched);
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
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return this.load(loadDetails, persistencyType, false);
	}
	
	@Override
	public void delete(boolean deleteDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		if(deleteDetails) throw new ResourceRegistryException("Unsupported operation");
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		ExecutionServer f=new ExecutionServer();
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
			ExecutionServer item=new ExecutionServer();
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
		if(!(target instanceof ExecutionServer)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((ExecutionServer)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((ExecutionServer)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((ExecutionServer)target).item.getID()!=null && !this.item.getID().equals(((ExecutionServer)target).item.getID())) return false;
		if(this.item.getFunctionality() == null && ((ExecutionServer)target).item.getFunctionality()!=null) return false;
		if(this.item.getFunctionality() != null && ((ExecutionServer)target).item.getFunctionality()==null) return false;
		if(this.item.getFunctionality() != null && ((ExecutionServer)target).item.getFunctionality()!=null && !this.item.getFunctionality().equals(((ExecutionServer)target).item.getFunctionality())) return false;
		if(this.item.getHostname() == null && ((ExecutionServer)target).item.getHostname()!=null) return false;
		if(this.item.getHostname() != null && ((ExecutionServer)target).item.getHostname()==null) return false;
		if(this.item.getHostname() != null && ((ExecutionServer)target).item.getHostname()!=null && !this.item.getHostname().equals(((ExecutionServer)target).item.getHostname())) return false;
		if(this.item.getPort() == null && ((ExecutionServer)target).item.getPort()!=null) return false;
		if(this.item.getPort() != null && ((ExecutionServer)target).item.getPort()==null) return false;
		if(this.item.getPort() != null && ((ExecutionServer)target).item.getPort()!=null && !this.item.getPort().equals(((ExecutionServer)target).item.getPort())) return false;
		if(this.item.getHostingNode() == null && ((ExecutionServer)target).item.getHostingNode()!=null) return false;
		if(this.item.getHostingNode() != null && ((ExecutionServer)target).item.getHostingNode()==null) return false;
		if(this.item.getHostingNode() != null && ((ExecutionServer)target).item.getHostingNode()!=null && !this.item.getID().equals(((ExecutionServer)target).item.getHostingNode())) return false;
		if(this.item.getScopes().size()!=((ExecutionServer)target).item.getScopes().size()) return false;
		if(!((ExecutionServer)target).item.getScopes().containsAll(this.item.getScopes())) return false;
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
			query=pm.newNamedQuery(ExecutionServerDao.class, "exists");
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
	public static List<ExecutionServer> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<ExecutionServer> col=new ArrayList<ExecutionServer>();
		try {
			if(ResourceRegistry.getContext().isTargetInMemory(ExecutionServer.class.getName()))
			{
				sharedLock.lock(); locked=true;
				Set<IRRElement> retrieved = InMemoryStore.getItems(ExecutionServer.class);
				sharedLock.unlock(); locked=false;
				if(retrieved != null)
				{
					for(IRRElement item : retrieved)
						col.add((ExecutionServer)item);
					return col;
				}
			}
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(ExecutionServerDao.class, "all");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute();
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				ExecutionServer f=new ExecutionServer();
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
	
	public static List<ExecutionServer> getMatchingNodes(boolean loadDetails, String requirementExpression, String rankingExpression) throws ResourceRegistryException
	{
		List<ExecutionServer> all = ExecutionServer.getAll(loadDetails);
		List<ExecutionServer> pickedNodes = new ArrayList<ExecutionServer>();
		MatchParser parser=new MatchParser(requirementExpression);
		for(ExecutionServer nfo : all)
		{
			boolean match=true;
			for(Map.Entry<String, String> entry : parser.requirments.entrySet())
			{
				if(entry.getKey()==null) break;
				if(entry.getValue()==null) break;
				String value=nfo.getHostingNode().getPairs().get(entry.getKey());
				if((value==null) || (!value.trim().equalsIgnoreCase(entry.getValue().trim())))
				{
					match=false;
					break;
				}
			}
			if(match) pickedNodes.add(nfo);
		}
		return pickedNodes;
	}
	
	public static List<ExecutionServer> getMatchingNodes(boolean loadDetails, String requirementExpression, String rankingExpression, NodeSelector selector) throws ResourceRegistryException
	{
		List<ExecutionServer> all = ExecutionServer.getAll(loadDetails);
		List<ExecutionServer> pickedNodes = new ArrayList<ExecutionServer>();
		MatchParser parser=new MatchParser(requirementExpression);
		for(ExecutionServer nfo : all)
		{
			boolean match=true;
			for(Map.Entry<String, String> entry : parser.requirments.entrySet())
			{
				if(entry.getKey()==null) break;
				if(entry.getValue()==null) break;
				String value=nfo.getHostingNode().getPairs().get(entry.getKey());
				if((value==null) || (!value.trim().equalsIgnoreCase(entry.getValue().trim())))
				{
					match=false;
					break;
				}
			}
			if(match) pickedNodes.add(nfo);
		}
		return pickedNodes;
	}
	
	public static ExecutionServer getMatchingNode(boolean loadDetails, String requirementExpression, String rankingExpression, NodeSelector selector) throws ResourceRegistryException
	{
		try
		{
			List<ExecutionServer> matching = getMatchingNodes(loadDetails, requirementExpression, rankingExpression);
			gr.uoa.di.madgik.commons.infra.HostingNode selected = selector.selectNode(new RRExecutionServer2HnAdapter().adaptAll(matching));
			for(ExecutionServer es : matching)
			{
				if(es.getID().equals(selected.getId())) return es;
			}
			return null;
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not find matching node", e);
		}
	}
	
	public static ExecutionServer getMatchingNode(boolean loadDetails, String requirementExpression, String rankingExpression) throws ResourceRegistryException
	{
		return getMatchingNode(loadDetails, requirementExpression, rankingExpression, new RandomNodeSelector());
	}
	
	/**
	 * Filters a list of Execution Servers, according to the Facet applied and
	 * the type of the constraint.
	 * @param facet - The facet to apply to each node.
	 * @param type - The type of the constraint.
	 * @param executionServers - List of Execution Servers.
	 * @return - The list of all Execution Servers that match the necessary criteria.
	 * @throws Exception
	 */
	public static Set<ExecutionServer> filterNodes(Facet facet, ConstraintType type, List<ExecutionServer> executionServers) throws Exception {
		Set<ExecutionServer> filteredServers = new HashSet<ExecutionServer> ();
		HostingNodeAdapter adapter = new RRExecutionServer2HnAdapter();
		
		for(ExecutionServer exServer: executionServers) {
			gr.uoa.di.madgik.commons.infra.HostingNode hostingNode = adapter.adapt(exServer);
			boolean flag = NodeFilter.filterNode(facet, type, hostingNode);
			if(flag)
				filteredServers.add(exServer);
		}
		
		return filteredServers;
	}
}
