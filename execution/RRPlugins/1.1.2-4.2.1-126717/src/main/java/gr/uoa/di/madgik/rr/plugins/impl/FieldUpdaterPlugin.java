package gr.uoa.di.madgik.rr.plugins.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
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
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao;
import gr.uoa.di.madgik.rr.plugins.Plugin;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

public class FieldUpdaterPlugin extends Plugin
{

	private static final Logger logger = LoggerFactory
			.getLogger(FieldUpdaterPlugin.class);
	
	public FieldUpdaterPlugin()
	{
		this.type = Type.POST_RETRIEVE;
		this.processedItems.add(new ProcessedItemType(Field.class, DatastoreType.LOCALBUFFER));
		this.processedItems.add(new ProcessedItemType(DataSource.class, DatastoreType.LOCALBUFFER));
		this.processedItems.add(new ProcessedItemType(ElementMetadataDao.class, DatastoreType.LOCALBUFFER));
		this.processedItems.add(new ProcessedItemType(FieldIndexContainerDao.class, DatastoreType.LOCALBUFFER));
	}

	@Override
	public void setup() throws ResourceRegistryException {
		Field.getBehavior().setMarkDeletion(true);
		Field.getBehavior().setMarkUpdate(true);
	}
	
	private List<Field> getFields() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<Field> fields = (Set<Field>)this.items.get(new ProcessedItemType(Field.class, DatastoreType.LOCALBUFFER));
		if(fields == null) return Field.getAll(true, DatastoreType.LOCALBUFFER);
		return new ArrayList<Field>(fields);
	}
	
	private List<DataSource> getDataSources() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<DataSource> datasources = (Set<DataSource>)this.items.get(new ProcessedItemType(DataSource.class, DatastoreType.LOCALBUFFER));
		if(datasources == null) return DataSource.getAll(false, DatastoreType.LOCALBUFFER);
		return new ArrayList<DataSource>(datasources);
	}
	
	private Set<IDaoElement> getElementMetadata() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<IDaoElement> elMetadata = (Set<IDaoElement>)this.itemDaos.get(new ProcessedItemType(ElementMetadataDao.class, DatastoreType.LOCALBUFFER));
		try
		{ 
			if(elMetadata == null) return DatastoreHelper.getItems(DatastoreType.LOCALBUFFER, ElementMetadataDao.class); 
		}catch(Exception e) { throw new ResourceRegistryException("",e); }
		return elMetadata;
	}
	
	private Set<IDaoElement> getFieldIndexContainer() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<IDaoElement> fic = (Set<IDaoElement>)this.itemDaos.get(new ProcessedItemType(FieldIndexContainerDao.class, DatastoreType.LOCALBUFFER));
		try
		{ 
			if(fic == null) return DatastoreHelper.getItems(DatastoreType.LOCALBUFFER, FieldIndexContainerDao.class); 
		}catch(Exception e) { throw new ResourceRegistryException("",e); }
		return fic;
	}
	
	@Override
	protected void execute(Set<Class<?>> targets) throws ResourceRegistryException
	{
		
		elementCache = CacheBuilder.newBuilder()
				.maximumSize(5000)
				.expireAfterWrite(30, TimeUnit.MINUTES)
				.build();
		
		logger.info( "Executing " + this.type + " plugin: " + this.getClass().getName());
		boolean locked = false;
		Lock writeLock = ResourceRegistry.getContext().getExclusiveLock();
		try
		{
			boolean updateMode = !(targets.contains(FieldDao.class) &&
			targets.contains(SearchableDao.class) && targets.contains(PresentableDao.class));
			RRContext.DatastoreType datastoreType = ResourceRegistry.isInitialBridgingComplete() && updateMode ? RRContext.DatastoreType.LOCAL : RRContext.DatastoreType.LOCALBUFFER;
			List<Field> allFields = getFields();
			List<DataSource> allDataSources = getDataSources();
			Set<IDaoElement> allMetadata = getElementMetadata();
			
			
			Map<String, IDaoElement> metadataMap = new HashMap<String, IDaoElement>();
			for (IDaoElement metaEl : allMetadata){
				if (metadataMap.get(metaEl.getID()) != null){
					logger.warn("metadata element " + metaEl.getID() + " already in map");
				}
				metadataMap.put(metaEl.getID(), metaEl);
			}
			
			Map<String, DataSource> datasourcesMap = new HashMap<String, DataSource>();
			for (DataSource datasource : allDataSources){
				if (datasourcesMap.get(datasource.getID()) != null){
					logger.warn("datasource element " + datasource.getID() + " already in map");
				}
				datasourcesMap.put(datasource.getID(), datasource);
			}
			
			Map<String, Field> fieldIDs = new HashMap<String, Field>();
			Map<String, Field> fieldNames = new HashMap<String, Field>();
			
			logger.info("fields size : " + allFields.size());
			
			for (Field field : allFields){
				fieldIDs.put(field.getID(), field);
				fieldNames.put(field.getName(), field);
				
			}
			
			logger.info("fieldIDs   : " + fieldIDs.keySet());
			logger.info("fieldNames : " + fieldNames.keySet());
			
			
			/*logger.info( "Before plugin execution...");
			for(Field field: allFields){
				logger.info( "3.####################################");
				logger.info( "field ID   : " + field.getID());
				logger.info( "field Name : " + field.getName());
				logger.info( "field Searchables  : " + field.getSearchables());
				logger.info( "field Presentables : " + field.getPresentables());
				logger.info( "3.####################################");
			}
			logger.info( "After plugin execution...OK");*/
			//Replace field names in FieldIndexContainer with field ids, for all published fields
			if(targets.contains(FieldIndexContainerDao.class))
			{
				Set<IDaoElement> elems = getFieldIndexContainer();
				HashSet<Class<?>> purge=new HashSet<Class<?>>();
				purge.add(FieldIndexContainerDao.class);
				DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, purge);
				logger.info("will run update for : " + elems.size() + " elements");
				
				
				Map<String, IDaoElement> elementMap = new HashMap<String, IDaoElement>();
				
				for(IDaoElement el : elems){
					if (elementMap.get(((FieldIndexContainerDao)el).getField()) != null){
						logger.warn("************ element " + ((FieldIndexContainerDao)el).getField() + " already in map");
					}
					elementMap.put(((FieldIndexContainerDao)el).getField(), el);
				}
				
				for(IDaoElement el : elems)
				{
					if(!(el instanceof FieldIndexContainerDao)) continue;
				//	List<Field> fs = Field.getFieldsWithName(false, ((FieldIndexContainerDao)el).getField());
					
					String fieldName = ((FieldIndexContainerDao)el).getField();
					
					logger.info("field update : " + fieldName);
					
					Field f = null;
					f = fieldIDs.get(fieldName);
					
					if (f == null){
						f = fieldNames.get(fieldName);
						
//						if (f == null){
//							logger.info("field : " + fieldName + " not found in db");
//							//continue;
//						}
					}
					
//					Field f = null;
//					for(Field field: allFields)
//					{
//						if(field.getID().equals(((FieldIndexContainerDao)el).getField()))
//						{
//							f = field;
//							break;
//						}
//					}
					
//					if(f == null) //if the field is not found by searching by its id, it might be a newly created field so search also by name
//					{
//						for(Field field : allFields)
//						{
//							if(field.getName().equals(((FieldIndexContainerDao)el).getField()))
//							{
//								f = field;
//								break;
//							}
//						}
//					}
	
					Field updatedField = null;
					//if(updateMode) 
					//{
						updatedField = updateField(f, (FieldIndexContainerDao)el, datasourcesMap, metadataMap, datastoreType, fieldIDs, fieldNames);
						/*logger.info( "updatedField : " + updatedField + ", f : " + f);
						if (updatedField != null){
							logger.info( "updatedFieldName : " + updatedField.getName());
							logger.info( "updatedFieldID : " + updatedField.getID());
						}
						if (f != null) {
							logger.info( "fName : " + f.getName());
							logger.info( "fID : " + f.getID());
						}*/
						//is new field
						if(f==null && updatedField != null) 
						{
							
							FieldIndexContainerDao fic = (FieldIndexContainerDao) elementMap.get(updatedField.getName());
							if (fic != null)
								fic.setField(updatedField.getID());
							
//							for(IDaoElement fic : elems)
//							{
//								if(((FieldIndexContainerDao)fic).getField().equals(updatedField.getName())) {
//										//logger.info( "found match in elems :  " + updatedField.getName());
//										((FieldIndexContainerDao)fic).setField(updatedField.getID());
//								}
//							}
						}
					//}
				}
				DatastoreHelper.bufferItems(elems);
			}
			/*logger.info( "After plugin execution...");
			for(Field field: allFields){
				logger.info( "3.####################################");
				logger.info( "field ID   : " + field.getID());
				logger.info( "field Name : " + field.getName());
				logger.info( "field Searchables  : " + field.getSearchables());
				logger.info( "field Presentables : " + field.getPresentables());
				logger.info( "3.####################################");
			}
			logger.info( "After plugin execution...OK");*/
			
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not align incoming elements", ex);
		}finally
		{
			if(locked) writeLock.unlock();
		}
	}
	
	private static boolean isDeletedSearchableOrPresentable(ElementMetadataDao elMetadata, String fieldID, String searchableID, String locator){
		CacheElement ce = new CacheElement();
		ce.metadataID = elMetadata.getID();
		ce.locator = locator;
		ce.searchableID = searchableID;
		ce.fieldID = fieldID;
		
		Boolean result = elementCache.getIfPresent(ce);
		
		if (result == null){
			result = computeIsDeletedSearchableOrPresentable(elMetadata, fieldID, searchableID, locator);
			elementCache.put(ce, result);
		}
		
		return result;
	}
	
	private static boolean computeIsDeletedSearchableOrPresentable(ElementMetadataDao elMetadata, String fieldID, String searchableID, String locator){
		logger.trace( "elMetadata id : " + elMetadata.getID() + " searchableID  : " + searchableID + " fieldID : " + fieldID);
		logger.trace( "elMetadata id : " + elMetadata.getID() + " DeletedElement property keys   : " + elMetadata.getPropertyKeys());
		logger.trace( "elMetadata id : " + elMetadata.getID() + " DeletedElement property values : " + elMetadata.getPropertyValues());
		//logger.info( "elMetadata id : " + elMetadata.getID() + " " + elMetadata.deepToString());
		 for (String value : elMetadata.getPropertyValues()){
			 if (value.toLowerCase().contains(searchableID.toLowerCase())){
				 logger.trace( "elMetadata id : " + elMetadata.getID() + " found match at value : " + value);
				 return true;
			 }
		 }
		 boolean locatorFound = false;
		 boolean fieldFound = false;
		 
		 
		 for (String value : elMetadata.getPropertyValues()){
			 if (value.toLowerCase().contains(fieldID.toLowerCase())){
				 logger.trace( "elMetadata id : " + elMetadata.getID() + " found match at value : " + value);
				 fieldFound = true;
			 }
		 }
		 
		 for (String value : elMetadata.getPropertyValues()){
			 if (value.toLowerCase().contains(locator.toLowerCase())){
				 logger.trace( "elMetadata id : " + elMetadata.getID() + " found match at value : " + value);
				 locatorFound = true;
			 }
		 }
		 
		 if (fieldFound && locatorFound)
			 return true;
		 
		 if (elMetadata.getID().equalsIgnoreCase(searchableID))
			 return true;
		 
		 return false;
	}
	
	private static String getDeletedID(ElementMetadataDao elMetadata){
		return elMetadata.getID();
		
	}
	
	private Field updateField(Field f, FieldIndexContainerDao el,  Map<String, DataSource> datasourcesMap, Map<String, IDaoElement> metadataMap, DatastoreType datastoreType, Map<String, Field> fieldIDs, Map<String, Field> fieldNames) throws ResourceRegistryException
	{
	//	RRContext.DatastoreType datastoreType = ResourceRegistry.isInitialBridgingComplete() ? RRContext.DatastoreType.DERBY : RRContext.DatastoreType.BUFFER;
		String locator = el.getID().split(":")[0];
	
		Field updatedField = new Field();
		if(f == null) 
		{
//			for(IDaoElement metadata: allMetadata)
//			{
				ElementMetadataDao elMetadata = (ElementMetadataDao) metadataMap.get(el.getField());
				
				if (elMetadata != null && elMetadata.getType().toString().equalsIgnoreCase(ElementMetadata.Type.DeletedField.toString())){
					logger.info( "field with id : " + el.getField() + " has been deleted. Update is not done");
					return null;
				}
				
//				if(elMetadata.getType().toString().equalsIgnoreCase(ElementMetadata.Type.DeletedField.toString()) && (elMetadata.getID().equals(el.getField()))){
//					logger.info( "field with id : " + el.getField() + " has been deleted. Update is not done");
//					return null;
//				}
//				if(elMetadata.getType().equals(ElementMetadata.Type.DeletedField) && (elMetadata.getID().equals(el.getField())))
//					return null;
//			}
			updatedField.setName(el.getField());
			updatedField.store(true, datastoreType);
			logger.info( "Created new field: " + el.getField());
			f = updatedField;
			
			fieldIDs.put(f.getID(), f);
			fieldNames.put(f.getName(), f);
			
		}else
		{
			updatedField = new Field();
			updatedField.setID(f.getID());
			updatedField.setDescription(f.getDescription());
			updatedField.setName(f.getName());
			updatedField.getSearchables().addAll(f.getSearchables());
			updatedField.getPresentables().addAll(f.getPresentables());
		}
		
		boolean found = false;
		boolean update = false;
		
		boolean deletedSearchable = false;
		boolean deletedPresentable = false;
		
		if(el.getFieldType().equals("s"))
		{
			List<Searchable> toBeRemoved = new ArrayList<Searchable>();
			
			outer:for(Searchable s : updatedField.getSearchables())
			{
				for(IDaoElement metadata : metadataMap.values())
				{
					
					ElementMetadataDao elMetadata = (ElementMetadataDao)metadata;
					
					//logger.info( "metadata id :  " + elMetadata.getID() + ", " + elMetadata.getType() + " field : " + f.getID() + " locator : " + locator + " eq : " + elMetadata.getType().toString().equalsIgnoreCase(ElementMetadata.Type.DeletedSearchable.toString()));
					
					if(elMetadata.getType().toString().equalsIgnoreCase(ElementMetadata.Type.DeletedSearchable.toString()) && isDeletedSearchableOrPresentable(elMetadata, f.getID(), s.getID(), locator)){
						logger.info( "Searchable for field: " + f.getName() + " : " + f.getID() + " has been deleted. searchable id :" + s.getID());
						
						
//						Searchable toBeRemoved = new Searchable();
//						toBeRemoved.setID(s.getID());
//						toBeRemoved.load(true);
						logger.info( "Searchables before remove : " + updatedField.getSearchables().size());
						
						for (Searchable ss : updatedField.getSearchables()){
							if (ss.getID().equalsIgnoreCase(s.getID())){
								toBeRemoved.add(ss);
								logger.info( "found what should be deleted. id: " + ss.getID() + " in searchables? : " + updatedField.getSearchables().contains(toBeRemoved));
								
							}
							logger.trace( "ss before remove : " + ss.getID());	
						}
						
						deletedSearchable = true;
						update = true;
						
						continue outer;
					}
				}
				
				if(s.getCollection().equals(el.getCollection()) && s.getLocator().equals(locator))
				{
					found = true;
					break;
				}
			}
			
			if (toBeRemoved.size() == 0)
				logger.trace( " toBeRemoved not found!");
			else{
				int sizeBeforeDelete = updatedField.getSearchables().size();
				
				logger.trace( "removing toBeRemoved with ids : " + toBeRemoved);
				updatedField.getSearchables().removeAll(toBeRemoved);
				
//				for (Searchable s : toBeRemoved){
//					logger.info( "removing toBeRemoved with id : " + s.getID());
//					updatedField.getSearchables().remove(toBeRemoved);
//					//s.delete(true);
//				}
				
				logger.trace( "Searchables after remove : " + updatedField.getSearchables().size());
				if (logger.isTraceEnabled()){
					for (Searchable ss : updatedField.getSearchables()){
						logger.trace( "ss after remove : " + ss.getID());	
					}
				}
				
				int sizeAfterDelete = updatedField.getSearchables().size();
				
				logger.info( "before and after removal : " + sizeBeforeDelete + " - " + sizeAfterDelete);
				
			}
			
			if(found == false && deletedSearchable == false)
			{
				DataSource ds = datasourcesMap.get(locator);
				if (ds == null){
					logger.warn("datasources dont contain : " + locator + ". all datasources : " + datasourcesMap.keySet());
				} else {
					Searchable s = new Searchable();
					s.setCollection(el.getCollection());
					s.setField(f.getID());
					s.setLocator(locator);
					s.setExpression(el.getExpression());
					s.getCapabilities().addAll(ds.getCapabilities());
					s.setDatasourceScopes(ds.getScopes());
					s.store(true, datastoreType);
					f.getSearchables().add(s);
					updatedField.getSearchables().add(s);
					logger.info( "Added new searchable to field " + f.getName() + " (" + f.getID() + ") for collection " + 
							s.getCollection() + " and data source " + s.getLocator());
	//				logger.info( "Searchable Added to datastore : ", datastoreType);
	//				Field newField = Field.getById(true, f.getID());
	//				logger.info( "New field from database");
	//				logger.info( "ID : " + newField.getID());
	//				logger.info( "Name : " + newField.getName());
	//				logger.info( "Searchables : " + newField.getSearchables());
	//				logger.info( "Presentables : " + newField.getPresentables());
						
						
						
					update = true;
				}
				
			}
		}else
		{
			List<Presentable> toBeRemoved = new ArrayList<Presentable>();
			outer:for(Presentable p : updatedField.getPresentables())
			{
				for(IDaoElement metadata : metadataMap.values())
				{
					ElementMetadataDao elMetadata = (ElementMetadataDao)metadata;
					if(elMetadata.getType().toString().equalsIgnoreCase(ElementMetadata.Type.DeletedPresentable.toString()) && isDeletedSearchableOrPresentable(elMetadata, f.getID(), p.getID(), locator)){
						logger.trace( "Presentalbe for field: " + f.getName() + " : " + f.getID() + " has been deleted. presentable id :" + p.getID());
						
//						Presentable toBeRemoved = new Presentable();
//						toBeRemoved.setID(p.getID());
//						toBeRemoved.load(true);
						
//						Presentable toBeRemoved = null;
						logger.info( "Presentables before remove : " + updatedField.getSearchables().size());
						for (Presentable pp : updatedField.getPresentables()){
							if (pp.getID().equalsIgnoreCase(p.getID())){
								toBeRemoved.add(pp);
								logger.info( "found what should be deleted. id: " + pp.getID() + " in presentables? : " + updatedField.getPresentables().contains(toBeRemoved));
							}
							logger.info( "pp before remove : " + pp.getID());	
						}
						logger.trace( "will remove presentable with id : " + p.getID());
//						if (toBeRemoved == null)
//							logger.info( " toBeRemoved not found!");
//						else
//							updatedField.getPresentables().remove(toBeRemoved);
//						
//						logger.info( "Presentables after remove : " + updatedField.getPresentables().size());
//						for (Presentable pp : updatedField.getPresentables()){
//							logger.info( "pp after remove : " + pp.getID());	
//						}
						
						
						deletedPresentable = true;
						update = true;
						
						continue outer;
					}
				}
				if(p.getCollection().equals(el.getCollection()) && p.getLocator().equals(locator))
				{
					found = true;
					break;
				}
			}
		
			if (toBeRemoved.size() == 0)
				logger.trace( " toBeRemoved not found!");
			else{
				int sizeBeforeDelete = updatedField.getPresentables().size();
				
				logger.trace( "removing toBeRemoved with ids : " + toBeRemoved);
				updatedField.getPresentables().removeAll(toBeRemoved);
				
//				for (Presentable p : toBeRemoved){
//					logger.info( "removing toBeRemoved with id : " + p.getID());
//					updatedField.getPresentables().remove(toBeRemoved);
//					//p.delete(true);
//				}
				
				logger.trace( "Presentables after remove : " + updatedField.getPresentables().size());
				if (logger.isTraceEnabled()){
					for (Presentable pp : updatedField.getPresentables()){
						logger.trace( "pp after remove : " + pp.getID());	
					}
				}
				int sizeAfterDelete = updatedField.getPresentables().size();
				
				logger.info( "before and after removal : " + sizeBeforeDelete + " - " + sizeAfterDelete);
				
			}
		
			if(found == false && deletedPresentable == false)
			{
				DataSource ds = datasourcesMap.get(locator);
				if (ds == null){
					logger.warn("datasources dont contain : " + locator + ". all datasources : " + datasourcesMap.keySet());
				} else {
				
					Presentable p = new Presentable();
					p.setCollection(el.getCollection());
					p.setField(f.getID());
					p.setLocator(locator);
					p.setDatasourceScopes(ds.getScopes());
					p.store(true, datastoreType);
					f.getPresentables().add(p);
					updatedField.getPresentables().add(p);
					logger.info( "Added new presentable to field " + f.getName() + " (" + f.getID() + ") for collection " + 
							p.getCollection() + " and data source " + p.getLocator());
					
	//				logger.info( "Added to datastore : ", datastoreType);
	//				Field newField = Field.getById(true, f.getID());
	//				logger.info( "New field from database");
	//				logger.info( "ID : " + newField.getID());
	//				logger.info( "Name : " + newField.getName());
	//				logger.info( "Searchables : " + newField.getSearchables());
	//				logger.info( "Presentables : " + newField.getPresentables());
					
					update = true;
				}
			}
		}
		
		logger.trace( "After Field Update : ");
		logger.trace( "searchables  size : " + f.getSearchables().size());
		logger.trace( "presentables size : " + f.getPresentables().size());
		
		//if(f == null || found == true)
		if(update || deletedPresentable || deletedSearchable){
			
//			logger.info( "before saving updatedField name : ", updatedField.getName());
//			logger.info( "before saving updatedField searchables : ", updatedField.getSearchables());
//			logger.info( "before saving updatedField presentables : ", updatedField.getPresentables());
			updatedField.store(true, datastoreType);
//			logger.info( "*************************************************");
//			logger.info( "updatedField Added to datastore : ", DatastoreType.LOCAL);
//			Field newField = Field.getById(true, updatedField.getID());
//			logger.info( "New field from database");
//			logger.info( "ID : " + newField.getID());
//			logger.info( "Name : " + newField.getName());
//			logger.info( "Searchables : " + newField.getSearchables());
//			logger.info( "Presentables : " + newField.getPresentables());
//			logger.info( "*************************************************");
//			updatedField.store(true, DatastoreType.LOCALBUFFER);
//			logger.info( "updatedField Added to datastore : ", DatastoreType.LOCAL);
//			newField = Field.getById(true, updatedField.getID());
//			logger.info( "New field from database");
//			logger.info( "ID : " + newField.getID());
//			logger.info( "Name : " + newField.getName());
//			logger.info( "Searchables : " + newField.getSearchables());
//			logger.info( "Presentables : " + newField.getPresentables());
//			logger.info( "*************************************************");
		}
		return updatedField;
	}
	
	
	public static Cache<CacheElement, Boolean> elementCache = null;
	
	
	
	
	static class CacheElement {
		String metadataID;
		String fieldID;
		String searchableID;
		String locator;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fieldID == null) ? 0 : fieldID.hashCode());
			result = prime * result
					+ ((locator == null) ? 0 : locator.hashCode());
			result = prime * result
					+ ((metadataID == null) ? 0 : metadataID.hashCode());
			result = prime * result
					+ ((searchableID == null) ? 0 : searchableID.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheElement other = (CacheElement) obj;
			if (fieldID == null) {
				if (other.fieldID != null)
					return false;
			} else if (!fieldID.equals(other.fieldID))
				return false;
			if (locator == null) {
				if (other.locator != null)
					return false;
			} else if (!locator.equals(other.locator))
				return false;
			if (metadataID == null) {
				if (other.metadataID != null)
					return false;
			} else if (!metadataID.equals(other.metadataID))
				return false;
			if (searchableID == null) {
				if (other.searchableID != null)
					return false;
			} else if (!searchableID.equals(other.searchableID))
				return false;
			return true;
		}
		
		
	}
}
