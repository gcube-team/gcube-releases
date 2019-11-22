package org.gcube.rest.commons.resourceawareservice;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;

public interface ResourceAwareServiceRestAPI {

	@POST
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH)
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response createResourceREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@FormParam("jsonParam") String jsonParam);

	@GET
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH
			+ "/{id}")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response getResourceREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@DefaultValue("false") @QueryParam("pretty") Boolean pretty);

	@DELETE
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH
			+ "/{id}")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response destroyResourceREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID);

	@GET
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH)
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response listResourcesREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@DefaultValue("false") @QueryParam("complete") Boolean complete,
			@DefaultValue("false") @QueryParam("pretty") Boolean pretty);

	@GET
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH + "/filter")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response filterResourcesREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@QueryParam("filter") String filter,
			@DefaultValue("false") @QueryParam("complete") Boolean complete,
			@DefaultValue("false") @QueryParam("pretty") Boolean pretty);
}
