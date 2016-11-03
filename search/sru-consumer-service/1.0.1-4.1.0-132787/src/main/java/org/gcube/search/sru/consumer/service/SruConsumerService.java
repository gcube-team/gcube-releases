package org.gcube.search.sru.consumer.service;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.datatransformation.DataTransformationClient;
import org.gcube.datatransformation.client.library.exceptions.DTSClientException;
import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.resourceawareservice.ResourceAwareService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceNotFoundException;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.search.sru.consumer.common.Constants;
import org.gcube.search.sru.consumer.common.apis.SruConsumerServiceAPI;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource.DescriptionDocument;
import org.gcube.search.sru.consumer.parser.sruparser.SRUParser;
import org.gcube.search.sru.consumer.service.helpers.ParserHelper;
import org.gcube.search.sru.consumer.service.helpers.QueryParserHelper;
import org.gcube.search.sru.consumer.service.helpers.RRHelper;
import org.gcube.search.sru.consumer.service.helpers.ResultSetHelpers;
import org.gcube.search.sru.consumer.service.helpers.URLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Path("/")
@Singleton
public class SruConsumerService extends ResourceAwareService<SruConsumerResource> implements SruConsumerServiceAPI {
	
	static final Logger logger = LoggerFactory.getLogger(SruConsumerService.class);

	QueryParserHelper queryParserHelper = new QueryParserHelper();
	SRUParser parser = new SRUParser();
	
	@Inject
	public SruConsumerService(ResourceFactory<SruConsumerResource> factory,
			ResourcePublisher<SruConsumerResource> publisher,
			IResourceFilter<SruConsumerResource> resourceFilter,
			IResourceFileUtils<SruConsumerResource> resourceFileUtils)
			throws Exception {
		super(factory, publisher, resourceFilter, resourceFileUtils);
		logger.info("SruConsumerService initialized");
		
	}

	public void initialize() throws Exception {
		new ServiceContext().initialize();
	}
	
	
	private String scope;
	
	public void setScope(String scope) {
		this.scope = scope;
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

	public Response query(String scope, String resourceID, String queryString, Long maxRecords, Boolean result, Boolean useRR) {
		SruConsumerResource resource = null;
		
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new ResponseObj("Resource with ID : " + resourceID + " not found"))
					.build();
		}
		
		try {
			
			DescriptionDocument dd = resource.getDescriptionDocument();
			logger.info("got description document");
			
			BiMap<String, String> fieldsMapping = null;
			
			logger.info("received query : " + queryString);
			
			if (useRR){
				fieldsMapping = HashBiMap.create();
				fieldsMapping.putAll(RRHelper.getFieldsMapping(scope));
				queryString = queryParserHelper.replaceFields(queryString, fieldsMapping.inverse());
				
				logger.info("using rr. query changed to : " + queryString + " and fieldsMapping : " + fieldsMapping);
			}
			
			logger.info("fieldsMapping : " + fieldsMapping);
			
			String queryPart = queryParserHelper.getQueryPart(queryString);
			
			logger.info("query part : " + queryPart);
			
			List<String> searchables = resource.getSearchables();
			List<String> presentables = resource.getPresentables();
			
			logger.info("resource searchables  : " + searchables);
			logger.info("resource presentables : " + presentables);
			
			List<String> projections = queryParserHelper.getProjectPart(queryString, presentables);
			
			//boolean includeSnippet = false;
			if (projections.contains("S")){
				logger.info("S field found in projections. will be replaced with : " + resource.getSnippetField());
				projections.remove("S");
				
				if (resource.getSnippetField()!= null) {
					projections.add(resource.getSnippetField());
				}
			}
			
			logger.info("query projections : " + projections);
			logger.info("query before parser : " + queryPart);
			
			
			queryPart = parser.parse(queryPart, null, searchables).toCQL();
			
			logger.info("query after parser : " + queryPart);
			
			String urlString = new URLHelper.SearchRetrieveRequest()
				.descriptionDocument(dd)
				.query(queryPart)
				.maximumRecords(maxRecords)
				.build();
			
			
			logger.info("query url for the description document : " + urlString);
			
			//TODO: get input stream and stream the response
			if (result){
				//String xml = URLHelper.urlToString(urlString);
				
				//logger.info("xml retrieved from url : " + xml);
				
				//List<Map<String, String>> records = ParserHelpers.parseResponse(xml, resource, recordConverter, projections, fieldsMapping, resource.getSnippetField());
				
				List<Map<String, String>> records = ParserHelper.parseResponse(getDTSClient(this.scope), urlString, resource, projections, fieldsMapping, resource.getSnippetField());
				
				logger.info("records extracted : " + records);
				
				return Response
						.status(Response.Status.OK)
						.entity(records)
						.build();
			} else {
				String grs2Locator = ResultSetHelpers.writeResponseFromUrlToGRS2(getDTSClient(this.scope), urlString, resource, projections, fieldsMapping, resource.getSnippetField());
				String msg = JSONConverter.convertToJSON("grslocator", grs2Locator);
				
				return Response
						.status(Response.Status.OK)
						.entity(msg)
						.build();
			}
			
		
		} catch (Exception e) {
			logger.error("error while querying the resource : " + resourceID + " for : " + queryString, e);
			return Response
					.serverError()
					.entity(new ResponseObj("error while querying the resource : " + resourceID + " for : " + queryString))
					.build();
		}
	}
	

	public Response explain(String scope, String resourceID) {
		SruConsumerResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			return Response
					.status(Response.Status.NOT_FOUND)
					.entity(new ResponseObj("Resource with ID : " + resourceID + " not found"))
					.build();
		}
		
		try {
			DescriptionDocument dd = resource.getDescriptionDocument();
			
			logger.info("got description document");
			String urlString = new URLHelper.ExplainRequest()
				.descriptionDocument(dd)
				.build();
			
			
			logger.info("explain url for the description document : " + urlString);
			
			String xml = URLHelper.urlToString(urlString);
			
			logger.info("xml retrieved from url : " + xml);
			
			//ExplainResponse explainResponse = XMLConverter.fromXMLNamespaced(xml, ExplainResponse.class);
			return Response
					.status(Response.Status.OK)
					.entity(xml)
					.build();
			
		} catch (Exception e) {
			logger.error("error while getting the explain for resource : " + resourceID, e);
			return Response
					.serverError()
					.entity(new ResponseObj("error while getting the explain for resource :" + resourceID))
					.build();
		}
	}
	
	static DataTransformationClient getDTSClient(String scope) throws DTSClientException{
		DataTransformationClient dtsclient = new DataTransformationClient();
		dtsclient.setScope(scope);
		dtsclient.randomClient();
		return dtsclient;
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



