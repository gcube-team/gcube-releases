package org.gcube.search.sru.consumer.common.apis;

import javax.ws.rs.DefaultValue;
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

public interface SruConsumerServiceAPI extends ResourceAwareServiceRestAPI {

	@GET
	@Path(value = "/ping")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response ping();
	
	@GET
	@Path(value = "/{id}/query")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	@GZIP
	public Response query(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@QueryParam("queryString") String queryString,
			@QueryParam("maxRecords") Long maxRecords,
			@QueryParam("result") @DefaultValue("false") Boolean result,
			@QueryParam("useRR") @DefaultValue("true") Boolean useRR);
	
	
	@GET
	@Path(value = "/{id}/explain")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response explain (
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID);
}
