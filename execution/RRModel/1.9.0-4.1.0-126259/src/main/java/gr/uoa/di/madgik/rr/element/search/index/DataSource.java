package gr.uoa.di.madgik.rr.element.search.index;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.execution.ExecutionService;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainer.FieldType;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

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

public abstract class DataSource extends RRElement
{
	private static final Logger logger = LoggerFactory
			.getLogger(DataSource.class);
	
	
	public enum Type {
		FullTextIndex,
		SruConsumer,
		OpenSearch;
		
		public boolean isExternal() {
			return (this.equals(OpenSearch) || this.equals(SruConsumer));
		}
	}
	
	private static Map<Class<? extends DataSource>, Class<? extends DataSourceDao>> SubTypes = 
		new HashMap<Class<? extends DataSource>, Class<? extends DataSourceDao>>();
	
	private static Map<Class<? extends DataSourceDao>, Class<? extends DataSource>> ReverseSubTypes = 
		new HashMap<Class<? extends DataSourceDao>, Class<? extends DataSource>>();
	
	private static Map<Type, Class<? extends DataSource>> SubTypesMap = 
		new HashMap<Type, Class<? extends DataSource>>();
	
	private static Map<Class<? extends DataSource>, Class<? extends DataSourceService>> Services =
		new HashMap<Class<? extends DataSource>, Class<? extends DataSourceService>>();
	
	public static void clearSubTypes()
	{
		DataSource.SubTypesMap.clear();
		DataSource.SubTypes.clear();
		DataSource.ReverseSubTypes.clear();
	}
	
	public static void addSubType(Type type, Class<? extends DataSource> ds, Class<? extends DataSourceDao> dsDao)
	{
		DataSource.SubTypesMap.put(type, ds);
		DataSource.SubTypes.put(ds, dsDao);
		DataSource.ReverseSubTypes.put(dsDao, ds);
	}
	
	public static void addServiceType(Class<? extends DataSource> ds, Class<? extends DataSourceService> service)
	{
		DataSource.Services.put(ds, service);
	}
	
	public static Set<Class<? extends DataSource>> subTypes()
	{
		return DataSource.SubTypes.keySet();
	}
	
	public static Set<Class<? extends DataSourceDao>> subTypeItems() 
	{
		return new HashSet<Class<? extends DataSourceDao>>(DataSource.SubTypes.values());
	}
	
	
	protected DataSourceDao item=null;
	protected Set<FieldIndexContainer> fieldInfo=new HashSet<FieldIndexContainer>();
	protected List<DataSourceService> boundDataSourceServices=null;
	protected RRContext context=null;
	
	private Class<? extends DataSourceDao> daoType = null;
	@SuppressWarnings("unused")
	private Class<? extends DataSourceService> serviceType = null;
	
	@Override
	public RRContext getISContext()
	{
		return this.context;
	}

