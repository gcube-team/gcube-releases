package org.gcube.search.sru.search.adapter.commons.apis;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.jboss.resteasy.annotations.GZIP;

public interface SruSearchAdapterServiceAPI extends ResourceAwareServiceRestAPI {

	@GET
	@Path(value = "/ping")
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping();
	
	@GET
	@Path(value = "/{id}")
	@Produces(MediaType.APPLICATION_XML+ "; " + "charset=UTF-8")
	@GZIP
	public Response get(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("operation") String operation, 
			@QueryParam("version") Float version,
			@QueryParam("recordPacking") String recordPacking, 
			@QueryParam("query") String query,
			@QueryParam("maximumRecords") Integer maximumRecords, 
			@QueryParam("recordSchema") String recordSchema);
	
	@GET
	@Produces(MediaType.APPLICATION_XML+ "; " + "charset=UTF-8")
	@GZIP
	public Response get(
			@QueryParam("operation") String operation, 
			@QueryParam("version") Float version,
			@QueryParam("recordPacking") String recordPacking, 
			@QueryParam("query") String query,
			@QueryParam("maximumRecords") Integer maximumRecords, 
			@QueryParam("recordSchema") String recordSchema);
}
