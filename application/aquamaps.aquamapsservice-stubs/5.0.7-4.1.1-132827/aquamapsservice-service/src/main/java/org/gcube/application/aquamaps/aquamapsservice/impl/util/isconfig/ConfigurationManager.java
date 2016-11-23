package org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.ISQueryConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ParserXPath;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.DBDescriptor.DBType;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationManager {

	final static Logger logger= LoggerFactory.getLogger(ConfigurationManager.class);
	
	
	private static TransformerFactory tf = TransformerFactory.newInstance();
	private static Transformer transformer;
	static {
		try{
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
		}catch(Exception e){
			logger.error("Unable to initialize transformer ",e);
		}
	}
	
	//Per Scope Management
	
//	private static HashMap<String,CachedObject<DBDescriptor>> internalDBConfiguration=new HashMap<String, CachedObject<DBDescriptor>>();
//	private static HashMap<String,CachedObject<DBDescriptor>> gisDBConfiguration=new HashMap<String, CachedObject<DBDescriptor>>();
//	
//	
//	public static synchronized DBDescriptor getInternalDBDescriptor() throws Exception{		
//		String scope=ScopeProvider.instance.get();
//		if((!internalDBConfiguration.containsKey(scope))||(!internalDBConfiguration.get(scope).isvalid())){
//			internalDBConfiguration.put(scope, new CachedObject<DBDescriptor>(getInternalDB()));			
//		}
//		return internalDBConfiguration.get(scope).getTheObject();
//	}
//	
//	
//	public static synchronized DBDescriptor getGISDB() throws Exception{
//		String scope=ScopeProvider.instance.get();
//		if((!gisDBConfiguration.containsKey(scope))||(!gisDBConfiguration.get(scope).isvalid())){
//			gisDBConfiguration.put(scope, new CachedObject<DBDescriptor>(getGISDBDescriptor()));			
//		}
//		return gisDBConfiguration.get(scope).getTheObject();
//	}
	
	
	public static synchronized VODescriptor getVODescriptor() throws Exception{
		if(!cachedConfiguration.isvalid()) cachedConfiguration=new CachedObject<VODescriptor>(getScopeInformation(configurationScope));
		return cachedConfiguration.getTheObject();
	}
	
	
	private static CachedObject<VODescriptor> cachedConfiguration=null;
	private static String configurationScope=null;
	/**
	 * Finds suitable scopes among available ones
	 * 
	 */
	
	public static String init(GCUBEScope infrastructureScope)throws Exception{
		
		for(GCUBEScope scope:findAvailableScopes(infrastructureScope)){			
			try{				
				cachedConfiguration=new CachedObject<VODescriptor>(getScopeInformation(scope.toString()));
				configurationScope=cachedConfiguration.getTheObject().getScope();
				logger.trace("Found well configured scope : "+configurationScope);
				return configurationScope;
			}catch(Exception e){
				logger.debug("Invalid scope : "+scope.toString(),e);
			}
		}
		throw new Exception("No well configured scope available");
	}
	
	
	private static VODescriptor getScopeInformation(String scope)throws Exception{		
		String callerScope=ScopeProvider.instance.get();
		try{
			ScopeProvider.instance.set(scope);
			logger.debug("Checking scope : "+scope);


			//************************** GeoServers - Runtime Resources
			List<GeoServerDescriptor> geoServers=getGeoServers();
			List<DataSourceDescriptor> geoNetworks=getGeoNetwork();
			
//			List<GeoServerDescriptor> geoServers=null;
//			List<DataSourceDescriptor> geoNetworks=null;
			
			//************************** DB References
			List<DBDescriptor> gisDBs=getGISDBDescriptor();
			List<DBDescriptor> internalDBs=getInternalDB();
			List<DBDescriptor> publisherDBs=getPublisherDB();


			//************************** Checks

			if(geoNetworks.size()>1) throw new Exception("Multiple GeoNetworks found");
			if(geoServers.size()>0&&gisDBs.isEmpty()) throw new Exception("No gis databases found");
			if(gisDBs.size()>1) throw new Exception ("Found "+gisDBs.size()+" gis DBs");
			if(internalDBs.size()!=1) throw new Exception ("Found "+internalDBs.size()+" internal DBs");
			if(publisherDBs.size()!=1) throw new Exception ("Found "+publisherDBs.size()+" publisher DBs");

			DataSourceDescriptor geoNetwork=geoNetworks.isEmpty()?null:geoNetworks.get(0);
			DBDescriptor internal=internalDBs.isEmpty()?null:internalDBs.get(0);
			DBDescriptor publisher=publisherDBs.isEmpty()?null:publisherDBs.get(0);
			DBDescriptor gisDb=gisDBs.isEmpty()?null:gisDBs.get(0);

			return new VODescriptor(scope, geoServers, geoNetwork, internal, gisDb,publisher);
		}catch(Exception e){
			logger.error("Unexpected Error while crawling scope "+scope+", error message : "+e.getMessage());		
			throw e;
		}finally{
			ScopeProvider.instance.set(callerScope);
		}

	}
	
	
	/**
	 * Finds accessible scopes from current ghn context
	 */
	protected static ArrayList<GCUBEScope> findAvailableScopes(
			GCUBEScope Infrastructure) throws Exception {
		HashSet<GCUBEScope> toReturn=new HashSet<GCUBEScope>();
		for(GCUBEScope scope : GHNContext.getContext().getStartScopes()){
			toReturn.add(scope);
			toReturn.addAll(getChildren(scope));
		}
		for(GCUBEScope scope :GHNContext.getContext().getStartScopes()){
			toReturn.add(scope);
			toReturn.addAll(getChildren(scope));
		}
		toReturn.add(Infrastructure);
		return new ArrayList<GCUBEScope>(toReturn);
	}

	/**
	 * Queries IS for generic resources describing children of the given scope
	 * 
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected static ArrayList<GCUBEScope> getChildren(GCUBEScope scope)throws Exception{
		ArrayList<GCUBEScope> toReturn=new ArrayList<GCUBEScope>();
		if(!scope.getType().equals(Type.VRE)){
			logger.debug("Retrieving children of scope "+scope+", ("+scope.getType()+")");
			SimpleQuery query = queryFor(GenericResource.class);
			String secondaryType=scope.getType().equals(Type.VO)?"VRE":"VO";		
			query.addCondition("$resource/Profile/SecondaryType/text() eq '"+secondaryType+"'");
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			ScopeProvider.instance.set(scope.toString());
			for(GenericResource res:client.submit(query)){
				try{
					// parse body as a XML serialization of a Computational Infrastructure
					StringWriter writer = new StringWriter();
					transformer.transform(new DOMSource(res.profile().body()), new StreamResult(writer));
					String theXML=writer.getBuffer().toString();
					
					String foundScope=ParserXPath.getTextFromXPathExpression("<rootNode>"+theXML+"</rootNode>", "//Scope").get(0);
					toReturn.add(GCUBEScope.getScope(foundScope));
				}catch(Exception e){
					logger.warn("Unable to detect scope from generic resource [ID : "+res.id()+"] under scope "+scope,e);
				}
			}
		}
		return toReturn;
	}
	
	
	
	
	//******************************* QUERIES
	
	/**
	 * Query passed scope for RuntimeResources describing valid GIS Database
	 * AtomicConditions : 	//Profile/Category = Constants.get().getGeoServerDBCategory()
	 * 						//Profile/Platform/Name = Constants.get().getGeoServerDBPlatformName()
	 * 
	 * Valid Access Points : 
	 * 				EntryName = Constants.get().getGeoServerDBEntryName()
	 * 
	 * 		Mandatory properties: 
	 * 				Constants.get().getGeoServerDBAquaMapsDataStore() : boolean = true
	 * 				Constants.get().getDBMaxConnection() : integer
	 * 				DBDescriptor.AQUAMAPS_WORLD_TABLE
	 * 
	 * @param scope
	 * @return
	 * @throws NumberFormatException 
	 * @throws Exception
	 */
	protected static List<DBDescriptor> getGISDBDescriptor() throws NumberFormatException, Exception{
		logger.debug("Checking gis DBs");
		ArrayList<DBDescriptor> toReturn=new ArrayList<DBDescriptor>();
		SimpleQuery query=queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+ISQueryConstants.get().getGeoServerDBCategory()+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+ISQueryConstants.get().getGeoServerDBPlatformName()+"'")				
         .setResult("$resource/Profile/AccessPoint");
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		for(AccessPoint point:client.submit(query)){
			Map<String,Property> map=point.propertyMap();
			if(point.name().equals(ISQueryConstants.get().getGeoServerDBEntryName())&&
					map.containsKey(ISQueryConstants.get().getGeoServerDBAquaMapsDataStore())&&
					Boolean.parseBoolean(map.get(ISQueryConstants.get().getGeoServerDBAquaMapsDataStore()).value())){
				logger.debug("Found Access point "+point.address());
				DBDescriptor toAdd=new DBDescriptor(
						point.address(), 
						point.username(), 
						decrypt(point.password()), 
						DBType.postgres,
						Integer.parseInt(map.get(ISQueryConstants.get().getDBMaxConnection()).value()));
				toAdd.setProperty(DBDescriptor.AQUAMAPS_WORLD_TABLE, map.get(DBDescriptor.AQUAMAPS_WORLD_TABLE).value());
				toReturn.add(toAdd);
			}
		}
		return toReturn;
	}
	
	
	/**
	 * Query passed scope for RuntimeResources describing valid Internal Database
	 * AtomicConditions : 	//Profile/Category = Constants.get().getInternalDBCategoryName()
	 * 						//Profile/Platform/Name = Constants.get().getInternalDBPlatformName()
	 * 
	 * Valid Access Points : 
	 * 				EntryName = Constants.get().getInternalDBEntryName()
	 * 
	 * 		Mandatory properties: 
	 * 				Constants.get().getInternalDBSchemaName() : boolean = true
	 * 				Constants.get().getDBMaxConnection() : integer
	 * 				DBDescriptor.TABLESPACE_PREFIX
	 * 				DBDescriptor.TABLESPACE_COUNT
	 * 
	 * @param scope
	 * @return
	 * @throws NumberFormatException 
	 * @throws Exception
	 */
	protected static List<DBDescriptor> getInternalDB() throws NumberFormatException, Exception{
		logger.debug("Checking internal DBs");
		ArrayList<DBDescriptor> toReturn=new ArrayList<DBDescriptor>();
		SimpleQuery query=queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+ISQueryConstants.get().getInternalDBCategoryName()+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+ISQueryConstants.get().getInternalDBPlatformName()+"'")				
         .setResult("$resource/Profile/AccessPoint");
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		for(AccessPoint point:client.submit(query)){
			Map<String,Property> map=point.propertyMap();
					if(point.name().equals(ISQueryConstants.get().getInternalDBEntryName())&&
							map.containsKey(ISQueryConstants.get().getInternalDBSchemaName())&&
							map.get(ISQueryConstants.get().getInternalDBSchemaName()).value().equals(ISQueryConstants.get().getInternalDBSchemaValue())){
						logger.debug("Found Access point "+point.address());
						DBDescriptor toAdd=new DBDescriptor(
								point.address(), 
								point.username(), 
								decrypt(point.password()), 
								DBType.postgres,
								Integer.parseInt(map.get(ISQueryConstants.get().getDBMaxConnection()).value()));

						toAdd.setProperty(DBDescriptor.TABLESPACE_PREFIX, map.get(DBDescriptor.TABLESPACE_PREFIX).value());
						toAdd.setProperty(DBDescriptor.TABLESPACE_COUNT, map.get(DBDescriptor.TABLESPACE_COUNT).value());
						toReturn.add(toAdd);
					}
				}
		return toReturn;
	}
	
	/**
	 * Query passed scope for RuntimeResources describing valid geoServers
	 * AtomicConditions : 	//Profile/Category = Constants.get().getGeoServerCategoryName()
	 * 						//Profile/Platform/Name = Constants.get().getGeoServerPlatformName()
	 * 
	 * Valid Access Points : 
	 * 				EntryName = Constants.get().getGeoServerEntryName()
	 * 		Mandatory properties :  
	 * 				Constants.get().getGeoServerAquaMapsWorkspace()
	 * 				Constants.get().getGeoServerAquaMapsDataStore()
	 * 				Constants.get().getGeoServerAquaMapsDefaultDistributionStyle()
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected static List<GeoServerDescriptor> getGeoServers()throws Exception{
		logger.debug("Checking geoServers..");
		ArrayList<GeoServerDescriptor> toReturn=new ArrayList<GeoServerDescriptor>();
		SimpleQuery query=queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+ISQueryConstants.get().getGeoServerCategoryName()+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+ISQueryConstants.get().getGeoServerPlatformName()+"'")				
         .setResult("$resource/Profile/AccessPoint");
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		for(AccessPoint point:client.submit(query)){
					if(point.name().equals(ISQueryConstants.get().getGeoServerEntryName())){
						Map<String,Property> map=point.propertyMap();
						logger.debug("Found Access point "+point.address());
						toReturn.add(new GeoServerDescriptor(
								point.address(), 
								point.username(), 
								decrypt(point.password()), 
								map.get(ISQueryConstants.get().getGeoServerAquaMapsWorkspace()).value(), 
								map.get(ISQueryConstants.get().getGeoServerAquaMapsDataStore()).value(), 
								map.get(ISQueryConstants.get().getGeoServerAquaMapsDefaultDistributionStyle()).value()));
					}
				}		
		return toReturn;
	}

	
	/**
	 * Query passed scope for RuntimeResources describing valid geoNetworks
	 * AtomicConditions : 	//Profile/Category = Constants.get().getGeoNetworkCategoryName()
	 * 						//Profile/Platform/Name = Constants.get().getGeoNetworkPlatformName()
	 * 
	 * Valid Access Points : 
	 * 				EntryName = Constants.get().getGeoNetworkEntryName()
	 * 
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected static List<DataSourceDescriptor> getGeoNetwork()throws Exception{
		logger.debug("Checking geoNetwork..");
		ArrayList<DataSourceDescriptor> toReturn=new ArrayList<DataSourceDescriptor>();
		SimpleQuery query=queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+ISQueryConstants.get().getGeoNetworkCategoryName()+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+ISQueryConstants.get().getGeoNetworkPlatformName()+"'")				
         .setResult("$resource/Profile/AccessPoint");
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		for(AccessPoint point:client.submit(query)){
					if(point.name().equals(ISQueryConstants.get().getGeoNetworkEntryName())){	
						logger.debug("Found Access point "+point.address());
						toReturn.add(new DataSourceDescriptor(
								point.address(), 
								point.username(),
								decrypt(point.password())));
					}
		}
		return toReturn;
	}
	
	/**
	 * Query passed scope for RuntimeResources describing valid Publisher Database
	 * AtomicConditions : 	//Profile/Category = Constants.get().getPublisherDBCategoryName()
	 * 						//Profile/Platform/Name = Constants.get().getPublisherDBPlatformName()
	 * 
	 * Valid Access Points : 
	 * 				EntryName = Constants.get().getPublisherDBEntryName()
	 * 
	 * 		Mandatory properties: 
	 * 				Constants.get().getPublisherDBSchemaName() : boolean = true
	 * 				Constants.get().getDBMaxConnection() : integer
	 * 
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	protected static ArrayList<DBDescriptor> getPublisherDB()throws Exception{
		logger.debug("Checking internal DBs");
		ArrayList<DBDescriptor> toReturn=new ArrayList<DBDescriptor>();
		SimpleQuery query=queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+ISQueryConstants.get().getPublisherDBCategoryName()+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+ISQueryConstants.get().getPublisherDBPlatformName()+"'")				
         .setResult("$resource/Profile/AccessPoint");
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		for(AccessPoint point:client.submit(query)){
			Map<String,Property> map=point.propertyMap();
					if(point.name().equals(ISQueryConstants.get().getPublisherDBEntryName())&&
							map.containsKey(ISQueryConstants.get().getPublisherDBSchemaName())&&
							map.get(ISQueryConstants.get().getPublisherDBSchemaName()).value().equals(ISQueryConstants.get().getPublisherDBSchemaValue())){
						logger.debug("Found Access point "+point.address());
						DBDescriptor toAdd=new DBDescriptor(
								point.address(), 
								point.username(), 
								decrypt(point.password()), 
								DBType.postgres,
								Integer.parseInt(map.get(ISQueryConstants.get().getDBMaxConnection()).value()));

						toReturn.add(toAdd);
					}
				}
		return toReturn;
	}
	
	private static final String decrypt(String toDecrypt) throws Exception{
		return StringEncrypter.getEncrypter().decrypt(toDecrypt);
	}
	
	
	
	
}
