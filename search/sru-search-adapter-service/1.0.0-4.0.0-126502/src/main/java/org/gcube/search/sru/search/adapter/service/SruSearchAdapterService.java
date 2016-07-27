package org.gcube.search.sru.search.adapter.service;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.resourceawareservice.ResourceAwareService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourceawareservice.exceptions.ResourceNotFoundException;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.search.SearchClient2;
import org.gcube.search.exceptions.SearchClientException;
import org.gcube.search.sru.search.adapter.commons.Constants;
import org.gcube.search.sru.search.adapter.commons.apis.SruSearchAdapterServiceAPI;
import org.gcube.search.sru.search.adapter.commons.resources.SruSearchAdapterResource;
import org.gcube.search.sru.search.adapter.service.helpers.QueryParserHelper;
import org.gcube.search.sru.search.adapter.service.helpers.RecordConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Path("/")
@Singleton
public class SruSearchAdapterService extends ResourceAwareService<SruSearchAdapterResource> implements SruSearchAdapterServiceAPI {
	
	QueryParserHelper queryParserHelper = new QueryParserHelper();
	RecordConverter recordConverer = new RecordConverter();
	
	boolean splitLists;
	boolean includeNonDC;
	int defaultRecordsNum = 0;
	
	static final Logger logger = LoggerFactory.getLogger(SruSearchAdapterService.class);
	
	@Inject
	public SruSearchAdapterService(ResourceFactory<SruSearchAdapterResource> factory,
			ResourcePublisher<SruSearchAdapterResource> publisher,
			IResourceFilter<SruSearchAdapterResource> resourceFilter,
			IResourceFileUtils<SruSearchAdapterResource> resourceFileUtils)
			throws ResourceAwareServiceException {
		super(factory, publisher, resourceFilter, resourceFileUtils);
		logger.info("SruSearchAdapterService initialized");
	}

	private String scope;
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public void setSplitLists(boolean splitLists) {
		this.splitLists = splitLists;
	}
	
	public void setIncludeNonDC(boolean includeNonDC) {
		this.includeNonDC = includeNonDC;
	}
	
	public void setRecordsNum(int defaultRecordsNum) {
		this.defaultRecordsNum = defaultRecordsNum;
	}
	
	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public String getResourceClass() {
		return Constants.RESOURCE_CLASS;
	}

	@Override
	public String getResourceNamePref() {
		return Constants.RESOURCE_NAME_PREF;
	}
	
