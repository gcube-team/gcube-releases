package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.execution.ExecutionService;
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

public abstract class DataSourceService extends RRElement
{
	private static final Logger logger = LoggerFactory
			.getLogger(DataSourceService.class);
	
	public enum Type {
		FullTextIndex,
		SruConsumer,
//		ForwardIndex,
//		GeoIndex,
		OpenSearch;
		
		public boolean isExternal() {
			if(this.equals(OpenSearch))
				return true;
			return false;
		}
	}
	
	private static Map<Class<? extends DataSourceService>, Class<? extends DataSourceServiceDao>> SubTypes = 
		new HashMap<Class<? extends DataSourceService>, Class<? extends DataSourceServiceDao>>();
	
	private static Map<Class<? extends DataSourceServiceDao>, Class<? extends DataSourceService>> ReverseSubTypes = 
		new HashMap<Class<? extends DataSourceServiceDao>, Class<? extends DataSourceService>>();
	
	private static Map<Type, Class<? extends DataSourceService>> SubTypesMap = 
		new HashMap<Type, Class<? extends DataSourceService>>();
	
	public static void clearSubTypes()
	{
		DataSourceService.SubTypesMap.clear();
		DataSourceService.SubTypes.clear();
		DataSourceService.ReverseSubTypes.clear();
	}
	
	public static void addSubType(Type type, Class<? extends DataSourceService> service, Class<? extends DataSourceServiceDao> serviceDao)
	{
		DataSourceService.SubTypesMap.put(type, service);
		DataSourceService.SubTypes.put(service, serviceDao);
		DataSourceService.ReverseSubTypes.put(serviceDao, service);
	}
	
	public static Set<Class<? extends DataSourceService>> subTypes()
	{
		return DataSourceService.SubTypes.keySet();
	}
	
	public static Set<Class<? extends DataSourceServiceDao>> subTypeItems() 
	{
		return new HashSet<Class<? extends DataSourceServiceDao>>(DataSourceService.SubTypes.values());
	}
	
	
	protected DataSourceServiceDao item=null;
	protected Set<DataSource> datasources=null;
	protected HostingNode hostingNode=null;
	protected RRContext context=null;
	
	private Class<? extends DataSourceServiceDao> daoType = null;
	
	@Override
	public RRContext getISContext()
	{
		return this.context;
	}

	protected DataSourceService(Class<? extends DataSourceServiceDao> type) throws ResourceRegistryException
	{
		try {
			this.item=type.newInstance();
			this.daoType=type;
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Unknown dao type: " + type.getName(), e);
		}
		this.item.setID(UUID.randomUUID().toString());
		this.context=ResourceRegistry.getContext();
	}
	
//	public DataSource() throws ResourceRegistryException
//	{
//		this.item=new DataSourceDao();
//		this.item.setID(UUID.randomUUID().toString());
//		this.context=ResourceRegistry.getContext();
//		this.daoType = DataSourceDao.class;
//	}

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
	
	public Type getType()
	{
		return Type.valueOf(item.getType());
	}
	
	protected void setType(Type type)
	{
		this.item.setType(type.toString());
	}

	public Set<String> getScopes()
	{
		return item.getScopes();
	}

	public String getHostingNode()
	{
		return item.getHostingNode();
	}

	public void setHostingNode(String hostingNode)
	{
		this.item.setHostingNode(hostingNode);
	}
	
	public String getEndpoint()
	{
		return this.item.getEndpoint();
	}
	
	public void setEndpoint(String endpoint)
	{
		this.item.setEndpoint(endpoint);
	}
	
	public Set<DataSource> getDataSources()
	{
		return this.datasources;
	}
	
	public String getFunctionality()
	{
		return this.item.getFunctionality();
	}

	public void setFunctionality(String functionality)
	{
		this.item.setFunctionality(functionality);
	}
	
	@Override
	public DataSourceServiceDao getItem()
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
		if(!(target instanceof DataSourceService)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((DataSourceService)target).item.getID());
		this.item.setEndpoint(((DataSourceService)target).item.getEndpoint());
		this.item.setHostingNode(((DataSourceService)target).item.getHostingNode());
		this.item.setType(((DataSourceService)target).item.getType());
		this.item.setFunctionality(((DataSourceService)target).item.getFunctionality());
		this.item.getDataSources().addAll(((DataSourceService)target).item.getDataSources());
		this.item.setScopes(((DataSourceService)target).item.getScopes());
		
