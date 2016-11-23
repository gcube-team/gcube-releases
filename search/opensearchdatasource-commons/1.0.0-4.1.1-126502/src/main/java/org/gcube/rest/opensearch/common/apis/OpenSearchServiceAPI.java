package org.gcube.rest.opensearch.common.apis;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.jboss.resteasy.annotations.GZIP;

public interface OpenSearchServiceAPI {

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
			@DefaultValue("false") @QueryParam("pretty") final Boolean pretty);
	
}
