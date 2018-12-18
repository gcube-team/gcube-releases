package org.gcube.datacatalogue.catalogue.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.Delegator;

@Path(Constants.GROUPS)
/**
 * Groups service endpoint.
 * All checks are demanded to CKAN
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class Group {
	
	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_show
		return Delegator.delegateGet(Constants.GROUP_SHOW, uriInfo);
	}
	
	@GET
	@Path(Constants.LIST_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String list(@Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_list
		return Delegator.delegateGet(Constants.GROUP_LIST, uriInfo);
	}
	
	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.create.group_create
		return Delegator.delegatePost(Constants.GROUP_CREATE, json, uriInfo);
	}
	
	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.group_delete
		return Delegator.delegatePost(Constants.GROUP_DELETE, json, uriInfo);
	}
	
	@DELETE
	@Path(Constants.PURGE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String purge(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.delete.group_purge
		return Delegator.delegatePost(Constants.GROUP_PURGE, json, uriInfo);
	}
	
	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.update.group_update
		return Delegator.delegatePost(Constants.GROUP_UPDATE, json, uriInfo);
	}
	
	@POST
	@Path(Constants.PATCH_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String patch(String json, @Context UriInfo uriInfo) {
		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.patch.group_patch
		return Delegator.delegatePost(Constants.GROUP_PATCH, json, uriInfo);
	}
	
}
