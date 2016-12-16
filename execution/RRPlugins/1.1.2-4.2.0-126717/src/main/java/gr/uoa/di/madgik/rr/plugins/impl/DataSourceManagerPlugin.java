package gr.uoa.di.madgik.rr.plugins.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadataDao;
import gr.uoa.di.madgik.rr.element.search.Field;
import gr.uoa.di.madgik.rr.element.search.FieldDao;
import gr.uoa.di.madgik.rr.element.search.Presentable;
import gr.uoa.di.madgik.rr.element.search.PresentableDao;
import gr.uoa.di.madgik.rr.element.search.Searchable;
import gr.uoa.di.madgik.rr.element.search.SearchableDao;
import gr.uoa.di.madgik.rr.element.search.index.DataSource;
import gr.uoa.di.madgik.rr.plugins.Plugin;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

public class DataSourceManagerPlugin extends Plugin 
{
	private static final Logger logger = LoggerFactory
			.getLogger(DataSourceManagerPlugin.class);
	
	public static long InactiveDataSourceGracePeriodDef = 10;
	public static TimeUnit InactiveDataSourceGracePeriodUnitDef = TimeUnit.DAYS;
	public static long InactiveDataSourceGracePeriod = InactiveDataSourceGracePeriodDef;
	public static TimeUnit InactiveDataSourceGracePeriodUnit = InactiveDataSourceGracePeriodUnitDef;
	
	public DataSourceManagerPlugin()
	{
		this.type = Type.PRE_UPDATE;
		this.processedItems.add(new ProcessedItemType(SearchableDao.class, DatastoreType.LOCAL));
		this.processedItems.add(new ProcessedItemType(PresentableDao.class, DatastoreType.LOCAL));
		this.processedItems.add(new ProcessedItemType(Field.class, DatastoreType.LOCAL));
	}
	
	@Override
	public void readConfiguration(String prefix, Properties properties) throws ResourceRegistryException
	{
		super.readConfiguration(prefix, properties);
		if(properties == null) return;
		boolean defaultUsed = true;
		String value = null;
		if((value = properties.getProperty("inactiveDataSourceGrace")) != null)
		{
			String intervalValue = value;
			if((value = properties.getProperty("inactiveDataSourceGraceUnit")) != null)
			{
				DataSourceManagerPlugin.InactiveDataSourceGracePeriod = Long.parseLong(intervalValue);
				DataSourceManagerPlugin.InactiveDataSourceGracePeriodUnit = TimeUnit.valueOf(value);
				defaultUsed = false;
				logger.info( "Using inactive DataSource grace period: " + DataSourceManagerPlugin.InactiveDataSourceGracePeriod + " " + 
						DataSourceManagerPlugin.InactiveDataSourceGracePeriodUnit);
			}
		}
		if(defaultUsed)
			logger.info( "Using default inactive DataSource grace period: " + DataSourceManagerPlugin.InactiveDataSourceGracePeriod + " " + 
					DataSourceManagerPlugin.InactiveDataSourceGracePeriodUnit);
	}
	