	protected DataSource(Class<? extends DataSourceDao> type, Class<? extends DataSourceService> serviceType) throws ResourceRegistryException
	{
		try {
			this.item=type.newInstance();
			this.daoType=type;
			this.serviceType=serviceType;
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
	
	public String getFunctionality()
	{
		return item.getFunctionality();
	}

	public void setFunctionality(String functionality)
	{
		this.item.setFunctionality(functionality);
	}
	
	public Set<String> getCapabilities()
	{
		return this.item.getCapabilities();
	}
	
	public Set<FieldIndexContainer> getFieldInfo()
	{
		return this.fieldInfo;
	}
	
	public abstract List<DataSourceService> getDataSourceServices() throws ResourceRegistryException;
	
	@Override
	public DataSourceDao getItem()
	{
		return this.item;
	}
	
	@Override
	public void setDirty()
	{
		this.item.setTimestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	private void apply(IRRElement target, boolean applyDetails,  DatastoreType persistencyType, boolean doStore) throws ResourceRegistryException
	{
		if(!(target instanceof DataSource)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.isEqual(target,applyDetails)) return;
		this.item.setID(((DataSource)target).item.getID());
		this.item.setFunctionality(((DataSource)target).item.getFunctionality());
		this.item.setType(((DataSource)target).item.getType());
		this.item.setFields(((DataSource)target).item.getFields());
		this.item.setCapabilities(((DataSource)target).item.getCapabilities());
		this.item.setScopes(((DataSource)target).item.getScopes());
		
		if(applyDetails)
		{
			throw new ResourceRegistryException("Unsupported operation");
		}
		
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
					DataSource retrieved = (DataSource)InMemoryStore.getItem(this.getClass(), this.getID());
					this.apply(retrieved, false, persistencyType, false);
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
		}finally
		{
			if(locked) sharedLock.unlock();
			if (pm!=null && pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			if(pm!=null) pm.close();
		}
		long lookupEnd = Calendar.getInstance().getTimeInMillis();
		long lookupDStart = lookupEnd;
		this.fieldInfo.clear();
		for(String item : this.item.getFields())
		{
			FieldIndexContainer w=new FieldIndexContainer();
			w.setID(item);
			if(loadDetails)
			{
				if(w.exists(persistencyType))
				{
					if(loadDetails)  w.load(loadDetails,persistencyType,overridePrefetched);
					this.fieldInfo.add(w);
				}
			}else
				this.fieldInfo.add(w);
		}
		long lookupDend = Calendar.getInstance().getTimeInMillis();

		return true;
	}

	@Override
	public void delete(boolean deleteDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		if(!this.exists(persistencyType)) return;
		if(persistencyType.equals(DatastoreType.LOCAL) && InMemoryStore.hasItem(this.getClass(), this.getID()))
			InMemoryStore.removeItem(this.getClass(), this.getID());
		PersistenceManager pm = this.getISContext().getManagerForWrite(persistencyType);
		try
		{
			DataSource d=(DataSource)this.getClass().newInstance();
			d.setID(this.getID());
			d.load(deleteDetails,persistencyType,true);
			
			pm.currentTransaction().begin();
			pm.deletePersistent(d.item);
			pm.currentTransaction().commit();
			pm.flush();
			
			if(deleteDetails)
			{
				for(FieldIndexContainer fic : this.getFieldInfo()) fic.delete(deleteDetails, persistencyType);
			}
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
	public void store(boolean storeDetails, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
	{
		this.store(storeDetails, persistencyType, false);
	}
	
	private void store(boolean storeDetails, RRContext.DatastoreType persistencyType, boolean writeThrough) throws ResourceRegistryException
	{
		if(this.exists(persistencyType) && !writeThrough)
		{
			DataSource item = null;
			try
			{
				item=this.getClass().newInstance();
			}catch(Exception e)
			{
				throw new ResourceRegistryException("Could not store data source", e);
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
				Set<String> fid=new HashSet<String>();
				for(FieldIndexContainer item : this.fieldInfo) fid.add(item.getID());
				this.item.setFields(fid);
				
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
				for(FieldIndexContainer item : this.fieldInfo) item.store(storeDetails, persistencyType);
			}
		}
	}

	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException
	{
		if(!(target instanceof DataSource)) throw new ResourceRegistryException("cannot apply to target of "+target);
		if(this.item.getID() == null && ((DataSource)target).item.getID()!=null) return false;
		if(this.item.getID() != null && ((DataSource)target).item.getID()==null) return false;
		if(this.item.getID() != null && ((DataSource)target).item.getID()!=null && !this.item.getID().equals(((DataSource)target).item.getID())) return false;
		if(this.item.getFunctionality() == null && ((DataSource)target).item.getFunctionality()!=null) return false;
		if(this.item.getFunctionality() != null && ((DataSource)target).item.getFunctionality()==null) return false;
		if(this.item.getFunctionality() != null && ((DataSource)target).item.getFunctionality()!=null && !this.item.getFunctionality().equals(((DataSource)target).item.getFunctionality())) return false;
		if(this.item.getCapabilities().size()!=((DataSource)target).getCapabilities().size()) return false;
		if(!((DataSource)target).getCapabilities().containsAll(this.item.getCapabilities())) return false;
		if(this.item.getFields().size()!=((DataSource)target).item.getFields().size()) return false;
		if(!((DataSource)target).item.getFields().containsAll(this.item.getFields())) return false;
		if(this.item.getScopes().size()!=((DataSource)target).getScopes().size()) return false;
		if(!((DataSource)target).getScopes().containsAll(this.item.getScopes())) return false;
		
		if(includeDetails)
		{
			if(this.fieldInfo.size()!=((DataSource)target).fieldInfo.size()) return false;
			for(FieldIndexContainer item : ((DataSource)target).fieldInfo)
			{
				FieldIndexContainer tmp=this.getFieldInfo(item.getID());
				if(tmp==null) return false;
				if(!tmp.isEqual(item,includeDetails)) return false;
			}
		}
		return true;
	}
	
	public FieldIndexContainer getFieldInfo(String id)
	{
		for(FieldIndexContainer item : this.fieldInfo) if(item.getID().equals(id)) return item;
		return null;
	}
	
	public String deepToString()
	{
		StringBuilder buf=new StringBuilder();
		buf.append("DataSource - ID : "+this.getID()+"\n");
		buf.append("DataSource - Type : "+this.getType()+"\n");
		buf.append("DataSource - Functionality : "+this.getFunctionality()+"\n");
		buf.append("DataSource - Scopes : ");
		for(String scope : this.getScopes()) buf.append(scope+" "); 
		buf.append("\nDataSource - Capabilities : ");
		for(String cap : this.getCapabilities()) buf.append(cap+" "); 
		buf.append("\nDataSource - Fields : "+"\n");
		for(FieldIndexContainer cap : this.getFieldInfo()) buf.append(cap.deepToString()+"\n"); 
		return buf.toString();
	}
	
	public boolean supportsCapabilities(List<String> capabilities, boolean all)
	{
		if(all) return this.getCapabilities().containsAll(capabilities);
		for(String capability : capabilities)
		{
			if(this.getCapabilities().contains(capability)) return true;
		}
		return false;
	}
	
	public Set<String> getLanguagesForCollectionAndSearchableField(String collection, String field)
	{
		Set<String> langs=new HashSet<String>();
		for(FieldIndexContainer cont : this.getFieldInfo())
		{
			if(!cont.getCollection().equals(collection)) continue;
			if(!cont.getField().equals(field)) continue;
			if(!cont.isSearchable()) continue;
			langs.add(cont.getLanguage());
		}
		return langs;
	}
	
	public Set<String> getCollectionsForLanguageAndSearchableField(String language, String field)
	{
		Set<String> cols=new HashSet<String>();
		for(FieldIndexContainer cont : this.getFieldInfo())
		{
			if(!cont.getLanguage().equals(language)) continue;
			if(!cont.getField().equals(field)) continue;
			if(!cont.isSearchable()) continue;
			cols.add(cont.getCollection());
		}
		return cols;
	}

	protected boolean exists(Class<? extends DataSourceDao> type, RRContext.DatastoreType persistencyType) throws ResourceRegistryException
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
		return this.exists(DataSourceDao.class, persistencyType);
	}

	@SuppressWarnings("unchecked")
	protected static List<DataSource> getAll(Class<? extends DataSourceDao> type, DatastoreType persistencyType, boolean loadDetails) throws ResourceRegistryException
	{
		
		List<DataSource> col=new ArrayList<DataSource>();
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
			
			Class<? extends DataSourceDao> targetType = null;
			boolean base = false;
			if(type.getName().equals(DataSourceDao.class.getName())) base = true;
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
						targetType = DataSource.SubTypes.get(DataSource.SubTypesMap.get(Type.valueOf((String)t.iterator().next())));
						if(targetType == null) continue;
					}
					Class<?> typeToInstantiate = DataSource.ReverseSubTypes.get(targetType);
					if(typeToInstantiate == null) continue;
					DataSource f = (DataSource)typeToInstantiate.newInstance();
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

	public static List<DataSource> getAll(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		return DataSource.getAll(DataSourceDao.class, persistencyType, loadDetails);
	}
	
	public static List<DataSource> getAll(boolean loadDetails) throws ResourceRegistryException
	{
		return DataSource.getAll(loadDetails, DatastoreType.LOCAL);
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
			query=pm.newNamedQuery(DataSourceDao.class, "exists");
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
			query=pm.newNamedQuery(DataSourceDao.class, "all");
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
		return DataSource.exists(id, DatastoreType.LOCAL);
	}
	
	public static DataSource getById(boolean loadDetails, DatastoreType persistencyType, String id) throws ResourceRegistryException 
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
				for(Class<? extends DataSource> type : DataSource.subTypes())
				{
					if(ResourceRegistry.getContext().isTargetInMemory(type.getName()))
						inMemory = true;
				}
				if(inMemory == true)
				{
					sharedLock.lock(); locked = true;
					for(Class<? extends DataSource> type : DataSource.subTypes())
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
				typeQuery = pm.newNamedQuery(DataSourceDao.class, "getType");
				typeQuery.compile();
				Map<String, Object> typeQueryArgs=new HashMap<String, Object>();
				typeQueryArgs.put("id", id);
				sharedLock.lock(); locked = true;
				Collection<?> t=(Collection<?>)typeQuery.executeWithMap(typeQueryArgs);
				if(t.isEmpty()) return null;
				sharedLock.unlock(); locked = false;
				long retrieveTypeEnd = Calendar.getInstance().getTimeInMillis();
				long instantiateStart = retrieveTypeEnd;
				Class<? extends DataSourceDao> targetType = DataSource.SubTypes.get(DataSource.SubTypesMap.get(Type.valueOf((String)t.iterator().next())));
				if(targetType == null) return null;
				typeToInstantiate = DataSource.ReverseSubTypes.get(targetType);
			}
			if(typeToInstantiate == null) return null;
			DataSource f = (DataSource)typeToInstantiate.newInstance();
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
	
	public static DataSource getById(boolean loadDetails, String id) throws ResourceRegistryException
	{
		return DataSource.getById(loadDetails, DatastoreType.LOCAL, id);
	}
	
	public static List<DataSource> queryByFieldIDAndTypeAndCollection(boolean loadDetails, String fieldID, String collection, FieldType type) throws ResourceRegistryException
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
		return DataSource.queryByFieldIDAndTypeAndCollection(RRContext.DatastoreType.LOCAL, loadDetails, fieldID, collection, stype);
	}
	
	public static List<DataSource> queryByFieldIDAndTypeAndCollectionAndScope(boolean loadDetails, String fieldID, String collection, FieldType type, String scope) throws ResourceRegistryException
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
		return DataSource.queryByFieldIDAndTypeAndCollectionAndScope(RRContext.DatastoreType.LOCAL, loadDetails, fieldID, collection, stype, scope);
	}

	public static List<DataSource> queryByFieldIDAndTypeAndLanguage(boolean loadDetails, String fieldID, String language, FieldType type) throws ResourceRegistryException
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
		return DataSource.queryByFieldIDAndTypeAndLanguage(RRContext.DatastoreType.LOCAL, loadDetails, fieldID, language, stype);
	}
	
	public boolean containsField(String id) 
	{
		return this.item.getFields().contains(id);
	}
	
	public static List<DataSource> queryByFieldIDAndTypeAndCollection(RRContext.DatastoreType persistencyType, boolean loadDetails, String fieldID, String collection, String type) throws ResourceRegistryException
	{
		logger.info("querying for field id : " + fieldID + " collection : " + collection + " type : " + type);
		
		List<FieldIndexContainer> fieldInfos=FieldIndexContainer.queryByFieldIDAndTypeAndCollection(persistencyType, fieldID, collection, type);
		
		logger.info("fieldInfos size : " + fieldInfos.size());
		
		Set<String> sourceIDs=new HashSet<String>();
		//List<DataSource> all=getAll(false);
		Set<IDaoElement> all = null;
		try
		{
			all = DatastoreHelper.getItems(DatastoreType.LOCAL, DataSourceDao.class);
			logger.info(" ~> all items from local : " + all.size());
			
		}catch(Exception e)
		{
			logger.info(" ~> error while getting all item from local", e);
			throw new ResourceRegistryException("Could not load data source information", e);
		}
		for(FieldIndexContainer nfo : fieldInfos)
		{
			for(IDaoElement ind:all) if(((DataSourceDao)ind).getFields().contains(nfo.getID())) sourceIDs.add(((DataSourceDao)ind).getID());
		}
		
		logger.info("sourceIDs gathered by fieldInfos : " + sourceIDs);
		
		ArrayList<DataSource> items=new ArrayList<DataSource>();
		for(String id : sourceIDs)
		{
			DataSource item=DataSource.getById(loadDetails, id);
			items.add(item);
		}
		
		logger.info("number of datasources to be returned for querying for field id : " + fieldID + " collection : " + collection + " type : " + type + " #" + items.size());
		return items;
	}
	
	public static List<DataSource> queryByFieldIDAndTypeAndCollectionAndScope(RRContext.DatastoreType persistencyType, boolean loadDetails, String fieldID, String collection, String type, String scope) throws ResourceRegistryException
	{
		List<DataSource> items = DataSource.queryByFieldIDAndTypeAndCollection(persistencyType, loadDetails, fieldID, collection, type);
		List<DataSource> filteredItems = new ArrayList<DataSource>();
		for(DataSource item : items)
		{
			if(item.getScopes().contains(scope))
				filteredItems.add(item);
		}
		return filteredItems;
	}
	
	public static List<DataSource> queryByFieldIDAndTypeAndLanguage(RRContext.DatastoreType persistencyType, boolean loadDetails, String fieldID, String language, String type) throws ResourceRegistryException
	{
		List<FieldIndexContainer> fieldInfos=FieldIndexContainer.queryByFieldIDAndTypeAndLanguage(persistencyType, fieldID, language, type);
		Set<String> sourceIDs=new HashSet<String>();
		List<DataSource> all=getAll(false);
		for(FieldIndexContainer nfo : fieldInfos)
		{
			for(DataSource ind:all) if(ind.item.getFields().contains(nfo.getID())) sourceIDs.add(ind.getID());
		}
		ArrayList<DataSource> items=new ArrayList<DataSource>();
		for(String id : sourceIDs)
		{
			DataSource item=DataSource.getById(loadDetails, id);
			items.add(item);
		}
		return items;
	}

	public static List<DataSource> queryByFieldIDAndTypeAndCollectionAndLanguage(boolean loadDetails, String fieldID, String collection, String language, FieldType type) throws ResourceRegistryException
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
		return DataSource.queryByFieldIDAndTypeAndCollectionAndLanguage(RRContext.DatastoreType.LOCAL, loadDetails, fieldID, collection, language, stype);
	}
	
	public static List<DataSource> queryByFieldIDAndTypeAndCollectionAndLanguage(RRContext.DatastoreType persistencyType, boolean loadDetails, String fieldID, String collection, String language, String type) throws ResourceRegistryException
	{
		List<FieldIndexContainer> fieldInfos=FieldIndexContainer.queryByFieldIDAndTypeAndCollectionAndLanguage(persistencyType, fieldID, collection, language, type);
		Set<String> sourceIDs=new HashSet<String>();
		List<DataSource> all=getAll(false);
		for(FieldIndexContainer nfo : fieldInfos)
		{
			for(DataSource ind:all) if(ind.item.getFields().contains(nfo.getID())) sourceIDs.add(ind.getID());
		}
		ArrayList<DataSource> items=new ArrayList<DataSource>();
		for(String id : sourceIDs)
		{
			DataSource item=DataSource.getById(loadDetails, id);
			items.add(item);
		}
		return items;
	}

}
