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

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.catalogue.utils.CatalogueUtils;
import org.gcube.datacatalogue.catalogue.utils.Constants;
import org.gcube.datacatalogue.catalogue.utils.Delegator;


@Path(Constants.GROUPS)
/**
 * Groups service endpoint.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Group {

	@GET
	@Path(Constants.SHOW_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String show(@Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_show
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);
		return Delegator.delegateGet(caller, context, Constants.GROUP_SHOW, uriInfo, isApplication);

	}
	
	@GET
	@Path(Constants.LIST_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	public String list(@Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_list
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		boolean isApplication = CatalogueUtils.isApplicationToken(caller);
		return Delegator.delegateGet(caller, context, Constants.GROUP_LIST, uriInfo, isApplication);

	}

	@POST
	@Path(Constants.CREATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_create
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.GROUP_CREATE, json, uriInfo, false);

	}

	@DELETE
	@Path(Constants.DELETE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_delete
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.GROUP_DELETE, json, uriInfo, false);

	}

	@DELETE
	@Path(Constants.PURGE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String purge(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_purge
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.GROUP_PURGE, json, uriInfo, false);

	}

	@POST
	@Path(Constants.UPDATE_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String update(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_update
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.GROUP_UPDATE, json, uriInfo, false);

	}

	@POST
	@Path(Constants.PATCH_METHOD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String patch(String json, @Context UriInfo uriInfo){

		// see http://docs.ckan.org/en/latest/api/#ckan.logic.action.get.group_patch
		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		return Delegator.delegatePost(caller, context, Constants.GROUP_PATCH, json, uriInfo, false);

	}

}