		if(doStore) this.store(applyDetails, persistencyType, true);
	}
	
	@Override
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return this.load(loadDetails, persistencyType, false);
	}
	
	public boolean load(boolean loadDetails, RRContext.DatastoreType persistencyType, boolean overridePrefetched) throws ResourceRegistryException
	{
		long existStart = Calendar.getInstance().getTimeInMillis();
		if(!this.exists(persistencyType)) return false;
		long existEnd = Calendar.getInstance().getTimeInMillis();
		long lookupStart = existEnd;
		
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
					DataSourceService retrieved = (DataSourceService)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, loadDetails, persistencyType, false);
					memLoaded = true;
				}
				sharedLock.unlock(); locked = false;
			}
			if(!memLoaded)
			{
				pm = this.getISContext().getManagerForRead(persistencyType);
				sharedLock.lock(); locked = true;
				pm.currentTransaction().begin();
				this.item=pm.detachCopy(pm.getObjectById(this.daoType, this.item.getID()));
				pm.currentTransaction().commit();
				sharedLock.unlock(); locked = false;
			}
			
			HostingNode hn=new HostingNode();
			hn.setID(this.item.getHostingNode());
			
			if(loadDetails)
			{
				if(hn.exists(persistencyType)) hn.load(loadDetails,persistencyType);
				else logger.warn("Hosting node " + hn.getID() + " does not exist!");
				
				this.hostingNode = hn;
				
				this.datasources=new HashSet<DataSource>();
				for(String dsId : this.item.getDataSources())
				{
					DataSource d = DataSource.getById(loadDetails, dsId);
					if(d != null) this.datasources.add(d);
					else logger.warn("Datasource " + dsId + " does not exist!");
				}
			}
		}finally
		{
			if(locked) sharedLock.unlock();
			if (pm!=null && pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			if(pm!=null) pm.close();
		}
		long lookupEnd = Calendar.getInstance().getTimeInMillis();
		
		return true;
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
			DataSourceService item = null;
			try
			{
				item=this.getClass().newInstance();
			}catch(Exception e)
			{
				throw new ResourceRegistryException("Could not store data source service", e);
			}
			item.setID(this.getID());
			item.load(storeDetails, persistencyType, true);
			item.apply(this, false, persistencyType, true);
		}
		else
		{
			this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
			if(persistencyType.equals(DatastoreType.LOCAL) && context.isTargetInMemory(this.getClass().getName()))
				InMemoryStore.setItem(this.getClass(), this);
			
			PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
			try
			{
				Set<String> did=new HashSet<String>();
				if(this.getDataSources() != null) //TODO is it ok?
				{
					for(DataSource item : this.getDataSources()) did.add(item.getID());
					this.item.setDataSources(did);
				}
				
				pm.currentTransaction().begin();
				this.item=pm.detachCopy(pm.makePersistent(this.item));
				pm.currentTransaction().commit();
				pm.flush();
				
				if(storeDetails)
				{
					for(DataSource d : this.datasources) d.store(storeDetails, persistencyType);
					this.hostingNode.store(storeDetails, persistencyType);
				}
			}finally
			{
				if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
				pm.close();
			}
		}
	}

	@Override
	public void delete(boolean deleteDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		if(deleteDetails) throw new ResourceRegistryException("Unsupported operation");
		
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			DataSourceService d=(DataSourceService)this.getClass().newInstance();
			d.setID(this.getID());
			d.load(deleteDetails,persistencyType,true);
			
			pm.currentTransaction().begin();
			pm.deletePersistent(d.item);
			pm.currentTransaction().commit();
			pm.flush();
			
		}
		catch(Exception e)
		{
			throw new ResourceRegistryException("Could not delete data source", e);
		}
		finally
		{
			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			pm.close();
		}
	}
	
	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException
	{
		if(!(target instanceof DataSourceService)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((DataSourceService)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((DataSourceService)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((DataSourceService)target).item.getID()!=null && !this.item.getID().equals(((DataSourceService)target).item.getID())) return false;
		if(this.item.getFunctionality() == null && ((DataSourceService)target).item.getFunctionality()!=null) return false;
		if(this.item.getFunctionality() != null && ((DataSourceService)target).item.getFunctionality()==null) return false;
		if(this.item.getFunctionality() != null && ((DataSourceService)target).item.getFunctionality()!=null && !this.item.getFunctionality().equals(((DataSourceService)target).item.getFunctionality())) return false;
		if(this.item.getEndpoint() == null && ((DataSourceService)target).item.getEndpoint()!=null) return false;
		if(this.item.getEndpoint() != null && ((DataSourceService)target).item.getEndpoint()==null) return false;
		if(this.item.getEndpoint() != null && ((DataSourceService)target).item.getEndpoint()!=null && !this.item.getEndpoint().equals(((DataSourceService)target).item.getEndpoint())) return false;
		if(this.item.getHostingNode() == null && ((DataSourceService)target).item.getHostingNode()!=null) return false;
		if(this.item.getHostingNode() != null && ((DataSourceService)target).item.getHostingNode()==null) return false;
		if(this.item.getHostingNode() != null && ((DataSourceService)target).item.getHostingNode()!=null && !this.item.getHostingNode().equals(((DataSourceService)target).item.getHostingNode())) return false;
		if(((DataSourceService)target).getDataSources() != null) //TODO null check ok?
		{
			if(this.item.getDataSources().size()!=((DataSourceService)target).getDataSources().size()) return false;
			if(!((DataSourceService)target).getDataSources().containsAll(this.item.getDataSources())) return false;
		}
		if(this.item.getScopes().size()!=((DataSourceService)target).getScopes().size()) return false;
		if(!((DataSourceService)target).getScopes().containsAll(this.item.getScopes())) return false;
		
		if(includeDetails)
		{
			if(this.datasources.size()!=((DataSourceService)target).datasources.size()) return false;
			for(DataSource item : ((DataSourceService)target).datasources)
			{
				DataSource tmp=this.getDataSource(item.getID());
				if(tmp==null) return false;
				if(!tmp.isEqual(item, includeDetails)) return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(!(other.getClass().isInstance(this.getClass()))) return false;
		return this.item.getID().equals(((DataSource)other).getID());
	}
	
	public DataSource getDataSource(String id)
	{
		for(DataSource item : this.datasources) if(item.getID().equals(id)) return item;
		return null;
	}
	
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("DataSourceService - ID : "+this.getID()+"\n");
		buf.append("DataSourceService - Type : "+this.getType()+"\n");
		buf.append("DataSourceService - EndPoint : "+this.getEndpoint()+"\n");
		buf.append("DataSourceService - Functionality : "+this.getFunctionality()+"\n");
		buf.append("DataSourceService - Hosting Node : "+this.getHostingNode()+"\n");
		buf.append("DataSourceService - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		return buf.toString();
	}

	protected boolean exists(Class<? extends DataSourceServiceDao> type, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
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
			query=pm.newNamedQuery(type, "exists");
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
	public boolean exists(RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		return this.exists(DataSourceServiceDao.class, persistencyType);
	}

	@SuppressWarnings("unchecked")
	protected static List<DataSourceService> getAll(Class<? extends DataSourceServiceDao> type, DatastoreType persistencyType, boolean loadDetails) throws ResourceRegistryException
	{
		
		List<DataSourceService> col=new ArrayList<DataSourceService>();
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		Query typeQuery = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(type, "all");
			query.compile();
			sharedLock.lock(); locked = true;
			Collection<String> res=(Collection<String>)query.execute();
			sharedLock.unlock(); locked = false;
			
			Class<? extends DataSourceServiceDao> targetType = null;
			boolean base = false;
			if(type.getName().equals(DataSourceServiceDao.class.getName())) base = true;
			else targetType = type;
	
			Map<String, Object> typeQueryArgs=new HashMap<String, Object>();
			if(base == true)
			{
				typeQuery = pm.newNamedQuery(type, "getType");
				typeQuery.compile();
			}
			try{
				for(String item : res)
				{
					if(base == true)
					{
						typeQueryArgs.put("id", item);
						sharedLock.lock(); locked = true;
						Collection<?> t=(Collection<?>)typeQuery.executeWithMap(typeQueryArgs);
						sharedLock.unlock(); locked = false;
						targetType = DataSourceService.SubTypes.get(DataSourceService.SubTypesMap.get(Type.valueOf((String)t.iterator().next())));
						if(targetType == null) continue;
					}
					Class<?> typeToInstantiate = DataSourceService.ReverseSubTypes.get(targetType);
					if(typeToInstantiate == null) continue;
					DataSourceService f = (DataSourceService)typeToInstantiate.newInstance();
					f.setID(item);
					f.load(loadDetails, persistencyType);
					col.add(f);
				}
			}catch(IllegalAccessException e)
			{
				throw new ResourceRegistryException("Could not instantiate type: " + type.getName(), e);
			}catch(InstantiationException e)
			{
				throw new ResourceRegistryException("Could not instantiate type: " + type.getName(), e);
			}
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(typeQuery != null) typeQuery.closeAll();
			if(pm!=null) pm.close();
		}
		return col;
	}

	public static List<DataSourceService> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSourceService.getAll(DataSourceServiceDao.class, persistencyType, loadDetails);
	}
	
	public static List<DataSourceService> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSourceService.getAll(loadDetails, DatastoreType.LOCAL);
	}
	
	public static boolean exists(String id, DatastoreType persistencyType) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(DataSourceServiceDao.class, "exists");
			query.compile();
			HashMap<String, Object> args=new HashMap<String, Object>();
			args.put("id", id);
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
	public static Set<String> getAllIds(DatastoreType persistencyType) throws ResourceRegistryException
	{
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		PersistenceManager pm = null;
		Query query = null;
		Collection<String> res = null;
		try
		{
			pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
			query=pm.newNamedQuery(DataSourceServiceDao.class, "all");
			query.compile();
			sharedLock.lock(); locked = true;
			res=(Collection<String>)query.execute();
			sharedLock.unlock(); locked = false;
			return new HashSet<String>(res);
		}finally
		{
			if(locked) sharedLock.unlock();
			if(query!=null) query.closeAll();
			if(pm!=null) pm.close();
		}
	}
	
	public static Set<String> getAllIds() throws ResourceRegistryException
	{
		return getAllIds(DatastoreType.LOCAL);
	}
	
	public static boolean exists(String id) throws ResourceRegistryException
	{
		return DataSourceService.exists(id, DatastoreType.LOCAL);
	}
	
	public static DataSourceService getById(boolean loadDetails, DatastoreType persistencyType, String id) throws ResourceRegistryException 
	{	
		PersistenceManager pm = null;
		Query typeQuery = null;
		Lock sharedLock = ResourceRegistry.getContext().getSharedLock();
		boolean locked = false;
		Class<?> typeToInstantiate = null;
		try 
		{
	
			boolean memLoaded = false;
			if(persistencyType.equals(DatastoreType.LOCAL))
			{
				boolean inMemory = false;
				for(Class<? extends DataSourceService> type : DataSourceService.subTypes())
				{
					if(ResourceRegistry.getContext().isTargetInMemory(type.getName()))
						inMemory = true;
				}
				if(inMemory == true)
				{
					sharedLock.lock(); locked = true;
					for(Class<? extends DataSourceService> type : DataSourceService.subTypes())
					{
						if(InMemoryStore.hasItem(type, id))
						{
							typeToInstantiate = type;
							memLoaded = true;
							break;
						}
					}
					sharedLock.unlock(); locked = false;
				}
			}
			if(!memLoaded)
			{
				long retrieveTypeStart = Calendar.getInstance().getTimeInMillis();
				pm = ResourceRegistry.getContext().getManagerForRead(persistencyType);
				typeQuery = pm.newNamedQuery(DataSourceServiceDao.class, "getType");
				typeQuery.compile();
				Map<String, Object> typeQueryArgs=new HashMap<String, Object>();
				typeQueryArgs.put("id", id);
				sharedLock.lock(); locked = true;
				Collection<?> t=(Collection<?>)typeQuery.executeWithMap(typeQueryArgs);
				if(t.isEmpty()) return null;
				sharedLock.unlock(); locked = false;
				long retrieveTypeEnd = Calendar.getInstance().getTimeInMillis();
				long instantiateStart = retrieveTypeEnd;
				Class<? extends DataSourceServiceDao> targetType = DataSourceService.SubTypes.get(DataSourceService.SubTypesMap.get(Type.valueOf((String)t.iterator().next())));
				if(targetType == null) return null;
				typeToInstantiate = DataSourceService.ReverseSubTypes.get(targetType);
			}
			if(typeToInstantiate == null) return null;
			DataSourceService f = (DataSourceService)typeToInstantiate.newInstance();
			long instantiateEnd = Calendar.getInstance().getTimeInMillis();
			long loadStart = instantiateEnd;
			f.setID(id);
			if(!f.load(loadDetails)) return null;
			long loadEnd = Calendar.getInstance().getTimeInMillis();
			return f;
		}catch(IllegalAccessException e)
		{
			throw new ResourceRegistryException("Could not instantiate type: " + typeToInstantiate.getName(), e);
		}catch(InstantiationException e)
		{
			throw new ResourceRegistryException("Could not instantiate type: " + typeToInstantiate.getName(), e);
		}finally 
		{
			if(locked) sharedLock.unlock();
			if(typeQuery!=null) typeQuery.closeAll();
			if(pm!=null) pm.close();
		}
	}
	
	public static DataSourceService getById(boolean loadDetails, String id) throws ResourceRegistryException
	{
		return DataSourceService.getById(loadDetails, DatastoreType.LOCAL, id);
	}
	
}
