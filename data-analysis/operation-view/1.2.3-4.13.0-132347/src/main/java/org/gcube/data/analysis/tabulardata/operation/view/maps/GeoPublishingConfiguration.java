package org.gcube.data.analysis.tabulardata.operation.view.maps;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;
import org.gcube.spatial.data.gis.GISInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoPublishingConfiguration{

	private static final Logger logger = LoggerFactory.getLogger(GeoPublishingConfiguration.class);
	
	
	
	private static ConcurrentHashMap<String, CachedObject<GeoPublishingConfiguration>> configurationMap=new ConcurrentHashMap<>();
	
	
	
	// get store initializes it
	
	public static synchronized GeoPublishingConfiguration get()throws Exception{
		String currentScope=ScopeProvider.instance.get();
		if(!configurationMap.containsKey(currentScope)||!configurationMap.get(currentScope).isvalid()){
			// load configuration from IS if new scope or configuration out of date
			configurationMap.put(currentScope, new CachedObject<GeoPublishingConfiguration>(new GeoPublishingConfiguration()));
		}
		return new GeoPublishingConfiguration();
	}
		
		
	private GeoPublishingConfiguration() throws Exception {
		// get GIS Interface
		this.gis=GISInterface.get();
		gis.setToRegisterXMLAdapters(Collections.singletonList((XMLAdapter)new XMLAdapterImpl()));
		
		
		logger.debug("Current GeoServer descriptor is "+gis.getCurrentGeoServerDescriptor());
		// get configuration params from generic resource
		
		this.params=getParameters();		
		// gather information on GIS DB on RI
		
		SimpleQuery query=queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '"+params.getGisDBCategory()+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+params.getGisDBPlatformName()+"'")				
         .setResult("$resource/Profile/AccessPoint");
		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);
		
		boolean foundDBAccess=false;
		
		for(AccessPoint point:client.submit(query)){
			Map<String,Property> map=point.propertyMap();
			if(point.name().equals(params.getAccessPointName())&&
					map.containsKey(params.getTdmDataStoreFlag())&&
					Boolean.parseBoolean(map.get(params.getTdmDataStoreFlag()).value())){				
				postgisUrl=params.getUrlPrefix()+point.address();
				postgisUser=point.username();
				postgisPwd=StringEncrypter.getEncrypter().decrypt(point.password());
				foundDBAccess=true;
				break;
			}
		}
		if(!foundDBAccess) throw new Exception("Unable to locate remote DB Endpoint");
		
		// TODO Add check and initiate datastore
		
	}
	
	private GeoPublishingParameters params;
	
	private String postgisUrl;
	private String postgisUser;
	private String postgisPwd;
	private GISInterface gis;
	
	public String getPostgisPwd() {
		return postgisPwd;
	}
	public String getPostgisUrl() {
		return postgisUrl;
	}
	public String getPostgisUser() {
		return postgisUser;
	}

	public GeoPublishingParameters getParams() {
		return params;
	}
	public GISInterface getGis() {
		return gis;
	}
	
	// IS QUERYING LOGIC 
	
	
	
	private static JAXBContext jaxbContext;
	
	static{
		try{
		jaxbContext= JAXBContext.newInstance(GeoPublishingParameters.class);
		}catch(Exception e){
			logger.error("Unable to initiate context ",e);
		}
	}
	
	
	
	private static GeoPublishingParameters getParameters() throws Exception{
		SimpleQuery paramsQuery = queryFor(GenericResource.class);
		paramsQuery.addCondition("$resource/Profile/SecondaryType/text() eq 'TDMConfiguration'");
		DiscoveryClient<GenericResource> pqClient = clientFor(GenericResource.class);
		for(GenericResource res:pqClient.submit(paramsQuery)){
			try{
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			return (GeoPublishingParameters) unmarshaller.unmarshal(new StringReader(res.profile().bodyAsString()));
			}catch(Exception e){
				logger.debug("Invalid resource {}",res.id());
			}
		}
		throw new Exception("No TDMConfiguration found ");
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoPublishingConfiguration [params=");
		builder.append(params);
		builder.append(", postgisUrl=");
		builder.append(postgisUrl);
		builder.append(", postgisUser=");
		builder.append(postgisUser);
		builder.append(", postgisPwd=");
		builder.append(postgisPwd);
		builder.append(", gis=");
		builder.append(gis);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
