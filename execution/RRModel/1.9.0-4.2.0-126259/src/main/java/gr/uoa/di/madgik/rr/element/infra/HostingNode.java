package gr.uoa.di.madgik.rr.element.infra;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.execution.ExecutionService;

public class HostingNode extends RRElement
{
	
	public static final String KeyValueDelimiter="##delim##";
	
	public static String HostnameProperty="hostname";
	public static String HostnameFullProperty="hn.hostname";
	public static String PortProperty="hn.port";
	public static String LoadOneHourProperty="hn.load.one_hour";
	public static String LoadOneWeekProperty="hn.load.one_week";
	public static String LoadFiveMinutesProperty="hn.load.five_min";
	public static String LoadFifteenMinutesProperty="hn.load.fifteen_min";
	public static String DiskSizeProperty="hn.disk.size";
	public static String PhysicalMemorySizeProperty="hn.memory.physical.size";
	public static String PhysicalMemoryAvailableProperty="hn.memory.physical.available";
	public static String VirtualMemorySizeProperty="hn.memory.virtual.size";
	public static String VirtualMemoryAvailableProperty="hn.memory.virtual.available";
	public static String ProcessorCountProperty="hn.processor.count";
	public static String ProcessorTotalBogomipsProperty="hn.processor.total_bogomips";
	public static String ProcessorTotalClockSpeedProperty="hn.processor.total_clockspeed";
	
	private HostingNodeDao item=new HostingNodeDao();
	private RRContext context=null;
	private Map<String, String> pairs=null;
	
	private boolean isLocal = false;
	
	private static final Logger logger = LoggerFactory
			.getLogger(HostingNode.class);
	
	
	
	@Override
	public RRContext getISContext()
	{
		return this.context;
	}

	public HostingNode(/*String id*/) throws ResourceRegistryException
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
	
	public Map<String,String> getPairs()
	{
		if(this.pairs==null) this.buildPairs();
		return this.pairs;
	}
	
	public boolean isLocal()
	{
		return this.isLocal;
	}
	
	private void buildPairs()
	{
		this.pairs=new HashMap<String, String>();
		for(String k : this.item.getPairKeys())
		{
			if(this.pairs.containsKey(k)) continue;
			for(String v:this.item.getPairValues())
			{
				String[] keyVal = v.split(KeyValueDelimiter);
				if(keyVal.length != 2) continue;
				if(keyVal[0].trim().equals(k))
				{
					this.pairs.put(k, keyVal[1]);
					break;
				}
			}
		}
	}
	
	@Override
	public HostingNodeDao getItem()
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
		if(!(target instanceof HostingNode)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((HostingNode)target).item.getID());
		this.item.getPairKeys().clear();
		this.item.getPairValues().clear();
		for(Map.Entry<String, String> p : ((HostingNode)target).getPairs().entrySet())
		{
			this.item.getPairKeys().add(p.getKey());
			this.item.getPairValues().add(p.getKey()+HostingNode.KeyValueDelimiter+p.getValue());
		}
		this.item.setScopes(((HostingNode)target).item.getScopes());
		this.isLocal = ((HostingNode)target).isLocal();
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
	@Override
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return this.load(loadDetails, persistencyType, false);
	}
	
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType, boolean overridePrefetched) throws ResourceRegistryException
	{
		logger.info("loading HostingNode");
		
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
					HostingNode retrieved = (HostingNode)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, persistencyType, true, false);
					return true;
				}
				sharedLock.unlock(); locked = false;
			}
			pm = this.getISContext().getManagerForRead(persistencyType);
			sharedLock.lock(); locked = true;
			pm.currentTransaction().begin();
			this.item=pm.detachCopy(pm.getObjectById(HostingNodeDao.class, this.item.getID()));
			pm.currentTransaction().commit();
			sharedLock.unlock(); locked = false;
			if(this.getPairs().containsKey(HostingNode.HostnameProperty) && this.getPairs().containsKey(HostingNode.PortProperty))
			{
				try
				{
					logger.info("checking if local. comparing : " + this.getPairs().get(HostingNode.HostnameProperty));
					logger.info("                          to : " + ResourceRegistry.getContext().getLocalNodeHostname());
					
					logger.info("checking if local. comparing : " + this.getPairs().get(HostingNode.PortProperty));
					logger.info("                          to : " + ResourceRegistry.getContext().getLocalNodePort());
					
					if(this.getPairs().get(HostingNode.HostnameProperty).equals(ResourceRegistry.getContext().getLocalNodeHostname()) 
							&& this.getPairs().get(HostingNode.PortProperty).equals(ResourceRegistry.getContext().getLocalNodePort())) 
						this.isLocal = true;
				}catch(ResourceRegistryException e)
				{
					logger.warn("Could not resolve host name and port of local node. Entity will not include such information");
				}
			}
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
		HostingNode n=new HostingNode();
		n.setID(this.getID());
		n.load(deleteDetails,persistencyType,true);
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			pm.currentTransaction().begin();
			pm.deletePersistent(n.item);
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
			HostingNode item=new HostingNode();
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
		if(!(target instanceof HostingNode)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((HostingNode)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((HostingNode)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((HostingNode)target).item.getID()!=null && !this.item.getID().equals(((HostingNode)target).item.getID())) return false;
		if(this.item.getPairKeys().size()!=((HostingNode)target).item.getPairKeys().size()) return false;
		if(!((HostingNode)target).item.getPairKeys().containsAll(this.item.getPairKeys())) return false;
		if(this.item.getPairValues().size()!=((HostingNode)target).item.getPairValues().size()) return false;
		if(!((HostingNode)target).item.getPairValues().containsAll(this.item.getPairValues())) return false;
		if(this.item.getScopes().size()!=((HostingNode)target).item.getScopes().size()) return false;
		if(!((HostingNode)target).item.getScopes().containsAll(this.item.getScopes())) return false;
		return true;
	}

	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("Node ID "+this.item.getID());
		for(Map.Entry<String, String> e : this.getPairs().entrySet())
			buf.append("Node Pair " + e.getKey() + " = " + e.getValue()+"\n");
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
			query=pm.newNamedQuery(HostingNodeDao.class, "exists");
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
	public static List<HostingNode> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		List<HostingNode> col=new ArrayList<HostingNode>();
		try {
			if(persistencyType.equals(DatastoreType.LOCAL) && ResourceRegistry.getContext().isTargetInMemory(HostingNode.class.getName()))
			{
				sharedLock.lock(); locked=true;
				Set<IRRElement> retrieved = InMemoryStore.getItems(HostingNode.class);
				sharedLock.unlock(); locked=false;
				if(retrieved != null)
				{
					for(IRRElement item : retrieved)
						col.add((HostingNode)item);
					return col;
				}
			}
			pm = ResourceRegistry.getContext().getManagerForRead(RRContext.DatastoreType.LOCAL);
			query=pm.newNamedQuery(HostingNodeDao.class, "all");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute();
			sharedLock.unlock(); locked = false;
			for(String item : res)
			{
				HostingNode f=new HostingNode();
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
	
	public static List<HostingNode> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return HostingNode.getAll(loadDetails, DatastoreType.LOCAL);
	}
}