	public Response ping(){
		return Response.ok().entity("pong").build();
	}
	
	
	public Response get(
			String scope,
			String resourceID,
			String operation, 
			Float version,
			String recordPacking, 
			String query,
			Integer maximumRecords, 
			String recordSchema){
			
		logger.info("---------------------------------------");
		logger.info("operation       : " + operation);
		logger.info("version         : " + version);
		logger.info("recordPacking   : " + recordPacking);
		logger.info("query           : " + query);
		logger.info("maximumRecords  : " + maximumRecords);
		logger.info("recordSchema    : " + recordSchema);
		
		SruSearchAdapterResource resource = null;
		SearchClient2 searchClient = null;
		
		
		try {
			resource = this.getResource(resourceID);
			
			searchClient = new SearchClient2.Builder()
							.endpoint(resource.getSearchSystemEndpoint())
							.scope(resource.getScope())
							.build();
				
			logger.info("initialized search client at : " + searchClient.getEndpoint() + " " + searchClient.getScope());
			
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new ResponseObj("Resource with ID : " + resourceID + " not found"))
					.build();
		} catch (SearchClientException e) {
			logger.warn("error while initializing search client", e);
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ResponseObj("Resource with ID : " + resourceID + " not found"))
					.build();
		}
		
		return getResponse(searchClient, scope, operation, version, recordPacking, query, maximumRecords, recordSchema);
	}
	
	
	public Response get(
			String operation, 
			Float version,
			String recordPacking, 
			String query,
			Integer maximumRecords, 
			String recordSchema){
			
		logger.info("---------------------------------------");
		logger.info("operation       : " + operation);
		logger.info("version         : " + version);
		logger.info("recordPacking   : " + recordPacking);
		logger.info("query           : " + query);
		logger.info("maximumRecords  : " + maximumRecords);
		logger.info("recordSchema    : " + recordSchema);
		
		SearchClient2 searchClient = null;
		
		try {
			searchClient = new SearchClient2.Builder()
				.scope(this.scope)
				.build();
		} catch (SearchClientException e) {
			logger.warn("error while initializing search client", e);
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.build();
		}
		
		return getResponse(searchClient, scope, operation, version, recordPacking, query, maximumRecords, recordSchema);
	}
	
	
	Response getResponse(SearchClient2 searchClient , String scope,
			String operation, 
			Float version,
			String recordPacking, 
			String query,
			Integer maximumRecords, 
			String recordSchema){
		try {
			
			if (operation == null || operation.equalsIgnoreCase("explain")){
				
				Map<String, List<String>> fields = searchClient.getSearchableFields();
				
				
				Map<String, String> collections = searchClient.getCollections();
				
				SruSearchAdapterExplain explain = SruSearchAdapterExplain.createExplain(collections, fields, getHostname(), getPort(), this.includeNonDC, this.defaultRecordsNum);
				
				
				return Response.ok().entity(explain.getExplainXML()).build();
				
			} else if (operation.equalsIgnoreCase("searchRetrieve")) {
				if (version != null && version >= 1.2) {
					String msg = diagnostics(5, "Unsupported version", "1.1");
					
					return Response.status(Response.Status.BAD_REQUEST).entity(new ResponseObj(msg)).build();
					}
				
				
				if (maximumRecords != null && maximumRecords < 0) {
					String msg =  diagnostics(6, "unsupported parameter value", maximumRecords.toString());
					
				return Response.status(Response.Status.BAD_REQUEST).entity(new ResponseObj(msg)).build();
					}
				
				if (query == null) {
					String msg = diagnostics(7, "Mandatory parameter not supplied", "query");
					
					return Response.status(Response.Status.BAD_REQUEST).entity(new ResponseObj(msg)).build();
				}
				
				
				try {
					String queryPart = URLDecoder.decode(query, "UTF-8");
					
					logger.info("query received : " + queryPart);
					
					Map<String, String> fieldsMapping = searchClient.getFieldsMapping();
					
					String queryString = this.queryParserHelper.replaceFields(queryPart, fieldsMapping);
					
					logger.info("query after field replacement : " + queryString);
					
					queryString += " project *";
					
					logger.info("Query = " + queryString);
					
					List<Map<String, String>> results = searchClient.queryAndRead(queryString, null, true);
					
					long resultsSize = (long) results.size();
					
					if (maximumRecords == null)
						maximumRecords = defaultRecordsNum;
					
					if (maximumRecords != null && maximumRecords > 0 && results.size() > maximumRecords) 
						results = results.subList(0, maximumRecords.intValue());
					
					String resultsString = this.recordConverer.convertRecordsToSru(resultsSize, results, splitLists);
					
					// transform results to desired schema
					
					return Response.ok().entity(resultsString).build();
				} catch (SearchClientException e) {
					logger.error("Search client error", e);
					
					return Response.serverError().entity(diagnostics(10, "Search client error", query)).build();
				} catch (Exception e) {
					logger.error("Query syntax error", e);
					
					return Response.serverError().entity(diagnostics(10, "Query syntax error", query)).build();
				}
			} else if (operation.equalsIgnoreCase("scan")){
				//logger.info("Query = " + query);
				return Response.ok().entity(scan()).build();
			} else {
				return Response.serverError().entity(diagnostics(10, "unknown operation requested", query)).build();
			}
		} catch (Exception e) {
			logger.error("error while executing operation", e);
			return Response.serverError().entity(new ResponseObj("Internal server error")).build();
		}
		
	}
	
	
	String getHostname(){
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
			
			return properties.getProperty("hostname");
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}
	}
	
	Integer getPort(){
		final Properties properties = new Properties();
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE)
				.openStream()) {
			properties.load(is);
			
			return Integer.valueOf(properties.getProperty("port"));
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"could not load property file  : "
							+ Constants.PROPERTIES_FILE);
		}
	}

	static String diagnostics(Integer code, String message, String details) {
		String xml = "<?xml version=\"1.0\"?>\n" + 
						"	<diagnostics>\n" + 
						"		<diagnostic xmlns=\"http://www.loc.gov/zing/srw/diagnostic/\">\n" + 
						"			<uri>info:srw/diagnostic/1/" + code +  "</uri>\n" + 
						"			<message>" + message + "</message>\n" + 
						"			<details>" + details + "</details>\n" + 
						"		</diagnostic>\n" + 
						"	</diagnostics>\n";
		return xml;
	}
	
	static  String scan() {
		String xml = "<?xml version=\"1.0\"?>" + 
				"<zs:scanResponse xmlns:zs=\"http://www.loc.gov/zing/srw/\">" + 
				"	<zs:version>1.1</zs:version>" + 
				"	<zs:diagnostics xmlns=\"http://www.loc.gov/zing/srw/diagnostic/\">" + 
				"		<diagnostic>" + 
				"			<uri>info:srw/diagnostic/1/4</uri>" + 
				"			<message>Unsupported operation</message>" + 
				"			<details>scan</details>" + 
				"		</diagnostic>" + 
				"	</zs:diagnostics>" + 
				"</zs:scanResponse>";
		
		return xml;
	}

}

@XmlRootElement 
class ResponseObj {
	
	@XmlElement
	String msg;
	
	public ResponseObj() {}

	public ResponseObj(String msg) {
		super();
		this.msg = msg;
	}
} 