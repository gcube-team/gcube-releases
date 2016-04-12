package org.gcube.execution.rr.bridge;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.config.StaticConfigurationDao;
import gr.uoa.di.madgik.rr.element.data.DataCollection;
import gr.uoa.di.madgik.rr.element.data.DataCollectionDao;
import gr.uoa.di.madgik.rr.element.data.DataLanguage;
import gr.uoa.di.madgik.rr.element.execution.ExecutionServerDao;
import gr.uoa.di.madgik.rr.element.execution.ExecutionServiceDao;
import gr.uoa.di.madgik.rr.element.execution.SearchServiceDao;
import gr.uoa.di.madgik.rr.element.execution.WorkflowServiceDao;
import gr.uoa.di.madgik.rr.element.functionality.Functionality;
import gr.uoa.di.madgik.rr.element.functionality.FunctionalityDao;
import gr.uoa.di.madgik.rr.element.infra.HostingNode;
import gr.uoa.di.madgik.rr.element.infra.HostingNodeDao;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadata;
import gr.uoa.di.madgik.rr.element.metadata.ElementMetadataDao;
import gr.uoa.di.madgik.rr.element.search.Field;
import gr.uoa.di.madgik.rr.element.search.FieldDao;
import gr.uoa.di.madgik.rr.element.search.Presentable;
import gr.uoa.di.madgik.rr.element.search.PresentableDao;
import gr.uoa.di.madgik.rr.element.search.Searchable;
import gr.uoa.di.madgik.rr.element.search.SearchableDao;
import gr.uoa.di.madgik.rr.element.search.index.DataSource;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceDao;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceService;
import gr.uoa.di.madgik.rr.element.search.index.DataSourceServiceDao;
import gr.uoa.di.madgik.rr.element.search.index.FTIndex;
import gr.uoa.di.madgik.rr.element.search.index.FTIndexDao;
import gr.uoa.di.madgik.rr.element.search.index.FTIndexService;
import gr.uoa.di.madgik.rr.element.search.index.FTIndexServiceDao;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainer;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao;
import gr.uoa.di.madgik.rr.element.search.index.OpenSearchDataSource;
import gr.uoa.di.madgik.rr.element.search.index.OpenSearchDataSourceDao;
import gr.uoa.di.madgik.rr.element.search.index.OpenSearchDataSourceService;
import gr.uoa.di.madgik.rr.element.search.index.OpenSearchDataSourceServiceDao;
import gr.uoa.di.madgik.rr.element.search.index.SruConsumer;
import gr.uoa.di.madgik.rr.element.search.index.SruConsumerDao;
import gr.uoa.di.madgik.rr.element.search.index.SruConsumerService;
import gr.uoa.di.madgik.rr.element.search.index.SruConsumerServiceDao;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.execution.rr.bridge.scope.ICScopeHelper;
import org.gcube.execution.rr.bridge.scope.ScopeHelper;
import org.gcube.execution.rr.configuration.ConfigurationProviderLoader;
import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.helpers.XPathEvaluator;
import org.gcube.rest.commons.resourceawareservice.resources.GeneralResource;
import org.gcube.rest.commons.resourceawareservice.resources.HostNode;
import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.commons.resourceawareservice.resources.SerInstance;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BridgeHelper
{
	private static final Logger logger = LoggerFactory
			.getLogger(BridgeHelper.class);
	
	private static List<String> scopes = null;
	private static List<String> searchSystemScopes = null;
	private static Set<IDaoElement> searchSystemServices = null;
	
	
	
	
	
	
	static ScopeHelper scopeHelper = new ICScopeHelper();
	
	
	public static void initializeIndexTypes()
	{
		DataSource.clearSubTypes();
		DataSource.addSubType(DataSource.Type.FullTextIndex, FTIndex.class, FTIndexDao.class);
//		DataSource.addSubType(DataSource.Type.ForwardIndex, FWIndex.class, FWIndexDao.class);
//		DataSource.addSubType(DataSource.Type.GeoIndex, GeoIndex.class, GeoIndexDao.class);
		DataSource.addSubType(DataSource.Type.OpenSearch, OpenSearchDataSource.class, OpenSearchDataSourceDao.class);
		
		DataSource.addSubType(DataSource.Type.SruConsumer, SruConsumer.class, SruConsumerDao.class);
		
		
		DataSourceService.clearSubTypes();
		DataSourceService.addSubType(DataSourceService.Type.FullTextIndex, FTIndexService.class, FTIndexServiceDao.class);
//		DataSourceService.addSubType(DataSourceService.Type.ForwardIndex, FWIndexService.class, FWIndexServiceDao.class);
//		DataSourceService.addSubType(DataSourceService.Type.GeoIndex, GeoIndexService.class, GeoIndexServiceDao.class);
		DataSourceService.addSubType(DataSourceService.Type.OpenSearch, OpenSearchDataSourceService.class, OpenSearchDataSourceServiceDao.class);
		
		DataSourceService.addSubType(DataSourceService.Type.SruConsumer, SruConsumerService.class, SruConsumerServiceDao.class);
		
	}
	
	@SuppressWarnings({ "unused", "unchecked" })
	public static void initializeIndexTypes(Properties config) throws ResourceRegistryException
	{
		try 
		{
			int count=Integer.parseInt(config.getProperty("dataSourceSubTypesCount","0"));
			if(count!=0) 
			{
				DataSource.clearSubTypes();
				Set<String> res=new HashSet<String>(count);
				for(int i=0;i<count;i+=1)
				{
					String type, key, value;
					if((type = config.getProperty("dataSourceSubTypesType."+i)) == null) throw new ResourceRegistryException("Could not read datasource type #"+i);
					if((key = config.getProperty("dataSourceSubTypesKey."+i)) == null) throw new ResourceRegistryException("Could not read datasource class name #"+i);
					if((value = config.getProperty("dataSourceSubTypesValue."+i)) ==  null) throw new ResourceRegistryException("Could not read datasource dao class name #"+i);
					DataSource.addSubType(DataSource.Type.valueOf(type), (Class<? extends DataSource>)Class.forName(key), 
					(Class<? extends DataSourceDao>)Class.forName(value));
					logger.info("Adding DataSource type: " + type + "-" + key + "-" + value);
				}
			}
			count=Integer.parseInt(config.getProperty("dataSourceServiceSubTypesCount", "0"));
			if(count!=0)
			{
				DataSourceService.clearSubTypes();
				Set<String> res=new HashSet<String>(count);
				for(int i=0;i<count;i+=1)
				{
					String type, key, value;
					if((type = config.getProperty("dataSourceServiceSubTypesType."+i)) == null) throw new ResourceRegistryException("Could not read datasource service type #"+i);
					if((key = config.getProperty("dataSourceServiceSubTypesKey."+i)) == null) throw new ResourceRegistryException("Could not read datasource service class name #"+i);
					if((value = config.getProperty("dataSourceServiceSubTypesValue."+i)) ==  null) throw new ResourceRegistryException("Could not read datasource service dao class name #"+i);
					DataSourceService.addSubType(DataSourceService.Type.valueOf(type), (Class<? extends DataSourceService>)Class.forName(key), 
					(Class<? extends DataSourceServiceDao>)Class.forName(value));
					logger.info("Adding DataSource service type: " + type + "-" + key + "-" + value);
				}
			}
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Error while initializing datasource types", e);
		}
	}
	
	public static void retrieveScopes() throws Exception
	{		
		if(isClientMode())
			retrieveScopesOffline();
		else
			retrieveScopesOnline();
		
		if(logger.isInfoEnabled())
		{
			StringBuilder logScopes = new StringBuilder();
			for(String scope : BridgeHelper.scopes)
			{
				logScopes.append(scope.toString());
				logScopes.append(" ");
			}
			logger.info("Scopes: " + logScopes.toString());
		}
		searchSystemServices = retrieveSearchService();
		Set<String> searchSystemScopeSet = new HashSet<String>();
		for(IDaoElement searchSystemService : searchSystemServices)
			searchSystemScopeSet.addAll(((SearchServiceDao)searchSystemService).scopes);
		searchSystemScopes = new ArrayList<String>();
		
		logger.info("searchSystemScopeSet contains : " + searchSystemScopeSet);
		for(String s : searchSystemScopeSet)
			searchSystemScopes.add(s);
	}
	
	public static List<String> getFieldModelScopes() throws Exception
	{
		Set<String> fieldScopes = new HashSet<String>();
		for(String scope : BridgeHelper.searchSystemScopes)
		{
			if (scopeHelper.isInfraScope(scope))
			{
				logger.info(scope + " is infra scope");
				continue;
			}
			if(scopeHelper.isVOScope(scope))
				logger.info(scope + " is a VO scope");
			else if (scopeHelper.isVREScope(scope))
				logger.info(scope + " is a VRE scope. Will add : " + scopeHelper.getEnclosingScope(scope));
				
			if(scopeHelper.isVOScope(scope))
				fieldScopes.add(scope);
			else if (scopeHelper.isVREScope(scope))
				fieldScopes.add(scopeHelper.getEnclosingScope(scope));
		}
		List<String> gCubeScopes = new ArrayList<String>();
		
		logger.info("#### searchSystemScopes : " + BridgeHelper.searchSystemScopes);
		logger.info("#### FieldModelScopes   : " + fieldScopes);
		
		for(String scope : fieldScopes)
			gCubeScopes.add(scope);
		//if(gCubeScopes.size()==0) return BridgeHelper.scopes;
		return gCubeScopes;
	}
	
	private static void retrieveScopesOnline() throws Exception
	{
		List<String> tmpScopes = getGHNContextScopes();

		logger.info("retrieveScopesOnline : " + tmpScopes);
		
		List<String> toKeepScopes = new ArrayList<String>();
		for(String scope : tmpScopes)
		{
			if(!scopeHelper.isInfraScope(scope))
				toKeepScopes.add(scope);
		}
		
		logger.info("retrieveScopesOnline after additions : " + toKeepScopes);
		BridgeHelper.scopes = new ArrayList<String>(toKeepScopes);// toKeepScopes.toArray(new GCUBEScope[0]);
	}
	
	private static void retrieveScopesOffline() throws Exception
	{
		List<String> scopes = getGHNContextStartScopes();
		
		logger.info("retrieveScopesOffline : " + scopes);
		scopes = BridgeHelper.getScopesOfPublishedVREResources();
		
		logger.info("retrieveScopesOffline after additions : " + scopes);
	
		BridgeHelper.scopes = new ArrayList<String>(scopes);
	}
	
	private static List<String> getScopesOfPublishedVREResources() throws Exception
	{
		
		logger.info("Searching for publised VRE resouces");
		
		List<Resource> resources = new ArrayList<Resource>();
		
		List<String> scopes = getGHNContextStartScopes();

		logger.info("getPublishedVREResources : " + scopes);
		
		Set<String> voScopes = getVOScopes(scopes);
		
		for(String scope : voScopes)
			resources.addAll(BackendConnector.newICollector().getGenericResourcesByType("VRE", scope));

		for(Resource resource : resources) {
			if (resource.getScopes() == null || resource.getScopes().isEmpty())
				continue;
			String retrievedScope = resource.getScopes().get(0);
			if(retrievedScope != null)
				scopes.add(retrievedScope);
		}

		return scopes;
	}
	
//	private static String getVREScope(GenericResource resource) throws Exception
//	{
//		if (resource != null && resource.scopes() != null && resource.scopes().size()>0)
//			return (String) resource.scopes().toArray()[0];
//		else
//			return null;
//		
////		Document doc = null;
////		if(resource != null && 
////				resource.getBody()!=null && 
////				resource.getBody().trim().length()!=0) doc = XMLUtils.Deserialize("<root>" + resource.getBody() + "</root>");
////			else return null;
////		
////		    return XMLUtils.GetChildElementWithName(doc.getDocumentElement(), "Scope").getFirstChild().getNodeValue();
//	}
	
	public static Set<IDaoElement> getElement(Class<?> type) throws Exception
	{
		if(type.equals(FieldDao.class)) return getFields();
		else if(type.equals(PresentableDao.class)) return getPresentables();
		else if(type.equals(SearchableDao.class)) return getSearchables();
		else if(type.equals(DataCollectionDao.class)) return getAllCollections();
		else if(type.equals(HostingNodeDao.class)) return getHostingNodes();
		else if(type.equals(FunctionalityDao.class)) return getFunctionality();
		else if(type.equals(ExecutionServerDao.class)) return getExecutionServer();
		else if(type.equals(ExecutionServiceDao.class)) return new HashSet<IDaoElement>(); //covered by the server case
		else if(type.equals(WorkflowServiceDao.class)) return getWorkflowService();
		else if(type.equals(FieldIndexContainerDao.class)) return new HashSet<IDaoElement>(); //covered by the Index cases
		else if(type.equals(FTIndexDao.class)) return getDataSourceFT();
//		else if(type.equals(FWIndexDao.class)) return getFWIndex();
//		else if(type.equals(GeoIndexDao.class)) return getGeoIndex();
		else if(type.equals(OpenSearchDataSourceDao.class)) return getOpenSearchDataSource(); //covered by the Index cases
		
		else if(type.equals(SruConsumerDao.class)) return getSruConsumerDataSource();
		
		else if(type.equals(FTIndexServiceDao.class)) return new HashSet<IDaoElement>(); //covered by the Index cases
//		else if(type.equals(FWIndexServiceDao.class)) return new HashSet<IDaoElement>(); //covered by the Index cases
//		else if(type.equals(GeoIndexServiceDao.class)) return new HashSet<IDaoElement>(); //covered by the Index cases
		else if(type.equals(OpenSearchDataSourceServiceDao.class)) return new HashSet<IDaoElement>(); //covered by the Index cases
		
		else if(type.equals(SruConsumerServiceDao.class)) return new HashSet<IDaoElement>(); //covered by the Index cases
		
		else if(type.equals(ElementMetadataDao.class)) return getElementMetadata();
		else if(type.equals(StaticConfigurationDao.class)) return getStaticConfiguration();
		throw new ResourceRegistryException("unrecognized element type "+type);
	}
	
	
//	private static Set<IDaoElement> getDataSource(String className, String serviceClassName, String serviceClass, String serviceName, String functionality, 
//			DataSource.Type type, String description) throws Exception
//	{
//		List<String> scopes = BridgeHelper.scopes;
//		logger.warn("### getting datasources from scopes : " + scopes);
//		
//		
//		Map<String, DataSourceDao> datasourceItems=new HashMap<String, DataSourceDao>();
//		Map<String, DataSourceServiceDao> datasourceServiceItems=new HashMap<String, DataSourceServiceDao>();
//		
//		Set<FieldIndexContainerDao> fieldItems=new HashSet<FieldIndexContainerDao>();
//		for(String scope : scopes)
//		{
//			logger.info("Searching for " + description + " indexes in scope "+scope.toString());
//			
//			List<SerInstance> resources = Deps.newICollector().discoverServiceInstances(serviceName, serviceClass, scope);;
//			
//			logger.info("found  " + resources.size() + " " + description +  "  in scope "+scope.toString());
//			 
//			for (SerInstance r : resources) {
//				logger.info("resource key : " + r.getKey() + " datasourcetimes : " + datasourceItems + " scope : " + scope);
//				
//				if(r.getKey()==null) continue;
//				String key=r.getKey();
//				
//				if(datasourceItems.containsKey(key)) 
//				{
//					for (String datasourceScope : r.getProperties().getScopes()) {
//						logger.trace("adding scope : " + datasourceScope);
//						datasourceItems.get(key).getScopes().add(datasourceScope);
//						datasourceServiceItems.get(key).getScopes().add(datasourceScope);
//					}
//					
//					datasourceItems.get(key).getScopes().add(scope.toString()); 
//					datasourceServiceItems.get(key).getScopes().add(scope.toString()); //If the IS was queried in VRE scope, it will return all
//				}
//				else
//				{
//					
//					DataSourceServiceDao ss = (DataSourceServiceDao)Class.forName(serviceClassName).newInstance();
//					ss.setID(key);
//					ss.setType(type.toString());
//					ss.getDataSources().add(key);
//					
//					logger.trace("------- endpoint : " + r.getEndpoint().toString());
//					ss.setEndpoint(r.getEndpoint().toString());
//					
//					ss.setFunctionality(functionality);
//					ss.setHostingNode(r.getProperties().getNodeId());
//					
//					ss.getScopes().addAll(r.getProperties().getScopes());
//					ss.getScopes().add(scope);
//					
////					for(GCUBEScope sc : d.getScope()) ss.getScopes().add(sc.toString());
////					ss.getScopes().add(scope.toString()); //If the IS was queried in VRE scope, it will return all
////														  //sources deployed on VO scope, regardless of the fact that
////					 									  //they might not contain the VRE scope. For that
////					 									  //reason, the current scope is added to the source scopes
//					ss.setTimestamp(Calendar.getInstance().getTimeInMillis());
//					datasourceServiceItems.put(ss.getID(), ss);
//					
//					DataSourceDao s = (DataSourceDao)Class.forName(className).newInstance();
//					//FTIndexServiceDao s=new FTIndexServiceDao();
//					s.setID(key);
//					s.setType(type.toString());
//					s.setFunctionality(functionality);
//					if(s.getBoundDataSourceServices() == null) s.setBoundDataSourceServices(new HashSet<String>());
//					s.getBoundDataSourceServices().add(key);
//					s.setTimestamp(Calendar.getInstance().getTimeInMillis());
//				//	s.setHostingNode(d.getGHNID());
//					s.getCapabilities().clear();
//					
//					XPathEvaluator xpath = new XPathEvaluator(r.getProperties().getCustomProperties());
//					for (String capability: xpath.evaluate("/doc/*[local-name()='SupportedRelations']/text()")) {
//						s.getCapabilities().add(capability);
//						
//					}
////					NodeList supportedRels = r.properties().customProperties().getElementsByTagName("SupportedRelations");
////					for (int i = 0 ; i < supportedRels.getLength() ; i++ ){
////						String capability = supportedRels.item(i).getTextContent();
////					}
//					
//					
//					//s.getCapabilities().addAll(d.evaluate("//SupportedRelations/text()"));
//					
//				//	s.getFields().clear();
//					
//					
//					s.getScopes().addAll(r.getProperties().getScopes());
//					s.getScopes().add(scope);
////					for(GCUBEScope sc : d.getScope()) s.getScopes().add(sc.toString());
////					s.getScopes().add(scope.toString()); //If the IS was queried in VRE scope, it will return all
////														 //sources deployed on VO scope, regardless of the fact that
////														 //they might not contain the VRE scope. For that
////														 //reason, the current scope is added to the source scopes
////					List<String> ff=d.evaluate("//Fields/text()");
//					Set<String> checkDups = new HashSet<String>();
//					
//					xpath = new XPathEvaluator(r.getProperties().getCustomProperties());
//					for (String f: xpath.evaluate("/doc/*[local-name()='Fields']/text()"))
//					{
//					
//						logger.trace("Custom properties f : " + f);
//						String []fparts=f.split(":");
//						if(fparts.length==6 && fparts[2].equals("s")) fparts[4] += ":" + fparts[5];
//						if(fparts.length<4 || fparts.length>6 ) continue;
//						if(fparts.length==6 && !fparts[2].equals("s")) continue;
//						FieldIndexContainerDao fc=new FieldIndexContainerDao();
//						fc.setID(s.getID()+":"+f);
//						if(checkDups.contains(fc.getID()))
//						{
//							logger.warn( "Duplicate field detected: " + fc.getID()); 
//							continue;
//						}
//						checkDups.add(fc.getID());
//						fc.setCollection(fparts[0]);
//						fc.setLanguage(fparts[1]);
//						
//						 
//						
//						fc.setFieldType(fparts[2]);
//						fc.setField(fparts[3]);
//						logger.info("Field : " + f + " language " + fparts[1]);
//						
//						if(fparts.length>=5) fc.setExpression(fparts[4]);
//						s.getFields().add(fc.getID());
//						fieldItems.add(fc);
//					}
//					logger.info("datasource : " + s.getID());
//					logger.info("datasource fields : " + s.getFields());
//					logger.info("datasource scopes : " + s.getScopes());
//					
//					//logger.trace("datasource scopes : " + s.getScopes());
//					datasourceItems.put(s.getID(), s);
//				}
//			}
//		}
//			
////		for (DataSourceDao dsd : datasourceItems.values()) {
////			logger.trace(dsd.getScopes());
////		}
//			
////			ISClient client =  GHNContext.getImplementation(ISClient.class);
////			WSResourceQuery query=client.getQuery(WSResourceQuery.class);
////			
////			
////			
////			
//////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceName","FullTextIndexLookup"));
//////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceClass","Index"));
//////			query.addGenericCondition("$result/Data/child::*[local-name()='ServiceName']/string() eq 'FullTextIndexLookup' and $result/Data/child::*[local-name()='ServiceClass']/string() eq 'Index'");
////			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceName']/string() eq '" + serviceName + "'");
////			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceClass']/string() eq '" + serviceClass + "'");
////			List<RPDocument> inds=client.execute(query, scope);
//			
//		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
//		retValue.addAll(datasourceItems.values());
//		retValue.addAll(datasourceServiceItems.values());
//		retValue.addAll(fieldItems);
//		return retValue;
//	}
	
	
	private static Set<IDaoElement> getDataSourceOpenSearch() throws Exception
	{
		
		String className = OpenSearchDataSourceDao.class.getName();
		String serviceClassName =OpenSearchDataSourceServiceDao.class.getName();
		
		String functionality = "opensearch.index.ft";
			
		List<String> scopes = BridgeHelper.scopes;
		logger.warn("### getting OPENSEARCH datasources from scopes : " + scopes);
		
		
		Map<String, DataSourceDao> datasourceItems=new HashMap<String, DataSourceDao>();
		Map<String, DataSourceServiceDao> datasourceServiceItems=new HashMap<String, DataSourceServiceDao>();
		
		Set<FieldIndexContainerDao> fieldItems=new HashSet<FieldIndexContainerDao>();
		for(String scope : scopes)
		{
			logger.info("Searching for opensearch datasources in scope "+scope.toString());
			
			List<Resource> resources = BackendConnector.newICollector().getGenericResourcesByType("OpenSearchDataSourceResources", scope);
			
			logger.info("found  " + resources.size() + "  in scope "+scope.toString());
			 
			for (Resource r : resources) {
				//String name = r.getName();
				String key = r.getResourceID();//name.split("\\.")[1];
				
				logger.info("resource key : " + key + " datasourcetimes : " + datasourceItems + " scope : " + scope);
				
				if(key==null) continue;
				
				if(datasourceItems.containsKey(key)) 
				{
					for (String datasourceScope : r.getScopes()) {
						logger.trace("adding scope : " + datasourceScope);
						datasourceItems.get(key).getScopes().add(datasourceScope);
						datasourceServiceItems.get(key).getScopes().add(datasourceScope);
					}
					
					datasourceItems.get(key).getScopes().add(scope.toString()); 
					datasourceServiceItems.get(key).getScopes().add(scope.toString()); //If the IS was queried in VRE scope, it will return all
				}
				else
				{
					DataSourceServiceDao ss = (DataSourceServiceDao)Class.forName(serviceClassName).newInstance();
					ss.setID(key);
					ss.setType(DataSource.Type.OpenSearch.toString());
					ss.getDataSources().add(key);

					
					
					String hostname = null;
					String endpoint= null;
					String hostingnode= null;

					
					Node body = r.getBody();
					XPathEvaluator xpath = new XPathEvaluator(body);
					
//					endpoint = getIndexServiceEndpoint(scope, hostname);
//					hostingnode = getIndexServiceGHNId(scope, hostname);
					
					if  (xpath.evaluate("//hostname/text()").size() > 0) {
						hostname = xpath.evaluate("//hostname/text()").get(0);
						endpoint = getOpenSearchServiceEndpoint(scope, hostname);
						hostingnode = getOpenSearchServiceGHNId(scope, hostname);
					}
					
					Set<String> resourceScopes = new HashSet<String>();
					for (String resourceScope: xpath.evaluate("//scope/text()")) {
						resourceScopes.add(resourceScope);
					}
					
					logger.info("scopes of datasource : " + resourceScopes);
					
					
					logger.info("------- endpoint : " + endpoint);
					ss.setEndpoint(endpoint);
					
					ss.setFunctionality(functionality);
					ss.setHostingNode(hostingnode);
					
					ss.getScopes().addAll(resourceScopes);
					ss.getScopes().add(scope);
					ss.setTimestamp(Calendar.getInstance().getTimeInMillis());
					datasourceServiceItems.put(ss.getID(), ss);
					
					
					
					DataSourceDao s = (DataSourceDao)Class.forName(className).newInstance();
					//FTIndexServiceDao s=new FTIndexServiceDao();
					s.setID(key);
					s.setType(DataSource.Type.OpenSearch.toString());
					s.setFunctionality(functionality);
					if(s.getBoundDataSourceServices() == null) s.setBoundDataSourceServices(new HashSet<String>());
					s.getBoundDataSourceServices().add(key);
					s.setTimestamp(Calendar.getInstance().getTimeInMillis());
				//	s.setHostingNode(d.getGHNID());
					s.getCapabilities().clear();
					for (String capability: xpath.evaluate("//supportedRelations/text()")) {
						capability = StringEscapeUtils.unescapeXml(capability);
						
						logger.info("capability found : " + capability);
						s.getCapabilities().add(capability);
						
					}
					
					
					s.getScopes().addAll(resourceScopes);
					s.getScopes().add(scope);
					Set<String> checkDups = new HashSet<String>();
					
					for (String f: xpath.evaluate("//fields/text()"))
					{
					
						logger.trace("Custom properties f : " + f);
						String []fparts=f.split(":");
						if(fparts.length==6 && fparts[2].equals("s")) fparts[4] += ":" + fparts[5];
						if(fparts.length<4 || fparts.length>6 ) continue;
						if(fparts.length==6 && !fparts[2].equals("s")) continue;
						FieldIndexContainerDao fc=new FieldIndexContainerDao();
						fc.setID(s.getID()+":"+f);
						if(checkDups.contains(fc.getID()))
						{
							logger.warn( "Duplicate field detected: " + fc.getID()); 
							continue;
						}
						checkDups.add(fc.getID());
						fc.setCollection(fparts[0]);
						fc.setLanguage(fparts[1]);
						
						 
						
						fc.setFieldType(fparts[2]);
						fc.setField(fparts[3]);
						logger.info("### Field : " + f + " language " + fparts[1] + " collection : " + fparts[0]);
						
						if(fparts.length>=5) fc.setExpression(fparts[4]);
						s.getFields().add(fc.getID());
						fieldItems.add(fc);
					}
					logger.info("datasource : " + s.getID());
					logger.info("datasource fields : " + s.getFields());
					logger.info("datasource scopes : " + s.getScopes());
					
					//logger.trace("datasource scopes : " + s.getScopes());
					datasourceItems.put(s.getID(), s);
				}
			}
		}
			
//		for (DataSourceDao dsd : datasourceItems.values()) {
//			logger.trace(dsd.getScopes());
//		}
			
//			ISClient client =  GHNContext.getImplementation(ISClient.class);
//			WSResourceQuery query=client.getQuery(WSResourceQuery.class);
//			
//			
//			
//			
////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceName","FullTextIndexLookup"));
////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceClass","Index"));
////			query.addGenericCondition("$result/Data/child::*[local-name()='ServiceName']/string() eq 'FullTextIndexLookup' and $result/Data/child::*[local-name()='ServiceClass']/string() eq 'Index'");
//			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceName']/string() eq '" + serviceName + "'");
//			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceClass']/string() eq '" + serviceClass + "'");
//			List<RPDocument> inds=client.execute(query, scope);
			
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(datasourceItems.values());
		retValue.addAll(datasourceServiceItems.values());
		retValue.addAll(fieldItems);
		return retValue;
	}
	
	
	
	
	
	
	private static Set<IDaoElement> getDataSourceSruConsumer() throws Exception
	{
		
		String className = SruConsumerDao.class.getName();
		String serviceClassName = SruConsumerServiceDao.class.getName();
		
		String functionality = "sruconsumer.consumer";
			
		List<String> scopes = BridgeHelper.scopes;
		logger.warn("### getting SRU_CONSUMER datasources from scopes : " + scopes);
		
		
		Map<String, DataSourceDao> datasourceItems=new HashMap<String, DataSourceDao>();
		Map<String, DataSourceServiceDao> datasourceServiceItems=new HashMap<String, DataSourceServiceDao>();
		
		Set<FieldIndexContainerDao> fieldItems=new HashSet<FieldIndexContainerDao>();
		for(String scope : scopes)
		{
			logger.info("Searching for sru consumers datasources in scope "+scope.toString());
			
			List<Resource> resources = BackendConnector.newICollector().getGenericResourcesByType("SruConsumerResources", scope);
			
			logger.info("found  " + resources.size() + "  in scope "+scope.toString());
			 
			for (Resource r : resources) {
				//String name = r.getName();
				String key = r.getResourceID();//name.split("\\.")[1];
				
				logger.info("resource key : " + key + " datasourcetimes : " + datasourceItems + " scope : " + scope);
				
				if(key==null) continue;
				
				if(datasourceItems.containsKey(key)) 
				{
					for (String datasourceScope : r.getScopes()) {
						logger.trace("adding scope : " + datasourceScope);
						datasourceItems.get(key).getScopes().add(datasourceScope);
						datasourceServiceItems.get(key).getScopes().add(datasourceScope);
					}
					
					datasourceItems.get(key).getScopes().add(scope.toString()); 
					datasourceServiceItems.get(key).getScopes().add(scope.toString()); //If the IS was queried in VRE scope, it will return all
				}
				else
				{
					DataSourceServiceDao ss = (DataSourceServiceDao)Class.forName(serviceClassName).newInstance();
					ss.setID(key);
					ss.setType(DataSource.Type.SruConsumer.toString());
					ss.getDataSources().add(key);

					
					
					String hostname = null;
					String endpoint= null;
					String hostingnode= null;

					
					Node body = r.getBody();
					XPathEvaluator xpath = new XPathEvaluator(body);
					
//					endpoint = getIndexServiceEndpoint(scope, hostname);
//					hostingnode = getIndexServiceGHNId(scope, hostname);
					
					if  (xpath.evaluate("//hostname/text()").size() > 0) {
						hostname = xpath.evaluate("//hostname/text()").get(0);
						endpoint = getSruConsumerServiceEndpoint(scope, hostname);
						hostingnode = getSruConsumerGHNId(scope, hostname);
					}
					
					Set<String> resourceScopes = new HashSet<String>();
					for (String resourceScope: xpath.evaluate("//scope/text()")) {
						resourceScopes.add(resourceScope);
					}
					
					logger.info("scopes of datasource : " + resourceScopes);
					
					
					logger.info("------- endpoint : " + endpoint);
					ss.setEndpoint(endpoint);
					
					ss.setFunctionality(functionality);
					ss.setHostingNode(hostingnode);
					
					ss.getScopes().addAll(resourceScopes);
					ss.getScopes().add(scope);
					ss.setTimestamp(Calendar.getInstance().getTimeInMillis());
					datasourceServiceItems.put(ss.getID(), ss);
					
					
					
					DataSourceDao s = (DataSourceDao)Class.forName(className).newInstance();
					//FTIndexServiceDao s=new FTIndexServiceDao();
					s.setID(key);
					s.setType(DataSource.Type.SruConsumer.toString());
					s.setFunctionality(functionality);
					if(s.getBoundDataSourceServices() == null) s.setBoundDataSourceServices(new HashSet<String>());
					s.getBoundDataSourceServices().add(key);
					s.setTimestamp(Calendar.getInstance().getTimeInMillis());
				//	s.setHostingNode(d.getGHNID());
					s.getCapabilities().clear();
					for (String capability: xpath.evaluate("//supportedRelations/text()")) {
						capability = StringEscapeUtils.unescapeXml(capability);
						
						logger.info("capability found : " + capability);
						s.getCapabilities().add(capability);
						
					}
					
					
					s.getScopes().addAll(resourceScopes);
					s.getScopes().add(scope);
					Set<String> checkDups = new HashSet<String>();
					
					for (String f: xpath.evaluate("//fields/text()"))
					{
					
						logger.trace("Custom properties f : " + f);
						String []fparts=f.split(":");
						if(fparts.length==6 && fparts[2].equals("s")) fparts[4] += ":" + fparts[5];
						if(fparts.length<4 || fparts.length>6 ) continue;
						if(fparts.length==6 && !fparts[2].equals("s")) continue;
						FieldIndexContainerDao fc=new FieldIndexContainerDao();
						fc.setID(s.getID()+":"+f);
						if(checkDups.contains(fc.getID()))
						{
							logger.warn( "Duplicate field detected: " + fc.getID()); 
							continue;
						}
						checkDups.add(fc.getID());
						fc.setCollection(fparts[0]);
						fc.setLanguage(fparts[1]);
						
						 
						
						fc.setFieldType(fparts[2]);
						fc.setField(fparts[3]);
						logger.info("### Field : " + f + " language " + fparts[1] + " collection : " + fparts[0]);
						
						if(fparts.length>=5) fc.setExpression(fparts[4]);
						s.getFields().add(fc.getID());
						fieldItems.add(fc);
					}
					logger.info("datasource : " + s.getID());
					logger.info("datasource fields : " + s.getFields());
					logger.info("datasource scopes : " + s.getScopes());
					
					//logger.trace("datasource scopes : " + s.getScopes());
					datasourceItems.put(s.getID(), s);
				}
			}
		}
			
//		for (DataSourceDao dsd : datasourceItems.values()) {
//			logger.trace(dsd.getScopes());
//		}
			
//			ISClient client =  GHNContext.getImplementation(ISClient.class);
//			WSResourceQuery query=client.getQuery(WSResourceQuery.class);
//			
//			
//			
//			
////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceName","FullTextIndexLookup"));
////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceClass","Index"));
////			query.addGenericCondition("$result/Data/child::*[local-name()='ServiceName']/string() eq 'FullTextIndexLookup' and $result/Data/child::*[local-name()='ServiceClass']/string() eq 'Index'");
//			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceName']/string() eq '" + serviceName + "'");
//			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceClass']/string() eq '" + serviceClass + "'");
//			List<RPDocument> inds=client.execute(query, scope);
			
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(datasourceItems.values());
		retValue.addAll(datasourceServiceItems.values());
		retValue.addAll(fieldItems);
		return retValue;
	}
	
	
	
	private static Set<IDaoElement> getDataSourceFT() throws Exception
	{
		
		String className = FTIndexDao.class.getName();
		String serviceClassName = FTIndexServiceDao.class.getName();
		
		String functionality = "search.index.ft";
			
		List<String> scopes = BridgeHelper.scopes;
		logger.warn("### getting datasources from scopes : " + scopes);
		
		
		Map<String, DataSourceDao> datasourceItems=new HashMap<String, DataSourceDao>();
		Map<String, DataSourceServiceDao> datasourceServiceItems=new HashMap<String, DataSourceServiceDao>();
		
		Set<FieldIndexContainerDao> fieldItems=new HashSet<FieldIndexContainerDao>();
		for(String scope : scopes)
		{
			logger.info("Searching for fulltext indexes in scope "+scope.toString());
			
			List<Resource> resources = BackendConnector.newICollector().getGenericResourcesByType("IndexResources", scope);
			
			logger.info("found  " + resources.size() + "  in scope "+scope.toString());
			 
			for (Resource r : resources) {
				//String name = r.getName();
				String key = r.getResourceID();//name.split("\\.")[1];
				
				logger.info("resource key : " + key + " datasourcetimes : " + datasourceItems + " scope : " + scope);
				
				if(key==null) continue;
				
				if(datasourceItems.containsKey(key)) 
				{
					for (String datasourceScope : r.getScopes()) {
						logger.trace("adding scope : " + datasourceScope);
						datasourceItems.get(key).getScopes().add(datasourceScope);
						datasourceServiceItems.get(key).getScopes().add(datasourceScope);
					}
					
					datasourceItems.get(key).getScopes().add(scope.toString()); 
					datasourceServiceItems.get(key).getScopes().add(scope.toString()); //If the IS was queried in VRE scope, it will return all
				}
				else
				{
					DataSourceServiceDao ss = (DataSourceServiceDao)Class.forName(serviceClassName).newInstance();
					ss.setID(key);
					ss.setType(DataSource.Type.FullTextIndex.toString());
					ss.getDataSources().add(key);

					
					
					String hostname = null;
					String endpoint= null;
					String hostingnode= null;

					
					Node body = r.getBody();
					XPathEvaluator xpath = new XPathEvaluator(body);
					
//					endpoint = getIndexServiceEndpoint(scope, hostname);
//					hostingnode = getIndexServiceGHNId(scope, hostname);
					
					if  (xpath.evaluate("//hostname/text()").size() > 0) {
						hostname = xpath.evaluate("//hostname/text()").get(0);
						endpoint = getIndexServiceEndpoint(scope, hostname);
						hostingnode = getIndexServiceGHNId(scope, hostname);
					}
					
					Set<String> resourceScopes = new HashSet<String>();
					for (String resourceScope: xpath.evaluate("//scope/text()")) {
						resourceScopes.add(resourceScope);
					}
					
					logger.info("scopes of datasource : " + resourceScopes);
					
					
					logger.trace("------- endpoint : " + endpoint);
					ss.setEndpoint(endpoint);
					
					ss.setFunctionality(functionality);
					ss.setHostingNode(hostingnode);
					
					ss.getScopes().addAll(resourceScopes);
					ss.getScopes().add(scope);
					ss.setTimestamp(Calendar.getInstance().getTimeInMillis());
					datasourceServiceItems.put(ss.getID(), ss);
					
					
					
					DataSourceDao s = (DataSourceDao)Class.forName(className).newInstance();
					//FTIndexServiceDao s=new FTIndexServiceDao();
					s.setID(key);
					s.setType(DataSource.Type.FullTextIndex.toString());
					s.setFunctionality(functionality);
					if(s.getBoundDataSourceServices() == null) s.setBoundDataSourceServices(new HashSet<String>());
					s.getBoundDataSourceServices().add(key);
					s.setTimestamp(Calendar.getInstance().getTimeInMillis());
				//	s.setHostingNode(d.getGHNID());
					s.getCapabilities().clear();
					for (String capability: xpath.evaluate("//supportedRelations/text()")) {
						capability = StringEscapeUtils.unescapeXml(capability);
						
						logger.debug("capability found : " + capability);
						s.getCapabilities().add(capability);
						
					}
					
					
					s.getScopes().addAll(resourceScopes);
					s.getScopes().add(scope);
					Set<String> checkDups = new HashSet<String>();
					
					for (String f: xpath.evaluate("//fields/text()"))
					{
					
						logger.trace("Custom properties f : " + f);
						String []fparts=f.split(":");
						if(fparts.length==6 && fparts[2].equals("s")) fparts[4] += ":" + fparts[5];
						if(fparts.length<4 || fparts.length>6 ) continue;
						if(fparts.length==6 && !fparts[2].equals("s")) continue;
						FieldIndexContainerDao fc=new FieldIndexContainerDao();
						fc.setID(s.getID()+":"+f);
						if(checkDups.contains(fc.getID()))
						{
							logger.warn( "Duplicate field detected: " + fc.getID()); 
							continue;
						}
						checkDups.add(fc.getID());
						fc.setCollection(fparts[0]);
						fc.setLanguage(fparts[1]);
						
						 
						
						fc.setFieldType(fparts[2]);
						fc.setField(fparts[3]);
						logger.debug("### Field : " + f + " language " + fparts[1] + " collection : " + fparts[0]);
						
						if(fparts.length>=5) fc.setExpression(fparts[4]);
						s.getFields().add(fc.getID());
						fieldItems.add(fc);
					}
					logger.info("datasource : " + s.getID());
					logger.info("datasource fields : " + s.getFields());
					logger.info("datasource scopes : " + s.getScopes());
					
					//logger.trace("datasource scopes : " + s.getScopes());
					datasourceItems.put(s.getID(), s);
				}
			}
		}
			
//		for (DataSourceDao dsd : datasourceItems.values()) {
//			logger.trace(dsd.getScopes());
//		}
			
//			ISClient client =  GHNContext.getImplementation(ISClient.class);
//			WSResourceQuery query=client.getQuery(WSResourceQuery.class);
//			
//			
//			
//			
////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceName","FullTextIndexLookup"));
////			query.addAtomicConditions(new AtomicCondition("/Data/ServiceClass","Index"));
////			query.addGenericCondition("$result/Data/child::*[local-name()='ServiceName']/string() eq 'FullTextIndexLookup' and $result/Data/child::*[local-name()='ServiceClass']/string() eq 'Index'");
//			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceName']/string() eq '" + serviceName + "'");
//			query.addGenericCondition("$result//Data/child::*[local-name()='ServiceClass']/string() eq '" + serviceClass + "'");
//			List<RPDocument> inds=client.execute(query, scope);
			
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(datasourceItems.values());
		retValue.addAll(datasourceServiceItems.values());
		retValue.addAll(fieldItems);
		return retValue;
	}
	
	
	
	private static String getSruConsumerServiceEndpoint(String scope, String hostname){
		Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey("SruConsumerDatasource", "Search", ENDPOINT_KEY, scope);
		
		logger.info("run instances : " + insts);
		
		for (RunInstance inst : insts){
				try {
					if (inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString().toLowerCase().contains(hostname.toLowerCase())){
						
						logger.info("found epr for the hostname : " + hostname + " at : " + inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString());
						
						return inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString();
					} 
				} catch (Exception e) {
					logger.warn( "error comparing the uri to the hostname");
				}
			}
		
		logger.warn( "not found epr for the hostname : " + hostname);
		
		return null;
	}
	
	private static String getSruConsumerGHNId(String scope, String hostname) {
		Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey("SruConsumerDatasource", "Search", ENDPOINT_KEY, scope);

		for (RunInstance inst : insts) {
			try {
				if (inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString().toLowerCase().contains(hostname.toLowerCase())) {
					logger.info("found ghnid for the hostname : " + hostname + " at : " + inst.getProfile().ghn.ghnId);

					return inst.getProfile().ghn.ghnId;
				}
			} catch (Exception e) {
				logger.warn( "error comparing the uri to the hostname");
			}
		}
		logger.warn( "not found ghnid for the hostname : " + hostname);

		return null;
	}
	
	private static String getOpenSearchServiceEndpoint(String scope, String hostname){
		Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey("OpenSearchDataSource", "Search", ENDPOINT_KEY, scope);
		
		logger.info("run instances : " + insts);
		
		for (RunInstance inst : insts){
				try {
					if (inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString().toLowerCase().contains(hostname.toLowerCase())){
						
						logger.info("found epr for the hostname : " + hostname + " at : " + inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString());
						
						return inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString();
					} 
				} catch (Exception e) {
					logger.warn( "error comparing the uri to the hostname");
				}
			}
		
		logger.warn( "not found epr for the hostname : " + hostname);
		
		return null;
	}
	
	private static String getOpenSearchServiceGHNId(String scope, String hostname) {
		Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey("OpenSearchDataSource", "Search", ENDPOINT_KEY, scope);

		for (RunInstance inst : insts) {
			try {
				if (inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString().toLowerCase().contains(hostname.toLowerCase())) {
					logger.info("found ghnid for the hostname : " + hostname + " at : " + inst.getProfile().ghn.ghnId);

					return inst.getProfile().ghn.ghnId;
				}
			} catch (Exception e) {
				logger.warn( "error comparing the uri to the hostname");
			}
		}
		logger.warn( "not found ghnid for the hostname : " + hostname);

		return null;
	}
	
	private static String getIndexServiceEndpoint(String scope, String hostname){
		Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey("FullTextIndexNode", "Index", ENDPOINT_KEY, scope);
		
		for (RunInstance inst : insts){
				try {
					if (inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString().toLowerCase().contains(hostname.toLowerCase())){
						
						logger.info("found epr for the hostname : " + hostname + " at : " + inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString());
						
						return inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString();
					} 
				} catch (Exception e) {
					logger.warn( "error comparing the uri to the hostname");
				}
			}
		
		logger.warn( "not found epr for the hostname : " + hostname);
		
		return null;
	}
	
	private static String getIndexServiceGHNId(String scope, String hostname) {
		Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey("FullTextIndexNode", "Index", ENDPOINT_KEY, scope);

		for (RunInstance inst : insts) {
			try {
				if (inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toASCIIString().toLowerCase().contains(hostname.toLowerCase())) {
					logger.info("found ghnid for the hostname : " + hostname + " at : " + inst.getProfile().ghn.ghnId);

					return inst.getProfile().ghn.ghnId;
				}
			} catch (Exception e) {
				logger.warn( "error comparing the uri to the hostname");
			}
		}
		logger.warn( "not found ghnid for the hostname : " + hostname);

		return null;
	}
	
//	private static Set<IDaoElement> getFTIndex() throws Exception
//	{
//		//DataSource.addSubType(DataSource.Type.FullTextIndex, FTIndexService.class, FTIndexServiceDao.class);
//		return getDataSource(FTIndexDao.class.getName(), FTIndexServiceDao.class.getName(), "Index", "FullTextIndexLookup", "search.index.ft", DataSource.Type.FullTextIndex, "full text");
//	}
	
	
	/*
	private static Set<IDaoElement> getFTIndex() throws Exception
	{
		
		logger.info("getting fulltext index");
		
		String functionality = "search.index.ft"; 
		DataSource.Type type = DataSource.Type.FullTextIndex;
		String description = "fulltext";
		
		
		List<String> scopes = BridgeHelper.scopes;
		logger.warn("### getting datasources from scopes : " + scopes);
		
		
		Map<String, DataSourceDao> datasourceItems=new HashMap<String, DataSourceDao>();
		Map<String, DataSourceServiceDao> datasourceServiceItems=new HashMap<String, DataSourceServiceDao>();
		
		Set<FieldIndexContainerDao> fieldItems=new HashSet<FieldIndexContainerDao>();
		for(String scope : scopes){
			logger.warn("### searching fulltext in scope " + scope);
			
			logger.warn("### calling ri discoverer " + scope);
			Set<String> endpoints = new RIDiscovererISimpl().discoverRunningInstances("FullTextIndexNode", "Index", "resteasy-servlet" ,scope);
			logger.warn("### ri discoverer returned enpoints : " + endpoints);
			
			logger.warn("### initializing harvester");
			ResourceHarvester<IndexResource> harvester = new ResourceHarvester<IndexResource>(scope);
			
			
			for (String endpoint : endpoints) {
				logger.warn("### getting resources for enpoint : " + endpoint);
				
				if (endpoint.endsWith("/"))
					endpoint = endpoint.substring(0, endpoint.length() - 2);
				
				try {
					Set<IndexResource> resources = harvester.getResources(endpoint, IndexResource.class);
					
					logger.warn("### harvester for enpoint : " + endpoint + " returned resources : " + resources.size());
					
					for (IndexResource resource : resources){
						
//							if (!scope.equalsIgnoreCase(resource.getScope())){
//								logger.trace("resource scopes not in given scope " + scope);
//								continue;
//							}
						
						
						String key=resource.getResourceID();
						
						
						if(datasourceItems.containsKey(key)){
							datasourceItems.get(key).getScopes().add(resource.getScope());
							datasourceServiceItems.get(key).getScopes().add(resource.getScope());
						
							datasourceItems.get(key).getScopes().add(scope);
							datasourceServiceItems.get(key).getScopes().add(scope);
						
						
						} else {
							
							DataSourceServiceDao ss = (DataSourceServiceDao)Class.forName(FTIndexServiceDao.class.getName()).newInstance();
							ss.setID(key);
							ss.setType(type.toString());
							ss.getDataSources().add(key);
							
							ss.setEndpoint(endpoint);
							
							ss.setFunctionality(functionality);
							//TODO: how to get HostingNode
							ss.setHostingNode("");
							
							ss.getScopes().add(resource.getScope());
							ss.getScopes().add(scope);
							
							ss.setTimestamp(Calendar.getInstance().getTimeInMillis());
							datasourceServiceItems.put(ss.getID(), ss);
							
							
							
							DataSourceDao s = (DataSourceDao)Class.forName(FTIndexDao.class.getName()).newInstance();
							s.setID(key);
							s.setType(type.toString());
							s.setFunctionality(functionality);
							
							if(s.getBoundDataSourceServices() == null) s.setBoundDataSourceServices(new HashSet<String>());
							s.getBoundDataSourceServices().add(key);
							s.setTimestamp(Calendar.getInstance().getTimeInMillis());
							
							s.getCapabilities().clear();

							
							s.getCapabilities().clear();
							s.getCapabilities().addAll(resource.getSupportedRelations());
							
							s.getScopes().add(scope);
							s.getScopes().add(resource.getScope());
							
							Set<String> checkDups = new HashSet<String>();
							
							for (String f: resource.getFields()){
								logger.trace("Custom properties f : " + f);
								String []fparts=f.split(":");
								if(fparts.length==6 && fparts[2].equals("s")) fparts[4] += ":" + fparts[5];
								if(fparts.length<4 || fparts.length>6 ) continue;
								if(fparts.length==6 && !fparts[2].equals("s")) continue;
								FieldIndexContainerDao fc=new FieldIndexContainerDao();
								fc.setID(s.getID()+":"+f);
								if(checkDups.contains(fc.getID()))
								{
									logger.warn( "Duplicate field detected: " + fc.getID()); 
									continue;
								}
								checkDups.add(fc.getID());
								fc.setCollection(fparts[0]);
								fc.setLanguage(fparts[1]);
								
								 
								
								fc.setFieldType(fparts[2]);
								fc.setField(fparts[3]);
								
								//logger.info("Field : " + f + " language " + fparts[1]);
								
								if(fparts.length>=5) fc.setExpression(fparts[4]);
								s.getFields().add(fc.getID());
								fieldItems.add(fc);
								
								logger.info("datasource : " + s.getID());
								logger.info("datasource fields : " + s.getFields());
								logger.info("datasource scopes : " + s.getScopes());
								
								//logger.trace("datasource scopes : " + s.getScopes());
								datasourceItems.put(s.getID(), s);
								
							}
							
							
						}
					}
				} catch (Exception e) {
					logger.trace("No resources found for endpoint : " + endpoint);
					logger.log(Level.SEVERE, "No resources found for endpoint : " + endpoint, e);
					e.printStackTrace();
				}
				
				
			}
			
		}
		
		
		
		
		//DataSource.addSubType(DataSource.Type.FullTextIndex, FTIndexService.class, FTIndexServiceDao.class);
//			return getDataSource(FTIndexDao.class.getName(), FTIndexServiceDao.class.getName(), "Index", "FullTextIndexNode", "search.index.ft", DataSource.Type.FullTextIndex, "full text");
		
		
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(datasourceItems.values());
		retValue.addAll(datasourceServiceItems.values());
		retValue.addAll(fieldItems);
		return retValue;
		
	}*/
	
	@SuppressWarnings("unused")
	private static Set<IDaoElement> getFakeFTIndex() throws Exception
	{
		
		//DataSource.addSubType(DataSource.Type.FullTextIndex, FTIndexService.class, FTIndexServiceDao.class);
		Map<String, DataSourceDao> datasourceItems=new HashMap<String, DataSourceDao>();
		Map<String, DataSourceServiceDao> serviceItems=new HashMap<String, DataSourceServiceDao>();
		Set<FieldIndexContainerDao> fieldItems=new HashSet<FieldIndexContainerDao>();
		
		FTIndexServiceDao ss =  new FTIndexServiceDao();
		ss.setID("1283-5c96-f869-172b");
		ss.setFunctionality("search.index.ft");
		ss.setHostingNode("FTIndexGHNOne");
		ss.setEndpoint("http://nowhere1.com/wsrf/index/fulltext");
		ss.getScopes().add("/no/scope/");
		ss.setTimestamp(Calendar.getInstance().getTimeInMillis());
		ss.setType(DataSource.Type.FullTextIndex.toString());
		serviceItems.put(ss.getID(), ss);
		
		FTIndexDao s = new FTIndexDao();
		s.setID("1283-5c96-f869-172b");
		s.setType(DataSource.Type.FullTextIndex.toString());
		s.setFunctionality("search.index.ft");
		
		s.getCapabilities().clear();
		s.getCapabilities().add("any");
		s.getFields().clear();
		s.getScopes().add("/no/scope/");
		List<String> ff=new ArrayList<String>();
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:s:title");
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:p:title");
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:s:source");
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:p:source");
		for(String f:ff)
		{
			String []fparts=f.split(":");
			if(fparts.length!=4) continue;
			FieldIndexContainerDao fc=new FieldIndexContainerDao();
			fc.setID(s.getID()+":"+f);
			fc.setCollection(fparts[0]);
			fc.setLanguage(fparts[1]);
			fc.setFieldType(fparts[2]);
			fc.setField(fparts[3]);
			s.getFields().add(fc.getID());
			fieldItems.add(fc);
		}
		datasourceItems.put(s.getID(), s);
		
		//////////////
		ss = new FTIndexServiceDao();
		ss.setID("768a-8ab8-1281-9812");
		ss.setType(DataSource.Type.FullTextIndex.toString());
		ss.setHostingNode("FTIndexGHNTwo");
		ss.setEndpoint("http://nowhere2.com/wsrf/index/fulltext");
		serviceItems.put(ss.getID(), ss);
		
		s = new FTIndexDao();
		s.setID("768a-8ab8-1281-9812");
		s.setType(DataSource.Type.FullTextIndex.toString());
		s.setFunctionality("search.index.ft");
		
		s.getCapabilities().clear();
		s.getCapabilities().add("any");
		
		s.getFields().clear();
		s.getScopes().add("/no/scope/");
		ff=new ArrayList<String>();
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:s:type");
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:p:type");
		for(String f:ff)
		{
			String []fparts=f.split(":");
			if(fparts.length!=4) continue;
			FieldIndexContainerDao fc=new FieldIndexContainerDao();
			fc.setID(s.getID()+":"+f);
			fc.setCollection(fparts[0]);
			fc.setLanguage(fparts[1]);
			fc.setFieldType(fparts[2]);
			fc.setField(fparts[3]);
			s.getFields().add(fc.getID());
			fieldItems.add(fc);
		}
		datasourceItems.put(s.getID(), s);
		///////////////////////
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(datasourceItems.values());
		retValue.addAll(serviceItems.values());
		retValue.addAll(fieldItems);
		return retValue;
	}
	/*
	private static Set<IDaoElement> getFWIndex() throws Exception
	{
		logger.info("getting forward index");
		//DataSource.addSubType(DataSource.Type.ForwardIndex, FWIndexService.class, FWIndexServiceDao.class);
		//return getDataSource(FWIndexDao.class.getName(), FWIndexServiceDao.class.getName(),"Index", "ForwardIndexLookup", "search.index.fw",  DataSource.Type.ForwardIndex, "forward");
		return getDataSource(FWIndexDao.class.getName(), FWIndexServiceDao.class.getName(), "Index", "ForwardIndexNode", "search.index.fw", DataSource.Type.ForwardIndex, "forward index");
	}
	
	private static Set<IDaoElement> getGeoIndex() throws Exception
	{
		logger.info("getting geo index");
		//DataSource.addSubType(DataSource.Type.GeoIndex, GeoIndexService.class, GeoIndexServiceDao.class);
		return getDataSource(GeoIndexDao.class.getName(), GeoIndexServiceDao.class.getName(), "Index",  "GeoIndexLookup", "search.index.geo", DataSource.Type.GeoIndex, "geo");
	}*/
	
	private static Set<IDaoElement> getOpenSearchDataSource() throws Exception
	{
		logger.info("getting opensearch datasource");
		return getDataSourceOpenSearch();
		//DataSource.addSubType(DataSource.Type.OpenSearch, OpenSearchService.class, OpenSearchServiceDao.class);
		//return getDataSource(OpenSearchDataSourceDao.class.getName(), OpenSearchDataSourceServiceDao.class.getName(),"OpenSearch", "OpenSearchDataSource",  "search.index.opensearch",  DataSource.Type.OpenSearch, "opensearch");
	}
	
	
	private static Set<IDaoElement> getSruConsumerDataSource() throws Exception
	{
		logger.info("getting sru consumer datasource");
		return getDataSourceSruConsumer();
	}
	
	/*
	private static Set<IDaoElement> getFakeFWIndex() throws Exception
	{
		//DataSource.addSubType(DataSource.Type.ForwardIndex, FWIndexService.class, FWIndexServiceDao.class);
		
		Map<String, DataSourceDao> datasourceItems=new HashMap<String, DataSourceDao>();
		Map<String, DataSourceServiceDao> serviceItems=new HashMap<String, DataSourceServiceDao>();
		
		Set<FieldIndexContainerDao> fieldItems=new HashSet<FieldIndexContainerDao>();
		FWIndexServiceDao ss = new FWIndexServiceDao();
		ss.setID("8b77-1111-89b1-c128");
		ss.setType(DataSource.Type.ForwardIndex.toString());
		ss.setFunctionality("search.index.fw");
		ss.setHostingNode("FWIndexGHNOne");
		ss.setEndpoint("http://nowhere3.com/wsrf/index/forward");
		ss.getScopes().add("/no/scope/");
		serviceItems.put(ss.getID(), ss);
		
		FWIndexDao s = new FWIndexDao();
		s.setID("8b77-1111-89b1-c128");
		s.setType(DataSource.Type.ForwardIndex.toString());
		s.setFunctionality("search.index.fw");
		
		s.getCapabilities().clear();
		s.getCapabilities().add("any");
		
		s.getFields().clear();
		s.getScopes().add("/no/scope/");
		List<String> ff=new ArrayList<String>();
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:s:identifier");
		ff.add("3572c6f0-2f5e-11df-a838-c20ddc2e724e:en:p:identifier");
		for(String f:ff)
		{
			String []fparts=f.split(":");
			if(fparts.length!=4) continue;
			FieldIndexContainerDao fc=new FieldIndexContainerDao();
			fc.setID(s.getID()+":"+f);
			fc.setCollection(fparts[0]);
			fc.setLanguage(fparts[1]);
			fc.setFieldType(fparts[2]);
			fc.setField(fparts[3]);
			s.getFields().add(fc.getID());
			fieldItems.add(fc);
		}
		datasourceItems.put(s.getID(), s);
		////////////////////
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(datasourceItems.values());
		retValue.addAll(fieldItems);
		return retValue;
		
		//return getDataSource(FWIndexServiceDao.class.getName(), "ForwardIndexLookup", "Index", "search.index.fw", "forward");
	}*/
	
	
	private static Set<IDaoElement> getWorkflowService() throws Exception 
	{
		List<String> scopes = BridgeHelper.scopes;
		Map<String,WorkflowServiceDao> serviceItems=new HashMap<String,WorkflowServiceDao>();
		
		for(String scope : scopes) {
			String serviceName = "WorkflowEngineService";
			String serviceClass = "Execution";
			
			Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstances(serviceName, serviceClass, scope);
			
			logger.info("Found "+insts.size()+" workflow services in scope");
			
			for (RunInstance inst :  insts){
				Map<String, URI> eprs = inst.getProfile().accessPoint.runningInstanceInterfaces;
				
				if(eprs.size()!=1) continue;
				
				Set<String> instScopes = new HashSet<String>();
				for (String instSc : inst.getScopes())
					instScopes.add((String) instSc);
				
				if (serviceItems.containsKey(inst.getId())){
					serviceItems.get(inst.getId()).getScopes().addAll(instScopes);
					
				} else {
					WorkflowServiceDao s=new WorkflowServiceDao();

					Entry<String, URI> epr = eprs.entrySet().iterator().next();
					
					logger.trace("---- WorkflowService uri    : " + epr.getValue().toString());
					logger.trace("---- WorkflowService name   : " + epr.getKey());
					logger.trace("---- WorkflowService string : " + epr.toString());
					s.setEndpoint(epr.getValue().toString());
					
					s.setFunctionality("execution.workflow");
					s.setID(inst.getId());
					s.setHostingNode(inst.getProfile().ghn.ghnId);
					s.getScopes().addAll(new HashSet<String>(instScopes));
					serviceItems.put(s.getID(), s);
				}
			}
		}
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(serviceItems.values());
		return retValue;
	}
	
	@SuppressWarnings("unused")
	private static Set<IDaoElement> getSearchService()
	{
		return searchSystemServices;
	}
	
	public static final String ENDPOINT_KEY = "resteasy-servlet";
	
	private static Set<IDaoElement> retrieveSearchService() throws Exception 
	{
		List<String> scopes = BridgeHelper.scopes;
		Map<String,SearchServiceDao> serviceItems=new HashMap<String,SearchServiceDao>();
		
		for(String scope : scopes) {
			logger.info("Searching for search system services in scope "+scope.toString());
			
			String serviceName = "SearchSystemService";
			String serviceClass = "Search";
			
			Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey(serviceName, serviceClass, ENDPOINT_KEY, scope);
			
			logger.info("Found "+insts.size()+" search services in scope");
			
			for (RunInstance inst : insts){
				if (inst != null && inst.getProfile().accessPoint.runningInstanceInterfaces != null){
					Set<String> instScopes = new HashSet<String>();
					for (String instSc : inst.getScopes())
						instScopes.add(instSc);
					
					logger.info("Scopes of " + inst.getId() + " : " + instScopes);
					
					if (serviceItems.containsKey(inst.getId())){
						serviceItems.get(inst.getId()).getScopes().addAll(instScopes);
					} else {
						SearchServiceDao s=new SearchServiceDao();
						
						s.setEndpoint(inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY).toString());
						s.setFunctionality("search.orchestrator");
						s.setID(inst.getId());
						s.setHostingNode(inst.getProfile().ghn.ghnId);
						s.getScopes().addAll(new HashSet<String>(instScopes));
						serviceItems.put(s.getID(), s);
					}
				}
			}
		}
		
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(serviceItems.values());
		return retValue;
	}
	
	
	private static Set<IDaoElement> getExecutionServer() throws Exception 
	{
		List<String> scopes = BridgeHelper.scopes;
		Map<String,ExecutionServerDao> serverItems=new HashMap<String,ExecutionServerDao>();
		Map<String,ExecutionServiceDao> serviceItems=new HashMap<String,ExecutionServiceDao>();
		for(String scope : scopes)
		{
			logger.info("Searching for execution engine services in scope "+scope.toString());
			
			String serviceName = "ExecutionEngineService";
			String serviceClass = "Execution";

			Set<RunInstance> insts = BackendConnector.newICollector().discoverRunningInstancesFilteredByEndopointKey(serviceName, serviceClass, ENDPOINT_KEY, scope);
			
			logger.info("Found "+insts.size()+" execution services in scope");
			
			
			for (RunInstance inst : insts){
				try {
					if (inst == null || inst.getProfile().accessPoint.runningInstanceInterfaces == null)
						continue;
					
					URI epr = inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY);
					if (epr == null){
						logger.info("running instance : " + inst.getId() + " has no execution engine service epr");
						continue;
					}
					
					Set<String> instScopes = new HashSet<String>();
					for (String instSc : inst.getScopes())
						instScopes.add(instSc);
					
					logger.info("Scopes of execution engine service with id : " + inst.getId() + " : " + instScopes);
					
					if (serviceItems.containsKey(inst.getId())){
						serviceItems.get(inst.getId()).getScopes().addAll(instScopes);
					} else {
						ExecutionServiceDao s=new ExecutionServiceDao();
						
						s.setEndpoint(epr.toString());
						s.setFunctionality("execution.execute");
						s.setID(inst.getId());
						s.setHostingNode(inst.getProfile().ghn.ghnId);
						s.getScopes().addAll(new HashSet<String>(instScopes));
						serviceItems.put(s.getID(), s);
					}
					
					String sd = null;
					
//					String strNode = XMLConverter.convertToXML(inst.getProfile().specificData);
//					logger.trace("strNode : " + strNode);
					
					XPathEvaluator xpath = new XPathEvaluator(inst.getProfile().specificData.root);
					List<String> specificDataList = xpath.evaluate("/");
					if (specificDataList != null)
						for (String val: specificDataList){
							sd = val;
						}
					
	
					
					if(sd==null || sd.trim().length()==0) continue;
//					sd = sd.substring("<doc>".length());
//					sd = sd.substring(0, sd.length() - "</doc>".length());
	
//					logger.trace(sd);
					Document doc = XMLUtils.Deserialize(sd);
					List<Element> elems=XMLUtils.GetChildElementsWithName(doc.getDocumentElement(), "element");
					String hostname=null;
					String port=null;
					String elemId=null;
					
					
					for(Element el : elems)
					{
						elemId=XMLUtils.GetAttribute(el, "id");
						Element dynElem=XMLUtils.GetChildElementWithName(el, "dynamic");
						List<Element> dynPairs=XMLUtils.GetChildElementsWithName(dynElem, "entry");
						for(Element pair : dynPairs)
						{
							if(!XMLUtils.AttributeExists(pair, "pe2ng.port")) //this is preserved for backward compatibility
							{
								if(!XMLUtils.AttributeExists(pair, "key")) continue;
								String attrVal = XMLUtils.GetAttribute(pair, "key"); 
								if(attrVal != null && attrVal.equals("pe2ng.port"))
									port = XMLUtils.GetChildText(pair);
							}
							else port=XMLUtils.GetAttribute(pair, "pe2ng.port");
							if(!XMLUtils.AttributeExists(pair, "hostname")) //this is preserved for backward compatibility
							{
								if(!XMLUtils.AttributeExists(pair, "key")) continue;
								String attrVal = XMLUtils.GetAttribute(pair, "key"); 
								if(attrVal == null || !attrVal.equals("hostname")) continue;
								hostname = XMLUtils.GetChildText(pair);
							}
							else hostname=XMLUtils.GetAttribute(pair, "hostname");
							break;
						}
						if(port!=null && hostname!=null) break;
					}
					if(port!=null /*&& hostname!=null*/) //TODO see if host name is needed
					{
						if(serverItems.containsKey(elemId)) serverItems.get(elemId).getScopes().addAll(instScopes);
						else
						{
							ExecutionServerDao ss=new ExecutionServerDao();
							ss.setFunctionality("execution.execute");
							ss.setHostingNode(inst.getProfile().ghn.ghnId);
							ss.setID(elemId);
							ss.setHostname(hostname);
							ss.setPort(port);
							ss.getScopes().addAll(instScopes);
							serverItems.put(ss.getID(), ss);
						}
					}
					logger.info("found execution server at : " + hostname + " : " + port);
				} catch (Exception e) {
					logger.warn( "Error parsing the running instance : " + inst.getId() , e);
				}
			}
			
//			for(GCUBERunningInstance inst : result)
//			{
//				List<Endpoint> eprs= inst.getAccessPoint().getRunningInstanceInterfaces().getEndpoint();
//				if(eprs.size()!=1) continue;
//				if(serviceItems.containsKey(inst.getID())) serviceItems.get(inst.getID()).getScopes().addAll(new HashSet<String>(inst.getScopes().keySet()));
//				else
//				{
//					ExecutionServiceDao s=new ExecutionServiceDao();
//					s.setEndpoint(eprs.get(0).getValue());
//					s.setFunctionality("execution.execute");
//					s.setID(inst.getID());
//					s.setHostingNode(inst.getGHNID());
//					s.getScopes().addAll(new HashSet<String>(inst.getScopes().keySet()));
//					serviceItems.put(s.getID(), s);
//				}
//				String sd = inst.getSpecificData();
//				if(sd==null || sd.trim().length()==0) continue;
//				Document doc = XMLUtils.Deserialize(sd);
//				List<Element> elems=XMLUtils.GetChildElementsWithName(doc.getDocumentElement(), "element");
//				String hostname=null;
//				String port=null;
//				String elemId=null;
				
				
				
				
				
				
//				for(Element el : elems)
//				{
//					elemId=XMLUtils.GetAttribute(el, "id");
//					Element dynElem=XMLUtils.GetChildElementWithName(el, "dynamic");
//					List<Element> dynPairs=XMLUtils.GetChildElementsWithName(dynElem, "entry");
//					for(Element pair : dynPairs)
//					{
//						if(!XMLUtils.AttributeExists(pair, "pe2ng.port")) //this is preserved for backward compatibility
//						{
//							if(!XMLUtils.AttributeExists(pair, "key")) continue;
//							String attrVal = XMLUtils.GetAttribute(pair, "key"); 
//							if(attrVal != null && attrVal.equals("pe2ng.port"))
//								port = XMLUtils.GetChildText(pair);
//						}
//						else port=XMLUtils.GetAttribute(pair, "pe2ng.port");
//						if(!XMLUtils.AttributeExists(pair, "hostname")) //this is preserved for backward compatibility
//						{
//							if(!XMLUtils.AttributeExists(pair, "key")) continue;
//							String attrVal = XMLUtils.GetAttribute(pair, "key"); 
//							if(attrVal == null || !attrVal.equals("hostname")) continue;
//							hostname = XMLUtils.GetChildText(pair);
//						}
//						else hostname=XMLUtils.GetAttribute(pair, "hostname");
//						break;
//					}
//					if(port!=null && hostname!=null) break;
//				}
//				if(port!=null /*&& hostname!=null*/) //TODO see if host name is needed
//				{
//					if(serverItems.containsKey(elemId)) serverItems.get(elemId).getScopes().addAll(new HashSet<String>(inst.getScopes().keySet()));
//					else
//					{
//						ExecutionServerDao ss=new ExecutionServerDao();
//						ss.setFunctionality("execution.execute");
//						ss.setHostingNode(inst.getGHNID());
//						ss.setID(elemId);
//						ss.setHostname(hostname);
//						ss.setPort(port);
//						ss.getScopes().addAll(new HashSet<String>(inst.getScopes().keySet()));
//						serverItems.put(ss.getID(), ss);
//					}
//				}
//			}
		}
		Set<IDaoElement> retValue=new HashSet<IDaoElement>();
		retValue.addAll(serviceItems.values());
		retValue.addAll(serverItems.values());
		logger.info("Found "+serverItems.values().size()+" execution servers");
		return retValue;
	}
	
	private static Set<IDaoElement> getFunctionality() throws Exception 
	{
		Set<IDaoElement> items = new HashSet<IDaoElement>();
		FunctionalityDao f=new FunctionalityDao();
		f.setName("execution.execute");
		items.add(f);
		f=new FunctionalityDao();
		f.setName("execution.workflow");
		items.add(f);
		f=new FunctionalityDao();
		f.setName("search.index.ft");
		items.add(f);
		f=new FunctionalityDao();
		f.setName("search.index.fw");
		items.add(f);
		f=new FunctionalityDao();
		f.setName("search.index.geo");
		items.add(f);
		f=new FunctionalityDao();
		f.setName("search.index.opensearch");
		items.add(f);
		f=new FunctionalityDao();
		f.setName("search.sruconsumer");
		items.add(f);
		return items;
	}
	
	static Set<IDaoElement> getHostingNodes() throws Exception 
	{
		List<String> scopes = BridgeHelper.scopes;
		HashMap<String, HostingNodeDao> items=new HashMap<String, HostingNodeDao>();
		for(String scope : scopes) {
			logger.info("Searching for hosting nodes in scope "+scope.toString());
			
			List<HostNode> insts = BackendConnector.newICollector().discoverHostingNodes(scope);
			
			logger.info("Found "+insts.size()+" nodes in scope");
			
			for(HostNode node : insts)
			{
				Set<String> nodeScopes = new HashSet<String>();
				for (String instSc : node.getScopes())
					nodeScopes.add(instSc);
				
//				logger.trace(XMLConverter.convertToXML(node));
				
				if(items.containsKey(node.getId()))
				{
					items.get(node.getId()).getScopes().addAll(nodeScopes);
					//logger.info("updated node of : \n"+items.get(node.getID()).deepToString());
				}
				else
				{			
					HostingNodeDao item=new HostingNodeDao();
					item.setID(node.getId());
					item.setScopes(new HashSet<String>(nodeScopes));
					try{item.getPairKeys().add("hn.infrastructure"); item.getPairValues().add("hn.infrastructure"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/Infrastructure/text()").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.country"); item.getPairValues().add("hn.country"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/Site/Country/text()").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.domain"); item.getPairValues().add("hn.domain"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/Site/Domain/text()").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.latitude"); item.getPairValues().add("hn.latitude"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/Site/Latitude/text()").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.longitude"); item.getPairValues().add("hn.longitude"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/Site/Longitude/text()").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.location"); item.getPairValues().add("hn.location"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/Site/Location/text()").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.architecture.platform"); item.getPairValues().add("hn.architecture.platform"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Architecture/@PlatformType").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.architecture.smp"); item.getPairValues().add("hn.architecture.smp"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Architecture/@SMPSize").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.architecture.smt"); item.getPairValues().add("hn.architecture.smt"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Architecture/@SMTSize").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.benchmark.sf00"); item.getPairValues().add("hn.benchmark.sf00"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Benchmark/@SF00").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.benchmark.si00"); item.getPairValues().add("hn.benchmark.si00"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Benchmark/@SI00").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.load.one_day"); item.getPairValues().add("hn.load.one_day"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/HistoricalLoad/@Last1Day").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.load.one_hour"); item.getPairValues().add("hn.load.one_hour"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/HistoricalLoad/@Last1H").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.load.one_week"); item.getPairValues().add("hn.load.one_week"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/HistoricalLoad/@Last1Week").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.load.one_min"); item.getPairValues().add("hn.load.one_min"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Load/@Last1Min").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.load.five_min"); item.getPairValues().add("hn.load.five_min"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Load/@Last5Min").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.load.fifteen_min"); item.getPairValues().add("hn.load.fifteen_min"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Load/@Last15Min").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.disk.size"); item.getPairValues().add("hn.disk.size"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/LocalAvailableSpace/text()").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.memory.physical.available"); item.getPairValues().add("hn.memory.physical.available"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/MainMemory/@RAMAvailable").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.memory.physical.size"); item.getPairValues().add("hn.memory.physical.size"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/MainMemory/@RAMSize").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.memory.virtual.size"); item.getPairValues().add("hn.memory.virtual.size"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/MainMemory/@VirtualSize").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.memory.virtual.available"); item.getPairValues().add("hn.memory.virtual.available"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/MainMemory/@VirtualAvailable").get(0));}catch(Exception ex){  }
					
					try{item.getPairKeys().add("hostname"); item.getPairValues().add("hostname"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Name/text()").get(0).substring(0, node.evaluate("/Profile/GHNDescription/Name/text()").get(0).lastIndexOf(':')));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.port"); item.getPairValues().add("hn.port"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Name/text()").get(0).substring(node.evaluate("/Profile/GHNDescription/Name/text()").get(0).lastIndexOf(':')+1));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.hostname"); item.getPairValues().add("hn.hostname"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Name/text()").get(0));}catch(Exception ex){  }

					int count=0;
					try{
						for (int i = 1; i <= Integer.parseInt(node.evaluate("count(/Profile/GHNDescription/NetworkAdapter)").get(0)); i++){
							try{item.getPairKeys().add("hn.network.adapter."+count+".inbound.ip"); item.getPairValues().add("hn.network.adapter."+count+".inbound.ip"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/NetworkAdapter[" + i + "]/@InboundIP").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.network.adapter."+count+".ip.address"); item.getPairValues().add("hn.network.adapter."+count+".ip.address"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/NetworkAdapter[" + i + "]/@IPAddress").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.network.adapter."+count+".mtu"); item.getPairValues().add("hn.network.adapter."+count+".mtu"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/NetworkAdapter[" + i + "]/@MTU").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.network.adapter."+count+".name"); item.getPairValues().add("hn.network.adapter."+count+".name"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/NetworkAdapter[" + i + "]/@Name").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.network.adapter."+count+".outbound.ip"); item.getPairValues().add("hn.network.adapter."+count+".outbound.ip"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/NetworkAdapter[" + i + "]/@OutboundIP").get(0));}catch(Exception ex){  }
							count+=1;
						}
					}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.os.name"); item.getPairValues().add("hn.os.name"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/OperatingSystem/@Name").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.os.release"); item.getPairValues().add("hn.os.release"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/OperatingSystem/@Release").get(0));}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.os.version"); item.getPairValues().add("hn.os.version"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/OperatingSystem/@Version").get(0));}catch(Exception ex){  }
					count=0;
					try{
						long totalBogoMips = 0;
						long totalClockSpeed = 0;
						
						for (int i = 1; i <= Integer.parseInt(node.evaluate("count(/Profile/GHNDescription/Processor)").get(0)); i++){
							try{item.getPairKeys().add("hn.processor."+count+".bogomips"); item.getPairValues().add("hn.processor."+count+".bogomips"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@Bogomips").get(0)); totalBogoMips+=Double.valueOf(node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@Bogomips").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".cache.l1"); item.getPairValues().add("hn.processor."+count+".cache.l1"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@CacheL1").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".cache.l1d"); item.getPairValues().add("hn.processor."+count+".cache.l1d"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@CacheL1D").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".cache.l1i"); item.getPairValues().add("hn.processor."+count+".cache.l1i"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@CacheL1I").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".cache.l2"); item.getPairValues().add("hn.processor."+count+".cache.l2"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@CacheL2").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".clockspeed"); item.getPairValues().add("hn.processor."+count+".clockspeed"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@ClockSpeedMhz").get(0)); totalClockSpeed+=Double.valueOf(node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@ClockSpeedMhz").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".family"); item.getPairValues().add("hn.processor."+count+".clockspeed"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@Family").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".model"); item.getPairValues().add("hn.processor."+count+".model"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@Model").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".model_name"); item.getPairValues().add("hn.processor."+count+".model_name"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@ModelName").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("hn.processor."+count+".vendor"); item.getPairValues().add("hn.processor."+count+".vendor"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Processor[" + i + "]/@Vendor").get(0));}catch(Exception ex){  }
							count+=1;
						}
						

						item.getPairKeys().add("hn.processor.count"); item.getPairValues().add("hn.processor.count" + HostingNode.KeyValueDelimiter + Integer.toString(count));
						item.getPairKeys().add("hn.processor.total_bogomips"); item.getPairValues().add("hn.processor.total_bogomips" + HostingNode.KeyValueDelimiter + Long.toString(totalBogoMips));
						item.getPairKeys().add("hn.processor.total_clockspeed"); item.getPairValues().add("hn.processor.total_clockspeed" + HostingNode.KeyValueDelimiter + Long.toString(totalClockSpeed));
					}catch(Exception ex){  }
					
					
					try{item.getPairKeys().add("hn.status"); item.getPairValues().add("hn.status"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Status/text()").get(0));}catch(Exception ex){  }
					count=0;
					try{
//						Iterator<org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.StorageDevice> it = node.profile().description().storageDevices().iterator();
//						while(it.hasNext()){
//							org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.StorageDevice sd = it.next();
//
//							try{item.getPairKeys().add("hn.disk.device."+count+".name"); item.getPairValues().add("hn.disk.device."+count+".name"+ HostingNode.KeyValueDelimiter+ sd.name());}catch(Exception ex){}
//							try{item.getPairKeys().add("hn.disk.device."+count+".size"); item.getPairValues().add("hn.disk.device."+count+".size"+ HostingNode.KeyValueDelimiter+ Long.toString(sd.size()));}catch(Exception ex){}
//							try{item.getPairKeys().add("hn.disk.device."+count+".transfer_rate"); item.getPairValues().add("hn.disk.device."+count+".transfer_rate"+ HostingNode.KeyValueDelimiter+ Long.toString(sd.transferRate()));}catch(Exception ex){}
//							try{item.getPairKeys().add("hn.disk.device."+count+".type"); item.getPairValues().add("hn.disk.device."+count+".type"+ HostingNode.KeyValueDelimiter+ sd.type());}catch(Exception ex){}
//							int newCount=0;
//							try{
//								Iterator<org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.StoragePartition> it_sp = node.profile().description().storagePartitions().iterator();
//								while (it_sp.hasNext()) {
//									org.gcube.common.resources.gcore.HostingNode.Profile.NodeDescription.StoragePartition sdp = it_sp.next();
//
//									try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".name"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".name"+ HostingNode.KeyValueDelimiter+ sdp.name());}catch(Exception ex){}
//									try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".read_rate"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".read_rate"+ HostingNode.KeyValueDelimiter+Long.toString(sdp.readRate()));}catch(Exception ex){}
//									try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".size"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".size"+ HostingNode.KeyValueDelimiter+ sdp.size());}catch(Exception ex){}
//									try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".write_rate"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".write_rate"+ HostingNode.KeyValueDelimiter+Long.toString(sdp.writeRate()));}catch(Exception ex){}
//									int deepCount=0;
//									try{
//										
//										for(FileSystem sdpfs : sdp.getFileSystems().values())
//										{
//											try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".name"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".name"+ HostingNode.KeyValueDelimiter+sdpfs.getName());}catch(Exception ex){}
//											try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".root"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".root"+ HostingNode.KeyValueDelimiter+sdpfs.getRoot());}catch(Exception ex){}
//											try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".size"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".size"+ HostingNode.KeyValueDelimiter+Long.toString(sdpfs.getSize()));}catch(Exception ex){}
//											try{item.getPairKeys().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".type"); item.getPairValues().add("hn.disk.device."+count+".partition."+newCount+".filesystem."+deepCount+".type"+ HostingNode.KeyValueDelimiter+sdpfs.getType());}catch(Exception ex){}
//											deepCount+=1;
//										}
//									}catch(Exception ex){}
//									newCount+=1;
//								}
//							}catch(Exception ex){}
//							count+=1;
//						}
					}catch(Exception ex){  }
					try{item.getPairKeys().add("hn.uptime"); item.getPairValues().add("hn.uptime"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/GHNDescription/Uptime/text()").get(0));}catch(Exception ex){  }
					try{
						for (int i = 1; i <= Integer.parseInt(node.evaluate("count(/Profile/GHNDescription/NetworkAdapter)").get(0)); i++) {
							String identifier=node.evaluate("/Profile/DeployedPackages/Package[" + i + "]/ServiceClass/text()").get(0) + 
									"." + node.evaluate("/Profile/DeployedPackages/Package[" + i + "]/ServiceName/text()").get(0) + 
									"."+ node.evaluate("/Profile/DeployedPackages/Package[" + i + "]/PackageName/text()").get(0);
							try{item.getPairKeys().add("software."+identifier+".deployed"); item.getPairValues().add("software."+identifier+".deployed"+ HostingNode.KeyValueDelimiter+ "true");}catch(Exception ex){  }
							try{item.getPairKeys().add("software."+identifier+".service_version"); item.getPairValues().add("software."+identifier+".service_version"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/DeployedPackages/Package[" + i + "]/ServiceVersion/text()").get(0));}catch(Exception ex){  }
							try{item.getPairKeys().add("software."+identifier+".package_version"); item.getPairValues().add("software."+identifier+".package_version"+ HostingNode.KeyValueDelimiter+ node.evaluate("/Profile/DeployedPackages/Package[" + i + "]/PackageVersion/text()").get(0));}catch(Exception ex){  }
						}
					}catch(Exception ex){  }
					try{
						for (int i = 1; i <= Integer.parseInt(node.evaluate("count(/Profile/GHNDescription/RunTimeEnv/Variable)").get(0)); i++) {
							String key = node.evaluate("/Profile/GHNDescription/RunTimeEnv/Variable[" + i + "]/Key/text()").get(0);
							String value = node.evaluate("/Profile/GHNDescription/RunTimeEnv/Variable[" + i + "]/Value/text()").get(0);
							try{item.getPairKeys().add(key); item.getPairValues().add(key+ HostingNode.KeyValueDelimiter+ value);}catch(Exception ex){  }
						}
					}catch(Exception ex){  }
					items.put(item.getID(), item);
					//logger.info("added hosting node of : \n"+item.deepToString());
				}
			}
		}
		
		logger.info("number of hosting nodes found : " + items.values().size());
		
		return new HashSet<IDaoElement>(items.values());
	}
	
	private static Set<IDaoElement> getAllCollections() throws Exception {
		Set<IDaoElement> dataCollections = new HashSet<IDaoElement>();
		dataCollections.addAll(getDataCollections());
		dataCollections.addAll(getTreeCollections());
		
		return dataCollections;
	}
	
	
	private static Set<IDaoElement> getDataCollections() throws Exception 
	{
		List<String> scopes = BridgeHelper.scopes;
		
		HashMap<String, DataCollectionDao> hcols=new HashMap<String, DataCollectionDao>();
		
		for(String scope : scopes) {
			List<Resource> resources = BackendConnector.newICollector().getGenericResourcesByType("DataSource", scope);
			for (Resource resource : resources) {
				String id = resource.getResourceID();
				 logger.info("Found collection with id : " + id);
				 if (hcols.containsKey(id)) {
					 hcols.get(id).getScopes().add(scope.toString());
				 } else {
				   try {
					String name = resource.getName();
					logger.info("collection with id : " + id + " has name : " + name);
					String description = resource.getDescription();
					DataSourceDescription datasourceDescription= DataSourceDescription.getCollection(resource);
					Boolean isUserCollection = datasourceDescription.isUser();
					Calendar creationTime = datasourceDescription.getCreationTime();
					String type = datasourceDescription.getType();

					if (type != null && (type.equalsIgnoreCase("opensearch") || type.equalsIgnoreCase("sru"))){
						logger.info("collection with id : " + id + " name : " + name + " is of type : " + type);	
					} else {
						logger.info("collection with id : " + id + " name : " + name + " is not of type : opensearch");
						
						if (type == null){
							logger.info("no type given skipping");
							continue;
						}
					}
					
					if(!isUserCollection) continue;
					DataCollectionDao d=new DataCollectionDao();
					d.setID(id);
					d.setDescription(description);
					d.setCollectionType(type);
					if(creationTime != null) d.setCreationTime(Long.toString(creationTime.getTimeInMillis()));
					d.setName(name);
					d.getScopes().add(scope.toString());
					hcols.put(d.getID(), d);
					if(d.getDescription() == null || d.getDescription().trim().length()==0) d.setDescription(null);
					if(d.getName().trim().length()==0) d.setName(null);
					Set<String> toDel=new HashSet<String>();
					for(String s : d.getScopes()) if(s.trim().length()==0) toDel.add(s);
					d.getScopes().removeAll(toDel);
					logger.info("collection with id : " + id + " has name : " + name + " type : " + type);
				 } catch (Exception e) {
						logger.warn( "problem getting the resource of : " + resource.getResourceID(), e);
					}
				 }
			}
		}
		
		
		
			
//			logger.info("Searching for collection in scope "+scope.toString());
//			List<Collection> cols = org.gcube.contentmanagement.gcubedocumentlibrary.util.Collections.list(scope);
//			
//			logger.info("Found "+cols.size()+" collections in scope");
//			for(Collection col : cols)
//			{
//				if(hcols.containsKey(col.getId()))
//				{
//					hcols.get(col.getId()).getScopes().add(scope.toString());
//					//logger.info("updated collection of : \n"+hcols.get(col.getId()).deepToString());
//				}
//				else
//				{
//					if(!col.isUserCollection()) continue;
//					DataCollectionDao d=new DataCollectionDao();
//					d.setID(col.getId());
//					d.setDescription(col.getDescription());
//					if(col.getCreationTime() != null) d.setCreationTime(Long.toString(col.getCreationTime().getTimeInMillis()));
//					d.setName(col.getName());
//					d.getScopes().add(scope.toString());
//					hcols.put(d.getID(), d);
//					if(d.getDescription() == null || d.getDescription().trim().length()==0) d.setDescription(null);
//					if(d.getName().trim().length()==0) d.setName(null);
//					Set<String> toDel=new HashSet<String>();
//					for(String s : d.getScopes()) if(s.trim().length()==0) toDel.add(s);
//					d.getScopes().removeAll(toDel);
//					//logger.info("added collection of : \n"+d.deepToString());
//				}
//			}
//		}
		return new HashSet<IDaoElement>(hcols.values());
	}
	
	private static final String JNDI_NAME = "gcube/data/tm";
	private static final String TREADER_NAME = JNDI_NAME+"/reader";
	private static final String SOURCENAME_RPNAME = "Name";
	private static final String SOURCEID_RPNAME = "SourceId";
//	private static final String CARDINALITY_RPNAME = "Cardinality";
	
	
	
	
	
	
	
	
	public static Set<IDaoElement> getTreeCollections() throws Exception {
		//String xquery = "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry'; declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider'; for $result in collection("/db/Properties")//Document  where ($result/Data/child::*[local-name()='ServiceName']/string() eq 'tree-manager-service')   and ($result/SourceKey/string() ne 'binder')  and ($result/SourceKey/string() ne 'manager')  return $result";
		List<String> scopes = BridgeHelper.scopes;
		HashMap<String, DataCollectionDao> hcols=new HashMap<String, DataCollectionDao>();
		
		logger.info(" will search for TREE COLLECTIONS in the following scopes");
		for(String scope : scopes) {
			logger.info("### " + scope.toString());
		}
		
		for(String scope : scopes) {
			logger.info(" searching for TREE COLLECTIONS in scope : " + scope);
			
			String serviceName = "tree-manager-service";
			String serviceClass = "DataAccess";
			 
			List<SerInstance> resources = BackendConnector.newICollector().discoverServiceInstances(serviceName, serviceClass, scope);
			 
			logger.info(" found " + resources.size() +  " TREE COLLECTIONS in scope : " + scope);
			for (SerInstance r : resources) {
				
				
				try {
					 String endpoint =  r.getEndpoint().getPath();
					 
					 if (endpoint.endsWith(TREADER_NAME)) {
						 logger.info("Parsing tree...");
					    	//String name =  result.evaluate("//*[local-name()='"+SOURCENAME_RPNAME+"']/text()").get(0);
					    	
						 XPathEvaluator xpath = new XPathEvaluator(r.getProperties().getCustomProperties());
							String name = xpath.evaluate("//*[local-name()='"+SOURCENAME_RPNAME+"']/text()").get(0);
					    	
					    	logger.info("\t name : "+name);
					    	//String id = result.evaluate("//*[local-name()='"+SOURCEID_RPNAME+"']/text()").get(0);
					    	
					    	xpath = new XPathEvaluator(r.getProperties().getCustomProperties());
							String id = xpath.evaluate("//*[local-name()='"+SOURCEID_RPNAME+"']/text()").get(0);
					    	
					    	logger.info("\t id : "+id);
					    	
					    	logger.info("Parsing tree...OK");
					    	
					    	//String totalItems = result.evaluate("//*[local-name()='"+CARDINALITY_RPNAME+"']/text()").get(0);
					    	String description = null;
					    	String creationTime = null;
					    	
					    	///logger.info("Parsed elements from xml : [id = " + id +", name = " + name + ", totalItems = " + totalItems + " ] ");
					    	logger.info("Parsed elements from xml : [id = " + id +", name = " + name + " ] ");
					    	
					    	if(hcols.containsKey(id))
							{
								hcols.get(id).getScopes().add(scope.toString());
								logger.info("updated collection of : \n"+hcols.get(id).deepToString());
								
								
								logger.info("+ added collection : "+name + " " + description + " scope : " + scope + " all scopes : " + hcols.get(id).getScopes());
							}
							else
							{
						    	DataCollectionDao d=new DataCollectionDao();
						    	d.setID(id);
						    	d.setName(name);
						    	d.setDescription(description);
						    	d.setCreationTime(creationTime);
						    	if(d.getDescription() == null || d.getDescription().trim().length()==0) d.setDescription(null);
								if(d.getName().trim().length()==0) d.setName(null);
						    	if (creationTime == null || creationTime.trim().length() == 0) d.setCreationTime(null);
						    	d.getScopes().add(scope.toString());
						    	
						    	hcols.put(d.getID(), d);
						    	
						    	Set<String> toDel=new HashSet<String>();
								for(String s : d.getScopes()) if(s.trim().length()==0) toDel.add(s);
								d.getScopes().removeAll(toDel);
								
								logger.info("added collection : \n"+hcols.get(id).deepToString());
								
								logger.info("+ added collection : "+name + " " + description + " scope : " + scope + " all scopes : " + d.getScopes());
							}
					 	}
					 }catch (Exception e) {
			    	logger.warn( "Error while retrieving-parsing the tree manager collection");
			    }
			
			}
		}
		
		
		
		
//		ISClient client = GHNContext.getImplementation(ISClient.class);
//		for(GCUBEScope scope : scopes) {
//			 logger.info("Searching for tree collection in scope : " + scope);
//			 WSResourceQuery query = client.getQuery(WSResourceQuery.class);
//			    	query.addAtomicConditions(
//			    		new AtomicCondition("//gc:ServiceName", "tree-manager-service"), new AtomicCondition("//gc:ServiceClass", "DataAccess")
//			    );
//			    List<RPDocument> results = client.execute(query, scope);
//			    for (RPDocument result : results) {
//			    	
//			    	try {
//					    String endpoint = result.getEndpoint().getAddress().getPath();
//					    if (endpoint.endsWith(TREADER_NAME)) {
//					    	logger.info("Parsing tree...");
//					    	String name = result.evaluate("//*[local-name()='"+SOURCENAME_RPNAME+"']/text()").get(0);
//					    	logger.info("\t name : "+name);
//					    	String id = result.evaluate("//*[local-name()='"+SOURCEID_RPNAME+"']/text()").get(0);
//					    	logger.info("\t id : "+id);
//					    	
//					    	logger.info("Parsing tree...OK");
//					    	
//					    	//String totalItems = result.evaluate("//*[local-name()='"+CARDINALITY_RPNAME+"']/text()").get(0);
//					    	String description = null;
//					    	String creationTime = null;
//					    	
//					    	///logger.info("Parsed elements from xml : [id = " + id +", name = " + name + ", totalItems = " + totalItems + " ] ");
//					    	logger.info("Parsed elements from xml : [id = " + id +", name = " + name + " ] ");
//					    	
//					    	if(hcols.containsKey(id))
//							{
//								hcols.get(id).getScopes().add(scope.toString());
//								logger.info("updated collection of : \n"+hcols.get(id).deepToString());
//							}
//							else
//							{
//						    	DataCollectionDao d=new DataCollectionDao();
//						    	d.setID(id);
//						    	d.setName(name);
//						    	d.setDescription(description);
//						    	d.setCreationTime(creationTime);
//						    	if(d.getDescription() == null || d.getDescription().trim().length()==0) d.setDescription(null);
//								if(d.getName().trim().length()==0) d.setName(null);
//						    	if (creationTime == null || creationTime.trim().length() == 0) d.setCreationTime(null);
//						    	d.getScopes().add(scope.toString());
//						    	
//						    	hcols.put(d.getID(), d);
//						    	
//						    	Set<String> toDel=new HashSet<String>();
//								for(String s : d.getScopes()) if(s.trim().length()==0) toDel.add(s);
//								d.getScopes().removeAll(toDel);
//								
//								logger.info("added collection : \n"+hcols.get(id).deepToString());
//							}
//					    }
//				    
//			    	}catch (Exception e) {
//				    	logger.warn( "Error while retrieving-parsing the tree manager collection");
//				    }
//				    
//			    }
//		}
		return new HashSet<IDaoElement>(hcols.values());	
	}
	
	private static Set<IDaoElement> getFields() throws Exception
	{
		logger.info("Searching for fields");
		Set<IDaoElement> items=new HashSet<IDaoElement>();
		Document fieldsDOM=null;
		String fieldsResource=FieldModel.getMainResource();
		logger.info("fieldsResource : " + fieldsResource );
		
		if (fieldsResource == null)
			return Sets.newHashSet();
		
//		XPathEvaluator xpath = new XPathEvaluator(FieldModel.mainResource.getBody());
//		
//		List<String> fieldIds = xpath.evaluate("//fieldId/text()");
		
		
		if(fieldsResource != null) fieldsDOM = XMLUtils.Deserialize(fieldsResource);
		else return items;

		boolean flatModel = false;
		List<Element> xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fields") , "field");
		if(xmlObjs.size()!=0)
			flatModel = true;
		else
			xmlObjs =XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fields"), "fieldId");
		
		
		if(flatModel)
		{
			for(Element elem : xmlObjs)
			{
					FieldDao f=new FieldDao();
					f.fromXML(elem);
					items.add(f);
			}
		}
		else
		{
			Set<String> fieldIds = FieldModel.getFieldIds();
			for(String fieldId : fieldIds)
			{
				String fieldResource=FieldModel.getFieldResource(fieldId);
				logger.trace("Field read from resource");
				if(fieldResource != null) 
				{
					fieldsDOM = XMLUtils.Deserialize(fieldResource);
					xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fieldInfo") , "field");
					
					logger.trace("field has elements : " + xmlObjs.size());
					for(Element elem : xmlObjs)
					{
						FieldDao f=new FieldDao();
						f.fromXML(elem);
						items.add(f);
					}
				}
			}
		}
		logger.info("Found " + items.size() + " fields");
		return items;
		
		
		
//			
//		
//		
//		
//		if(flatModel)
//		{
//			for(Element elem : xmlObjs)
//			{
//					FieldDao f=new FieldDao();
//					f.fromXML(elem);
//					items.add(f);
//			}
//		}
//		else
//		{
//			Set<String> fieldIds = FieldModel.getFieldIds();
		
		
		
//		for(String fieldId : fieldIds)
//			{
//				Resource fieldResource = FieldModel.getFieldResourceObj(fieldId);
//				
//				
//				XPathEvaluator xpath2 = new XPathEvaluator(fieldResource.getBody());
//				
//				List<String> fieldInfos = xpath2.evaluate("//fieldInfo");
//				
//				for (String elem : fieldInfos){
//					FieldDao f=new FieldDao();
//					
//					elem = elem.replace("<fieldInfo>", "");
//					elem = elem.replace("</fieldInfo>", "");
//					
//					Element el = (Element) XMLConverter.stringToNode(elem);
//					
//					f.fromXML(el);
//					items.add(f);
//				}
//				
//				
////				String fieldResource=FieldModel.getFieldResource(fieldId);
////				logger.trace("Field read from resource");
////				if(fieldResource != null) 
////				{
////					fieldsDOM = XMLUtils.Deserialize(fieldResource);
////					List<Element> xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fieldInfo") , "field");
////					
////					logger.trace("field has elements : " + xmlObjs.size());
////					for(Element elem : xmlObjs)
////					{
////						FieldDao f=new FieldDao();
////						f.fromXML(elem);
////						items.add(f);
////					}
////				}
//			}
////		}
//		logger.info("Found " + items.size() + " fields");
//		return items;
	}
	
	private static Set<IDaoElement> getSearchables() throws Exception
	{
		logger.info("Searching for searchables");
		Set<IDaoElement> items=new HashSet<IDaoElement>();
		Document fieldsDOM=null;
		String fieldsResource=FieldModel.getMainResource();
		if(fieldsResource != null) fieldsDOM = XMLUtils.Deserialize(fieldsResource);
		else return items;
		boolean flatModel = false;
		List<Element> xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "searchables") , "searchable");
		if(xmlObjs.size()!=0)
			flatModel=true;
		else
			xmlObjs =XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fields"), "fieldId");
		
		if(flatModel)
		{
			for(Element elem : xmlObjs)
			{
				SearchableDao f=new SearchableDao();
				f.fromXML(elem);
				items.add(f);
			}
		}
		else
		{
			Set<String> fieldIds = FieldModel.getFieldIds();
			for(String fieldId : fieldIds)
			{
				String fieldResource=FieldModel.getFieldResource(fieldId);
				if(fieldResource != null) 
				{
					fieldsDOM = XMLUtils.Deserialize(fieldResource);
					xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "searchables") , "searchable");
					for(Element elem : xmlObjs)
					{
						SearchableDao f=new SearchableDao();
						f.fromXML(elem);
						items.add(f);
					}
				}
			}
		}
		logger.info("Found " + items.size() + " searchables");
		//logger.trace("Found " + items.size() + " searchables");
		return items;
	}
	
	private static Set<IDaoElement> getPresentables() throws Exception
	{
		logger.info("Searching for presentables");
		Set<IDaoElement> items=new HashSet<IDaoElement>();
		Document fieldsDOM=null;
		String fieldsResource=FieldModel.getMainResource();
		if(fieldsResource != null) fieldsDOM = XMLUtils.Deserialize(fieldsResource);
		else return items;
		boolean flatModel = false;
		List<Element> xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "presentables") , "presentable");
		if(xmlObjs.size()!=0)
			flatModel=true;
		else
			xmlObjs =XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "fields"), "fieldId");
		
		if(flatModel)
		{
			for(Element elem : xmlObjs)
			{
				PresentableDao f=new PresentableDao();
				f.fromXML(elem);
				items.add(f);
			}
		}
		else
		{
			Set<String> fieldIds = FieldModel.getFieldIds();
			for(String fieldId : fieldIds)
			{
				String fieldResource=FieldModel.getFieldResource(fieldId);
				if(fieldResource != null) 
				{
					fieldsDOM = XMLUtils.Deserialize(fieldResource);
					xmlObjs = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "presentables") , "presentable");
					for(Element elem : xmlObjs)
					{
						PresentableDao f=new PresentableDao();
						f.fromXML(elem);
						items.add(f);
					}
				}
			}
		}
		logger.info("Found " + items.size() + " presentables");
		//logger.trace("Found " + items.size() + " presentables");
		return items;
	}
	
	private static Set<IDaoElement> getElementMetadata() throws Exception
	{
		logger.info("Searching for element metadata");
		Set<IDaoElement> items = new HashSet<IDaoElement>();
		Document fieldsDOM=null;
		String resource = null;
		String fieldsResource = null;
		String metadataResource = FieldModel.getMetadataResource();
		if(metadataResource == null)
		{
			fieldsResource = FieldModel.getMainResource();
			resource = fieldsResource;
		}
		else
			resource = metadataResource;
		
		if(resource != null) fieldsDOM = XMLUtils.Deserialize(resource);
		else return items;
		
		List<Element> xmlObjs = null;
		Element metadataElement = XMLUtils.GetChildElementWithName(fieldsDOM.getDocumentElement(), "metadata");
		if(metadataElement==null) return items;
		
		xmlObjs = XMLUtils.GetChildElementsWithName(metadataElement, "elementMetadata");
		for(Element elem : xmlObjs)
		{
			ElementMetadataDao s=new ElementMetadataDao();
			s.fromXML(elem);
			items.add(s);
		}
		logger.info("Found " + items.size() + " element metadata");
		return items;
	}

	private static Set<IDaoElement> getStaticConfiguration() throws Exception
	{
		logger.info("Searching for static configuration");
		Set<IDaoElement> items = new HashSet<IDaoElement>();
		Document staticConfigDOM=null;
		String staticConfigResource = FieldModel.getStaticConfigResource();
		
		logger.info("static configuration XML retrieved from FieldModel : " + staticConfigResource);
		
		if(staticConfigResource != null) staticConfigDOM = XMLUtils.Deserialize(staticConfigResource);
		else return items;
		
		//Element staticConfigElement = XMLUtils.GetChildElementWithName(staticConfigDOM.getDocumentElement(), "staticConfiguration");
		//if(staticConfigElement==null) return items;
		
		StaticConfigurationDao item = new StaticConfigurationDao();
		item.fromXML(staticConfigDOM.getDocumentElement());
		
		items.add(item);
		
		logger.info("Found " + items.size() + " static configuration");
		return items;
	}
	
	public static List<Resource> getPublishedFieldResources() throws Exception
	{
		return getPublishedFieldResources(GCubeRepositoryProvider.RRModelGenericResourceName);
	}
	
	public static List<Resource> getPublishedMetadataResources() throws Exception
	{
		return getPublishedFieldResources(GCubeRepositoryProvider.RRModelGenericResourceName + "." + "Metadata");
	}
	
	public static List<Resource> getPublishedStaticConfigResources() throws Exception
	{
		return getPublishedFieldResources(GCubeRepositoryProvider.RRModelGenericResourceName + "." + "StaticConfig");
	}
	
	public static List<Resource> getPublishedFieldResourcesForField(String fieldId) throws Exception
	{
		return getPublishedFieldResources(GCubeRepositoryProvider.RRModelGenericResourceName + "." + fieldId);
	}
	
	public static List<Resource> getPublishedFieldResources(String resourceName) throws Exception
	{
		//logger.info("getPublishedFieldResources: RR Classloader=" + Thread.currentThread().getContextClassLoader().getClass().getName());
		//logger.info("GHNContext Classloader: " + GHNContext.class.getClassLoader());
		
		
		logger.info("Searching for publised field resources in scopes : " + BridgeHelper.scopes);
		
		Set<String> scopes = getVOScopes(BridgeHelper.scopes);
		logger.info("VO scopes of : " + BridgeHelper.scopes + " are : " + scopes);
		
		List<Resource> resources = new ArrayList<Resource>();
		
		for(String scope : scopes) {
			resources.addAll(BackendConnector.newICollector().getGenericResourcesByTypeAndName(GCubeRepositoryProvider.RRModelGenericResourceName, resourceName, scope));
		}
		
		
//		
//		ISClient client = null;
//		GCUBEGenericResourceQuery query = null;
//		client =  GHNContext.getImplementation(ISClient.class);
//		query = client.getQuery(GCUBEGenericResourceQuery.class);
//		
//		query.addAtomicConditions(new AtomicCondition("/Profile/SecondaryType", GCubeRepositoryProvider.RRModelGenericResourceName));
//		query.addAtomicConditions(new AtomicCondition("/Profile/Name", resourceName));
//		
//		GCUBEScope []scopes = BridgeHelper.scopes;
//		
//		List<GCUBEGenericResource> resources=new ArrayList<GCUBEGenericResource>();
//		for(GCUBEScope scope : scopes) resources.addAll(client.execute(query,scope));
		return resources;

	}
	
	public static Set<String> getVOScopes(Collection<String> vreScopes){
		Set<String> voScopes = new HashSet<String>();
		if (vreScopes != null) {
			for (String scope : vreScopes) {
				String voScope = scopeHelper.getVOScope(scope);
				if (voScope != null)
					voScopes.add(voScope);
			}
		}
		return voScopes;
	}
	
	public static boolean checkIfExists(String resourceName, String resourceType, Set<String> scopes){
		
		for (String scope : scopes){
			
			List<Resource> resources = BackendConnector.newICollector().getGenericResourcesByName(resourceName, scope);
			
			boolean found = false;
			for (Resource resource : resources){
				if (resource.getType().equalsIgnoreCase(resourceType) && resource.getScopes().containsAll(scopes) /*&& scopes.containsAll(resource.getScopes())*/){
					found = true;
					break;
				}
			}
			
			if (found == false){
				logger.info("resource with name : " + resourceName + " does not exist in exactly the following scopes : " + scopes);
				return false;
			}
		}
		return true;
		
	}
	
	public static Resource getResourceByNameAndType(String resourceName, String resourceType, Set<String> scopes){
		
		for (String scope : scopes){
			
			List<Resource> resources = BackendConnector.newICollector().getGenericResourcesByName(resourceName, scope);
			
			for (Resource resource : resources){
				if (resource.getType().equalsIgnoreCase(resourceType) && resource.getScopes().containsAll(scopes) /*&& scopes.containsAll(resource.getScopes())*/){
					return resource;
				}
			}
		}
		return null;
		
	}
	
	public static Resource getResourceByID(String resourceID, Set<String> scopes){
		
		for (String scope : scopes){
			
			List<Resource> resources = BackendConnector.newICollector().getGenericResourcesByID(resourceID, scope);
			
			for (Resource resource : resources){
				if (resource.getScopes().containsAll(scopes) /*&& scopes.containsAll(resource.getScopes())*/){
					return resource;
				}
			}
		}
		return null;
		
	}
	
	public static void publishFieldResource(Resource resource, boolean isNew, Set<String> nonUpdateVOScopes) throws Exception {
		logger.info("publishing information on IS");
		
		logger.info("nonUpdateVOScopes : " + nonUpdateVOScopes);
		
		for (String gcubeScope : BridgeHelper.getFieldModelScopes()){
			logger.info("will check scope  : " + gcubeScope.toString());
		}
		
		Set<String> voScopes = getVOScopes(BridgeHelper.getFieldModelScopes());
		
		for (String gcubeScope : voScopes){
			
			logger.info("VOScope scope : " + gcubeScope);
			
			if(nonUpdateVOScopes.contains(gcubeScope)){
				logger.info("VOScope of scope : " + gcubeScope + " is in nonUpdateVOScopes");
				continue;
			}
			
			logger.info("trying to use scope : " + gcubeScope.toString());
			
			if (isNew){
				logger.info("creating resource with id : " + resource.getResourceID() + " . is new : " + isNew);
				
				logger.info("creating resource Body : " + resource.getBodyAsString());
				//logger.info("creating resource XML  : " + resource.toXML());
				
				updateOrPublish(resource, gcubeScope);
				
			} else {
				logger.info("updating resource with id : " + resource.getResourceID() + " is new : " + isNew);
				if (resource.getScopes().contains(gcubeScope)){
					logger.info("scope in resource. updating with id : " + resource.getResourceID() + " is new : " + isNew);
					updateOrPublish(resource, gcubeScope);
				} else {
					logger.info("scope not in resource. creating resource with id : " + resource.getResourceID() + ". is new : " + isNew);
					updateOrPublish(resource, gcubeScope);
				}
			}
		}
		logger.info("done publishing information on IS");
	}
	
	public static void updateOrPublish(Resource resource, String scope){
		ResourcePublisher<GeneralResource> publisher = BackendConnector.newPublisher();
		
		try {
			logger.trace("resource updating...");
			publisher.updateResource(resource, resource.getType(), resource.getName(), scope, false, true);
			logger.trace("resource updating...OK");
		} catch (Exception e){
			logger.trace("resource update failed. trying to publish");
			try {
				publisher.publishResource(resource, resource.getType(), resource.getName(), scope, false, true);
				logger.trace("resource publish OK");
			} catch (Exception e2){
				e2.printStackTrace();
				logger.trace("resource publish failed");
			}
		}
	}
	
	public static void deleteFieldResource(Resource resource, Set<String> nonUpdateVOScopes) throws Exception {
		logger.info("Deleting information from IS");
		
		for (String gcubeScope : BridgeHelper.getFieldModelScopes()){
			logger.info("trying to use scope : " + gcubeScope.toString());
			
			String VOScope1 = scopeHelper.getVOScope(gcubeScope);
			
			if(nonUpdateVOScopes.contains(gcubeScope) || nonUpdateVOScopes.contains(VOScope1)){
				logger.info("VOScope of scope : " + gcubeScope + " is in nonUpdateVOScopes");
				continue;
			}
			
			ResourcePublisher<GeneralResource> publisher = BackendConnector.newPublisher();
			
			publisher.deleteResource(resource.getResourceID(), VOScope1);
		}
		logger.info("done deleting information from IS");
	}
	
	public static String buildFieldDirectorySerialization(Set<IDaoElement> fields) throws ResourceRegistryException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<root>\n");
		buf.append("<fields>\n");
		for(IDaoElement elem : fields)
		{
			buf.append("<fieldId>");
			buf.append(((FieldDao)elem).getID());
			buf.append("</fieldId>");
		}
		buf.append("</fields>\n");	
		buf.append("</root>\n");
		return buf.toString();
	}
	
	public static String buildFieldSerialization(IDaoElement field,Set<IDaoElement> searchables,Set<IDaoElement> presentables, Set<String> deletedSearchables, Set<String> deletedPresentables) throws ResourceRegistryException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<root>\n");
		buf.append("<fieldInfo>\n");
		buf.append(field.toXML());
		buf.append("</fieldInfo>\n");
		buf.append("<searchables>\n");
		for(String searchableId : ((FieldDao)field).getSearchables()) 
		{
			for(IDaoElement elem : searchables)
			{
				if(elem.getID().equals(searchableId) && !deletedSearchables.contains(searchableId)){
					buf.append(elem.toXML());
				}
			}
		}
		buf.append("</searchables>\n");
		buf.append("<presentables>\n");
		for(String presentableId : ((FieldDao)field).getPresentables()) 
		{
			for(IDaoElement elem :presentables)
			{
				if(elem.getID().equals(presentableId) && !deletedPresentables.contains(presentableId)){
					buf.append(elem.toXML());
				}
			}
		}
		buf.append("</presentables>\n");
		
		buf.append("</root>\n");
		
		return buf.toString();
	}
	
	
	public static Set<IDaoElement> updateFieldList(Set<IDaoElement> fields,Set<IDaoElement> searchables,Set<IDaoElement> presentables, List<String> emptyScopedSearchables, List<String> emptyScoperdPresentables) throws ResourceRegistryException
	{
		Set<IDaoElement> updatedFields = new HashSet<IDaoElement>();
		
		for (IDaoElement field : fields)
			if (shouldUpdateField(field, searchables, presentables, emptyScopedSearchables, emptyScoperdPresentables) == true)
				updatedFields.add(field);
		return updatedFields;
	}
	
	public static boolean shouldUpdateField(IDaoElement field,Set<IDaoElement> searchables,Set<IDaoElement> presentables, List<String> emptyScopedSearchables, List<String> emptyScoperdPresentables) throws ResourceRegistryException
	{
		for(String searchableId : ((FieldDao)field).getSearchables()) 
		{
			for(IDaoElement elem : searchables)
			{
				if(elem.getID().equals(searchableId) && !emptyScopedSearchables.contains(searchableId)){
					return true;
				}
			}
		}
		for(String presentableId : ((FieldDao)field).getPresentables()) 
		{
			for(IDaoElement elem :presentables)
			{
				if(elem.getID().equals(presentableId) && !emptyScoperdPresentables.contains(presentableId)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static String updateFieldSerialization(String originalSerialization, IDaoElement field,Set<IDaoElement> searchables,Set<IDaoElement> presentables,
			boolean updateFields, boolean updateSearchables, boolean updatePresentables, Set<String> deletedSearchables, Set<String> deletedPresentables) throws Exception
	{
		Document original = null;
		
		try {
			original = XMLUtils.Deserialize(originalSerialization);
		} catch (Exception e) {
			logger.error("error in deserializing : " + originalSerialization);
			throw e;
		}
		StringBuilder buf=new StringBuilder();
		buf.append("<root>\n");
		if(updateFields)
		{
			if(updateSearchables && updatePresentables)
			{
				buf.append("<fieldInfo>\n");
				buf.append(field.toXML());
				buf.append("</fieldInfo>\n");
			}else
			{
				Element originalField = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(original.getDocumentElement(), "fieldInfo"), "field").get(0);
				buf.append("<fieldInfo>\n");
				FieldDao updatedDao = new FieldDao();
				Document dbField = XMLUtils.Deserialize(field.toXML());
				
				updatedDao.setID(((FieldDao)field).getID());
				updatedDao.setName(((FieldDao)field).getName());
				updatedDao.setDescription(((FieldDao)field).getDescription());
			
				Set<String> updatedSearchables = new HashSet<String>();
				if(updateSearchables)
				{
					//the searchables of this field as retrieved from the database (updated)
					List<Element> dbSearchables = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(dbField.getDocumentElement(), "searchables") , "searchable");
					for(Element dbSearchable : dbSearchables){
						String id = dbSearchable.getFirstChild().getNodeValue();
						logger.info("checking if presentable with id : "  + id + " in deleted searchables");
						if (deletedSearchables.contains(id)){
							continue;
						}
						updatedSearchables.add(id);
					}
				}else 
				{
					//the searchables of this field as retrieved from the is (original)
					List<Element> originalSearchables = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(originalField, "searchables"), "searchable");
					for(Element originalSearchable : originalSearchables)
						updatedSearchables.add(originalSearchable.getFirstChild().getNodeValue());
				}
				
				Set<String> updatedPresentables = new HashSet<String>();
				if(updatePresentables)
				{
					//the presentables of this field as retrieved from the database (updated)
					List<Element> dbPresentables = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(dbField.getDocumentElement(), "presentables") , "presentable");
					for(Element dbPresentable : dbPresentables){
						String id = dbPresentable.getFirstChild().getNodeValue();
						logger.info("checking presentable with if id : "  + id + " in deleted presentables");
						if (deletedSearchables.contains(id)){
							continue;
						}
						updatedPresentables.add(id);
					}
				}else 
				{
					//the presentables of this field as retrieved from the is (original)
					List<Element> originalPresentables = XMLUtils.GetChildElementsWithName(XMLUtils.GetChildElementWithName(originalField, "presentables"), "presentable");
					for(Element originalPresentable : originalPresentables)
						updatedPresentables.add(originalPresentable.getFirstChild().getNodeValue());
				}
				
				updatedDao.setSearchables(updatedSearchables);
				updatedDao.setPresentables(updatedPresentables);
				
				buf.append(updatedDao.toXML());
				buf.append("</fieldInfo>\n");
			}
		}else 
		{
			Element originalFields = XMLUtils.GetChildElementWithName(original.getDocumentElement(), "fieldInfo");
			buf.append(XMLUtils.Serialize(originalFields, true));
		}
		
		if(updateSearchables)
		{
			buf.append("<searchables>\n");
			for(String searchableId : ((FieldDao)field).getSearchables()) 
			{
				for(IDaoElement elem : searchables)
				{
					if(elem.getID().equals(searchableId) && !deletedSearchables.contains(searchableId))
						buf.append(elem.toXML());
				}
			}
			buf.append("</searchables>\n");
		}else
		{
			Element originalSearchables = XMLUtils.GetChildElementWithName(original.getDocumentElement(), "searchables");
			buf.append(XMLUtils.Serialize(originalSearchables, true));
		}
		
		if(updatePresentables)
		{
			buf.append("<presentables>\n");
			for(String presentableId : ((FieldDao)field).getPresentables()) 
			{
				for(IDaoElement elem :presentables)
				{
					if(elem.getID().equals(presentableId) && !deletedPresentables.contains(presentableId))
						buf.append(elem.toXML());
				}
			}
			buf.append("</presentables>\n");
		}else
		{
			Element originalPresentables = XMLUtils.GetChildElementWithName(original.getDocumentElement(), "presentables");
			buf.append(XMLUtils.Serialize(originalPresentables, true));
		}
		
		buf.append("</root>\n");
		return buf.toString();
	}
	
	public static String buildElementMetadataSerialization(Set<IDaoElement> metadata) throws ResourceRegistryException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<root>\n");
		buf.append("<metadata>\n");
		for(IDaoElement elem : metadata) buf.append(elem.toXML());
		buf.append("</metadata>\n");
		buf.append("</root>\n");
		return buf.toString();
	}
	
	public static String buildStaticConfigSerialization(IDaoElement staticConfig) throws ResourceRegistryException
	{
		StringBuilder buf=new StringBuilder();
		buf.append(staticConfig.toXML());
		return buf.toString();
	}
	
	public static void prefetchInMemoryItems(Set<Class<?>> itemTypes) throws ResourceRegistryException
	{
		InMemoryStore.clear();
		
		Map<String, ElementMetadataDao> metadata = new HashMap<String, ElementMetadataDao>();
		Set<IDaoElement> metadataSet = null;
		try { metadataSet = DatastoreHelper.getItems(DatastoreType.LOCAL, ElementMetadataDao.class); }
		catch(Exception e) { throw new ResourceRegistryException("Could not retrieve element metadata", e); }
		for(IDaoElement m : metadataSet)
			metadata.put(m.getID(), (ElementMetadataDao)m);
		
		boolean baseDatasource = false;
		for(Class<?> itemType : itemTypes)
		{
			if(itemType.getName().equals(DataSource.class.getName()))
			{
				baseDatasource = true;
				break;
			}
		}
		boolean baseDatasourceService = false;
		for(Class<?> itemType : itemTypes)
		{
			if(itemType.getName().equals(DataSourceService.class.getName()))
			{
				baseDatasourceService = true;
				break;
			}
		}
		for(Class<?> itemType : itemTypes)
		{
			logger.info("Prefetching element " + itemType.getName());
			if(itemType.getName().equals(DataCollection.class.getName()))
				InMemoryStore.setItems(itemType, new HashSet<DataCollection>(DataCollection.getAllCollections(false)));
			else if(itemType.getName().equals(DataLanguage.class.getName()))
				InMemoryStore.setItems(itemType, new HashSet<DataLanguage>(DataLanguage.getLanguages()));
			else if(itemType.getName().equals(Functionality.class.getName()))
				InMemoryStore.setItems(itemType, new HashSet<Functionality>(Functionality.getAllFunctionalities(false)));
			else if(itemType.getName().equals(HostingNode.class.getName()))
				InMemoryStore.setItems(itemType, new HashSet<HostingNode>(HostingNode.getAll(false)));
			else if(itemType.getName().equals(Field.class.getName()))
				InMemoryStore.setItems(itemType, new HashSet<Field>(Field.getAll(false)));
			else if(itemType.getName().equals(Searchable.class.getName()))
			{
				Set<IDaoElement> items = null;
				try { items = DatastoreHelper.getItems(DatastoreType.LOCAL, SearchableDao.class); }
				catch(Exception e) { throw new ResourceRegistryException("Could not prefetch searchables", e); }
				for(IDaoElement item : items)
				{
					Searchable loaded = new Searchable();
					loaded.setID(((SearchableDao)item).getID());
					loaded.load(false);
					if(!(metadata.containsKey(loaded.getField()) && metadata.get(loaded.getField()).getType().equals(ElementMetadata.Type.DeletedField.toString()))){
						if (metadata.get(loaded.getField()) != null)
							logger.debug("loaded searchable for field : " + loaded.getField() + " id : " + item.getID()  +  " type " + metadata.get(loaded.getField()).getType());
						else
							logger.debug("loaded searchable for field : " + loaded.getField() + " id : " + item.getID()  +  " is not in metadata");
						InMemoryStore.setItem(Searchable.class, loaded);
					}
				}
			}
			else if(itemType.getName().equals(Presentable.class.getName()))
			{
				Set<IDaoElement> items = null;
				try { items = DatastoreHelper.getItems(DatastoreType.LOCAL, PresentableDao.class); }
				catch(Exception e) { throw new ResourceRegistryException("Could not prefetch presentables", e); }
				for(IDaoElement item : items)
				{
					Presentable loaded = new Presentable();
					loaded.setID(((PresentableDao)item).getID());
					loaded.load(false);
					if(!(metadata.containsKey(loaded.getField()) && metadata.get(loaded.getField()).getType().equals(ElementMetadata.Type.DeletedField.toString()))){
						if (metadata.get(loaded.getField()) != null)
							logger.debug("loaded presentable for field : " + loaded.getField() + " id : " + item.getID()  +  " type " + metadata.get(loaded.getField()).getType());
						else
							logger.debug("loaded presentable for field : " + loaded.getField() + " id : " + item.getID()  +  " is not in metadata");
						
						InMemoryStore.setItem(Presentable.class, loaded);
					}
				}
			}
			else if(itemType.getName().equals(DataSource.class.getName()))
			{
				List<DataSource> ds = DataSource.getAll(false);
				for(DataSource d : ds)
					InMemoryStore.setItem(d.getClass(), d);	
			}
			else if(itemType.getName().equals(DataSourceService.class.getName()))
			{
				List<DataSourceService> ds = DataSourceService.getAll(false);
				for(DataSourceService d : ds)
					InMemoryStore.setItem(d.getClass(), d);	
			}
			else if(itemType.getName().equals(FTIndex.class.getName()))
			{
				if(baseDatasource==false)
					InMemoryStore.setItems(FTIndex.class, new HashSet<DataSource>(FTIndex.getAll(false)));
			}
			else if(itemType.getName().equals(FTIndexService.class.getName()))
			{
				if(baseDatasourceService == false)
					InMemoryStore.setItems(FTIndexService.class, new HashSet<DataSourceService>(FTIndexService.getAll(false)));
			}
//			else if(itemType.getName().equals(FWIndex.class.getName()))
//			{
//				if(baseDatasource == false)
//					InMemoryStore.setItems(FWIndex.class, new HashSet<DataSource>(FWIndex.getAll(false)));
//			}
//			else if(itemType.getName().equals(FWIndexService.class.getName()))
//			{
//				if(baseDatasourceService == false)
//					InMemoryStore.setItems(FWIndexService.class, new HashSet<DataSourceService>(FWIndexService.getAll(false)));
//			}
//			else if(itemType.getName().equals(GeoIndex.class.getName()))
//			{
//				if(baseDatasource == false)
//					InMemoryStore.setItems(GeoIndex.class, new HashSet<DataSource>(GeoIndex.getAll(false)));
//			}
//			else if(itemType.getName().equals(GeoIndexService.class.getName()))
//			{
//				if(baseDatasourceService == false)
//					InMemoryStore.setItems(GeoIndexService.class, new HashSet<DataSourceService>(GeoIndexService.getAll(false)));
//			}
			else if(itemType.getName().equals(OpenSearchDataSource.class.getName()))
			{
				if(baseDatasource == false)
					InMemoryStore.setItems(OpenSearchDataSource.class, new HashSet<DataSource>(OpenSearchDataSource.getAll(false)));
			}
			else if(itemType.getName().equals(OpenSearchDataSourceService.class.getName()))
			{
				if(baseDatasourceService == false)
					InMemoryStore.setItems(OpenSearchDataSourceService.class, new HashSet<DataSourceService>(OpenSearchDataSourceService.getAll(false)));
			}
			
			
			
			else if(itemType.getName().equals(SruConsumer.class.getName()))
			{
				if(baseDatasource == false)
					InMemoryStore.setItems(SruConsumer.class, new HashSet<DataSource>(SruConsumer.getAll(false)));
			}
			else if(itemType.getName().equals(SruConsumerService.class.getName()))
			{
				if(baseDatasourceService == false)
					InMemoryStore.setItems(SruConsumerService.class, new HashSet<DataSourceService>(SruConsumerService.getAll(false)));
			}
			
			else if(itemType.getName().equals(FieldIndexContainer.class.getName()))
			{
				Set<IDaoElement> items = null;
				try { items = DatastoreHelper.getItems(DatastoreType.LOCAL, FieldIndexContainerDao.class); }
				catch(Exception e) { throw new ResourceRegistryException("Could not prefetch datasource field info", e); }
				for(IDaoElement item : items)
				{
					FieldIndexContainer loaded = new FieldIndexContainer();
					loaded.setID(((FieldIndexContainerDao)item).getID());
					loaded.load(false);
					InMemoryStore.setItem(FieldIndexContainer.class, loaded);
				}
			}
		}
	}
	
	
	static public List<String> getGHNContextStartScopes() {
		List<String> scopes = ConfigurationProviderLoader.getProvider().getGHNContextStartScopes();
		logger.info("gHNContextStartScopes : " + scopes);
		return scopes;
		/*
		List<String> scopes = new ArrayList<String>();
		for (GCUBEScope scope : GHNContext.getContext().getStartScopes()){
			scopes.add(scope.toString());
		}
		
		//XMLReader.getStartScopes();
		
		return scopes;*/
	}
	
	
	static public List<String> getGHNContextScopes() {
		List<String> scopes = ConfigurationProviderLoader.getProvider().getGHNContextScopes();
		logger.info("gHNContextScopes : " + scopes);
		return scopes;
		/*
		List<String> scopes = new ArrayList<String>();
		for (GCUBEScope scope : GHNContext.getContext().getGHN().getScopes().values()){
			scopes.add(scope.toString());
		}
		//XMLReader.getStartScopes();
		
		return scopes;*/
	}
	
	static public boolean isClientMode() {
		boolean isClientMode = ConfigurationProviderLoader.getProvider().isClientMode();
		logger.info("isClientMode : " + isClientMode);
		return isClientMode;
		//return GHNContext.getContext().isClientMode();
		
		//test JNDI to detect client mode and set status to DOWN in case
		/*try {getContext().lookup("java:comp/env/status");}
		catch(Exception e) {return true;}
		return false;*/
	}
	
	/*@SuppressWarnings("unchecked")
	static  InitialContext getContext() {
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(SynchronizedContext.SYNCHRONIZED, "true");
			env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.naming.java.javaURLContextFactory");
			return new InitialContext(env);
		} catch (Exception e) {
			
		}
		return null;
	}*/
	
	
	
	
	public static void main(String[] args) throws Exception {
		
//		String scope = "/gcube/devNext";
//		String serviceName = "ForwardIndexNode";
//		String serviceClass = "Index";
//		ScopeProvider.instance.set(scope);
//		SimpleQuery query = queryFor(ServiceInstance.class);
//		
//		query.addCondition("$resource/Data/gcube:ServiceClass/text() eq '" + serviceClass + "'")
//			 .addCondition("$resource/Data/gcube:ServiceName/text() eq '" + serviceName + "'");
//		 
//		DiscoveryClient<ServiceInstance> client = clientFor(ServiceInstance.class);
//		
//		List<ServiceInstance> resources = client.submit(query);
//		
//		
//		for (ServiceInstance resource : resources) {
//			resource.properties().customProperties().getClass();
//			XPathHelper xpath = new XPathHelper(resource.properties().customProperties());
//			for (String val: xpath.evaluate("/doc/*[local-name()='Fields']/text()"))
//				logger.trace(val);
//		}
//
////		
//		
////		
//////		logger.trace(resources);
//		
		
//		
		
//		logger.trace(getIndexServiceEndpoint("/gcube/devNext", "dl015.madgik.di.uoa.gr"));
//		logger.trace(getIndexServiceGHNId("/gcube/devNext", "dl015.madgik.di.uoa.gr"));
		
		BridgeHelper.scopes = new ArrayList<String>();
//		BridgeHelper.scopes.add("/d4science.research-infrastructures.eu/gCubeApps");
//		BridgeHelper.scopes.add("/d4science.research-infrastructures.eu/FARM");
		BridgeHelper.scopes.add("/gcube/devNext");
//		BridgeHelper.scopes.add("/gcube/devNext/NextNext");
		BridgeHelper.searchSystemScopes = Lists.newArrayList(BridgeHelper.scopes);
		
//		ResourceRegistry.startBridging();
//		while (!ResourceRegistry.isInitialBridgingComplete())
//			Thread.sleep(1000);
//		
//		
//		List<DataCollection> datacollections = DataCollection.getCollectionsOfScope(true, "/gcube/devNext");
		
		//BridgeHelper.scopes.add("/d4science.research-infrastructures.eu/Ecosystem");
		//BridgeHelper.scopes.add("/d4science.research-infrastructures.eu/Ecosystem/TryIt");
//		BridgeHelper.scopes.add("/d4science.research-infrastructures.eu");
//		BridgeHelper.scopes.add("/d4science.research-infrastructures.eu/gCubeApps");
		
		
//		FieldModel.retrieve();
//		logger.trace( getFields().size());
//		getDataCollections();
//		retrieveSearchService();
		//getDataSourceSruConsumer();
		
//		checkIfExists("ResourceRegistryModel.StaticConfig", "ResourceRegistryModel", Sets.newHashSet(BridgeHelper.scopes));
//		
//		List<Resource> resources = getPublishedStaticConfigResources();
		
		
//		Resource r = new Resource();
//		r.setName("New 4th Name");
//		r.setType("Test");
		
		
//		updateOrPublish(r, "/gcube/devNext");
////		
//		Thread.sleep(10000);
		
		//Resource r2 =getResourceByID("9345ed6a-0635-467a-982d-08bbae96ef71",  Sets.newHashSet(BridgeHelper.scopes));
		Resource r2 = getResourceByNameAndType("ResourceRegistryModel.StaticConfig2", "ResourceRegistryModel", Sets.newHashSet(BridgeHelper.scopes));
		
		
		//r2.setName("New 5th Name");
		
		
		//XMLConverter.convertToXML(r);
		XMLConverter.convertToXML(r2);
		
		updateOrPublish(r2, "/gcube/devNext");
		updateOrPublish(r2, "/gcube/devsec");
		
		//getDataCollections();
		//logger.trace(getHostingNodes().size());
//		getSearchables();
//		getPresentables();
//		BridgeHelper.scopes.add("/d4science.research-infrastructures.eu/EUBrazilOpenBio/SpeciesLab");
////		BridgeHelper.scopes.add("/d4science.research-infrastructures.eu/EUBrazilOpenBio/SpeciesLab");
//		
//		logger.trace(getVOScopes(BridgeHelper.scopes));
//		
////		Set<DataCollection> colls = QueryHelper.getExternalCollectionsOfScope("/d4science.research-infrastructures.eu/EUBrazilOpenBio");
////		for (DataCollection col : colls){
////			logger.trace("col : " + col.getID() + " , " + col.getName());
////		}
//		
////		 BridgeHelper.searchSystemScopes = BridgeHelper.scopes;
////		 logger.trace(getPublishedMetadataResources().size());
//////		retrieveSearchService();
//////		FieldModel.retrieve();
//////		getExecutionServer();
//////		getDataCollections();
//////		getOpenSearchDataSource();
//////		getFields();
//////		getTreeCollections();
//////		getWorkflowService();
//////		retrieveSearchService();
////		getFTIndex();
		
//		String field = "977ec5d3-7a99-4262-8251-8332c4c16766";
//		String collection = "553e9014-fd4f-45fd-868e-07834c55b83b";
//		List<String> relations = new ArrayList<String>();
//		relations.add("=");
//		List<String> projections = new ArrayList<String>();
//		relations.add("4b9b2594-9ffe-4f40-8de1-698818dfecc0");
//		String scope = "/gcube/devNext";
//		
//		
//		
//		Set<String> s = QueryHelper.getLanguageByFieldRelationCol(field, relations,collection, projections, scope);
//		logger.trace(s);
		
		
//		DataCollection.getCollectionsOfScope(true, "/gcube/devNext/NextNext");
		
//////		logger.trace();
//		getFWIndex();
		

	}
}
