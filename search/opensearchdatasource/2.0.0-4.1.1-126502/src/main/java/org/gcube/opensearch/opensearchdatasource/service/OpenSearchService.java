package org.gcube.opensearch.opensearchdatasource.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.rmi.RemoteException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.opensearch.common.Constants;
import org.gcube.rest.opensearch.common.apis.OpenSearchServiceAPI;
import org.gcube.rest.opensearch.common.discover.OpenSearchDiscovererAPI;
import org.gcube.rest.opensearch.common.helpers.ResultReader;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.resourceawareservice.ResourceAwareService;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourceawareservice.exceptions.ResourceNotFoundException;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Path("/")
@Singleton
public class OpenSearchService extends ResourceAwareService<OpenSearchDataSourceResource> implements OpenSearchServiceAPI {

	static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);
	
	private final OpenSearchOperator operator;
	
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
	
	
	@Inject
	public OpenSearchService(
			ResourceFactory<OpenSearchDataSourceResource> factory,
			ResourcePublisher<OpenSearchDataSourceResource> publisher,
			IResourceFilter<OpenSearchDataSourceResource> resourceFilter,
			IResourceFileUtils<OpenSearchDataSourceResource> resourceFileUtils,
			OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer,
			OpenSearchOperator operator)
			throws ResourceAwareServiceException {
		super(factory, publisher, resourceFilter, resourceFileUtils);
		
		ServiceContext context = new ServiceContext(operator, discoverer);
		context.initialize();
		
		this.operator = operator;
	}

	@GET
	@Path(value = "/{id}/query")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response query(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) final String scope,
			@PathParam("id") final String resourceID,
			@QueryParam("queryString") final String queryString,
			@DefaultValue("true") @QueryParam("useRR") final Boolean useRR,
			@DefaultValue("false") @QueryParam("result")final  Boolean result,
			@DefaultValue("false") @QueryParam("stream") final Boolean stream,
			@DefaultValue("false") @QueryParam("pretty") final Boolean pretty){
		
		logger.info("will execute query for query : " + queryString);
		
		Object msg = null;
		Response.Status status = null;
		
		OpenSearchDataSourceResource resource = null;
		try {
			resource = this.getResource(resourceID);
		} catch (ResourceNotFoundException e) {
			logger.warn("error while getting resource", e);
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;

			return Response.status(status).entity(msg).build();
		}
		
		URI grslocatorURI = null;
		try {
			grslocatorURI = operator.query(resource, queryString, useRR);
			
			final String grslocator = grslocatorURI.toASCIIString();
			
			if (result) {
				
				if (stream) {
					StreamingOutput stm = new StreamingOutput() {
						@Override
						public void write(OutputStream output)
								throws IOException, WebApplicationException {
							Writer writer = new BufferedWriter(
									new OutputStreamWriter(output));

							try {
								ResultReader.streamResultSetToJsonRecords(
										writer, grslocator, pretty);
							} catch (Exception e) {
								e.printStackTrace();
								throw new WebApplicationException(e);
							}
							writer.flush();
							writer.close();
						}
					};
					msg = stm;
					status = Response.Status.OK;
				} else {
				
					msg = ResultReader.resultSetToJsonRecords(grslocator,
							pretty);
					status = Response.Status.OK;
				}
			} else {
				msg = JSONConverter.convertToJSON("grslocator", grslocator);
				status = Response.Status.OK;
			}
			
		} catch (RemoteException e) {
			
			logger.error("error while querying for query : " + queryString, e);
			
			msg = JSONConverter.convertToJSON("Error",
					"error while querying : " + e.getMessage());
			status = Response.Status.INTERNAL_SERVER_ERROR;
		}
		
		return Response.status(status).entity(msg).build();
		
	}
	
}
