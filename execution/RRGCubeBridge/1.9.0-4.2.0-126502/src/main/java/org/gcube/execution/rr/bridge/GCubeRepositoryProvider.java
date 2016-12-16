package org.gcube.execution.rr.bridge;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.bridge.IRegistryProvider;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.config.StaticConfigurationDao;
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
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

public class GCubeRepositoryProvider implements IRegistryProvider
{
	private static final Logger logger = LoggerFactory
			.getLogger(GCubeRepositoryProvider.class);

	
	public static String RRModelGenericResourceNameDef = "ResourceRegistryModel";
	public static String RRModelGenericResourceSecondaryTypeDef = "ResourceRegistryModel";

	public static String RRModelGenericResourceName = RRModelGenericResourceNameDef;
	public static String RRModelGenericResourceSecondaryType = RRModelGenericResourceSecondaryTypeDef;

	public Set<Class<?>> inMemoryTargets = new HashSet<Class<?>>();
	
	static 
	{
		BridgeHelper.initializeIndexTypes();
	}
	
	@Override
	public void readConfiguration(Properties config)
	{
		if(config == null) return;
		boolean defaultUsed = true;
		String value = null;
		
		if((value = config.getProperty("modelGenericResourceSecondaryType")) != null)
		{
			String secTypeValue = value;
			if((value = config.getProperty("modelGenericResourceName")) != null)
			{
				GCubeRepositoryProvider.RRModelGenericResourceSecondaryType = secTypeValue;
				GCubeRepositoryProvider.RRModelGenericResourceName = value;
				defaultUsed = false;
				logger.info( "Using Model Generic Resource secondary type: " + GCubeRepositoryProvider.RRModelGenericResourceSecondaryType);
				logger.info( "Using Model Generic Resource name: " + GCubeRepositoryProvider.RRModelGenericResourceName);
			}
		}
		if(defaultUsed)
		{
			logger.info( "Using default Model Generic Resource secondary type: " + GCubeRepositoryProvider.RRModelGenericResourceSecondaryType);
			logger.info( "Using default Model Generic Resource name: " + GCubeRepositoryProvider.RRModelGenericResourceName);
		}
		
//		if((value = config.getProperty("updateFieldsOnDataSourceRefresh")) != null)
//		{
//			if(value.equalsIgnoreCase("true")) GCubeRepositoryProvider.UpdateFieldsOnDataSourceRefresh = true;
//			else GCubeRepositoryProvider.UpdateFieldsOnDataSourceRefresh = false;
//		}
		try
		{
			BridgeHelper.initializeIndexTypes(config);
		}catch(ResourceRegistryException e)
		{
			logger.warn( "Could not initialize index types. Defaults will be used", e);
			BridgeHelper.initializeIndexTypes();
		}
	}
	
	@Override
	public boolean isReadPolicySupported(RRContext.ReadPolicy policy) throws ResourceRegistryException
	{
		switch(policy)
		{
			case READ_LOCAL:
			{
				return false;
			}
			case READ_THROUGH:
			{
				return false;
			}
			case REFRESH_AHEAD:
			{
				return ResourceRegistry.getContext().isDatastoreSupported(DatastoreType.LOCAL) && ResourceRegistry.getContext().isDatastoreSupported(DatastoreType.LOCALBUFFER);
			}
			default:
				return false;
		}
	}
	
	@Override
	public boolean isWritePolicySupported(RRContext.WritePolicy policy) throws ResourceRegistryException
	{
		switch(policy)
		{
			case WRITE_THROUGH:
			{
				return false; 
			}
			case WRITE_LOCAL:
			{
				return false;
			}
			case WRITE_BEHIND: 
			{
				return ResourceRegistry.getContext().isDatastoreSupported(DatastoreType.LOCAL) && ResourceRegistry.getContext().isDatastoreSupported(DatastoreType.LOCALBUFFER);
			}
			default:
				return false;
		}
	}
	
	@Override
	public void setInMemoryTargets(Set<Class<?>> items)
	{
		this.inMemoryTargets = items;
	}
	