	private List<Field> getFields() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<Field> fields = (Set<Field>)this.items.get(new ProcessedItemType(Field.class, DatastoreType.LOCAL));
		if(fields == null) return Field.getAll(true, DatastoreType.LOCAL);
		return new ArrayList<Field>(fields);
	}
	
	private Set<IDaoElement> getSearchables() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<IDaoElement> s = (Set<IDaoElement>)this.itemDaos.get(new ProcessedItemType(SearchableDao.class, DatastoreType.LOCAL));
		try
		{ 
			if(s == null) return DatastoreHelper.getItems(DatastoreType.LOCAL, SearchableDao.class); 
		}catch(Exception e) { throw new ResourceRegistryException("",e); }
		return s;
	}
	
	private Set<IDaoElement> getPresentables() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<IDaoElement> p = (Set<IDaoElement>)this.itemDaos.get(new ProcessedItemType(PresentableDao.class, DatastoreType.LOCAL));
		try
		{ 
			if(p == null) return DatastoreHelper.getItems(DatastoreType.LOCAL, PresentableDao.class); 
		}catch(Exception e) { throw new ResourceRegistryException("",e); }
		return p;
	}
	
	@Override
	public void setup() throws ResourceRegistryException { }

	private ElementMetadata createElementMetadataForDataSource(String dataSourceId, long timestamp) throws ResourceRegistryException 
	{
		DataSource ds = DataSource.getById(true, dataSourceId);
		ElementMetadata m = new ElementMetadata();
		m.setID(dataSourceId);
		m.setType(ElementMetadata.Type.DataSource);
		m.setMetadataTimestamp(timestamp);
		StringBuffer buf = new StringBuffer();
		if(ds != null) 
		{
			for(String scope : ds.getScopes())
				buf.append(scope + " ");
		}
		m.getProperties().put("scopes", buf.toString().trim());
		return m;
	}
	
	@Override
	protected void execute(Set<Class<?>> targets)throws ResourceRegistryException 
	{
		long timestamp = new Date().getTime();
		
		try 
		{
			//Update element metadata
			if(targets.contains(ElementMetadataDao.class))
			{
				Set<IDaoElement> allSearchables = getSearchables();
				Set<IDaoElement> allPresentables = getPresentables();
				
				Set<String> allDataSourceIds = DataSource.getAllIds();
				for(String dsId : allDataSourceIds)
				{
					ElementMetadata m = null;
					if((m = ElementMetadata.getById(true, dsId)) == null)
					{
						logger.trace( "Creating element metadata for active datasource " + dsId);
						m = createElementMetadataForDataSource(dsId, timestamp);
						m.store(true, DatastoreType.LOCAL);
					}else
					{
						m.setMetadataTimestamp(timestamp);
						m.store(true, DatastoreType.LOCAL);
					}
				}
			
				for(IDaoElement s : allSearchables)
				{
					ElementMetadata m = ElementMetadata.getById(true, DatastoreType.LOCAL, ((SearchableDao)s).getLocator());
					if(!allDataSourceIds.contains(((SearchableDao)s).getLocator()))
					{
						if(m==null) 
						{
							logger.trace( "Creating element metadata for inactive datasource " + ((SearchableDao)s).getLocator());
							m = createElementMetadataForDataSource(((SearchableDao)s).getLocator(), timestamp);
							m.store(true, DatastoreType.LOCAL);
							allDataSourceIds.add(((SearchableDao)s).getLocator());
						}
					}
				}
				for(IDaoElement s : allPresentables)
				{
					ElementMetadata m = ElementMetadata.getById(true, DatastoreType.LOCAL, ((PresentableDao)s).getLocator());
					if(!allDataSourceIds.contains(((PresentableDao)s).getLocator()))
					{
						if(m==null) 
						{
							logger.trace( "Creating element metadata for inactive datasource " + ((PresentableDao)s).getLocator());
							m = createElementMetadataForDataSource(((PresentableDao)s).getLocator(), timestamp);
							m.store(true, DatastoreType.LOCAL);
							allDataSourceIds.add(((PresentableDao)s).getLocator());
						}
					}
				}
				
				if(targets.contains(FieldDao.class) && targets.contains(SearchableDao.class) && targets.contains(PresentableDao.class))
				{
					List<Field> allFields = getFields();
					//Set<IDaoElement> allMetadata = DatastoreHelper.getItems(DatastoreType.DERBY, ElementMetadataDao.class);
	
					//HashSet<Class<?>> purge=new HashSet<Class<?>>();
					//purge.add(FieldDao.class);
					//DatastoreHelper.clear(RRContext.DatastoreType.BUFFER, purge);
					Set<String> toDelMetadata = new HashSet<String>();
					for(Field f : allFields)
					{
						boolean updated = false;
						List<Searchable> toDelSearchables = new ArrayList<Searchable>();
						for(Searchable s : f.getSearchables()) 
						{
							if(!DataSource.exists(s.getLocator()))
							{
								ElementMetadata m = ElementMetadata.getById(true, s.getLocator());
								if(timestamp - m.getMetadataTimestamp() > TimeUnit.MILLISECONDS.convert(InactiveDataSourceGracePeriod, InactiveDataSourceGracePeriodUnit))
								{
									updated = true;
									toDelSearchables.add(s);
									toDelMetadata.add(s.getLocator());
								}
							}
						}
						List<Presentable> toDelPresentables = new ArrayList<Presentable>();
						for(Presentable p : f.getPresentables()) 
						{
							if(!DataSource.exists(p.getLocator()))
							{
								ElementMetadata m = ElementMetadata.getById(true, p.getLocator());
								if(timestamp - m.getMetadataTimestamp() > TimeUnit.MILLISECONDS.convert(InactiveDataSourceGracePeriod, InactiveDataSourceGracePeriodUnit))
								{
									updated = true;
									toDelPresentables.add(p);
									toDelMetadata.add(p.getLocator());
								}
							}
						}
						
						for(Searchable s : toDelSearchables)
						{
							logger.trace( "Datasource " + s.getLocator() + " was inactive for more than " + 
									InactiveDataSourceGracePeriod + " " + InactiveDataSourceGracePeriodUnit + ". Removing searchable " + s.getID());
							f.getSearchables().remove(s);
							s.delete(true, DatastoreType.LOCAL);
						}
						for(Presentable p : toDelPresentables)
						{
							logger.trace( "Datasource " + p.getLocator() + " was inactive for more than " + 
									InactiveDataSourceGracePeriod + " " + InactiveDataSourceGracePeriodUnit + ". Removing presentable " + p.getID());
							f.getPresentables().remove(p);
							p.delete(true, DatastoreType.LOCAL);
						}
	
						
						if(updated) f.store(false, DatastoreType.LOCAL);
					}
					
					for(String m : toDelMetadata)
					{
						ElementMetadata toDel = new ElementMetadata();
						toDel.setID(m);
						toDel.delete(true, DatastoreType.LOCAL);
					}
				}
			}
		}catch(Exception e)
		{
			throw new ResourceRegistryException("could not align outgoing elements", e);
		}
		
	}

}
