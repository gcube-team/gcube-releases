package org.gcube.search.sru.db.service;

import java.net.URLDecoder;
import java.sql.SQLException;

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
import org.gcube.search.sru.db.DB;
import org.gcube.search.sru.db.common.Constants;
import org.gcube.search.sru.db.common.apis.SruDBServiceAPI;
import org.gcube.search.sru.db.common.resources.SruDBResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Path("/")
@Singleton
public class SruDBService extends ResourceAwareService<SruDBResource> implements SruDBServiceAPI {
	static final Logger logger = LoggerFactory.getLogger(SruDBService.class);
	
	boolean splitLists;
	
	@Inject
	public SruDBService(ResourceFactory<SruDBResource> factory,
			ResourcePublisher<SruDBResource> publisher,
			IResourceFilter<SruDBResource> resourceFilter,
			IResourceFileUtils<SruDBResource> resourceFileUtils)
			throws ResourceAwareServiceException {
		super(factory, publisher, resourceFilter, resourceFileUtils);
		logger.info("SruDBService initialized");
	}

	private String scope;
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public void setSplitLists(boolean splitLists) {
		this.splitLists = splitLists;
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
		
		SruDBResource resource = null;
		try {
			resource = this.getResource(resourceID);
			
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new ResponseObj("Resource with ID : " + resourceID + " not found"))
					.build();
		}
		
		
		if (operation == null || operation.equalsIgnoreCase("explain")){
			DB db = new DB.Builder()
				.serverHost(resource.getHostname())
				.serverPort(resource.getPort())
				.databaseName(resource.getDbName())
				.databaseType(resource.getDbType())
				.databaseUsername(resource.getUsername())
				.databasePassword(resource.getPassword())
				.databaseTitle(resource.getDbTitle())
				.databaseName(resource.getDbName())
				.schemaID(resource.getExplainInfo().getSchemaID())
				.schemaName(resource.getExplainInfo().getSchemaName())
				.recordSchema(resource.getExplainInfo().getRecordSchema())
				.recordPacking(resource.getExplainInfo().getRecordPacking())
				.indexSets(resource.getExplainInfo().getIndexSets())
				.indexInfo(resource.getExplainInfo().getIndexInfo())
				.defaultTable(resource.getExplainInfo().getDefaultTable())
				.build();
			
			db.initializeExplain();
			
			return Response.ok().entity(db.getExplain()).build();
			
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
				query = URLDecoder.decode(query, "UTF-8");
				logger.info("Query = " + query);
				
				DB db = new DB.Builder()
					.serverHost(resource.getHostname())
					.serverPort(resource.getPort())
					.databaseName(resource.getDbName())
					.databaseType(resource.getDbType())
					.databaseUsername(resource.getUsername())
					.databasePassword(resource.getPassword())
					.databaseTitle(resource.getDbTitle())
					.databaseName(resource.getDbName())
					.schemaID(resource.getExplainInfo().getSchemaID())
					.schemaName(resource.getExplainInfo().getSchemaName())
					.recordSchema(resource.getExplainInfo().getRecordSchema())
					.recordPacking(resource.getExplainInfo().getRecordPacking())
					.indexSets(resource.getExplainInfo().getIndexSets())
					.indexInfo(resource.getExplainInfo().getIndexInfo())
					.defaultTable(resource.getExplainInfo().getDefaultTable())
					.build();
				
				logger.info("connecting to db...");
				BiMap<String, String> fieldsMapping = HashBiMap.create();
				fieldsMapping.putAll(resource.getFieldsMapping());
				
				String results = db.connectToAndQueryDatabase(query, fieldsMapping, splitLists);
				
				return Response.ok().entity(results).build();
			} catch (ClassNotFoundException e) {
				logger.error("connection to the database problem", e);
				
				return Response.serverError().entity(new ResponseObj("error connecting to the database")).build();
				
			} catch (SQLException e) {
				logger.error("Query syntax error", e);
				
				return Response.status(Response.Status.BAD_REQUEST).entity(diagnostics(10, "Query syntax error", query)).build();
			} catch (Exception e) {
				logger.error("error", e);
				
				return Response.serverError().entity(diagnostics(10, "error : " + e.getMessage(), query)).build();
			}
		} else if (operation.equalsIgnoreCase("scan")){
			//logger.info("Query = " + query);
			return Response.ok().entity(scan()).build();
		} else {
			return Response.serverError().entity(diagnostics(10, "unsupported operation : " + operation, query)).build();
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