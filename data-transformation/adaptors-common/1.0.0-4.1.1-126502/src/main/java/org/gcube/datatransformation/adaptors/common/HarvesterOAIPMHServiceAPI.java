package org.gcube.datatransformation.adaptors.common;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceRestAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;

public interface HarvesterOAIPMHServiceAPI extends ResourceAwareServiceRestAPI {
	
	@GET
	@Path(value = "/HarvestOAIPMHSource")
	@Produces(MediaType.TEXT_XML + "; " + "charset=UTF-8")
	public Response HarvestOAIPMHSource(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scopeHeader,
			@Context Request request,
			@QueryParam("RepositoryBaseURL") final String baseURL,
			@QueryParam("metadataPrefix") String metadataPrefix,
			@QueryParam("set") final String setSpec
			);
	
	
	@GET
	@Path(value = "/test")
	@Produces(MediaType.TEXT_XML + "; " + "charset=UTF-8")
	public Response test(
			@QueryParam("metadataPrefix") final String metadataPrefix
			);
	
	
	
	
	
	
	
	
}