	@Override
	public void persistDirect(Class<?> item, String id) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("Operation not supported");
	}
	
	@Override
	public void persistDirect(Class<?> items) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("Operation not supported");
	}
	
	@Override
	public void persist(Set<Class<?>> items, Set<String> nonUpdateVOScopes) throws ResourceRegistryException
	{
		logger.info( "starting aligning");
		this.alignOutgoing(items);
		logger.info("starting persisting");
		this.bridgeOutgoing(items, nonUpdateVOScopes);
		logger.info("finished persisting");
	}

	@Override
	public void retrieve(Set<Class<?>> items) throws ResourceRegistryException
	{
		logger.info("starting retrieving");
		this.bridgeIncoming(items);
		logger.info("starting aligning");
		this.alignIncoming(items);
		logger.info("finished retrieving");
	}
	
	@Override
	public void retrieveDirect(Class<?> item, String id) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("Operation not supported");
	}
	
	@Override
	public void retrieveDirect(Class<?> item) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("Operation not supported");
	}
	
	@Override
	public void prefetchInMemoryItems() throws ResourceRegistryException
	{
		BridgeHelper.prefetchInMemoryItems(this.inMemoryTargets);
	}
	
	private void bridgeIncoming(Set<Class<?>> targets) throws ResourceRegistryException
	{
		try
		{
			BridgeHelper.retrieveScopes();
			FieldModel.retrieve();
			
			DatastoreHelper.clear(DatastoreType.LOCALBUFFER, targets);
			
			for(Class<?> include : targets)
			{
				logger.info( "retrieving info from IS for element "+include.getName());
				Set<IDaoElement> items=BridgeHelper.getElement(include);
				logger.info( "buffering information retrieved for element "+include.getName());
				DatastoreHelper.bufferItems(items);
			}
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not bridge incoming elements", ex);
		}
	}
	
	private void alignIncoming(Set<Class<?>> targets) throws ResourceRegistryException
	{
		boolean locked = false;
		Lock writeLock = ResourceRegistry.getContext().getExclusiveLock();
		try
		{
			boolean updateMode = !(targets.contains(FieldDao.class) &&
			targets.contains(SearchableDao.class) && targets.contains(PresentableDao.class));
			//boolean updateFieldsOnDSRefresh = GCubeRepositoryProvider.UpdateFieldsOnDataSourceRefresh && updateMode;
			RRContext.DatastoreType datastoreType = ResourceRegistry.isInitialBridgingComplete() && updateMode ? RRContext.DatastoreType.LOCAL : RRContext.DatastoreType.LOCALBUFFER;
			
			logger.info( "datastore type : " + datastoreType);
			
			List<Field> allFields = Field.getAll(true, DatastoreType.LOCALBUFFER);
			List<DataSource> allDataSources = DataSource.getAll(false, DatastoreType.LOCALBUFFER);
			//Set<IDaoElement> allMetadata = DatastoreHelper.getItems(DatastoreType.LOCALBUFFER, ElementMetadataDao.class);
			
			writeLock.lock(); locked = true;
			List<ElementMetadata> updatedFieldsMetadata = ElementMetadata.getUpdatedFieldsMetadata(true);
			for(ElementMetadata m : updatedFieldsMetadata)
				m.delete(true, DatastoreType.LOCAL);
			writeLock.unlock(); locked = false;
			
			Map<String, Field> fieldIDs = new HashMap<String, Field>();
			Map<String, Field> fieldNames = new HashMap<String, Field>();
			
			logger.info("fields size : " + allFields.size());
			
			for (Field field : allFields){
				fieldIDs.put(field.getID(), field);
				fieldNames.put(field.getName(), field);
			}
			
			logger.info("fieldIDs   : " + fieldIDs);
			logger.info("fieldNames : " + fieldNames);
			
			//Replace field names in FieldIndexContainer with field ids, for all published fields
			if(targets.contains(FieldIndexContainerDao.class))
			{
				Set<IDaoElement> elems = DatastoreHelper.retrieveAll(RRContext.DatastoreType.LOCALBUFFER, FieldIndexContainerDao.class);
				HashSet<Class<?>> purge=new HashSet<Class<?>>();
				purge.add(FieldIndexContainerDao.class);
				DatastoreHelper.clear(RRContext.DatastoreType.LOCALBUFFER, purge);
				for(IDaoElement el : elems)
				{
					if(!(el instanceof FieldIndexContainerDao)) continue;
				//	List<Field> fs = Field.getFieldsWithName(false, ((FieldIndexContainerDao)el).getField());
					Field f = fieldNames.get(((FieldIndexContainerDao)el).getField());
//					for(Field field: allFields)
//					{
//						if(field.getName().equals(((FieldIndexContainerDao)el).getField()))
//						{
//							f = field;
//							break;
//						}
//					}
					if(f!=null)
					{
						((FieldIndexContainerDao)el).setField(f.getID());
					}
					//Field updatedField = null;
					//if(updateFieldsOnDSRefresh) 
					//{
					//	updatedField = updateField(f, (FieldIndexContainerDao)el, allFields, allDataSources, allMetadata, datastoreType);
					//	if(f==null && updatedField != null) ((FieldIndexContainerDao)el).setField(updatedField.getID());
					//}
				}
				DatastoreHelper.bufferItems(elems);
			}
			
			Map<String, DataSource> datasourcesMap = new HashMap<String, DataSource>();
			for (DataSource datasource : allDataSources){
				if (datasourcesMap.get(datasource.getID()) != null){
					logger.warn(" datasource element " + datasource.getID() + " already in map");
				}
				datasourcesMap.put(datasource.getID(), datasource);
			}
			
			logger.info("will save searchables and presentables of " + allFields.size() + " fields");
			int i = 1;
			for(Field f : allFields)
			{
				logger.info("will save searchables and presentables of the field # " + i);
				i++;
				
				for(Searchable s : f.getSearchables()) {
					DataSource ds = datasourcesMap.get(s.getLocator());
					if (ds == null){
						logger.warn("no datasource for : " + s.getLocator());
						continue;
					}
//					for(DataSource d : allDataSources)
//					{
//						if(d.getID().equals(s.getLocator()))
//						{
//							ds = d;
//							break;
//						}
//					}
						logger.debug("### Datasource " + ds.getID() + " scopes : " + ds.getScopes() + " capabilities : " + ds.getCapabilities());
						logger.debug("### Datasource : " + ds.deepToString());
					
					if(ds.getScopes() != null) 
						s.getDatasourceScopes().addAll(ds.getScopes());
				//	s.delete(true, datastoreType);
					Searchable item=new Searchable();
					item.setID(s.getID());
					item.setCollection(s.getCollection());
					item.setField(s.getField());
					item.setLocator(s.getLocator());
					item.setExpression(s.getExpression());
					item.setOrder(s.isOrder());
					//item.getCapabilities().addAll(s.getCapabilities());
					if (ds.getCapabilities() != null)
						item.getCapabilities().addAll(ds.getCapabilities());
					item.setDatasourceScopes(s.getDatasourceScopes());
					
					//System.out.println("Searchable : " + item.deepToString());
					logger.debug("## will save searchable " + item.getID() + " from field : " + f.getID()  + " , " + f.getName() +  " from collection : " + s.getCollection() + " from locator : " + s.getLocator() +   " scopes : " + ds.getScopes());
					item.store(true, datastoreType);
				}
				
				for(Presentable p : f.getPresentables()) {
					DataSource ds = datasourcesMap.get(p.getLocator());
					if (ds == null){
						logger.warn("no datasource for : " + p.getLocator());
						continue;
					}
					
					logger.debug("### Datasource " + ds.getID() + " scopes : " + ds.getScopes() + " capabilities : " + ds.getCapabilities());
					logger.debug("### Datasource : " + ds.deepToString());
					if(ds.getScopes() != null) 
						p.getDatasourceScopes().addAll(ds.getScopes());
				//	s.delete(true, datastoreType);
					Presentable item=new Presentable();
					item.setID(p.getID());
					item.setCollection(p.getCollection());
					item.setField(p.getField());
					item.setLocator(p.getLocator());
					item.setExpression(p.getExpression());
					item.setOrder(p.isOrder());
					item.setPresentationInfo(p.getPresentationInfo());
					item.setDatasourceScopes(p.getDatasourceScopes());
					//System.out.println("Presentable : " + item.deepToString());
					
					logger.debug("## will save presentable " + item.getID() + " from field : " + f.getID()  + " , " + f.getName() +  "f rom collection : " + p.getCollection() + " from locator : " + p.getLocator() +   " scopes : " + ds.getScopes());
					
					item.store(true, datastoreType);
				}
			}
			logger.info("fininshed saving searchables and presentables of " + allFields.size() + " fields");
			
			
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not align incoming elements", ex);
		}finally
		{
			if(locked) writeLock.unlock();
		}
	}
	
	private void alignOutgoing(Set<Class<?>> targets) throws ResourceRegistryException
	{
		
		
	}
	
	private void bridgeOutgoing(Set<Class<?>> targets, Set<String> nonUpdateVOScopes) throws ResourceRegistryException
	{
		try 
		{
			logger.info( "retrieving field info from local");
			Set<IDaoElement> fields = new HashSet<IDaoElement>();
			Set<IDaoElement> searchables = new HashSet<IDaoElement>();
			Set<IDaoElement> presentables = new HashSet<IDaoElement>();
			Set<IDaoElement> metadata = new HashSet<IDaoElement>();
			IDaoElement staticConfig = null;
			
			boolean updateFields = false;
			boolean updateSearchables = false;
			boolean updatePresentables = false;
			boolean updateMetadata = false;
			boolean updateConfig = false;
			
			
			if(targets.contains(FieldDao.class))
			{
				logger.info("retrieving info for "+FieldDao.class.getName());
				fields = DatastoreHelper.retrieveAll(RRContext.DatastoreType.LOCAL, FieldDao.class);
				logger.info("done retrieving info for "+ fields.size() +" "+FieldDao.class.getName());
				updateFields = true;
			}
			if(targets.contains(SearchableDao.class))
			{
				logger.info("retrieving info for "+SearchableDao.class.getName());
				searchables = DatastoreHelper.retrieveAll(RRContext.DatastoreType.LOCAL, SearchableDao.class);
				logger.info("done retrieving info for "+ searchables.size() +" "+SearchableDao.class.getName());
				updateSearchables = true;
			}
			if(targets.contains(PresentableDao.class))
			{
				logger.info("retrieving info for "+PresentableDao.class.getName());
				presentables = DatastoreHelper.retrieveAll(RRContext.DatastoreType.LOCAL, PresentableDao.class);
				logger.info("done retrieving info for "+ presentables.size() +" "+PresentableDao.class.getName());
				updatePresentables = true;
			}
			if(targets.contains(ElementMetadataDao.class))
			{
				logger.info("retrieving info for "+ElementMetadataDao.class.getName());
				metadata = DatastoreHelper.retrieveAll(RRContext.DatastoreType.LOCAL, ElementMetadataDao.class);
				logger.info("done retrieving info for "+ metadata.size() +" "+ElementMetadataDao.class.getName());
				updateMetadata = true;
			}
			if(targets.contains(StaticConfigurationDao.class))
			{
				logger.info("retrieving info for " +StaticConfigurationDao.class.getName());
				Set<IDaoElement> staticConfigs = DatastoreHelper.retrieveAll(RRContext.DatastoreType.LOCAL, StaticConfigurationDao.class);
				logger.info("done retrieving info for " + staticConfigs.size() + " "+StaticConfigurationDao.class.getName());
				if(staticConfigs.size()>1) throw new ResourceRegistryException("Multiple static configuration elements were found");
				if(!staticConfigs.isEmpty()) staticConfig = staticConfigs.iterator().next();
				updateConfig = true;
			}
			
			Set<String> datasourceIDs = DataSource.getAllIds();
			
			List<String> danglingPresentablesIDs = new ArrayList<String>();
			List<String> emptyScopePresentablesIDs = new ArrayList<String>();
			if (presentables != null)
				for (IDaoElement pr : presentables){
					
					Set<String> voScopes = BridgeHelper.getVOScopes(((PresentableDao)pr).getDatasourceScopes());
					logger.info("presentable datasource scopes : " + voScopes);
					
					if (nonUpdateVOScopes.containsAll(voScopes)){
						logger.info("presentable : " + ((PresentableDao)pr).getID() + " has all its scopes in nonUpdateVOScopes");
						emptyScopePresentablesIDs.add(((PresentableDao)pr).getID());
					}
					
					String datasourceID = ((PresentableDao)pr).getLocator();
					if (datasourceIDs.contains(datasourceID) == false)
							danglingPresentablesIDs.add(((PresentableDao)pr).getID());
				}
			
			logger.info("dangling presentables : " + danglingPresentablesIDs);
			logger.info("empty scope presentables : " + emptyScopePresentablesIDs);
			
			List<String> danglingSearchablesIDs = new ArrayList<String>();
			List<String> emptyScopeSearchablesIDs = new ArrayList<String>();
			if (searchables != null)
				for (IDaoElement pr : searchables){
					
					Set<String> voScopes = BridgeHelper.getVOScopes(((SearchableDao)pr).getDatasourceScopes());
					logger.info("searchable datasource scopes : " + voScopes);
					
					if (nonUpdateVOScopes.containsAll(voScopes)){
						logger.info("searchable : " + ((SearchableDao)pr).getID() + " has all its scopes in nonUpdateVOScopes");
						emptyScopeSearchablesIDs.add(((SearchableDao)pr).getID());
					}
					
					
					String datasourceID = ((SearchableDao)pr).getLocator();
					if (datasourceIDs.contains(datasourceID) == false)
						danglingSearchablesIDs.add(((SearchableDao)pr).getID());
				}
			
			logger.info("dangling searchables  : " + danglingSearchablesIDs);
			logger.info("empty scope searchables : " + emptyScopeSearchablesIDs);
			
			for (String danglingSearchableID : danglingSearchablesIDs){
				Searchable s=new Searchable();
				s.setID(danglingSearchableID);
				s.load(true);
				s.delete(true);
			}
			for (String danglingPresentablesID : danglingPresentablesIDs){
				Presentable p=new Presentable();
				p.setID(danglingPresentablesID);
				p.load(true);
				p.delete(true);
			}
			
			if (danglingPresentablesIDs.size() > 0){
				updateMetadata = true;
				updateFields = true;
				updatePresentables = true;
				
				logger.info("will unmark deleted presentables");
				unmarkDeletedPresentables(danglingPresentablesIDs);
			}
			if (danglingSearchablesIDs.size() > 0){
				updateMetadata = true;
				updateFields = true;
				updateSearchables = true;
				
				logger.info("will unmark deleted searchables");
				unmarkDeletedSearchables(danglingSearchablesIDs);
			}
			
			if(!updateFields && !updateSearchables && !updatePresentables && !updateMetadata && !updateConfig)  return;
			
			logger.info( "finished retrieving field info from local");
	
			ResourceRegistryException re = null;
			List<Resource> resources=null;
			
			if(updateFields)
			{
				logger.info( "updating field directory gcube generic resource");
				try
				{
					resources=BridgeHelper.getPublishedFieldResources();
				}catch(Exception ex)
				{
					throw new ResourceRegistryException("could not retrieve remote field directory", ex);
				}
				
				logger.info( "resources      : " + resources);
				if (resources != null)
					logger.info( "resources size : " + resources.size());
				
				if(resources.size()>0)
				{
					for(Resource resource : resources)
					{
						logger.info( "updating field directory resource with id : " + resource.getResourceID());
						logger.info( "---------------------------------------------------");
						logger.info( "Updating resource");
						logger.info( resource.getResourceID());
						logger.info( resource.getName());
						logger.info( resource.getDescription());
						logger.info( resource.getBodyAsString());
						logger.info( "---------------------------------------------------");
						resource.setDescription(Long.toString(new Date().getTime()));
						
						logger.info( "resource with id1 : " + resource.getResourceID());
						
						if (hasNonUpdaterVOScope(resource, nonUpdateVOScopes)){
							logger.info( "resource has a nonVOScope in : " + resource.getScopes() + ". Creating new resource with id : " + resource.getResourceID());
							
							resource = getResourceForNonUpdateScopes(nonUpdateVOScopes, RRModelGenericResourceName, RRModelGenericResourceSecondaryType);
						}
						
						
						logger.info( "resource with id2 : " + resource.getResourceID());
						resource.setDescription(Long.toString(new Date().getTime()));
						
						
						Set<IDaoElement> updatedFields = BridgeHelper.updateFieldList(fields, searchables, presentables, emptyScopeSearchablesIDs, emptyScopePresentablesIDs);
						if (updatedFields.size() == 0){
							logger.info( "Updating resource");
							logger.info( resource.getResourceID());
							logger.info( resource.getName());
							logger.info( "skipping creation fields cause they have empty searchables and presentables");
							
							continue;
						}
						
						
						String serialization=BridgeHelper.buildFieldDirectorySerialization(updatedFields);
						String scopes="(";
						for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
						scopes+=")";
						logger.trace( "updating field directory resource serialization in scope "+scopes+" to :\n"+serialization);
						
						addSerializationToResource(resource, serialization);
						try
						{
							BridgeHelper.publishFieldResource(resource, false, nonUpdateVOScopes);
						}catch(Exception ex)
						{
							throw new ResourceRegistryException("could not publish remote field profile", ex);
						}
					}
				}
				else
				{
					Resource resource = new Resource();
					logger.info( "creating field directory resource. new id : " + resource.getResourceID());
//					try
//					{
//						resource =GHNContext.getImplementation(GenericResource.class);
//					}catch(Exception ex)
//					{
//						throw new ResourceRegistryException("could not retrieve generic resource instance", ex);
//					}
					//resource.addScope(BridgeHelper.getFieldModelScopes());
//					for (GCUBEScope sc : BridgeHelper.getFieldModelScopes()){
//						resource.profile().addScope(sc.toString());
//						//resource.addScope(BridgeHelper.getFieldModelScopes());
//					}
					
					resource.setName(RRModelGenericResourceName);
					resource.setType(RRModelGenericResourceSecondaryType);
					resource.setDescription(Long.toString(new Date().getTime()));
					
					Set<IDaoElement> updatedFields = BridgeHelper.updateFieldList(fields, searchables, presentables, emptyScopeSearchablesIDs, emptyScopePresentablesIDs);
					if (updatedFields.size() == 0){
						logger.info( "Adding resource");
						logger.info( resource.getResourceID());
						logger.info( resource.getName());
						logger.info( "skipping creation fields cause they have empty searchables and presentables");
						
					} else {
						
						String serialization=BridgeHelper.buildFieldDirectorySerialization(updatedFields);
						String scopes="(";
						for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
						scopes+=")";
						logger.trace( "adding field directory resource serialization in scope "+scopes+" to :\n"+serialization);
						
						
						addSerializationToResource(resource, serialization);
						try
						{
							logger.info( "Adding NEW resource : " + resource);
							BridgeHelper.publishFieldResource(resource, true, nonUpdateVOScopes);
							logger.info( "Adding NEW resource done ");
						}catch(Exception ex)
						{
							throw new ResourceRegistryException("could not publish remote field profile", ex);
						}
					}
				}
				logger.info( "finished updating field directory gcube generic resource");
		
				List<ElementMetadata> deletedFieldsMetadata = ElementMetadata.getDeletedFieldsMetadata(true);
				Set<String> deletedFieldIds = new HashSet<String>();
				for(ElementMetadata m : deletedFieldsMetadata)
				{
					try
					{
						resources=null;
						try
						{
							resources=BridgeHelper.getPublishedFieldResourcesForField(m.getProperties().get("id"));
						}catch(Exception ex)
						{
							throw new ResourceRegistryException("could not retrieve remote field profile", ex);
						}
						if(resources.size()>0)
						{
							for(Resource resource : resources)
							{
								String scopes="(";
								for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
								scopes+=")";
								logger.trace( "deleting resource for field " + m.getProperties().get("id") + " in scope "+ scopes);
								try
								{
									BridgeHelper.deleteFieldResource(resource, nonUpdateVOScopes);
									deletedFieldIds.add(m.getProperties().get("id"));
								}catch(Exception ex)
								{
									throw new ResourceRegistryException("could not delete remote field profile", ex);
								}
							}
						}
						m.delete(true, DatastoreType.LOCAL);
					}catch(ResourceRegistryException e)
					{
						logger.warn( "Could not delete remote field profiles");
						re = e;
					}
				}
			
				List<ElementMetadata> updatedFieldsMetadata = ElementMetadata.getUpdatedFieldsMetadata(true);
				Set<String> updatedFieldIds = new HashSet<String>();
				for(ElementMetadata m : updatedFieldsMetadata)
					updatedFieldIds.add(m.getProperties().get("id"));
				
				
				logger.info( "Updating fields");
				logger.info( "updatedFieldIds " + updatedFieldIds);
				
				
				Set<String> deletedSearchables = getDeletedSearchables();
				deletedSearchables.addAll(danglingSearchablesIDs);
				deletedSearchables.addAll(emptyScopeSearchablesIDs);
				Set<String> deletedPresentables =  getDeletedPresentables();
				deletedPresentables.addAll(danglingPresentablesIDs);
				deletedPresentables.addAll(emptyScopePresentablesIDs);
				
				for(IDaoElement f : fields)
				{
					try 
					{
						FieldDao field = (FieldDao)f;
						logger.info( "1. updating gcube generic resource for field " + field.getName() + " (" + field.getID() + ")");
						// if(!updatedFieldIds.contains(field.getID())) continue; //field has not been updated, ignore
						if(deletedFieldIds.contains(f.getID())) continue;      //field has been deleted
						logger.info( "2. updating gcube generic resource for field " + field.getName() + " (" + field.getID() + ")");
						resources=null;
						try
						{
							resources=BridgeHelper.getPublishedFieldResourcesForField(field.getID());
						}catch(Exception ex)
						{
							throw new ResourceRegistryException("could not retrieve remote field profile", ex);
						}
						
						logger.info( "resources      : " + resources);
						if (resources != null)
							logger.info( "resources size : " + resources.size());
						
						if(resources.size()>0)
						{
							for(Resource resource : resources)
							{
								logger.info( "updating field resource with id : " + resource.getResourceID());
								
								logger.info( "resource with id1 : " + resource.getResourceID());
								Node bodyNode = resource.getBody();								
								if (hasNonUpdaterVOScope(resource, nonUpdateVOScopes)){
									logger.info( "resource has a nonVOScope in : " + resource.getScopes() + ". Creating new resource with id : " + resource.getResourceID());
									
									resource = getResourceForNonUpdateScopes(nonUpdateVOScopes, RRModelGenericResourceName+"."+field.getID(), RRModelGenericResourceSecondaryType);
								}
								
								resource.setDescription(Long.toString(new Date().getTime()));
								
								logger.info( "resource with id2 : " + resource.getResourceID());
								
								
								TransformerFactory transFactory = TransformerFactory.newInstance();
								Transformer transformer = transFactory.newTransformer();
								StringWriter buffer = new StringWriter();
								transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
								transformer.transform(new DOMSource(bodyNode),
								      new StreamResult(buffer));
								String bodySerialization = buffer.toString();
								
								
								logger.trace("serialization1 : " + bodySerialization);
								
								
								if (BridgeHelper.shouldUpdateField(field, searchables, presentables, emptyScopeSearchablesIDs, emptyScopePresentablesIDs) == false){
									logger.info( "1.####################################");
									logger.info( "field ID   : " + field.getID());
									logger.info( "field Name : " + field.getName());
									logger.info( "field " + field.getID() + " - " + field.getName() +  "has no presentables or searchable. skipping");
									//continue;
									logger.info("field will be emptied");
									//BridgeHelper.deleteFieldResource(resource, nonUpdateVOScopes);
								}
								
								String serialization=BridgeHelper.updateFieldSerialization(bodySerialization, field,  searchables, presentables,
										updateFields, updateSearchables, updatePresentables, deletedSearchables, deletedPresentables);
								
								logger.info( "1.####################################");
								logger.info( "field ID   : " + field.getID());
								logger.info( "field Name : " + field.getName());
								logger.info( serialization);
								logger.info( "field Searchables  : " + field.getSearchables());
								logger.info( "field Presentables : " + field.getPresentables());
//								logger.info( "searchables  : " + searchables);
//								logger.info( "presentables : " + presentables);
//								logger.info( "updateFields  : " + updateFields);
//								logger.info( "updateSearchables : " + updateSearchables);
//								logger.info( "updatePresentables : " + updatePresentables);
								
								logger.info( "1.####################################");
								
								String scopes="(";
								for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
								scopes+=")";
								logger.trace( "updating resource serialization for field " + field.getName() + " (" + field.getID() + ")" + " in scope "+scopes+" to :\n"+serialization);
								
								addSerializationToResource(resource, serialization);
								try
								{
									BridgeHelper.publishFieldResource(resource, false, nonUpdateVOScopes);
								}catch(Exception ex)
								{
									throw new ResourceRegistryException("could not publish remote field profile", ex);
								}
							}
						}
						else
						{
							Resource resource = new Resource();
							logger.info( "creating field resource. New  id : " + resource.getResourceID());
//							try
//							{
//								resource =GHNContext.getImplementation(GenericResource.class);
//							}catch(Exception ex)
//							{
//								throw new ResourceRegistryException("could not retrieve generic resource instance", ex);
//							}
//							resource.addScope(BridgeHelper.getFieldModelScopes());
							resource.setName(RRModelGenericResourceName+"."+field.getID());
							resource.setType(RRModelGenericResourceSecondaryType);
							resource.setDescription(Long.toString(new Date().getTime()));
							
							if (BridgeHelper.shouldUpdateField(field, searchables, presentables, emptyScopeSearchablesIDs, emptyScopePresentablesIDs) == false){
								logger.info( "2.####################################");
								logger.info( "field ID   : " + field.getID());
								logger.info( "field Name : " + field.getName());
								logger.info( "field " + field.getID() + " - " + field.getName() +  "has no presentables or searchable. skipping");
								continue;
							}
							String serialization=BridgeHelper.buildFieldSerialization(field,searchables,presentables, deletedSearchables, deletedPresentables);
							
							logger.info( "2.####################################");
							logger.info( "field ID   : " + field.getID());
							logger.info( "field Name : " + field.getName());
							logger.info( serialization);
							logger.info( "field Searchables  : " + field.getSearchables());
							logger.info( "field Presentables : " + field.getPresentables());
//							logger.info( "searchables  : " + searchables);
//							logger.info( "presentables : " + presentables);
//							logger.info( "updateFields  : " + updateFields);
//							logger.info( "updateSearchables : " + updateSearchables);
//							logger.info( "updatePresentables : " + updatePresentables);
							logger.info( "2.####################################");
							
							
							String scopes="(";
							for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
							scopes+=")";
							logger.trace( "adding resource serialization for field " + field.getName() + " (" + field.getID() + ")" + " in scope "+scopes+" to :\n"+serialization);
							addSerializationToResource(resource, serialization);
							try
							{
								BridgeHelper.publishFieldResource(resource, true, nonUpdateVOScopes);
							}catch(Exception ex)
							{
								throw new ResourceRegistryException("could not publish remote field profile", ex);
							}
						}
						logger.info( "finished updating gcube generic resource");
					}
					catch(ResourceRegistryException e) 
					{ 
						re = e;
						logger.warn( "Error creating/updating gcube generic resource for field " + ((FieldDao)f).getName() + "(" + ((FieldDao)f).getID() + ")");  
					}
				}
			}
			
			if(updateMetadata)
			{
				logger.info( "updating element metadata gcube generic resource");
				resources=null;
				try
				{
					resources=BridgeHelper.getPublishedMetadataResources();
				}catch(Exception ex)
				{
					throw new ResourceRegistryException("could not retrieve remote element metadata", ex);
				}
				if(resources.size()>0)
				{
					for(Resource resource : resources)
					{
						logger.info( "updating metadata resource. id : " + resource.getResourceID());
						
						logger.info( "recource id1 : " + resource.getResourceID());
						
						if (hasNonUpdaterVOScope(resource, nonUpdateVOScopes)){
							logger.info( "resource has a nonVOScope in : " + resource.getScopes() + ". Creating new resource with id : " + resource.getResourceID());
							resource = getResourceForNonUpdateScopes(nonUpdateVOScopes, RRModelGenericResourceName+".Metadata", RRModelGenericResourceSecondaryType);
						}
						logger.info( "recource id2 : " + resource.getResourceID());
						resource.setDescription(Long.toString(new Date().getTime()));
						
						
						String serialization=BridgeHelper.buildElementMetadataSerialization(metadata);
						String scopes="(";
						for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
						scopes+=")";
						logger.trace( "updating element metadata resource serialization in scope "+scopes+" to :\n"+serialization);
						addSerializationToResource(resource, serialization);
						try
						{
							BridgeHelper.publishFieldResource(resource, false, nonUpdateVOScopes);
						}catch(Exception ex)
						{
							throw new ResourceRegistryException("could not publish remote element metadata", ex);
						}
					}
				}
				else
				{
					Resource resource = new Resource();
					logger.info( "creating metadata resource. new id : " + resource.getResourceID());
//					try
//					{
//						resource =GHNContext.getImplementation(GenericResource.class);
//					}catch(Exception ex)
//					{
//						throw new ResourceRegistryException("could not retrieve generic resource instance", ex);
//					}
//					resource.addScope(BridgeHelper.getFieldModelScopes());
					resource.setName(RRModelGenericResourceName+".Metadata");
					resource.setType(RRModelGenericResourceSecondaryType);
					resource.setDescription(Long.toString(new Date().getTime()));
					String serialization=BridgeHelper.buildElementMetadataSerialization(metadata);
					String scopes="(";
					for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
					scopes+=")";
					logger.trace( "adding element metadata resource serialization in scope "+scopes+" to :\n"+serialization);
					addSerializationToResource(resource, serialization);
					try
					{
						BridgeHelper.publishFieldResource(resource, true, nonUpdateVOScopes);
					}catch(Exception ex)
					{
						throw new ResourceRegistryException("could not publish remote field profiles", ex);
					}
				}
				logger.info( "finished updating element metadata gcube generic resource");	
				
			}
			
			if(updateConfig)
			{
				logger.info( "updating static configuration gcube generic resource");
				resources=null;
				try
				{
					resources=BridgeHelper.getPublishedStaticConfigResources();
				}catch(Exception ex)
				{
					throw new ResourceRegistryException("could not retrieve remote static configuration", ex);
				}
				if(resources.size()>0)
				{
					for(Resource resource : resources)
					{
						logger.info( "updating static config resource. id : " + resource.getResourceID());
						logger.info( "resource id1 :" + resource.getResourceID());
						if (hasNonUpdaterVOScope(resource, nonUpdateVOScopes)){
							logger.info( "resource has a nonVOScope in : " + resource.getScopes() + ". Creating new resource with id : " + resource.getResourceID());
							resource = getResourceForNonUpdateScopes(nonUpdateVOScopes, RRModelGenericResourceName+".StaticConfig", RRModelGenericResourceSecondaryType);
						}
						logger.info( "resource id2 :" + resource.getResourceID());
						
						resource.setDescription(Long.toString(new Date().getTime()));
						String serialization=BridgeHelper.buildStaticConfigSerialization(staticConfig);
						String scopes="(";
						for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
						scopes+=")";
						logger.trace( "updating static configuration resource serialization in scope "+scopes+" to :\n"+serialization);
						addSerializationToResource(resource, serialization);
						try
						{
							BridgeHelper.publishFieldResource(resource, false, nonUpdateVOScopes);
						}catch(Exception ex)
						{
							throw new ResourceRegistryException("could not publish remote element metadata", ex);
						}
					}
				}
				else
				{
					Resource resource = new Resource();
					logger.info( "creating static config resource. new id : " + resource.getResourceID());
//					try
//					{
//						resource =GHNContext.getImplementation(GenericResource.class);
//					}catch(Exception ex)
//					{
//						throw new ResourceRegistryException("could not retrieve generic resource instance", ex);
//					}
					//resource.addScope(BridgeHelper.getFieldModelScopes());
					resource.setName(RRModelGenericResourceName+".StaticConfig");
					resource.setType(RRModelGenericResourceSecondaryType);
					resource.setDescription(Long.toString(new Date().getTime()));
					String serialization=BridgeHelper.buildStaticConfigSerialization(staticConfig);
					String scopes="(";
					for(String s : BridgeHelper.getFieldModelScopes()) scopes+=s+" ";
					scopes+=")";
					logger.trace( "adding static configuration resource serialization in scope "+scopes+" to :\n"+serialization);
					addSerializationToResource(resource, serialization);
					try
					{
						BridgeHelper.publishFieldResource(resource, true, nonUpdateVOScopes);
					}catch(Exception ex)
					{
						throw new ResourceRegistryException("could not publish remote static configuration", ex);
					}
				}
				logger.info( "finished updating static configuration gcube generic resource");	
				
			}
			
			
			if(re!=null)
				throw re;
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not bridge outgoing elements", ex);
		}
	}

	private static void addSerializationToResource(Resource resource,
			String serialization) throws SAXException, IOException,
			ParserConfigurationException, FactoryConfigurationError {
		
		//Element newBody = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(serialization.getBytes())).getDocumentElement();
		
		Node newBody = XMLConverter.stringToNode(serialization);
		
		resource.setBody(newBody);
		
//		if (resource.getBody() == null || resource.getBody().getOwnerDocument() == null)
//			resource.setBody(newBody);
//		else {
//			
//			Node importedNode = resource.getBody().getOwnerDocument().importNode(newBody, true);
//			resource.getBody().appendChild(importedNode);
//		}
	}
	
	
	boolean hasNonUpdaterVOScope(Resource resource, Set<String> nonUpdaterVOScopes){
//		if (resource.scopes().containsAll(nonUpdaterVOScopes))
//			return true;
		
		logger.debug("resource scopes : "  + resource.getScopes());
		logger.debug("scope           : "  + nonUpdaterVOScopes);
		
		for (String scope :nonUpdaterVOScopes)
			if (resource.getScopes().contains(scope))
				return true;
		return false;
	}
	
	void unmarkDeletedSearchables(List<String> searchables){
		List<ElementMetadata> deletedMetadata = new ArrayList<ElementMetadata>();
		try {
			List<ElementMetadata> deletedSearchables = ElementMetadata.getDeletedSearchablesMetadata(true);
			logger.info( "deleted searchables elementes : " + deletedSearchables.size());
			
			for (ElementMetadata em : deletedSearchables){
				if (em == null){
					continue;
				}
					
				if (em.getProperties() == null){
					logger.warn( "em : " + em.getID() + " has no properties");
					continue;
				}
				
				if (em.getProperties().get("searchable_id") == null){
					logger.warn( "em : " + em.getID() + " has no searchable_id in properties");
					continue;
				}
				
				String[] valParts = em.getProperties().get("searchable_id").split("#");
				if (valParts == null || valParts.length == 0){
					logger.warn( "em : " + em.getID() + " searchable_id has no #");
					continue;
				}
				String searchableID = valParts[valParts.length - 1];
				if (searchables.contains(searchableID))
					deletedMetadata.add(em);
			}
			logger.warn( "found : " + deletedMetadata.size() + " out of : " + searchables.size());
			for (ElementMetadata em : deletedMetadata){
				try {
					em.delete(true);
				} catch (Exception e) {
					logger.warn( "error deleting element metadata : " + em.getID());
				}
			}
			
		} catch (ResourceRegistryException e) {
			logger.warn( "error in unmarking deleted searchables", e);
		}
		
	}
	
	
	
	Set<String> getDeletedSearchables(){
		Set<String> deleted = new HashSet<String>();
		try {
			List<ElementMetadata> deletedSearchables = ElementMetadata.getDeletedSearchablesMetadata(true);
			logger.info( "deleted searchables elementes : " + deletedSearchables.size());
			
			for (ElementMetadata em : deletedSearchables){
				if (em == null){
					continue;
				}
					
				if (em.getProperties() == null){
					logger.warn( "em : " + em.getID() + " has no properties");
					continue;
				}
				
				if (em.getProperties().get("searchable_id") == null){
					logger.warn( "em : " + em.getID() + " has no searchable_id in properties");
					continue;
				}
				
				String[] valParts = em.getProperties().get("searchable_id").split("#");
				if (valParts == null || valParts.length == 0){
					logger.warn( "em : " + em.getID() + " searchable_id has no #");
					continue;
				}
				deleted.add(valParts[valParts.length - 1]);
			}
			
		} catch (ResourceRegistryException e) {
			logger.warn( "error in deleted searchables", e);
		}
		
		logger.info( "deleted searchables : " + deleted);
		return deleted;	
	}
	
	void unmarkDeletedPresentables(List<String> presentables){
		List<ElementMetadata> deletedMetadata = new ArrayList<ElementMetadata>();
		try {
			List<ElementMetadata> deletedPresentables = ElementMetadata.getDeletedPresentablesMetadata(true);
			logger.info( "deleted presentables elements : " + deletedPresentables.size());
			
			for (ElementMetadata em : deletedPresentables){
				if (em == null){
					continue;
				}
					
				if (em.getProperties() == null){
					logger.warn( "em : " + em.getID() + " has no properties");
					continue;
				}
				
				if (em.getProperties().get("presentable_id") == null){
					logger.warn( "em : " + em.getID() + " has no presentable_id in properties");
					continue;
				}
				
				String[] valParts = em.getProperties().get("presentable_id").split("#");
				if (valParts == null || valParts.length == 0){
					logger.warn( "em : " + em.getID() + " presentable_id has no #");
					continue;
				}
				String presentableID = valParts[valParts.length - 1];
				if (presentables.contains(presentableID))
					deletedMetadata.add(em);
			}
			logger.warn( "found : " + deletedMetadata.size() + " out of : " + presentables.size());
			for (ElementMetadata em : deletedMetadata){
				try {
					em.delete(true);
				} catch (Exception e) {
					logger.warn( "error deleting element metadata : " + em.getID());
				}
			}
			
		} catch (ResourceRegistryException e) {
			logger.warn( "error in unmarking deleted presentables", e);
		}
		
	}
	
	Set<String> getDeletedPresentables(){
		Set<String> deleted = new HashSet<String>();
		try {
			List<ElementMetadata> deletedPresentables = ElementMetadata.getDeletedPresentablesMetadata(true);
			logger.info( "deleted presentables elements : " + deletedPresentables.size());
			
			for (ElementMetadata em : deletedPresentables){
				if (em == null){
					continue;
				}
					
				if (em.getProperties() == null){
					logger.warn( "em : " + em.getID() + " has no properties");
					continue;
				}
				
				if (em.getProperties().get("presentable_id") == null){
					logger.warn( "em : " + em.getID() + " has no presentable_id in properties");
					continue;
				}
				
				String[] valParts = em.getProperties().get("presentable_id").split("#");
				if (valParts == null || valParts.length == 0){
					logger.warn( "em : " + em.getID() + " presentable_id has no #");
					continue;
				}
				deleted.add(valParts[valParts.length - 1]);
			}
			
		} catch (ResourceRegistryException e) {
			logger.warn( "error in deleted presentables", e);
		}
		logger.info( "deleted presentables : " + deleted);
		return deleted;	
	}
	
	static Set<String> difference(Set<String> set1, Set<String> set2){
		Set<String> newSet = Sets.newHashSet(set1);
		newSet.removeAll(set2);
		
		return newSet;
	}
	
	static boolean checkIfExists(String resourceName, String resourceType, Set<String> scopes){
		return BridgeHelper.checkIfExists(resourceName, resourceType, scopes);
	}
	
	
	static Resource getResourceForNonUpdateScopes(Set<String> nonUpdateVOScopes, String resourceName, String resourceType) throws Exception{
		Resource resource = null;
		
		Set<String> tobeupdatedScopes = difference(Sets.newHashSet(BridgeHelper.getFieldModelScopes()), nonUpdateVOScopes);
		if (checkIfExists(resourceName, resourceType, tobeupdatedScopes)){
			logger.info("resource has already been created for " + resourceName + " for scopes : " + tobeupdatedScopes);
			
			resource = BridgeHelper.getResourceByNameAndType(resourceName, resourceType, tobeupdatedScopes);
			resource.setDescription(Long.toString(new Date().getTime()));
		} else {
			logger.info("resource does not exist for scopes : " + tobeupdatedScopes);

			resource = new Resource();								

			resource.setBody(null);

			resource.setName(resourceName);
			resource.setType(resourceType);
			resource.setDescription(Long.toString(new Date().getTime()));
		}
		
		return resource;
	}
	 
}
