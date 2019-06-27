package org.gcube.gcat.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.annotation.PATCH;
import org.gcube.gcat.persistence.ckan.CKANResource;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(Resource.COLLECTION)
public class Resource extends BaseREST implements org.gcube.gcat.api.interfaces.Resource<Response,Response> {
	
	protected static final String ITEM_ID_PARAMETER = Item.ITEM_ID_PARAMETER;
	protected static final String RESOURCE_ID_PARAMETER = "RESOURCE_ID";
	
	protected static final String COLLECTION = Item.ITEMS + "/{" + ITEM_ID_PARAMETER + "}/" + RESOURCES;
	
	@GET
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String list(@PathParam(ITEM_ID_PARAMETER) String itemID) {
		setCalledMethod("GET /" + COLLECTION);
		CKANResource ckanResource = new CKANResource(itemID);
		ckanResource.setName(itemID);
		return ckanResource.list();
	}
	
	@POST
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response create(@PathParam(ITEM_ID_PARAMETER) String itemID, String json) {
		setCalledMethod("POST /" + COLLECTION);
		CKANResource ckanResource = new CKANResource(itemID);
		String ret = ckanResource.create(json);
		
		ResponseBuilder responseBuilder = Response.status(Status.CREATED).entity(ret);
		responseBuilder = addLocation(responseBuilder, ckanResource.getResourceID());
		return responseBuilder.type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8).build();
	}
	
	@GET
	@Path("/{" + RESOURCE_ID_PARAMETER + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String read(@PathParam(ITEM_ID_PARAMETER) String itemID,
			@PathParam(RESOURCE_ID_PARAMETER) String resourceID) {
		setCalledMethod("GET /" + COLLECTION + "/{" + RESOURCE_ID_PARAMETER + "}");
		CKANResource ckanResource = new CKANResource(itemID);
		ckanResource.setResourceID(resourceID);
		return ckanResource.read();
	}
	
	@PUT
	@Path("/{" + RESOURCE_ID_PARAMETER + "}")
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String update(@PathParam(ITEM_ID_PARAMETER) String itemID,
			@PathParam(RESOURCE_ID_PARAMETER) String resourceID, String json) {
		setCalledMethod("PUT /" + COLLECTION + "/{" + RESOURCE_ID_PARAMETER + "}");
		CKANResource ckanResource = new CKANResource(itemID);
		ckanResource.setResourceID(resourceID);
		return ckanResource.update(json);
	}
	
	@PATCH
	@Path("/{" + RESOURCE_ID_PARAMETER + "}")
	@Consumes(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String patch(@PathParam(ITEM_ID_PARAMETER) String itemID,
			@PathParam(RESOURCE_ID_PARAMETER) String resourceID, String json) {
		setCalledMethod("PATCH /" + COLLECTION	+ "/{" + RESOURCE_ID_PARAMETER + "}");
		CKANResource ckanResource = new CKANResource(itemID);
		ckanResource.setResourceID(resourceID);
		return ckanResource.patch(json);
	}
	
	@DELETE
	@Path("/{" + RESOURCE_ID_PARAMETER + "}")
	public Response delete(@PathParam(ITEM_ID_PARAMETER) String itemID,
			@PathParam(RESOURCE_ID_PARAMETER) String resourceID) {
		setCalledMethod("DELETE /" + COLLECTION	+ "/{" + RESOURCE_ID_PARAMETER + "}");
		CKANResource ckanResource = new CKANResource(itemID);
		ckanResource.setResourceID(resourceID);
		ckanResource.delete(false);
		return Response.status(Status.NO_CONTENT).build();
	}
	
}
